package be.stijnhooft.portal.activity.utils;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class DateUtil {

    public static List<LocalDate> getDaysBetween(@NotNull LocalDate inclusiveStart, @NotNull LocalDate exclusiveEnd) {
        List<LocalDate> result = new ArrayList<>();

        if (exclusiveEnd.isBefore(inclusiveStart)) {
            return result;
        }

        LocalDate currentDay = inclusiveStart;
        do {
            result.add(currentDay);
            currentDay = currentDay.plus(1, DAYS);
        } while (currentDay.isBefore(exclusiveEnd));

        return result;
    }

    public static List<LocalDate> getNextWeekend() {
        var saturday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        var sunday = saturday.plus(1, DAYS);
        return List.of(saturday, sunday);
    }

}
