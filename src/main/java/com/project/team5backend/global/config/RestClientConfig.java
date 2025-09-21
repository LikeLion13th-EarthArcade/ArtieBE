package com.project.team5backend.global.config;

import com.project.team5backend.global.address.exception.AddressErrorCode;
import com.project.team5backend.global.address.exception.AddressException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
@Slf4j
public class RestClientConfig {

    @Bean(name = "kakaoRestClient")
    public RestClient kakaoRestClient(
            RestClient.Builder builder,
            @Value("${kakao.api.rest-key:}") String restKey
    ) {
        if (restKey == null || restKey.isBlank()) {
            throw new AddressException(AddressErrorCode.MISSING_ADDRESS);
        }
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(java.time.Duration.ofSeconds(2));
        factory.setReadTimeout(java.time.Duration.ofSeconds(3));

        return builder
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(org.springframework.http.HttpHeaders.AUTHORIZATION, "KakaoAK " + restKey)
                .defaultHeader(org.springframework.http.HttpHeaders.ACCEPT, org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(factory)
                .build();
    }

    @Bean(name = "bizNumberRestClient")
    public RestClient bizNumberRestClient(
            RestClient.Builder builder,
            @Value("${biz.base-url}") String baseUrl
    ) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        return builder
                .uriBuilderFactory(factory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    @Bean(name = "openAiRestClient")
    public RestClient openAiRestClient(
            RestClient.Builder builder,
            @Value("${openai.api.key}") String openAiApiKey
    ) {
        return builder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "culturePortalRestClient")
    public RestClient culturePortalRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://apis.data.go.kr/B553457/cultureinfo")
                .build();
    }
}