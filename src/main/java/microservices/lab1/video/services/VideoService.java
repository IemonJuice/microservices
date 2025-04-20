package microservices.lab1.video.services;

import lombok.RequiredArgsConstructor;
import microservices.lab1.common.exception.CustomException;
import microservices.lab1.video.dto.CreateVideoDTO;
import microservices.lab1.video.dto.UpdateVideoDTO;
import microservices.lab1.video.dto.GetVideoDTO;
import microservices.lab1.video.mapper.VideoMapper;
import microservices.lab1.video.models.Video;
import microservices.lab1.video.repository.VideoRepository;
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
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    public GetVideoDTO save(CreateVideoDTO videoDTO, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new CustomException("Файл не передано.");
        }

        Video video = videoMapper.toEntity(videoDTO);

        File mediaDirectory = new File("media");
        if (!mediaDirectory.exists()) {
            mediaDirectory.mkdirs();
        }

        String fileName = videoDTO.getTitle() + "_" + System.currentTimeMillis() + getFileExtension(file.getOriginalFilename());
        Path filePath = Paths.get("src/main/resources/media", fileName);
        Files.write(filePath, file.getBytes());

        video.setPath(filePath.toString());

        Video savedVideo = videoRepository.save(video);
        return videoMapper.toGetVideoDTO(savedVideo);
    }

    public GetVideoDTO findById(long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new CustomException("Відео з ID " + id + " не знайдено."));
        return videoMapper.toGetVideoDTO(video);
    }

    public List<GetVideoDTO> findAll() {
        return videoRepository.findAll().stream()
                .map(videoMapper::toGetVideoDTO)
                .collect(Collectors.toList());
    }

    public GetVideoDTO update(long id, UpdateVideoDTO videoDTO, MultipartFile file) throws IOException {
        if (!videoRepository.existsById(id)) {
            throw new CustomException("Відео з ID " + id + " не знайдено.");
        }

        Video video = videoRepository.findById(id).get();
        video = videoMapper.toEntity(videoDTO, video);

        if (file != null && !file.isEmpty()) {
            File mediaDirectory = new File("media");
            if (!mediaDirectory.exists()) {
                mediaDirectory.mkdirs();
            }

            String fileName = videoDTO.getTitle() + "_" + System.currentTimeMillis() + getFileExtension(file.getOriginalFilename());
            Path filePath = Paths.get("src/main/resources/media", fileName);
            Files.write(filePath, file.getBytes());

            video.setPath(filePath.toString());
        }

        Video updatedVideo = videoRepository.save(video);
        return videoMapper.toGetVideoDTO(updatedVideo);
    }

    public boolean delete(long id, Long userId) {
        if (!videoRepository.existsById(id)) {
            throw new CustomException("Відео з ID " + id + " не знайдено.");
        }

        Video video = videoRepository.findById(id).get();
        if (userId != null && (video.getUser() == null || !video.getUser().getId().equals(userId))) {
            throw new CustomException("Відео не належить користувачу з ID " + userId);
        }

        videoRepository.deleteById(id);
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