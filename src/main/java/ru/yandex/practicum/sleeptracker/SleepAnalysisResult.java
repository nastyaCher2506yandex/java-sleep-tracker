package ru.yandex.practicum.sleeptracker;

import java.util.Objects;
import java.util.Optional;

public class SleepAnalysisResult<T>  {

    private String description;
    private Optional<T> result;

    SleepAnalysisResult(String description, Optional<T> result) {
        this.description = description;
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public Optional<T> getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SleepAnalysisResult that = (SleepAnalysisResult) o;
        return Objects.equals(result, that.result)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, result);
    }
}
