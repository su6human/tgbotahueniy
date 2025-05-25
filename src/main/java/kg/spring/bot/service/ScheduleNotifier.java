package kg.spring.bot.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import kg.spring.bot.entity.Schedule;
import kg.spring.bot.entity.User;
import kg.spring.bot.repository.ScheduleRepository;
import kg.spring.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleNotifier {

  private final TelegramBotService telegramBotService;
  private final ScheduleRepository scheduleRepository;
  private final UserRepository userRepository;


  @Scheduled(cron = "0 * * * * *")
  public void notifyUsers() {
    LocalDateTime now = LocalDateTime.now();

    List<Schedule> allSchedules = scheduleRepository.findAll();

    for (Schedule schedule : allSchedules) {
      if (schedule.getDateTime().minusHours(1).isBefore(now) &&
          schedule.getDateTime().isAfter(now)) {

        List<User> users = userRepository.findByDirection(schedule.getDirection());

        for (User user : users) {
          telegramBotService.sendMessage(
              user.getChatId(),
              "Напоминание: через час у вас пара:\n" + schedule.getContent()
          );
        }
      }
    }
  }
}
