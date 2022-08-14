package dungeons.online.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private static final String DIED_MESSAGE = "You died";

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println("Connected to the server.");

            new ListenerThread(writer, scanner).start();

            String line;
            while (true) {

                if ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains(DIED_MESSAGE)) {
                        System.exit(0);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Connection lost with server. Check resources/log.txt for more information");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }
}
