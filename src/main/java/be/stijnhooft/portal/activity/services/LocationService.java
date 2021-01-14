package be.stijnhooft.portal.activity.services;

import be.stijnhooft.portal.model.location.Distance;
import be.stijnhooft.portal.model.location.DistanceQuery;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class LocationService {

    public static final String SERVICE_ID = "location";

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;


    public List<Distance> findDistance(String userInputLocation, Set<String> activityLocations) {
        String url = UriComponentsBuilder.fromHttpUrl(findPortalLocationUrl())
                .path("distance")
                .build()
                .toString();

        List<DistanceQuery> distanceQueries = activityLocations.stream()
                .map(activityLocation -> new DistanceQuery(userInputLocation, activityLocation))
                .collect(Collectors.toList());

        var response = restTemplate.postForEntity(url, distanceQueries, Distance[].class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            log.warn("Location microservice gave back a {} response: {}", response.getStatusCode(), response.getBody());
            return new ArrayList<>();
        }

        return Arrays.asList(response.getBody());
    }

    private String findPortalLocationUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(SERVICE_ID);
        if (instances != null && !instances.isEmpty()) {
            return instances.get(0).getUri().toString() + "/";
        } else {
            throw new IllegalStateException("No instance of " + SERVICE_ID + " registered with Eureka");
        }
    }
}
