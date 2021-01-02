package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.searchparameters.LocationSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.SearchParameter;
import be.stijnhooft.portal.activity.services.LocationService;
import be.stijnhooft.portal.model.location.Distance;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class LocationListFilter implements ListFilter {

    private final LocationService locationService;

    public Collection<Activity> apply(Collection<Activity> activities, LocationSearchParameter searchParameter) {
        String userInput = searchParameter.getName().trim();
        int maxRadius = searchParameter.getRadiusInKm();

        var activitiesWithLocation = activities.stream()
                .filter(activity -> activity.getLocation() != null)
                .filter(activity -> activity.getLocation().toString().trim().length() > 0)
                .collect(Collectors.toList());

        // first look for literal matches
        var activitiesWithLiteralMatch = activitiesWithLocation.stream()
                .filter(activity -> activity.getLocation().literalMatch(userInput))
                .collect(Collectors.toList());

        if (activitiesWithLocation.size() == activitiesWithLiteralMatch.size()) {
            return activitiesWithLiteralMatch;
        }

        // For those without literal, go ask the location service for their distance.
        // Instead of firing multiple rest queries to the location service, every query needs to get bundled into 1 request
        // and the response needs to get matched back to the corresponding activities.
        var activitiesToCalculateDistanceFor = CollectionUtils.disjunction(activitiesWithLocation, activitiesWithLiteralMatch);
        var locationsToCompareWithUserInput = activitiesToCalculateDistanceFor.stream().map(activity -> activity.getLocation().toString()).collect(Collectors.toSet());
        var distances = locationService.findDistance(userInput, locationsToCompareWithUserInput);
        var activitiesWithinDistance = activitiesToCalculateDistanceFor.stream()
                .filter(activity -> isDistanceInMaxRadius(maxRadius, distances, activity))
                .collect(Collectors.toList());

        return CollectionUtils.union(activitiesWithLiteralMatch, activitiesWithinDistance);
    }

    private Boolean isDistanceInMaxRadius(int maxRadius, Collection<Distance> distances, Activity activity) {
        return distances.stream()
                .filter(distance -> distance.getLocation2Query().equals(activity.getLocation().toString()))
                .findFirst()
                .map(distance -> maxRadius >= distance.getKm())
                .orElse(false);
    }

    @Override
    public Collection<Activity> apply(Collection<Activity> activities, SearchParameter searchParameter) {
        return apply(activities, (LocationSearchParameter) searchParameter);
    }

    @Override
    public boolean supports(SearchParameter searchParameter) {
        return searchParameter.getClass().isAssignableFrom(LocationSearchParameter.class);
    }

    @Override
    public int cost() {
        return 5;
    }

}
