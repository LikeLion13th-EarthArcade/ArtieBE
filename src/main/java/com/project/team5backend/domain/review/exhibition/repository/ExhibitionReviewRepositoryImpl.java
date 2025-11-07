package com.project.team5backend.domain.review.exhibition.repository;

import com.project.team5backend.domain.common.enums.ReviewSearchType;
import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.domain.image.entity.QExhibitionReviewImage;
import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.review.exhibition.entity.QExhibitionReview;
import com.project.team5backend.domain.review.exhibition.exception.ExhibitionReviewErrorCode;
import com.project.team5backend.domain.review.exhibition.exception.ExhibitionReviewException;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExhibitionReviewRepositoryImpl implements ExhibitionReviewRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ExhibitionReview> findReviewsByTargetId(Long targetId, ReviewSearchType type, Sort sort, Pageable pageable) {
        QExhibitionReview exhibitionReview = QExhibitionReview.exhibitionReview;
        QExhibitionReviewImage exhibitionReviewImage = QExhibitionReviewImage.exhibitionReviewImage;

        BooleanExpression condition = switch (type) {
            case SPACE ->  throw new ExhibitionReviewException(ExhibitionReviewErrorCode.EXHIBITION_REVIEW_BAD_REQUEST);
            case EXHIBITION -> exhibitionReview.exhibition.id.eq(targetId);
            case USER -> exhibitionReview.user.id.eq(targetId);
        };

        Long total = queryFactory
                .select(exhibitionReview.count())
                .from(exhibitionReview)
                .where(
                        condition,
                        exhibitionReview.isDeleted.isFalse()
                )
                .fetchOne();

        // 페이징된 결과 조회
        List<ExhibitionReview> content = queryFactory
                .selectFrom(exhibitionReview)
                .distinct()
                .join(exhibitionReview.exhibitionReviewImages, exhibitionReviewImage).fetchJoin()
                .where(
                        condition,
                        exhibitionReview.isDeleted.isFalse()
                )
                .orderBy(exhibitionReview.createdAt.asc()) // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
