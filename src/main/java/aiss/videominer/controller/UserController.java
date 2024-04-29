package aiss.videominer.controller;

import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.exception.UserNotFoundException;
import aiss.videominer.model.User;
import aiss.videominer.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<User> findAll() { return repository.findAll();}

    // GET http://localhost:8080/videominer/users/{id}
    @GetMapping("/{id}")
    public User findById(@PathVariable String id) throws UserNotFoundException {
        Optional<User> user = repository.findById(id);
        if(!user.isPresent()){
            throw new UserNotFoundException();
        }
        return user.get();
    }

    // PUT http://localhost:8080/videominer/users/{id}
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody User updatedUser, @PathVariable String id) throws CaptionNotFoundException {
        Optional<User> captionData = repository.findById(id);
        if (!captionData.isPresent()) {
            throw new CaptionNotFoundException();
        }
        User _caption = captionData.get();
        _caption.setName(updatedUser.getName());
        _caption.setUser_link(updatedUser.getUser_link());
        _caption.setPicture_link(updatedUser.getPicture_link());
        repository.save(_caption);
    }

    // DELETE http://localhost:8080/videominer/users/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        if(repository.existsById(id)) {
            repository.deleteById(id);
        }
    }
}
