package com.project.team5backend.domain.reservation.scheduler;

import com.project.team5backend.domain.reservation.converter.ReservationConverter;
import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.reservation.entity.TempReservation;
import com.project.team5backend.domain.reservation.repository.ReservationRepository;
import com.project.team5backend.domain.reservation.repository.TempReservationRepository;
import com.project.team5backend.domain.reservation.service.lock.DistributedLockService;
import com.project.team5backend.global.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.project.team5backend.domain.common.util.DateUtils.generateSlots;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ReservationScheduler {

    private final TempReservationRepository tempReservationRepository;
    private final ReservationRepository reservationRepository;
    private final RedisUtils<String> redisUtils;
    private final DistributedLockService lockService;

    @Scheduled(cron = "0 * * * * *")
    public void checkDeposit() {
        log.info("[Reservation Scheduler] 입금 확인 시작");

        List<TempReservation> tempReservations = tempReservationRepository.findAll();

        List<TempReservation> tempReservationsToDelete = new ArrayList<>();

        for (TempReservation tempReservation : tempReservations) {
            if (tempReservation.isDeposited()) {
                if (redisUtils.hasLock("system:lock:" + tempReservation.getSpace().getId() + ":" + tempReservation.getEndDate())) {
                    log.info("[Reservation Scheduler] TempReservationId = {} 입금 완료", tempReservation.getTempReservationId());
                    Reservation reservation = ReservationConverter.toReservation(tempReservation);
                    reservationRepository.save(reservation);
                    log.info("[Reservation Scheduler] TempReservationId = {} 실제 예약 객체 생성 완료", tempReservation.getTempReservationId());
                    tempReservationsToDelete.add(tempReservation);
                    lockService.releaseLocks(tempReservation.getSpace().getId(), tempReservation.getUser().getEmail(), generateSlots(tempReservation.getStartDate(), tempReservation.getEndDate()));
                } else {
                    log.info("[Reservation Scheduler] TempReservationId = {} 입금 시간 늦음 환불 진행(구현 안해서 삭제)",  tempReservation.getTempReservationId());
                    tempReservationsToDelete.add(tempReservation);
                }
            } else {
                if (redisUtils.hasLock("lock:" + tempReservation.getSpace().getId() + ":" + tempReservation.getEndDate())) {
                    log.info("[Reservation Scheduler] TempReservationId = {} 아직 입금 되지 않음 대기",  tempReservation.getTempReservationId());
                } else {
                    log.info("[Reservation Scheduler] TempReservationId = {} 15분 내 입금하지 않음 삭제",  tempReservation.getTempReservationId());
                    tempReservationsToDelete.add(tempReservation);
                    lockService.releaseLocks(tempReservation.getSpace().getId(), tempReservation.getUser().getEmail(), generateSlots(tempReservation.getStartDate(), tempReservation.getEndDate()));
                }
            }
        }
        log.info("[Reservation Scheduler] 임시 예약 객체 {}개를 삭제합니다.",  tempReservationsToDelete.size());
        tempReservationRepository.deleteAll(tempReservationsToDelete);
        log.info("[Reservation Scheduler] 임시 예약 객체 {}개를 삭제 완료했습니다.", tempReservationsToDelete.size());
    }
}
