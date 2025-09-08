package com.project.team5backend.domain.space.space.service.command;

import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.facility.entity.ExhibitionFacility;
import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.domain.facility.entity.SpaceFacility;
import com.project.team5backend.domain.facility.repository.FacilityRepository;
import com.project.team5backend.domain.image.entity.SpaceImage;
import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.space.review.repository.SpaceReviewRepository;
import com.project.team5backend.domain.space.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.space.converter.SpaceLikeConverter;
import com.project.team5backend.domain.space.space.dto.request.SpaceReqDTO;
import com.project.team5backend.domain.space.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.space.exception.SpaceException;
import com.project.team5backend.domain.space.space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;

import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.address.converter.AddressConverter;
import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.global.address.service.AddressService;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpaceCommandServiceImpl implements SpaceCommandService {

    private final SpaceRepository spaceRepository;
    private final SpaceLikeRepository spaceLikeRepository;
    private final UserRepository userRepository;
    private final SpaceImageRepository spaceImageRepository;
    private final SpaceReviewRepository spaceReviewRepository;
    private final FacilityRepository facilityRepository;
    private final AddressService addressService;
    private final S3Uploader s3Uploader;
    private final ImageCommandService imageCommandService;

    @Override
    public SpaceResDTO.CreateSpaceResDTO createSpace(SpaceReqDTO.CreateSpaceReqDTO request, long userId, List<MultipartFile> images) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        //주소 가져오기
        AddressResDTO.AddressCreateResDTO addressResDTO = addressService.resolve(request.address());
        Address address = AddressConverter.toAddress(addressResDTO);

        // 업로드 및 image 획득
        List<String> imageUrls = images.stream()
                .map(file -> s3Uploader.upload(file, "spaces"))
                .toList();

        Space space = SpaceConverter.toSpace(request, user, imageUrls.get(0), address);
        spaceRepository.save(space);

        // 2. 시설 매핑 (문자열 → Facility 엔티티 조회 → ExhibitionFacility 생성)
        List<Facility> facilities = facilityRepository.findByNameIn(request.facilities());
        facilities.forEach(facility -> {
            SpaceFacility sf = SpaceConverter.toCreateSpaceFacility(space, facility);
            space.getSpaceFacilities().add(sf);
        });

        // Space 이미지 엔티티 저장
        for (String url : imageUrls) {
            spaceImageRepository.save(ImageConverter.toEntitySpaceImage(space, url));
        }
        return SpaceConverter.toCreateSpaceResDTO(space);
    }

    @Override
    public SpaceResDTO.LikeSpaceResDTO likeSpace(long spaceId, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(()-> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));

        boolean alreadyLiked = spaceLikeRepository.existsByUserIdAndSpaceId(userId, spaceId);

        String message = alreadyLiked
                ? handleUnlike(userId, spaceId, space)
                : handleLike(user, space);

        return SpaceLikeConverter.toLikeSpaceResDTO(spaceId, message);
    }

    @Override
    public void deleteSpace(long spaceId, long userId) {
        Space space = spaceRepository.findByIdAndIsDeletedFalseAndStatusApproved(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));

        space.softDelete();

        List<String> imageUrls = spaceImageRepository.findBySpaceId(spaceId).stream()
                .peek(SpaceImage::deleteImage)
                .map(SpaceImage::getImageUrl)
                .toList();

        // 좋아요 하드 삭제 (벌크)
        spaceLikeRepository.deleteBySpaceId(spaceId);
        // 리뷰 소프트 삭제 (벌크)
        spaceReviewRepository.softDeleteBySpaceId(spaceId);

        // s3 보존 휴지통 prefix로 이동시키기
        try{
            imageCommandService.moveToTrashPrefix(imageUrls);
        } catch (ImageException e) {
            throw new ImageException(ImageErrorCode.S3_MOVE_TRASH_FAIL);
        }
    }
    private String handleUnlike(long userId, long spaceId, Space space) {
        spaceLikeRepository.deleteByUserIdAndSpaceId(userId, spaceId);
        space.decreaseLikeCount();
        return "관심목록에서 삭제되었습니다.";
    }

    private String handleLike(User user, Space space) {
        spaceLikeRepository.save(SpaceLikeConverter.toSpaceLike(user, space));
        space.increaseLikeCount();
        return "관심목록에 추가되었습니다.";
    }

//    @Override
//    public boolean toggleLike(Long spaceId, Long userId) {
//        // ... 기존 로직과 동일
//        Space space = spaceRepository.findById(spaceId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 전시 공간이 존재하지 않습니다."));
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
//        return spaceLikeRepository.findBySpaceIdAndUserId(spaceId, userId)
//                .map(existingLike -> {
//                    spaceLikeRepository.softDelete(existingLike);
//                    return false;
//                })
//                .orElseGet(() -> {
//                    SpaceLike like = new SpaceLike();
//                    like.setSpace(space);
//                    like.setUser(user);
//                    spaceLikeRepository.save(like);
//                    return true;
//                });
//    }
}