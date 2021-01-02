package be.stijnhooft.portal.activity.factory;

import org.springframework.data.domain.Sort;

import java.util.Optional;

public class SortFactory {

    public static Optional<Sort> create(String column, String direction) {
        if (column == null || direction == null) {
            return Optional.empty();
        }
        return switch (direction.toUpperCase()) {
            case "ASC" -> Optional.of(Sort.by(column).ascending());
            case "DESC" -> Optional.of(Sort.by(column).descending());
            default -> throw new UnsupportedOperationException("Cannot create a sort with direction " + direction);
        };
    }

}
