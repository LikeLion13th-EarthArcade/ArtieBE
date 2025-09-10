package com.project.team5backend.domain.reservation.controller;

import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.service.command.ReservationCommandService;
import com.project.team5backend.domain.reservation.service.query.ReservationQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.userdetails.CurrentUser;
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

}


