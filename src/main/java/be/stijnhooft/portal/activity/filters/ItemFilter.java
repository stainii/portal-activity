package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.searchparameters.SearchParameter;

public interface ItemFilter {

    boolean apply(Activity activity, SearchParameter searchParameter);

    boolean supports(SearchParameter searchParameter);

    /**
     * How much does applying a filter cost in term of time and resources.
     * For example: a simple in-memory filter has cost 0. When an external api has to be called, the cost is higher.
     */
    int cost();
}
