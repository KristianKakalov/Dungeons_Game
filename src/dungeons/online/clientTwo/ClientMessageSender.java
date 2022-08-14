package dungeons.online.clientTwo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ClientMessageSender extends Thread {

    private ByteBuffer buffer;
    private static final int BUFFER_SIZE = 1024;

    private final SocketChannel socketChannel;

    public ClientMessageSender(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            try {
                messageToServer(line, socketChannel);
            } catch (IOException e) {
                System.err.println("You lost your connection with server");
                break;
            }
        }
    }

    private void messageToServer(String msg, SocketChannel serverSocketChannel) throws IOException {
        buffer.clear();
        buffer.put(msg.getBytes());
        buffer.flip();
        serverSocketChannel.write(buffer);
    }
}
