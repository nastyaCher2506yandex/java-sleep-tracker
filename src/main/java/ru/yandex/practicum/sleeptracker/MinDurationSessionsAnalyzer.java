package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MinDurationSessionsAnalyzer implements SleepAnalyzer {

    //функция, которая вычисляет минимальную продолжительность сессии (в минутах)
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessions) {
        Optional<Integer> minSleepSession = sleepingSessions.stream()
                .map(session -> {
                    Duration durationSleep = Duration.between(session.getDateSleep(), session.getDateWake());
                    return (int)durationSleep.toMinutes();
                })
                .min(Comparator.comparingInt(minutes -> minutes));

        return new SleepAnalysisResult<Integer>("Минимальная продолжительность сессии (в минутах)", minSleepSession);
    }
}
