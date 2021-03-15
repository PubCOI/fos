package org.pubcoi.fos.svc.models.dao.neo;

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