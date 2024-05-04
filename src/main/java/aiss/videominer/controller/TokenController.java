package aiss.videominer.controller;

import aiss.videominer.model.Token;
import aiss.videominer.repository.TokenRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videominer/tokens")
public class TokenController {

    @Autowired
    TokenRepository repository;

    // POST http://localhost:8080/videominer/tokens
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public List<Token> addToken(@Valid @RequestBody List<Token> tokens) {

        repository.saveAll(tokens);

        return tokens;
    }
}
