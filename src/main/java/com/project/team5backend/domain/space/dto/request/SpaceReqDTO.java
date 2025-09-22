package com.project.team5backend.domain.space.dto.request;


import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.global.address.dto.request.AddressReqDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

import static com.project.team5backend.global.constant.valid.MessageConstant.*;
import static com.project.team5backend.global.constant.valid.PatternConstant.BIZ_NUMBER_PATTERN;
import static com.project.team5backend.global.constant.valid.PatternConstant.PHONE_NUMBER_PATTERN;

public class SpaceReqDTO {

    // 전시 공간 등록 요청 DTO
    public record SpaceCreateReqDTO(
            @NotBlank(message = BLANK_BIZ_NUMBER)
            @Pattern(regexp = BIZ_NUMBER_PATTERN, message = WRONG_BIZ_NUMBER_PATTERN)
            String bizNumber,

            @NotNull @Valid
            AddressReqDTO.AddressCreateReqDTO address,

            @NotNull
            SpaceType spaceType,

            @NotNull
            SpaceSize spaceSize,

            @NotNull
            SpaceMood spaceMood,

            @NotBlank(message = BLANK_NAME)
            String name,

            @NotBlank(message = BLANK_SPACE_OPERATING_HOURS)
            String operatingHours,

            String description,

            List<String> facilities,

            @NotBlank(message = BLANK_PHONE_NUMBER)
            @Pattern(regexp = PHONE_NUMBER_PATTERN, message = WRONG_PHONE_NUMBER_PATTERN)
            String phoneNumber,

            @NotBlank(message = BLANK_EMAIL)
            @Email
            String email,

            String websiteUrl,

            String snsUrl
    ){
    }
}