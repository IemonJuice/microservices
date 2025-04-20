package microservices.lab1.video.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetVideoDTO {
    private Long id;
    private String title;
    private String director;
    private String genre;
    private String path;
    private Long userId;
}