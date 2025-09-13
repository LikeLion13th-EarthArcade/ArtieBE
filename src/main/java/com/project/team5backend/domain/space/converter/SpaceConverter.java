package com.project.team5backend.domain.space.converter;

import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.domain.facility.entity.SpaceFacility;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.util.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.project.team5backend.domain.space.dto.request.SpaceReqDTO;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SpaceConverter {
    public static Space toSpace(SpaceReqDTO.SpaceCreateReqDTO request, User user, String thumbnail, Address address){

        return Space.builder()
                .name(request.name())
                .description(request.description())
                .openTime(request.openTime())
                .closeTime(request.closeTime())
                .thumbnail(thumbnail)
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .websiteUrl(request.websiteUrl())
                .snsUrl(request.snsUrl())
                .ratingAvg(BigDecimal.ZERO)
                .likeCount(0)
                .reviewCount(0)
                .totalReviewScore(0)
                .isDeleted(false)
                .address(address)
                .type(request.type())
                .size(request.size())
                .mood(request.mood())
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
                .address(
                        space.getAddress() != null
                                ? String.format("%s %s",
                                space.getAddress().getRoadAddress(),
                                space.getAddress().getDetail() != null ? space.getAddress().getDetail() : "")
                                : null
                )
                .latitude(space.getAddress().getLatitude())
                .longitude(space.getAddress().getLongitude())
                .openTime(space.getOpenTime())
                .closeTime(space.getCloseTime())
                .spaceSize(space.getSize())
                .spaceMood(space.getMood())
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

    public static SpaceResDTO.SpaceSearchResDTO toSpaceSearchResDTO(Space space){
        return SpaceResDTO.SpaceSearchResDTO.builder()
                .spaceId(space.getId())
                .name(space.getName())
                .thumbnail(space.getThumbnail())
                .openTime(space.getOpenTime())
                .closeTime(space.getCloseTime())
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
}