package com.project.team5backend.domain.reservation.controller;

import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.entity.ReservationStatus;
import com.project.team5backend.domain.reservation.service.command.ReservationCommandService;
import com.project.team5backend.domain.reservation.service.query.ReservationQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import com.project.team5backend.global.util.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "SPACE RESERVATION", description = "공간 예약 관련 API")
public class ReservationController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    @Operation(summary = "전시 공간 예약")
    @PostMapping("/spaces/{spaceId}/reservations")
    public CustomResponse<ReservationResDTO.ReservationCreateResDTO> createReservation(
            @PathVariable Long spaceId,
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid ReservationReqDTO.ReservationCreateReqDTO reservationCreateReqDTO
    ) {
        return CustomResponse.onSuccess(reservationCommandService.createReservation(spaceId, currentUser.getId(), reservationCreateReqDTO));
    }

    @Operation(summary = "예약 단일 조회")
    @GetMapping("/reservations/{reservationId}")
    public CustomResponse<ReservationResDTO.ReservationDetailResDTO> getReservationDetail(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable long reservationId
    ) {
        return CustomResponse.onSuccess(reservationQueryService.getReservationDetail(currentUser.getId(), reservationId));
    }

    @Operation(summary = "예약 목록 조회 (호스트 전용)",
            description = "내가 등록한 공간에 대한 예약 목록 조회 <br>" +
                    "status의 경우에는 입력하지 않으면 전체 목록이 나온다. <br>" +
                    "PENDING : 호스트 승인 대기, CONFIRMED : 호스트 승인 완료, CANCELED : 취소된 예약, DONE : 만료된 과거 예약 기록")
    @GetMapping("/reservations/host")
    public CustomResponse<PageResponse<ReservationResDTO.ReservationDetailResDTO>> getReservationList(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return CustomResponse.onSuccess(PageResponse.of(reservationQueryService.getReservationListForSpaceOwner(currentUser.getId(), status, pageable)));
    }

    @Operation(summary = "예약 목록 조회 (예약자 전용)", description = "내가 예약한 목록 조회")
    @GetMapping("/reservations/my")
    public CustomResponse<PageResponse<ReservationResDTO.ReservationDetailResDTO>> getMyReservationList(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return CustomResponse.onSuccess(PageResponse.of(reservationQueryService.getMyReservationList(currentUser.getId(), status, pageable)));
    }
}


