package Enumerations;

public enum SocialNetwork {

    FACEBOOK("Facebook"),
    GOOGLE_PLUS("Google Plus"),
    TWITTER("Twitter");

    private final String representation;

    SocialNetwork(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    public static SocialNetwork fromString(String representation) {
        for (SocialNetwork s : SocialNetwork.values()) {
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
