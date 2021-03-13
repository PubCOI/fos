package org.pubcoi.fos.svc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FosException extends ResponseStatusException {

    public FosException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public FosException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public FosException(HttpStatus status, String message) {
        super(status, message);
    }

    public FosException(HttpStatus status) {
        super(status);
    }
}
