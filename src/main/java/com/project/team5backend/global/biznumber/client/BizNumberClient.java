package com.project.team5backend.global.biznumber.client;

import com.project.team5backend.global.biznumber.dto.response.BizNumberResDTO;
import com.project.team5backend.global.biznumber.exception.BizNumberErrorCode;
import com.project.team5backend.global.biznumber.exception.BizNumberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BizNumberClient {

    private final RestClient bizNumberRestClient;

    @Value("${biz.api-key}") String serviceKey;

    public BizNumberResDTO.BizInfo verifyBizNumber(String bizNumber) {
        try {
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
        } catch (ResourceAccessException e) {
            // 타임아웃/네트워크 장애
            throw new BizNumberException(BizNumberErrorCode.EXTERNAL_API_TIMEOUT);
        } catch (RestClientException e) {
            // 그 외 RestClient 레벨 문제
            throw new BizNumberException(BizNumberErrorCode.EXTERNAL_API_ERROR);
        } catch (Exception e) {
            // JSON 파싱 문제 등
            throw new BizNumberException(BizNumberErrorCode.PARSE_ERROR);
        }

    }

}
