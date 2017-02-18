package io.github.hwestphal.odata;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.github.hwestphal.odata.model.jpa.Customer;
import io.github.hwestphal.odata.model.jpa.Description;
import io.github.hwestphal.odata.model.jpa.Product;

@Component
@ConditionalOnProperty(name = "init-data", matchIfMissing = true)
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
	Customer customer = new Customer("C1");
	customer.setName("Customer 1");
	Product product1 = new Product("P1");
	product1.setDescription(new Description("Product 1", "ABC"));
	customer.addProduct(product1);
	Product product2 = new Product("P2");
	product2.setDescription(new Description("Product 2", "XYZ"));
	customer.addProduct(product2);
	entityManager.persist(customer);
    }

}
