package Exceptions;

import MultilanguageLabels.ErrorLabels;

public class RimozioneCategoriaException extends Exception {

    public RimozioneCategoriaException() {
        super(ErrorLabels.CATEGORY_REMOVAL_FAILED_ITA);
    }
}
