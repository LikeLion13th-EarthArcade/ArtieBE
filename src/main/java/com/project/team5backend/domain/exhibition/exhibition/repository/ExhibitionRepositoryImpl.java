package com.project.team5backend.domain.exhibition.exhibition.repository;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.QExhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.global.entity.enums.Status;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
public class ExhibitionRepositoryImpl implements ExhibitionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Exhibition> findExhibitionsWithFilters(
            Category category, String district, Mood mood, LocalDate localDate,
            Sort sort, Pageable pageable) {

        QExhibition exhibition = QExhibition.exhibition;

        // 동적 쿼리 조건 생성
        BooleanBuilder builder = new BooleanBuilder()
                .and(exhibition.isDeleted.isFalse())
                .and(exhibition.status.eq(Status.APPROVED));

        // 날짜 조건
        if (localDate != null) {
            // 사용자가 날짜를 지정한 경우: 그 날짜에 '진행중'인 전시만
            builder.and(exhibition.startDate.loe(localDate))
                    .and(exhibition.endDate.goe(localDate));
        } else {
            // 날짜 미지정: 아직 끝나지 않은 전시(진행중 or 진행예정)
            LocalDate today = LocalDate.now();
            builder.and(exhibition.endDate.goe(today));
        }

        // 카테고리 필터
        if (category != null) {
            builder.and(exhibition.category.eq(category));
        }

        // 지역 필터 (Address 엔티티의 roadAddress 필드 사용)
        if (district != null && !district.trim().isEmpty()) {
            builder.and(exhibition.address.district.equalsIgnoreCase(district.trim()));
        }

        // 분위기 필터
        if (mood != null) {
            builder.and(exhibition.mood.eq(mood));
        }

        // 전체 개수 조회 (fetchCount 대신 fetch().size() 사용 - 최신 QueryDSL 버전 호환)
        Long total = queryFactory
                .select(exhibition.count())
                .from(exhibition)
                .where(builder)
                .fetchOne();

        // 정렬, 디폴트 최신순
        OrderSpecifier<?> order = switch (sort == null ? Sort.POPULAR : sort) {
            case OLD     -> exhibition.createdAt.asc();
            case POPULAR -> new OrderSpecifier<>(Order.DESC, exhibition.reviewCount, OrderSpecifier.NullHandling.NullsLast);
            case NEW     -> exhibition.createdAt.desc();
        };

        // 페이징된 결과 조회
        List<Exhibition> content = queryFactory
                .selectFrom(exhibition)
                .where(builder)
                .orderBy(order, exhibition.id.desc()) // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
    @Override
    public List<Exhibition> findUnpopularCandidates(LocalDate today, int limit) {
        QExhibition e = QExhibition.exhibition;

        var likes   = com.querydsl.core.types.dsl.Expressions
                .numberTemplate(Integer.class, "coalesce({0},0)", e.likeCount);
        var reviews = com.querydsl.core.types.dsl.Expressions
                .numberTemplate(Integer.class, "coalesce({0},0)", e.reviewCount);

        // 인기 점수: 좋아요 + 2*리뷰수 (원하면 가중치 바꿔도 됨)
        var popularity = likes.add(reviews.multiply(2));

        return queryFactory
                .selectFrom(e)
                .where(
                        e.isDeleted.isFalse(),
                        e.status.eq(Status.APPROVED),
                        e.startDate.loe(today),
                        e.endDate.goe(today)
                )
                .orderBy(
                        popularity.asc(),   // 덜 인기 순
                        e.createdAt.desc(), // 동률이면 더 최신 우선
                        e.id.desc()
                )
                .limit(limit)
                .fetch();
    }
}