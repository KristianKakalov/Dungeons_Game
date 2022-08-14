package dungeons.gameLogic;

import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.map.Coordinates;
import dungeons.gameLogic.treasure.Potion;
import dungeons.gameLogic.treasure.Spell;
import dungeons.gameLogic.treasure.TreasureItem;
import dungeons.gameLogic.treasure.TreasureType;
import dungeons.gameLogic.treasure.Weapon;
import dungeons.messeges.Messages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeroAndTreasuresTest {

    private final Coordinates coordinates = new Coordinates(1, 1);

    @Test
    void testIncreaseXP() {
        Hero hero = new Hero("1", coordinates);
        hero.increaseXP(15);
        hero.takeDamage(130);
        assertTrue(hero.isAlive(), "Hero should be alive");
        assertEquals(2, hero.getLevel(), "Hero didn't increase level");
    }

    @Test
    void testEquipSpellAndWeapon() {
        Hero hero = new Hero("1", coordinates);
        TreasureItem weapon1 = new Weapon("weapon 1", 10, 1, coordinates);
        TreasureItem spell1 = new Spell("spell 1", 10, 1, 10, coordinates);

        weapon1.use(hero);
        spell1.use(hero);

        int expected = 35;
        int actual = hero.attack();

        assertEquals(expected, actual, "Weapon and spell don't give correct damage");
    }

    @Test
    void testEquipWeaponAndThenEquipAnotherWeapon() {
        Hero hero = new Hero("1", coordinates);
        TreasureItem weapon1 = new Weapon("weapon 1", 10, 1, coordinates);
        TreasureItem weapon2 = new Weapon("weapon 2", 10, 1, coordinates);

        weapon1.use(hero);
        weapon2.use(hero);

        TreasureItem itemFromBackpack = hero.getFromBackpack(0);

        assertEquals(weapon1, itemFromBackpack, "Item not swapped after equipping with another weapon");
    }

    @Test
    public void testUseSpellAndDrinkManaPotion() {
        Hero hero = new Hero("1", coordinates);
        TreasureItem spell = new Spell("spell", 50, 1, 100, coordinates);
        TreasureItem manaPotion = new Potion("mana potion", 100, coordinates, TreasureType.MANA_POTION);
        spell.use(hero);

        int expectedDamage = 65;
        int actualDamage = hero.attack();
        assertEquals(expectedDamage, actualDamage, "Spell not learnt");

        int expectedDamageNoSpell = 15;
        int actualDamageNoSpell = hero.attack();
        assertEquals(expectedDamageNoSpell, actualDamageNoSpell,
                "Not correct usage of spell and its mana cast point");

        manaPotion.use(hero);

        int expectedDamageAfterPotion = 65;
        int actualDamageAfterPotion = hero.attack();
        assertEquals(expectedDamageAfterPotion, actualDamageAfterPotion, "Mana potion not used correctly");
    }

    @Test
    public void testUseHealthPotion() {
        Hero hero = new Hero("1", coordinates);
        TreasureItem healthPotion = new Potion("health potion", 100, coordinates, TreasureType.HEALTH_POTION);

        hero.takeDamage(100);
        assertTrue(hero.isAlive(), "Player should be alive");

        healthPotion.use(hero);
        hero.takeDamage(100);

        assertTrue(hero.isAlive(), "Player did not consume health potion");
    }

    @Test
    public void testBackpackOverFlow() {
        Hero hero = new Hero("1", coordinates);
        for (int i = 0; i < 10; i++) {
            TreasureItem item = new Weapon("item", 1, 1, coordinates);
            String expectedMsg = "item added";
            String actualMsg = hero.addToBackpack(item);
            assertEquals(expectedMsg, actualMsg);
        }
        TreasureItem item = new Weapon("item", 1, 1, coordinates);
        String expected = Messages.BACKPACK_FULL.toString();
        String actual = hero.addToBackpack(item);
        String message = "Backpack capacity exceeds maximum";

        assertEquals(expected, actual, message);
        assertTrue(hero.isBackpackFull(), message);
    }

    @Test
    public void testGetRandomItemFromBackpack() {
        Hero hero = new Hero("1", coordinates);
        for (int i = 0; i < 10; i++) {
            TreasureItem item = new Weapon("item", 1, 1, coordinates);
            hero.addToBackpack(item);
        }
        TreasureItem expected = new Weapon("item", 1, 1, coordinates);
        TreasureItem actual = hero.dropItemFromBackpack();

        assertEquals(expected, actual, "Backpack dropped incorrect item");
        assertFalse(hero.isBackpackFull(), "Backpack should remove dropped item");
    }

    @Test
    public void testGetRandomItemFromEmptyBackpack() {
        Hero hero = new Hero("1", coordinates);

        TreasureItem actual = hero.dropItemFromBackpack();
        assertNull(actual, "Backpack is empty");
    }

    @Test
    public void testRemoveItemFromBackpackAndDisplayIt() {
        Hero hero = new Hero("1", coordinates);

        TreasureItem weapon1 = new Weapon("weapon 1", 10, 1, coordinates);
        TreasureItem weapon2 = new Weapon("weapon 2", 10, 1, coordinates);

        hero.addToBackpack(weapon1);
        hero.addToBackpack(weapon2);

        hero.removeFromBackpack(1);

        String expected = "0. WEAPON weapon 1, Damage=10, level=1" + System.lineSeparator();
        String actual = hero.displayBackpack();

        assertEquals(expected, actual, "Backpack not displayed correctly");
    }

    @Test
    public void testDisplayEmptyBackpack() {
        Hero hero = new Hero("1", coordinates);

        String expected = Messages.BACKPACK_EMPTY.toString();
        String actual = hero.displayBackpack();
        assertEquals(expected, actual, "Backpack not displayed correctly");
    }

    @Test
    public void testDisplayHero() {
        Hero hero = new Hero("1", coordinates);
        TreasureItem weapon = new Weapon("weapon", 10, 1, coordinates);
        TreasureItem spell = new Spell("spell", 50, 1, 100, coordinates);
        weapon.use(hero);
        spell.use(hero);

        String expected = "Hero 1, Level= 1, Weapon= WEAPON weapon, " +
                "Damage=10, level=1, Spell= SPELL spell, Damage=50, level=1, " +
                "minMana=100, Stats{health=100, mana=100, attack=50, defense=50}";
        String actual = hero.toString();

        assertEquals(expected, actual, "Hero not displayed correctly");
    }

    @Test
    public void testHeroCoordinates() {
        Hero hero = new Hero("1", coordinates);
        Coordinates newCoordinates = Coordinates.generateCoordinates(10, 10);

        hero.setCoordinates(newCoordinates);

        Coordinates expected = newCoordinates;
        Coordinates actual = hero.getCoordinates();

        assertEquals(expected, actual, "Coordinates not set correctly");
    }

    @Test
    public void testHeroWhenAttackedIfHeDies() {
        Hero hero = new Hero("1", coordinates);
        TreasureItem healthPotion = new Potion("health potion", 1000, coordinates, TreasureType.HEALTH_POTION);

        healthPotion.use(hero);
        for (int i = 0; i < 5; i++) {
            hero.takeDamage(60);
            assertTrue(hero.isAlive(), "Hero don't absorb damage");
        }

        hero.takeDamage(1000);
        assertFalse(hero.isAlive(), "Hero should be dead");
    }

    @Test
    public void testTakeDamageWithNegativeNum() {
        Hero hero = new Hero("1", coordinates);

        assertThrows(IllegalArgumentException.class,
                () -> hero.takeDamage(-1));
    }

    @Test
    public void testLevelNotEnoughTreasureItems() {
        Hero hero = new Hero("1", coordinates);

        TreasureItem weapon1 = new Weapon("weapon 1", 10, 2, coordinates);
        TreasureItem spell1 = new Spell("spell 1", 10, 2, 10, coordinates);

        String expectedWeaponMsg = "Minimum level to use weapon 1 is 2";
        String actualWeaponMsg = weapon1.use(hero);

        String expectedSpellMsg = "Minimum level to use spell 1 is 2";
        String actualSpellMsg = spell1.use(hero);

        assertEquals(expectedWeaponMsg, actualWeaponMsg, "Hero level not enough to use weapon");
        assertEquals(expectedSpellMsg, actualSpellMsg, "Hero level not enough to use spell");
    }
}