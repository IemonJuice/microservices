package microservices.lab1.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateUserDTO {
    @NotBlank(message = "Username is required")
    private String username;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotEmpty(message = "Music IDs cannot be empty if provided")
    private List<Long> musicIds;

    @NotEmpty(message = "Video IDs cannot be empty if provided")
    private List<Long> videoIds;
}