package io.github.hwestphal.odata.client;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

class DeferredDeserializer extends JsonDeserializer<Optional<?>> implements ContextualDeserializer {

    private final ObjectMapper mapper;
    private final JavaType targetType;

    DeferredDeserializer(ObjectMapper mapper) {
	this(mapper, null);
    }

    private DeferredDeserializer(ObjectMapper mapper, JavaType targetType) {
	this.mapper = mapper;
	this.targetType = targetType;
    }

    @Override
    public Optional<?> deserialize(JsonParser p, DeserializationContext context)
	    throws IOException, JsonProcessingException {
	ObjectNode node = p.readValueAsTree();
	if (node.has("__deferred")) {
	    return Optional.empty();
	}
	if (targetType.isCollectionLikeType()) {
	    return Optional.of(mapper.convertValue(node.get("results"), targetType));
	}
	return Optional.of(mapper.convertValue(node, targetType));
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
	    throws JsonMappingException {
	return new DeferredDeserializer(mapper, property.getType().containedType(0));
    }

}
