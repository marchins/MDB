package Exceptions;

import MultilanguageLabels.ErrorLabels;

public class ConnectionErrorException extends Exception {

    public ConnectionErrorException() {
        super(ErrorLabels.CONNECTION_ERROR_ITA);
    }
}
