package be.stijnhooft.portal.activity.mappers;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.model.domain.FlowAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SuggestionEventMapperTest {

    private SuggestionEventMapper mapper;

    @BeforeEach
    void init() {
        mapper = new SuggestionEventMapper();
    }

    @Test
    void map() {
        var activity1 = Activity.builder()
                .name("Maximal")
                .description("desc")
                .photo("photo.jpg")
                .build();

        var activity2 = Activity.builder()
                .name("Minimal")
                .build();

        var events = new ArrayList<>(mapper.map(List.of(activity1, activity2)));

        var event1 = events.get(0);
        assertEquals(FlowAction.START, event1.getFlowAction());
        assertNotNull(event1.getFlowId());
        assertEquals("Activity", event1.getSource());
        assertNotNull(event1.getPublishDate());
        assertEquals(3, event1.getData().keySet().size());
        assertEquals("Maximal", event1.getData().get("name"));
        assertEquals("desc", event1.getData().get("description"));
        assertEquals("photo.jpg", event1.getData().get("photo"));

        var event2 = events.get(1);
        assertEquals(FlowAction.START, event2.getFlowAction());
        assertNotNull(event2.getFlowId());
        assertEquals("Activity", event2.getSource());
        assertNotNull(event2.getPublishDate());
        assertEquals(3, event2.getData().keySet().size());
        assertEquals("Minimal", event2.getData().get("name"));
        assertNull(event2.getData().get("description"));
        assertNull(event2.getData().get("photo"));
    }

}