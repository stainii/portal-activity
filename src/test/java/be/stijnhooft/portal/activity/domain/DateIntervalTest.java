package be.stijnhooft.portal.activity.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateIntervalTest {

    @Test
    void buildInvalidWhenCombiningInfiniteStartWithStartDay() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .startDay(1)
                .infiniteEnd(true)
                .build());
    }

    @Test
    void buildInvalidWhenCombiningInfiniteStartWithStartMonth() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .startMonth(1)
                .infiniteEnd(true)
                .build());
    }

    @Test
    void buildInvalidWhenCombiningInfiniteStartWithStartYear() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .startYear(1)
                .infiniteEnd(true)
                .build());
    }

    @Test
    void buildInvalidWhenHavingAnInfiniteStartButNoEndDay() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .endMonth(1)
                .endYear(1)
                .build());
    }

    @Test
    void buildInvalidWhenHavingAnInfiniteStartButNoEndMonth() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .endYear(1)
                .build());
    }

    @Test
    void buildInvalidWhenHavingAnInfiniteStartButNoEndYear() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .endDay(1)
                .endMonth(1)
                .build());
    }

    @Test
    void buildInvalidWhenCombiningInfiniteEndWithEndDay() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .infiniteEnd(true)
                .endDay(1)
                .build());
    }

    @Test
    void buildInvalidWhenCombiningInfiniteEndWithEndMonth() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .infiniteEnd(true)
                .endMonth(1)
                .build());
    }

    @Test
    void buildInvalidWhenCombiningInfiniteEndWithEndYear() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .infiniteEnd(true)
                .endYear(1)
                .build());
    }

    @Test
    void buildInvalidWhenHavingAnInfiniteEndButNoStartDay() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .startMonth(1)
                .startYear(1)
                .infiniteEnd(true)
                .build());
    }

    @Test
    void buildInvalidWhenHavingAnInfiniteEndButNoStartMonth() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .startYear(1)
                .infiniteEnd(true)
                .build());
    }

    @Test
    void buildInvalidWhenHavingAnInfiniteEndButNoStartYear() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .startDay(1)
                .startMonth(1)
                .infiniteEnd(true)
                .build());
    }

    @Test
    void buildInvalidWhenCombiningStartWithEndYearWithEndWithoutEndYear() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .startDay(1)
                .startMonth(1)
                .startYear(1)
                .endDay(1)
                .endMonth(1)
                .build());
    }

    @Test
    void buildInvalidWhenCombiningStartWithoutEndYearWithEndWithEndYear() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .startDay(1)
                .startMonth(1)
                .endDay(1)
                .endMonth(1)
                .endYear(1)
                .build());
    }

    @Test
    void buildInvalidWhenProvidingNoStartInformation() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteEnd(true)
                .build());
    }

    @Test
    void buildInvalidWhenProvidingNoEndInformation() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .build());
    }

    @Test
    void buildInvalidWhenProvidingStartDayButNotStartMonth() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .startDay(1)
                .infiniteEnd(true)
                .build());
    }

    @Test
    void buildInvalidWhenProvidingStartMonthButNotStartDay() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .startMonth(1)
                .infiniteEnd(true)
                .build());
    }

    @Test
    void buildInvalidWhenProvidingEndDayButNotEndMonth() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .endDay(1)
                .build());
    }

    @Test
    void buildInvalidWhenProvidingEndMonthButNotEndDay() {
        assertThrows(IllegalArgumentException.class, () -> DateInterval.builder()
                .infiniteStart(true)
                .endMonth(1)
                .build());
    }

    @Test
    void coversWhenInfiniteStartAndEndDateThenTrue() {
        var requestedStartDate = LocalDate.now().minus(1, DAYS);
        var requestedEndDate = LocalDate.now();

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(true)
                .infiniteEnd(true)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void coversWhenInfiniteStartDateAndIntervalOverlapsWithRequestedThenTrue() {
        var requestedStartDate = LocalDate.of(2020, 10, 10);
        var requestedEndDate = LocalDate.of(2020, 10, 12);

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(true)
                .endDay(13)
                .endMonth(10)
                .endYear(2020)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void coversWhenInfiniteStartDateAndIntervalBeforeRequestedThenFalse() {
        var requestedStartDate = LocalDate.of(2020, 10, 10);
        var requestedEndDate = LocalDate.of(2020, 10, 12);

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(true)
                .endDay(9)
                .endMonth(10)
                .endYear(2020)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isFalse();
    }


    @Test
    void coversWhenInfiniteEndDateAndIntervalOverlapsWithRequestedThenTrue() {
        var requestedStartDate = LocalDate.of(2020, 10, 10);
        var requestedEndDate = LocalDate.of(2020, 10, 12);

        DateInterval dateInterval = DateInterval.builder()
                .startDay(30)
                .startMonth(9)
                .startYear(2020)
                .infiniteEnd(true)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void coversWhenInfiniteEndDateAndIntervalAfterRequestedThenFalse() {
        var requestedStartDate = LocalDate.of(2020, 10, 10);
        var requestedEndDate = LocalDate.of(2020, 10, 12);

        DateInterval dateInterval = DateInterval.builder()
                .startDay(1)
                .startMonth(12)
                .startYear(2020)
                .infiniteEnd(true)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isFalse();
    }

    @Test
    void coversWhenIntervalOverlapsWithRequestedThenTrue() {
        var requestedStartDate = LocalDate.now();
        var requestedEndDate = LocalDate.now().plus(10, DAYS);

        var intervalStartDate = LocalDate.now().minus(1, DAYS);
        var intervalEndDate = LocalDate.now().plus(1, DAYS);

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(false)
                .infiniteEnd(false)
                .startDay(intervalStartDate.getDayOfMonth())
                .startMonth(intervalStartDate.getMonthValue())
                .endDay(intervalEndDate.getDayOfMonth())
                .endMonth(intervalEndDate.getMonthValue())
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void coversWhenIntervalBeforeRequestedThenFalse() {
        var requestedStartDate = LocalDate.of(2021, 10, 1);
        var requestedEndDate = LocalDate.of(2021, 10, 7);

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(false)
                .infiniteEnd(false)
                .startDay(1)
                .startMonth(6)
                .endDay(31)
                .endMonth(7)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isFalse();
    }

    @Test
    void coversWhenIntervalAfterRequestedThenFalse() {
        var requestedStartDate = LocalDate.of(2021, 10, 10);
        var requestedEndDate = LocalDate.of(2021, 10, 21);

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(false)
                .infiniteEnd(false)
                .startDay(1)
                .startMonth(11)
                .endDay(1)
                .endMonth(12)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isFalse();
    }

    @Test
    void coversWhenIntervalIntervalEndDateOverlapsWithRequestedStartDateThenTrue() {
        var requestedStartDate = LocalDate.now();
        var requestedEndDate = LocalDate.now().plus(10, DAYS);

        var intervalStartDate = LocalDate.now().minus(1, DAYS);
        var intervalEndDate = LocalDate.now();

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(false)
                .infiniteEnd(false)
                .startDay(intervalStartDate.getDayOfMonth())
                .startMonth(intervalStartDate.getMonthValue())
                .endDay(intervalEndDate.getDayOfMonth())
                .endMonth(intervalEndDate.getMonthValue())
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void coversWhenIntervalIntervalStartDateOverlapsWithRequestedEndDateThenTrue() {
        var requestedStartDate = LocalDate.now();
        var requestedEndDate = LocalDate.now().plus(10, DAYS);

        var intervalStartDate = LocalDate.now().plus(10, DAYS);
        var intervalEndDate = LocalDate.now().plus(20, DAYS);

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(false)
                .infiniteEnd(false)
                .startDay(intervalStartDate.getDayOfMonth())
                .startMonth(intervalStartDate.getMonthValue())
                .startYear(intervalStartDate.getYear())
                .endDay(intervalEndDate.getDayOfMonth())
                .endMonth(intervalEndDate.getMonthValue())
                .endYear(intervalEndDate.getYear())
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void doesNotTakeYearIntoAccountWhenItsNotFilledIn() {
        // requested date is next year. Should not be a problem, the interval date does not have a year specified
        var requestedStartDate = LocalDate.now().plus(1, YEARS);
        var requestedEndDate = LocalDate.now().plus(1, YEARS).plus(10, DAYS);

        var intervalStartDate = LocalDate.now();
        var intervalEndDate = LocalDate.now().plus(20, DAYS);

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(false)
                .infiniteEnd(false)
                .startDay(intervalStartDate.getDayOfMonth())
                .startMonth(intervalStartDate.getMonthValue())
                .endDay(intervalEndDate.getDayOfMonth())
                .endMonth(intervalEndDate.getMonthValue())
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void doesTakeYearIntoAccountWhenItsFilledIn() {
        // requested date is next year. The interval only applies to this year, however.
        var requestedStartDate = LocalDate.now().plus(1, YEARS);
        var requestedEndDate = LocalDate.now().plus(1, YEARS).plus(10, DAYS);

        var intervalStartDate = LocalDate.now();
        var intervalEndDate = LocalDate.now().plus(20, DAYS);

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(false)
                .infiniteEnd(false)
                .startDay(intervalStartDate.getDayOfMonth())
                .startMonth(intervalStartDate.getMonthValue())
                .startYear(intervalStartDate.getYear())
                .endDay(intervalEndDate.getDayOfMonth())
                .endMonth(intervalEndDate.getMonthValue())
                .endYear(intervalStartDate.getYear())
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isFalse();
    }

    @Test
    void doesTakeYearOverlapsIntoAccountWhenYearIsNotFilledInCase1() {
        // requested date is next year. The interval only applies to this year, however.
        var requestedStartDate = LocalDate.of(2021, 1, 1);
        var requestedEndDate = LocalDate.of(2021, 1, 1);

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(false)
                .infiniteEnd(false)
                .startDay(1)
                .startMonth(12)
                .endDay(1)
                .endMonth(3)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void doesTakeYearOverlapsIntoAccountWhenYearIsNotFilledInCase2() {
        // requested date is next year. The interval only applies to this year, however.
        var requestedStartDate = LocalDate.of(2020, 12, 1);
        var requestedEndDate = LocalDate.of(2020, 12, 5);

        DateInterval dateInterval = DateInterval.builder()
                .infiniteStart(false)
                .infiniteEnd(false)
                .startDay(1)
                .startMonth(12)
                .endDay(1)
                .endMonth(3)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void doesTakeYearOverlapsIntoAccountWhenYearIsFilledIn() {
        // requested date is next year. The interval only applies to this year, however.
        var requestedStartDate = LocalDate.of(2021, 1, 1);
        var requestedEndDate = LocalDate.of(2021, 1, 1);

        DateInterval dateInterval = DateInterval.builder()
                .startDay(1)
                .startMonth(12)
                .startYear(2020)
                .endDay(31)
                .endMonth(3)
                .endYear(2021)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void coversIsTrueWhenIntervalHasNoYearsDefinedAndRequestSpansMultipleYears() {
        // requested date is next year. The interval only applies to this year, however.
        var requestedStartDate = LocalDate.of(2021, 1, 1);
        var requestedEndDate = LocalDate.of(2025, 1, 1);

        DateInterval dateInterval = DateInterval.builder()
                .startDay(1)
                .startMonth(6)
                .endDay(1)
                .endMonth(9)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isTrue();
    }

    @Test
    void coversIsFalseWhenIntervalHasYearsDefinedAndRequestSpansMultipleYearsButAfterInterval() {
        // requested date is next year. The interval only applies to this year, however.
        var requestedStartDate = LocalDate.of(2021, 1, 1);
        var requestedEndDate = LocalDate.of(2025, 1, 1);

        DateInterval dateInterval = DateInterval.builder()
                .startDay(1)
                .startMonth(6)
                .startYear(1900)
                .endDay(1)
                .endMonth(9)
                .endYear(1990)
                .build();

        assertThat(dateInterval.covers(requestedStartDate, requestedEndDate)).isFalse();
    }

}