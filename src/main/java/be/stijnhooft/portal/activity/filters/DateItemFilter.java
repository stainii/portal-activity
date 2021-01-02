package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.searchparameters.DateSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.SearchParameter;
import org.springframework.stereotype.Component;

@Component
public class DateItemFilter implements ItemFilter {

    @Override
    public boolean apply(Activity activity, SearchParameter searchParameter) {
        return apply(activity, (DateSearchParameter) searchParameter);
    }

    public boolean apply(Activity activity, DateSearchParameter searchParameter) {
        if (activity.getDateIntervals() == null || activity.getDateIntervals().isEmpty()) {
            return true;
        }

        return activity.getDateIntervals()
                .stream()
                .anyMatch(interval -> interval.covers(searchParameter.getStartDate(), searchParameter.getEndDate()));
    }

    @Override
    public boolean supports(SearchParameter searchParameter) {
        return searchParameter.getClass().isAssignableFrom(DateSearchParameter.class);
    }

    @Override
    public int cost() {
        return 0;
    }

}
