package org.pubcoi.fos.svc.exceptions;

import org.springframework.http.HttpStatus;

public class FosBadRequestException extends FosException {
    public FosBadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public FosBadRequestException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
