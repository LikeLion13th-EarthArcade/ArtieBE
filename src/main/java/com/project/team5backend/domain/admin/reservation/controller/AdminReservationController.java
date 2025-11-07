package com.project.team5backend.domain.admin.reservation.controller;

import com.project.team5backend.domain.admin.reservation.service.query.AdminReservationQueryService;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.domain.common.enums.StatusGroup;
import com.project.team5backend.global.util.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reservations")
@Tag(name = "Admin Reservation", description = "admin 예약 관련 API")
public class AdminReservationController {

    private final AdminReservationQueryService adminReservationQueryService;

    @Operation(summary = "전체 예약 조회",
            description = "현재 서비스에서 생성된 모든 예약을 검색합니다. <br> " +
                    "[RequestParam statusGroup] : [ALL], [PENDING], [DONE] 3가지 선택<br><br>" +
                    "ALL : 모든 상태의 예약 <br><br>" +
                    "PENDING : 진행중 <br>PENDING(호스트 승인 대기), BOOKER_CANCEL_REQUESTED(예약자의 취소 요청됨) <br><br>" +
                    "DONE : 완료됨 <br>APPROVED(호스트 예약 승인), REJECTED(PENDING 상태에서 호스트의 거절), CANCELED_BY_BOOKER(예약자 취소 요청 승인), <br>" +
                    "BOOKER_CANCEL_REJECTED(예약자 취소 요청 거부), CANCELED_BY_HOST(호스트의 예약 거절(예약 확정됐는데, 호스트 책임의 거절))<br><br>" +
                    "Reservation Status 참고 : https://www.notion.so/Reservation-2782a6b086de80ca8123d0ea8d57edbb")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public CustomResponse<PageResponse<ReservationResDTO.ReservationDetailResDTO>> getReservationList(
            @RequestParam StatusGroup statusGroup,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return CustomResponse.onSuccess(PageResponse.of(adminReservationQueryService.getReservationList(statusGroup, pageable)));
    }

    @Operation(summary = "예약 단일 조회")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{reservationId}")
    public CustomResponse<ReservationResDTO.ReservationDetailResDTO> getReservationDetail(
            @PathVariable Long reservationId
    ) {
        return CustomResponse.onSuccess(adminReservationQueryService.getReservationDetail(reservationId));
    }
}
