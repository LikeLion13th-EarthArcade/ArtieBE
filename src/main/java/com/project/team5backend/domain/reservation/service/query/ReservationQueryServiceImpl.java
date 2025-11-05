package com.project.team5backend.domain.reservation.service.query;

import com.project.team5backend.domain.reservation.converter.ReservationConverter;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.reservation.exception.ReservationErrorCode;
import com.project.team5backend.domain.reservation.exception.ReservationException;
import com.project.team5backend.domain.reservation.repository.CustomReservationRepository;
import com.project.team5backend.domain.reservation.repository.ReservationRepository;
import com.project.team5backend.domain.user.entity.Role;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.domain.common.enums.StatusGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryServiceImpl implements ReservationQueryService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final CustomReservationRepository customReservationRepository;

    @Override
    public ReservationResDTO.ReservationDetailResDTO getReservationDetail(long userId, long reservationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // 관리자가 아니거나, 해당 예약의 주인이 아니면 예외
        if (!Objects.equals(user, reservation.getUser()) || !Objects.equals(user.getRole(), Role.ROLE_ADMIN)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_ACCESS_DENIED);
        }

        return ReservationConverter.toReservationDetailResDTO(reservation);
    }

    @Override
    public Page<ReservationResDTO.ReservationDetailResDTO> getReservationListForSpaceOwner(long userId, StatusGroup statusGroup, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Page<Reservation> reservationPage = customReservationRepository.findBySpaceOwnerWithFilters(user, statusGroup, pageable);

        return reservationPage.map(ReservationConverter::toReservationDetailResDTO);
    }

    @Override
    public Page<ReservationResDTO.ReservationDetailResDTO> getMyReservationList(long userId, StatusGroup statusGroup, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Page<Reservation> reservationPage = customReservationRepository.findByUserWithFilters(user, statusGroup, pageable);

        return reservationPage.map(ReservationConverter::toReservationDetailResDTO);
    }
}


