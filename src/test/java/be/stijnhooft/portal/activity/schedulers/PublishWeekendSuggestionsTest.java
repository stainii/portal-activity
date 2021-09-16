package be.stijnhooft.portal.activity.schedulers;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.mappers.SuggestionEventMapper;
import be.stijnhooft.portal.activity.messaging.EventPublisher;
import be.stijnhooft.portal.activity.searchparameters.DateSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.LocationSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.WeatherSearchParameter;
import be.stijnhooft.portal.activity.services.ActivitySearchService;
import be.stijnhooft.portal.activity.utils.DateUtil;
import be.stijnhooft.portal.model.domain.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PublishWeekendSuggestionsTest {

    public static final String LOCATION = "België";
    public static final int LOCATION_RADIUS = 15;

    private PublishWeekendSuggestions publishWeekendSuggestions;

    @Mock
    private ActivitySearchService activitySearchService;

    @Mock
    private SuggestionEventMapper suggestionEventMapper;

    @Mock
    private EventPublisher eventPublisher;

    @BeforeEach
    void init() {
        publishWeekendSuggestions = new PublishWeekendSuggestions(LOCATION, LOCATION_RADIUS, activitySearchService, suggestionEventMapper, eventPublisher);
    }

    @Test
    void publishWhenThereAreSuggestions() {
        // arrange
        List<LocalDate> nextWeekend = DateUtil.getNextWeekend();
        var dateSearchParameter = DateSearchParameter.create(nextWeekend.get(0), nextWeekend.get(1))
                .orElseThrow();
        var locationSearchParameter = LocationSearchParameter.create(LOCATION, LOCATION_RADIUS)
                .orElseThrow();
        var weatherSearchParameter = WeatherSearchParameter.create(true, nextWeekend.get(0), nextWeekend.get(1))
                .orElseThrow();
        var searchParameters = List.of(dateSearchParameter, locationSearchParameter, weatherSearchParameter);

        var activities = List.of(
                Activity.builder().id("1").build(),
                Activity.builder().id("2").build()
        );

        var events = List.of(
                Event.builder().flowId("1").source("test").publishDate(LocalDateTime.now()).data(new HashMap<>()).build(),
                Event.builder().flowId("2").source("test").publishDate(LocalDateTime.now()).data(new HashMap<>()).build()
        );

        when(activitySearchService.find(searchParameters)).thenReturn(activities);
        when(suggestionEventMapper.map(activities)).thenReturn(events);

        // act
        publishWeekendSuggestions.publishSuggestionsForTheWeekend();

        // assert
        verify(activitySearchService).find(searchParameters);
        verify(suggestionEventMapper).map(activities);
        verify(eventPublisher).publish(events);
        verifyNoMoreInteractions(activitySearchService, suggestionEventMapper, eventPublisher);
    }

    @Test
    void publishWhenThereNoSuggestions() {
        // arrange
        List<LocalDate> nextWeekend = DateUtil.getNextWeekend();
        var dateSearchParameter = DateSearchParameter.create(nextWeekend.get(0), nextWeekend.get(1))
                .orElseThrow();
        var locationSearchParameter = LocationSearchParameter.create(LOCATION, LOCATION_RADIUS)
                .orElseThrow();
        var weatherSearchParameter = WeatherSearchParameter.create(true, nextWeekend.get(0), nextWeekend.get(1))
                .orElseThrow();
        var searchParameters = List.of(dateSearchParameter, locationSearchParameter, weatherSearchParameter);

        when(activitySearchService.find(searchParameters)).thenReturn(new ArrayList<>());

        // act
        publishWeekendSuggestions.publishSuggestionsForTheWeekend();

        // assert
        verify(activitySearchService).find(searchParameters);
        verifyNoMoreInteractions(activitySearchService, suggestionEventMapper, eventPublisher);
    }

}
