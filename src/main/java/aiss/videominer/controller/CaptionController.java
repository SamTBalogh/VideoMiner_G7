package aiss.videominer.controller;

import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.VideoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer")
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;

    @Autowired
    VideoRepository videoRepository;

    // GET http://localhost:8080/videominer/captions
    @GetMapping("/captions")
    public List<Caption> findAll() { return captionRepository.findAll();}

    // GET http://localhost:8080/videominer/captions/{id}
    @GetMapping("/captions/{id}")
    public Caption findById(@PathVariable String id) {
        Optional<Caption> caption = captionRepository.findById(id);
        return caption.get();
    }

    // GET http://localhost:8080/videominer/videos/{videoId}/captions
    @GetMapping("/videos/{videoId}/captions")
    public List<Caption> getAllCaptionsByVideo(@PathVariable("videoId") String videoId) {

        Optional<Video> video = videoRepository.findById(videoId);

        return new ArrayList<>(video.get().getCaptions());
    }

    // POST http://localhost:8080/videominer/videos/{videoId}/captions
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/videos/{videoId}/captions")
    public Caption create(@PathVariable("videoId") String videoId, @Valid @RequestBody Caption captionRequest) {

        Optional<Video> video = videoRepository.findById(videoId);

        video.get().getCaptions().add(captionRequest);
        return captionRepository.save(captionRequest);
    }

    // PUT http://localhost:8080/videominer/captions/{id}
    @PutMapping("/captions/{id}")
    public void update(@Valid @RequestBody Caption updatedCaption, @PathVariable String id) {
        Optional<Caption> captionData = captionRepository.findById(id);
        Caption _caption = captionData.get();
        _caption.setId(updatedCaption.getId());
        _caption.setName(updatedCaption.getName());
        _caption.setLanguage(updatedCaption.getLanguage());
        captionRepository.save(_caption);
    }

    // DELETE http://localhost:8080/videominer/captions/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/captions/{id}")
    public void delete(@PathVariable String id) {
        if(captionRepository.existsById(id)) {
            captionRepository.deleteById(id);
        }
    }
}
