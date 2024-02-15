package com.example.testmongo.repository;

import com.example.testmongo.MongoDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface InsureRepository extends MongoRepository<MongoDto, String> {
    MongoDto findByProductName(String productName);
}
