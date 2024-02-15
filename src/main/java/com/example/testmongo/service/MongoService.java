package com.example.testmongo.service;

import com.example.testmongo.repository.InsureRepository;
import com.example.testmongo.MongoDto;
import org.springframework.stereotype.Service;

@Service
public class MongoService {
    private final InsureRepository insureRepository;

    public MongoService(InsureRepository insureRepository) {
        this.insureRepository = insureRepository;
    }

    public String save(MongoDto mongoDto) {
        try {
            insureRepository.save(mongoDto);
            return "success";
        } catch (Exception e) {
            return "Fail";
        }
    }

    public MongoDto find(String productName) {
        return insureRepository.findByProductName(productName);
    }
}
