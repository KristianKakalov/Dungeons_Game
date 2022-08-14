package dungeons.gameLogic.map;

import dungeons.gameLogic.map.exceptions.InvalidMapCoordinatesException;

import java.util.Set;

public class GameMap {

    public static final int WIDTH = 20;
    public static final int HEIGHT = 5;

    private static final int NUM_OF_SINGLE_OBSTACLES = 10;
    private static final int NUM_OF_DOUBLE_OBSTACLES = 5;
    private static final int NUM_OF_TRIPLE_OBSTACLES = 3;

    private String[][] matrix;

    public GameMap(Set<Coordinates> minionsCoordinates, Set<Coordinates> treasuresCoordinates) {
        this.matrix = new String[WIDTH][HEIGHT];
        initializeMinionsCoordinates(minionsCoordinates);
        initializeTreasuresCoordinates(treasuresCoordinates);
        initializeObstacles();
        initializeEmptySpots();
    }

    private void initializeMinionsCoordinates(Set<Coordinates> minionsCoordinates) {
        for (var coordinate : minionsCoordinates) {
            matrix[coordinate.x()][coordinate.y()] = MapSymbols.MINION.toString();
        }
    }

    private void initializeTreasuresCoordinates(Set<Coordinates> treasuresCoordinates) {
        for (var coordinate : treasuresCoordinates) {
            matrix[coordinate.x()][coordinate.y()] = MapSymbols.TREASURE.toString();
        }
    }

    private void initializeObstacles() {
        initializeTripleObstacles();
        initializeDoubleObstacles();
        initializeSingleObstacles();
    }

    private void initializeSingleObstacles() {
        for (int i = 0; i < NUM_OF_SINGLE_OBSTACLES; ) {
            Coordinates coordinatesRandom = Coordinates.generateCoordinates(WIDTH, HEIGHT);
            int x = coordinatesRandom.x();
            int y = coordinatesRandom.y();
            if (coordinateIsEmpty(x, y)) {
                matrix[x][y] = MapSymbols.OBSTACLE.toString();
                i++;
            }
        }
    }

    private void initializeDoubleObstacles() {
        for (int i = 0; i < NUM_OF_DOUBLE_OBSTACLES; ) {
            Coordinates coordinatesRandom = Coordinates.generateCoordinates(WIDTH, HEIGHT);
            int x = coordinatesRandom.x();
            int y = coordinatesRandom.y();
            if (coordinateIsEmpty(x, y) &&
                    coordinateIsEmpty(x + 1, y)) {
                matrix[x][y] = MapSymbols.OBSTACLE.toString();
                matrix[x + 1][y] = MapSymbols.OBSTACLE.toString();
                i++;
            }
        }
    }

    private void initializeTripleObstacles() {
        for (int i = 0; i < NUM_OF_TRIPLE_OBSTACLES; ) {
            Coordinates coordinatesRandom = Coordinates.generateCoordinates(WIDTH, HEIGHT);
            int x = coordinatesRandom.x();
            int y = coordinatesRandom.y();
            if (coordinateIsEmpty(x, y) &&
                    coordinateIsEmpty(x + 1, y) &&
                    coordinateIsEmpty(x + 2, y)) {
                matrix[x][y] = MapSymbols.OBSTACLE.toString();
                matrix[x + 1][y] = MapSymbols.OBSTACLE.toString();
                matrix[x + 2][y] = MapSymbols.OBSTACLE.toString();
                i++;
            }
        }
    }

    private void initializeEmptySpots() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (coordinateIsEmpty(i, j))
                    matrix[i][j] = MapSymbols.FREE_SPOT.toString();
            }
        }
    }

    private boolean coordinateIsEmpty(int x, int y) {
        if (validCoordinates(x, y) && matrix[x][y] == null) {
            return true;
        }
        return validCoordinates(x, y) &&
                !matrix[x][y].equals(MapSymbols.MINION.toString()) &&
                !matrix[x][y].equals(MapSymbols.TREASURE.toString()) &&
                !matrix[x][y].equals(MapSymbols.OBSTACLE.toString());
    }

    private boolean validCoordinates(int x, int y) {
        return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT;
    }

    public String gameMapString() {
        StringBuilder matrixAsString = new StringBuilder();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                matrixAsString.append(matrix[j][i]);
            }
            matrixAsString.append(System.lineSeparator());
        }
        return matrixAsString.toString();
    }

    public String getSymbol(Coordinates coordinates) {
        checkCoordinatesNull(coordinates);
        int x = coordinates.x();
        int y = coordinates.y();
        if (validCoordinates(x, y)) {
            return matrix[x][y];
        } else {
            throw new InvalidMapCoordinatesException("Coordinates out of map bounds");
        }
    }

    public void changeSymbol(Coordinates coordinates, String symbol) {
        int x = coordinates.x();
        int y = coordinates.y();
        if (!validCoordinates(x, y)) {
            return;
        }
        matrix[coordinates.x()][coordinates.y()] = symbol;
    }

    public Coordinates generateRandomFreeCoordinates() {
        Coordinates coordinates;
        while (true) {
            coordinates = Coordinates.generateCoordinates(WIDTH, HEIGHT);
            if (getSymbol(coordinates).equals(MapSymbols.FREE_SPOT.toString())) {
                return coordinates;
            }
        }
    }

    private void checkCoordinatesNull(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates are null");
        }
    }
}
