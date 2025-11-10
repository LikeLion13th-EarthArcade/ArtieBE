package com.project.team5backend.domain.reservation.service.lock;

import com.project.team5backend.domain.reservation.converter.ReservationConverter;
import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.exception.ReservationErrorCode;
import com.project.team5backend.domain.reservation.exception.ReservationException;
import com.project.team5backend.domain.reservation.repository.ReservationRepository;
import com.project.team5backend.global.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DistributedLockServiceImpl implements  DistributedLockService {

    private final RedisUtils<String> redisUtils;
    private final DefaultRedisScript<List> lockAcquireScript;
    private final DefaultRedisScript<Long> lockReleaseScript;
    private final DefaultRedisScript<Long> lockRenewScript;
    private final ReservationRepository reservationRepository;

    @Override
    public ReservationResDTO.ReservationLockAcquireResDTO acquireLocks(String email, Long spaceId, ReservationReqDTO.ReservationLockAcquireReqDTO reservationLockAcquireReqDTO) {
        LocalDate startDate = reservationLockAcquireReqDTO.startDate();
        LocalDate endDate = reservationLockAcquireReqDTO.endDate();

        List<LocalDate> dateSlots = generateSlots(startDate, endDate);

        for (LocalDate date : dateSlots) {
            if (reservationRepository.existsByDateAndTimeSlots(date)) {
                throw new ReservationException(ReservationErrorCode.ALREADY_RESERVED);
            }
        }

        List<String> keys = dateSlots.stream()
                .map(date -> "lock:" + spaceId + ":" + date)
                .toList();

        List<Object> result = redisUtils.executeLua(lockAcquireScript, keys, email, String.valueOf(TimeUnit.MINUTES.toMillis(5)));

        if (result.get(0).equals(0L)) {
            throw new ReservationException(ReservationErrorCode.LOCK_CONFLICT);
        }
        return ReservationConverter.toReservationLockAcquireResDTO(spaceId, result);
    }

    private List<LocalDate> generateSlots(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dateSlots = new ArrayList<>();
        while (!startDate.isAfter(endDate)) {
            dateSlots.add(startDate);
            startDate = startDate.plusDays(1);
        }
        return dateSlots;
    }
}
