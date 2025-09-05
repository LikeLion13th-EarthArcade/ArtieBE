package com.project.team5backend.domain.space.reservation.controller;

import com.project.team5backend.domain.space.reservation.dto.request.ReservationRequest;
import com.project.team5backend.domain.space.reservation.dto.response.ReservationResponse;
import com.project.team5backend.domain.space.reservation.service.command.ReservationCommandService;
import com.project.team5backend.domain.space.reservation.service.query.ReservationQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReservationController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    @Operation(summary = "전시 공간 예약")
    @PostMapping("spaces/{spaceId}/reservations")
    public CustomResponse<ReservationResponse.DetailResponse> createReservation(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody ReservationRequest.ReservationSpace request) {
        return CustomResponse.onSuccess(
                reservationCommandService.createReservation(currentUser.getId(), request)
        );
    }
    @Operation(summary = "예약 목록 조회")
    @GetMapping("spaces/reservations/manage")
    public CustomResponse<List<ReservationResponse.ListResponse>> getReservations(
            @AuthenticationPrincipal CurrentUser currentUser) {
        return CustomResponse.onSuccess(
                reservationQueryService.getReservations(currentUser.getId())
        );
    }
    @Operation(summary = "예약 상세 조회")
    @GetMapping("reservations/{reservationId}")
    public CustomResponse<ReservationResponse.DetailResponse> getReservationDetail(
            @PathVariable Long reservationId) {
        return CustomResponse.onSuccess(
                reservationQueryService.getReservationDetail(reservationId)
        );
    }
    @Operation(summary = "예약 확정")
    @PatchMapping("reservations/{reservationId}/confirm")
    public CustomResponse<String> confirm(
            @PathVariable Long reservationId,@AuthenticationPrincipal CurrentUser currentUser) {
        Long userId = currentUser.getId();
        reservationCommandService.confirmReservation(reservationId, userId);
        return CustomResponse.onSuccess("예약 확정 완료");
    }

    @Operation(summary = "예약 취소")
    @PatchMapping("reservations/{reservationId}/cancel")
    public CustomResponse<String> cancel(
            @PathVariable Long reservationId,
            @RequestBody ReservationRequest.CancelRequest request,
            @AuthenticationPrincipal CurrentUser currentUser) {
        // 로그인한 사용자의 ID를 가져와 서비스 메서드에 전달
        Long userId = currentUser.getId();
        reservationCommandService.cancelReservation(reservationId, userId, request.reason());
        return CustomResponse.onSuccess("예약 취소 완료");
    }

}


