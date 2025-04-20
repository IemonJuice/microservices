package microservices.lab1.music.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMusicDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @NotBlank(message = "Artist is required")
    @Size(min = 1, max = 100, message = "Artist must be between 1 and 100 characters")
    private String artist;

    @Size(max = 50, message = "Genre must not exceed 50 characters")
    private String genre;

    private String path;

    @NotNull(message = "User ID is required")
    private Long userId;
}