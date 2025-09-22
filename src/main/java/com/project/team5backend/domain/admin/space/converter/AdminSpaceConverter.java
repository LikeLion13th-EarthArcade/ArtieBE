package com.project.team5backend.domain.admin.space.converter;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.facility.entity.SpaceFacility;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.SpaceVerification;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.util.S3UrlResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class AdminSpaceConverter {

    private final S3UrlResolver s3UrlResolver;

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
                .operatingHours(space.getOperatingHours())
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
                .businessNumber(verification.getBusinessNumber())
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
