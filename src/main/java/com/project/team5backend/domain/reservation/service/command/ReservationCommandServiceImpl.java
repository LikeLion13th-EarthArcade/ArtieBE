package com.project.team5backend.domain.reservation.service.command;

import com.project.team5backend.domain.reservation.converter.ReservationConverter;
import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.reservation.exception.ReservationErrorCode;
import com.project.team5backend.domain.reservation.exception.ReservationException;
import com.project.team5backend.domain.reservation.repository.ReservationRepository;
import com.project.team5backend.domain.reservation.service.lock.DistributedLockService;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.domain.common.enums.Status;
import com.project.team5backend.global.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.project.team5backend.domain.common.util.DateUtils.generateSlots;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandServiceImpl implements ReservationCommandService {

    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RedisUtils<String> redisUtils;
    private final DistributedLockService lockService;

    @Override
    public ReservationResDTO.ReservationCreateResDTO createReservation(Long spaceId, Long userId, ReservationReqDTO.ReservationCreateReqDTO reservationCreateReqDTO) {
        Space space = getSpace(spaceId);
        User user = getUser(userId);

        LocalDate startDate = reservationCreateReqDTO.startDate();
        LocalDate endDate = reservationCreateReqDTO.endDate();
        List<LocalDate> dateSlots = generateSlots(startDate, endDate);

        // 락이 제대로 존재하는지 확인
        validateLock(user, spaceId, dateSlots);

        Reservation reservation = ReservationConverter.toReservation(space, user, reservationCreateReqDTO);
        reservationRepository.save(reservation);

        lockService.releaseLocks(spaceId, user.getEmail(), dateSlots);
        return ReservationConverter.toReservationCreateResDTO(reservation);
    }

    @Override
    public ReservationResDTO.ReservationStatusResDTO approveRequest(Long userId, Long reservationId) {

        Reservation reservation = validateReservation(userId, reservationId);
        Status status = reservation.getStatus();

        if (!(status == Status.PENDING || status == Status.BOOKER_CANCEL_REQUESTED)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_STATUS_IS_NOT_APPROVABLE);
        }
        switch (status) {
            case PENDING -> reservation.changeStatus(Status.APPROVED);
            case BOOKER_CANCEL_REQUESTED ->  reservation.changeStatus(Status.CANCELED_BY_BOOKER);
        }

        return ReservationConverter.toReservationStatusResDTO(reservation);
    }

    @Override
    public ReservationResDTO.ReservationStatusResDTO rejectRequest(Long userId, Long reservationId, ReservationReqDTO.ReservationRejectReqDTO reservationRejectReqDTO) {

        Reservation reservation = validateReservation(userId, reservationId);
        Status status = reservation.getStatus();

        if (!(status == Status.PENDING || status == Status.BOOKER_CANCEL_REQUESTED || status == Status.APPROVED)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_STATUS_IS_NOT_REJECTABLE);
        }
        switch(reservation.getStatus()) {
            case PENDING -> reservation.changeStatus(Status.REJECTED);
            case BOOKER_CANCEL_REQUESTED -> reservation.changeStatus(Status.BOOKER_CANCEL_REJECTED);
            case APPROVED ->  reservation.changeStatus(Status.CANCELED_BY_HOST);
        }
        reservation.changeHostCancelReason(reservation.getHostCancelReason());

        return ReservationConverter.toReservationStatusResDTO(reservation);
    }

    @Override
    public ReservationResDTO.ReservationStatusResDTO requestCancellation(Long userId, Long reservationId, ReservationReqDTO.ReservationCancellationReqDTO reservationCancellationReqDTO) {
        Reservation reservation = validateReservation(userId, reservationId);

        Status status = reservation.getStatus();

        if (!(status == Status.PENDING || status == Status.APPROVED)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_STATUS_IS_NOT_CANCELLABLE);
        }
        switch (status) {
            case PENDING -> reservation.changeStatus(Status.CANCELED);
            case APPROVED -> reservation.changeStatus(Status.BOOKER_CANCEL_REQUESTED);
        }
        reservation.changeHostCancelReason(reservationCancellationReqDTO.bookerCancelReason());

        return ReservationConverter.toReservationStatusResDTO(reservation);
    }

    private Reservation validateReservation(Long userId, Long reservationId) {

        User user =  getUser(userId);
        Reservation reservation = getReservation(reservationId);

        if (!Objects.equals(reservation.getUser(), user)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_ACCESS_DENIED);
        }
        return reservation;
    }

    private void validateLock(User user, Long spaceId, List<LocalDate> dateSlots) {
        for (LocalDate date : dateSlots) {
            if (!Objects.equals(redisUtils.get("lock:" + spaceId + ":" + date), user.getEmail())) {
                throw new ReservationException(ReservationErrorCode.RESERVATION_DATE_LOCK_FAILED);
            }
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Space getSpace(Long spaceId) {
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));
    }

    private Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }
}

