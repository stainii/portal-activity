package be.stijnhooft.portal.activity.domain;

import lombok.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {

    private String street;
    private String number;
    private String city;
    private String province;
    private String country;

    public boolean literalMatch(@NonNull String userInput) {
        return userInput.equals(city)
                || userInput.equals(province)
                || userInput.equals(country);
    }

    public String toString() {
        return Stream.of(Stream.ofNullable(street),
                Stream.ofNullable(number),
                Stream.ofNullable(city),
                Stream.ofNullable(province),
                Stream.ofNullable(country))
                .reduce(Stream::concat)
                .orElseGet(Stream::empty)
                .collect(Collectors.joining(", "));
    }
}
