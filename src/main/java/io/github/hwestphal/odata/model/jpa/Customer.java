package io.github.hwestphal.odata.model.jpa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import io.github.hwestphal.odata.server.CustomJpaQueryBuilder;

@Entity
@EntityListeners(CustomJpaQueryBuilder.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "code", name = "CUSTOMER_UK"))
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String code;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private Set<Product> products = new HashSet<Product>();

    Customer() {
    }

    public Customer(String code) {
	this.code = Objects.requireNonNull(code, "code must not be null");
    }

    public Long getId() {
	return id;
    }

    public String getCode() {
	return code;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Set<Product> getProducts() {
	return Collections.unmodifiableSet(products);
    }

    public boolean addProduct(Product product) {
	product.setCustomer(this);
	return products.add(product);
    }

    public boolean removeProduct(Product product) {
	return products.remove(product);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((code == null) ? 0 : code.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	Customer other = (Customer) obj;
	if (code == null) {
	    if (other.code != null) {
		return false;
	    }
	} else if (!code.equals(other.code)) {
	    return false;
	}
	return true;
    }

}
