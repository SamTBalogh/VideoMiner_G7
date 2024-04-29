package aiss.videominer.controller;


import aiss.videominer.model.Channel;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import aiss.videominer.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer")
public class VideoController {

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    CaptionRepository captionRepository;

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    // GET http://localhost:8080/videominer/videos
    @GetMapping("/videos")
    public List<Video> findAll() { return videoRepository.findAll();}

    // GET http://localhost:8080/videominer/videos/{id}
    @GetMapping("/videos/{id}")
    public Video findById(@PathVariable String id) {
        Optional<Video> video = videoRepository.findById(id);
        return video.get();
    }

    // GET http://localhost:8080/videominer/channels/{channelId}/videos
    @GetMapping("/channels/{channelId}/videos")
    public List<Video> getAllVideosByChannel(@PathVariable("channelId") String channelId) {

        Optional<Channel> channel = channelRepository.findById(channelId);

        return new ArrayList<>(channel.get().getVideos());
    }

    // POST http://localhost:8080/videominer/channels/{channelId}/videos
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/channels/{channelId}/videos")
    public Video create(@PathVariable("channelId") String channelId, @Valid @RequestBody Video videoRequest) {

        Optional<Channel> channel = channelRepository.findById(channelId);

        channel.get().getVideos().add(videoRequest);
        for (Comment comment : videoRequest.getComments()) {
            userRepository.save(comment.getAuthor());
            commentRepository.save(comment);
        }
        captionRepository.saveAll(videoRequest.getCaptions());

        return videoRepository.save(videoRequest);
    }

    // PUT http://localhost:8080/videominer/videos/{id}
    @PutMapping("/videos/{id}")
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
    @DeleteMapping("/videos/{id}")
    public void delete(@PathVariable String id) {
        if(videoRepository.existsById(id)) {
            videoRepository.deleteById(id);
        }
    }
}
