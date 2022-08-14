package logger;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

    private Logger logger = Logger.getLoggerInstance();
    private static final String FILE_NAME = "resources" + File.separator + "logs.txt";

    @Test
    public void testLoggerCorrect() {
        logger.log(new Log(LogLevel.INFO, LocalDateTime.of(2022, 1, 1, 1, 1), "test"));
        String expected ="[INFO]|2022-01-01T01:01:00|test";

        try (var br = Files.newBufferedReader(Path.of(FILE_NAME))) {
            String actual = br.readLine();
            if (actual != null) {
                assertEquals(expected, actual, "Log not saved correctly to file");
            }

        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading the file", e);
        }
    }
}