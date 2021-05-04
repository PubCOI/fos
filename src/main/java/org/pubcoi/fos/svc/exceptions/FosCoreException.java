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

public class FosCoreException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(FosCoreException.class);

    public FosCoreException(String message) {
        super(message);
        logger.error("Exception: {}", message);
    }

    public FosCoreException(String message, Throwable e) {
        super(message, e);
        logger.error("Exception: {}", message);
    }
}
