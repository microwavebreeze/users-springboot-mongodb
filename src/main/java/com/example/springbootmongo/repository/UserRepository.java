package com.example.springbootmongo.repository;

import com.example.springbootmongo.collection.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    List<User> findByFirstNameStartsWithIgnoreCase(String name);

//    List<Person> findByAgeBetween(Integer min, Integer max);

    @Query(value = "{'age' : {$gt : ?0, $lt : ?1}}",
    fields ="{addresses: 0}")
    List<User> findUserByAgeBetween(Integer min, Integer max);
}
