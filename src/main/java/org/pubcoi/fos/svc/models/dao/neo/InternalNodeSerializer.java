package org.pubcoi.fos.svc.models.dao.neo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.neo4j.driver.internal.InternalNode;

import java.io.IOException;

public class InternalNodeSerializer extends StdSerializer<InternalNode> {
    public InternalNodeSerializer() {
        super(InternalNode.class);
    }

    protected InternalNodeSerializer(Class<InternalNode> t) {
        super(t);
    }

    @Override
    public void serialize(InternalNode internalNode, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("neo4j_id", internalNode.id());
        jsonGenerator.writeArrayFieldStart("labels");
        for (String label : internalNode.labels()) {
            jsonGenerator.writeString(label);
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeObjectField("properties", internalNode.asMap());
        jsonGenerator.writeEndObject();
    }
}
