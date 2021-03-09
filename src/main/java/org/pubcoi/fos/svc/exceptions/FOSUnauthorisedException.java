package org.pubcoi.fos.svc.exceptions;

import org.springframework.http.HttpStatus;

public class FOSUnauthorisedException extends FOSException {
    public FOSUnauthorisedException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
