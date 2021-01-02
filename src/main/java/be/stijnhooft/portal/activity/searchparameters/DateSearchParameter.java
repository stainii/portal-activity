package be.stijnhooft.portal.activity.searchparameters;

import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class DateSearchParameter implements SearchParameter {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public static Optional<DateSearchParameter> create(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Optional.empty();
        } else {
            return Optional.of(new DateSearchParameter(startDate, endDate));
        }
    }

}
