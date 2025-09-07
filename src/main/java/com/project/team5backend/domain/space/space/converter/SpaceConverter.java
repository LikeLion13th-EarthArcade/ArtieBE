package com.project.team5backend.domain.space.space.converter;

import com.project.team5backend.domain.space.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.project.team5backend.domain.space.space.dto.request.SpaceReqDTO;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SpaceConverter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // SpaceRequest.Create DTO를 Space 엔티티로 변환
    public static Space toSpace(SpaceReqDTO.CreateSpaceReqDTO request, User user, String thumbnail, Address address){

        return Space.builder()
                .name(request.name())
                .description(request.description())
                .openTime(request.openTime())
                .closeTime(request.closeTime())
                .thumbnail(thumbnail)
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .homepageUrl(request.homepageUrl())
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
}