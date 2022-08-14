package dungeons.gameLogic.characters.utilities;

public class Stats {

    private int health;
    private int mana;
    private int attack;
    private int defense;

    public Stats(int health, int mana, int attack, int defense) {
        this.health = health;
        this.mana = mana;
        this.attack = attack;
        this.defense = defense;
    }

    public void increaseStats(int healthIncrease, int manaIncrease, int attackIncrease, int defenseIncrease) {
        this.health += healthIncrease;
        this.mana += manaIncrease;
        this.attack += attackIncrease;
        this.defense += defenseIncrease;
    }

    public int getHealth() {
        return health;
    }

    public int getMana() {
        return mana;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }


    public void setHealth(int health) {
        this.health = health;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "health=" + health +
                ", mana=" + mana +
                ", attack=" + attack +
                ", defense=" + defense +
                '}';
    }
}
