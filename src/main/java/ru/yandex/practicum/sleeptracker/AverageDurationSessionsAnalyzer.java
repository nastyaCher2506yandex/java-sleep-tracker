package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class AverageDurationSessionsAnalyzer implements SleepAnalyzer {

    //функция, которая вычисляет средную продолжительность сессии (в минутах)
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessions) {
        Optional<Integer> averageSleepSession = Optional.of((int) sleepingSessions.stream()
                .mapToInt(session -> {
                    Duration durationSleep = Duration.between(session.getDateSleep(), session.getDateWake());
                    return (int) durationSleep.toMinutes();
                })
                .average().orElse(0.0));

        return new SleepAnalysisResult<Integer>("Средняя продолжительность сессии (в минутах)", averageSleepSession);
    }
}
