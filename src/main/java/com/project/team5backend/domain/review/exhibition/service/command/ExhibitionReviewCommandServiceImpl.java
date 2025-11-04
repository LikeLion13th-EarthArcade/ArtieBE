package com.project.team5backend.domain.review.exhibition.service.command;

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
import com.project.team5backend.global.infra.s3.S3FileStorageAdapter;
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
    private final S3FileStorageAdapter s3FileStorageAdapter;
    @Override
    public ExhibitionReviewResDTO.ExReviewCreateResDTO createExhibitionReview(Long exhibitionId, Long userId, ExhibitionReviewReqDTO.createExReviewReqDTO createExhibitionReviewReqDTO, List<MultipartFile> images) {
        Exhibition exhibition = exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApproveAndOpening(exhibitionId, LocalDate.now(), Status.APPROVED)
                .orElseThrow(()-> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));

        ExhibitionReview exhibitionReview = ExhibitionReviewConverter.toEntity(createExhibitionReviewReqDTO, exhibition, user);
        exhibitionReviewRepository.save(exhibitionReview);

        saveReviewImages(images, exhibitionReview);

        exhibitionRepository.applyReviewCreated(exhibitionId, exhibitionReview.getRate()); // 리뷰 평균/카운트 갱신

        return ExhibitionReviewConverter.toExReviewCreateResDTO(exhibitionReview.getId());
    }
    @Override
    public void deleteExhibitionReview(Long exhibitionReviewId, Long userId) {
        ExhibitionReview exhibitionReview = exhibitionReviewRepository.findByIdAndIsDeletedFalse(exhibitionReviewId)
                .orElseThrow(() -> new ExhibitionReviewException(ExhibitionReviewErrorCode.EXHIBITION_REVIEW_NOT_FOUND));
        if (!exhibitionReview.getUser().getId().equals(userId)) {
            throw new ExhibitionReviewException(ExhibitionReviewErrorCode.EXHIBITION_REVIEW_FORBIDDEN);
        }
        exhibitionReview.softDelete();
        List<String> fileKeys = deleteExhibitionReviewImage(exhibitionReview.getId()); // 전시이미지 소프트 삭제

        exhibitionRepository.applyReviewDeleted(exhibitionReview.getExhibition().getId(), exhibitionReview.getRate()); // 리뷰 평균/카운트 갱신

        moveImagesToTrash(fileKeys); // s3 보존 휴지통 prefix로 이동시키기
    }

    private void saveReviewImages(List<MultipartFile> images, ExhibitionReview exhibitionReview) {
        List<ExhibitionReviewImage> exhibitionImages = Optional.ofNullable(images)
                .orElseGet(List::of)
                .stream()
                .map(file -> {
                    String url = s3FileStorageAdapter.upload(file, "exhibitionReviews");
                    return ImageConverter.toExhibitionReviewImage(exhibitionReview, url);
                })
                .toList();

        if (!exhibitionImages.isEmpty()) {
            exhibitionReviewImageRepository.saveAll(exhibitionImages);
        }
    }

    private List<String> deleteExhibitionReviewImage(Long exhibitionReviewId) {
        List<ExhibitionReviewImage> images = exhibitionReviewImageRepository.findByExhibitionReviewId(exhibitionReviewId);
        images.forEach(ExhibitionReviewImage::deleteImage);
        return images.stream()
                .map(ExhibitionReviewImage::getFileKey)
                .toList();
    }

    private void moveImagesToTrash(List<String> fileKeys) {
        try {
            imageCommandService.deleteImages(fileKeys);
        } catch (ImageException e) {
            throw new ImageException(ImageErrorCode.S3_MOVE_TRASH_FAIL);
        }
    }
}
