package kg.spring.bot.service;

import kg.spring.bot.entity.User;
import kg.spring.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public void registerUser(Long chatId, Long cardId, String password, String direction) {
    User user = new User();
    user.setIdCard(cardId);
    user.setChatId(chatId);
    user.setPassword(password);
    user.setDirection(direction);
    userRepository.save(user);
  }

  public boolean loginUser(Long cardId, String password) {
    User user = userRepository.findByIdCard(cardId);
    if (user == null) {
      System.out.println("User not found");
      return false; // User not found
    }
    return user.getPassword().equals(password);
  }

  public User getUserByIdCard(Long cardId) {
    return userRepository.findByIdCard(cardId);
  }

}
