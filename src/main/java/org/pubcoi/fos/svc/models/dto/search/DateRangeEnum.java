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

package org.pubcoi.fos.svc.models.dto.search;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public enum DateRangeEnum {
    _3m(3, ChronoUnit.MONTHS),
    _6m(6, ChronoUnit.MONTHS),
    _1y(1, ChronoUnit.YEARS),
    _3y(3, ChronoUnit.YEARS),
    _5y(5, ChronoUnit.YEARS),
    _10y(10, ChronoUnit.YEARS);

    int value;
    TemporalUnit unit;

    DateRangeEnum(int value, TemporalUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public LocalDate getDateFrom() {
        return LocalDate.now().minus(value, unit);
    }
}
