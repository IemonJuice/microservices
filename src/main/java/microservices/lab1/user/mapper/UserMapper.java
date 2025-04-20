package microservices.lab1.user.mapper;

import lombok.RequiredArgsConstructor;
import microservices.lab1.common.exception.CustomException;
import microservices.lab1.music.models.Music;
import microservices.lab1.music.repository.MusicRepository;
import microservices.lab1.user.dto.CreateUserDTO;
import microservices.lab1.user.dto.UpdateUserDTO;
import microservices.lab1.user.dto.GetUserDTO;
import microservices.lab1.user.models.User;
import microservices.lab1.video.models.Video;
import microservices.lab1.video.repository.VideoRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final MusicRepository musicRepository;
    private final VideoRepository videoRepository;

    public User toEntity(CreateUserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setMusics(getMusicEntities(dto.getMusicIds()));
        user.setVideos(getVideoEntities(dto.getVideoIds()));
        return user;
    }

    public User toEntity(UpdateUserDTO dto, User existingUser) {
        existingUser.setUsername(dto.getUsername());
        existingUser.setPassword(dto.getPassword());
        existingUser.setMusics(getMusicEntities(dto.getMusicIds()));
        existingUser.setVideos(getVideoEntities(dto.getVideoIds()));
        return existingUser;
    }

    public GetUserDTO toGetUserDTO(User user) {
        GetUserDTO dto = new GetUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setMusicIds(user.getMusics() != null
                ? user.getMusics().stream().map(Music::getId).collect(Collectors.toList())
                : Collections.emptyList());
        dto.setVideoIds(user.getVideos() != null
                ? user.getVideos().stream().map(Video::getId).collect(Collectors.toList())
                : Collections.emptyList());
        return dto;
    }

    private List<Music> getMusicEntities(List<Long> musicIds) {
        if (musicIds == null || musicIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Music> musics = musicRepository.findAllById(musicIds);
        if (musics.size() != musicIds.size()) {
            throw new CustomException("Деякі музичні треки не знайдено.");
        }
        return musics;
    }

    private List<Video> getVideoEntities(List<Long> videoIds) {
        if (videoIds == null || videoIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Video> videos = videoRepository.findAllById(videoIds);
        if (videos.size() != videoIds.size()) {
            throw new CustomException("Деякі відео не знайдено.");
        }
        return videos;
    }
}