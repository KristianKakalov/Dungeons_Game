package dungeons.messeges;

public enum Messages {
    //treasure messages
    WEAPON_EQUIPPED_SUCCESSFULLY("%s equipped"),
    SPELL_LEARNT_SUCCESSFULLY("%s spell learnt"),
    LEVEL_NOT_ENOUGH("Minimum level to use %s is %d"),
    HEALTH_INCREASE_MESSAGE("+%d health"),
    MANA_INCREASE_MESSAGE("+%d mana"),
    POTION_NOT_VALID("Potion not correct type"),
    //backpack messages
    BACKPACK_FULL("Backpack is full"),
    BACKPACK_EMPTY("Backpack is empty"),
    BACKPACK_ITEM_REMOVED(" removed"),
    BACKPACK_ITEM_ADDED("%s added"),
    ITEM_NOT_FOUND_IN_BACKPACK("No item at that index"),
    //game tactics messages
    INVALID_MOVE("Invalid move"),
    SUCCESSFUL_MOVE("Player moved"),
    MINION_KILLED("Minion killed +%dXP"),
    PLAYER_KILLED("You died from "),
    PLAYER_KILLED_OTHER_PLAYER("You killed "),
    OPPONENT_BACKPACK_FULL("Other player's backpack is full"),
    ITEM_SWAPPED(" swapped"),
    NOT_ON_SAME_SPOT("Not on same coordinates"),
    //user repository
    CONNECTED_SUCCESSFULLY("You have connected as %d"),
    LOBBY_IS_FULL("The lobby is full"),
    ALREADY_CONNECTED("You are already connected"),
    HERO_NOT_FOUND("Hero not found"),
    USER_NOT_PLAYING("You are not connected to the lobby"),
    //command
    INVALID_COMMAND("Your command arguments are not valid"),
    UNKNOWN_COMMAND("Unknown command"),
    //server
    CONNECTION_LOST("%s socket connection lost");

    private final String message;

    Messages(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
    }
