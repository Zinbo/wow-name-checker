package com.stacktobasics.wownamechecker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import java.util.stream.Stream;

public class MemoryAppender extends ListAppender<ILoggingEvent> {

    public void reset() { this.list.clear(); }

    public boolean eventsExistWithMessagesEqualTo(Level level, String... messages) {
        return Stream.of(messages).allMatch(message -> this.list.stream().anyMatch(event -> event.getFormattedMessage().equals(message)
        && event.getLevel().equals(level)));
    }
}
