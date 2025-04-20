package microservices.lab1.music.services;

import lombok.RequiredArgsConstructor;
import microservices.lab1.common.exception.CustomException;
import microservices.lab1.music.dto.CreateMusicDTO;
import microservices.lab1.music.dto.UpdateMusicDTO;
import microservices.lab1.music.dto.GetMusicDTO;
import microservices.lab1.music.mapper.MusicMapper;
import microservices.lab1.music.models.Music;
import microservices.lab1.music.repository.MusicRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;
    private final MusicMapper musicMapper;

    public GetMusicDTO save(CreateMusicDTO musicDTO, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new CustomException("Файл не передано.");
        }

        Music music = musicMapper.toEntity(musicDTO);

        File mediaDirectory = new File("media");
        if (!mediaDirectory.exists()) {
            mediaDirectory.mkdirs();
        }

        String fileName = musicDTO.getTitle() + "_" + System.currentTimeMillis() + getFileExtension(file.getOriginalFilename());
        Path filePath = Paths.get("src/main/resources/media", fileName);
        Files.write(filePath, file.getBytes());

        music.setFilePath(filePath.toString());

        Music savedMusic = musicRepository.save(music);
        return musicMapper.toGetMusicDTO(savedMusic);
    }

    public GetMusicDTO findById(long id) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new CustomException("Музику з ID " + id + " не знайдено."));
        return musicMapper.toGetMusicDTO(music);
    }

    public List<GetMusicDTO> findAll() {
        return musicRepository.findAll().stream()
                .map(musicMapper::toGetMusicDTO)
                .collect(Collectors.toList());
    }

    public GetMusicDTO update(long id, UpdateMusicDTO musicDTO, MultipartFile file) throws IOException {
        if (!musicRepository.existsById(id)) {
            throw new CustomException("Музику з ID " + id + " не знайдено.");
        }

        Music music = musicRepository.findById(id).get();
        music = musicMapper.toEntity(musicDTO, music);

        if (file != null && !file.isEmpty()) {
            File mediaDirectory = new File("media");
            if (!mediaDirectory.exists()) {
                mediaDirectory.mkdirs();
            }

            String fileName = musicDTO.getTitle() + "_" + System.currentTimeMillis() + getFileExtension(file.getOriginalFilename());
            Path filePath = Paths.get("src/main/resources/media", fileName);
            Files.write(filePath, file.getBytes());

            music.setFilePath(filePath.toString());
        }

        Music updatedMusic = musicRepository.save(music);
        return musicMapper.toGetMusicDTO(updatedMusic);
    }

    public boolean delete(long id, Long userId) {
        if (!musicRepository.existsById(id)) {
            throw new CustomException("Музику з ID " + id + " не знайдено.");
        }

        Music music = musicRepository.findById(id).get();
        if (userId != null && (music.getUser() == null || !music.getUser().getId().equals(userId))) {
            throw new CustomException("Музика не належить користувачу з ID " + userId);
        }

        musicRepository.deleteById(id);
        return true;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
}