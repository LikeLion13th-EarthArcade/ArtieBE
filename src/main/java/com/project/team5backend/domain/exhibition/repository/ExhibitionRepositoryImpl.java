package com.project.team5backend.domain.exhibition.repository;

import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.entity.QExhibition;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.entity.enums.StatusGroup;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExhibitionRepositoryImpl implements ExhibitionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Exhibition> findExhibitionsWithFilters(
            ExhibitionCategory exhibitionCategory, String district, ExhibitionMood exhibitionMood, LocalDate localDate,
            Sort sort, Pageable pageable) {

        QExhibition exhibition = QExhibition.exhibition;

        Long total = queryFactory
                .select(exhibition.count())
                .from(exhibition)
                .where(
                        exhibition.isDeleted.isFalse(),
                        exhibition.status.eq(Status.APPROVED),
                        dateCondition(exhibition, localDate),
                        categoryCondition(exhibition, exhibitionCategory),
                        districtCondition(exhibition, district),
                        moodCondition(exhibition, exhibitionMood)
                )
                .fetchOne();

        // 정렬, 디폴트 최신순
        OrderSpecifier<?> order = switch (sort == null ? Sort.POPULAR : sort) {
            case OLD     -> exhibition.createdAt.asc();
            case POPULAR -> exhibition.reviewCount.desc().nullsLast();
            case NEW     -> exhibition.createdAt.desc();
        };

        // 페이징된 결과 조회
        List<Exhibition> content = queryFactory
                .selectFrom(exhibition)
                .where(
                        exhibition.isDeleted.isFalse(),
                        exhibition.status.eq(Status.APPROVED),
                        dateCondition(exhibition, localDate),
                        categoryCondition(exhibition, exhibitionCategory),
                        districtCondition(exhibition, district),
                        moodCondition(exhibition, exhibitionMood)
                )
                .orderBy(order, exhibition.id.desc()) // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
    @Override
    public List<Exhibition> findUnpopularCandidates(LocalDate today, int limit) {
        QExhibition exhibition = QExhibition.exhibition;

        var likes   = com.querydsl.core.types.dsl.Expressions
                .numberTemplate(Integer.class, "coalesce({0},0)", exhibition.likeCount);
        var reviews = com.querydsl.core.types.dsl.Expressions
                .numberTemplate(Integer.class, "coalesce({0},0)", exhibition.reviewCount);

        // 인기 점수: 좋아요 + 2*리뷰수 (원하면 가중치 바꿔도 됨)
        var popularity = likes.add(reviews.multiply(2));

        return queryFactory
                .selectFrom(exhibition)
                .where(
                        exhibition.isDeleted.isFalse(),
                        exhibition.status.eq(Status.APPROVED),
                        exhibition.startDate.loe(today),
                        exhibition.endDate.goe(today)
                )
                .orderBy(
                        popularity.asc(),   // 덜 인기 순
                        exhibition.createdAt.desc(), // 동률이면 더 최신 우선
                        exhibition.id.desc()
                )
                .limit(limit)
                .fetch();
    }

    @Override
    public Page<Exhibition> findAdminExhibitionsByStatus(StatusGroup status, Pageable pageable){
        QExhibition exhibition = QExhibition.exhibition;

        Long total = queryFactory
                .select(exhibition.count())
                .from(exhibition)
                .where(statusCondition(exhibition, status))
                .fetchOne();

        List<Exhibition> content = queryFactory
                .selectFrom(exhibition)
                .where(statusCondition(exhibition, status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<Exhibition> findMyExhibitionsByStatus(Long userId, StatusGroup status, Pageable pageable){
        QExhibition exhibition = QExhibition.exhibition;

        Long total = queryFactory
                .select(exhibition.count())
                .from(exhibition)
                .where(
                        exhibition.user.id.eq(userId),
                        statusCondition(exhibition, status))
                .fetchOne();

        List<Exhibition> content = queryFactory
                .selectFrom(exhibition)
                .where(
                        exhibition.user.id.eq(userId),
                        statusCondition(exhibition, status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression dateCondition(QExhibition exhibition, LocalDate localDate) {
        if (localDate != null) {
            // 사용자가 날짜를 지정한 경우: 그 날짜에 진행 중인 전시
            return exhibition.startDate.loe(localDate)
                    .and(exhibition.endDate.goe(localDate));
        } else {
            // 날짜 미지정: 아직 끝나지 않은 전시(진행 중 or 예정)
            LocalDate today = LocalDate.now();
            return exhibition.endDate.goe(today);
        }
    }

    private BooleanExpression categoryCondition(QExhibition exhibition, ExhibitionCategory category) {
        return category != null ? exhibition.exhibitionCategory.eq(category) : null;
    }

    private BooleanExpression districtCondition(QExhibition exhibition, String district) {
        return (district != null && !district.isBlank())
                ? exhibition.address.district.eq(district.trim())
                : null;
    }

    private BooleanExpression moodCondition(QExhibition exhibition, ExhibitionMood mood) {
        return mood != null ? exhibition.exhibitionMood.eq(mood) : null;
    }

    private BooleanExpression statusCondition(QExhibition exhibition, StatusGroup status) {
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(7).atStartOfDay();
        return switch (status) {
            case ALL -> exhibition.createdAt.goe(sevenDaysAgo);
            case PENDING -> exhibition.status.eq(Status.PENDING).and(exhibition.createdAt.goe(sevenDaysAgo));
            case DONE -> exhibition.status.in(Status.APPROVED, Status.REJECTED).and(exhibition.createdAt.goe(sevenDaysAgo));
        };
    }
}