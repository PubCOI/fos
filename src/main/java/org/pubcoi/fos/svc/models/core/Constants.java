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

package org.pubcoi.fos.svc.models.core;

public class Constants {
    public class Neo4J {
        public static final String REL_AWARDED_TO = "AWARDED_TO";
        public static final String REL_AKA = "AKA";
        public static final String REL_PUBLISHED = "PUBLISHED";
        // ORG may either be an org or a client node
        public static final String REL_PERSON = "ORG_PERSON";
        public static final String REL_AWARDED = "AWARDED";
        public static final String REL_LEGAL_ENTITY = "LEGAL_ENTITY";
        public static final String REL_CONFLICT = "CONFLICT";
    }
}
