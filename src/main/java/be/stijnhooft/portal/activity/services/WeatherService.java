package be.stijnhooft.portal.activity.services;

import be.stijnhooft.portal.model.weather.Forecast;
import be.stijnhooft.portal.model.weather.ForecastRequest;
import be.stijnhooft.portal.model.weather.ForecastRequests;
import be.stijnhooft.portal.model.weather.ForecastResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Slf4j
@AllArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public static final String SERVICE_ID = "weather";

    public Collection<Forecast> findForecasts(@NonNull Collection<String> locations, @NonNull LocalDate startDate, @NonNull LocalDate endDate) {
        ForecastRequests forecastRequestsDto = createForecastRequestsDto(locations, startDate, endDate);

        String url = UriComponentsBuilder.fromHttpUrl(findPortalWeatherUrl())
                .path("forecasts")
                .build()
                .toString();

        try {
            var forecastResponse = restTemplate.postForObject(url, forecastRequestsDto, ForecastResponse.class);
            if (forecastResponse == null || forecastResponse.getForecasts() == null) {
                return new ArrayList<>();
            } else {
                return forecastResponse.getForecasts();
            }
        } catch (Exception ex) {
            log.warn("Could not retrieve forecasts for {}", forecastRequestsDto, ex);
            return new ArrayList<>();
        }
    }

    private ForecastRequests createForecastRequestsDto(Collection<String> locations, LocalDate startDate, LocalDate endDate) {
        var forecastRequests = locations.stream()
                .map(location -> ForecastRequest.builder()
                        .location(location)
                        .startDateTime(startDate.atStartOfDay())
                        .endDateTime(endDate.plus(1, DAYS).atStartOfDay())
                        .build())
                .collect(Collectors.toSet());
        return ForecastRequests.builder()
                .forecastRequests(forecastRequests)
                .build();
    }

    private String findPortalWeatherUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(SERVICE_ID);
        if (instances != null && !instances.isEmpty()) {
            return instances.get(0).getUri().toString() + "/";
        } else {
            throw new IllegalStateException("No instance of " + SERVICE_ID + " registered with Eureka");
        }
    }
}
