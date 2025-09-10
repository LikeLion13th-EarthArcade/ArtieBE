package com.project.team5backend.domain.admin.space.converter;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminSpaceConverter {
    public static AdminSpaceResDTO.adminSpaceDetailResDTO toDetailAdminSpaceResDTO(Space spage){
        return AdminSpaceResDTO.adminSpaceDetailResDTO.builder()
                .spaceId(spage.getId())
                .name(spage.getName())
                .createdAt(spage.getCreatedAt())
                .status(spage.getStatus())
                .build();
    }
}
