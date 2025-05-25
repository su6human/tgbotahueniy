package kg.spring.bot.enums;

import lombok.Getter;

@Getter
public enum Direction {

  COMFCI23("COMFCI-23"),
  COMCEH23("COMCEH-23"),
  COMSE23("COMSE-23"),
  EEAIR23("EEAIR-23"),
  IEMIT23("IEMIT-23");

  private final String name;

  Direction(String name) {
    this.name = name;
  }


}
