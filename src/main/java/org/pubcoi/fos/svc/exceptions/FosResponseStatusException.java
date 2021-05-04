/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FosResponseStatusException extends ResponseStatusException {
    private static final Logger logger = LoggerFactory.getLogger(FosResponseStatusException.class);

    public FosResponseStatusException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
        logger.error("Exception, returning 500");
    }

    public FosResponseStatusException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
        logger.error("Exception, returning 500: {}", message);
    }

    public FosResponseStatusException(HttpStatus status, String message) {
        super(status, message);
        logger.error("Exception, returning {}: {}", status, message);
    }

    public FosResponseStatusException(HttpStatus status) {
        super(status);
        logger.error("Exception, returning {}", status);
    }

    public FosResponseStatusException(String message, Throwable e) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
        logger.error(e.getMessage(), e);
    }

    public FosResponseStatusException(Throwable e) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
