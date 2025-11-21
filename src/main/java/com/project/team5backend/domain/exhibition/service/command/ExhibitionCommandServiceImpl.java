package com.project.team5backend.domain.exhibition.service.command;

import com.project.team5backend.domain.common.storage.FileStoragePort;
import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.exhibition.ExhibitionLikeReader;
import com.project.team5backend.domain.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.converter.ExhibitionLikeConverter;
import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.repository.ExhibitionLikeRepository;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.domain.facility.repository.FacilityRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.entity.ExhibitionImage;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.image.validator.ExhibitionImageValidator;
import com.project.team5backend.domain.recommendation.service.InteractLogService;
import com.project.team5backend.domain.review.exhibition.repository.ExhibitionReviewRepository;
import com.project.team5backend.domain.user.UserReader;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.address.converter.AddressConverter;
import com.project.team5backend.global.address.service.AddressService;
import com.project.team5backend.domain.common.embedded.Address;
import com.project.team5backend.domain.common.enums.Status;
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
public class ExhibitionCommandServiceImpl implements ExhibitionCommandService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;
    private final ExhibitionReviewRepository exhibitionReviewRepository;
    private final ExhibitionLikeReader exhibitionLikeReader;
    private final UserReader userReader;
    private final FacilityRepository facilityRepository;
    private final ImageCommandService imageCommandService;
    private final AddressService addressService;
    private final InteractLogService interactLogService;
    private final FileStoragePort fileStoragePort;
    private final FileUrlResolverPort fileUrlResolverPort;

    @Override
    public ExhibitionResDTO.ExhibitionCreateResDTO createExhibition(ExhibitionReqDTO.ExhibitionCreateReqDTO exhibitionCreateReqDTO, Long userId, List<MultipartFile> images) {
        ExhibitionImageValidator.validateImages(images); // 이미지 검증 (개수, null 여부)

        User user = userReader.readUser(userId);
        Address address = resolveAddress(exhibitionCreateReqDTO);
        List<String> imageUrls = uploadImages(images);
        Exhibition exhibition = saveExhibition(exhibitionCreateReqDTO, user, address, imageUrls.get(0));

        saveExhibitionFacilities(exhibitionCreateReqDTO, exhibition);
        saveExhibitionImages(exhibition, imageUrls);

        return ExhibitionConverter.toExhibitionCreateResDTO(exhibition.getId(), exhibition.getCreatedAt());
    }

    @Override
    public ExhibitionResDTO.ExhibitionLikeResDTO toggleLike(Long exhibitionId, Long userId) {
        User user = userReader.readUser(userId);
        Exhibition exhibition = getActiveExhibition(exhibitionId);
        boolean alreadyLiked = exhibitionLikeReader.isLikedByUser(userId, exhibitionId);
        return alreadyLiked ? cancelLike(user, exhibition) : addLike(user, exhibition);
    }

    @Override
    public void deleteExhibition(Long exhibitionId, Long userId) {
        Exhibition exhibition = getExhibitionOwnedByUser(exhibitionId, userId);
        performSoftDelete(exhibition);
        cleanupRelatedData(exhibitionId);
    }


    private ExhibitionResDTO.ExhibitionLikeResDTO cancelLike(User user, Exhibition exhibition) {
        exhibitionLikeRepository.deleteByUserIdAndExhibitionId(user.getId(), exhibition.getId());
        exhibition.decreaseLikeCount();
        return ExhibitionLikeConverter.toExhibitionLikeResDTO(exhibition.getId(), "관심목록에서 삭제되었습니다.");
    }

    private ExhibitionResDTO.ExhibitionLikeResDTO addLike(User user, Exhibition exhibition) {
        exhibitionLikeRepository.save(ExhibitionLikeConverter.toEntity(user, exhibition));
        exhibition.increaseLikeCount();
        interactLogService.logLike(user.getId(), exhibition.getId());
        return ExhibitionLikeConverter.toExhibitionLikeResDTO(exhibition.getId(), "관심목록에 추가되었습니다.");
    }

    private List<String> deleteExhibitionImage(Long exhibitionId) {
        List<ExhibitionImage> images = exhibitionImageRepository.findByExhibitionId(exhibitionId);
        images.forEach(ExhibitionImage::deleteImage);
        return images.stream()
                .map(ExhibitionImage::getFileKey)
                .toList();
    }

    private void moveImagesToTrash(List<String> fileKeys) {
        imageCommandService.deleteImages(fileKeys);
    }

    private Exhibition getActiveExhibition(Long exhibitionId) {
        return exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApproved(exhibitionId, Status.APPROVED)
                .orElseThrow(()-> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
    }

    private Exhibition getExhibitionOwnedByUser(Long exhibitionId, Long userId) {
        return exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApprovedWithUser(exhibitionId, userId, Status.APPROVED)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
    }

    private Address resolveAddress(ExhibitionReqDTO.ExhibitionCreateReqDTO exhibitionCreateReqDTO) {
        var addressResDTO = addressService.resolve(exhibitionCreateReqDTO.address());
        return AddressConverter.toAddress(addressResDTO);
    }

    private List<String> uploadImages(List<MultipartFile> images) {
        return images.stream()
                .map(file -> fileStoragePort.upload(file, "exhibitions"))
                .toList();
    }

    private Exhibition saveExhibition(ExhibitionReqDTO.ExhibitionCreateReqDTO exhibitionCreateReqDTO, User user, Address address, String image) {
        String thumbnail = fileUrlResolverPort.toFileKey(image);
        Exhibition exhibition = ExhibitionConverter.toExhibition(exhibitionCreateReqDTO, user, thumbnail, address);
        return exhibitionRepository.save(exhibition);
    }

    private void saveExhibitionFacilities(ExhibitionReqDTO.ExhibitionCreateReqDTO exhibitionCreateReqDTO, Exhibition exhibition) {
        List<Facility> facilities = facilityRepository.findByNameIn(exhibitionCreateReqDTO.facilities());
        exhibition.getExhibitionFacilities().addAll(
                facilities.stream()
                        .map(facility -> ExhibitionConverter.toCreateExhibitionFacility(exhibition, facility))
                        .toList()
        );
    }

    private void saveExhibitionImages(Exhibition exhibition, List<String> imageUrls) {
        imageUrls.forEach(imageUrl -> exhibitionImageRepository.save(ImageConverter.toExhibitionImage(exhibition, imageUrl)));
    }

    private void performSoftDelete(Exhibition exhibition) {
        exhibition.softDelete();
        List<String> fileKeys = deleteExhibitionImage(exhibition.getId()); // 전시이미지 소프트 삭제
        exhibition.resetCount(); // 집계 초기화

        if (exhibition.getPortalExhibitionId() == null) {
            moveImagesToTrash(fileKeys); // 크롤링 하지 않은 전시의 사진만 s3 보존 휴지통 prefix로 이동시키기
        }
    }

    private void cleanupRelatedData(Long exhibitionId) {
        exhibitionLikeRepository.deleteByExhibitionId(exhibitionId); // 좋아요 하드 삭제 (벌크)
        exhibitionReviewRepository.softDeleteByExhibitionId(exhibitionId); // 리뷰 소프트 삭제 (벌크)
    }
}
