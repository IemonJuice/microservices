package microservices.lab1.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetUserDTO {
    private Long id;
    private String username;
    private String password;
    private List<Long> musicIds;
    private List<Long> videoIds;
}