package com.project.team5backend.domain.exhibition.exhibition.dto.request;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionType;
import com.project.team5backend.global.address.dto.request.AddressReqDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ExhibitionReqDTO {
    public record CreateExhibitionReqDTO (
            @NotBlank String title,
            String description,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @Schema(description = "운영 시작 시간",  example = "10:00") @JsonFormat(pattern = "HH:mm") LocalTime openTime,
            @Schema(description = "운영 종료 시간",  example = "20:00") @JsonFormat(pattern = "HH:mm") LocalTime closeTime,
            String websiteUrl,
            @NotNull ExhibitionCategory exhibitionCategory,
            @NotNull ExhibitionType exhibitionType,
            @NotNull ExhibitionMood exhibitionMood,
            Integer price,
            List<String> facilities,
            @NotNull @Valid AddressReqDTO.AddressCreateReqDTO address
    ) {}

    public record SearchExhibitionReqDTO (
            ExhibitionCategory exhibitionCategory,
            String distinct,
            ExhibitionMood exhibitionMood,
            LocalDate localDate
    ){}
}
