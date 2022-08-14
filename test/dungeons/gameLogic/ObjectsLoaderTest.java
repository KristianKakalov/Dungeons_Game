package dungeons.gameLogic;

import dungeons.gameLogic.characters.Minion;
import dungeons.gameLogic.map.Coordinates;
import dungeons.gameLogic.objectsLoader.ObjectsLoader;
import dungeons.gameLogic.objectsLoader.exceptions.TreasureItemsNotLoadedCorrectlyException;
import dungeons.gameLogic.treasure.Potion;
import dungeons.gameLogic.treasure.TreasureItem;
import dungeons.gameLogic.treasure.TreasureType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectsLoaderTest {

    private static ObjectsLoader objectsLoader;
    private static final Path FILE_PATH = Path.of("resources" + File.separator + "TreasureItems.csv");

    @BeforeAll
    public static void setUp() {
        objectsLoader = new ObjectsLoader(FILE_PATH);
    }

    @Test
    public void testTreasureItemsLoadedCorrectly() throws IOException {
        long expected = Files.lines(FILE_PATH).count() - 1;
        long actual = objectsLoader.coordinatesOfTreasuresSet().size();

        assertEquals(expected, actual, "Treasure items not loaded correctly");
    }

    @Test
    public void testMinionsLoadedCorrectly() throws IOException {
        int expected = 5;
        int actual = objectsLoader.coordinatesOfMinionsSet().size();

        assertEquals(expected, actual, "Minions not loaded correctly");
    }

    @Test
    public void testLoadTreasureItemsWithIncorrectFilePath() {
        assertThrows(TreasureItemsNotLoadedCorrectlyException.class,
                () -> new ObjectsLoader(Path.of("incorrect path")),
                "File path not found not handled correctly");
    }

    @Test
    public void testAddGetAndRemoveTreasure() {
        Coordinates coordinates = new Coordinates(-1, -1);
        TreasureItem item =
                new Potion("potion", 1, coordinates, TreasureType.HEALTH_POTION);

        objectsLoader.addTreasure(item);

        TreasureItem expected = item;
        TreasureItem actual = objectsLoader.getTreasure(coordinates);
        assertEquals(expected, actual, "Object Loader didn't save treasure item correctly");

        objectsLoader.removeTreasure(item);
        assertNull(objectsLoader.getTreasure(coordinates),
                "Object Loader didn't remove treasure item correctly");

    }

    @Test
    public void testAddAndGetMinion() {
        Coordinates coordinates = new Coordinates(-1, -1);
        Minion minion = Minion.generateMinion(1, coordinates);

        objectsLoader.addMinion(minion);

        Minion expected = minion;
        Minion actual = objectsLoader.getMinion(coordinates);
        assertEquals(expected, actual, "Object loader didn't save minion correctly");

    }
}