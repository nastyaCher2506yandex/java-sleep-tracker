package ru.yandex.practicum.sleeptracker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

public class SleepTrackerApp {

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    static List<SleepAnalyzer> analyticFunction = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Вы не передали в аргументы консоли путь к логу умных часов");
            return;
        }

        //получение названия файла лога трекера сна умных часов
        String nameFile = args[0];

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(nameFile, StandardCharsets.UTF_8))) {
            List<SleepingSession> sleepTracker = bufferedReader.lines()
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        String[] parts = line.split(";");
                        LocalDateTime sleep = LocalDateTime.parse(parts[0].trim(),FORMATTER);
                        LocalDateTime wake = LocalDateTime.parse(parts[1].trim(),FORMATTER);
                        SleepState state = SleepState.valueOf(parts[2].trim());
                        return new SleepingSession(sleep,wake,state,FORMATTER);
                    })
                    .toList();

            //добавление всех функций в список
            createAndAddFunction();

            //пробегаемся по всему списку и получаем результаты анализа
            System.out.println("Результаты после анализа сессии сна: ");
            System.out.println();

            List<SleepAnalysisResult> sleepAnalysisResults = analyticFunction.stream()
                    .map(result -> {
                        return result.apply(sleepTracker);
                    })
                    .filter(result -> result != null && result.getResult().isPresent())
                    .peek(result -> {
                        if (result.getResult().isPresent()) System.out.println(result.getDescription()
                                + " - " + result.getResult().get());
                    }).toList();

        }  catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //создание и добавление всех функций
    public static void createAndAddFunction() {
        analyticFunction.add(new CountSessionsAnalyzer());
        analyticFunction.add(new MinDurationSessionsAnalyzer());
        analyticFunction.add(new MaxDurationSessionsAnalyzer());
        analyticFunction.add(new AverageDurationSessionsAnalyzer());
        analyticFunction.add(new CountBadStatusSessionsAnalyzer());
        analyticFunction.add(new CountNotSleepingSessionAnalyzer());
        analyticFunction.add(new DetermineChronotypeAnalyzer());
    }
}