package com.project.team5backend.domain.recommendation.service.query;

import com.project.team5backend.domain.common.enums.Status;
import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.exhibition.ExhibitionLikeReader;
import com.project.team5backend.domain.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionType;
import com.project.team5backend.domain.exhibition.repository.ExhibitionLikeRepository;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.recommendation.cache.RecommendationCachePort;
import com.project.team5backend.domain.recommendation.converter.RecommendationConverter;
import com.project.team5backend.domain.recommendation.dto.KeyScoreRow;
import com.project.team5backend.domain.recommendation.dto.response.RecommendationResDTO;
import com.project.team5backend.domain.recommendation.entity.ExhibitionEmbedding;
import com.project.team5backend.domain.recommendation.repository.ExhibitionEmbeddingRepository;
import com.project.team5backend.domain.recommendation.repository.ExhibitionInteractLogRepository;
import com.project.team5backend.domain.recommendation.service.Reranker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendationQueryServiceImpl implements RecommendationQueryService {

    private final ExhibitionInteractLogRepository logRepo;
    private final ExhibitionRepository exhibitionRepo;
    private final ExhibitionEmbeddingRepository embRepo;
    private final ExhibitionLikeRepository likeRepo;
    private final ExhibitionLikeReader exhibitionLikeReader;
    private final FileUrlResolverPort fileUrlResolverPort;
    private final RecommendationCachePort recommendationCachePort;

    private static final int WINDOW_DAYS = 90;

    public record Keys(ExhibitionCategory category, ExhibitionType type, ExhibitionMood mood, boolean eligible) {}

    // 홈 요약(프리뷰 1개)
    public RecommendationResDTO.PersonalizedSummaryResDTO getPersonalizedSummary(Long userId) {
        var cache = recommendationCachePort.getTopRecommendations(userId);

        if (cache != null && cache.exhibitionIds() != null && !cache.exhibitionIds().isEmpty()) {
            var exhibition = exhibitionRepo.findById(cache.exhibitionIds().get(0)).orElse(null);
            if (exhibition != null) {
                boolean isLiked = exhibitionLikeReader.isLikedByUser(userId, exhibition.getId());
                return RecommendationConverter.toPersonalizedSummaryResDTO(exhibition, isLiked);
            }
        }
        return RecommendationResDTO.PersonalizedSummaryResDTO.empty();
    }

    // 상세(4개 + 키워드 라벨)
    public RecommendationResDTO.PersonalizedDetailResDTO getPersonalizedDetail(Long userId) {
        var cache = recommendationCachePort.getTopRecommendations(userId);
        if (cache != null && cache.exhibitionIds() != null && !cache.exhibitionIds().isEmpty()) {
            var exhibitions = exhibitionRepo.findAllById(cache.exhibitionIds());
            Set<Long> likedIds = fetchLikedExhibitionIds(userId, exhibitions);

            var items = exhibitions.stream()
                    .map(ex -> {
                        String thumbnail = fileUrlResolverPort.toFileUrl(ex.getThumbnail());
                        return ExhibitionConverter.toCard(ex, likedIds.contains(ex.getId()), thumbnail);
                    })
                    .toList();

            return RecommendationConverter.toPersonalizedDetailResDTO(
                    cache.topExhibitionCategory(), cache.topExhibitionType(), cache.topExhibitionMood(), items);
        }
        return RecommendationResDTO.PersonalizedDetailResDTO.empty();
    }

    // 좋아요 ID 세트 조회
    private Set<Long> fetchLikedExhibitionIds(Long userId, List<Exhibition> exhibitions) {
        if (userId == null || exhibitions.isEmpty()) return Set.of();
        List<Long> ids = exhibitions.stream().map(Exhibition::getId).toList();
        return new HashSet<>(likeRepo.findLikedExhibitionIds(userId, ids));
    }

    public Keys computeKeys(Long userId) {
        if (userId == null) return new Keys(null, null, null, false);
        var since = LocalDateTime.now().minusDays(WINDOW_DAYS);
        if (logRepo.countSince(userId, since) == 0) return new Keys(null, null, null, false);

        KeyScoreRow catRow = logRepo.topCategory(userId, since);
        KeyScoreRow typeRow = logRepo.topType(userId, since);
        KeyScoreRow moodRow = logRepo.topMood(userId, since);

        if (catRow == null && typeRow == null && moodRow == null)
            return new Keys(null, null, null, false);

        ExhibitionCategory category = (catRow != null)
                ? ExhibitionCategory.valueOf(catRow.getKeyName())
                : null;
        ExhibitionType type = (typeRow != null)
                ? ExhibitionType.valueOf(typeRow.getKeyName())
                : null;
        ExhibitionMood mood = (moodRow != null)
                ? ExhibitionMood.valueOf(moodRow.getKeyName())
                : null;
        return new Keys(category, type, mood, true);
    }

    public List<Exhibition> recommendWithEmbedding(Long userId, Keys keys, int topK) {
        List<Exhibition> candidates = fetchCandidateExhibitions(keys);
        if (candidates.isEmpty()) return List.of();

        float[] userVector = computeUserVector(userId);
        if (userVector == null) return applyFallbackRanking(candidates, keys, topK);

        return applyEmbeddingBasedRanking(candidates, userVector, keys, topK);
    }

    private List<Exhibition> fetchCandidateExhibitions(Keys keys) {
        var today = LocalDate.now();
        return exhibitionRepo.recommendByKeywords(
                keys.category(), keys.mood(), Status.APPROVED, today, PageRequest.of(0, 200)
        );
    }

    private float[] computeUserVector(Long userId) {
        var since = LocalDateTime.now().minusDays(WINDOW_DAYS);
        var recentIds = logRepo.findRecentExhibitionIds(userId, since, 50);
        var embeddings = embRepo.findByExhibitionIdIn(recentIds);
        return averageEmbedding(embeddings);
    }

    private List<Exhibition> applyFallbackRanking(List<Exhibition> candidates, Keys keys, int topK) {
        if (keys.category() != null && keys.mood() != null) {
            return Reranker.enforceAtLeastNAndMatches(
                    candidates, topK, 2,
                    Exhibition::getId,
                    Exhibition::getExhibitionCategory,
                    Exhibition::getExhibitionMood,
                    keys.category(),
                    keys.mood()
            );
        }
        return candidates.stream().limit(topK).toList();
    }

    private List<Exhibition> applyEmbeddingBasedRanking(
            List<Exhibition> candidates,
            float[] userVector,
            Keys keys,
            int topK
    ) {
        var embMap = buildEmbeddingMap(candidates.stream().map(Exhibition::getId).toList());
        var reviewScores = computeNormalizedReviewScores(candidates);

        final double W_SIM = 0.7;
        final double W_REV = 0.3;

        List<Exhibition> sorted = candidates.stream()
                .map(e -> {
                    float[] vec = embMap.get(e.getId());
                    double sim = (vec == null) ? 0 : cosine(userVector, vec);
                    double rev = reviewScores.getOrDefault(e.getId(), 0.0);
                    double score = W_SIM * sim + W_REV * rev;
                    return Map.entry(e, score);
                })
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        if (keys.category() != null && keys.mood() != null) {
            return Reranker.enforceAtLeastNAndMatches(
                    sorted, topK, 2,
                    Exhibition::getId,
                    Exhibition::getExhibitionCategory,
                    Exhibition::getExhibitionMood,
                    keys.category(),
                    keys.mood()
            );
        }

        return sorted.stream().limit(topK).toList();
    }

    private Map<Long, float[]> buildEmbeddingMap(List<Long> ids) {
        return embRepo.findByExhibitionIdIn(ids).stream()
                .collect(Collectors.toMap(
                        ExhibitionEmbedding::getExhibitionId,
                        ExhibitionEmbedding::toArray
                ));
    }

    private Map<Long, Double> computeNormalizedReviewScores(List<Exhibition> exhibitions) {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        Map<Long, Double> raw = new HashMap<>();

        for (var e : exhibitions) {
            double s = e.getReviewCount() + e.getReviewSum();
            raw.put(e.getId(), s);
            min = Math.min(min, s);
            max = Math.max(max, s);
        }

        double range = max - min;
        Map<Long, Double> normalized = new HashMap<>();

        for (var entry : raw.entrySet()) {
            double v = (range > 0)
                    ? (entry.getValue() - min) / range
                    : 0.0;
            normalized.put(entry.getKey(), v);
        }
        return normalized;
    }

    private float[] averageEmbedding(List<ExhibitionEmbedding> embeddings) {
        if (embeddings == null || embeddings.isEmpty()) return null;
        float[] sum = null;
        int count = 0;

        for (var e : embeddings) {
            float[] vec = e.toArray();
            if (sum == null) sum = new float[vec.length];
            for (int i = 0; i < vec.length; i++) sum[i] += vec[i];
            count++;
        }

        for (int i = 0; i < sum.length; i++) sum[i] /= count;
        return sum;
    }

    private double cosine(float[] a, float[] b) {
        if (a == null || b == null || a.length != b.length) return 0;
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}