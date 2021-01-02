package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.domain.Location;
import be.stijnhooft.portal.activity.searchparameters.LocationSearchParameter;
import be.stijnhooft.portal.activity.services.LocationService;
import be.stijnhooft.portal.model.location.Distance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@ExtendWith(SpringExtension.class)
class LocationListFilterTest {

    @InjectMocks
    private LocationListFilter filter;

    @Mock
    private LocationService locationService;

    @Test
    void applyWhenLiteralMatch() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .street("Voskeslaan")
                        .city("Ghent")
                        .province("Oost-Vlaanderen")
                        .country("Belgium")
                        .build())
                .build();
        var searchParameter = LocationSearchParameter.create("Ghent", 10).get();

        // act
        var result = filter.apply(List.of(activity), searchParameter);

        // assert
        verifyNoInteractions(locationService);

        assertEquals(1, result.size());
        assertTrue(result.contains(activity));
    }

    @Test
    void applyWhenLocationServiceRepliesWithADistanceInRange() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .street("Voskeslaan")
                        .city("Ghent")
                        .province("Oost-Vlaanderen")
                        .country("Belgium")
                        .build())
                .build();
        var searchParameter = LocationSearchParameter.create("Aalst", 30).get();

        when(locationService.findDistance("Aalst", Set.of("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium")))
                .thenReturn(List.of(Distance.builder()
                        .location1Query("Aalst")
                        .location2Query("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium")
                        .km(15)
                .build()));

        // act
        var result = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(locationService).findDistance("Aalst", Set.of("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium"));
        verifyNoMoreInteractions(locationService);

        assertEquals(1, result.size());
        assertTrue(result.contains(activity));
    }

    @Test
    void applyWhenLocationServiceRepliesWithADistanceNotInRange() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .street("Voskeslaan")
                        .city("Ghent")
                        .province("Oost-Vlaanderen")
                        .country("Belgium")
                        .build())
                .build();
        var searchParameter = LocationSearchParameter.create("Aalst", 5).get();

        when(locationService.findDistance("Aalst", Set.of("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium")))
                .thenReturn(List.of(Distance.builder()
                        .location1Query("Aalst")
                        .location2Query("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium")
                        .km(15)
                        .build()));

        // act
        var result = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(locationService).findDistance("Aalst", Set.of("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium"));
        verifyNoMoreInteractions(locationService);

        assertEquals(0, result.size());
    }

    @Test
    void applyWhenLocationServiceRepliesWithADistanceEqualsMaxRange() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .street("Voskeslaan")
                        .city("Ghent")
                        .province("Oost-Vlaanderen")
                        .country("Belgium")
                        .build())
                .build();
        var searchParameter = LocationSearchParameter.create("Aalst", 30).get();

        when(locationService.findDistance("Aalst", Set.of("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium"))).thenReturn(List.of(Distance.builder()
                .location1Query("Aalst")
                .location2Query("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium")
                .km(30)
                .build()));

        // act
        var result = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(locationService).findDistance("Aalst", Set.of("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium"));
        verifyNoMoreInteractions(locationService);

        assertEquals(1, result.size());
        assertTrue(result.contains(activity));
    }

    @Test
    void applyWhenLocationServiceReturnsEmpty() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .street("Voskeslaan")
                        .city("Ghent")
                        .province("Oost-Vlaanderen")
                        .country("Belgium")
                        .build())
                .build();
        var searchParameter = LocationSearchParameter.create("Aalst", 30).get();

        when(locationService.findDistance("Aalst", Set.of("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium")))
                .thenReturn(new ArrayList<>());

        // act
        var result = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(locationService).findDistance("Aalst", Set.of("Voskeslaan, Ghent, Oost-Vlaanderen, Belgium"));
        verifyNoMoreInteractions(locationService);

        assertEquals(0, result.size());
    }

    @Test
    void applyWhenActivityHasNoLocationDefined() {
        // arrange
        var activity = Activity.builder()
                .build();
        var searchParameter = LocationSearchParameter.create("Aalst", 30).get();

        // act
        var result = filter.apply(List.of(activity), searchParameter);

        // assert
        verifyNoInteractions(locationService);

        assertEquals(0, result.size());
    }

    @Test
    void applyWhenActivityHasEmptyLocationDefined() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder().build())
                .build();
        var searchParameter = LocationSearchParameter.create("Aalst", 30).get();

        // act
        var result = filter.apply(List.of(activity), searchParameter);

        // assert
        verifyNoInteractions(locationService);

        assertEquals(0, result.size());
    }

}