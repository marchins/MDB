package Enumerations;

public enum Formato {

    DIGITALE("Digitale"),
    CARTACEO("Cartaceo");

    private final String representation;

    Formato(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    public static Formato fromString(String representation) {
        for (Formato s : Formato.values()) {
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
