package be.stijnhooft.portal.activity.searchparameters;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class LabelSearchParameter implements SearchParameter {

    private final List<String> labelsToLookFor;

    public static Optional<LabelSearchParameter> create(List<String> labelsToLookFor) {
        if (labelsToLookFor == null || labelsToLookFor.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new LabelSearchParameter(labelsToLookFor));
        }
    }

}
