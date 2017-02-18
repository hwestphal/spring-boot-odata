package io.github.hwestphal.odata.model.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Description {

    private final String name;
    private final String producer;

    @JsonCreator
    public Description(@JsonProperty("Name") String name, @JsonProperty("Producer") String producer) {
	this.name = name;
	this.producer = producer;
    }

    public String getName() {
	return name;
    }

    public String getProducer() {
	return producer;
    }

}
