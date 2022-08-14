package dungeons.online.server.exception;

public class ClientDisconnectedException extends Exception {
    public ClientDisconnectedException(String message) {
        super(message);
    }

    public ClientDisconnectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
