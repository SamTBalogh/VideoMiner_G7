package aiss.videominer.controller;

import aiss.videominer.model.User;
import aiss.videominer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository repository;

    // GET http://localhost:8080/api/users
    @GetMapping
    public List<User> findAll() { return repository.findAll();}
}
