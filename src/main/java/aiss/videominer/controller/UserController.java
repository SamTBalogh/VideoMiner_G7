package aiss.videominer.controller;

import aiss.videominer.exception.BadRequestIdParameter;
import aiss.videominer.exception.BadRequestParameterField;
import aiss.videominer.exception.TokenNotValidException;
import aiss.videominer.exception.TokenRequiredException;
import aiss.videominer.exception.UserNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Comment;
import aiss.videominer.model.User;
import aiss.videominer.model.Video;
import aiss.videominer.repository.TokenRepository;
import aiss.videominer.repository.UserRepository;
import aiss.videominer.repository.VideoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name="User", description="User management API")
@RestController
@RequestMapping("/videoMiner/v1")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    VideoRepository videoRepository;

    // GET http://localhost:8080/videoMiner/v1/users
    @Operation( summary = "Retrieve a list of users",
            description = "Get a list of users with different options in paging, ordering and filtering. Only one of the filter parameters (id, name, userLink, pictureLink) may be present at the same time",
            tags = {"users", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema=@Schema(implementation = User.class)), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema=@Schema())})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users")
    public List<User> findAll(@RequestHeader HttpHeaders header,
                              @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String id, @RequestParam(required = false) String name,
                              @RequestParam(required = false) String userLink, @RequestParam(required = false) String pictureLink,
                              @RequestParam(required = false) String order) throws TokenRequiredException, TokenNotValidException, BadRequestParameterField, BadRequestIdParameter {
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
                try{
                    Long idL = Long.valueOf(id);
                    pageChannels = userRepository.findById(idL, paging);
                }catch(NumberFormatException  e) {
                    throw new BadRequestIdParameter();
                }
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

    // GET http://localhost:8080/videoMiner/v1/users/{id}
    @Operation( summary = "Retrieve a User by Id",
            description = "Get a User object by specifying its id",
            tags = {"users", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema=@Schema(implementation = User.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema=@Schema())})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{id}")
    public User findById(@PathVariable String id, @RequestHeader HttpHeaders header) throws UserNotFoundException, TokenNotValidException, TokenRequiredException {
        String token = header.getFirst("Authorization");
        if (token == null) {
            throw new TokenRequiredException();
        } else if (tokenRepository.existsById(token)) {
            Optional<User> user = userRepository.findById(id);
            if(!user.isPresent()){
                throw new UserNotFoundException();
            }
            return user.get();
        } else {
            throw new TokenNotValidException();
        }
    }

    //GET http://localhost:8080/videoMiner/v1/videos/{videoId}/users
    @Operation( summary = "Retrieve the list of users of a Video",
            description = "Get a list of users associated with the video Id",
            tags = {"users", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema=@Schema(implementation = User.class)), mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema=@Schema())})
    })
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

    // PUT http://localhost:8080/videoMiner/v1/users/{id}
    @Operation( summary = "Update a User",
            description = "Update a User object by specifying its Id and whose data is passed in the body of the request in JSON format. The id field cannot be modified.",
            tags = {"captions", "put"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema=@Schema())})
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/users/{id}")
    public void update(@Valid @RequestBody User updatedUser, @PathVariable String id, @RequestHeader HttpHeaders header) throws UserNotFoundException, TokenNotValidException, TokenRequiredException {
        String token = header.getFirst("Authorization");
        if (token == null) {
            throw new TokenRequiredException();
        } else if (tokenRepository.existsById(token)) {
            Optional<User> userData = userRepository.findById(id);
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
