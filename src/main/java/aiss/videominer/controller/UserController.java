package aiss.videominer.controller;

import aiss.videominer.exception.TokenNotValidException;
import aiss.videominer.exception.TokenRequiredException;
import aiss.videominer.exception.UserNotFoundException;
import aiss.videominer.model.User;
import aiss.videominer.repository.TokenRepository;
import aiss.videominer.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/v1")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    // GET http://localhost:8080/videominer/v1/users
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users")
    public List<User> findAll(@RequestHeader HttpHeaders header) throws TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token == null) {
            throw new TokenRequiredException();
        } else if (tokenRepository.existsById(token)) {
            return userRepository.findAll();
        } else {
            throw new TokenNotValidException();
        }
    }

    // GET http://localhost:8080/videominer/v1/users/{id}
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{id}")
    public User findById(@PathVariable String id, @RequestHeader HttpHeaders header) throws UserNotFoundException, TokenNotValidException, TokenRequiredException {
        String token = header.getFirst("Authorization");
        if (token == null) {
            throw new TokenRequiredException();
        } else if (tokenRepository.existsById(token)) {
            Optional<User> user = userRepository.findById(Long.valueOf(id));
            if(!user.isPresent()){
                throw new UserNotFoundException();
            }
            return user.get();
        } else {
            throw new TokenNotValidException();
        }
    }

    // PUT http://localhost:8080/videominer/v1/users/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/users/{id}")
    public void update(@Valid @RequestBody User updatedUser, @PathVariable String id, @RequestHeader HttpHeaders header) throws UserNotFoundException, TokenNotValidException, TokenRequiredException {
        String token = header.getFirst("Authorization");
        if (token == null) {
            throw new TokenRequiredException();
        } else if (tokenRepository.existsById(token)) {
            Optional<User> userData = userRepository.findById(Long.valueOf(id));
            if (!userData.isPresent()) {
                throw new UserNotFoundException();
            }
            User _user = userData.get();
            _user.setName(updatedUser.getName());
            _user.setUser_link(updatedUser.getUser_link());
            _user.setPicture_link(updatedUser.getPicture_link());
            userRepository.save(_user);
        } else {
            throw new TokenNotValidException();
        }
    }

}
