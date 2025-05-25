package kg.spring.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class TelegramBotService {

  private final ScheduleBot scheduleBot;

  public void sendMessage(Long chatId, String message) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText(message);
    try {
      scheduleBot.execute(sendMessage);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }




}
