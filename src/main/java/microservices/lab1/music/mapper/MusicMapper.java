package microservices.lab1.music.mapper;

import lombok.RequiredArgsConstructor;
import microservices.lab1.common.exception.CustomException;
import microservices.lab1.music.dto.CreateMusicDTO;
import microservices.lab1.music.dto.UpdateMusicDTO;
import microservices.lab1.music.dto.GetMusicDTO;
import microservices.lab1.music.models.Music;
import microservices.lab1.user.models.User;
import microservices.lab1.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MusicMapper {

    private final UserRepository userRepository;

    public Music toEntity(CreateMusicDTO dto) {
        Music music = new Music();
        music.setTitle(dto.getTitle());
        music.setArtist(dto.getArtist());
        music.setGenre(dto.getGenre());
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException("Користувача з ID " + dto.getUserId() + " не знайдено."));
        music.setUser(user);
        return music;
    }

    public Music toEntity(UpdateMusicDTO dto, Music existingMusic) {
        existingMusic.setTitle(dto.getTitle());
        existingMusic.setArtist(dto.getArtist());
        existingMusic.setGenre(dto.getGenre());
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException("Користувача з ID " + dto.getUserId() + " не знайдено."));
        existingMusic.setUser(user);
        return existingMusic;
    }

    public GetMusicDTO toGetMusicDTO(Music music) {
        GetMusicDTO dto = new GetMusicDTO();
        dto.setId(music.getId());
        dto.setTitle(music.getTitle());
        dto.setArtist(music.getArtist());
        dto.setGenre(music.getGenre());
        return dto;
    }
}