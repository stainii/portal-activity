package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.searchparameters.DateSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.LabelSearchParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LabelItemFilterTest {

    private LabelItemFilter filter;

    @BeforeEach
    void init() {
        filter = new LabelItemFilter();
    }

    @Test
    void applyWhenLabelFoundThenTrue() {
        var activity = Activity.builder()
                .labels(List.of("A", "B", "C"))
                .build();

        var searchParameter = LabelSearchParameter.create(List.of("B", "D")).get();

        assertThat(filter.apply(activity, searchParameter)).isTrue();
    }

    @Test
    void applyWhenLabelNotFoundThenFalse() {
        var activity = Activity.builder()
                .labels(List.of("A", "B", "C"))
                .build();

        var searchParameter = LabelSearchParameter.create(List.of("D", "E")).get();

        assertThat(filter.apply(activity, searchParameter)).isFalse();
    }

    @Test
    void applyWhenActivityHasNoLabelsThenFalse() {
        var activity = Activity.builder()
                .labels(new ArrayList<>())
                .build();

        var searchParameter = LabelSearchParameter.create(List.of("D", "E")).get();

        assertThat(filter.apply(activity, searchParameter)).isFalse();
    }

    @Test
    void supportsWhenTrue() {
        assertThat(filter.supports(LabelSearchParameter.create(List.of("A")).get())).isTrue();
    }

    @Test
    void supportsWhenFalse() {
        assertThat(filter.supports(DateSearchParameter.create(LocalDate.now(), LocalDate.now()).get())).isFalse();
    }

}