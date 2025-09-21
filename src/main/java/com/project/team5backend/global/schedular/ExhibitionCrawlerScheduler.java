package com.project.team5backend.global.schedular;

import com.project.team5backend.domain.exhibition.service.schedular.ExhibitionCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExhibitionCrawlerScheduler {

    private final ExhibitionCrawlerService exhibitionCrawlerService;

    // 매일 새벽 1시에 실행
    @Scheduled(cron = "0 0 1 * * *")
    public void crawlDaily() {
        exhibitionCrawlerService.crawlFromPortal();
    }
}
