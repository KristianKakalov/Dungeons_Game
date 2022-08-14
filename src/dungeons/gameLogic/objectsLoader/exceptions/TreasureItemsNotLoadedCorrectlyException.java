package dungeons.gameLogic.objectsLoader.exceptions;

public class TreasureItemsNotLoadedCorrectlyException extends RuntimeException {

    public TreasureItemsNotLoadedCorrectlyException(String message) {
        super(message);
    }

    public TreasureItemsNotLoadedCorrectlyException(String message, Throwable cause) {
        super(message, cause);
    }
}
