package be.stijnhooft.portal.activity.repositories;

import be.stijnhooft.portal.activity.domain.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, String> {

    Page<Activity> findByNameContainingIgnoreCase(String filter, Pageable pageable);

}