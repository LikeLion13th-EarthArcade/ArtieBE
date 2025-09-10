package com.project.team5backend.domain.exhibition.dto.request;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionType;
import com.project.team5backend.global.address.dto.request.AddressReqDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.project.team5backend.global.constant.valid.MessageConstant.*;

public class ExhibitionReqDTO {
    public record CreateExhibitionReqDTO (
            @NotBlank(message = BLANK_EXHIBITION_TITLE)
            String title,

            String description,

            @NotNull(message = BLANK_EXHIBITION_START_DATE)
            LocalDate startDate,

            @NotNull(message = BLANK_EXHIBITION_END_DATE)
            LocalDate endDate,

            @Schema(description = "운영 시작 시간", example = "10:00")
            @JsonFormat(pattern = "HH:mm")
            @NotNull(message = BLANK_EXHIBITION_OPEN_TIME)
            LocalTime openTime,

            @Schema(description = "운영 종료 시간", example = "20:00")
            @JsonFormat(pattern = "HH:mm")
            @NotNull(message = BLANK_EXHIBITION_CLOSE_TIME)
            LocalTime closeTime,

            String websiteUrl,

            @NotNull(message = BLANK_EXHIBITION_CATEGORY)
            ExhibitionCategory exhibitionCategory,

            @NotNull(message = BLANK_EXHIBITION_TYPE)
            ExhibitionType exhibitionType,

            @NotNull(message = BLANK_EXHIBITION_MOOD)
            ExhibitionMood exhibitionMood,

            Integer price,
            List<String> facilities,

            @NotNull(message = BLANK_EXHIBITION_ADDRESS)
            @Valid
            AddressReqDTO.AddressCreateReqDTO address
    ) {}
}
