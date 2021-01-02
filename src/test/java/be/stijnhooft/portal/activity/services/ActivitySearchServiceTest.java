package be.stijnhooft.portal.activity.services;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.domain.DateInterval;
import be.stijnhooft.portal.activity.domain.Location;
import be.stijnhooft.portal.activity.domain.Weather;
import be.stijnhooft.portal.activity.repositories.ActivityRepository;
import be.stijnhooft.portal.activity.searchparameters.DateSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.LabelSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.WeatherSearchParameter;
import be.stijnhooft.portal.model.weather.Forecast;
import be.stijnhooft.portal.model.weather.Temperature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * Kind of integration test: we use the real filter, but mock what contacts the outside world (repo's, services with rest templates, ...)
 * This keeps the tests to the point and easy to understand.
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
@SpringBootTest
class ActivitySearchServiceTest {

    @Autowired
    private ActivitySearchService activitySearchService;

    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private LocationService locationService;

    @Test
    void findWhenSomethingAppliesToAllFiltersThenReturnIt() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Dendermonde")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(15)
                        .build())
                .dateInterval(DateInterval.builder()
                        .startDay(1)
                        .startMonth(6)
                        .endDay(1)
                        .endMonth(9)
                        .build())
                .labels(List.of("test", "another label"))
                .build();

        var startDate = LocalDate.of(2021, 7, 1);
        var endDate = LocalDate.of(2021, 7, 1);

        var dateSearchParameter = DateSearchParameter.create(startDate, endDate).get();
        var labelSearchParameter = LabelSearchParameter.create(List.of("test")).get();
        var weatherSearchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        when(activityRepository.findAll()).thenReturn(List.of(activity));
        when(weatherService.findForecasts(Set.of("Dendermonde"), startDate, endDate)).thenReturn(List.of(Forecast.builder()
                .location("Dendermonde")
                .createdAt(LocalDateTime.now())
                .source("Test")
                .date(startDate)
                .temperature(Temperature.builder()
                        .feelsLike(20.0)
                        .build())
                .build()));

        // act
        var foundActivities = activitySearchService.find(List.of(dateSearchParameter, labelSearchParameter, weatherSearchParameter));

        // assert
        assertThat(foundActivities).containsOnly(activity);

        verify(activityRepository).findAll();
        verify(weatherService).findForecasts(Set.of("Dendermonde"), startDate, endDate);
    }

    @Test
    void findWhenSomethingDoesNotApplyToAnItemFilterThenDoNotReturnIt() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Dendermonde")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(15)
                        .build())
                .dateInterval(DateInterval.builder()
                        .startDay(1)
                        .startMonth(6)
                        .endDay(1)
                        .endMonth(9)
                        .build())
                .labels(List.of("test", "another label"))
                .build();

        // looking for a period in the winter, while the activity takes places only during summer
        var startDate = LocalDate.of(2021, 12, 1);
        var endDate = LocalDate.of(2021, 12, 1);

        var dateSearchParameter = DateSearchParameter.create(startDate, endDate).get();
        var labelSearchParameter = LabelSearchParameter.create(List.of("test")).get();
        var weatherSearchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        when(activityRepository.findAll()).thenReturn(List.of(activity));

        // act
        var foundActivities = activitySearchService.find(List.of(dateSearchParameter, labelSearchParameter, weatherSearchParameter));

        // assert
        assertThat(foundActivities).isEmpty();

        verify(activityRepository).findAll();
        verifyNoInteractions(weatherService);
    }

    @Test
    void findWhenSomethingDoesNotApplyToAListFilterThenDoNotReturnIt() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Dendermonde")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(15)
                        .build())
                .dateInterval(DateInterval.builder()
                        .startDay(1)
                        .startMonth(6)
                        .endDay(1)
                        .endMonth(9)
                        .build())
                .labels(List.of("test", "another label"))
                .build();

        var startDate = LocalDate.of(2021, 6, 1);
        var endDate = LocalDate.of(2021, 6, 1);

        var dateSearchParameter = DateSearchParameter.create(startDate, endDate).get();
        var labelSearchParameter = LabelSearchParameter.create(List.of("test")).get();
        var weatherSearchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        when(activityRepository.findAll()).thenReturn(List.of(activity));
        when(weatherService.findForecasts(Set.of("Dendermonde"), startDate, endDate)).thenReturn(List.of(Forecast.builder()
                .location("Dendermonde")
                .createdAt(LocalDateTime.now())
                .source("Test")
                .date(startDate)
                .temperature(Temperature.builder()
                        .feelsLike(0.0) // it's too cold for the activity!
                        .build())
                .build()));

        // act
        var foundActivities = activitySearchService.find(List.of(dateSearchParameter, labelSearchParameter, weatherSearchParameter));

        // assert
        assertThat(foundActivities).isEmpty();

        verify(activityRepository).findAll();
        verify(weatherService).findForecasts(Set.of("Dendermonde"), startDate, endDate);
    }

    @Test
    void findWithoutSearchParameters() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Dendermonde")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(15)
                        .build())
                .dateInterval(DateInterval.builder()
                        .startDay(1)
                        .startMonth(6)
                        .endDay(1)
                        .endMonth(9)
                        .build())
                .labels(List.of("test", "another label"))
                .build();

        when(activityRepository.findAll()).thenReturn(List.of(activity));

        // act
        var foundActivities = activitySearchService.find(new ArrayList<>());

        // assert
        assertThat(foundActivities).containsOnly(activity);

        verify(activityRepository).findAll();
        verifyNoInteractions(weatherService);
    }

    @Test
    void findWithOneSearchParameterThatOnlyAppliesToAnItemFilter() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Dendermonde")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(15)
                        .build())
                .dateInterval(DateInterval.builder()
                        .startDay(1)
                        .startMonth(6)
                        .endDay(1)
                        .endMonth(9)
                        .build())
                .labels(List.of("test", "another label"))
                .build();

        var labelSearchParameter = LabelSearchParameter.create(List.of("test")).get();

        when(activityRepository.findAll()).thenReturn(List.of(activity));

        // act
        var foundActivities = activitySearchService.find(List.of(labelSearchParameter));

        // assert
        assertThat(foundActivities).containsOnly(activity);

        verify(activityRepository).findAll();
        verifyNoInteractions(weatherService);
    }

    @Test
    void findWithOneSearchParameterThatOnlyAppliesToAListFilter() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Dendermonde")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(15)
                        .build())
                .dateInterval(DateInterval.builder()
                        .startDay(1)
                        .startMonth(6)
                        .endDay(1)
                        .endMonth(9)
                        .build())
                .labels(List.of("test", "another label"))
                .build();

        var startDate = LocalDate.of(2021, 6, 1);
        var endDate = LocalDate.of(2021, 6, 1);
        var weatherSearchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        when(activityRepository.findAll()).thenReturn(List.of(activity));

        // act
        var foundActivities = activitySearchService.find(List.of(weatherSearchParameter));

        // assert
        assertThat(foundActivities).containsOnly(activity);

        verify(activityRepository).findAll();
        verify(weatherService).findForecasts(Set.of("Dendermonde"), startDate, endDate);
    }

}