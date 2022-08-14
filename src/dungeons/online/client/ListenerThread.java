package dungeons.online.client;

import java.io.PrintWriter;
import java.util.Scanner;

public class ListenerThread extends Thread {

    private final PrintWriter writer;
    private final Scanner scanner;

    public ListenerThread(PrintWriter writer, Scanner scanner) {
        this.writer = writer;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        while (true) {
            String message = scanner.nextLine();
            if (message.equals("quit")) {
                System.out.println("You are disconnected.");
                System.exit(0);
            }
            writer.println(message);
        }
    }
}
