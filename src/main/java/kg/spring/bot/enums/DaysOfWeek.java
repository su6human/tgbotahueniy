package kg.spring.bot.enums;

import lombok.Getter;

@Getter
public enum DaysOfWeek {
    MONDAY("Понедельник"),
    TUESDAY("Вторник"),
    WEDNESDAY("Среда"),
    THURSDAY("Четверг"),
    FRIDAY("Пятница");

    private final String name;

    DaysOfWeek(String name) {
        this.name = name;
    }

}
