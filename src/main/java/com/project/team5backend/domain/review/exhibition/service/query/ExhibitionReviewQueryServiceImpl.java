package com.project.team5backend.domain.review.exhibition.service.query;

import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import com.project.team5backend.domain.review.exhibition.converter.ExhibitionReviewConverter;
import com.project.team5backend.domain.review.exhibition.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.review.exhibition.exception.ExhibitionReviewErrorCode;
import com.project.team5backend.domain.review.exhibition.exception.ExhibitionReviewException;
import com.project.team5backend.domain.review.exhibition.repository.ExhibitionReviewRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.util.S3UrlResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionReviewQueryServiceImpl implements ExhibitionReviewQueryService {

    private final ExhibitionReviewRepository exhibitionReviewRepository;
    private final S3UrlResolver s3UrlResolver;
    private final UserRepository userRepository;

    @Override
    public ExhibitionReviewResDTO.ExReviewDetailResDTO getExhibitionReviewDetail(Long exhibitionReviewId) {
        ExhibitionReview exhibitionReview = exhibitionReviewRepository.findByIdAndIsDeletedFalse(exhibitionReviewId)
                .orElseThrow(()-> new ExhibitionReviewException(ExhibitionReviewErrorCode.EXHIBITION_REVIEW_NOT_FOUND));

        List<String> imageUrls = exhibitionReview.getExhibitionReviewImages().stream()
                .map(ExhibitionReviewImage::getFileKey)
                .map(s3UrlResolver::toFileUrl)
                .toList();
        return ExhibitionReviewConverter.toExReviewDetailResDTO(exhibitionReview, imageUrls);
    }

    @Override
    public Page<ExhibitionReviewResDTO.ExReviewDetailResDTO> getExhibitionReviews(Long exhibitionId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<ExhibitionReview> reviewPage = exhibitionReviewRepository.findByExhibitionIdAndIsDeletedFalse(exhibitionId, pageable);

        return reviewPage.map(review -> {
            List<String> imageUrls = review.getExhibitionReviewImages().stream()
                    .map(ExhibitionReviewImage::getFileKey)
                    .map(s3UrlResolver::toFileUrl)
                    .toList();
            return ExhibitionReviewConverter.toExReviewDetailResDTO(review, imageUrls);
        });
    }

    @Override
    public Page<ExhibitionReviewResDTO.ExReviewDetailResDTO> getMyExhibitionReviews(Long userId, Pageable pageable) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Page<ExhibitionReview> ExReviewPage = exhibitionReviewRepository.findMyExReviewsByIdAndIsDeletedFalse(user, pageable);

        return ExReviewPage.map(review -> {
            List<String> imageUrls = review.getExhibitionReviewImages().stream()
                    .map(ExhibitionReviewImage::getFileKey)
                    .map(s3UrlResolver::toFileUrl)
                    .toList();
            return ExhibitionReviewConverter.toExReviewDetailResDTO(review, imageUrls);
        });
    }
}
