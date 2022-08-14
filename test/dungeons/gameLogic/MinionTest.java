package dungeons.gameLogic;

import dungeons.gameLogic.characters.Minion;
import dungeons.gameLogic.map.Coordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MinionTest {

    private final Coordinates coordinates = new Coordinates(1, 1);

    @Test
    void testGiveXP() {
        Minion minion = Minion.generateMinion(2, coordinates);
        int expected = 20;
        int actual = minion.giveXP();

        assertEquals(expected, actual, "Minion didn't give correct XP");
    }

    @Test
    void testMinionAttack() {
        Minion minion = Minion.generateMinion(1, coordinates);
        int expected = 15;
        int actual = minion.attack();

        assertEquals(expected, actual, "Minion didn't initiate correct attack damage");
    }

    @Test
    void testToString() {
        Minion minion = Minion.generateMinion(1, coordinates);
        String expected = "Minion level 1";
        String actual = minion.toString();

        assertEquals(expected,actual,"Minion string not same");
    }
}