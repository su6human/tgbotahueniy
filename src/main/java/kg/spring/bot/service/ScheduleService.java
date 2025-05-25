package kg.spring.bot.service;

import java.util.List;
import kg.spring.bot.entity.Schedule;
import kg.spring.bot.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

  private final ScheduleRepository scheduleRepository;

  public String getSchedule(String direction, String day) {
    List<Schedule> allByDirectionAndDayOfWeek = scheduleRepository.findAllByDirectionAndDayOfWeek(
        direction, day);
    String content = "";
    for (Schedule schedule : allByDirectionAndDayOfWeek) {
      content += schedule.getDateTime().getHour() + ":" + schedule.getDateTime().getMinute() + " " + schedule.getContent() + "\n";
    }
    if (content.isEmpty()) {
      return "Расписание на " + day + " для направления " + direction + " не найдено.";
    } else {
      return content;
    }

  }


}
