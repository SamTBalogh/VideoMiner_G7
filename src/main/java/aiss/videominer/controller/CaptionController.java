package aiss.videominer.controller;

import aiss.videominer.model.Caption;
import aiss.videominer.model.Channel;
import aiss.videominer.repository.CaptionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/captions")
public class CaptionController {

    @Autowired
    CaptionRepository repository;

    // GET http://localhost:8080/videominer/captions
    @GetMapping
    public List<Caption> findAll() { return repository.findAll();}

    // GET http://localhost:8080/videominer/captions/{id}
    @GetMapping("/{id}")
    public Caption findById(@PathVariable String id) {
        Optional<Caption> caption = repository.findById(id);
        return caption.get();
    }

    //POST http://localhost:8080/videominer/captions
    @PostMapping
    public Caption create(@Valid @RequestBody Caption caption) {
        Caption _caption = repository.save(new Caption(caption.getId(), caption.getLanguage(), caption.getName()));
        return _caption;
    }

    // PUT http://localhost:8080/videominer/captions/{id}
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Caption updatedCaption, @PathVariable String id) {
        Optional<Caption> captionData = repository.findById(id);
        Caption _caption = captionData.get();
        _caption.setId(updatedCaption.getId());
        _caption.setName(updatedCaption.getName());
        _caption.setLanguage(updatedCaption.getLanguage());
        repository.save(_caption);
    }

    // DELETE http://localhost:8080/videominer/captions/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        if(repository.existsById(id)) {
            repository.deleteById(id);
        }
    }
}
