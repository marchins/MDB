package Exceptions;

import MultilanguageLabels.ErrorLabels;

public class CategoriaGiaEsistenteException extends Exception {

    public CategoriaGiaEsistenteException() {
        super(ErrorLabels.CATEGORY_ALREADY_EXISTS_ITA);
    }
}
