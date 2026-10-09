package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CountNotSleepingSessionAnalyzer implements SleepAnalyzer {

    //вспомогательные функции
    //Функция, которая возвращает true, если ночь не была бессонной
    Predicate<SleepingSession> isGoodSleep = session -> {
        LocalDateTime dataSleep = session.getDateSleep();
        LocalDateTime dataWake = session.getDateWake();

        //создаем интервалы с день пробуждение 00:00 и этот же день 06:00
        LocalDateTime intervalStart = dataWake.toLocalDate().atStartOfDay();
        LocalDateTime intervalEnd = intervalStart.plusHours(6);

        return dataSleep.isBefore(intervalEnd) && dataWake.isAfter(intervalStart);
    };

    //функция, которая возвращает день и добавляет к нему еще один, если >12дня
    public static LocalDate getSleepNightDate(SleepingSession session) {
        //получаем первую сессию сна
        LocalDateTime start = session.getDateSleep();

        LocalTime time = start.toLocalTime();

        if (time.isBefore(LocalTime.NOON)) return start.toLocalDate().minusDays(1);
        return start.toLocalDate();
    }

    //функция по подсчету бессоных ночей
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sleepingSessions) {
        if (sleepingSessions.isEmpty()) return null;

        //получаем первую и последнюю сессию временную метку
        SleepingSession startSessions = sleepingSessions.get(0);
        SleepingSession lastSessions = sleepingSessions.get(sleepingSessions.size() - 1);

        //Количество ночей, которые вошли в сессии, так как он не считает последнюю границу добавляем +1
        long totalNights = ChronoUnit.DAYS.between(
                startSessions.getDateSleep().toLocalDate(),
                lastSessions.getDateSleep().toLocalDate()
        ) + 1;

        //добавляем один день, если человек уснул до 12 дня и если больше 1 сессии, так как это тогда считается за одну ночь,
        //так как согласно условиям эта ночь считается предыдущей ночью
        if (startSessions.getDateSleep().toLocalTime().isBefore(LocalTime.NOON) && sleepingSessions.size() > 1) totalNights = totalNights + 1;

        //если в последнюю сессию, согласно условию, человек уснул до 12 дня, то это считается в предыдущую ночь
        //поэтому отнимаем один день
        if (lastSessions.getDateSleep().toLocalTime().isBefore(LocalTime.NOON) && sleepingSessions.size() > 1) totalNights = totalNights - 1;


        //получаем все сесси сна, которые относятся к определенному дню
        HashMap<LocalDate,List<SleepingSession>> nightSession = new HashMap<>();
        nightSession = sleepingSessions.stream()
                .collect(Collectors.groupingBy(
                        CountNotSleepingSessionAnalyzer::getSleepNightDate,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        //перебираем и получаем количество ночей сессий, в которых не спал
        int amountNightNotSleep = (int) nightSession.values().stream()
                .filter(nightInSession -> {
                    return nightInSession.stream().noneMatch(session -> {

                        return isGoodSleep.test(session);
                    });
                })
                .count();

        //в результате прибавляем к количеству бесснонных ночей, которые есть в сессиях,
        //разницу между общим количеством ночей с первой сессии по последнюю
        //и все те, которые есть отчете (nightSession), посколько мы не знаем какое количество ночей пользователь не спал вообще
        return new SleepAnalysisResult<Long>("Количество бессонных ночей",Optional.of(amountNightNotSleep + (totalNights - nightSession.size())));
    }
}
