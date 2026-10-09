package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DetermineChronotypeAnalyzer implements SleepAnalyzer {

    //для определение бессонных ночей используем функциональный интерфейс для определение бессонных ночей - CountNotSleepingSessionAnalyzer
    CountNotSleepingSessionAnalyzer countNotSleepingSessionAnalyzer = new CountNotSleepingSessionAnalyzer();

    //вспомогательные функции
    //функция по поиску ночей, которые подходят для жаворонка (засыпание до 22:00 и пробуждение до 07:00)
    Function<List<SleepingSession>,List<SleepingSession>> findEarlySleepers = sessions -> {
        return sessions.stream()
                //если ночь бессоная её проверять и добавлять не нужно
                //также это убирает дневные сессии
                .filter(session -> countNotSleepingSessionAnalyzer.isGoodSleep.test(session))
                //
                .filter(session -> {
                    LocalDateTime startSleep = session.getDateSleep();
                    LocalDateTime endSleep = session.getDateWake();

                    if (startSleep.toLocalDate().isEqual(endSleep.toLocalDate())) return false;

                    //если входит в диапазон, значит ночь подходит жаворонкам
                    return startSleep.toLocalTime().isBefore(LocalTime.of(22,0)) && endSleep.toLocalTime().isBefore(LocalTime.of(7,0));
                })
                .collect(Collectors.toList());
    };

    //функция по поиску ночей, которые подходят под совы (засыпание после 23:00 и пробуждение после 09:00)
    Function<List<SleepingSession>, List<SleepingSession>> findLateSleepers = sessions -> {
        return sessions.stream()
                //игнорируем бессонные ночи и дневные сессии
                .filter(session -> countNotSleepingSessionAnalyzer.isGoodSleep.test(session))
                .filter(session -> {
                    //получаем дату и время засыпания / пробуждения в сессии
                    LocalDateTime startSleep = session.getDateSleep();
                    LocalDateTime endSleep = session.getDateWake();

                    //создаем интервалы : предыдущий день после пробуждение 23:00 и день пробуждение 09:00
                    LocalDateTime intervalStart = endSleep.toLocalDate().atStartOfDay().minusDays(1).plusHours(23);
                    LocalDateTime intervalEnd = endSleep.toLocalDate().atStartOfDay().plusHours(9);

                    return startSleep.isAfter(intervalStart) && endSleep.isAfter(intervalEnd);
                })
                .collect(Collectors.toList());
    };

    //функция по поиску ночей, которые относятся к голубям (если они не относятся не к жаворонкам, и не к совам
    Function<List<SleepingSession>, List<SleepingSession>> findAllIntermediateSessions = sessions -> {
        List<SleepingSession> earlySleepSession = findEarlySleepers.apply(sessions);
        List<SleepingSession> lastSleepSession = findLateSleepers.apply(sessions);

        return sessions.stream()
                //игнорируем бессонные ночи и дневные сессии
                .filter(session -> countNotSleepingSessionAnalyzer.isGoodSleep.test(session))
                .filter(session -> {
                    return (!(earlySleepSession.contains(session) || lastSleepSession.contains(session)));
                })
                .collect(Collectors.toList());
    };

    //основная функция
    //функция по определению типа по хронотипу на «сов» и «жаворонков»
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessions) {
        List<SleepingSession> earlySleepSession = findEarlySleepers.apply(sleepingSessions);
        List<SleepingSession> lastSleepSession = findLateSleepers.apply(sleepingSessions);
        List<SleepingSession> allIntermediateSessions = findAllIntermediateSessions.apply(sleepingSessions);

        if (allIntermediateSessions.size() > lastSleepSession.size()
                && allIntermediateSessions.size() > earlySleepSession.size()) {
            return new SleepAnalysisResult<String>("Человек относится к хронотипу", Optional.of("голубь"));
        } else if (earlySleepSession.size() < lastSleepSession.size()) {
            return new SleepAnalysisResult<String>("Человек относится к хронотипу",Optional.of("сова"));
        } else if (earlySleepSession.size() > lastSleepSession.size()) {
            return new SleepAnalysisResult<String>("Человек относится к хронотипу",Optional.of("жаворонок"));
        } else {
            return new SleepAnalysisResult<String>("Человек относится к хронотипу",Optional.of("голубь"));
        }
    }
}
