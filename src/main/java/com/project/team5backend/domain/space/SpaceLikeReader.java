package com.project.team5backend.domain.space;

import com.project.team5backend.domain.space.repository.SpaceLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpaceLikeReader {

    private final SpaceLikeRepository spaceLikeRepository;

    public boolean isLikedByUser(Long userId, Long spaceId) {
        return spaceLikeRepository.existsByUserIdAndSpaceId(userId, spaceId);
    }

}
