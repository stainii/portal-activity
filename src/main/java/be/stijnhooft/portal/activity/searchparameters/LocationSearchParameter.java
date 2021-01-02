package be.stijnhooft.portal.activity.searchparameters;

import lombok.*;

import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class LocationSearchParameter implements SearchParameter {

    private final String name;
    private final int radiusInKm;

    public static Optional<LocationSearchParameter> create(String location, Integer locationRadiusInKm) {
        if (location == null) {
            return Optional.empty();
        }

        if (locationRadiusInKm == null) {
            locationRadiusInKm = 15;
        }
        return Optional.of(new LocationSearchParameter(location, locationRadiusInKm));
    }

}
