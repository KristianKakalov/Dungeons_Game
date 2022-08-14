package dungeons.online.command;

import java.util.Locale;

public class CommandCreator {

    public static Command newCommand(String clientInput) {
        if (clientInput == null || clientInput.isEmpty()) {
            return new Command(CommandType.UNKNOWN, "");
        }
        String[] tokens = clientInput.trim().replace(" +", " ").split(" ", 2);
        if (tokens.length < 1) {
            return new Command(CommandType.UNKNOWN, "");
        }
        try {
            String argument = (tokens.length == 2) ? tokens[1] : "";
            return new Command(CommandType.valueOf(tokens[0].toUpperCase(Locale.ROOT)), argument);
        } catch (IllegalArgumentException e) {
            return new Command(CommandType.UNKNOWN, "");
        }
    }
}
