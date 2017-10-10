package Exceptions;

import MultilanguageLabels.ErrorLabels;

public class CategoriaGiaAssegnataException extends Exception {

    public CategoriaGiaAssegnataException() {
        super(ErrorLabels.CATEGORY_ALREADY_ASSIGNED_ITA);
    }

}
