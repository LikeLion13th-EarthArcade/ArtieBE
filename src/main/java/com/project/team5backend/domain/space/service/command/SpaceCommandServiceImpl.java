package com.project.team5backend.domain.space.service.command;

import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.domain.facility.entity.SpaceFacility;
import com.project.team5backend.domain.facility.repository.FacilityRepository;
import com.project.team5backend.domain.facility.repository.SpaceFacilityRepository;
import com.project.team5backend.domain.image.entity.SpaceImage;
import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.recommendation.service.InteractLogService;
import com.project.team5backend.domain.space.review.repository.SpaceReviewRepository;
import com.project.team5backend.domain.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.converter.SpaceLikeConverter;
import com.project.team5backend.domain.space.dto.request.SpaceReqDTO;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.space.repository.SpaceRepository;

import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.address.converter.AddressConverter;
import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.global.address.service.AddressService;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.util.ImageUtils;
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
    private final SpaceFacilityRepository spaceFacilityRepository;
    private final InteractLogService interactLogService;
    private final AddressService addressService;
    private final S3Uploader s3Uploader;
    private final ImageCommandService imageCommandService;

    @Override
    public SpaceResDTO.SpaceCreateResDTO createSpace(SpaceReqDTO.SpaceCreateReqDTO request, long userId, List<MultipartFile> images) {

        ImageUtils.validateImages(images); // 이미지 검증 (개수, null 여부)

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

        // 시설 매핑 (문자열 → Facility 엔티티 조회 → SpaceFacility 생성)
        List<Facility> facilities = facilityRepository.findByNameIn(request.facilities());
        facilities.forEach(facility -> {
            SpaceFacility sf = SpaceConverter.toCreateSpaceFacility(space, facility);
            space.getSpaceFacilities().add(sf);
        });

        // Space 이미지 엔티티 저장
        for (String url : imageUrls) {
            spaceImageRepository.save(ImageConverter.toSpaceImage(space, url));
        }
        return SpaceConverter.toCreateSpaceResDTO(space);
    }

    @Override
    public SpaceResDTO.SpaceLikeResDTO likeSpace(long spaceId, long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));
        Space space = spaceRepository.findByIdAndIsDeletedFalseAndStatusApproved(spaceId, Status.APPROVED)
                .orElseThrow(()-> new SpaceException(SpaceErrorCode.APPROVED_SPACE_NOT_FOUND));

        boolean alreadyLiked = spaceLikeRepository.existsByUserIdAndSpaceId(user.getId(), spaceId);
        return alreadyLiked ? cancelLike(user, space) : addLike(user, space);
    }

    @Override
    public void deleteSpace(long spaceId, long userId) {
        Space space = spaceRepository.findByIdAndIsDeletedFalseAndStatusApproved(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.APPROVED_SPACE_NOT_FOUND));

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

    private SpaceResDTO.SpaceLikeResDTO cancelLike(User user, Space space) {
        spaceLikeRepository.deleteByUserIdAndSpaceId(user.getId(), space.getId());
        space.decreaseLikeCount();
        return SpaceLikeConverter.toLikeSpaceResDTO(space.getId(), "관심목록에서 삭제되었습니다.");
    }

    private SpaceResDTO.SpaceLikeResDTO addLike(User user, Space space) {
        spaceLikeRepository.save(SpaceLikeConverter.toSpaceLike(user, space));
        space.increaseLikeCount();
        interactLogService.logLike(user.getId(), space.getId());
        return SpaceLikeConverter.toLikeSpaceResDTO(space.getId(), "관심목록에 추가되었습니다.");
    }
}