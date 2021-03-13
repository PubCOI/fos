package org.pubcoi.fos.svc.exceptions;

import org.springframework.http.HttpStatus;

public class ItemNotFoundException extends FosException {

    public ItemNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

}
