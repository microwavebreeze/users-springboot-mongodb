package com.example.springbootmongo.service;

import com.example.springbootmongo.collection.User;
import com.example.springbootmongo.repository.UserRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(User user) {
        return userRepository.save(user).getUserId();
    }

    @Override
    public List<User> getUserWithStartWith(String name) {
        return userRepository.findByFirstNameStartsWithIgnoreCase(name);
    }

    @Override
    public void delete(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getByUserAge(Integer minAge, Integer maxAge) {
        return userRepository.findUserByAgeBetween(minAge, maxAge);
    }

    @Override
    public Page<User> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable) {

        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            criteria.add(Criteria.where("firstName").regex(name, "i"));
        }

        if (minAge != null && maxAge != null) {
            criteria.add(Criteria.where("age").gte(minAge).lte(maxAge));
        }

        if (city != null && !city.isEmpty()) {
            criteria.add(Criteria.where("addresses.city").is(city));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria()
                    .andOperator(criteria.toArray(new Criteria[0])));
        }

        Page<User> people = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, User.class
                ), pageable, () -> mongoTemplate.count(query.skip(0).limit(0), User.class));
        return people;
    }

    @Override
    public List<Document> getOldestUserByCity() {
        UnwindOperation unwindOperation
                = Aggregation.unwind("addresses");
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC, "age");
        GroupOperation groupOperation
                = Aggregation.group("addresses.city").first(Aggregation.ROOT).as("oldestUser");
        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation, sortOperation, groupOperation);

        List<Document> user
                = mongoTemplate.aggregate(aggregation, User.class, Document.class).getMappedResults();

        return user;
    }

    @Override
    public List<Document> getPopulationByCity() {

        UnwindOperation unwindOperation
                = Aggregation.unwind("addresses");

        GroupOperation groupOperation
                = Aggregation.group("addresses.city")
                .count().as("popCount");
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC, "popCount");

        ProjectionOperation projectionOperation
                = Aggregation.project()
                .andExpression("_id").as("city")
                .andExpression("popCount").as("count")
                .andExclude("_id");

        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation, groupOperation, sortOperation, projectionOperation);
        List<Document> documents
                = mongoTemplate.aggregate(aggregation,
                User.class,
                Document.class).getMappedResults();
        return documents;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
