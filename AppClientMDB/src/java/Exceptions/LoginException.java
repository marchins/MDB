package Exceptions;

import MultilanguageLabels.ErrorLabels;

public class LoginException extends Exception {

    public LoginException() {
        super(ErrorLabels.LOGIN_FAILED_ITA);
    }

}
