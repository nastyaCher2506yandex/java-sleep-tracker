package ru.yandex.practicum.sleeptracker;

import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//класс, который хранит все аналитические функциии приложения
public class AnalisusFunction {

    //функция, которая вычисляет сколько сессий за представленный период
    Function<List<SleepingSession>, SleepAnalysisResult> countSleepSession = sessions -> {
        return new SleepAnalysisResult<Integer>("Количество сессий сна за представленный период", Optional.of(sessions.size()));
    };

    //функция, которая вычисляет минимальную продолжительность сессии (в минутах)
    Function<List<SleepingSession>, SleepAnalysisResult> minDuractionSleep = sessions -> {
        Optional<Integer> minSleepSession = sessions.stream()
            .map(session -> {
                Duration durationSleep = Duration.between(session.getDateSleep(), session.getDateWake());
                return (int)durationSleep.toMinutes();
            })
            .min(Comparator.comparingInt(minutes -> minutes));

        return new SleepAnalysisResult<Integer>("Минимальная продолжительность сессии (в минутах)", minSleepSession);
    };

    //функция, которая вычисляет максимальную продолжительность сессии (в минутах)
    Function<List<SleepingSession>, SleepAnalysisResult> maxDuractionSleep = sessions -> {
        Optional<Integer> maxSleepSession = sessions.stream()
                .map(session -> {
                    Duration durationSleep = Duration.between(session.getDateSleep(), session.getDateWake());
                    return (int)durationSleep.toMinutes();
                })
                .max(Comparator.comparingInt(minutes -> minutes));

        return new SleepAnalysisResult<Integer>("Максимальная продолжительность сессии (в минутах)", maxSleepSession);
    };

    //функция, которая вычисляет средную продолжительность сессии (в минутах)
    Function<List<SleepingSession>, SleepAnalysisResult> averageDuractionSleep = sessions -> {
        Optional<Integer> averageSleepSession = Optional.of((int) sessions.stream()
                .mapToInt(session -> {
                    Duration durationSleep = Duration.between(session.getDateSleep(), session.getDateWake());
                    return (int) durationSleep.toMinutes();
                })
                .average().orElse(0.0));

        return new SleepAnalysisResult<Integer>("Средняя продолжительность сессии (в минутах)", averageSleepSession);
    };

    //количество сессий с плохим качеством сна
    Function<List<SleepingSession>, SleepAnalysisResult> countBadSleepSession = sessions -> {
        Optional<Long> countBadSession = Optional.of(sessions.stream()
                .filter(session -> session.getSleepState() == SleepState.BAD)
                .count());

        return new SleepAnalysisResult<Long>("Количество сессий с плохим качеством сна", countBadSession);
    };

    //Функция, которая возвращает true, если ночь не была бессонной
    Predicate<SleepingSession> isGoodSleep = session -> {
        LocalDateTime dataSleep = session.getDateSleep();
        LocalDateTime dataWake = session.getDateWake();

        //создаем интервалы с день пробуждение 00:00 и этот же день 06:00
        LocalDateTime intervalStart = dataWake.toLocalDate().atStartOfDay();
        LocalDateTime intervalEnd = intervalStart.plusHours(6);

        return dataSleep.isBefore(intervalEnd) && dataWake.isAfter(intervalStart);
    };

    //функция по подсчету бессоных ночей
    Function<List<SleepingSession>, SleepAnalysisResult> countNightNotSleep = sessions -> {
        //получаем первую и последнюю сессию временную метку
        SleepingSession startSessions = sessions.get(0);
        SleepingSession lastSessions = sessions.get(sessions.size() - 1);

        //Количество ночей, которые вошли в сессии, так как он не считает последнюю границу добавляем +1
        Period amountNight = Period.between(startSessions.getDateSleep().toLocalDate(),lastSessions.getDateSleep().toLocalDate()).plusDays(1);

        //добавляем один день, если человек уснул до 12 дня и если больше 1 сессии, так как это тогда считается за одну ночь,
        //так как согласно условиям эта ночь считается предыдущей ночью
        if(startSessions.getDateSleep().toLocalTime().isBefore(LocalTime.NOON) && sessions.size() > 1) amountNight = amountNight.plusDays(1);

        //если в последнюю сессию, согласно условию, человек уснул до 12 дня, то это считается в предыдущую ночь
        //поэтому отнимаем один день
        if(lastSessions.getDateSleep().toLocalTime().isBefore(LocalTime.NOON) && sessions.size() > 1) amountNight = amountNight.minusDays(1);


        //получаем все сесси сна, которые относятся к определенному дню
        HashMap<LocalDate,List<SleepingSession>> nightSession = new HashMap<>();
        nightSession = sessions.stream()
                .collect(Collectors.groupingBy(
                        AnalisusFunction::getSleepNightDate,
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
        return new SleepAnalysisResult<Integer>("Количество бессонных ночей",Optional.of(amountNightNotSleep + (amountNight.getDays() - nightSession.size())));
    };

    //функция по поиску ночей, которые подходят для жаворонка (засыпание до 22:00 и пробуждение до 07:00)
    Function <List<SleepingSession>,List<SleepingSession>> findEarlySleepers = sessions -> {
        return sessions.stream()
                //если ночь бессоная её проверять и добавлять не нужно
                //также это убирает дневные сессии
                .filter(session -> isGoodSleep.test(session))
                //
                .filter(session -> {
                    LocalDateTime startSleep = session.getDateSleep();
                    LocalDateTime endSleep = session.getDateWake();

                    if(startSleep.toLocalDate().isEqual(endSleep.toLocalDate())) return false;

                    //если входит в диапазон, значит ночь подходит жаворонкам
                    return startSleep.toLocalTime().isBefore(LocalTime.of(22,0)) && endSleep.toLocalTime().isBefore(LocalTime.of(7,0));
                })
                .collect(Collectors.toList());
    };

    //функция по поиску ночей, которые подходят под совы (засыпание после 23:00 и пробуждение после 09:00)
    Function<List<SleepingSession>, List<SleepingSession>> findLateSleepers = sessions -> {
        return sessions.stream()
                //игнорируем бессонные ночи и дневные сессии
                .filter(session -> isGoodSleep.test(session))
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
                .filter(session -> isGoodSleep.test(session))
                .filter(session -> {
                    return (!(earlySleepSession.contains(session) || lastSleepSession.contains(session)));
                })
                .collect(Collectors.toList());
    };

    //функция по определению типа по хронотипу на «сов» и «жаворонков»
    Function<List<SleepingSession>,SleepAnalysisResult> determineChronotype = sessions -> {
        List<SleepingSession> earlySleepSession = findEarlySleepers.apply(sessions);
        List<SleepingSession> lastSleepSession = findLateSleepers.apply(sessions);
        List<SleepingSession> allIntermediateSessions = findAllIntermediateSessions.apply(sessions);

        if (allIntermediateSessions.size() > lastSleepSession.size()
                && allIntermediateSessions.size() > earlySleepSession.size()) {
            return new SleepAnalysisResult<String>("Человек относится к хронотипу",Optional.of("голубь"));
        }
        else if (earlySleepSession.size() < lastSleepSession.size()) {
            return new SleepAnalysisResult<String>("Человек относится к хронотипу",Optional.of("сова"));
        }
        else if (earlySleepSession.size() > lastSleepSession.size()) {
            return new SleepAnalysisResult<String>("Человек относится к хронотипу",Optional.of("жаворонок"));
        } else {
            return new SleepAnalysisResult<String>("Человек относится к хронотипу",Optional.of("голубь"));
        }
    };

    //функция, которая возвращает день и добавляет к нему еще один, если >12дня
    public static LocalDate getSleepNightDate(SleepingSession session) {
        //получаем первую сессию сна
        LocalDateTime start = session.getDateSleep();

        LocalTime time = start.toLocalTime();

        if (time.isBefore(LocalTime.NOON)) return start.toLocalDate().minusDays(1);
        return start.toLocalDate();
    }
}
