package dungeons.gameLogic.map.exceptions;

import java.util.Map;

public class InvalidMapCoordinatesException extends RuntimeException {
    public InvalidMapCoordinatesException(String message) {
        super(message);
    }

    public InvalidMapCoordinatesException(String message, Throwable cause) {
        super(message, cause);
    }
}
