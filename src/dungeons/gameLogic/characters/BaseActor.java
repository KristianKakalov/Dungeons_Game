package dungeons.gameLogic.characters;

import dungeons.gameLogic.map.Coordinates;
import dungeons.gameLogic.characters.utilities.Stats;

public abstract class BaseActor implements Actor {

    protected int level;
    protected boolean alive;
    protected String actorSymbol;
    protected Stats stats;
    protected Coordinates coordinates;

    protected static final double PERCENTAGE_PER_HIT = 0.3;
    protected static final double PERCENTAGE_DEFENSE_ABSORPTION = 0.2;

    private static final int HEALTH_START_STAT = 100;
    private static final int MANA_START_STAT = 100;
    private static final int ATTACK_START_STAT = 50;
    private static final int DEFENSE_START_STAT = 50;

    protected static final int XP_PER_LEVEL = 15;
    protected static final int HEALTH_PER_LEVEL = 10;
    protected static final int MANA_PER_LEVEL = 10;
    protected static final int ATTACK_PER_LEVEL = 5;
    protected static final int DEFENSE_PER_LEVEL = 5;

    public BaseActor(String actorSymbol, Coordinates coordinates) {
        this.actorSymbol = actorSymbol;
        this.coordinates = coordinates;
        this.stats = new Stats(HEALTH_START_STAT, MANA_START_STAT, ATTACK_START_STAT, DEFENSE_START_STAT);
        this.alive = true;
    }

    @Override
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Negative number passed");
        }
        int absorbedDamage = (int) (damage * PERCENTAGE_DEFENSE_ABSORPTION);
        if (absorbedDamage > stats.getDefense()) {
            absorbedDamage = stats.getDefense();
        }
        int damageTaken = damage - absorbedDamage;
        stats.setHealth(stats.getHealth() - damageTaken);
        stats.setDefense(stats.getDefense() - absorbedDamage);

        if (stats.getHealth() <= 0) {
            this.alive = false;
            stats.setHealth(0);
        }
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public String getSymbol() {
        return actorSymbol;
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }
}
