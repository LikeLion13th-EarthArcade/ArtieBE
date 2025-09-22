package com.project.team5backend.domain.space.dto.request;


import com.project.team5backend.domain.space.entity.SpaceVerification;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.global.address.dto.request.AddressReqDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SpaceReqDTO {

    // 전시 공간 등록 요청 DTO
    public record SpaceCreateReqDTO(
            @NotBlank
            String businessNumber,
            @NotNull
            AddressReqDTO.AddressCreateReqDTO address,
            @NotNull
            SpaceType spaceType,
            @NotNull
            SpaceSize spaceSize,
            @NotNull
            SpaceMood spaceMood,
            @NotBlank
            String name,
            @NotBlank
            String operatingHours,

            String description,
            List<String> facilities,
            String phoneNumber,
            String email,
            String websiteUrl,
            String snsUrl
    ){
    }
}