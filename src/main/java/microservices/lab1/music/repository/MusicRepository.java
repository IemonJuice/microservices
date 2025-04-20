package microservices.lab1.music.repository;
import microservices.lab1.music.models.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, Long> {}