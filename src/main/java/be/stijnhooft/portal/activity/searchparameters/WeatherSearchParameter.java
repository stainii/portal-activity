package be.stijnhooft.portal.activity.searchparameters;

import lombok.*;
import org.apache.commons.lang.BooleanUtils;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class WeatherSearchParameter implements SearchParameter {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public static Optional<WeatherSearchParameter> create(Boolean considerWeather, LocalDate startDate, LocalDate endDate) {
        if (BooleanUtils.isNotTrue(considerWeather) || startDate == null || endDate == null) {
            return Optional.empty();
        } else {
            return Optional.of(new WeatherSearchParameter(startDate, endDate));
        }
    }

}
