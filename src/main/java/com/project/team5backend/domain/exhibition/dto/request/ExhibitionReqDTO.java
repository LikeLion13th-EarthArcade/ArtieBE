package com.project.team5backend.domain.exhibition.dto.request;



import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionType;
import com.project.team5backend.global.address.dto.request.AddressReqDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.project.team5backend.global.constant.valid.MessageConstant.*;

public class ExhibitionReqDTO {
    public record ExhibitionCreateReqDTO(
            @NotBlank(message = BLANK_EXHIBITION_TITLE)
            String title,

            String description,

            @NotNull(message = BLANK_EXHIBITION_START_DATE)
            LocalDate startDate,

            @NotNull(message = BLANK_EXHIBITION_END_DATE)
            LocalDate endDate,

            @NotNull(message = BLANK_EXHIBITION_OPERATING_HOURS)
            LocalTime openingTime,

            @NotNull(message = BLANK_EXHIBITION_OPERATING_HOURS)
            LocalTime closingTime,

            String operatingInfo,

            String websiteUrl,

            @NotNull(message = BLANK_EXHIBITION_CATEGORY)
            ExhibitionCategory exhibitionCategory,

            @NotNull(message = BLANK_EXHIBITION_TYPE)
            ExhibitionType exhibitionType,

            @NotNull(message = BLANK_EXHIBITION_MOOD)
            ExhibitionMood exhibitionMood,

            @NotNull(message = BLANK_EXHIBITION_PRICE)
            Integer price,

            List<String> facilities,

            @NotNull(message = BLANK_EXHIBITION_ADDRESS)
            @Valid
            AddressReqDTO.AddressCreateReqDTO address
    ) {}
}
