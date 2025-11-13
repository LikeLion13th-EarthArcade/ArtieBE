package com.project.team5backend.domain.reservation.controller;

import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.service.command.ReservationCommandService;
import com.project.team5backend.domain.reservation.service.lock.DistributedLockService;
import com.project.team5backend.domain.reservation.service.query.ReservationQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.domain.common.enums.StatusGroup;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import com.project.team5backend.global.util.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "SPACE RESERVATION", description = "공간 예약 관련 API")
public class ReservationController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;
    private final DistributedLockService distributedLockService;

    @Operation(summary = "전시 공간 예약", description = "예약 날짜 락 획득이 선행되어야 함")
    @PostMapping("/spaces/{spaceId}/reservations")
    public CustomResponse<ReservationResDTO.ReservationCreateResDTO> createReservation(
            @PathVariable Long spaceId,
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid ReservationReqDTO.ReservationCreateReqDTO reservationCreateReqDTO
    ) {
        return CustomResponse.onSuccess(HttpStatus.CREATED, reservationCommandService.createReservation(spaceId, currentUser.getId(), reservationCreateReqDTO));
    }

    @Operation(summary = "예약 단일 조회")
    @GetMapping("/reservations/{reservationId}")
    public CustomResponse<ReservationResDTO.ReservationDetailResDTO> getReservationDetail(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long reservationId
    ) {
        return CustomResponse.onSuccess(reservationQueryService.getReservationDetail(currentUser.getId(), reservationId));
    }

    @Operation(summary = "예약 목록 조회 (호스트 전용)",
            description = "내가 등록한 공간에 대한 예약 목록 조회 <br>" +
                    "[RequestParam statusGroup] : [ALL], [PENDING], [DONE] 3가지 선택<br><br>" +
                    "ALL : 모든 상태의 예약 <br><br>" +
                    "PENDING : 진행중 <br>PENDING(호스트의 승인 대기), BOOKER_CANCEL_REQUESTED(예약자의 취소 요청됨) <br><br>" +
                    "DONE : 완료됨 <br>APPROVED(호스트의 예약 승인), REJECTED(PENDING 상태에서 호스트의 거절), CANCELED_BY_BOOKER(예약자 취소 요청 승인), <br>" +
                    "BOOKER_CANCEL_REJECTED(예약자의 취소 요청 거절됨), CANCELED_BY_HOST(호스트의 예약 거절(예약 확정됐는데, 호스트 책임의 거절))")
    @GetMapping("/reservations/host")
    public CustomResponse<PageResponse<ReservationResDTO.ReservationDetailResDTO>> getReservationList(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam StatusGroup statusGroup,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return CustomResponse.onSuccess(PageResponse.of(reservationQueryService.getReservationListForSpaceOwner(currentUser.getId(), statusGroup, pageable)));
    }

    @Operation(summary = "예약 목록 조회 (예약자 전용)",
            description = "내가 생성한 예약 목록 조회 <br><br>" +
                    "[RequestParam statusGroup] : [ALL], [PENDING], [DONE] 3가지 선택<br><br>" +
                    "ALL : 모든 상태의 예약 <br><br>" +
                    "PENDING : 진행중 <br>PENDING(호스트 승인 대기), BOOKER_CANCEL_REQUESTED(예약자의 취소 요청됨) <br><br>" +
                    "DONE : 완료됨 <br>APPROVED(호스트 예약 승인), REJECTED(PENDING 상태에서 호스트의 거절), CANCELED_BY_BOOKER(예약자 취소 요청 승인), <br>" +
                    "BOOKER_CANCEL_REJECTED(예약자 취소 요청 거부), CANCELED_BY_HOST(호스트의 예약 거절(예약 확정됐는데, 호스트 책임의 거절))<br><br>" +
                    "Reservation Status 참고 : https://www.notion.so/Reservation-2782a6b086de80ca8123d0ea8d57edbb")
    @GetMapping("/reservations/my")
    public CustomResponse<PageResponse<ReservationResDTO.ReservationDetailResDTO>> getMyReservationList(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam(required = false) StatusGroup statusGroup,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return CustomResponse.onSuccess(PageResponse.of(reservationQueryService.getMyReservationList(currentUser.getId(), statusGroup, pageable)));
    }

    @Operation(summary = "예약자의 요청 수락 (호스트 전용)",
            description = "PENDING(호스트 승인 대기) -> APPROVED(호스트 예약 승인)<br>" +
            "BOOKER_CANCEL_REQUESTED(예약자 취소 요청됨) -> CANCELED_BY_BOOKER(예약자 취소 요청 승인)<br><br>" +
                    "Reservation Status 참고 : https://www.notion.so/Reservation-2782a6b086de80ca8123d0ea8d57edbb")
    @PostMapping("/reservations/{reservationId}/host/approval")
    public CustomResponse<ReservationResDTO.ReservationStatusResDTO> approveRequest(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long reservationId
    ) {
        return CustomResponse.onSuccess(reservationCommandService.approveRequest(currentUser.getId(), reservationId));
    }

    @Operation(summary = "예약자의 요청 거절 (호스트 전용)",
            description = "PENDING(호스트 승인 대기) -> REJECTED(승인 거절)<br>" +
                    "APPROVED(호스트 예약 승인) -> CANCELED_BY_HOST(호스트의 취소)<br>" +
                    "BOOKER_CANCEL_REQUESTED(예약자 취소 요청됨) -> BOOKER_CANCEL_REJECTED(예약자 취소 요청 거부)<br><br>" +
                    "Reservation Status 참고 : https://www.notion.so/Reservation-2782a6b086de80ca8123d0ea8d57edbb")
    @PostMapping("/reservations/{reservationId}/host/rejection")
    public CustomResponse<ReservationResDTO.ReservationStatusResDTO> rejectRequest(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationReqDTO.ReservationRejectReqDTO reservationRejectReqDTO
    ) {
        return CustomResponse.onSuccess(reservationCommandService.rejectRequest(currentUser.getId(), reservationId, reservationRejectReqDTO));
    }

    @Operation(summary = "예약 취소 요청 (예약자 전용)",
            description = "PENDING(호스트 승인 대기) -> CANCELED(승인 되기 전에 취소함)<br>" +
                    "APPROVED(호스트 예약 승인) -> BOOKER_CANCEL_REQUESTED(예약자 취소 요청됨)<br><br>" +
                    "Reservation Status 참고 : https://www.notion.so/Reservation-2782a6b086de80ca8123d0ea8d57edbb")
    @PostMapping("/reservations/{reservationId}/booker/cancel-request")
    public CustomResponse<ReservationResDTO.ReservationStatusResDTO> requestCancellation(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationReqDTO.ReservationCancellationReqDTO reservationCancellationReqDTO
    ) {
        return CustomResponse.onSuccess(reservationCommandService.requestCancellation(currentUser.getId(), reservationId, reservationCancellationReqDTO));
    }

    @Operation(summary = "예약 날짜 락 획득", description = "락 생성 성공시 모든 성공한 락 리스트")
    @PostMapping("/reservations/spaces/{spaceId}/locks/acquire")
    public CustomResponse<ReservationResDTO.ReservationLockAcquireResDTO> acquireLocks(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId,
            @RequestBody @Valid ReservationReqDTO.ReservationLockAcquireReqDTO reservationLockAcquireReqDTO
    ) {
        return CustomResponse.onSuccess(distributedLockService.acquireLocks(currentUser.getEmail(), spaceId, reservationLockAcquireReqDTO));
    }

    @Operation(summary = "락 삭제(프론트 개발용)", description = "락은 예약을 생성하면서 사라지는데, 개발할 때 혹시 삭제가 필요할까봐 만듦")
    @PostMapping("reservation/spaces/{spaceId}/locks/renew")
    public CustomResponse<ReservationResDTO.ReservationLockReleaseResDTO> releaseLocksProd(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId,
            @RequestBody @Valid ReservationReqDTO.ReservationLockReleaseReqDTO reservationLockReleaseReqDTO
    ) {
        return CustomResponse.onSuccess(distributedLockService.releaseLocksProd(spaceId, currentUser.getEmail(), reservationLockReleaseReqDTO));
    }
}


