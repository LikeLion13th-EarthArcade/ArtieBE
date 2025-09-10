package com.project.team5backend.domain.reservation.repository;

import com.project.team5backend.domain.reservation.entity.QReservation;
import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.reservation.entity.ReservationStatus;
import com.project.team5backend.domain.space.space.entity.QSpace;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.enums.Status;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomReservationRepositoryImpl implements CustomReservationRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Reservation> findBySpaceOwnerWithFilters(User user, ReservationStatus status, Pageable pageable) {
        QReservation reservation = QReservation.reservation;
        QSpace space = QSpace.space;

        BooleanBuilder builder = new BooleanBuilder()
                .and(reservation.space.user.eq(user));

        if (status != null) {
            builder.and(reservation.status.eq(status));
        }

        List<Reservation> content = queryFactory
                .selectFrom(reservation)
                .join(reservation.space, space).fetchJoin()
                .join(space.user).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(reservation.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(reservation.count())
                .from(reservation)
                .join(reservation.space)
                .join(reservation.space.user)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}

