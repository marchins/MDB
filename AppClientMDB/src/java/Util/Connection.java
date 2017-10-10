package Util;

import java.io.IOException;
import java.net.Socket;

public class Connection {

    private Connection() {
        throw new AssertionError();
    }

    public static boolean isConnectionAvailable() {

        try (Socket socket = new Socket("http://www.google.com", 80)) {
            return socket.isConnected();
        } catch (IOException e) {
            //TODO this is just a workaround if ping to google fails (proxy issue)
            return true;
        }

    }

}
