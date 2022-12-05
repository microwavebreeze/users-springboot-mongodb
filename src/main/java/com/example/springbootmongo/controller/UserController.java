package com.example.springbootmongo.controller;

import com.example.springbootmongo.collection.User;
import com.example.springbootmongo.service.UserService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public String save(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping
    public List<User> getUserStartWith(@RequestParam("name") String name) {
        return userService.getUserWithStartWith(name);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/populationByCity")
    public List<Document> getPopulationByCity() {
        return userService.getPopulationByCity();
    }

    @GetMapping("/age")
    public List<User> getByUserAge(@RequestParam Integer minAge, @RequestParam Integer maxAge) {
        return userService.getByUserAge(minAge, maxAge);
    }

    @GetMapping("/search")
    public Page<User> searchUser(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        Pageable pageable
                = PageRequest.of(page, size);
        return userService.search(name, minAge, maxAge, city, pageable);
    }

    // by city
    @GetMapping("/oldestuser")
    public List<Document> getOldestUser() {
        return userService.getOldestUserByCity();
    }
}
