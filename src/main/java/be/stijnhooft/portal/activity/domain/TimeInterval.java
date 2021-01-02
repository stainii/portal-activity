package be.stijnhooft.portal.activity.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TimeInterval {
    private LocalTime startTime;
    private LocalTime endTime;
}
