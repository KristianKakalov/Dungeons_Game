package dungeons.gameLogic.gameTactics;

import dungeons.gameLogic.map.Coordinates;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static Coordinates coordinatesAfterMovement(Coordinates coordinates, Direction direction) {
        return switch (direction) {
            case UP -> new Coordinates(coordinates.x(), coordinates.y() - 1);
            case DOWN -> new Coordinates(coordinates.x(), coordinates.y() + 1);
            case LEFT -> new Coordinates(coordinates.x() - 1, coordinates.y());
            case RIGHT -> new Coordinates(coordinates.x() + 1, coordinates.y());
        };
    }
}