package com.project.team5backend.domain.recommendation.service;

import com.project.team5backend.domain.recommendation.converter.RecommendationConverter;
import com.project.team5backend.domain.recommendation.entity.ExhibitionInteractLog;
import com.project.team5backend.domain.recommendation.model.ActionType;
import com.project.team5backend.domain.recommendation.repository.ExhibitionInteractLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InteractLogService {

    private final ExhibitionInteractLogRepository logRepo;
    private static final java.time.Duration DEDUP = java.time.Duration.ofMinutes(5);

    public void logClick(Long userId, Long exhibitionId) {
        logAction(userId, exhibitionId, ActionType.CLICK);
    }

    public void logLike(Long userId, Long exhibitionId) {
        logAction(userId, exhibitionId, ActionType.LIKE);
    }

    private void logAction(Long userId, Long exhibitionId, ActionType actionType) {
        LocalDateTime since = java.time.LocalDateTime.now().minus(DEDUP);
        if (logRepo.countRecent(userId, exhibitionId, "ACTION", since) == 0) {
            logRepo.save(RecommendationConverter.toExhibitionInteractLog(userId, exhibitionId, actionType));
        }
    }
}