package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class SleepingSession {

    private DateTimeFormatter formatTime;
    private LocalDateTime dateSleep;
    private LocalDateTime dateWake;
    private SleepState sleepState;

    SleepingSession(LocalDateTime dateSleep, LocalDateTime dateWake, SleepState sleepState, DateTimeFormatter formatTime) {
        this.dateSleep = dateSleep;
        this.dateWake = dateWake;
        this.sleepState = sleepState;
        this.formatTime = formatTime;
    }

    public DateTimeFormatter getFormatTime() {
        return formatTime;
    }


    public LocalDateTime getDateSleep() {
        return dateSleep;
    }


    public LocalDateTime getDateWake() {
        return dateWake;
    }


    public SleepState getSleepState() {
        return sleepState;
    }


    @Override
    public String toString() {
        return "Уснул: "  + dateSleep.format(formatTime) +
                ". Проснулся: " + dateWake.format(formatTime) +
                ". Состояние сна: " + sleepState;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SleepingSession that = (SleepingSession) o;
        return Objects.equals(formatTime, that.formatTime) && Objects.equals(dateSleep, that.dateSleep) && Objects.equals(dateWake, that.dateWake) && sleepState == that.sleepState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(formatTime, dateSleep, dateWake, sleepState);
    }
}
