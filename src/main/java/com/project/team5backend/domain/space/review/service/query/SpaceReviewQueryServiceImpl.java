package com.project.team5backend.domain.space.review.service.query;


import com.project.team5backend.domain.image.entity.SpaceReviewImage;
import com.project.team5backend.domain.space.review.converter.SpaceReviewConverter;
import com.project.team5backend.domain.space.review.dto.response.SpaceReviewResDTO;
import com.project.team5backend.domain.space.review.entity.SpaceReview;
import com.project.team5backend.domain.space.review.exception.SpaceReviewErrorCode;
import com.project.team5backend.domain.space.review.exception.SpaceReviewException;
import com.project.team5backend.domain.space.review.repository.SpaceReviewRepository;
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

    @Override
    public SpaceReviewResDTO.DetailSpaceReviewResDTO getSpaceReviewDetail(long spaceReviewId) {
        SpaceReview spaceReview = spaceReviewRepository.findByIdAndIsDeletedFalse(spaceReviewId)
                .orElseThrow(() -> new SpaceReviewException(SpaceReviewErrorCode.SPACE_REVIEW_NOT_FOUND));

        List<String> imageUrls = spaceReview.getSpaceReviewImages().stream()
                .map(SpaceReviewImage::getImageUrl)
                .toList();
        return SpaceReviewConverter.toDetailSpaceReviewResDTO(spaceReview, imageUrls);
    }

    public Page<SpaceReviewResDTO.DetailSpaceReviewResDTO> getSpaceReviewList(long spaceId, Pageable pageable) {
        Page<SpaceReview> spaceReviewPage = spaceReviewRepository.findBySpaceIdAndIsDeletedFalse(spaceId, pageable);


        return spaceReviewPage.map(spaceReview -> {
            List<String> imageUrls = spaceReview.getSpaceReviewImages().stream()
                    .map(SpaceReviewImage::getImageUrl)
                    .toList();

            return SpaceReviewConverter.toDetailSpaceReviewResDTO(spaceReview, imageUrls);
        });
    }
}