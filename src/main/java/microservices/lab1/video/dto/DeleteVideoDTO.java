package microservices.lab1.video.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteVideoDTO {
    @NotNull(message = "ID is required")
    private Long id;

    private Long userId;
}