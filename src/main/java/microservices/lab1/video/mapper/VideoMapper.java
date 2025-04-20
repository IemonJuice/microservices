package microservices.lab1.video.mapper;

import lombok.RequiredArgsConstructor;
import microservices.lab1.common.exception.CustomException;
import microservices.lab1.user.models.User;
import microservices.lab1.user.repository.UserRepository;
import microservices.lab1.video.dto.CreateVideoDTO;
import microservices.lab1.video.dto.UpdateVideoDTO;
import microservices.lab1.video.dto.GetVideoDTO;
import microservices.lab1.video.models.Video;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoMapper {

    private final UserRepository userRepository;

    public Video toEntity(CreateVideoDTO dto) {
        Video video = new Video();
        video.setTitle(dto.getTitle());
        video.setDirector(dto.getDirector());
        video.setGenre(dto.getGenre());
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException("Користувача з ID " + dto.getUserId() + " не знайдено."));
        video.setUser(user);
        return video;
    }

    public Video toEntity(UpdateVideoDTO dto, Video existingVideo) {
        existingVideo.setTitle(dto.getTitle());
        existingVideo.setDirector(dto.getDirector());
        existingVideo.setGenre(dto.getGenre());
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException("Користувача з ID " + dto.getUserId() + " не знайдено."));
        existingVideo.setUser(user);
        return existingVideo;
    }

    public GetVideoDTO toGetVideoDTO(Video video) {
        GetVideoDTO dto = new GetVideoDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setDirector(video.getDirector());
        dto.setGenre(video.getGenre());
        dto.setPath(video.getPath());
        dto.setUserId(video.getUser() != null ? video.getUser().getId() : null);
        return dto;
    }
}