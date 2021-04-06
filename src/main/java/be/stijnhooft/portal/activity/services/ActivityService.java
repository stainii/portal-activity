package be.stijnhooft.portal.activity.services;

import be.stijnhooft.portal.activity.domain.Activity;
import be.stijnhooft.portal.activity.repositories.ActivityRepository;
import be.stijnhooft.portal.activity.searchparameters.SearchParameter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ActivityService {

    private final ActivitySearchService activitySearchService;
    private final ActivityRepository activityRepository;
    private final ImageService imageService;

    public Collection<Activity> find(@NonNull Collection<? extends SearchParameter> searchParameters) {
        return activitySearchService.find(searchParameters);
    }

    public Page<Activity> find(PageRequest pageRequest, String filter) {
        if (filter == null) {
            return activityRepository.findAll(pageRequest);
        } else {
            return activityRepository.findByNameContainingIgnoreCase(filter, pageRequest);
        }
    }

    public Activity create(Activity activity) {
        return createOrReplaceImageAndSave(activity);
    }

    public Activity update(@NonNull Activity activity) {
        if (activity.getId() == null) {
            throw new IllegalArgumentException("Activity has no id");
        }
        return createOrReplaceImageAndSave(activity);
    }

    public void delete(@NonNull String id) {
        activityRepository.deleteById(id);
    }

    public Optional<Activity> findById(@NonNull String id) {
        return activityRepository.findById(id);
    }

    private Activity createOrReplaceImageAndSave(Activity activity) {
        String newPhotoContent = activity.getNewPhotoContent();
        String newThumbnail = null;
        String oldThumbnail = null;

        try {
            // create new thumbnail
            if (newPhotoContent != null) {
                oldThumbnail = activity.getPhoto();
                newThumbnail = imageService.createThumbnail(newPhotoContent);
                activity.setPhoto(newThumbnail);
            } else {
                activity.setPhoto(null);
            }

            // save
            activityRepository.save(activity);
        } catch (RuntimeException e) {
            // rollback when necessary
            if (newThumbnail != null) {
                imageService.rollbackCreateImage(newThumbnail);
            }
            throw e;
        }

        // clean up old thumbnail
        if (newPhotoContent != null && oldThumbnail != null) {
            imageService.delete(oldThumbnail);
        }

        return activity;
    }

}
