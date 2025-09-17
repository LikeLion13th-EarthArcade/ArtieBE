package com.project.team5backend.domain.exhibition.service.schedular;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiEnumService {

    private final RestClient openAiRestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExhibitionResDTO.ExhibitionEnumResDTO classify(String title, String place) {
        String prompt = """
            전시 정보를 enum으로 분류해.
            Enums:
            - ExhibitionCategory: PAINTING, SCULPTURE_INSTALLATION, CRAFT_DESIGN, PHOTO_MEDIA_ART
            - ExhibitionType: PERSON, GROUP
            - ExhibitionMood: SOLO, DATE, TRENDY, FAMILY

            title: %s
            place: %s

            반드시 JSON으로만 답해.
            예시: {"exhibitionCategory":"PHOTO_MEDIA_ART","exhibitionType":"GROUP","exhibitionMood":"FAMILY"}
            """.formatted(title, place);

        try {
            String response = openAiRestClient.post()
                    .uri("/chat/completions")
                    .body(Map.of(
                            "model", "gpt-4o-mini",
                            "messages", List.of(Map.of("role", "user", "content", prompt))
                    ))
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // 안정화: 백틱 제거
            if (content.startsWith("```")) {
                content = content.replaceAll("```json", "")
                        .replaceAll("```", "")
                        .trim();
            }
            return objectMapper.readValue(content, ExhibitionResDTO.ExhibitionEnumResDTO.class);

        } catch (Exception e) {
            log.error("OpenAI 분류 요청 실패 - title: {}, place: {}", title, place, e);
            throw new RuntimeException("OpenAI 분류 실패", e);
        }
    }
}
