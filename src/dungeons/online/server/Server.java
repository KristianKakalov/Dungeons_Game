package dungeons.online.server;

import dungeons.messeges.Messages;
import dungeons.online.command.CommandCreator;
import dungeons.online.command.CommandExecutor;
import dungeons.online.server.exception.ClientDisconnectedException;
import logger.Log;
import logger.LogLevel;
import logger.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Iterator;

public class Server {
    private static final int BUFFER_SIZE = 1024;
    private static final int SERVER_PORT = 8888;
    private static final String HOST = "localhost";

    private Selector selector;
    private final ByteBuffer buffer;
    private final Logger logger;

    private final CommandExecutor commandExecutor;

    private static final String CLIENT_DISCONNECTED = "Client disconnected";

    public Server(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.logger = Logger.getLoggerInstance();
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            while (true) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();

                        if (key.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            read(clientChannel);

                        } else if (key.isAcceptable()) {
                            accept(selector, key);
                        }
                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    logger.log(new Log(LogLevel.ERROR, LocalDateTime.now(),
                            "Error occurred while processing client request: " + e.getMessage()));
                }
            }
        } catch (IOException e) {
            logger.log(new Log(LogLevel.ERROR, LocalDateTime.now(), "Failed to start server: " + e.getMessage()));
            throw new UncheckedIOException("Failed to start server", e);
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, SERVER_PORT));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws ClientDisconnectedException {
        buffer.clear();

        try {
            int readBytes = clientChannel.read(buffer);
            if (readBytes < 0) {
                clientChannel.close();
                throw new ClientDisconnectedException(CLIENT_DISCONNECTED);
            }
        } catch (IOException e) {
            throw new ClientDisconnectedException(CLIENT_DISCONNECTED);
        }

        buffer.flip();
        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws ClientDisconnectedException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        try {
            clientChannel.write(buffer);
            if (output.contains(Messages.PLAYER_KILLED.toString())) {
                clientChannel.close();
            }
        } catch (IOException e) {
            throw new ClientDisconnectedException(CLIENT_DISCONNECTED);
        }
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);

        String message = commandExecutor.connectPlayer(accept) +
                System.lineSeparator();
        try {
            writeClientOutput(accept, message);
            logger.log(new Log(LogLevel.ACCEPT, LocalDateTime.now(), accept.getRemoteAddress() + " accepted"));
        } catch (ClientDisconnectedException e) {
            String logMessage = String.format(Messages.CONNECTION_LOST.toString(), accept.getRemoteAddress());
            logger.log(new Log(LogLevel.CONNECTION_LOST, LocalDateTime.now(), logMessage));
        }
    }

    private void read(SocketChannel clientChannel) throws IOException {
        String clientInput;
        try {
            clientInput = getClientInput(clientChannel);
            String output =
                    commandExecutor.execute(CommandCreator.newCommand(clientInput), clientChannel) +
                            System.lineSeparator();
            writeClientOutput(clientChannel, output);

            logger.log(new Log(LogLevel.INFO, LocalDateTime.now(),
                    clientChannel.getRemoteAddress() + " input: " + clientInput));
            logger.log(new Log(LogLevel.INFO, LocalDateTime.now(),
                    clientChannel.getRemoteAddress() + " output: " + output));
        } catch (ClientDisconnectedException e) {
            String logMessage =
                    String.format(Messages.CONNECTION_LOST.toString(), clientChannel.getRemoteAddress());
            logger.log(new Log(LogLevel.CONNECTION_LOST, LocalDateTime.now(), logMessage));

            commandExecutor.disconnectPlayer(clientChannel);
            clientChannel.close();
        }
    }
}