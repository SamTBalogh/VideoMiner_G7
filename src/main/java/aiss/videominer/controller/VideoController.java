package aiss.videominer.controller;

import aiss.videominer.exception.*;
import aiss.videominer.model.Channel;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import aiss.videominer.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/v1")
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
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    // GET http://localhost:8080/videominer/v1/videos
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/videos")
    public List<Video> findAll(@RequestHeader HttpHeaders header,
                               @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String id, @RequestParam(required = false) String name,
                               @RequestParam(required = false) String description, @RequestParam(required = false) String releaseTime,
                               @RequestParam(required = false) String order) throws TokenRequiredException, TokenNotValidException, BadRequestParameterField {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Page<Video> pageChannels;
            Pageable paging;
            if(order!=null){
                if(order.startsWith("-")){
                    paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
                }
                else{
                    paging = PageRequest.of(page, size, Sort.by(order).ascending());
                }
            }else{
                paging = PageRequest.of(page, size);
            }
            int count = 0;
            if (id != null) count++;
            if (name != null) count++;
            if (description != null) count++;
            if (releaseTime != null) count++;

            if (count > 1) {
                throw new BadRequestParameterField();
            }

            if (id != null) {
                pageChannels = videoRepository.findById(id, paging);
            } else if (name != null) {
                pageChannels = videoRepository.findByName(name, paging);
            } else if (description != null) {
                pageChannels = videoRepository.findByDescriptionContaining(description, paging);
            } else if (releaseTime != null) {
                pageChannels = videoRepository.findByReleaseTimeContaining(releaseTime, paging);
            } else {
                pageChannels = videoRepository.findAll(paging);
            }
            return pageChannels.getContent();
        } else {
            throw new TokenNotValidException();
        }
}

    // GET http://localhost:8080/videominer/v1/videos/{id}
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/videos/{id}")
    public Video findById(@PathVariable String id, @RequestHeader HttpHeaders header) throws VideoNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Video> video = videoRepository.findById(id);
            if(!video.isPresent()){
                throw new VideoNotFoundException();
            }
            return video.get();
        } else {
            throw new TokenNotValidException();
        }
    }

    // GET http://localhost:8080/videominer/v1/channels/{channelId}/videos
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/channels/{channelId}/videos")
    public List<Video> getAllVideosByChannel(@PathVariable("channelId") String channelId, @RequestHeader HttpHeaders header) throws ChannelNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Channel> channel = channelRepository.findById(channelId);
            if (!channel.isPresent()) {
                throw new ChannelNotFoundException();
            }
            return new ArrayList<>(channel.get().getVideos());
        } else {
            throw new TokenNotValidException();
        }
    }

    // POST http://localhost:8080/videominer/v1/channels/{channelId}/videos
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/channels/{channelId}/videos")
    public Video create(@PathVariable("channelId") String channelId, @Valid @RequestBody Video videoRequest, @RequestHeader HttpHeaders header) throws ChannelNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Channel> channel = channelRepository.findById(channelId);
            if (!channel.isPresent()) {
                throw new ChannelNotFoundException();
            }
            channel.get().getVideos().add(videoRequest);
            for (Comment comment : videoRequest.getComments()) {
                userRepository.save(comment.getAuthor());
                commentRepository.save(comment);
            }
            captionRepository.saveAll(videoRequest.getCaptions());
            Video video = videoRepository.save(videoRequest);
            channelRepository.save(channel.get());
            return video;
        } else {
            throw new TokenNotValidException();
        }
    }

    // PUT http://localhost:8080/videominer/v1/videos/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/videos/{id}")
    public void update(@Valid @RequestBody Video updatedVideo, @PathVariable String id, @RequestHeader HttpHeaders header) throws VideoNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Video> videoData = videoRepository.findById(id);
            if (!videoData.isPresent()) {
                throw new VideoNotFoundException();
            }
            Video _video = videoData.get();
            _video.setName(updatedVideo.getName());
            _video.setDescription(updatedVideo.getDescription());
            _video.setReleaseTime(updatedVideo.getReleaseTime());
            videoRepository.save(_video);
        } else {
            throw new TokenNotValidException();
        }
    }

    // DELETE http://localhost:8080/videominer/v1/videos/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/videos/{id}")
    public void delete(@PathVariable String id, @RequestHeader HttpHeaders header) throws VideoNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            if(videoRepository.existsById(id)) {
                videoRepository.deleteById(id);
            }
            else {
                throw new VideoNotFoundException();
            }
        } else {
            throw new TokenNotValidException();
        }
    }
}
