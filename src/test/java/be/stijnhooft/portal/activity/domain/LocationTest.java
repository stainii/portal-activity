package be.stijnhooft.portal.activity.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationTest {

    @Test
    public void toStringWhenAllFieldsAreFilledIn() {
        var location = Location.builder()
                .street("Zottekesstraat")
                .number("21")
                .city("Zottegem")
                .province("Oost-Vlaanderen")
                .country("België")
                .build();

        assertEquals("Zottekesstraat 21, Zottegem, Oost-Vlaanderen, België", location.toString());
    }

    @Test
    public void toStringWhenNoFieldsAreFilledIn() {
        var location = Location.builder()
                .build();

        assertEquals("", location.toString());
    }

    @Test
    public void toStringWhenOnlyCountryIsFilledIn() {
        var location = Location.builder()
                .country("België")
                .build();

        assertEquals("België", location.toString());
    }

    @Test
    public void toStringWhenOnlyCountryIsFilledInAndOtherFieldsAreEmptyStrings() {
        var location = Location.builder()
                .street("")
                .number("")
                .city("")
                .province("")
                .country("België")
                .build();

        assertEquals("België", location.toString());
    }

}
