package dungeons.gameLogic;

import dungeons.gameLogic.map.Coordinates;
import dungeons.gameLogic.map.GameMap;
import dungeons.gameLogic.map.MapSymbols;
import dungeons.gameLogic.map.exceptions.InvalidMapCoordinatesException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameMapTest {

    private static GameMap gameMap;
    private static Set<Coordinates> minionCoordinatesSet;
    private static Set<Coordinates> treasureItemsCoordinatesSet;

    @BeforeAll
    public static void setUp() {
        minionCoordinatesSet = Set.of(new Coordinates(1, 1),
                new Coordinates(1, 2),
                new Coordinates(1, 3),
                new Coordinates(1, 4));
        treasureItemsCoordinatesSet = Set.of(new Coordinates(2, 1),
                new Coordinates(2, 2),
                new Coordinates(2, 3));

        gameMap = new GameMap(minionCoordinatesSet, treasureItemsCoordinatesSet);
    }

    @Test
    public void testMapLoadedCorrectly() {
        String gameMapString = gameMap.gameMapString();

        long expectedMinionsCount = minionCoordinatesSet.size();
        long actualMinionCount = countMatches(gameMapString, MapSymbols.MINION.toString());

        assertEquals(expectedMinionsCount, actualMinionCount, "Minions not loaded correctly");

        long expectedTreasureItemsCount = treasureItemsCoordinatesSet.size();
        long actualTreasureItemsCount = countMatches(gameMapString, MapSymbols.TREASURE.toString());

        assertEquals(expectedTreasureItemsCount, actualTreasureItemsCount, "Treasure items not loaded correctly");

    }

    @Test
    public void testGetAndChangeSymbol() {
        Coordinates coordinates = gameMap.generateRandomFreeCoordinates();

        String expected = MapSymbols.FREE_SPOT.toString();
        String actual = gameMap.getSymbol(coordinates);

        assertEquals(expected, actual, "Not correct symbol");

        gameMap.changeSymbol(coordinates, MapSymbols.OBSTACLE.toString());

        String expectedAfterChange = MapSymbols.OBSTACLE.toString();
        String actualAfterChange = gameMap.getSymbol(coordinates);

        assertEquals(expectedAfterChange, actualAfterChange,
                "Symbol not changed correctly");
    }

    @Test
    public void testGetSymbolWithInvalidCoordinates() {
        Coordinates invalidCoordinates = new Coordinates(-1, -1);

        assertThrows(InvalidMapCoordinatesException.class,
                () -> gameMap.getSymbol(invalidCoordinates),
                "Invalid coordinates not handled correctly");
    }

    @Test
    public void testGetSymbolWithNullCoordinates() {

        assertThrows(IllegalArgumentException.class,
                () -> gameMap.getSymbol(null),
                "Null coordinates not handled correctly");
    }

    private long countMatches(String map, String symbol) {
        char symbolChar = symbol.toCharArray()[0];
        return map.chars().filter(c -> symbolChar == c).count();
    }
}