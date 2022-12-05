package com.example.springbootmongo.service;

import com.example.springbootmongo.collection.User;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    String save(User user);

    List<User> getUserWithStartWith(String name);

    void delete(String id);

    List<User> getByUserAge(Integer minAge, Integer maxAge);

    Page<User> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable);

    List<Document> getOldestUserByCity();

    List<Document> getPopulationByCity();

    List<User> findAll();
}
