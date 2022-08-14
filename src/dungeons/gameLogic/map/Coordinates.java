package dungeons.gameLogic.map;
import java.util.Random;

public record Coordinates(int x, int y) {

    public static Coordinates generateCoordinates(int boundaryX, int boundaryY) {
        Random random = new Random();
        int x = random.nextInt(boundaryX);
        int y = random.nextInt(boundaryY);
        return new Coordinates(x, y);
    }
}
