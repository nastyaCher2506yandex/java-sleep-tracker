package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.Optional;

public class CountBadStatusSessionsAnalyzer implements SleepAnalyzer {

    //количество сессий с плохим качеством сна
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessions) {
        Optional<Long> countBadSession = Optional.of(sleepingSessions.stream()
                .filter(session -> session.getSleepState() == SleepState.BAD)
                .count());

        return new SleepAnalysisResult<Long>("Количество сессий с плохим качеством сна", countBadSession);
    }
}
