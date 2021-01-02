package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.domain.DateInterval;
import be.stijnhooft.portal.activity.searchparameters.DateSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.ParticipantsSearchParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class DateItemFilterTest {

    private DateItemFilter filter;

    @BeforeEach
    void init() {
        filter = new DateItemFilter();
    }

    @Test
    void applyWhenSearchingForDateThisYearAndTrue() {
        var dateSearchParameterStartDate = LocalDate.now();
        var dateSearchParameterEndDate = LocalDate.now();
        var dateSearchParameter = DateSearchParameter.create(dateSearchParameterStartDate, dateSearchParameterEndDate);

        var interval1 = mock(DateInterval.class);
        var interval2 = mock(DateInterval.class);

        var activity = Activity.builder()
                .dateIntervals(List.of(interval1, interval2))
                .build();

        when(interval1.covers(dateSearchParameterStartDate, dateSearchParameterEndDate)).thenReturn(false);
        when(interval2.covers(dateSearchParameterStartDate, dateSearchParameterEndDate)).thenReturn(true);

        assertThat(filter.apply(activity, dateSearchParameter.get())).isTrue();
    }

    @Test
    void applyWhenFalse() {
        var dateSearchParameterStartDate = LocalDate.now();
        var dateSearchParameterEndDate = LocalDate.now();
        var dateSearchParameter = DateSearchParameter.create(dateSearchParameterStartDate, dateSearchParameterEndDate);

        var interval1 = mock(DateInterval.class);
        var interval2 = mock(DateInterval.class);

        var activity = Activity.builder()
                .dateIntervals(List.of(interval1, interval2))
                .build();

        when(interval1.covers(dateSearchParameterStartDate, dateSearchParameterEndDate)).thenReturn(false);
        when(interval2.covers(dateSearchParameterStartDate, dateSearchParameterEndDate)).thenReturn(false);

        assertThat(filter.apply(activity, dateSearchParameter.get())).isFalse();
    }

    @Test
    void applyWhenNoIntervalsDefined() {
        var dateSearchParameterStartDate = LocalDate.now();
        var dateSearchParameterEndDate = LocalDate.now();
        var dateSearchParameter = DateSearchParameter.create(dateSearchParameterStartDate, dateSearchParameterEndDate);

        var activity = Activity.builder()
                .dateIntervals(new ArrayList<>())
                .build();

        assertThat(filter.apply(activity, dateSearchParameter.get())).isTrue();
    }

    @Test
    void supportsWhenTrue() {
        assertThat(filter.supports(DateSearchParameter.create(LocalDate.now(), LocalDate.now()).get())).isTrue();
    }

    @Test
    void supportsWhenFalse() {
        assertThat(filter.supports(ParticipantsSearchParameter.create(5).get())).isFalse();
    }

}