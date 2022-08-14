package dungeons.online.clientTwo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ClientTwo {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;
    private ByteBuffer buffer;

    public ClientTwo() {
        buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open()) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            System.out.println("Connected to the server.");

            Thread tread = new ClientMessageSender(socketChannel);
            tread.setDaemon(true);
            tread.start();

            while (true) {
                String message = messageFromServer(socketChannel);
                if (message == null) {
                    continue;
                }

                System.out.println(message);
            }

        } catch (IOException e) {
            System.err.println("There is a problem with the network communication: " + e.getMessage());
            System.exit(1);
        }
    }

    private String messageFromServer(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        if (clientChannel.read(buffer) < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();
        byte[] serverInputBytes = new byte[buffer.remaining()];
        buffer.get(serverInputBytes);

        return new String(serverInputBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        new ClientTwo().start();
    }
}
