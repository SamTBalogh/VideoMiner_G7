package aiss.videominer.controller;

import aiss.videominer.exception.*;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.TokenRepository;
import aiss.videominer.repository.VideoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name="Caption", description="Caption management API")
@RestController
@RequestMapping("/videominer/v1")
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    TokenRepository tokenRepository;

    // GET http://localhost:8080/videominer/v1/captions
    @Operation( summary = "Retrieve a list of captions",
                description = "Get a list of captions with different options in paging, ordering and filtering",
                tags = {"captions", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema=@Schema(implementation = Caption.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema=@Schema())})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/captions")
    public List<Caption> findAll(@RequestHeader HttpHeaders header,
                                 @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String id, @RequestParam(required = false) String name,
                                 @RequestParam(required = false) String language, @RequestParam(required = false) String order) throws TokenRequiredException, TokenNotValidException, BadRequestParameterField {
        String token = header.getFirst("Authorization");
        if (token==null) {
            throw new TokenRequiredException();
        }
        else if(tokenRepository.existsById(token)) {
            Page<Caption> pageChannels;
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
            if (language != null) count++;

            if (count > 1) {
                throw new BadRequestParameterField();
            }

            if (id != null) {
                pageChannels = captionRepository.findById(id, paging);
            } else if (name != null) {
                pageChannels = captionRepository.findByName(name, paging);
            } else if (language != null) {
                pageChannels = captionRepository.findByLanguage(language, paging);
            } else {
                pageChannels = captionRepository.findAll(paging);
            }
            return pageChannels.getContent();
        } else {
            throw new TokenNotValidException();
        }
    }

    // GET http://localhost:8080/videominer/v1/captions/{id}
    @Operation( summary = "Retrieve a Caption by Id",
            description = "Get a Caption object by specifying its id",
            tags = {"captions", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema=@Schema(implementation = Caption.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema=@Schema())})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/captions/{id}")
    public Caption findById(@Parameter(description = "Id of the caption to be searched") @PathVariable String id, @RequestHeader HttpHeaders header) throws CaptionNotFoundException, TokenRequiredException, TokenNotValidException {
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

    //GET http://localhost:8080/videominer/v1/videos/{videoId}/captions
    @Operation( summary = "Retrieve the list of captions of a Video",
            description = "Get a list of captions associated with the video Id",
            tags = {"captions", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema=@Schema(implementation = Caption.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema=@Schema())})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/videos/{videoId}/captions")
    public List<Caption> getAllCaptionsByVideo(@Parameter (description = "The Id of the video for which captions are to be retrieved") @PathVariable("videoId") String videoId,
                                               @RequestHeader HttpHeaders header) throws VideoNotFoundException, TokenRequiredException, TokenNotValidException {
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

    //POST http://localhost:8080/videominer/v1/videos/{videoId}/captions
    @Operation( summary = "Insert a Caption into the list of captions of a Video",
            description = "Post a Caption object into the list of captions associated with the video Id, the Caption data is passed in the body of the request in JSON format",
            tags = {"captions", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema=@Schema(implementation = Caption.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema=@Schema())})
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/videos/{videoId}/captions")
    public List<Caption> create(@Parameter(description = "The ID of the video to which the caption is added") @PathVariable("videoId") String videoId,
                                @Valid @RequestBody Caption caption, @RequestHeader HttpHeaders header) throws VideoNotFoundException, TokenRequiredException, TokenNotValidException {
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

    // PUT http://localhost:8080/videominer/v1/captions/{id}
    @Operation( summary = "Update a Caption",
            description = "Update a Caption object by specifying its Id and whose data is passed in the body of the request in JSON format",
            tags = {"captions", "put"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema=@Schema())})
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/captions/{id}")
    public void update(@Valid @RequestBody Caption updatedCaption,
                       @Parameter(description = "Id of the caption to be updated") @PathVariable String id,
                       @RequestHeader HttpHeaders header) throws CaptionNotFoundException, TokenRequiredException, TokenNotValidException {
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

    // DELETE http://localhost:8080/videominer/v1/captions/{id}
    @Operation( summary = "Delete a Caption",
            description = "Delete a Caption object by specifying its Id",
            tags = {"captions", "delete"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema=@Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema=@Schema())})
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/captions/{id}")
    public void delete(@Parameter(description = "Id of the caption to be deleted") @PathVariable String id,
                       @RequestHeader HttpHeaders header) throws TokenRequiredException, TokenNotValidException, CaptionNotFoundException {
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
