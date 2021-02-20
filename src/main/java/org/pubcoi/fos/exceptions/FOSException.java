package org.pubcoi.fos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FOSException extends ResponseStatusException {

    public FOSException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public FOSException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public FOSException(HttpStatus status, String message) {
        super(status, message);
    }

    public FOSException(HttpStatus status) {
        super(status);
    }
}
