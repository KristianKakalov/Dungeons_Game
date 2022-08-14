package dungeons.gameLogic.treasure;

import dungeons.messeges.Messages;
import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.map.Coordinates;

import java.util.Objects;

public class Weapon extends BaseTreasureItem {

    private final int level;

    public Weapon(String name, int points, int level, Coordinates coordinates) {
        super(name, points, coordinates);
        this.level = level;
        this.treasureType = TreasureType.WEAPON;
    }

    @Override
    public String use(Hero hero) {
        if (hero.getLevel() >= this.level) {
            hero.equipWeapon(this);
            return String.format(Messages.WEAPON_EQUIPPED_SUCCESSFULLY.toString(), name);
        } else {
            return String.format(Messages.LEVEL_NOT_ENOUGH.toString(), name, level);
        }
    }

    @Override
    public String toString() {
        return treasureType + " " +
                name +
                ", Damage=" + points +
                ", level=" + level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Weapon weapon = (Weapon) o;
        return level == weapon.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), level);
    }
}
