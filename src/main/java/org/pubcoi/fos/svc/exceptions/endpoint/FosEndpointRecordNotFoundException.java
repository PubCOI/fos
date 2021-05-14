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

package org.pubcoi.fos.svc.exceptions.endpoint;

import org.jetbrains.annotations.NotNull;
import org.pubcoi.fos.svc.exceptions.core.FosCoreRecordNotFoundException;
import org.springframework.http.HttpStatus;

public class FosEndpointRecordNotFoundException extends FosEndpointException {

    public FosEndpointRecordNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public FosEndpointRecordNotFoundException(@NotNull FosCoreRecordNotFoundException e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
