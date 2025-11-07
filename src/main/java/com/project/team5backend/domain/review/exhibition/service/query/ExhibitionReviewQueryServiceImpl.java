package com.project.team5backend.domain.review.exhibition.service.query;

import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import com.project.team5backend.domain.image.repository.ExhibitionReviewImageRepository;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionReviewQueryServiceImpl implements ExhibitionReviewQueryService {

    private final ExhibitionReviewRepository exhibitionReviewRepository;
    private final ExhibitionReviewImageRepository exhibitionReviewImageRepository;
    private final FileUrlResolverPort fileUrlResolverPort;
    private final UserRepository userRepository;

    @Override
    public ExhibitionReviewResDTO.ExReviewDetailResDTO getExhibitionReviewDetail(Long exhibitionReviewId) {
        ExhibitionReview exhibitionReview = getActiveExhibitionReview(exhibitionReviewId);

        List<String> imageUrls = getFileUrls(exhibitionReview);
        return ExhibitionReviewConverter.toExReviewDetailResDTO(exhibitionReview, imageUrls);
    }

    @Override
    public Page<ExhibitionReviewResDTO.ExReviewDetailResDTO> getExhibitionReviews(Long exhibitionId, Sort sort, Pageable pageable) {
        Page<ExhibitionReview> reviewPage = exhibitionReviewRepository.findByExhibitionIdAndIsDeletedFalse(exhibitionId, sort, pageable);

        return reviewPage.map(review -> {
            List<String> imageUrls = getFileUrls(review);
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
                    .map(fileUrlResolverPort::toFileUrl)
                    .toList();
            return ExhibitionReviewConverter.toExReviewDetailResDTO(review, imageUrls);
        });
    }


    private List<String> getFileUrls(ExhibitionReview exhibitionReview) {
        return exhibitionReview.getExhibitionReviewImages().stream()
                .map(ExhibitionReviewImage::getFileKey)
                .map(fileUrlResolverPort::toFileUrl)
                .toList();
    }

    private ExhibitionReview getActiveExhibitionReview(Long exhibitionReviewId) {
        return exhibitionReviewRepository.findByIdAndIsDeletedFalse(exhibitionReviewId)
                .orElseThrow(() -> new ExhibitionReviewException(ExhibitionReviewErrorCode.EXHIBITION_REVIEW_NOT_FOUND));
    }
}
