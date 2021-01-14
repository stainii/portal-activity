package be.stijnhooft.portal.activity;

import be.stijnhooft.portal.activity.messaging.EventTopic;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootTest
@EnableBinding(EventTopic.class)
class PortalActivityApplicationTests {

    @Test
    void contextLoads() {
    }

}
