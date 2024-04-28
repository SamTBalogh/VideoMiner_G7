package aiss.videominer.controller;


import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.repository.ChannelRepository;
import aiss.videominer.repository.VideoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/videos")
public class VideoController {

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    VideoRepository videoRepository;

    // GET http://localhost:8080/videominer/videos
    @GetMapping
    public List<Video> findAll() { return videoRepository.findAll();}

    // GET http://localhost:8080/videominer/videos/{id}
    @GetMapping("/{id}")
    public Video findById(@PathVariable String id) {
        Optional<Video> video = videoRepository.findById(id);
        return video.get();
    }

    //POST http://localhost:8080/videominer/channels/{channelId}/videos
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Video create(@PathVariable("channelId") String channelId, @Valid @RequestBody Video video) {
        Video _video = videoRepository.save(new Video(video.getId(), video.getName(), video.getDescription(), video.getReleaseTime()));
        return _video;
    }

    // PUT http://localhost:8080/videominer/videos/{id}
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Video updatedVideo, @PathVariable String id) {
        Optional<Video> videoData = videoRepository.findById(id);
        Video _video = videoData.get();
        _video.setId(updatedVideo.getId());
        _video.setName(updatedVideo.getName());
        _video.setDescription(updatedVideo.getDescription());
        _video.setReleaseTime(updatedVideo.getReleaseTime());
        videoRepository.save(_video);
    }

    // DELETE http://localhost:8080/videominer/videos/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        if(videoRepository.existsById(id)) {
            videoRepository.deleteById(id);
        }
    }
}
