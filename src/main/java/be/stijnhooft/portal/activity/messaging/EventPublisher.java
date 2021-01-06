package be.stijnhooft.portal.activity.messaging;

import be.stijnhooft.portal.model.domain.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Slf4j
public class EventPublisher {

  private final EventTopic eventTopic;

  @Autowired
  public EventPublisher(EventTopic eventTopic) {
    this.eventTopic = eventTopic;
  }

  public void publish(Collection<Event> events) {
      log.info("Sending events to the Event topic");
      log.debug("{}", events);
      eventTopic.writeToEventTopic().send(MessageBuilder.withPayload(events).build());
  }

}
