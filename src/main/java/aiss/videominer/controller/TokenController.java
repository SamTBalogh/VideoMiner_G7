package aiss.videominer.controller;

import aiss.videominer.model.Token;
import aiss.videominer.repository.TokenRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/videominer/v1")
public class TokenController {

    @Autowired
    TokenRepository repository;

    // POST http://localhost:8080/videominer/api/v1/tokens
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/token")
    public Token addToken(@Valid @RequestBody Token tokens) {

        repository.save(tokens);

        return tokens;
    }
}
