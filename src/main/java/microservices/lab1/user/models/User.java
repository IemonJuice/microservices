package microservices.lab1.user.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import microservices.lab1.music.models.Music;
import microservices.lab1.video.models.Video;

import java.util.List;
@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Music> musics;
    @OneToMany(mappedBy = "user")
    private List<Video> videos;

}
