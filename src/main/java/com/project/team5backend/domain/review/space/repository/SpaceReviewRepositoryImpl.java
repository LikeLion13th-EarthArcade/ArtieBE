package com.project.team5backend.domain.review.space.repository;

import com.project.team5backend.domain.common.enums.ReviewSearchType;
import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.domain.image.entity.QSpaceReviewImage;
import com.project.team5backend.domain.review.space.entity.QSpaceReview;
import com.project.team5backend.domain.review.space.entity.SpaceReview;
import com.project.team5backend.domain.review.space.exception.SpaceReviewErrorCode;
import com.project.team5backend.domain.review.space.exception.SpaceReviewException;
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
public class SpaceReviewRepositoryImpl implements SpaceReviewRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SpaceReview> findReviewsByTargetId(Long targetId, ReviewSearchType type, Sort sort, Pageable pageable) {
        QSpaceReview spaceReview = QSpaceReview.spaceReview;
        QSpaceReviewImage spaceReviewImage = QSpaceReviewImage.spaceReviewImage;

        BooleanExpression condition = switch (type) {
            case EXHIBITION ->  throw new SpaceReviewException(SpaceReviewErrorCode.SPACE_REVIEW_BAD_REQUEST);
            case SPACE -> spaceReview.space.id.eq(targetId);
            case USER -> spaceReview.user.id.eq(targetId);
        };

        Long total = queryFactory
                .select(spaceReview.count())
                .from(spaceReview)
                .where(
                        condition,
                        spaceReview.isDeleted.isFalse()
                )
                .fetchOne();

        // 페이징된 결과 조회
        List<SpaceReview> content = queryFactory
                .selectFrom(spaceReview)
                .distinct()
                .join(spaceReview.spaceReviewImages, spaceReviewImage).fetchJoin()
                .where(
                        condition,
                        spaceReview.isDeleted.isFalse()
                )
                .orderBy(spaceReview.createdAt.desc()) // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
