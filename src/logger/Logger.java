package logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Logger {

    private static final String FILE_NAME = "resources" + File.separator + "logs.txt";
    private static Logger logger = new Logger();

    private Logger() {
        try (var bw = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
            //clear previous logs
            bw.write("");
            bw.flush();
        } catch (IOException e) {
            throw new LogException("File not found");
        }
    }

    public static Logger getLoggerInstance() {
        return logger;
    }

    public void log(Log log) {
        try (var bw = Files.newBufferedWriter(Paths.get(FILE_NAME), StandardOpenOption.APPEND)) {
            bw.write(log.convertLogToMessage());
            bw.flush();
        } catch (IOException e) {
            throw new LogException("Log exception");
        }
    }
}
