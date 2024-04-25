package aiss.videominer.controller;


import aiss.videominer.model.Video;
import aiss.videominer.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    VideoRepository repository;

    // GET http://localhost:8080/api/videos
    @GetMapping
    public List<Video> findAll() { return repository.findAll();}
}
