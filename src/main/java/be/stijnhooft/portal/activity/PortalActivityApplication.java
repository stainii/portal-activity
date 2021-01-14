package be.stijnhooft.portal.activity;

import be.stijnhooft.portal.activity.messaging.EventTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding(EventTopic.class)
public class PortalActivityApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalActivityApplication.class, args);
    }

}
