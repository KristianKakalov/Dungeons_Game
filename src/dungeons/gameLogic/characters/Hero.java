package dungeons.gameLogic.characters;

import dungeons.gameLogic.map.Coordinates;
import dungeons.gameLogic.treasure.Potion;
import dungeons.gameLogic.treasure.Spell;
import dungeons.gameLogic.treasure.TreasureItem;
import dungeons.gameLogic.treasure.TreasureType;
import dungeons.gameLogic.treasure.Weapon;
import dungeons.messeges.Messages;
import dungeons.gameLogic.characters.utilities.Backpack;

public class Hero extends BaseActor {

    private int experience;
    private Weapon weapon;
    private Spell spell;
    private final Backpack backpack;
    private static final int START_LEVEL = 1;

    public Hero(String actorSymbol, Coordinates coordinates) {
        super(actorSymbol, coordinates);
        this.backpack = new Backpack();
        this.level = START_LEVEL;
    }

    public void increaseXP(int xp) {
        experience += xp;
        if (experience >= XP_PER_LEVEL) {
            experience = experience % XP_PER_LEVEL;
            level++;
            stats.increaseStats(HEALTH_PER_LEVEL, MANA_PER_LEVEL, ATTACK_PER_LEVEL, DEFENSE_PER_LEVEL);
        }
    }

    @Override
    public int attack() {
        int weaponDamage = (weapon == null) ? 0 : weapon.getPoints();
        int spellDamage = 0;
        if (spell != null && spell.getMinMana() <= stats.getMana()) {
            spellDamage = spell.getPoints();
            stats.setMana(stats.getMana() - spell.getMinMana());
            if (stats.getMana() < 0) {
                stats.setMana(0);
            }
        }
        return (int) (stats.getAttack() * PERCENTAGE_PER_HIT)
                + weaponDamage
                + spellDamage;
    }


    public void equipWeapon(Weapon weapon) {
        backpack.removeItem(weapon);
        if (this.weapon != null) {
            addToBackpack(this.weapon);
        }
        this.weapon = weapon;
    }

    public void learnSpell(Spell spell) {
        backpack.removeItem(spell);
        if (this.spell != null) {
            addToBackpack(this.spell);
        }
        this.spell = spell;
    }

    public String drinkPotion(Potion potion) {
        backpack.removeItem(potion);
        if (potion.getType() == TreasureType.HEALTH_POTION) {
            stats.setHealth(stats.getHealth() + potion.getPoints());
            return String.format(Messages.HEALTH_INCREASE_MESSAGE.toString(), potion.getPoints());
        }
        if (potion.getType() == TreasureType.MANA_POTION) {
            stats.setMana(stats.getMana() + potion.getPoints());
            return String.format(Messages.MANA_INCREASE_MESSAGE.toString(), potion.getPoints());
        }
        return Messages.POTION_NOT_VALID.toString();
    }

    public String addToBackpack(TreasureItem item) {
        return backpack.addItem(item);
    }

    public TreasureItem getFromBackpack(int index) {
        return backpack.getItem(index);
    }

    public String removeFromBackpack(int index) {
        return backpack.removeItem(backpack.getItem(index));
    }

    public boolean isBackpackFull() {
        return backpack.isBackpackFull();
    }

    public String displayBackpack() {
        return backpack.toString();
    }

    public TreasureItem dropItemFromBackpack() {
        return backpack.dropRandomTreasure();
    }

    public String displayStats() {
        return "Level= " + level +
                ", Weapon= " + weapon +
                ", Spell= " + spell +
                ", " + stats;
    }

    @Override
    public String toString() {
        return "Hero " + getSymbol() + ", " + displayStats();
    }
}
