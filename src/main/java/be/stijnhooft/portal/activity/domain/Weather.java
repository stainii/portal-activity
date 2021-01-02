package be.stijnhooft.portal.activity.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Weather {

    /** in Celsius **/
    private Integer minTemperature;

    /** in Celsius **/
    private Integer maxTemperature;

    /** between 0 and 100 **/
    private Integer maxCloudiness;

    /** between 0 and 100 **/
    private Integer maxRain;

    /** between 0 and 100 **/
    private Integer maxSnow;

    /** between 0 and 100 **/
    private Integer maxFog;

    /** in beaufort **/
    private Integer minWind;

    /** in beaufort **/
    private Integer maxWind;

}
