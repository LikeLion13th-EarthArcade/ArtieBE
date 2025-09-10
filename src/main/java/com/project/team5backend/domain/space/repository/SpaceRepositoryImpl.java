package com.project.team5backend.domain.space.repository;

import com.project.team5backend.domain.facility.entity.QFacility;
import com.project.team5backend.domain.facility.entity.QSpaceFacility;
import com.project.team5backend.domain.reservation.entity.QReservation;
import com.project.team5backend.domain.reservation.entity.ReservationStatus;
import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.domain.space.entity.*;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.global.entity.enums.Status;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SpaceRepositoryImpl implements SpaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Space> findSpacesWithFilters(
            LocalDate requestedStartDate, LocalDate requestedEndDate, String district, SpaceSize size,
            SpaceType type, SpaceMood mood, List<String> facilities, Sort sort, Pageable pageable) {

        QSpace space = QSpace.space;
        QReservation reservation = QReservation.reservation;
        QSpaceFacility spaceFacility = QSpaceFacility.spaceFacility;

        // 전체 개수 조회
        Long total = queryFactory
                .select(space.count())
                .from(space)
                .where(
                        space.isDeleted.isFalse(),
                        noOverlappingReservation(reservation, space, requestedStartDate, requestedEndDate), // 예약기간
                        districtCondition(space, district),
                        sizeCondition(space, size),
                        typeCondition(space, type),
                        moodCondition(space, mood),
                        facilityCondition(space ,spaceFacility, facilities)
                        )
                .fetchOne();

        // 정렬, 디폴트 최신순
        OrderSpecifier<?> order = switch (sort == null ? Sort.POPULAR : sort) {
            case OLD     -> space.createdAt.asc();
            case POPULAR -> space.reviewCount.desc().nullsLast();
            case NEW     -> space.createdAt.desc();
        };

        // 페이징된 결과 조회
        List<Space> content = queryFactory
                .selectFrom(space)
                .where(
                        space.isDeleted.isFalse(),
                        noOverlappingReservation(reservation, space, requestedStartDate, requestedEndDate),
                        districtCondition(space, district),
                        sizeCondition(space, size),
                        typeCondition(space, type),
                        moodCondition(space, mood),
                        facilityCondition(space, spaceFacility, facilities)
                )
                .orderBy(order, space.id.desc()) // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression noOverlappingReservation(
            QReservation reservation,
            QSpace space,
            LocalDate requestedStartDate,
            LocalDate requestedEndDate
    ) {
        if (requestedStartDate == null || requestedEndDate == null) {
            return null; // 조건 적용하지 않음
        }
        return JPAExpressions
                .selectOne()
                .from(reservation)
                .where(
                        reservation.space.eq(space),
                        reservation.status.eq(Status.APPROVED),
                        reservation.startDate.loe(requestedEndDate),
                        reservation.endDate.goe(requestedStartDate)
                )
                .notExists();
    }

    private BooleanExpression districtCondition(QSpace space, String district) {
        return (district != null && !district.isBlank())
                ? space.address.district.equalsIgnoreCase(district.trim())
                : null;
    }

    private BooleanExpression sizeCondition(QSpace space, SpaceSize size) {
        return size != null ? space.size.eq(size) : null;
    }

    private BooleanExpression typeCondition(QSpace space, SpaceType type) {
        return type != null ? space.type.eq(type) : null;
    }

    private BooleanExpression moodCondition(QSpace space, SpaceMood mood) {
        return mood != null ? space.mood.eq(mood) : null;
    }

    private BooleanExpression facilityCondition(QSpace space, QSpaceFacility spaceFacility, List<String> facilities) {
        if (facilities == null || facilities.isEmpty()) return null;

        QFacility facility = QFacility.facility;

        // space.id 기준 groupBy 해서 시설 개수 카운트 → 요청한 개수와 같아야 함
        return space.id.in(
                JPAExpressions
                        .select(spaceFacility.space.id)
                        .from(spaceFacility)
                        .join(spaceFacility.facility, facility)  // Facility 조인
                        .where(facility.name.in(facilities)) // 이름 기준 검색
                        .groupBy(spaceFacility.space.id)
                        .having(spaceFacility.count().eq((long) facilities.size()))
        );
    }
}
