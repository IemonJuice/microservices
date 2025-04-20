package microservices.lab1.video.repository;
import microservices.lab1.video.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {}