package Exceptions;

import MultilanguageLabels.ErrorLabels;

public class CreazioneCategoriaException extends Exception {

    public CreazioneCategoriaException() {
        super(ErrorLabels.CATEGORY_CREATION_FAILED_ITA);
    }

}
