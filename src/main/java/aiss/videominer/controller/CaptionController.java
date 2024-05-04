package aiss.videominer.controller;

import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.exception.TokenNotValidException;
import aiss.videominer.exception.TokenRequiredException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.TokenRepository;
import aiss.videominer.repository.VideoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer")
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    TokenRepository tokenRepository;

    // GET http://localhost:8080/videominer/captions
    @GetMapping("/captions")
    public List<Caption> findAll(@RequestHeader HttpHeaders header) throws TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            return captionRepository.findAll();
        } else {
            throw new TokenNotValidException();
        }
    }

    // GET http://localhost:8080/videominer/captions/{id}
    @GetMapping("/captions/{id}")
    public Caption findById(@PathVariable String id, @RequestHeader HttpHeaders header) throws CaptionNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Caption> caption = captionRepository.findById(id);
            if(!caption.isPresent()){
                throw new CaptionNotFoundException();
            }
            return caption.get();
        } else {
            throw new TokenNotValidException();
        }
    }

    //GET http://localhost:8080/videominer/videos/{videoId}/captions
    @GetMapping("/videos/{videoId}/captions")
    public List<Caption> getAllCaptionsByVideo(@PathVariable("videoId") String videoId, @RequestHeader HttpHeaders header) throws VideoNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Video> video = videoRepository.findById(videoId);
            if (!video.isPresent()) {
                throw new VideoNotFoundException();
            }
            return video.get().getCaptions();
        } else {
            throw new TokenNotValidException();
        }
    }

    //POST http://localhost:8080/videominer/videos/{videoId}/captions
    @PostMapping("/videos/{videoId}/captions")
    public List<Caption> create(@PathVariable("videoId") String videoId, @Valid @RequestBody Caption caption, @RequestHeader HttpHeaders header) throws VideoNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Video> video = videoRepository.findById(videoId);
            if (!video.isPresent()) {
                throw new VideoNotFoundException();
            }
            video.get().getCaptions().add(caption);
            videoRepository.save(video.get());
            captionRepository.save(new Caption(caption.getId(), caption.getLanguage(), caption.getName()));
            return video.get().getCaptions();
        } else {
            throw new TokenNotValidException();
        }
    }

    // PUT http://localhost:8080/videominer/captions/{id}
    @PutMapping("/captions/{id}")
    public void update(@Valid @RequestBody Caption updatedCaption, @PathVariable String id, @RequestHeader HttpHeaders header) throws CaptionNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Caption> captionData = captionRepository.findById(id);
            if(!captionData.isPresent()){
                throw new CaptionNotFoundException();
            }
            Caption _caption = captionData.get();
            _caption.setName(updatedCaption.getName());
            _caption.setLanguage(updatedCaption.getLanguage());
            captionRepository.save(_caption);
        } else {
            throw new TokenNotValidException();
        }
    }

    // DELETE http://localhost:8080/videominer/captions/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/captions/{id}")
    public void delete(@PathVariable String id, @RequestHeader HttpHeaders header) throws TokenRequiredException, TokenNotValidException, CaptionNotFoundException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            if(captionRepository.existsById(id)) {
                captionRepository.deleteById(id);
            }
            else {
                throw new CaptionNotFoundException();
            }
        } else {
            throw new TokenNotValidException();
        }
    }
}
