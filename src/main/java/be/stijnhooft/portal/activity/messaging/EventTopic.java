package be.stijnhooft.portal.activity.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EventTopic {

    String INPUT = "readFromEventTopic";
    String OUTPUT = "writeToEventTopic";

    @Input(INPUT)
    MessageChannel readFromEventTopic();

    @Output(OUTPUT)
    MessageChannel writeToEventTopic();

}
