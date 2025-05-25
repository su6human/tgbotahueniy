package kg.spring.bot.repository;

import java.util.List;
import kg.spring.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
  User findByIdCard(Long cardId);
  List<User> findByDirection(String direction);
}
