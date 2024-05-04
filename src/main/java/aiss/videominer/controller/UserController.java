package aiss.videominer.controller;

import aiss.videominer.exception.UserNotFoundException;
import aiss.videominer.model.User;
import aiss.videominer.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/users")
public class UserController {

    @Autowired
    UserRepository repository;

    // GET http://localhost:8080/videominer/users
    @GetMapping
    public List<User> findAll(@RequestHeader HttpHeaders header) { return repository.findAll();}

    // GET http://localhost:8080/videominer/users/{id}
    @GetMapping("/{id}")
    public User findById(@PathVariable String id, @RequestHeader HttpHeaders header) throws UserNotFoundException {
        Optional<User> user = repository.findById(id);
        if(!user.isPresent()){
            throw new UserNotFoundException();
        }
        return user.get();
    }

    // PUT http://localhost:8080/videominer/users/{id}
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody User updatedUser, @PathVariable String id, @RequestHeader HttpHeaders header) throws UserNotFoundException {
        Optional<User> userData = repository.findById(id);
        if (!userData.isPresent()) {
            throw new UserNotFoundException();
        }
        User _user = userData.get();
        _user.setName(updatedUser.getName());
        _user.setUser_link(updatedUser.getUser_link());
        _user.setPicture_link(updatedUser.getPicture_link());
        repository.save(_user);
    }

    // DELETE http://localhost:8080/videominer/users/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id, @RequestHeader HttpHeaders header) {
        if(repository.existsById(id)) {
            repository.deleteById(id);
        }
    }
}
