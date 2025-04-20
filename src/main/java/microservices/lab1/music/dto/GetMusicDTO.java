package microservices.lab1.music.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMusicDTO {
    private Long id;
    private String title;
    private String artist;
    private String genre;
    private String path;
}