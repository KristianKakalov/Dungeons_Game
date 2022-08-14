package dungeons.gameLogic.characters;

import dungeons.gameLogic.map.Coordinates;
import dungeons.gameLogic.map.MapSymbols;

public class Minion extends BaseActor {

    private static final int XP_PER_KILL = 10;

    public Minion(int level, Coordinates coordinates) {
        super(MapSymbols.MINION.toString(), coordinates);
        this.level = level;
        setStatsForThatLevel();
    }

    private void setStatsForThatLevel() {
        if (level == 1) {
            return;
        }
        stats.setHealth(stats.getHealth() + (level * HEALTH_PER_LEVEL));
        stats.setMana(stats.getMana() + (level * MANA_PER_LEVEL));
        stats.setAttack(stats.getAttack() + (level * ATTACK_PER_LEVEL));
        stats.setDefense(stats.getDefense() + (level * DEFENSE_PER_LEVEL));
    }

    public int giveXP() {
        return level * XP_PER_KILL;
    }

    @Override
    public int attack() {
        return (int) (stats.getAttack() * PERCENTAGE_PER_HIT);
    }

    @Override
    public String toString() {
        return "Minion " +
                "level " + level;
    }

    public static Minion generateMinion(int level, Coordinates coordinates) {
        return new Minion(level, coordinates);
    }
}
