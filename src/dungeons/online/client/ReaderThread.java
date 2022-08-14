package dungeons.online.client;

import java.io.BufferedReader;
import java.io.IOException;

public class ReaderThread implements Runnable {

    private final BufferedReader reader;

    public ReaderThread(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("You died")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("You lost your connection with server : " + e.getMessage());
                break;
            }
        }
    }
}