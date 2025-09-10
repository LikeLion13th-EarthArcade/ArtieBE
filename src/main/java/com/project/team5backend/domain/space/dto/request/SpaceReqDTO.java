package com.project.team5backend.domain.space.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.global.address.dto.request.AddressReqDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


import java.time.LocalTime;
import java.util.List;

public class SpaceReqDTO {

    // 전시 공간 등록 요청 DTO
    public record CreateSpaceReqDTO (
            @Schema(description = "공간 위치") @NotNull @Valid AddressReqDTO.AddressCreateReqDTO address,
            @Schema(description = "공간 유형")SpaceType type,
            @Schema(description = "공간 사양")SpaceSize size,
            @Schema(description = "공간 분위기")SpaceMood mood,
            @Schema(description = "공간 이름")String name,
            @Schema(description = "운영 시작 시간",  example = "10:00") @JsonFormat(pattern = "HH:mm") LocalTime openTime,
            @Schema(description = "운영 종료 시간",  example = "20:00") @JsonFormat(pattern = "HH:mm") LocalTime closeTime,
            @Schema(description = "공간 설명")String description,
            @Schema(description = "시설")List<String> facilities,
            @Schema(description = "전화") String phoneNumber,
            @Schema(description = "이메일")String email,
            @Schema(description = "공간 소개 링크")String websiteUrl,
            @Schema(description = "SNS")String snsUrl
    ){}
}