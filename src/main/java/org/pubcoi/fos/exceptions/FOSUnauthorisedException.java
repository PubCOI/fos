package org.pubcoi.fos.exceptions;

import org.springframework.http.HttpStatus;

public class FOSUnauthorisedException extends FOSException {
    public FOSUnauthorisedException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
