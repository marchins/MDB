package Exceptions;

import MultilanguageLabels.ErrorLabels;

public class RegistrazioneException extends Exception {

    public RegistrazioneException() {
        super(ErrorLabels.REGISTRATION_ERROR_ITA);
    }

}
