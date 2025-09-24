package com.project.team5backend.domain.admin.space.converter;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.SpaceVerification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static AdminSpaceResDTO.SpaceDetailResDTO toSpaceDetailResDTO(Space space,
                                                                         SpaceVerification spaceVerification,
                                                                         List<String> imageUrls,
                                                                         String businessLicenseFile,
                                                                         String buildingRegisterFile)
    {
        return AdminSpaceResDTO.SpaceDetailResDTO.builder()
                .spaceId(space.getId())
                .name(space.getName())
                .spaceVerificationResDTO(toVerificationResDTO(spaceVerification, businessLicenseFile, buildingRegisterFile))
                .address(space.getAddress().getRoadAddress())
                .operatingInfo(space.getOperatingInfo())
                .spaceSize(space.getSpaceSize())
                .spaceMood(space.getSpaceMood())
                .description(space.getDescription())
                .facilities(extractFacility(space))
                .phoneNumber(space.getPhoneNumber())
                .email(space.getEmail())
                .websiteUrl(space.getWebsiteUrl())
                .snsUrl(space.getSnsUrl())
                .imageUrls(imageUrls)
                .build();
    }
    private static AdminSpaceResDTO.SpaceDetailResDTO.SpaceVerificationResDTO toVerificationResDTO(SpaceVerification verification, String businessLicenseFile, String buildingRegisterFile) {
        return AdminSpaceResDTO.SpaceDetailResDTO.SpaceVerificationResDTO.builder()
                .bizNumber(verification.getBizNumber())
                .businessLicenseFile(businessLicenseFile)
                .buildingRegisterFile(buildingRegisterFile)
                .build();
    }

    private static List<String> extractFacility(Space space) {
        if (space.getSpaceFacilities() == null) {
            return List.of(); // null-safe
        }
        return space.getSpaceFacilities().stream()
                .map(sf -> sf.getFacility().getName())
                .toList();
    }

}
