package org.pubcoi.fos.svc.exceptions;

import org.springframework.http.HttpStatus;

public class FosUnauthorisedException extends FosException {
    public FosUnauthorisedException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
