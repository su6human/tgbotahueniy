package kg.spring.bot.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import kg.spring.bot.config.BotConfig;
import kg.spring.bot.entity.User;
import kg.spring.bot.enums.DaysOfWeek;
import kg.spring.bot.enums.Direction;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class ScheduleBot extends TelegramLongPollingBot {

  private final BotConfig botConfig;
  private final UserService userService;
  private final ScheduleService scheduleService;

  private final static HashMap<Long, String> userState = new HashMap<>();
  private final static HashMap<Long, String> userDirection = new HashMap<>();

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      String text = update.getMessage().getText();
      long chatId = update.getMessage().getChatId();

      String[] userCredentials;



      switch (text) {

        case "/start":
          userState.remove(chatId);
          userDirection.remove(chatId);
          sendStartMenu(chatId);
          return;

        case "Вход":
          sendMessage(chatId, "Введите ID Card и пароль через пробел:", null);
          userState.put(chatId, "awaiting_login");
          return;

        case "Регистрация":
          sendMessage(chatId, "Введите ID Card, пароль и направление (например COMSE23, COMFCI23 и т.д.):", null);
          userState.put(chatId, "awaiting_register");
          return;

        case "Назад":
          String state = userState.getOrDefault(chatId, "");
          if (state.equals("selecting_day")) {
            sendDirectionMenu(chatId);
          } else {
            sendStartMenu(chatId);
          }
          return;

        case "Стоп":
          userState.remove(chatId);
          userDirection.remove(chatId);
          sendStartMenu(chatId);
          return;
      }

      String state = userState.getOrDefault(chatId, "");

      switch (state) {

        case "awaiting_login": {
          userCredentials = text.trim().split("\\s+");
          login(chatId, userCredentials);
          return;
        }

        case "awaiting_register": {
          userCredentials = text.trim().split("\\s+");
          register(chatId, userCredentials);
          return;
        }

        case "selecting_direction":
          if (Arrays.stream(Direction.values()).anyMatch(direction -> direction.toString().equals(text))) {
            userDirection.put(chatId, text);
            sendDayMenu(chatId);
          } else {
            sendMessage(chatId, "Выберите направление из списка.", null);
          }
          return;

        case "selecting_day":
          if (Arrays.stream(DaysOfWeek.values()).anyMatch(day -> day.toString().equals(text))) {
            String direction = userDirection.get(chatId);
            if (direction != null) {
              sendSchedule(chatId, direction, text);
            } else {
              sendMessage(chatId, "Ошибка: направление не выбрано.", null);
            }
          } else {
            sendMessage(chatId, "Выберите день недели из списка.", null);
          }
          return;

        default:
          sendMessage(chatId, "Неизвестная команда. Нажмите /start.", null);
          break;
      }
    }
    

  }

  @Override
  public String getBotUsername() {
    return botConfig.getBotName();
  }

  @Override
  public String getBotToken() {
    return botConfig.getToken();
  }


  private void login(Long chatId, String[] userCredentials) {
    Long cardId = Long.valueOf(userCredentials[0]);
    String password = userCredentials[1];
    if (userCredentials.length != 2) {
      sendMessage(chatId, "Неверный формат. Введите ID и пароль через пробел.", null);
      return;
    }

    if (userService.loginUser(cardId, password)) {
      sendDirectionMenu(chatId);
    } else {
      sendMessage(chatId, "Неверный ID Card или пароль. Попробуйте снова.", null);
    }
  }

  private void register(Long chatId, String[] userCredentials) {
    if (userCredentials.length != 3) {
      sendMessage(chatId, "Неверный формат. Введите ID и пароль через пробел.", null);
      return;
    }


    Long cardId = Long.valueOf(userCredentials[0]);
    String password = userCredentials[1];
    String direction = userCredentials[2];


    userService.registerUser(chatId, cardId, password,direction);
    sendMessage(chatId, "Регистрация прошла успешно! Теперь войдите.", null);
    sendStartMenu(chatId);
  }

  private void sendMessage(long chatId, String text, ReplyKeyboardMarkup keyboardMarkup) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(text);
    if (keyboardMarkup != null) {
      message.setReplyMarkup(keyboardMarkup);
    }

    try {
      execute(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  private void sendStartMenu(long chatId) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    keyboardMarkup.setResizeKeyboard(true);

    List<KeyboardRow> keyboard = new ArrayList<>();
    KeyboardRow row = new KeyboardRow();
    row.add(new KeyboardButton("Вход"));
    row.add(new KeyboardButton("Регистрация"));
    keyboard.add(row);

    keyboardMarkup.setKeyboard(keyboard);

    sendMessage(chatId, "Добро пожаловать! Выберите действие:", keyboardMarkup);
    userState.put(chatId, "start");
  }

  private void sendDirectionMenu(long chatId) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    keyboardMarkup.setResizeKeyboard(true);

    List<KeyboardRow> keyboard = new ArrayList<>();
    for (Direction dir : Direction.values()) {
      KeyboardRow row = new KeyboardRow();
      row.add(new KeyboardButton(dir.toString()));
      keyboard.add(row);
    }

    KeyboardRow stopRow = new KeyboardRow();
    stopRow.add(new KeyboardButton("Назад"));
    stopRow.add(new KeyboardButton("Стоп"));
    keyboard.add(stopRow);

    keyboardMarkup.setKeyboard(keyboard);

    sendMessage(chatId, "Выберите направление:", keyboardMarkup);
    userState.put(chatId, "selecting_direction");
  }

  private void sendDayMenu(long chatId) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    keyboardMarkup.setResizeKeyboard(true);

    List<KeyboardRow> keyboard = new ArrayList<>();
    for (DaysOfWeek day : DaysOfWeek.values()) {
      KeyboardRow row = new KeyboardRow();
      row.add(new KeyboardButton(day.toString()));
      keyboard.add(row);
    }

    KeyboardRow row = new KeyboardRow();
    row.add(new KeyboardButton("Назад"));
    row.add(new KeyboardButton("Стоп"));
    keyboard.add(row);

    keyboardMarkup.setKeyboard(keyboard);

    sendMessage(chatId, "Выберите день недели:", keyboardMarkup);
    userState.put(chatId, "selecting_day");
  }

  private void sendSchedule(long chatId, String direction, String day) {
    String schedule = scheduleService.getSchedule(direction, day);
    sendMessage(chatId, "Расписание для " + direction + " на " + day + ":\n" + schedule, null);
  }


}
