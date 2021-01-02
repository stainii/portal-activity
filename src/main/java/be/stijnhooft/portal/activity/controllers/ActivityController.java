package be.stijnhooft.portal.activity.controllers;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.factory.PageRequestFactory;
import be.stijnhooft.portal.activity.factory.SortFactory;
import be.stijnhooft.portal.activity.searchparameters.*;
import be.stijnhooft.portal.activity.services.ActivityService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/search/")
    public Collection<Activity> find(@RequestParam(value = "location", required = false) String location,
                                     @RequestParam(value = "radius", required = false) Integer locationRadiusInKm,
                                     @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                     @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                     @RequestParam(value = "numberOfParticipants", required = false) Integer numberOfParticipants,
                                     @RequestParam(value = "considerWeather", required = false) Boolean considerWeather,
                                     @RequestParam(value = "labels", required = false) List<String> labels) {
        var searchParameters = Stream.of(
                LocationSearchParameter.create(location, locationRadiusInKm),
                DateSearchParameter.create(startDate, endDate),
                ParticipantsSearchParameter.create(numberOfParticipants),
                WeatherSearchParameter.create(considerWeather, startDate, endDate),
                LabelSearchParameter.create(labels)
        )
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        return activityService.find(searchParameters);
    }

    @GetMapping
    public Page<Activity> findAll(@RequestParam(value = "page") Integer page,
                                  @RequestParam(value = "pageSize") Integer pageSize,
                                  @RequestParam(value = "sortField", required = false) String sortField,
                                  @RequestParam(value = "order", required = false) String order,
                                  @RequestParam(value = "filter", required = false) String filter) {
        var sort = SortFactory.create(sortField, order)
                .orElse(null);
        var pageRequest = PageRequestFactory.create(page, pageSize, sort)
                .orElse(null);

        return activityService.find(pageRequest, filter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> findById(@PathVariable("id") String id) {
        return activityService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Activity create(@RequestBody Activity activity) {
        return activityService.create(activity);
    }

    @PutMapping("/{id}")
    public Activity update(@PathVariable("id") String id, @RequestBody Activity activity) {
        if (!id.equals(activity.getId())) {
            throw new IllegalArgumentException("The id in the url (" + id + ") is not the same as the id in the payload (" + activity.getId() + ")");
        }
        return activityService.update(activity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        activityService.delete(id);
    }

}
