package aiss.videominer.controller;


import aiss.videominer.exception.TokenNotValidException;
import aiss.videominer.model.*;
import aiss.videominer.repository.*;
import aiss.videominer.exception.ChannelNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/channels")
public class ChannelController {

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

    // GET http://localhost:8080/videominer/channels
    @GetMapping
    public List<Channel> findAll() { return channelRepository.findAll();}

    // GET http://localhost:8080/videominer/channels/{id}
    @GetMapping("/{id}")
    public Channel findById(@PathVariable String id) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findById(id);
        if (!channel.isPresent()) {
            throw new ChannelNotFoundException();
        }
        return channel.get();
    }

    //POST http://localhost:8080/videominer/channels
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Channel create(@Valid @RequestBody Channel channel, @RequestHeader HttpHeaders header) throws TokenNotValidException {
        String token = header.getFirst("Authorization");
        if(token!= null && tokenRepository.findById(token).isPresent()) {
            Channel _channel = channelRepository.save(channel);
            for (Video v : channel.getVideos()) {
                Video video = videoRepository.save(v);
                _channel.getVideos().add(video);
                captionRepository.saveAll(v.getCaptions());
                for (Comment com : v.getComments()) {
                    Comment comment = commentRepository.save(com);
                    video.getComments().add(commentRepository.save(com));
                    userRepository.save(comment.getAuthor());
                }
            }
            return _channel;
        }
        else {
            throw new TokenNotValidException();
        }
    }

    // PUT http://localhost:8080/videominer/channels/{id}
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Channel updatedChannel, @PathVariable String id) throws ChannelNotFoundException {
        Optional<Channel> channelData = channelRepository.findById(id);
        if (!channelData.isPresent()) {
            throw new ChannelNotFoundException();
        }
        Channel _channel = channelData.get();
        _channel.setName(updatedChannel.getName());
        _channel.setDescription(updatedChannel.getDescription());
        channelRepository.save(_channel);
    }

    // DELETE http://localhost:8080/videominer/channels/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        if(channelRepository.existsById(id)) {
            channelRepository.deleteById(id);
        }
    }
}
