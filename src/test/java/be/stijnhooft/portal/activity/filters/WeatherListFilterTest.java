package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.domain.Location;
import be.stijnhooft.portal.activity.domain.Weather;
import be.stijnhooft.portal.activity.searchparameters.ParticipantsSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.WeatherSearchParameter;
import be.stijnhooft.portal.activity.services.WeatherService;
import be.stijnhooft.portal.model.weather.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@ExtendWith(SpringExtension.class)
class WeatherListFilterTest {

    @InjectMocks
    private WeatherListFilter filter;

    @Mock
    private WeatherService weatherService;

    @Test
    void applyWhenActivityHasNoWeatherConditionsDefined() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verifyNoInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityHasNoLocationDefined() {
        // arrange
        var activity = Activity.builder()
                .weather(Weather.builder()
                        .minTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verifyNoInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenAllForecastDaysHaveGoodWeather() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecastDay1 = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .feelsLike(10.0)
                        .build())
                .build();
        var forecastDay2 = Forecast.builder()
                .location("Zottegem")
                .date(endDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .feelsLike(15.0)
                        .build())
                .build();
        var forecasts = List.of(forecastDay1, forecastDay2);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenAllForecastDaysHaveUnknownWeather() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        doReturn(new ArrayList<>()).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenOneForecastDayHasBadWeatherAndOthersAreOk() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecastDay1 = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .feelsLike(10.0)
                        .build())
                .build();
        var forecastDay2 = Forecast.builder()
                .location("Zottegem")
                .date(endDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .feelsLike(5.0)
                        .build())
                .build();
        var forecasts = List.of(forecastDay1, forecastDay2);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenFeelsLikeIsFilledInAndTooLowTemperature() {
        // arrange
        var sunnyActivity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(20)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .feelsLike(10.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(sunnyActivity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenFeelsLikeIsNotFilledInAndTooLowTemperature() {
        // arrange
        var sunnyActivity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(20)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .minTemperature(10.0)
                        .maxTemperature(15.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(sunnyActivity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenFeelsLikeIsFilledInAndTooHighTemperature() {
        // arrange
        var winterActivity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .feelsLike(20.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(winterActivity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenFeelsLikeIsNotFilledInAndTooHighTemperature() {
        // arrange
        var winterActivity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .minTemperature(5.0) // cold night, but it doesn't matter
                        .maxTemperature(20.0) // because the day is hot!
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(winterActivity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenFeelsLikeIsFilledInAndTemperatureMatchesMinTemperature() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .feelsLike(10.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenFeelsLikeIsNotFilledInAndTemperatureMatchesMinTemperature() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .minTemperature(8.0)
                        .maxTemperature(10.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenFeelsLikeIsFilledInTemperatureMatchesMaxTemperature() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .feelsLike(10.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenFeelsLikeIsNotFilledInTemperatureMatchesMaxTemperature() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .minTemperature(10.0)
                        .maxTemperature(10.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityHasTemperatureConditionsButForecastContainsNoTemperaturePrediction() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxTemperature(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityContainsNoTemperatureConditions() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                    .minWind(2)
                    .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .temperature(Temperature
                        .builder()
                        .minTemperature(10.0)
                        .maxTemperature(10.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenRainIsOk() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxRain(50)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.RAIN)
                        .probability(50)
                        .intensity(50)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenTooMuchRain() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxRain(50)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.RAIN)
                        .probability(90)
                        .intensity(100)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenMaxRain() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxRain(25)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.RAIN)
                        .probability(50)
                        .intensity(50)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityHasRainConditionsButForecastContainsNoRainPrediction() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxRain(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityContainsNoRainConditions() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minWind(2)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.RAIN)
                        .intensity(50)
                        .probability(50.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenCloudinessIsOk() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxCloudiness(50)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .cloudiness(10)
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenTooManyClouds() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxCloudiness(50)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .cloudiness(60)
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenMaxCloudiness() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxCloudiness(50)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .cloudiness(50)
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityHasCloudinessConditionsButForecastContainsNoCloudinessPrediction() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxCloudiness(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityContainsNoCloudinessConditions() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minWind(2)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .cloudiness(50)
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenFogIsOk() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxFog(50)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.FOG)
                        .probability(50)
                        .intensity(50)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenTooMuchFog() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxFog(50)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.FOG)
                        .probability(80)
                        .intensity(70)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenMaxFog() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxFog(25)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.FOG)
                        .probability(50)
                        .intensity(50)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityHasFogConditionsButForecastContainsNoFogPrediction() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxFog(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityContainsNoFogConditions() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minWind(2)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.FOG)
                        .intensity(50)
                        .probability(50.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenSnowIsOk() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxSnow(50)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.SNOW)
                        .probability(50)
                        .intensity(50)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenTooMuchSnow() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxSnow(50)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.SNOW)
                        .probability(90)
                        .intensity(60)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenMaxSnow() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxSnow(25)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.SNOW)
                        .probability(50)
                        .intensity(50)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityHasSnowConditionsButForecastContainsNoSnowPrediction() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .maxSnow(10)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityContainsNoSnowConditions() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minWind(2)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .precipitation(Precipitation.builder()
                        .type(PrecipitationType.SNOW)
                        .intensity(50)
                        .probability(50.0)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenWindIsOk() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minWind(0)
                        .maxWind(3)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .wind(Wind.builder()
                        .beaufort(2)
                        .direction(WindDirection.EAST)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenTooMuchWind() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minWind(0)
                        .maxWind(3)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .wind(Wind.builder()
                        .beaufort(5)
                        .direction(WindDirection.EAST)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenTooLittleWind() {
        // arrange
        var sailingActivity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minWind(2)
                        .maxWind(5)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .wind(Wind.builder()
                        .beaufort(0)
                        .direction(WindDirection.EAST)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(sailingActivity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(0, filteredActivities.size());
    }

    @Test
    void applyWhenMinWind() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minWind(1)
                        .maxWind(3)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .wind(Wind.builder()
                        .beaufort(1)
                        .direction(WindDirection.EAST)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenMaxWind() {
            // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minWind(0)
                        .maxWind(3)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .wind(Wind.builder()
                        .beaufort(3)
                        .direction(WindDirection.EAST)
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityHasWindConditionsButForecastContainsNoWindPrediction() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minWind(3)
                        .maxWind(5)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void applyWhenActivityContainsNoWindConditions() {
        // arrange
        var activity = Activity.builder()
                .location(Location.builder()
                        .city("Zottegem")
                        .build())
                .weather(Weather.builder()
                        .minTemperature(20)
                        .build())
                .build();

        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var searchParameter = WeatherSearchParameter.create(true, startDate, endDate).get();

        var forecast = Forecast.builder()
                .location("Zottegem")
                .date(startDate)
                .source("test")
                .createdAt(LocalDateTime.now())
                .wind(Wind.builder()
                        .beaufort(3)
                        .direction(WindDirection.EAST)
                        .build())
                .build();
        var forecasts = List.of(forecast);

        doReturn(forecasts).when(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);

        // act
        Collection<Activity> filteredActivities = filter.apply(List.of(activity), searchParameter);

        // assert
        verify(weatherService).findForecasts(Set.of("Zottegem"), startDate, endDate);
        verifyNoMoreInteractions(weatherService);

        assertEquals(1, filteredActivities.size());
        assertTrue(filteredActivities.contains(activity));
    }

    @Test
    void supportsWhenTrue() {
        assertTrue(filter.supports(WeatherSearchParameter.create(true, LocalDate.now(), LocalDate.now()).get()));
    }

    @Test
    void supportsWhenFalse() {
        assertFalse(filter.supports(ParticipantsSearchParameter.create(10).get()));
    }

}