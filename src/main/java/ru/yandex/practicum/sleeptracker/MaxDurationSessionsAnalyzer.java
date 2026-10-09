package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MaxDurationSessionsAnalyzer implements SleepAnalyzer {

    //функция, которая вычисляет максимальную продолжительность сессии (в минутах)
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessions) {
        Optional<Integer> maxSleepSession = sleepingSessions.stream()
                .map(session -> {
                    Duration durationSleep = Duration.between(session.getDateSleep(), session.getDateWake());
                    return (int)durationSleep.toMinutes();
                })
                .max(Comparator.comparingInt(minutes -> minutes));

        return new SleepAnalysisResult<Integer>("Максимальная продолжительность сессии (в минутах)", maxSleepSession);
    }
}
