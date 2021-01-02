package be.stijnhooft.portal.activity.filters;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.searchparameters.DateSearchParameter;
import be.stijnhooft.portal.activity.searchparameters.ParticipantsSearchParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class ParticipantsItemFilterTest {

    private ParticipantsItemFilter filter;

    @BeforeEach
    void init() {
        filter = new ParticipantsItemFilter();
    }

    @Test
    void applyWhenNoMaximumAndNoMinimumNumberOfParticipantsAreDefinedThenTrue() {
        var numberOfParticipants = 5;
        var minNumberOfParticipants = 0;

        var activity = Activity.builder()
                .minNumberOfParticipants(minNumberOfParticipants)
                .maxNumberOfParticipants(null)
                .build();

        var searchParameter = ParticipantsSearchParameter.create(numberOfParticipants).get();

        assertThat(filter.apply(activity, searchParameter)).isTrue();
    }

    @Test
    void applyWhenNoMaximumAndMinimumNumberOfParticipantsIsHigherThenFalse() {
        var numberOfParticipants = 5;
        var minNumberOfParticipants = 10;

        var activity = Activity.builder()
                .minNumberOfParticipants(minNumberOfParticipants)
                .maxNumberOfParticipants(null)
                .build();

        var searchParameter = ParticipantsSearchParameter.create(numberOfParticipants).get();

        assertThat(filter.apply(activity, searchParameter)).isFalse();
    }

    @Test
    void applyWhenMinimumNumberOfParticipantsIsHigherThenFalse() {
        var numberOfParticipants = 5;
        var minNumberOfParticipants = 10;
        var maxNumberOfParticipants = 15;

        var activity = Activity.builder()
                .minNumberOfParticipants(minNumberOfParticipants)
                .maxNumberOfParticipants(maxNumberOfParticipants)
                .build();

        var searchParameter = ParticipantsSearchParameter.create(numberOfParticipants).get();

        assertThat(filter.apply(activity, searchParameter)).isFalse();
    }

    @Test
    void applyWhenMaximumNumberOfParticipantsIsLowerThenFalse() {
        var numberOfParticipants = 30;
        var minNumberOfParticipants = 10;
        var maxNumberOfParticipants = 15;

        var activity = Activity.builder()
                .minNumberOfParticipants(minNumberOfParticipants)
                .maxNumberOfParticipants(maxNumberOfParticipants)
                .build();

        var searchParameter = ParticipantsSearchParameter.create(numberOfParticipants).get();

        assertThat(filter.apply(activity, searchParameter)).isFalse();
    }

    @Test
    void applyWhenNumberOfParticipantsBetweenMinAndMaxThenTrue() {
        var numberOfParticipants = 12;
        var minNumberOfParticipants = 10;
        var maxNumberOfParticipants = 15;

        var activity = Activity.builder()
                .minNumberOfParticipants(minNumberOfParticipants)
                .maxNumberOfParticipants(maxNumberOfParticipants)
                .build();

        var searchParameter = ParticipantsSearchParameter.create(numberOfParticipants).get();

        assertThat(filter.apply(activity, searchParameter)).isTrue();
    }

    @Test
    void applyWhenNumberOfParticipantsEqualToMinThenTrue() {
        var numberOfParticipants = 10;
        var minNumberOfParticipants = 10;
        var maxNumberOfParticipants = 15;

        var activity = Activity.builder()
                .minNumberOfParticipants(minNumberOfParticipants)
                .maxNumberOfParticipants(maxNumberOfParticipants)
                .build();

        var searchParameter = ParticipantsSearchParameter.create(numberOfParticipants).get();

        assertThat(filter.apply(activity, searchParameter)).isTrue();
    }

    @Test
    void applyWhenNumberOfParticipantsEqualToMaxThenTrue() {
        var numberOfParticipants = 15;
        var minNumberOfParticipants = 10;
        var maxNumberOfParticipants = 15;

        var activity = Activity.builder()
                .minNumberOfParticipants(minNumberOfParticipants)
                .maxNumberOfParticipants(maxNumberOfParticipants)
                .build();

        var searchParameter = ParticipantsSearchParameter.create(numberOfParticipants).get();

        assertThat(filter.apply(activity, searchParameter)).isTrue();
    }

    @Test
    void supportsWhenTrue() {
        assertThat(filter.supports(ParticipantsSearchParameter.create(5).get())).isTrue();
    }

    @Test
    void supportsWhenFalse() {
        assertThat(filter.supports(DateSearchParameter.create(LocalDate.now(), LocalDate.now()).get())).isFalse();
    }

}