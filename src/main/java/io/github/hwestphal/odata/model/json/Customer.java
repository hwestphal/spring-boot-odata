package io.github.hwestphal.odata.model.json;

import java.util.Collection;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {

    private final long id;
    private final String code;
    private final String name;
    private final Optional<Collection<Product>> products;

    @JsonCreator
    public Customer(@JsonProperty("Id") long id, @JsonProperty("Code") String code, @JsonProperty("Name") String name,
	    @JsonProperty("ProductDetails") Optional<Collection<Product>> products) {
	this.id = id;
	this.code = code;
	this.name = name;
	this.products = products;
    }

    public long getId() {
	return id;
    }

    public String getCode() {
	return code;
    }

    public String getName() {
	return name;
    }

    public Optional<Collection<Product>> getProducts() {
	return products;
    }

}
