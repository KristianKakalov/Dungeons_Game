package dungeons.online.storage;

import dungeons.gameLogic.characters.Hero;
import dungeons.gameLogic.gameTactics.GameTactics;
import dungeons.gameLogic.map.Coordinates;
import dungeons.messeges.Messages;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

public class UserRepository {

    private static final int NUM_OF_PLAYERS = 9;

    private final TreeSet<Integer> availablePlayerNumbers;
    private final Map<SocketChannel, Hero> socketChannelWithHero;


    public UserRepository() {
        this.availablePlayerNumbers = new TreeSet<>();
        this.socketChannelWithHero = new HashMap<>();
        setAvailablePlayerNumbers();
    }

    private void setAvailablePlayerNumbers() {
        for (int number = 1; number <= NUM_OF_PLAYERS; number++) {
            availablePlayerNumbers.add(number);
        }
    }

    public String connectPlayer(SocketChannel socketChannel, GameTactics game) {
        if (socketChannelWithHero.size() == NUM_OF_PLAYERS) {
            return Messages.LOBBY_IS_FULL.toString();
        } else if (socketChannelWithHero.containsKey(socketChannel)) {
            return Messages.ALREADY_CONNECTED.toString();
        } else {
            Coordinates coordinates = game.getRandomFreeCoordinates();
            Integer numOfPlayer = availablePlayerNumbers.first();
            Hero hero = new Hero(numOfPlayer.toString(), coordinates);
            game.spawnHero(hero);
            availablePlayerNumbers.remove(numOfPlayer);
            socketChannelWithHero.put(socketChannel, hero);
            return String.format(Messages.CONNECTED_SUCCESSFULLY.toString(), numOfPlayer);
        }
    }

    public void removePlayer(SocketChannel socketChannel, GameTactics game) {
        if (!socketChannelWithHero.containsKey(socketChannel)) {
            return;
        }
        Hero heroToRemove = socketChannelWithHero.get(socketChannel);
        availablePlayerNumbers.add(Integer.parseInt(heroToRemove.getSymbol()));
        socketChannelWithHero.remove(socketChannel);
        if (heroToRemove.isAlive()) {
            game.removeHeroFromMap(heroToRemove);
        }
    }

    public Collection<SocketChannel> getSocketChannels() {
        return socketChannelWithHero.keySet();
    }

    public Hero getHero(SocketChannel socketChannel) {
        return socketChannelWithHero.get(socketChannel);
    }

    public boolean isPlayerPlaying(SocketChannel socketChannel) {
        return socketChannelWithHero.containsKey(socketChannel);
    }

    public Map.Entry<SocketChannel, Hero> getPlayerInfoBySymbol(String otherPlayerSymbol) {
        Optional<Map.Entry<SocketChannel, Hero>> otherPlayerOptional = socketChannelWithHero
                .entrySet()
                .stream()
                .filter(entry ->
                        entry.getValue()
                                .getSymbol()
                                .equals(otherPlayerSymbol))
                .findAny();
        return otherPlayerOptional.orElse(null);
    }
}
