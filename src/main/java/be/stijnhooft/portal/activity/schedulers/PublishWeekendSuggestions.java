package be.stijnhooft.portal.activity.schedulers;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.mappers.SuggestionEventMapper;
import be.stijnhooft.portal.activity.messaging.EventPublisher;
import be.stijnhooft.portal.activity.searchparameters.LocationSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.WeatherSearchParameter;
import be.stijnhooft.portal.activity.services.ActivitySearchService;
import be.stijnhooft.portal.activity.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
@EnableScheduling
public class PublishWeekendSuggestions {

    private final String location;
    private final int locationRadius;
    private final ActivitySearchService activitySearchService;
    private final SuggestionEventMapper suggestionEventMapper;
    private final EventPublisher eventPublisher;

    public PublishWeekendSuggestions(@Value("${activity.suggestions.location}") String location, @Value("${activity.suggestions.location-radius:15}") int locationRadius, ActivitySearchService activitySearchService, SuggestionEventMapper suggestionEventMapper, EventPublisher eventPublisher) {
        this.location = location;
        this.locationRadius = locationRadius;
        this.activitySearchService = activitySearchService;
        this.suggestionEventMapper = suggestionEventMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishing suggestions for weekend activities on every Wednesday.
     * At that point, weather forecasts are available and there is some time to plan.
     */
    @Scheduled(cron = "0 0 16 * * WED")
    //@Scheduled(cron = "0 * * * * *")
    public void publishSuggestionsForTheWeekend() {
        log.info("Checking for suggestions for the weekend.");

        var suggestions = findSuggestions();
        if (!suggestions.isEmpty()) {
            log.info("Found {} activities for next weekend.", suggestions.size());
            var events = suggestionEventMapper.map(suggestions);
            eventPublisher.publish(events);
        } else {
            log.info("No activities found for next weekend.");
        }
    }

    private Collection<Activity> findSuggestions() {
        List<LocalDate> nextWeekend = DateUtil.getNextWeekend();
        var locationSearchParameter = LocationSearchParameter.create(location, locationRadius)
                .orElseThrow();
        var weatherSearchParameter = WeatherSearchParameter.create(true, nextWeekend.get(0), nextWeekend.get(1))
                .orElseThrow();
        return activitySearchService.find(List.of(locationSearchParameter, weatherSearchParameter));
    }

}
