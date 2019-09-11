package com.lifengdi.serializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Instant时间格式化
 *
 */
public class InstantDateDeserializer extends StdDeserializer<Instant> {
    private static final long serialVersionUID = 5488628600857342630L;

    public InstantDateDeserializer() {
        super(Instant.class);
    }
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS Z").withZone(ZoneOffset.UTC);
    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String s = p.getText();
        if(s != null){
            return Instant.from(fmt.parse(s));
        }
        return null;
    }
}
