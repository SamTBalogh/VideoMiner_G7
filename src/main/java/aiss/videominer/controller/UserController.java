package aiss.videominer.controller;

import aiss.videominer.exception.*;
import aiss.videominer.model.Comment;
import aiss.videominer.model.User;
import aiss.videominer.model.Video;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/videominer/v1")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    VideoRepository videoRepository;

    // GET http://localhost:8080/videominer/v1/users
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users")
    public List<User> findAll(@RequestHeader HttpHeaders header,
                              @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String id, @RequestParam(required = false) String name,
                              @RequestParam(required = false) String userLink, @RequestParam(required = false) String pictureLink,
                              @RequestParam(required = false) String order) throws TokenRequiredException, TokenNotValidException, BadRequestParameterField {
        String token = header.getFirst("Authorization");
        if (token == null) {
            throw new TokenRequiredException();
        } else if (tokenRepository.existsById(token)) {
            Page<User> pageChannels;
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
            if (userLink != null) count++;
            if (pictureLink != null) count++;

            if (count > 1) {
                throw new BadRequestParameterField();
            }

            if (id != null) {
                pageChannels = userRepository.findById(Long.valueOf(id), paging);
            } else if (name != null) {
                pageChannels = userRepository.findByName(name, paging);
            } else if (userLink != null) {
                pageChannels = userRepository.findByUserLinkContaining(userLink, paging);
            } else if (pictureLink != null) {
                pageChannels = userRepository.findByPictureLinkContaining(pictureLink, paging);
            } else {
                pageChannels = userRepository.findAll(paging);
            }

            return pageChannels.getContent();
        } else {
            throw new TokenNotValidException();
        }
    }

    // GET http://localhost:8080/videominer/v1/users/{id}
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{id}")
    public User findById(@PathVariable String id, @RequestHeader HttpHeaders header) throws UserNotFoundException, TokenNotValidException, TokenRequiredException {
        String token = header.getFirst("Authorization");
        if (token == null) {
            throw new TokenRequiredException();
        } else if (tokenRepository.existsById(token)) {
            Optional<User> user = userRepository.findById(Long.valueOf(id));
            if(!user.isPresent()){
                throw new UserNotFoundException();
            }
            return user.get();
        } else {
            throw new TokenNotValidException();
        }
    }

    //GET http://localhost:8080/videominer/v1/videos/{videoId}/captions
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/videos/{videoId}/users")
    public List<User> getAllCaptionsByVideo(@PathVariable("videoId") String videoId, @RequestHeader HttpHeaders header) throws VideoNotFoundException, TokenRequiredException, TokenNotValidException {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Optional<Video> video = videoRepository.findById(videoId);
            if (!video.isPresent()) {
                throw new VideoNotFoundException();
            }
            return video.get().getComments().stream().map(Comment::getAuthor).collect(Collectors.toList());
        } else {
            throw new TokenNotValidException();
        }
    }

    // PUT http://localhost:8080/videominer/v1/users/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/users/{id}")
    public void update(@Valid @RequestBody User updatedUser, @PathVariable String id, @RequestHeader HttpHeaders header) throws UserNotFoundException, TokenNotValidException, TokenRequiredException {
        String token = header.getFirst("Authorization");
        if (token == null) {
            throw new TokenRequiredException();
        } else if (tokenRepository.existsById(token)) {
            Optional<User> userData = userRepository.findById(Long.valueOf(id));
            if (!userData.isPresent()) {
                throw new UserNotFoundException();
            }
            User _user = userData.get();
            _user.setName(updatedUser.getName());
            _user.setUser_link(updatedUser.getUser_link());
            _user.setPicture_link(updatedUser.getPicture_link());
            userRepository.save(_user);
        } else {
            throw new TokenNotValidException();
        }
    }

}
