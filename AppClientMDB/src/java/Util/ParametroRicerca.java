package Util;

public enum ParametroRicerca {

    TITOLO("Digitale"),
    AUTORE("Cartaceo"),
    ISBN("Isbn"),
    VALUTAZIONE("Valutazione"),
    STATO_LETTURA("Stato lettura"),
    FORMATO("Formato"),
    LIBRERIA("Libreria"),
    POSIZIONE("Posizione");

    private final String representation;

    ParametroRicerca(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    public static ParametroRicerca fromString(String representation) {
        for (ParametroRicerca s : ParametroRicerca.values()) {
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
