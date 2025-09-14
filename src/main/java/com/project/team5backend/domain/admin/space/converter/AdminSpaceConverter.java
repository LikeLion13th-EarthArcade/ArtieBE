package com.project.team5backend.domain.admin.space.converter;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;

import java.util.List;


public class AdminSpaceConverter {
    public static AdminSpaceResDTO.SpaceSummaryResDTO toSpaceSummaryResDTO(Space space){
        return AdminSpaceResDTO.SpaceSummaryResDTO.builder()
                .spaceId(space.getId())
                .name(space.getName())
                .createdAt(space.getCreatedAt())
                .status(space.getStatus())
                .build();
    }

    public static AdminSpaceResDTO.SpaceStatusUpdateResDTO toSpaceStatusUpdateResDTO(Space space, String message){
        return AdminSpaceResDTO.SpaceStatusUpdateResDTO.builder()
                .spaceId(space.getId())
                .status(space.getStatus())
                .message(message)
                .build();
    }

    public static AdminSpaceResDTO.SpaceDetailResDTO toSpaceDetailResDTO(Space space, List<String> imageUrls){
        return AdminSpaceResDTO.SpaceDetailResDTO.builder()
                .spaceId(space.getId())
                .name(space.getName())
                .imageUrls(imageUrls)
                .address(
                        space.getAddress() != null
                                ? String.format("%s %s",
                                space.getAddress().getRoadAddress(),
                                space.getAddress().getDetail() != null ? space.getAddress().getDetail() : "")
                                : null
                )
                .openTime(space.getOpenTime())
                .closeTime(space.getCloseTime())
                .spaceSize(space.getSpaceSize())
                .spaceMood(space.getSpaceMood())
                .description(space.getDescription())
                .facilities(
                        space.getSpaceFacilities().stream()
                                .map(sf -> sf.getFacility().getName())
                                .toList()
                )
                .phoneNumber(space.getPhoneNumber())
                .email(space.getEmail())
                .websiteUrl(space.getWebsiteUrl())
                .snsUrl(space.getSnsUrl())
                .build();
    }
}
