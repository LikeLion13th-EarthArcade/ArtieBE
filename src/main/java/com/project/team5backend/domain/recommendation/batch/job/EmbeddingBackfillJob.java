package com.project.team5backend.domain.recommendation.batch.job;

import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.recommendation.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingBackfillJob {

    private final ExhibitionRepository exhibitionRepository;
    private final EmbeddingService embeddingService;

    // 매일 00:30 (누락건만 보정)
    @Scheduled(cron = "0 18 02 * * *", zone = "Asia/Seoul")
    public void backfillApprovedWithoutEmbedding() {
        // 간단하게: 최근 3일 승인 전시 중 임베딩 없는 것
        var threeDaysAgo = java.time.LocalDate.now().minusDays(3);
        List<Exhibition> candidates = exhibitionRepository.findApprovedWithoutEmbedding(threeDaysAgo);

        candidates.forEach(embeddingService::upsertExhibitionEmbedding);
        log.info("[EmbeddingBackfill] 완료, processed={}", candidates.size());
    }
}