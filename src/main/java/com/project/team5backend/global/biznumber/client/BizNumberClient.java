package com.project.team5backend.global.biznumber.client;

import com.project.team5backend.global.biznumber.dto.response.BizNumberResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BizNumberClient {

    private final RestClient bizNumberRestClient;

    @Value("${biz.api-key}") String serviceKey;

    public BizNumberResDTO.BizInfo verifyBizNumber(String bizNumber) {
        return bizNumberRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/status")
                        .queryParam("serviceKey", serviceKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("b_no", List.of(bizNumber)))
                .retrieve()
                .body(BizNumberResDTO.BizInfo.class);
    }

}
