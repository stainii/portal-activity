package be.stijnhooft.portal.activity.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class DateUtilTest {

    @Test
    void getDaysBetweenWhenNormalScenario() {
        var dateTime1 = LocalDate.of(2020, 9, 4);
        var dateTime2 = LocalDate.of(2020, 9, 9);
        assertThat(DateUtil.getDaysBetween(dateTime1, dateTime2)).isEqualTo(List.of(
                LocalDate.of(2020, 9, 4),
                LocalDate.of(2020, 9, 5),
                LocalDate.of(2020, 9, 6),
                LocalDate.of(2020, 9, 7),
                LocalDate.of(2020, 9, 8)
        ));
    }

    @Test
    void getDaysBetweenWhenStartIsEqualToEnd() {
        var dateTime1 = LocalDate.of(2020, 9, 4);
        var dateTime2 = LocalDate.of(2020, 9, 4);
        assertThat(DateUtil.getDaysBetween(dateTime1, dateTime2)).isEqualTo(List.of(LocalDate.of(2020, 9, 4)));
    }

    @Test
    void getDaysBetweenWhenStartIsSmallerThanEnd() {
        var dateTime1 = LocalDate.of(2020, 9, 10);
        var dateTime2 = LocalDate.of(2020, 9, 1);
        assertThat(DateUtil.getDaysBetween(dateTime1, dateTime2)).isEmpty();
    }

}