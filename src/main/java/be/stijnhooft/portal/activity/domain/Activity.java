package be.stijnhooft.portal.activity.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Activity {

    @Id
    private String id;

    private String name;

    private String photo;

    /**
     * Fill in this field only when you want to upload a new image.
     * Never persisted to the database, is meant to be used as a DTO property.
     * If more fields need to be transient, consider separating these fields in a separate DTO,
     * but for now this works fine.
     */
    @Transient
    private String newPhotoContent;

    private String source;

    private String description;

    private Location location;

    private Weather weather;

    private int minNumberOfParticipants;

    private Integer maxNumberOfParticipants;

    @Singular
    private List<DateInterval> dateIntervals = new ArrayList<>();

    @Singular
    private List<TimeInterval> timeIntervals = new ArrayList<>();

    @Singular
    private List<String> labels = new ArrayList<>();

    @Version
    private Integer version;

}
