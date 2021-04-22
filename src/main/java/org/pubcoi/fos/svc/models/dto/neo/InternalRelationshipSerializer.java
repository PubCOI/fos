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

package org.pubcoi.fos.svc.models.dto.neo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.neo4j.driver.internal.InternalRelationship;

import java.io.IOException;

public class InternalRelationshipSerializer extends StdSerializer<InternalRelationship> {
    public InternalRelationshipSerializer() {
        super(InternalRelationship.class);
    }

    protected InternalRelationshipSerializer(Class<InternalRelationship> t) {
        super(t);
    }

    @Override
    public void serialize(InternalRelationship internalRelationship, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("neo4j_id", internalRelationship.id());
        jsonGenerator.writeNumberField("start", internalRelationship.startNodeId());
        jsonGenerator.writeNumberField("end", internalRelationship.endNodeId());
        jsonGenerator.writeStringField("type", internalRelationship.type());
        jsonGenerator.writeObjectField("properties", internalRelationship.asMap());
        jsonGenerator.writeEndObject();
    }
}