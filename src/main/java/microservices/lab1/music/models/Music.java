package microservices.lab1.music.models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import microservices.lab1.user.models.User;


@Entity
@Getter
@Setter
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String title;
    private String artist;
    private String genre;
    private String path;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void setFilePath(String filePath) {
        this.path = filePath;
    }
}
