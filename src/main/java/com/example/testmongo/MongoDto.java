package com.example.testmongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;

@Data
@Document(collection = "mongoDto")
@NoArgsConstructor
@AllArgsConstructor
public class MongoDto {
    @Indexed(unique = true)
    String productName;
    HashMap<String, List<String>> mainTreaty;
    HashMap<String, HashMap<String, List<String>>> treaty;
    List<String> history;
}
