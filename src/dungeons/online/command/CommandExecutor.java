package dungeons.online.command;

import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.gameTactics.Direction;
import dungeons.gameLogic.gameTactics.GameTactics;
import dungeons.messeges.Messages;
import dungeons.online.storage.UserRepository;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Map;

public class CommandExecutor {

    private final GameTactics game;
    private final UserRepository userRepository;

    private static final int BUFFER_SIZE = 1024;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public CommandExecutor(GameTactics game, UserRepository userRepository) {
        this.game = game;
        this.userRepository = userRepository;
    }

    public String execute(Command cmd, SocketChannel socketChannel) {
        if (!userRepository.isPlayerPlaying(socketChannel)) {
            return Messages.USER_NOT_PLAYING.toString();
        }

        return switch (cmd.type()) {
            case UP, DOWN, LEFT, RIGHT -> movePlayer(cmd, socketChannel);
            case BACKPACK -> displayBackpack(socketChannel);
            case STATS -> displayStats(socketChannel);
            case USE -> useItemFromBackpack(cmd, socketChannel);
            case REMOVE -> removeItemFromBackpack(cmd, socketChannel);
            case SWAP, FIGHT -> interactWithPlayer(cmd, socketChannel);
            default -> Messages.UNKNOWN_COMMAND.toString();
        };
    }

    public String connectPlayer(SocketChannel socketChannel) {
        String message = userRepository.connectPlayer(socketChannel, game);
        updateMap();
        return message;
    }

    public void disconnectPlayer(SocketChannel clientChannel) {
        if (!userRepository.isPlayerPlaying(clientChannel)) {
            return;
        }
        userRepository.removePlayer(clientChannel, game);
        updateMap();
    }

    private String movePlayer(Command cmd, SocketChannel socketChannel) {
        Hero hero = userRepository.getHero(socketChannel);
        Direction direction = Direction.valueOf(cmd.type().name());
        String message = game.moveHero(hero, direction);
        if ((!message.equals(Messages.INVALID_MOVE.toString())) && hero.isAlive()) {
            updateMap();
        }
        if (!hero.isAlive()) {
            disconnectPlayer(socketChannel);
        }
        return message;
    }

    private String displayBackpack(SocketChannel socketChannel) {
        Hero hero = userRepository.getHero(socketChannel);
        return hero.displayBackpack();
    }

    private String displayStats(SocketChannel socketChannel) {
        Hero hero = userRepository.getHero(socketChannel);
        return hero.displayStats();
    }

    private String useItemFromBackpack(Command cmd, SocketChannel socketChannel) {
        try {
            int index = convertCommandArgumentToInt(cmd);
            Hero hero = userRepository.getHero(socketChannel);
            return game.heroUseItemFromBackpack(hero, index);
        } catch (NumberFormatException e) {
            return Messages.INVALID_COMMAND.toString();
        }
    }

    private String removeItemFromBackpack(Command cmd, SocketChannel socketChannel) {
        try {
            int index = convertCommandArgumentToInt(cmd);
            Hero hero = userRepository.getHero(socketChannel);
            String message = game.heroRemoveItemFromBackpack(hero, index);
            if (message.contains(Messages.BACKPACK_ITEM_REMOVED.toString())) {
                updateMap();
            }
            return message;
        } catch (NumberFormatException e) {
            return Messages.INVALID_COMMAND.toString();
        }
    }

    private String interactWithPlayer(Command cmd, SocketChannel socketChannel) {
        Hero hero = userRepository.getHero(socketChannel);
        String otherPlayerSymbol = game.getOtherPlayerSymbol(hero);

        if (otherPlayerSymbol.equals(Messages.NOT_ON_SAME_SPOT.toString())) {
            return Messages.NOT_ON_SAME_SPOT.toString();
        }

        Map.Entry<SocketChannel, Hero> otherPlayer =
                userRepository.getPlayerInfoBySymbol(otherPlayerSymbol);
        if (otherPlayer == null) {
            return Messages.HERO_NOT_FOUND.toString();
        }

        if (cmd.type() == CommandType.SWAP) {
            return swapItemWithPlayer(cmd, hero, otherPlayer);
        } else {
            return fightWithPlayer(hero, socketChannel, otherPlayer);
        }
    }

    private String swapItemWithPlayer(Command cmd, Hero initiator, Map.Entry<SocketChannel, Hero> otherPlayer) {
        try {
            int index = convertCommandArgumentToInt(cmd);
            String message = game.swapItem(initiator, otherPlayer.getValue(), index);
            if (message.contains(Messages.ITEM_SWAPPED.toString())) {
                writeClientOutput(otherPlayer.getKey(), message);
            }
            return message;

        } catch (NumberFormatException e) {
            return Messages.INVALID_COMMAND.toString();
        }
    }

    private String fightWithPlayer(Hero initiator, SocketChannel socketChannelInitiator,
                                   Map.Entry<SocketChannel, Hero> otherPlayer) {
        Hero otherPlayerHero = otherPlayer.getValue();
        String message = game.heroesFight(initiator, otherPlayerHero);
        if (message.contains(Messages.PLAYER_KILLED.toString())) {
            disconnectPlayer(socketChannelInitiator);
            writeClientOutput(otherPlayer.getKey(),
                    Messages.PLAYER_KILLED_OTHER_PLAYER.toString() + initiator);

        } else {
            disconnectPlayer(otherPlayer.getKey());
            writeClientOutput(otherPlayer.getKey(),
                    Messages.PLAYER_KILLED.toString() + initiator);
        }
        return message;
    }

    private int convertCommandArgumentToInt(Command cmd) {
        return Integer.parseInt(cmd.argument());
    }

    private void updateMap() {
        String map = game.getMap();
        Collection<SocketChannel> socketChannels = userRepository.getSocketChannels();

        for (SocketChannel socketChannelRecipient : socketChannels) {
            writeClientOutput(socketChannelRecipient, map);
        }
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) {
        output += System.lineSeparator();
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        try {
            clientChannel.write(buffer);
        } catch (IOException e) {
            //The server will close this client channel
        }
    }
}
