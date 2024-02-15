package com.example.testmongo;

import com.example.testmongo.service.CrawlingService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceFinder {
    private final List<CrawlingService> crawlingServiceList;

    public ServiceFinder(List<CrawlingService> crawlingServiceList) {
        this.crawlingServiceList = crawlingServiceList;
    }

    public CrawlingService findCrawler(String company) {
        return crawlingServiceList.stream()
                .filter(crawlingService -> crawlingService.getCompany().equals(company))
                .findAny()
                .orElseThrow(RuntimeException::new);
    }
}
