package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.searchparameters.LabelSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.SearchParameter;
import org.springframework.stereotype.Component;

@Component
public class LabelItemFilter implements ItemFilter {

    @Override
    public boolean apply(Activity activity, SearchParameter searchParameter) {
        return apply(activity, (LabelSearchParameter) searchParameter);
    }

    public boolean apply(Activity activity, LabelSearchParameter searchParameter) {
        if (activity.getLabels() == null || activity.getLabels().isEmpty()) {
            return false;
        }

        return activity.getLabels()
                .stream()
                .anyMatch(label -> searchParameter.getLabelsToLookFor().contains(label));
    }

    @Override
    public boolean supports(SearchParameter searchParameter) {
        return searchParameter.getClass().isAssignableFrom(LabelSearchParameter.class);
    }

    @Override
    public int cost() {
        return 0;
    }

}
