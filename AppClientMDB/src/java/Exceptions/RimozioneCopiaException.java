package Exceptions;

import MultilanguageLabels.ErrorLabels;

public class RimozioneCopiaException extends Exception {

    public RimozioneCopiaException() {
        super(ErrorLabels.COPY_REMOVAL_FAILED_ITA);
    }

}
