package microservices.lab1.music.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservices.lab1.music.dto.CreateMusicDTO;
import microservices.lab1.music.dto.UpdateMusicDTO;
import microservices.lab1.music.dto.DeleteMusicDTO;
import microservices.lab1.music.dto.GetMusicDTO;
import microservices.lab1.music.services.MusicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<GetMusicDTO> createMusic(
            @RequestPart("music") @Valid CreateMusicDTO musicDTO,
            @RequestPart("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            GetMusicDTO createdMusic = musicService.save(musicDTO, file);
            return new ResponseEntity<>(createdMusic, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetMusicDTO> getMusicById(@PathVariable long id) {
        GetMusicDTO music = musicService.findById(id);
        if (music != null) {
            return new ResponseEntity<>(music, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<GetMusicDTO>> getAllMusic() {
        List<GetMusicDTO> musicList = musicService.findAll();
        return new ResponseEntity<>(musicList, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<GetMusicDTO> updateMusic(
            @PathVariable long id,
            @RequestPart("music") @Valid UpdateMusicDTO musicDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            GetMusicDTO updatedMusic = musicService.update(id, musicDTO, file);
            if (updatedMusic != null) {
                return new ResponseEntity<>(updatedMusic, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusic(@PathVariable long id, @RequestBody DeleteMusicDTO deleteDTO) {
        boolean deleted = musicService.delete(id, deleteDTO.getUserId());
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}