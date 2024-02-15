package com.example.testmongo.service;

import com.example.testmongo.MongoDto;

public interface CrawlingService {
    MongoDto scrap() throws InterruptedException;
    String getCompany();
}
