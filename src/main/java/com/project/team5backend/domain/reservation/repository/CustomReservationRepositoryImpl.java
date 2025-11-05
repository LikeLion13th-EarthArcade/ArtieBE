package com.project.team5backend.domain.reservation.repository;

import com.project.team5backend.domain.reservation.entity.QReservation;
import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.space.entity.QSpace;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.common.enums.Status;
import com.project.team5backend.domain.common.enums.StatusGroup;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.project.team5backend.domain.reservation.entity.QReservation.reservation;

@Repository
@RequiredArgsConstructor
public class CustomReservationRepositoryImpl implements CustomReservationRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Reservation> findBySpaceOwnerWithFilters(User user, StatusGroup statusGroup, Pageable pageable) {
        QReservation reservation = QReservation.reservation;
        QSpace space = QSpace.space;

        BooleanBuilder builder = new BooleanBuilder()
                .and(reservation.space.user.eq(user))
                .and(statusGroupCondition(statusGroup));

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

    @Override
    public Page<Reservation> findByUserWithFilters(User user, StatusGroup statusGroup, Pageable pageable) {
        QReservation reservation = QReservation.reservation;

        BooleanBuilder builder = new BooleanBuilder()
                .and(reservation.user.eq(user))
                .and(statusGroupCondition(statusGroup));

        List<Reservation> content = findReservations(reservation, builder, pageable);

        Long total = countReservation(reservation, builder);

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<Reservation> findAllReservationWithFilters(StatusGroup statusGroup, Pageable pageable) {
        QReservation reservation = QReservation.reservation;

        BooleanBuilder builder = new BooleanBuilder()
                .and(statusGroupCondition(statusGroup));

        List<Reservation> content = findReservations(reservation, builder, pageable);

        Long total = countReservation(reservation, builder);

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression statusGroupCondition(StatusGroup statusGroup) {
        if (statusGroup == null || statusGroup == StatusGroup.ALL) {
            return null;
        }
        return switch (statusGroup) {
            case PENDING -> reservation.status.in(
                    Status.PENDING,
                    Status.BOOKER_CANCEL_REQUESTED
            );
            case DONE -> reservation.status.in(
                    Status.APPROVED,
                    Status.REJECTED,
                    Status.BOOKER_CANCEL_REJECTED,
                    Status.CANCELED_BY_BOOKER,
                    Status.CANCELED_BY_HOST
            );
            default -> null;
        };
    }

    private List<Reservation> findReservations(QReservation reservation, BooleanBuilder builder, Pageable pageable) {
        return queryFactory
                .selectFrom(reservation)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(reservation.createdAt.desc())
                .fetch();
    }

    private Long countReservation(QReservation reservation, BooleanBuilder builder) {
        return queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(builder)
                .fetchOne();
    }
}

