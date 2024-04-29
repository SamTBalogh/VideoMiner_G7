package aiss.videominer.controller;

import aiss.videominer.exception.CommentNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Comment;
import aiss.videominer.model.User;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.UserRepository;
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
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    UserRepository userRepository;

    // GET http://localhost:8080/videominer/comments
    @GetMapping("/comments")
    public List<Comment> findAll() { return commentRepository.findAll();}

    // GET http://localhost:8080/videominer/comments/{id}
    @GetMapping("/comments/{id}")
    public Comment findById(@PathVariable String id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);
        if (!comment.isPresent()) {
            throw new CommentNotFoundException();
        }
        return comment.get();
    }

    // GET http://localhost:8080/videominer/videos/{videoId}/comments
    @GetMapping("/videos/{videoId}/comments")
    public List<Comment> getAllCommentsByVideo(@PathVariable("videoId") String videoId) throws VideoNotFoundException {

        Optional<Video> video = videoRepository.findById(videoId);
        if (!video.isPresent()) {
            throw new VideoNotFoundException();
        }
        return new ArrayList<>(video.get().getComments());
    }

    // POST http://localhost:8080/videominer/videos/{videoId}/comments
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/videos/{videoId}/comments")
    public Comment create(@PathVariable("videoId") String videoId, @Valid @RequestBody Comment commentRequest) throws VideoNotFoundException {

        Optional<Video> video = videoRepository.findById(videoId);
        if (!video.isPresent()) {
            throw new VideoNotFoundException();
        }
        video.get().getComments().add(commentRequest);
        User user = commentRequest.getAuthor();
        userRepository.save(user);
        return commentRepository.save(commentRequest);
    }

    // PUT http://localhost:8080/videominer/comments/{id}
    @PutMapping("/comments/{id}")
    public void update(@Valid @RequestBody Comment updatedComment, @PathVariable String id) throws CommentNotFoundException {
        Optional<Comment> commentData = commentRepository.findById(id);
        if (!commentData.isPresent()) {
            throw new CommentNotFoundException();
        }
        Comment _comment = commentData.get();
        _comment.setId(updatedComment.getId());
        _comment.setCreatedOn(updatedComment.getCreatedOn());
        commentRepository.save(_comment);
    }

    // DELETE http://localhost:8080/videominer/comments/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/comments/{id}")
    public void delete(@PathVariable String id) {
        if(commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        }
    }
}
