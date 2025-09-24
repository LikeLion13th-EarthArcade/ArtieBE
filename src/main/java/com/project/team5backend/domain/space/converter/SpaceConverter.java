package com.project.team5backend.domain.space.converter;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.domain.facility.entity.SpaceFacility;
import com.project.team5backend.domain.space.dto.request.SpaceReqDTO;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.SpaceVerification;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.util.PageResponse;

import java.util.List;
import java.util.Objects;

public class SpaceConverter {
    public static Space toSpace(SpaceReqDTO.SpaceCreateReqDTO spaceCreateReqDTO, User user, String thumbnail, Address address){

        return Space.builder()
                .name(spaceCreateReqDTO.name())
                .description(spaceCreateReqDTO.description())
                .operatingInfo(spaceCreateReqDTO.operatingStartHour() + "/" +  spaceCreateReqDTO.operatingEndHour() + "/" + spaceCreateReqDTO.operatingOption())
                .thumbnail(thumbnail)
                .phoneNumber(spaceCreateReqDTO.phoneNumber())
                .email(spaceCreateReqDTO.email())
                .websiteUrl(spaceCreateReqDTO.websiteUrl())
                .snsUrl(spaceCreateReqDTO.snsUrl())
                .ratingAvg(0.0)
                .likeCount(0)
                .reviewCount(0)
                .reviewSum(0)
                .isDeleted(false)
                .address(address)
                .spaceType(spaceCreateReqDTO.spaceType())
                .spaceSize(spaceCreateReqDTO.spaceSize())
                .spaceMood(spaceCreateReqDTO.spaceMood())
                .status(Status.PENDING)
                .user(user)
                .build();
    }

    public static SpaceFacility toSpaceFacility(Space space, Facility facility) {
        return SpaceFacility.builder()
                .space(space)
                .facility(facility)
                .build();
    }

    public static SpaceVerification toSpaceVerification(Space space, String bizNumber, String businessLicenseFileUrl, String buildingRegisterFileUrl){
        return SpaceVerification.builder()
                .bizNumber(bizNumber)
                .businessLicenseKey(businessLicenseFileUrl)
                .buildingRegisterKey(buildingRegisterFileUrl)
                .space(space)
                .build();
    }

    public static SpaceResDTO.SpaceCreateResDTO toSpaceCreateResDTO(Space space){
        return SpaceResDTO.SpaceCreateResDTO.builder()
                .id(space.getId())
                .createdAt(space.getCreatedAt())
                .build();
    }

    public static SpaceResDTO.SpaceDetailResDTO toSpaceDetailResDTO(Space space, List<String> imageUrls){
        return SpaceResDTO.SpaceDetailResDTO.builder()
                .spaceId(space.getId())
                .name(space.getName())
                .imageUrls(imageUrls)
                .address(formatAddress(space.getAddress()))
                .latitude(space.getAddress().getLatitude())
                .longitude(space.getAddress().getLongitude())
                .operatingInfo(space.getOperatingInfo())
                .spaceSize(space.getSpaceSize())
                .spaceMood(space.getSpaceMood())
                .description(space.getDescription())
                .facilities(extractFacility(space))
                .phoneNumber(space.getPhoneNumber())
                .email(space.getEmail())
                .websiteUrl(space.getWebsiteUrl())
                .snsUrl(space.getSnsUrl())
                .build();
    }

    public static SpaceResDTO.MySpaceDetailResDTO toMySpaceDetailResDTO(Space space,
                                                                      SpaceVerification spaceVerification,
                                                                      List<String> imageUrls,
                                                                      String businessLicenseFile,
                                                                      String buildingRegisterFile)
    {
        return SpaceResDTO.MySpaceDetailResDTO.builder()
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

    public static SpaceResDTO.SpaceSearchResDTO toSpaceSearchResDTO(Space space, String thumbnail){
        return SpaceResDTO.SpaceSearchResDTO.builder()
                .spaceId(space.getId())
                .name(space.getName())
                .thumbnail(thumbnail)
                .operatingInfo(space.getOperatingInfo())
                .address(space.getAddress().getRoadAddress() + " " + space.getAddress().getDetail())
                .latitude(space.getAddress().getLatitude())
                .longitude(space.getAddress().getLongitude())
                .build();
    }

    public static SpaceResDTO.SpaceSearchPageResDTO toSpaceSearchPageResDTO(PageResponse<SpaceResDTO.SpaceSearchResDTO> page, Double lat, Double lon) {
        return SpaceResDTO.SpaceSearchPageResDTO.builder()
                .page(page)
                .map(new SpaceResDTO.SpaceSearchPageResDTO.MapInfo(lat, lon))
                .build();
    }

    private static SpaceResDTO.MySpaceDetailResDTO.SpaceVerificationResDTO toVerificationResDTO(SpaceVerification verification, String businessLicenseFile, String buildingRegisterFile) {
        return SpaceResDTO.MySpaceDetailResDTO.SpaceVerificationResDTO.builder()
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

    private static String formatAddress(Address address) {
        if (address == null) return null;
        return String.format("%s %s",
                Objects.toString(address.getRoadAddress(), ""),
                Objects.toString(address.getDetail(), "")
        ).trim();
    }

}