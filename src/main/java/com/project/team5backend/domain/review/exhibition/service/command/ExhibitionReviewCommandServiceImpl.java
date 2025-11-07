package com.project.team5backend.domain.review.exhibition.service.command;

import com.project.team5backend.domain.common.storage.FileStoragePort;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.repository.ExhibitionReviewImageRepository;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.review.exhibition.converter.ExhibitionReviewConverter;
import com.project.team5backend.domain.review.exhibition.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.review.exhibition.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.review.exhibition.exception.ExhibitionReviewErrorCode;
import com.project.team5backend.domain.review.exhibition.exception.ExhibitionReviewException;
import com.project.team5backend.domain.review.exhibition.repository.ExhibitionReviewRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.domain.common.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionReviewCommandServiceImpl implements ExhibitionReviewCommandService {

    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionReviewRepository exhibitionReviewRepository;
    private final ExhibitionReviewImageRepository exhibitionReviewImageRepository;
    private final ImageCommandService imageCommandService;
    private final FileStoragePort fileStoragePort;

    @Override
    public ExhibitionReviewResDTO.ExReviewCreateResDTO createExhibitionReview(Long exhibitionId, Long userId, ExhibitionReviewReqDTO.createExReviewReqDTO createExhibitionReviewReqDTO, List<MultipartFile> images) {
        Exhibition exhibition = getActiveOpeningExhibition(exhibitionId);
        User user = getActiveUser(userId);

        ExhibitionReview exhibitionReview = ExhibitionReviewConverter.toEntity(createExhibitionReviewReqDTO, exhibition, user);
        exhibitionReviewRepository.save(exhibitionReview);

        saveReviewImages(images, exhibitionReview);
        exhibitionRepository.applyReviewCreated(exhibitionId, exhibitionReview.getRate()); // 리뷰 평균/카운트 갱신
        return ExhibitionReviewConverter.toExReviewCreateResDTO(exhibitionReview.getId());
    }

    @Override
    public void deleteExhibitionReview(Long exhibitionReviewId, Long userId) {
        ExhibitionReview exhibitionReview = getActiveExhibition(exhibitionReviewId, userId);
        performsSoftDelete(exhibitionReview);
    }

    private void performsSoftDelete(ExhibitionReview exhibitionReview) {
        exhibitionReview.softDelete();
        exhibitionReview.getExhibitionReviewImages().forEach(ExhibitionReviewImage::deleteImage);

        List<String> fileKeys = exhibitionReview.getExhibitionReviewImages().stream()
                .map(ExhibitionReviewImage::getFileKey)
                .toList();

        exhibitionRepository.applyReviewDeleted(exhibitionReview.getExhibition().getId(), exhibitionReview.getRate()); // 리뷰 평균/카운트 갱신
        moveImagesToTrash(fileKeys); // s3 보존 휴지통 prefix로 이동시키기
    }

    private ExhibitionReview getActiveExhibition(Long exhibitionReviewId, Long userId) {
        return exhibitionReviewRepository.findByIdAndIsDeletedFalseWithUser(exhibitionReviewId, userId)
                .orElseThrow(() -> new ExhibitionReviewException(ExhibitionReviewErrorCode.EXHIBITION_REVIEW_NOT_FOUND));
    }

    private void saveReviewImages(List<MultipartFile> images, ExhibitionReview exhibitionReview) {
        if(images == null || images.isEmpty()) return;

        List<ExhibitionReviewImage> exhibitionReviewImages = images.stream()
                .map(image -> {
                    String url = fileStoragePort.upload(image, "exhibitionReviews");
                    return ImageConverter.toExhibitionReviewImage(exhibitionReview, url);
                })
                .toList();
        exhibitionReviewImageRepository.saveAll(exhibitionReviewImages);
    }

    private void moveImagesToTrash(List<String> fileKeys) {
        try {
            imageCommandService.deleteImages(fileKeys);
        } catch (ImageException e) {
            throw new ImageException(ImageErrorCode.S3_MOVE_TRASH_FAIL);
        }
    }

    private Exhibition getActiveOpeningExhibition(Long exhibitionId) {
        return exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApproveAndOpening(exhibitionId, LocalDate.now(), Status.APPROVED)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
    }

    private User getActiveUser(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}
