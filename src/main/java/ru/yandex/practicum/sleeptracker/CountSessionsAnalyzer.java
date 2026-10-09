package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.Optional;

public class CountSessionsAnalyzer implements SleepAnalyzer {

    //функция, которая вычисляет сколько сессий за представленный период
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessions) {
        return new SleepAnalysisResult<Integer>("Количество сессий сна за представленный период", Optional.of(sleepingSessions.size()));
    }
}
