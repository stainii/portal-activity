package be.stijnhooft.portal.activity.mappers;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.services.ImageService;
import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.model.domain.FlowAction;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SuggestionEventMapper {

    private final ImageService imageService;

    public List<Event> map(Collection<Activity> suggestions) {
        return suggestions.stream()
                .map(activity -> Event.builder()
                    .flowId(UUID.randomUUID().toString())
                    .source("Activity")
                    .flowAction(FlowAction.START)
                    .publishDate(LocalDateTime.now())
                    .data(toDataMap(activity))
                    .build())
                .collect(Collectors.toList());
    }

    private Map<String, String> toDataMap(Activity activity) {
        var map = new HashMap<String, String>();
        map.put("name", activity.getName());
        map.put("description", activity.getDescription());
        if (StringUtils.isNotEmpty(activity.getPhoto())) {
            map.put("photo", imageService.findPortalImageUrl() + "api/retrieve/" + activity.getPhoto());
        }
        return map;
    }

}
