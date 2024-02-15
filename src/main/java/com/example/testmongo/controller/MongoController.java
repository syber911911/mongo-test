package com.example.testmongo.controller;

import com.example.testmongo.MongoDto;
import com.example.testmongo.ServiceFinder;
import com.example.testmongo.service.MongoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MongoController {
    private final MongoService mongoService;
    private final ServiceFinder serviceFinder;

    public MongoController(MongoService mongoService, ServiceFinder serviceFinder) {
        this.mongoService = mongoService;
        this.serviceFinder = serviceFinder;
    }

    @PostMapping("/crawl")
    public String crawlAndSave(@RequestParam("company") String company) throws InterruptedException {
        return mongoService.save(serviceFinder.findCrawler(company).scrap());
    }

    @GetMapping("/")
    public MongoDto getData() {
        return mongoService.find("무배당 AIG 참 기특한 암보험2309(순수보장형(1종))");
    }
}
