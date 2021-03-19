package be.stijnhooft.portal.activity.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EventTopic {

    String OUTPUT = "writeToEventTopic";

    @Output(OUTPUT)
    MessageChannel writeToEventTopic();

}
