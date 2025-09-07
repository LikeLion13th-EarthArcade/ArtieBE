package com.project.team5backend.domain.space.space.dto.request;


import com.project.team5backend.domain.space.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.space.entity.enums.SpacePurpose;
import com.project.team5backend.domain.space.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.space.entity.enums.SpaceType;
import com.project.team5backend.global.address.dto.request.AddressReqDTO;
import com.project.team5backend.global.entity.enums.Facility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SpaceReqDTO {

    // 전시 공간 등록 요청 DTO
    public record CreateSpaceReqDTO (
            @NotNull @Valid AddressReqDTO.AddressCreateReqDTO address, // 공간 위치
            SpaceType type, // 공간 타입
            SpaceSize size, // 공간 크기(면적)
            SpacePurpose purpose, // 공간 목적
            SpaceMood mood, // 공간 분위기
            String name,  // 공간 이름
            LocalDate startDate, // 공간 이용 시작일
            LocalDate endDate, // 공간 이용 마감일
            String description,// 공간 설명
            LocalTime openTime, // 공간 운영 시작 시간
            LocalTime closeTime, // 공간 운영 종료 시간
            List<Facility> facility, // 시설
            String phoneNumber,
            String email,
            String homepageUrl,
            String snsUrl
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