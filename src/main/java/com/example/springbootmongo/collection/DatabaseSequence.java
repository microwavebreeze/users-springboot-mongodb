package com.example.springbootmongo.collection;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customSequences")
public class DatabaseSequence {

    @Id
    private String id;
    private int seq;
}