package com.project.team5backend.domain.review.space.service.query;


import com.project.team5backend.domain.common.enums.ReviewSearchType;
import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.domain.image.entity.SpaceReviewImage;
import com.project.team5backend.domain.review.space.SpaceReviewReader;
import com.project.team5backend.domain.review.space.converter.SpaceReviewConverter;
import com.project.team5backend.domain.review.space.dto.response.SpaceReviewResDTO;
import com.project.team5backend.domain.review.space.entity.SpaceReview;
import com.project.team5backend.domain.review.space.exception.SpaceReviewErrorCode;
import com.project.team5backend.domain.review.space.exception.SpaceReviewException;
import com.project.team5backend.domain.review.space.repository.SpaceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpaceReviewQueryServiceImpl implements SpaceReviewQueryService {

    private final SpaceReviewRepository spaceReviewRepository;
    private final SpaceReviewReader spaceReviewReader;
    private final FileUrlResolverPort fileUrlResolverPort;

    @Override
    public SpaceReviewResDTO.SpaceReviewDetailResDTO getSpaceReviewDetail(Long spaceReviewId) {
        SpaceReview spaceReview = getActiveSpaceReview(spaceReviewId);

        List<String> imageUrls = getFileUrls(spaceReview);
        return SpaceReviewConverter.toSpaceReviewDetailResDTO(spaceReview, imageUrls);
    }

    @Override
    public Page<SpaceReviewResDTO.SpaceReviewDetailResDTO> getSpaceReviews(Long spaceId, Sort sort, Pageable pageable) {
        Page<SpaceReview> spaceReviewPage = spaceReviewReader.readBySpace(spaceId, sort, pageable);
        return toReviewDetailPage(spaceReviewPage);
    }

    @Override
    public Page<SpaceReviewResDTO.SpaceReviewDetailResDTO> getMySpaceReviews(Long userId, Sort sort, Pageable pageable) {
        Page<SpaceReview> spaceReviewPage = spaceReviewReader.readByUser(userId, sort, pageable);
        return toReviewDetailPage(spaceReviewPage);
    }

    private List<String> getFileUrls(SpaceReview spaceReview) {
        return spaceReview.getSpaceReviewImages().stream()
                .map(SpaceReviewImage::getFileKey)
                .map(fileUrlResolverPort::toFileUrl)
                .toList();
    }

    private SpaceReview getActiveSpaceReview(Long spaceReviewId) {
        return spaceReviewRepository.findByIdAndIsDeletedFalse(spaceReviewId)
                .orElseThrow(() -> new SpaceReviewException(SpaceReviewErrorCode.SPACE_REVIEW_NOT_FOUND));
    }

    private Page<SpaceReviewResDTO.SpaceReviewDetailResDTO> toReviewDetailPage(Page<SpaceReview> spaceReviewPage) {
        return spaceReviewPage.map(spaceReview -> {
            List<String> imageUrls = getFileUrls(spaceReview);
            return SpaceReviewConverter.toSpaceReviewDetailResDTO(spaceReview, imageUrls);
        });
    }
}