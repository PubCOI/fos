package org.pubcoi.fos.svc.exceptions;

import org.springframework.http.HttpStatus;

public class FOSBadRequestException extends FOSException {
    public FOSBadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
