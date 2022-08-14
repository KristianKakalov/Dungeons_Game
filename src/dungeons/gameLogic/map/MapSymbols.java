package dungeons.gameLogic.map;

public enum MapSymbols {
    OBSTACLE("#"),
    TREASURE("T"),
    FREE_SPOT("."),
    MINION("M");

    private final String symbol;

    MapSymbols(String symbol) {
        this.symbol = symbol;
    }


    public static MapSymbols fromString(String text) {
        for (MapSymbols mapSymbol : MapSymbols.values()) {
            if (mapSymbol.symbol.equalsIgnoreCase(text)) {
                return mapSymbol;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return symbol;
    }
}