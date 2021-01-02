package be.stijnhooft.portal.activity.services;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.filters.ItemFilter;
import be.stijnhooft.portal.activity.filters.ListFilter;
import be.stijnhooft.portal.activity.repositories.ActivityRepository;
import be.stijnhooft.portal.activity.searchparameters.SearchParameter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ActivitySearchService {

    private List<ItemFilter> itemFilters;
    private List<ListFilter> listFilters;
    private final ActivityRepository activityRepository;

    @PostConstruct
    public void init() {
        // order filters by cost, so that the least expensive ones get called first
        itemFilters = itemFilters.stream().sorted(Comparator.comparingInt(ItemFilter::cost)).collect(Collectors.toList());
        listFilters = listFilters.stream().sorted(Comparator.comparingInt(ListFilter::cost)).collect(Collectors.toList());
    }

    public Collection<Activity> find(@NonNull Collection<? extends SearchParameter> searchParameters) {
        log.info("Searching for activities with parameters {}", searchParameters);

        var allActivities = activityRepository.findAll();
        var filteredActivities = applyItemFilters(allActivities, searchParameters);
        filteredActivities = applyListFilters(filteredActivities, searchParameters);
        return filteredActivities;
    }

    private Collection<Activity> applyItemFilters(Collection<Activity> activities, Collection<? extends SearchParameter> searchParameters) {
        return activities
                .stream()
                .filter(activity -> appliesToAllSearchParameters(activity, searchParameters))
                .collect(Collectors.toList());
    }


    private boolean appliesToAllSearchParameters(Activity activity, Collection<? extends SearchParameter> searchParameters) {
        return searchParameters
                .stream()
                .allMatch(searchParameter -> applyItemFilterForSearchParameter(activity, searchParameter));
    }

    private boolean applyItemFilterForSearchParameter(Activity activity, SearchParameter searchParameter) {
        return itemFilterFor(searchParameter)
                .map(filter -> filter.apply(activity, searchParameter))
                .orElse(true);
    }

    private Optional<ItemFilter> itemFilterFor(SearchParameter searchParameter) {
        return itemFilters
                .stream()
                .filter(filter -> filter.supports(searchParameter))
                .findFirst();
    }

    private Collection<Activity> applyListFilters(Collection<Activity> activities, Collection<? extends SearchParameter> searchParameters) {
        Collection<Activity> filteredActivities = activities;
        for (ListFilter listFilter : listFilters) {
            var supportedSearchParameters = searchParameters.stream()
                    .filter(listFilter::supports)
                    .collect(Collectors.toList());
            for (SearchParameter searchParameter : supportedSearchParameters) {
                filteredActivities = listFilter.apply(filteredActivities, searchParameter);
            }
        }
        return filteredActivities;
    }


}
