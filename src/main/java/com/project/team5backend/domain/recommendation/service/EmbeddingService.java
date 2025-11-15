package com.project.team5backend.domain.recommendation.service;

import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.recommendation.entity.ExhibitionEmbedding;
import com.project.team5backend.domain.recommendation.repository.ExhibitionEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    @Qualifier("openAiRestClient")
    private final RestClient openaiRestClient;
    private final ExhibitionEmbeddingRepository embRepo;

    public void upsertExhibitionEmbedding(Exhibition e) {
        // 승인된 것만 생성하도록 호출부에서 보장
        String input = String.join("\n",
                "title: " + ns(e.getTitle()),
                "exhibitionCategory: " + ns(String.valueOf(e.getExhibitionCategory())),
                "exhibitionMood: " + ns(String.valueOf(e.getExhibitionMood())),
                "location: " + ns(e.getAddress().getRoadAddress()),      // ← venue 대신 location
                "description: " + ns(e.getDescription()) // ← summary 대신 description
        );
        try {
            var req = Map.of("model", "text-embedding-3-small", "input", input);
            Map resp = openaiRestClient.post()
                    .uri("/embeddings")
                    .body(req)
                    .retrieve()
                    .body(Map.class);

            List data = (List) resp.get("data");
            if (data == null || data.isEmpty()) {
                log.warn("OpenAI embeddings이 비어있음 {}", e.getId());
                return;
            }
            Map first = (Map) data.get(0);
            List<Double> emb = (List<Double>) first.get("embedding");
            float[] vec = new float[emb.size()];
            for (int i=0; i<emb.size(); i++) vec[i] = emb.get(i).floatValue();

            embRepo.save(ExhibitionEmbedding.builder()
                    .exhibitionId(e.getId())
                    .vector(ExhibitionEmbedding.fromArray(vec))
                    .build());
        } catch (Exception ex) {
            log.error("Embedding upsert 실패 {}: {}", e.getId(), ex.getMessage());
            // 실패는 치명적이지 않음(야간 백필에서 재시도 가능)
        }
    }

    private String ns(String s){ return s==null? "" : s; }
}