package kg.spring.bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

  @Id
  @NotNull
  private Long idCard;

  @Column
  private String password;

  @Column
  private Long chatId;

  @Column
  private String direction;

}
