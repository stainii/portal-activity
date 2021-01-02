package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.searchparameters.ParticipantsSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.SearchParameter;
import org.springframework.stereotype.Component;

@Component
public class ParticipantsItemFilter implements ItemFilter {

    @Override
    public boolean apply(Activity activity, SearchParameter searchParameter) {
        return apply(activity, (ParticipantsSearchParameter) searchParameter);
    }

    public boolean apply(Activity activity, ParticipantsSearchParameter searchParameter) {
        return searchParameter.getNumberOfParticipants() >= activity.getMinNumberOfParticipants()
                && (activity.getMaxNumberOfParticipants() == null || searchParameter.getNumberOfParticipants() <= activity.getMaxNumberOfParticipants());
    }

    @Override
    public boolean supports(SearchParameter searchParameter) {
        return searchParameter.getClass().isAssignableFrom(ParticipantsSearchParameter.class);
    }

    @Override
    public int cost() {
        return 0;
    }

}
