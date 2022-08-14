package logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Log(LogLevel level, LocalDateTime timeStamp, String message) {

    private static final char SEPARATOR = '|';

    public String convertLogToMessage() {
        return "[" + level + "]" + SEPARATOR + timeStamp.format(DateTimeFormatter.ISO_DATE_TIME)
                + SEPARATOR + message;
    }
}
