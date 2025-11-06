package com.project.team5backend.domain.space.service.command;

import com.project.team5backend.domain.common.cache.CachePort;
import com.project.team5backend.domain.common.storage.FileStoragePort;
import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.domain.facility.repository.FacilityRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.entity.SpaceImage;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.image.validator.ExhibitionImageValidator;
import com.project.team5backend.domain.recommendation.service.InteractLogService;
import com.project.team5backend.domain.review.space.repository.SpaceReviewRepository;
import com.project.team5backend.domain.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.converter.SpaceLikeConverter;
import com.project.team5backend.domain.space.dto.request.SpaceReqDTO;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.SpaceVerification;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.domain.space.repository.SpaceVerificationRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.address.converter.AddressConverter;
import com.project.team5backend.global.address.service.AddressService;
import com.project.team5backend.domain.common.embedded.Address;
import com.project.team5backend.domain.common.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
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
    private final SpaceVerificationRepository spaceVerificationRepository;
    private final UserRepository userRepository;
    private final SpaceImageRepository spaceImageRepository;
    private final SpaceReviewRepository spaceReviewRepository;
    private final FacilityRepository facilityRepository;
    private final InteractLogService interactLogService;
    private final AddressService addressService;
    private final FileStoragePort fileStoragePort;
    private final ImageCommandService imageCommandService;
    private final FileUrlResolverPort fileUrlResolverPort;
    private final CachePort cachePort;

    @Override
    public SpaceResDTO.SpaceCreateResDTO createSpace(SpaceReqDTO.SpaceCreateReqDTO spaceCreateReqDTO, long userId, MultipartFile businessLicenseFile, MultipartFile buildingRegisterFile, List<MultipartFile> images) {
        // 사업자 번호 검증을 완료 했는지?
        final String bizNumber = spaceCreateReqDTO.bizNumber();
        if (!cachePort.isValidated(bizNumber)) {
            throw new SpaceException(SpaceErrorCode.BIZ_NUMBER_VALIDATION_DOES_NOT_EXIST);
        }

        ExhibitionImageValidator.validateImages(images); // 이미지 검증 (개수, null 여부)
        User user = getActiveUser(userId);
        Address address = resolveAddress(spaceCreateReqDTO);

        var verificationFiles = uploadVerificationFiles(businessLicenseFile, buildingRegisterFile);
        List<String> imageUrls = uploadImages(images);

        Space space = saveSpace(spaceCreateReqDTO, user, address, imageUrls.get(0));
        SpaceVerification spaceVerification = SpaceConverter.toSpaceVerification(space, bizNumber, verificationFiles.getLeft(), verificationFiles.getRight());

        spaceVerificationRepository.save(spaceVerification);
        saveSpaceFacilities(spaceCreateReqDTO, space);
        saveSpaceImages(imageUrls, space);

        cachePort.invalidate(bizNumber); //redis key 제거
        return SpaceConverter.toSpaceCreateResDTO(space);
    }

    @Override
    public SpaceResDTO.SpaceLikeResDTO toggleLike(long spaceId, long userId) {
        User user = getActiveUser(userId);
        Space space = getActiveSpace(spaceId);
        boolean alreadyLiked = spaceLikeRepository.existsByUserIdAndSpaceId(user.getId(), spaceId);
        return alreadyLiked ? cancelLike(user, space) : addLike(user, space);
    }

    @Override
    public void deleteSpace(long spaceId, long userId) {
        Space space = getSpaceOwnedByUser(spaceId);
        performSoftDelete(space);
        cleanupRelatedDate(spaceId);
    }

    private Space getSpaceOwnedByUser(long spaceId) {
        return spaceRepository.findByIdAndIsDeletedFalseAndStatusApprovedWithUser(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.APPROVED_SPACE_NOT_FOUND));
    }

    private void performSoftDelete(Space space) {
        space.softDelete();
        List<String> fileKeys = deleteSpaceImage(space.getId());
        space.resetCount();
        moveImagesToTrash(fileKeys); // s3 보존 휴지통 prefix로 이동시키기
    }

    private void cleanupRelatedDate(Long spaceId){
        spaceLikeRepository.deleteBySpaceId(spaceId); // 좋아요 하드 삭제 (벌크)
        spaceReviewRepository.softDeleteBySpaceId(spaceId); // 리뷰 소프트 삭제 (벌크)
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

    private List<String> deleteSpaceImage(Long spaceId) {
        List<SpaceImage> images = spaceImageRepository.findBySpaceId(spaceId);
        images.forEach(SpaceImage::deleteImage);
        return images.stream()
                .peek(SpaceImage::deleteImage)
                .map(SpaceImage::getFileKey)
                .toList();
    }

    private void moveImagesToTrash(List<String> fileKeys) {
        imageCommandService.deleteImages(fileKeys);
    }

    private Space getActiveSpace(long spaceId) {
        return spaceRepository.findByIdAndIsDeletedFalseAndStatusApproved(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.APPROVED_SPACE_NOT_FOUND));
    }

    private Space saveSpace(SpaceReqDTO.SpaceCreateReqDTO spaceCreateReqDTO, User user, Address address, String image) {
        String thumbnail = fileUrlResolverPort.toFileKey(image);
        Space space = SpaceConverter.toSpace(spaceCreateReqDTO, user, thumbnail, address);
        return spaceRepository.save(space);
    }

    private void saveSpaceImages(List<String> imageUrls, Space space) {
        for (String url : imageUrls) {
            spaceImageRepository.save(ImageConverter.toSpaceImage(space, url));
        }
    }

    private void saveSpaceFacilities(SpaceReqDTO.SpaceCreateReqDTO spaceCreateReqDTO, Space space) {
        List<Facility> facilities = facilityRepository.findByNameIn(spaceCreateReqDTO.facilities());
        space.getSpaceFacilities().addAll(
                facilities.stream()
                        .map(facility -> SpaceConverter.toSpaceFacility(space, facility))
                        .toList()
        );
    }

    private User getActiveUser(long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Address resolveAddress(SpaceReqDTO.SpaceCreateReqDTO spaceCreateReqDTO) {
        var addressResDTO = addressService.resolve(spaceCreateReqDTO.address());
        return AddressConverter.toAddress(addressResDTO);
    }

    private List<String> uploadImages(List<MultipartFile> images) {
        return images.stream()
                .map(file -> fileStoragePort.upload(file, "spaces"))
                .toList();
    }

    private Pair<String, String> uploadVerificationFiles(
            MultipartFile businessLicenseFile,
            MultipartFile buildingRegisterFile
    ) {
        String businessUrl = fileStoragePort.upload(businessLicenseFile, "businessLicenseFile");
        String registerUrl = fileStoragePort.upload(buildingRegisterFile, "buildingRegisterFile");
        return Pair.of(businessUrl, registerUrl);
    }

}