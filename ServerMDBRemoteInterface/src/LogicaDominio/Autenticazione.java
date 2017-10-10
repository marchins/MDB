package LogicaDominio;

import java.io.Serializable;

public class Autenticazione implements Serializable {

    private static final long serialVersionUID = 100L;
    private final String username;
    private final String password;

    public Autenticazione(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
