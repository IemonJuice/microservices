package microservices.lab1.user.repository;
import microservices.lab1.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}