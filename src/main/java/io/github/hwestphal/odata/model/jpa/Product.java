package io.github.hwestphal.odata.model.jpa;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import io.github.hwestphal.odata.server.CustomJpaQueryBuilder;

@Entity
@EntityListeners(CustomJpaQueryBuilder.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "code" }, name = "PRODUCT_UK"))
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String code;

    private Description description;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "PRODUCT_CUSTOMER_FK"))
    private Customer customer;

    Product() {
    }

    public Product(String code) {
	this.code = Objects.requireNonNull(code, "code must not be null");
    }

    public Long getId() {
	return id;
    }

    public String getCode() {
	return code;
    }

    public Description getDescription() {
	return description;
    }

    public void setDescription(Description description) {
	this.description = description;
    }

    public Customer getCustomer() {
	return customer;
    }

    void setCustomer(Customer customer) {
	this.customer = customer;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((code == null) ? 0 : code.hashCode());
	result = prime * result + ((customer == null) ? 0 : customer.hashCode());
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
	Product other = (Product) obj;
	if (code == null) {
	    if (other.code != null) {
		return false;
	    }
	} else if (!code.equals(other.code)) {
	    return false;
	}
	if (customer == null) {
	    if (other.customer != null) {
		return false;
	    }
	} else if (!customer.equals(other.customer)) {
	    return false;
	}
	return true;
    }

}
