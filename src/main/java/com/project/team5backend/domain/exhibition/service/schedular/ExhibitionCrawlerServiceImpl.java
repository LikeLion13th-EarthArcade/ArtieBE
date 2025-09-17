package com.project.team5backend.domain.exhibition.service.schedular;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.project.team5backend.domain.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import com.project.team5backend.global.address.converter.AddressConverter;
import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.global.address.service.AddressService;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.util.S3UrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ExhibitionCrawlerServiceImpl implements ExhibitionCrawlerService {

    private final RestClient culturePortalRestClient;

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;
    private final AddressService addressService;
    private final OpenAiEnumService openAiEnumService;
    private final S3UrlUtils s3UrlUtils;

    @Value("${culture.portal.service-key}")
    private String serviceKey;

    private static final int NUM_OF_ROWS = 10;

    @Override
    public void crawlFromPortal() {
        int pageNo = 1;
        String sido = "서울";
        String serviceTp = "A"; // A : 공연/전시

        String xmlResponse = culturePortalRestClient.get()
                .uri(u -> u.path("/area2")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("PageNo", pageNo)
                        .queryParam("numOfRows", NUM_OF_ROWS)
                        .queryParam("sido", sido)
                        .queryParam("serviceTp", serviceTp)
                        .build())
                .retrieve()
                .body(String.class);
        List<ExhibitionResDTO.ExhibitionCrawlResDto> items = parseXml(xmlResponse);

        List<ExhibitionResDTO.ExhibitionCrawlResDto> newItems = items.stream()
                .filter(exhibitionCrawlResDto -> "전시".equals(exhibitionCrawlResDto.realmName()))
                .filter(dto -> !exhibitionRepository.existsByPortalExhibitionId(Long.valueOf(dto.portalExhibitionId())))
                .toList();
        if (newItems.isEmpty()) {
            log.info("오늘은 신규 전시 없음");
        } else {
            newItems.forEach(this::processExhibition);
        }
    }

    private void processExhibition(ExhibitionResDTO.ExhibitionCrawlResDto exhibitionCrawlResDto) {
        try {
            // 1. 주소 변환
            AddressResDTO.AddressCreateResDTO addressDto = addressService.resolveByCoordinates(exhibitionCrawlResDto.gpsX(), exhibitionCrawlResDto.gpsY());
            Address address = AddressConverter.toAddressCrawl(addressDto, exhibitionCrawlResDto.place());
            // 2. OpenAI 분류
            ExhibitionResDTO.ExhibitionEnumResDTO enums = openAiEnumService.classify(exhibitionCrawlResDto.title(), exhibitionCrawlResDto.place());
            // 3. DB 저장
            String thumbnail = s3UrlUtils.toFileKey(exhibitionCrawlResDto.thumbnail());
            Exhibition exhibition = ExhibitionConverter.toExhibitionCrawl(exhibitionCrawlResDto, thumbnail, address, enums);
            exhibitionRepository.save(exhibition);
            exhibitionImageRepository.save(ImageConverter.toExhibitionImage(exhibition, thumbnail));

            log.info("전시 저장 성공: {}", exhibitionCrawlResDto.title());
        } catch (Exception e) {
            // 예외 발생 시, 현재 전시만 스킵하고 다음 전시로 넘어감
            log.warn("전시 처리 실패 (스킵됨) - title: {}, reason: {}",
                    exhibitionCrawlResDto.title(), e.getMessage());
        }
    }

    private List<ExhibitionResDTO.ExhibitionCrawlResDto> parseXml(String xml) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode root = xmlMapper.readTree(xml);

            JsonNode itemsNode = root.path("body").path("items").path("item");
            List<ExhibitionResDTO.ExhibitionCrawlResDto> items = new ArrayList<>();

            if (itemsNode.isArray()) {
                for (JsonNode node : itemsNode) {
                    ExhibitionResDTO.ExhibitionCrawlResDto dto = xmlMapper.treeToValue(node, ExhibitionResDTO.ExhibitionCrawlResDto.class);
                    items.add(dto);
                }
            } else if (!itemsNode.isMissingNode()) {
                ExhibitionResDTO.ExhibitionCrawlResDto dto = xmlMapper.treeToValue(itemsNode, ExhibitionResDTO.ExhibitionCrawlResDto.class);
                items.add(dto);
            }

            return items;
        } catch (Exception e) {
            log.error("XML 파싱 실패", e);
            return Collections.emptyList();
        }
    }
}