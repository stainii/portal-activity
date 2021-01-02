package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.searchparameters.SearchParameter;
import be.stijnhooft.portal.activity.searchparameters.WeatherSearchParameter;
import be.stijnhooft.portal.activity.services.WeatherService;
import be.stijnhooft.portal.model.weather.Forecast;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class WeatherListFilter implements ListFilter {

    private final WeatherService weatherService;

    @Override
    public Collection<Activity> apply(Collection<Activity> activities, SearchParameter searchParameter) {
        return apply(activities, (WeatherSearchParameter) searchParameter);
    }

    public Collection<Activity> apply(Collection<Activity> activities, WeatherSearchParameter weatherSearchParameter) {
        var activitiesThatCanBeChecked = activities.stream()
                .filter(activity -> activity.getLocation() != null)
                .filter(activity -> activity.getWeather() != null)
                .collect(Collectors.toList());

        if (activitiesThatCanBeChecked.isEmpty()) {
            return new ArrayList<>(activities);
        }

        var forecasts = findForecasts(weatherSearchParameter, activitiesThatCanBeChecked);

        var filteredActivities = activitiesThatCanBeChecked.stream()
                .filter(activity -> isTheWeatherOkOrUnknown(activity, forecasts))
                .collect(Collectors.toList());

        var activitiesThatCannotBeChecked = CollectionUtils.disjunction(activities, activitiesThatCanBeChecked);
        return CollectionUtils.union(filteredActivities, activitiesThatCannotBeChecked);
    }

    private Collection<Forecast> findForecasts(WeatherSearchParameter weatherSearchParameter, List<Activity> activitiesWithWeatherConditionsAndLocation) {
        var locations = activitiesWithWeatherConditionsAndLocation.stream()
                .map(activity -> activity.getLocation().toString())
                .collect(Collectors.toSet());

        var startDate = weatherSearchParameter.getStartDate();
        var endDate = weatherSearchParameter.getEndDate();

        return weatherService.findForecasts(locations, startDate, endDate);
    }

    private boolean isTheWeatherOkOrUnknown(Activity activity, Collection<Forecast> allForecasts) {
        var forecastsOfActivity = allForecasts.stream()
                .filter(forecast -> forecast.getLocation().equals(activity.getLocation().toString()))
                .collect(Collectors.toList());

        return forecastsOfActivity.stream()
                .allMatch(forecast -> isTheWeatherOkOrUnknown(activity, forecast));
    }

    private boolean isTheWeatherOkOrUnknown(Activity activity, Forecast forecast) {
        var weatherConditions = activity.getWeather();

        // temperature
        if (forecast.getTemperature() != null) {
            double actualTemperature = Optional.ofNullable(forecast.getTemperature().getFeelsLike()).orElse(forecast.getTemperature().getMaxTemperature());
            if (weatherConditions.getMinTemperature() != null && weatherConditions.getMinTemperature() > actualTemperature) {
                return false;
            }
            if (weatherConditions.getMaxTemperature() != null && weatherConditions.getMaxTemperature() < actualTemperature) {
                return false;
            }
        }

        // wind
        if (forecast.getWind() != null && weatherConditions.getMinWind() != null && forecast.getWind().getBeaufort() < weatherConditions.getMinWind()) {
            return false;
        }
        if (forecast.getWind() != null && weatherConditions.getMaxWind() != null && forecast.getWind().getBeaufort() > weatherConditions.getMaxWind()) {
            return false;
        }

        // cloudiness
        if (forecast.getCloudiness() != null && weatherConditions.getMaxCloudiness() != null && forecast.getCloudiness() > weatherConditions.getMaxCloudiness()) {
            return false;
        }

        // precipitation
        var precipitation = forecast.getPrecipitation();
        if (precipitation != null) {
            switch (precipitation.getType()) {
                case FOG -> {
                    if (!isPrecipitationOkOrUnknown(weatherConditions.getMaxFog(), precipitation.getIntensity(), precipitation.getProbability())) {
                        return false;
                    }
                }
                case RAIN -> {
                    if (!isPrecipitationOkOrUnknown(weatherConditions.getMaxRain(), precipitation.getIntensity(), precipitation.getProbability())) {
                        return false;
                    }
                }
                case SNOW -> {
                    if (!isPrecipitationOkOrUnknown(weatherConditions.getMaxSnow(), precipitation.getIntensity(), precipitation.getProbability())) {
                        return false;
                    }
                }
                default -> {
                    // do nothing
                }
            }

        }

        return true;
    }

    private boolean isPrecipitationOkOrUnknown(Integer max, int forecastedIntensity, double forecastedProbability) {
        if (max == null) {
            return true;
        }
        var precipitation = ((double) forecastedIntensity / 100) * (forecastedProbability / 100) * 100;
        return precipitation <= max;
    }

    @Override
    public boolean supports(SearchParameter searchParameter) {
        return searchParameter.getClass().isAssignableFrom(WeatherSearchParameter.class);
    }

    @Override
    public int cost() {
        return 10;
    }

}
