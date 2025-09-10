package com.project.team5backend.domain.space.converter;

import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.SpaceLike;
import com.project.team5backend.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceLikeConverter {

    public static SpaceLike toSpaceLike (User user, Space space) {
        return SpaceLike.builder()
                .user(user)
                .space(space)
                .build();
    }

    public static SpaceResDTO.LikeSpaceResDTO toLikeSpaceResDTO(long spaceId, String message) {
        return SpaceResDTO.LikeSpaceResDTO.builder()
                .spaceId(spaceId)
                .message(message)
                .build();
    }

}