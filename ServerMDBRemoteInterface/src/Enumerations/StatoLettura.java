package Enumerations;

public enum StatoLettura {

    LETTO("Letto"),
    IN_LETTURA("In lettura"),
    NON_LETTO("Non letto"),
    ABBANDONATO("Abbandonato"),
    NON_SPECIFICATO("Non specificato");

    private final String representation;

    StatoLettura(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    public static StatoLettura fromString(String representation) {
        for (StatoLettura s : StatoLettura.values()) {
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
