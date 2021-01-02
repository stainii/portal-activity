package be.stijnhooft.portal.activity.domain;

import be.stijnhooft.portal.activity.utils.DateUtil;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@NoArgsConstructor
@Builder
@Data
public class DateInterval {
    private boolean infiniteStart;
    private Integer startDay;
    private Integer startMonth;
    private Integer startYear;

    private boolean infiniteEnd;
    private Integer endDay;
    private Integer endMonth;
    private Integer endYear;

    public DateInterval(boolean infiniteStart, Integer startDay, Integer startMonth, Integer startYear, boolean infiniteEnd, Integer endDay, Integer endMonth, Integer endYear) {
        this.infiniteStart = infiniteStart;
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.startYear = startYear;
        this.infiniteEnd = infiniteEnd;
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.endYear = endYear;

        if (infiniteStart && infiniteEnd
                && startDay == null && startMonth == null && startYear == null
                && endDay == null && endMonth == null && endYear == null) {
            return;
        }

        if (infiniteStart) {
            if (startDay != null || startMonth != null || startYear != null) {
                throw new IllegalArgumentException("Cannot combine an infinite start with a specific start day, month or year");
            }
            if (endDay == null || endMonth == null || endYear == null) {
                throw new IllegalArgumentException("When having a infinite start, you need to provide an end day, month and year");
            }
        } else {
            if (startDay == null || startMonth == null) {
                throw new IllegalArgumentException("You need to provide a start day and month, or mark start as infinite");
            }
            if (startYear == null && endYear != null) {
                throw new IllegalArgumentException("If you define an end year, a start year should also be defined");
            }
        }

        if (infiniteEnd) {
            if (endDay != null || endMonth != null || endYear != null) {
                throw new IllegalArgumentException("Cannot combine an infinite end with a specific end day, month or year");
            }
            if (startYear == null) {
                throw new IllegalArgumentException("When having a infinite end, you need to provide a start day, month and year");
            }
        } else {
            if (endDay == null || endMonth == null) {
                throw new IllegalArgumentException("You need to provide a end day and month, or mark end as infinite");
            }
            if (endYear == null && startYear != null) {
                throw new IllegalArgumentException("If you define a start year, an end year should also be defined");
            }
        }
    }

    public boolean covers(LocalDate requestedStartDate, LocalDate requestedEndDate) {
        if (infiniteStart && infiniteEnd) {
            return true;
        }

        if (infiniteStart) {
            return requestedEndDate.isBefore(LocalDate.of(endYear, endMonth, endDay).plus(1, DAYS));
        }

        if (infiniteEnd) {
            return requestedStartDate.isAfter(LocalDate.of(startYear, startMonth, startDay).minus(1, DAYS));
        }

        if (startYear == null && endYear == null) {
            if (requestedEndDate.getYear() - requestedStartDate.getYear() > 1) {
                return true;
            }

            boolean overlaps2Years = endMonth < startMonth || (endMonth.equals(startMonth) && endDay < startDay);
            if (overlaps2Years) {
                var commonDaysYear1 = determineCommonDays(requestedStartDate, requestedEndDate,
                        LocalDate.of(requestedStartDate.getYear() - 1, startMonth, startDay),
                        LocalDate.of(requestedStartDate.getYear(), endMonth, endDay));
                var commonDaysYear2 = determineCommonDays(requestedStartDate, requestedEndDate,
                        LocalDate.of(requestedStartDate.getYear(), startMonth, startDay),
                        LocalDate.of(requestedStartDate.getYear() + 1, endMonth, endDay));
                return !commonDaysYear1.isEmpty() || !commonDaysYear2.isEmpty();
            } else {
                var commonDays = determineCommonDays(requestedStartDate, requestedEndDate,
                        LocalDate.of(requestedStartDate.getYear(), startMonth, startDay),
                        LocalDate.of(requestedStartDate.getYear(), endMonth, endDay));
                return !commonDays.isEmpty();
            }
        }

        // no infinite dates, no years are null, just 2 completely filled in dates
        var commonDays = determineCommonDays(requestedStartDate, requestedEndDate, LocalDate.of(startYear, startMonth, startDay), LocalDate.of(endYear, endMonth, endDay));
        return !commonDays.isEmpty();
    }

    private Collection<LocalDate> determineCommonDays(LocalDate requestedStartDate, LocalDate requestedEndDate, LocalDate intervalStartDate, LocalDate intervalEndDate) {
        List<LocalDate> daysBetweenRequestedStartAndEndDate = DateUtil.getDaysBetween(requestedStartDate, requestedEndDate.plus(1, DAYS));
        List<LocalDate> daysBetweenIntervalStartAndEndDate = DateUtil.getDaysBetween(intervalStartDate, intervalEndDate.plus(1, DAYS));

        var commonDays = new ArrayList<>(daysBetweenIntervalStartAndEndDate);
        commonDays.retainAll(daysBetweenRequestedStartAndEndDate);
        return commonDays;
    }

}
