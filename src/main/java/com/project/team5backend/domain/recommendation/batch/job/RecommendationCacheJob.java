package com.project.team5backend.domain.recommendation.batch.job;

import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.recommendation.cache.RecommendationCachePort;
import com.project.team5backend.domain.recommendation.converter.RecommendationConverter;
import com.project.team5backend.domain.recommendation.service.query.RecommendationQueryServiceImpl;
import com.project.team5backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationCacheJob {

    private final UserRepository userRepository;
    private final RecommendationQueryServiceImpl recommendationQueryService;
    private final RecommendationCachePort recommendationCachePort;


     // 매일 자정(00:00)에 실행 - 사용자별 top4 추천 결과 Redis에 캐싱
     @Scheduled(cron = "30 15 02 * * *", zone = "Asia/Seoul")
     public void refreshDailyRecommendations() {
         log.info("[Job] Start refreshing cached recommendations...");
         List<Long> userIds = userRepository.findAllUserIds();
         int count = 0;

         for (Long userId : userIds) {
             var keys = recommendationQueryService.computeKeys(userId);
             if (!keys.eligible()) continue;

             List<Exhibition> top4 = recommendationQueryService.recommendWithEmbedding(userId, keys, 4);
             if (top4.isEmpty()) continue;

             List<Long> ids = top4.stream().map(Exhibition::getId).toList();

             var dto = RecommendationConverter.toCachedRecommendationResDTO(ids, keys.category(), keys.mood(), keys.type());

             recommendationCachePort.saveTopRecommendations(userId, dto);
             count++;
         }

         log.info("[Job] Completed caching for {} users", count);
     }
}
