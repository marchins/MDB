package Enumerations;

public enum Valutazione {

    UNO("Uno"),
    DUE("Due"),
    TRE("Tre"),
    QUATTRO("Quattro"),
    CINQUE("Cinque"),
    NON_VALUTATO("Non valutato");

    private final String representation;

    Valutazione(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    public static Valutazione fromString(String representation) {
        for (Valutazione s : Valutazione.values()) {
            if (s.getRepresentation().equals(representation)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No value associated with given representation exists");
    }

    @Override
    public String toString() {
        return this.getRepresentation();
    }

}
