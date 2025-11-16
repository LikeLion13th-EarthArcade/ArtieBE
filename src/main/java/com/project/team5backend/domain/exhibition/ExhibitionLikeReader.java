package com.project.team5backend.domain.exhibition;

import com.project.team5backend.domain.exhibition.repository.ExhibitionLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExhibitionLikeReader {

    private final ExhibitionLikeRepository exhibitionLikeRepository;

    public boolean isLikedByUser(Long userId, Long exhibitionId) {
        return exhibitionLikeRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
    }
}
