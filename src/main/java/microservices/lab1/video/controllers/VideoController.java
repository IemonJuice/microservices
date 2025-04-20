package microservices.lab1.video.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservices.lab1.video.dto.CreateVideoDTO;
import microservices.lab1.video.dto.UpdateVideoDTO;
import microservices.lab1.video.dto.DeleteVideoDTO;
import microservices.lab1.video.dto.GetVideoDTO;
import microservices.lab1.video.services.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<GetVideoDTO> createVideo(
            @RequestPart("video") @Valid CreateVideoDTO videoDTO,
            @RequestPart("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            GetVideoDTO createdVideo = videoService.save(videoDTO, file);
            return new ResponseEntity<>(createdVideo, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetVideoDTO> getVideoById(@PathVariable long id) {
        GetVideoDTO video = videoService.findById(id);
        if (video != null) {
            return new ResponseEntity<>(video, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<GetVideoDTO>> getAllVideos() {
        List<GetVideoDTO> videoList = videoService.findAll();
        return new ResponseEntity<>(videoList, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<GetVideoDTO> updateVideo(
            @PathVariable long id,
            @RequestPart("video") @Valid UpdateVideoDTO videoDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            GetVideoDTO updatedVideo = videoService.update(id, videoDTO, file);
            if (updatedVideo != null) {
                return new ResponseEntity<>(updatedVideo, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable long id, @RequestBody DeleteVideoDTO deleteDTO) {
        if (videoService.delete(id, deleteDTO.getUserId())) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}