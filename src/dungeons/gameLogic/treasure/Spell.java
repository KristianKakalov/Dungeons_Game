package dungeons.gameLogic.treasure;

import dungeons.messeges.Messages;
import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.map.Coordinates;

import java.util.Objects;

public class Spell extends BaseTreasureItem {

    private final int level;
    private final int minMana;

    public Spell(String name, int points, int level, int minMana, Coordinates coordinates) {
        super(name, points, coordinates);
        this.level = level;
        this.minMana = minMana;
        this.treasureType = TreasureType.SPELL;
    }

    @Override
    public String use(Hero hero) {
        if (hero.getLevel() >= this.level) {
            hero.learnSpell(this);
            return String.format(Messages.SPELL_LEARNT_SUCCESSFULLY.toString(), name);
        } else {
            return String.format(Messages.LEVEL_NOT_ENOUGH.toString(), name, level);
        }
    }

    public int getMinMana() {
        return minMana;
    }

    @Override
    public String toString() {
        return treasureType + " " + name +
                ", Damage=" + points +
                ", level=" + level +
                ", minMana=" + minMana;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Spell spell = (Spell) o;
        return level == spell.level && minMana == spell.minMana;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), level, minMana);
    }
}
