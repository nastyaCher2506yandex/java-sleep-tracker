package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepTrackerAppTest {
    AnalisusFunction analisusFunction;
    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    ArrayList<SleepingSession> sleepingSessions;

    @BeforeEach
    void forTest() {
        analisusFunction = new AnalisusFunction();
        sleepingSessions = new ArrayList<>();
    }

    //тесты на проверку количества сессии за представленный в файле период, когда он не пустой
    @Test
    void checkWorkFunctionCountSleepSessionsIsNotEmpty() {
        String[] logFileSleepTracker = {
          "30.09.25 23:00;01.10.25 08:00;GOOD",
          "01.10.25 19:00;02.10.25 05:00;GOOD",
          "04.10.25 01:00;04.10.25 10:00;GOOD"
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(),FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(),FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep,wake,state,FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.countSleepSession.apply(sleepingSessions);

        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(3,result);
    }

    //когда он пустой
    @Test
    void checkWorkFunctionCountSleepSessionsIsEmpty() {
        String[] logFileSleepTracker = {};

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(),FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(),FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep,wake,state,FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.countSleepSession.apply(sleepingSessions);

        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(0,result);
    }

    //тесты на проверку функции на максимальную продолжительность сессии (в минутах)
    //когда не пустая
    @Test
    void testMaxDurationSleepSessionIsNotEmpty() {
        String[] logFileSleepTracker = {
                "30.09.25 23:00;01.10.25 08:00;GOOD", //540 минут
                "01.10.25 19:00;02.10.25 05:00;GOOD", //600 минут
                "04.10.25 01:00;04.10.25 10:00;GOOD"  //540 минут
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(),FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(),FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep,wake,state,FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.maxDuractionSleep.apply(sleepingSessions);

        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(600,result);
    }

    //когда два одинаковых промежутка
    @Test
    void testMaxDurationSleepSessionIsSameDuration() {
        String[] logFileSleepTracker = {
                "30.09.25 23:00;01.10.25 08:00;GOOD", //540 минут
                "01.10.25 19:00;02.10.25 05:00;GOOD", //600 минут
                "04.10.25 19:00;05.10.25 05:00;GOOD"  //600 минут
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(),FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(),FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep,wake,state,FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.maxDuractionSleep.apply(sleepingSessions);

        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(600,result);
    }

    //когда пустой
    @Test
    void testMaxDurationSleepSessionIsEmpty() {
        String[] logFileSleepTracker = {};

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(),FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(),FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep,wake,state,FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.maxDuractionSleep.apply(sleepingSessions);

        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(0,result);
    }

    //тесты на проверку функции на минимальную продолжительность сессии (в минутах)
    //когда список не пуст
    @Test
    void testMinDurationSleepSessionIsNotEmpty() {
        String[] logFileSleepTracker = {
                "30.09.25 23:00;01.10.25 08:00;GOOD", //540 минут
                "01.10.25 23:00;02.10.25 01:30;BAD", //150 минут
                "04.10.25 19:00;05.10.25 05:00;GOOD"  //600 минут
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(),FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(),FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep,wake,state,FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.minDuractionSleep.apply(sleepingSessions);

        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(150,result);
    }

    //когда пустой
    @Test
    void testMinDurationSleepSessionIsEmpty() {
        String[] logFileSleepTracker = {};

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(),FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(),FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep,wake,state,FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.minDuractionSleep.apply(sleepingSessions);

        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(0,result);
    }

    //тесты на проверку функции на среднюю продолжительность сессии (в минутах)
    //когда список не пуст
    @Test
    void testAverageDurationSleepSessionIsNotEmpty() {
        String[] logFileSleepTracker = {
                "30.09.25 23:00;01.10.25 08:00;GOOD", //540 минут
                "01.10.25 23:00;02.10.25 01:30;BAD", //150 минут
                "04.10.25 19:00;05.10.25 05:00;GOOD"  //600 минут
                //1 290 / 3 = 430 минут
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(),FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(),FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep,wake,state,FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.averageDuractionSleep.apply(sleepingSessions);

        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(430,result);
    }

    //когда пустой
    @Test
    void testAverageDurationSleepSessionIsEmpty() {
        String[] logFileSleepTracker = {};

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(),FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(),FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep,wake,state,FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.averageDuractionSleep.apply(sleepingSessions);

        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(0,result);
    }

    //тесты на проверку функции на количество снов с плохим статусом
    // когда список не пуст
    @Test
    void testCountBadStatusSleepSession() {
        String[] logFileSleepTracker = {
                "30.09.25 23:00;01.10.25 08:00;GOOD",
                "01.10.25 23:00;02.10.25 01:30;BAD",
                "04.10.25 19:00;05.10.25 05:00;GOOD"
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }
            SleepAnalysisResult sleepAnalysisResult = analisusFunction.countBadSleepSession.apply(sleepingSessions);

            long result = 0;

            if (sleepAnalysisResult.getResult().isPresent()) result = (long)sleepAnalysisResult.getResult().get();
            assertEquals(1,result);
        }

    //когда сессий с плохим сном не было
    @Test
    void testWhenSleepSessionIsBadStatusNotExist() {
        String[] logFileSleepTracker = {
                "30.09.25 23:00;01.10.25 08:00;GOOD",
                "01.10.25 19:00;02.10.25 05:00;GOOD",
                "04.10.25 19:00;05.10.25 05:00;GOOD"
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.countBadSleepSession.apply(sleepingSessions);

        long result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (long)sleepAnalysisResult.getResult().get();
        assertEquals(0,result);
    }

    //когда список сессий пуст
    @Test
    void testWhenSleepSessionIsEmpty() {
        String[] logFileSleepTracker = {};

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.countBadSleepSession.apply(sleepingSessions);

        long result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (long)sleepAnalysisResult.getResult().get();
        assertEquals(0,result);
    }

    //проверка функции на количество бессонных ночей
    @Test
    void testNotSleepInSession() {
        //проверка, когда между двумя днями человек не спал ночь
        String[] logFileSleepTracker = {
                "30.09.25 23:00;01.10.25 08:00;GOOD",       //нормальный сон, который попадает в промежуток между 0:00 - 6:00
                "02.10.25 19:00;03.10.25 05:00;GOOD"        //следующая не бессонная сессия через два дня
                //ночь с 01.10.25 по 02.10.2025 - бессонная
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.countNightNotSleep.apply(sleepingSessions);
        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(1,result);

        //для следующего теста очищаем все сессии
        sleepingSessions.clear();

        //если таких ночей две
        logFileSleepTracker = new String[]{
                "30.09.25 23:00;01.10.25 08:00;GOOD",       //нормальный сон, который попадает в промежуток между 0:00 - 6:00
                "03.10.25 19:00;04.10.25 05:00;GOOD"        //следующая не бессонная сессия через три дня
                //ночь с 01.10.25 по 02.10.2025 - бессонная и ночь с 02.10.25 по 03.10.25
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }

        sleepAnalysisResult = analisusFunction.countNightNotSleep.apply(sleepingSessions);
        result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(2,result);
    }

    //когда бессонных ночей нет + проверка когда был и дневной сон и бессонная ночь
    @Test
    void testWhenNotSleepSessionIsNotExist() {
        String[] logFileSleepTracker = {
                "30.09.25 23:00;01.10.25 08:00;GOOD",
                "01.10.25 19:00;02.10.25 05:00;GOOD",
                "03.10.25 00:00;03.10.25 07:00;GOOD",
                "03.10.25 23:00;04.10.25 06:00;GOOD",
                "04.10.25 12:30;04.10.25 15:00;GOOD",       //в ночь 04.10-05.10 дневной сон
                "05.10.25 00:00;05.10.25 09:00;GOOD"        //ночь 04.10-05.10 не бессоная ночь
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.countNightNotSleep.apply(sleepingSessions);
        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(0,result);
    }

    //когда все сессии считаются бессонным (например, у человека сессия с:)
    @Test
    void testWhenAllSleepSessionIsNotSleeping() {
        String[] logFileSleepTracker = {
                "30.09.25 23:00;01.10.25 00:00;GOOD",       //ночь не попадает в промежуток - бессонная
                "03.10.25 06:00;03.10.25 12:00;GOOD",       //уснул после 6:00  - бессонная (02.10.25-03.10.25)
                "05.10.25 12:00;05.10.25 15:00;GOOD"        //дневной сон и больше нет сессии - бессонная (05.10.25-06.10.25)
                //ночи бессонные (нет в сессиях) - 01.10.25-02.10.25, 03.10.25-04.10.25, 04.10.25-05.10.25,
                // т.о. бессонных - 6
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.countNightNotSleep.apply(sleepingSessions);
        int result = 0;

        if (sleepAnalysisResult.getResult().isPresent()) result = (int)sleepAnalysisResult.getResult().get();
        assertEquals(6,result);
    }

    //тест на проверку функции на хронотипы
    //когда ночей для сов и жаворонков одинаковое количество
    @Test
    void testWhenEqualsLastEarlySleeper() {
        String[] logFileSleepTracker = {
                "30.09.25 23:30;01.10.25 09:30;GOOD",      //сова
                "03.10.25 05:00;03.10.25 12:00;GOOD",      //сова
                "05.10.25 21:00;06.10.25 05:00;GOOD",      //жаворонок
                "06.10.25 20:00;07.10.25 04:00;GOOD",      //жаворонок
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.determineChronotype.apply(sleepingSessions);
        String result = "";

        if (sleepAnalysisResult.getResult().isPresent()) result = sleepAnalysisResult.getResult().get().toString();
        assertEquals("голубь",result);
    }

    //когда ночей для сов больше
    @Test
    void testWhenMoreLastSleeper() {
        String[] logFileSleepTracker = {
                "30.09.25 23:30;01.10.25 09:30;GOOD",      //сова
                "03.10.25 05:00;03.10.25 12:00;GOOD",      //сова
                "03.10.25 23:30;04.10.25 10:00;GOOD",      //сова
                "05.10.25 06:30;05.10.25 12:30;GOOD",      //бессонная
                "05.10.25 21:00;06.10.25 05:00;GOOD",      //жаворонок
                "06.10.25 20:00;07.10.25 04:00;GOOD",      //жаворонок
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.determineChronotype.apply(sleepingSessions);
        String result = "";

        if (sleepAnalysisResult.getResult().isPresent()) result = sleepAnalysisResult.getResult().get().toString();
        assertEquals("сова",result);
    }

    //когда ночей для жаворонков больше
    @Test
    void testWhenMoreEarlySleeper() {
        String[] logFileSleepTracker = {
                "30.09.25 23:30;01.10.25 09:30;GOOD",      //сова
                "03.10.25 05:00;03.10.25 12:00;GOOD",      //сова
                "03.10.25 21:30;04.10.25 05:30;GOOD",      //жаворонок
                "05.10.25 06:30;05.10.25 12:30;GOOD",      //бессонная
                "05.10.25 21:00;06.10.25 05:00;GOOD",      //жаворонок
                "06.10.25 20:00;07.10.25 04:00;GOOD",      //жаворонок
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.determineChronotype.apply(sleepingSessions);
        String result = "";

        if (sleepAnalysisResult.getResult().isPresent()) result = sleepAnalysisResult.getResult().get().toString();
        assertEquals("жаворонок",result);
    }

    //когда больше смешанных ночей (не к сове, не к жаворонку)
    @Test
    void testWhenMoreIntermediateSleeper() {
        String[] logFileSleepTracker = {
                "30.09.25 23:30;01.10.25 04:30;GOOD",      //голубь
                "03.10.25 05:00;03.10.25 12:00;GOOD",      //сова
                "03.10.25 21:30;04.10.25 05:30;GOOD",      //жаворонок
                "05.10.25 06:30;05.10.25 12:30;GOOD",      //бессонная
                "05.10.25 21:00;06.10.25 10:00;GOOD",      //голубь
                "06.10.25 20:00;07.10.25 04:00;GOOD",      //жаворонок
                "08.10.25 00:00;08.10.25 06:00;GOOD"       //голубь
        };

        for (int i = 0; i < logFileSleepTracker.length; i++) {
            String[] parts = logFileSleepTracker[i].split(";");
            LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime wake = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepState state = SleepState.valueOf(parts[2].trim());

            sleepingSessions.add(new SleepingSession(sleep, wake, state, FORMATTER));
        }

        SleepAnalysisResult sleepAnalysisResult = analisusFunction.determineChronotype.apply(sleepingSessions);
        String result = "";

        if (sleepAnalysisResult.getResult().isPresent()) result = sleepAnalysisResult.getResult().get().toString();
        assertEquals("голубь",result);
    }
}
