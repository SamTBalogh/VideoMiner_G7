package aiss.videominer.controller;

import aiss.videominer.model.Caption;
import aiss.videominer.repository.CaptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/captions")
public class CaptionController {

    @Autowired
    CaptionRepository repository;

    // GET http://localhost:8080/api/captions
    @GetMapping
    public List<Caption> findAll() { return repository.findAll();}
}
