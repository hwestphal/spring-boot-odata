package io.github.hwestphal.odata.model.json;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    private final long id;
    private final String code;
    private final Description description;
    private final Optional<Customer> customer;

    @JsonCreator
    public Product(@JsonProperty("Id") long id, @JsonProperty("Code") String code,
	    @JsonProperty("Description") Description description,
	    @JsonProperty("CustomerDetails") Optional<Customer> customer) {
	this.id = id;
	this.code = code;
	this.description = description;
	this.customer = customer;
    }

    public long getId() {
	return id;
    }

    public String getCode() {
	return code;
    }

    public Description getDescription() {
	return description;
    }

    public Optional<Customer> getCustomer() {
	return customer;
    }

}
