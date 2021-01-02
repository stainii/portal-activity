package be.stijnhooft.portal.activity.services;

import be.stijnhooft.portal.activity.repositories.ActivityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class LabelService {

    private final ActivityRepository activityRepository;

    public List<String> findAll() {
        log.info("Finding all labels");
        return activityRepository.findAll()
                .stream()
                .flatMap(activity -> activity.getLabels().stream())
                .distinct()
                .collect(Collectors.toList());
    }

}
