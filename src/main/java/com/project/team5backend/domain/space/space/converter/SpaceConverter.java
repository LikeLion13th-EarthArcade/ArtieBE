package com.project.team5backend.domain.space.space.converter;

import com.project.team5backend.domain.space.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.enums.Status;
import org.springframework.stereotype.Component;
import com.project.team5backend.domain.space.space.dto.request.SpaceReqDTO;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SpaceConverter {
    public static Space toSpace(SpaceReqDTO.CreateSpaceReqDTO request, User user, String thumbnail, Address address){

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
                .isDeleted(false)
                .address(address)
                .type(request.type())
                .size(request.size())
                .purpose(request.purpose())
                .mood(request.mood())
                .status(Status.PENDING)
                .facilities(request.facility())
                .user(user)
                .build();
    }

    public static SpaceResDTO.CreateSpaceResDTO toCreateSpaceResDTO(Space space){
        return SpaceResDTO.CreateSpaceResDTO.builder()
                .id(space.getId())
                .createdAt(space.getCreatedAt())
                .build();
    }

    public static SpaceResDTO.DetailSpaceResDTO toDetailSpaceResDTO(Space space, List<String> imageUrls){
        return SpaceResDTO.DetailSpaceResDTO.builder()
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
                .spacePurpose(space.getPurpose())
                .spaceMood(space.getMood())
                .description(space.getDescription())
                .facility(space.getFacilities())
                .phoneNumber(space.getPhoneNumber())
                .email(space.getEmail())
                .websiteUrl(space.getWebsiteUrl())
                .snsUrl(space.getSnsUrl())
                .build();
    }
}