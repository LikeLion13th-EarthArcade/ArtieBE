package com.project.team5backend.domain.space.space.dto.request;


import com.project.team5backend.domain.space.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.space.entity.enums.SpacePurpose;
import com.project.team5backend.domain.space.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.space.entity.enums.SpaceType;
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
            @Schema(description = "공간 목적")SpacePurpose purpose,
            @Schema(description = "공간 분위기")SpaceMood mood,
            @Schema(description = "공간 이름")String name,
            @Schema(description = "운영 시작 시간")LocalTime openTime,
            @Schema(description = "운영 종료 시간")LocalTime closeTime,
            @Schema(description = "공간 설명")String description,
            @Schema(description = "시설")List<String> facilities,
            @Schema(description = "전화") String phoneNumber,
            @Schema(description = "이메일")String email,
            @Schema(description = "공간 소개 링크")String websiteUrl,
            @Schema(description = "SNS")String snsUrl
    ){}

    // 공간 검색 요청 DTO
    public record Search (
            String address,
            SpaceSize size,
            SpaceType type,
            SpaceMood mood,
            String startDate,
            String endDate
    ){}

    public record Like (
            Long spaceId,
            boolean liked
    ){}
}