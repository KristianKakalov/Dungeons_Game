package dungeons.gameLogic;

import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.characters.Minion;
import dungeons.gameLogic.gameTactics.Direction;
import dungeons.gameLogic.gameTactics.GameTactics;
import dungeons.gameLogic.map.Coordinates;
import dungeons.gameLogic.map.GameMap;
import dungeons.gameLogic.map.exceptions.InvalidMapCoordinatesException;
import dungeons.gameLogic.objectsLoader.ObjectsLoader;
import dungeons.gameLogic.treasure.Potion;
import dungeons.gameLogic.treasure.TreasureItem;
import dungeons.gameLogic.treasure.TreasureType;
import dungeons.messeges.Messages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameTacticsTest {

    private GameMap gameMapMock = Mockito.mock(GameMap.class);
    private ObjectsLoader objectsLoaderMock = Mockito.mock(ObjectsLoader.class);

    private GameTactics gameTactics = new GameTactics(gameMapMock, objectsLoaderMock);


    @Test
    public void testMoveHeroToFreeSpot() {
        Hero hero = new Hero("1", new Coordinates(0, 0));
        when(gameMapMock.getSymbol(new Coordinates(0, 1))).thenReturn(".");
        when(gameMapMock.getSymbol(new Coordinates(0, 0))).thenReturn("1");

        String expectedMsg = Messages.SUCCESSFUL_MOVE.toString();
        String actualMsg = gameTactics.moveHero(hero, Direction.DOWN);

        Coordinates expected = new Coordinates(0, 1);
        Coordinates actual = hero.getCoordinates();

        String message = "Hero not moved correctly to free spot";
        assertEquals(expectedMsg, actualMsg, message);
        assertEquals(expected, actual, message);
    }

    @Test
    public void testMoveHeroToObstacle() {
        Hero hero = new Hero("1", new Coordinates(0, 0));
        when(gameMapMock.getSymbol(new Coordinates(0, 1))).thenReturn("#");
        when(gameMapMock.getSymbol(new Coordinates(0, 0))).thenReturn("1");

        String expectedMsg = Messages.INVALID_MOVE.toString();
        String actualMsg = gameTactics.moveHero(hero, Direction.DOWN);

        Coordinates expected = new Coordinates(0, 0);
        Coordinates actual = hero.getCoordinates();

        String message = "Hero should not be moved";
        assertEquals(expectedMsg, actualMsg, message);
        assertEquals(expected, actual, message);
    }

    @Test
    public void testMoveHeroToTreasure() {
        Hero hero = new Hero("1", new Coordinates(0, 0));
        Coordinates itemCoordinates = new Coordinates(0, 1);
        TreasureItem item = new Potion("potion", 10, itemCoordinates, TreasureType.HEALTH_POTION);
        when(gameMapMock.getSymbol(itemCoordinates)).thenReturn("T");
        when(gameMapMock.getSymbol(new Coordinates(0, 0))).thenReturn("1");
        when(objectsLoaderMock.getTreasure(itemCoordinates)).thenReturn(item);

        String expectedMsg = "potion added";
        String actualMsg = gameTactics.moveHero(hero, Direction.DOWN);

        Coordinates expectedCoordinates = new Coordinates(0, 1);
        Coordinates actualCoordinates = hero.getCoordinates();

        TreasureItem expectedItem = item;
        TreasureItem actualItem = hero.getFromBackpack(0);

        String message = "Hero should have collected treasure";
        assertEquals(expectedMsg, actualMsg, message);
        assertEquals(expectedCoordinates, actualCoordinates, message);
        assertEquals(expectedItem, actualItem, message);
    }

    @Test
    public void testMoveHeroToMinionAndKillIt() {
        Hero hero = new Hero("1", new Coordinates(0, 0));
        Coordinates minionCoordinates = new Coordinates(0, 1);
        Minion minion = Minion.generateMinion(1, minionCoordinates);
        when(gameMapMock.getSymbol(new Coordinates(0, 1))).thenReturn("M");
        when(gameMapMock.getSymbol(new Coordinates(0, 0))).thenReturn("1");
        when(objectsLoaderMock.getMinion(minionCoordinates)).thenReturn(minion);

        String expectedMsg = "Minion killed +10XP";
        String actualMsg = gameTactics.moveHero(hero, Direction.DOWN);

        Coordinates expected = new Coordinates(0, 1);
        Coordinates actual = hero.getCoordinates();

        String message = "Hero should have killed minion";
        assertEquals(expectedMsg, actualMsg, message);
        assertEquals(expected, actual, message);
        assertTrue(hero.isAlive(), message);
    }

    @Test
    public void testMoveHeroToMinionAndDie() {
        Hero hero = new Hero("1", new Coordinates(0, 0));
        Coordinates minionCoordinates = new Coordinates(0, 1);
        Minion minion = Minion.generateMinion(5, minionCoordinates);
        when(gameMapMock.getSymbol(new Coordinates(0, 1))).thenReturn("M");
        when(gameMapMock.getSymbol(new Coordinates(0, 0))).thenReturn("1");
        when(objectsLoaderMock.getMinion(minionCoordinates)).thenReturn(minion);

        String expectedMsg = "You died from Minion level 5";
        String actualMsg = gameTactics.moveHero(hero, Direction.DOWN);

        String message = "Hero should have died from minion";
        assertEquals(expectedMsg, actualMsg, message);
        assertFalse(hero.isAlive(), message);
    }

    @Test
    public void testMoveHeroToOtherHero() {
        Hero hero = new Hero("1", new Coordinates(0, 0));
        when(gameMapMock.getSymbol(new Coordinates(0, 1))).thenReturn("2");
        when(gameMapMock.getSymbol(new Coordinates(0, 0))).thenReturn("1");

        String expectedMsg = Messages.SUCCESSFUL_MOVE.toString();
        String actualMsg = gameTactics.moveHero(hero, Direction.DOWN);

        Coordinates expected = new Coordinates(0, 1);
        Coordinates actual = hero.getCoordinates();

        String message = "Hero not moved correctly";
        assertEquals(expectedMsg, actualMsg, message);
        assertEquals(expected, actual, message);
    }

    @Test
    public void testMoveHeroWithInvalidCoordinates() {
        Hero hero = new Hero("1", new Coordinates(0, 0));
        when(gameMapMock.getSymbol(new Coordinates(-1, 0))).thenThrow(InvalidMapCoordinatesException.class);
        when(gameMapMock.getSymbol(new Coordinates(0, 0))).thenReturn("1");

        String expectedMsg = Messages.INVALID_MOVE.toString();
        String actualMsg = gameTactics.moveHero(hero, Direction.LEFT);

        Coordinates expected = new Coordinates(0, 0);
        Coordinates actual = hero.getCoordinates();

        String message = "Hero should not be moved";
        assertEquals(expectedMsg, actualMsg, message);
        assertEquals(expected, actual, message);
    }

    @Test
    public void testSwapItemSuccessfully() {
        Coordinates coordinates = new Coordinates(0, 0);
        Hero hero1 = new Hero("1", coordinates);
        Hero hero2 = new Hero("2", coordinates);
        TreasureItem item = new Potion("potion", 10, coordinates, TreasureType.HEALTH_POTION);
        hero1.addToBackpack(item);

        String expectedMsg = "potion swapped";
        String actualMsg = gameTactics.swapItem(hero1, hero2, 0);

        TreasureItem expectedItem = item;
        TreasureItem actualItem = hero2.getFromBackpack(0);

        String message = "Swap not successful";
        assertEquals(expectedMsg, actualMsg, message);
        assertEquals(expectedItem, actualItem, message);
        assertThrows(IndexOutOfBoundsException.class,
                () -> hero1.getFromBackpack(0),
                "Hero should have removed item after swap");
    }

    @Test
    public void testSwapItemWithHeroFullBackpack() {
        Coordinates coordinates = new Coordinates(0, 0);
        Hero hero1 = new Hero("1", coordinates);
        Hero hero2 = new Hero("2", coordinates);
        fillUpHeroBackpack(hero2);
        TreasureItem item = new Potion("potion", 10, coordinates, TreasureType.HEALTH_POTION);
        hero1.addToBackpack(item);

        String expectedMsg = Messages.OPPONENT_BACKPACK_FULL.toString();
        String actualMsg = gameTactics.swapItem(hero1, hero2, 0);

        TreasureItem expectedItem = item;
        TreasureItem actualItem = hero1.getFromBackpack(0);

        String message = "Swap shouldn't be successful";
        assertEquals(expectedMsg, actualMsg, message);
        assertEquals(expectedItem, actualItem, message);
    }

    @Test
    public void testSwapItemWithHeroesNotOnSameSpot() {
        Hero hero1 = new Hero("1", new Coordinates(0, 0));
        Hero hero2 = new Hero("2", new Coordinates(1, 1));

        String expectedMsg = Messages.NOT_ON_SAME_SPOT.toString();
        String actualMsg = gameTactics.swapItem(hero1, hero2, 0);

        String message = "Swap shouldn't be successful";
        assertEquals(expectedMsg, actualMsg, message);
        assertThrows(IndexOutOfBoundsException.class,
                () -> hero2.getFromBackpack(0),
                "Hero should not have item");
    }

    @Test
    public void testSwapItemWithInvalidIndex() {
        Coordinates coordinates = new Coordinates(0, 0);
        Hero hero1 = new Hero("1", coordinates);
        Hero hero2 = new Hero("2", coordinates);
        TreasureItem item = new Potion("potion", 10, coordinates, TreasureType.HEALTH_POTION);
        hero1.addToBackpack(item);

        String expectedMsg = Messages.ITEM_NOT_FOUND_IN_BACKPACK.toString();
        String actualMsg = gameTactics.swapItem(hero1, hero2, 3);

        TreasureItem expectedItem = item;
        TreasureItem actualItem = hero1.getFromBackpack(0);

        String message = "Swap shouldn't be successful";
        assertEquals(expectedMsg, actualMsg, message);
        assertEquals(expectedItem, actualItem, message);
        assertThrows(IndexOutOfBoundsException.class,
                () -> hero2.getFromBackpack(0),
                "Hero should not have item");
    }

    @Test
    public void testHeroUseItemFromBackpack() {
        Coordinates coordinates = new Coordinates(0, 0);
        Hero hero = new Hero("1", coordinates);
        TreasureItem item = new Potion("potion", 10, coordinates, TreasureType.HEALTH_POTION);
        hero.addToBackpack(item);

        String expectedMsg = "+10 health";
        String actualMsg = gameTactics.heroUseItemFromBackpack(hero, 0);

        String message = "Hero didn't use item from backpack correctly";
        assertEquals(expectedMsg, actualMsg, message);
        assertThrows(IndexOutOfBoundsException.class,
                () -> hero.getFromBackpack(0),
                "Hero should not have item");
    }

    @Test
    public void testHeroUseItemFromBackpackInvalidIndex() {
        Coordinates coordinates = new Coordinates(0, 0);
        Hero hero = new Hero("1", coordinates);

        String expectedMsg = Messages.ITEM_NOT_FOUND_IN_BACKPACK.toString();
        String actualMsg = gameTactics.heroUseItemFromBackpack(hero, 0);

        String message = "Invalid index not handled correctly";
        assertEquals(expectedMsg, actualMsg, message);
    }

    @Test
    public void testHeroRemoveItemFromBackpack() {
        Coordinates coordinates = new Coordinates(0, 0);
        Hero hero = new Hero("1", coordinates);
        TreasureItem item = new Potion("potion", 10, coordinates, TreasureType.HEALTH_POTION);
        hero.addToBackpack(item);

        String expectedMsg = "potion removed";
        String actualMsg = gameTactics.heroRemoveItemFromBackpack(hero, 0);

        String message = "Hero didn't remove item from backpack correctly";
        assertEquals(expectedMsg, actualMsg, message);
        assertThrows(IndexOutOfBoundsException.class,
                () -> hero.getFromBackpack(0),
                "Hero should not have item");
    }

    @Test
    public void testHeroRemoveItemFromBackpackInvalidIndex() {
        Coordinates coordinates = new Coordinates(0, 0);
        Hero hero = new Hero("1", coordinates);

        String expectedMsg = Messages.ITEM_NOT_FOUND_IN_BACKPACK.toString();
        String actualMsg = gameTactics.heroRemoveItemFromBackpack(hero, 0);

        String message = "Invalid index not handled correctly";
        assertEquals(expectedMsg, actualMsg, message);
    }

    @Test
    public void testHeroesFightNotOnSameSpot() {
        Hero hero1 = new Hero("1", new Coordinates(0, 0));
        Hero hero2 = new Hero("2", new Coordinates(1, 1));

        String expectedMsg = Messages.NOT_ON_SAME_SPOT.toString();
        String actualMsg = gameTactics.heroesFight(hero1, hero2);

        assertEquals(expectedMsg, actualMsg, "Fight shouldn't be successful");
    }

    @Test
    public void testHeroesFightHero1Win() {
        Coordinates coordinates = new Coordinates(0, 0);
        Hero hero1 = new Hero("1", coordinates);
        Hero hero2 = new Hero("2", coordinates);
        when(gameMapMock.getSymbol(coordinates)).thenReturn("12");

        TreasureItem item = new Potion("potion", 100, coordinates, TreasureType.HEALTH_POTION);
        item.use(hero1);

        gameTactics.heroesFight(hero1, hero2);

        String message = "Fight not successful";
        assertFalse(hero2.isAlive(), message);
        assertTrue(hero1.isAlive(), message);
    }

    @Test
    public void testHeroesFightHero2Win() {
        Coordinates coordinates = new Coordinates(0, 0);
        Hero hero1 = new Hero("1", coordinates);
        Hero hero2 = new Hero("2", coordinates);
        when(gameMapMock.getSymbol(coordinates)).thenReturn("12");

        TreasureItem item = new Potion("potion", 100, coordinates, TreasureType.HEALTH_POTION);
        TreasureItem itemToBackpack = new Potion("potion 2", 100, coordinates, TreasureType.HEALTH_POTION);
        item.use(hero2);
        hero1.addToBackpack(itemToBackpack);

        gameTactics.heroesFight(hero1, hero2);

        String message = "Fight not successful";
        assertFalse(hero1.isAlive(), message);
        assertTrue(hero2.isAlive(), message);
    }

    @Test
    public void testGetOtherPlayerSymbol() {
        Hero hero = new Hero("1", new Coordinates(0, 0));
        when(gameMapMock.getSymbol(hero.getCoordinates())).thenReturn("12");

        String expectedSymbol = "2";
        String actualSymbol = gameTactics.getOtherPlayerSymbol(hero);

        assertEquals(expectedSymbol, actualSymbol, "Symbol extracted incorrectly");
    }

    @Test
    public void testGetOtherPlayerSymbolNotOnSameCoordinates() {
        Hero hero = new Hero("1", new Coordinates(0, 0));
        when(gameMapMock.getSymbol(hero.getCoordinates())).thenReturn("1");

        String expectedSymbol = Messages.NOT_ON_SAME_SPOT.toString();
        String actualSymbol = gameTactics.getOtherPlayerSymbol(hero);

        assertEquals(expectedSymbol, actualSymbol, "Symbol extracted incorrectly");
    }

    @Test
    public void testGetOtherPlayerSymbolOnCoordinatesWithTreasure() {
        Hero hero = new Hero("1", new Coordinates(0, 0));
        when(gameMapMock.getSymbol(hero.getCoordinates())).thenReturn("1T");

        String expectedSymbol = Messages.NOT_ON_SAME_SPOT.toString();
        String actualSymbol = gameTactics.getOtherPlayerSymbol(hero);

        assertEquals(expectedSymbol, actualSymbol, "Symbol extracted incorrectly");
    }

    private void fillUpHeroBackpack(Hero hero) {
        for (int i = 0; i < 10; i++) {
            Coordinates coordinates = new Coordinates(i, i);
            TreasureItem item = new Potion("potion", 10, coordinates, TreasureType.HEALTH_POTION);
            hero.addToBackpack(item);
        }
    }
}