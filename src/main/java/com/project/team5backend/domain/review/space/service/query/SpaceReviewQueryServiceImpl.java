package com.project.team5backend.domain.review.space.service.query;


import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.image.entity.SpaceReviewImage;
import com.project.team5backend.domain.review.space.converter.SpaceReviewConverter;
import com.project.team5backend.domain.review.space.dto.response.SpaceReviewResDTO;
import com.project.team5backend.domain.review.space.entity.SpaceReview;
import com.project.team5backend.domain.review.space.exception.SpaceReviewErrorCode;
import com.project.team5backend.domain.review.space.exception.SpaceReviewException;
import com.project.team5backend.domain.review.space.repository.SpaceReviewRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpaceReviewQueryServiceImpl implements SpaceReviewQueryService {
    private final SpaceReviewRepository spaceReviewRepository;
    private final FileUrlResolverPort fileUrlResolverPort;
    private final UserRepository userRepository;

    @Override
    public SpaceReviewResDTO.SpaceReviewDetailResDTO getSpaceReviewDetail(Long spaceReviewId) {
        SpaceReview spaceReview = spaceReviewRepository.findByIdAndIsDeletedFalse(spaceReviewId)
                .orElseThrow(() -> new SpaceReviewException(SpaceReviewErrorCode.SPACE_REVIEW_NOT_FOUND));

        List<String> imageUrls = spaceReview.getSpaceReviewImages().stream()
                .map(SpaceReviewImage::getFileKey)
                .map(fileUrlResolverPort::toFileUrl)
                .toList();
        return SpaceReviewConverter.toSpaceReviewDetailResDTO(spaceReview, imageUrls);
    }

    public Page<SpaceReviewResDTO.SpaceReviewDetailResDTO> getSpaceReviewList(Long spaceId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<SpaceReview> spaceReviewPage = spaceReviewRepository.findBySpaceIdAndIsDeletedFalse(spaceId, pageable);

        return spaceReviewPage.map(spaceReview -> {
            List<String> imageUrls = spaceReview.getSpaceReviewImages().stream()
                    .map(SpaceReviewImage::getFileKey)
                    .map(fileUrlResolverPort::toFileUrl)
                    .toList();
            return SpaceReviewConverter.toSpaceReviewDetailResDTO(spaceReview, imageUrls);
        });
    }

    @Override
    public Page<SpaceReviewResDTO.SpaceReviewDetailResDTO> getMySpaceReviews(Long userId, Pageable pageable) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Page<SpaceReview> spaceReviewPage = spaceReviewRepository.findMySpaceReviewsByIdAndIsDeletedFalse(user, pageable);

        return spaceReviewPage.map(review -> {
            List<String> imageUrls = review.getSpaceReviewImages().stream()
                    .map(SpaceReviewImage::getFileKey)
                    .map(fileUrlResolverPort::toFileUrl)
                    .toList();
            return SpaceReviewConverter.toSpaceReviewDetailResDTO(review, imageUrls);
        });
    }
}