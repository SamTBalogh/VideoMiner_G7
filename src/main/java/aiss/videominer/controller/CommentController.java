package aiss.videominer.controller;

import aiss.videominer.model.Caption;
import aiss.videominer.model.Comment;
import aiss.videominer.repository.CommentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/comments")
public class CommentController {

    @Autowired
    CommentRepository repository;

    // GET http://localhost:8080/videominer/comments
    @GetMapping
    public List<Comment> findAll() { return repository.findAll();}

    // GET http://localhost:8080/videominer/comments/{id}
    @GetMapping("/{id}")
    public Comment findById(@PathVariable String id) {
        Optional<Comment> comment = repository.findById(id);
        return comment.get();
    }

    //POST http://localhost:8080/videominer/comments
    @PostMapping
    public Comment create(@Valid @RequestBody Comment comment) {
        Comment _comment = repository.save(new Comment(comment.getId(), comment.getText(), comment.getCreatedOn(), comment.getAuthor()));
        return _comment;
    }

    // PUT http://localhost:8080/videominer/comments/{id}
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Comment updatedComment, @PathVariable String id) {
        Optional<Comment> commentData = repository.findById(id);
        Comment _comment = commentData.get();
        _comment.setId(updatedComment.getId());
        _comment.setAuthor(updatedComment.getAuthor());
        _comment.setCreatedOn(updatedComment.getCreatedOn());
        repository.save(_comment);
    }

    // DELETE http://localhost:8080/videominer/comments/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        if(repository.existsById(id)) {
            repository.deleteById(id);
        }
    }
}
