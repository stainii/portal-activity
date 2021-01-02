package be.stijnhooft.portal.activity.searchparameters;

import lombok.*;

import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class ParticipantsSearchParameter implements SearchParameter {

    private final int numberOfParticipants;

    public static Optional<ParticipantsSearchParameter> create(Integer numberOfParticipants) {
        if (numberOfParticipants == null) {
            return Optional.empty();
        } else {
            return Optional.of(new ParticipantsSearchParameter(numberOfParticipants));
        }
    }

}
