package Exceptions;

import MultilanguageLabels.ErrorLabels;

public class ModificaInformazioniException extends Exception {

    public ModificaInformazioniException() {
        super(ErrorLabels.MODIFICATION_FAILED_ITA);
    }

}
