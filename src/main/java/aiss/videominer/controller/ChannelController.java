package aiss.videominer.controller;


import aiss.videominer.model.Channel;
import aiss.videominer.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    @Autowired
    ChannelRepository repository;

    // GET http://localhost:8080/api/channels
    @GetMapping
    public List<Channel> findAll() { return repository.findAll();}
}
