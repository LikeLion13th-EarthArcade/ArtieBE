package com.project.team5backend.domain.review.exhibition.service.command;

import com.project.team5backend.domain.review.exhibition.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.review.exhibition.dto.response.ExhibitionReviewResDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExhibitionReviewCommandService {
    ExhibitionReviewResDTO.ExReviewCreateResDTO createExhibitionReview(Long exhibitionId, Long userId, ExhibitionReviewReqDTO.createExReviewReqDTO createExReviewReqDTO, List<MultipartFile> images);

    void deleteExhibitionReview(Long exhibitionReviewId, Long userId);
}
