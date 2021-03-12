package org.pubcoi.fos.svc.exceptions;

import org.springframework.http.HttpStatus;

public class ItemNotFoundException extends FOSException {

    public ItemNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

}
