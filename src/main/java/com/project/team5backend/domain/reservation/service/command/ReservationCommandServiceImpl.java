package com.project.team5backend.domain.reservation.service.command;

import com.project.team5backend.domain.reservation.converter.ReservationConverter;
import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.reservation.exception.ReservationErrorCode;
import com.project.team5backend.domain.reservation.exception.ReservationException;
import com.project.team5backend.domain.reservation.repository.ReservationRepository;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.entity.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandServiceImpl implements ReservationCommandService {

    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public ReservationResDTO.ReservationCreateResDTO createReservation(long spaceId, long userId, ReservationReqDTO.ReservationCreateReqDTO reservationCreateReqDTO) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Reservation reservation = ReservationConverter.toReservation(space, user, reservationCreateReqDTO);
        reservationRepository.save(reservation);

        return ReservationConverter.toReservationCreateResDTO(reservation);
    }

    @Override
    public ReservationResDTO.ReservationStatusResDTO approveRequest(long userId, long reservationId) {

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
    public ReservationResDTO.ReservationStatusResDTO rejectRequest(long userId, long reservationId, ReservationReqDTO.ReservationRejectReqDTO reservationRejectReqDTO) {
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
    public ReservationResDTO.ReservationStatusResDTO requestCancellation(long userId, long reservationId, ReservationReqDTO.ReservationCancellationReqDTO reservationCancellationReqDTO) {
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

    private Reservation validateReservation(long userId, long reservationId) {

        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!Objects.equals(reservation.getUser(), user)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_ACCESS_DENIED);
        }

        return reservation;
    }
}

