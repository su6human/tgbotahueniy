package kg.spring.bot.repository;

import java.util.List;
import kg.spring.bot.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {

  @Query("SELECT s FROM Schedule s WHERE s.direction = ?1 AND s.dayOfWeek = ?2")
  List<Schedule> findAllByDirectionAndDayOfWeek(String direction, String dayOfWeek);

//
}
