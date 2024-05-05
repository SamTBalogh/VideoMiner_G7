package aiss.videominer.controller;

import aiss.videominer.exception.*;
import aiss.videominer.model.Comment;
import aiss.videominer.model.User;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.TokenRepository;
import aiss.videominer.repository.UserRepository;
import aiss.videominer.repository.VideoRepository;
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
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    // GET http://localhost:8080/videominer/v1/comments
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/comments")
    public List<Comment> findAll(@RequestHeader HttpHeaders header,
                                 @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String id, @RequestParam(required = false) String text,
                                 @RequestParam(required = false) String createdOn,@RequestParam(required = false) String order) throws TokenRequiredException, TokenNotValidException, BadRequestParameterField {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Page<Comment> pageChannels;
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
            if (text != null) count++;
            if (createdOn != null) count++;

            if (count > 1) {
                throw new BadRequestParameterField();
            }

            if (id != null) {
                pageChannels = commentRepository.findById(id, paging);
            } else if (text != null) {
                pageChannels = commentRepository.findByTextContaining(text, paging);
            } else if (createdOn != null) {
                pageChannels = commentRepository.findByCreatedOnContaining(createdOn, paging);
            } else {
                pageChannels = commentRepository.findAll(paging);
            }
            return pageChannels.getContent();
        } else {
            throw new TokenNotValidException();
        }
    }

    // GET http://localhost:8080/videominer/v1/comments/{id}
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/comments/{id}")
    public Comment findById(@PathVariable String id, @RequestHeader HttpHeaders header) throws CommentNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Comment> comment = commentRepository.findById(id);
            if (!comment.isPresent()) {
                throw new CommentNotFoundException();
            }
            return comment.get();
        } else {
            throw new TokenNotValidException();
        }
    }

    // GET http://localhost:8080/videominer/v1/videos/{videoId}/comments
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/videos/{videoId}/comments")
    public List<Comment> getAllCommentsByVideo(@PathVariable("videoId") String videoId, @RequestHeader HttpHeaders header) throws VideoNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Video> video = videoRepository.findById(videoId);
            if (!video.isPresent()) {
                throw new VideoNotFoundException();
            }
            return new ArrayList<>(video.get().getComments());
        } else {
            throw new TokenNotValidException();
        }
    }

    // POST http://localhost:8080/videominer/v1/videos/{videoId}/comments
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/videos/{videoId}/comments")
    public Comment create(@PathVariable("videoId") String videoId, @Valid @RequestBody Comment commentRequest, @RequestHeader HttpHeaders header) throws VideoNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Video> video = videoRepository.findById(videoId);
            if (!video.isPresent()) {
                throw new VideoNotFoundException();
            }
            video.get().getComments().add(commentRequest);
            User user = commentRequest.getAuthor();
            userRepository.save(user);
            Comment comment = commentRepository.save(commentRequest);
            videoRepository.save(video.get());
            return comment;
        } else {
            throw new TokenNotValidException();
        }
    }

    // PUT http://localhost:8080/videominer/v1/comments/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/comments/{id}")
    public void update(@Valid @RequestBody Comment updatedComment, @PathVariable String id, @RequestHeader HttpHeaders header) throws CommentNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Comment> commentData = commentRepository.findById(id);
            if (!commentData.isPresent()) {
                throw new CommentNotFoundException();
            }
            Comment _comment = commentData.get();
            _comment.setId(updatedComment.getId());
            _comment.setText(updatedComment.getText());
            _comment.setCreatedOn(updatedComment.getCreatedOn());
            commentRepository.save(_comment);
        } else {
            throw new TokenNotValidException();
        }
    }

    // DELETE http://localhost:8080/videominerd/v1/comments/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/comments/{id}")
    public void delete(@PathVariable String id, @RequestHeader HttpHeaders header) throws CommentNotFoundException, TokenNotValidException, TokenRequiredException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Comment> comment = commentRepository.findById(id);
            if(!comment.isPresent()) {
                throw new CommentNotFoundException();
            }
            else {
                String userId = String.valueOf(comment.get().getAuthor().getId());
                commentRepository.deleteById(id);
                userRepository.deleteById(userId);
        }
        } else {
            throw new TokenNotValidException();
        }
    }
}
