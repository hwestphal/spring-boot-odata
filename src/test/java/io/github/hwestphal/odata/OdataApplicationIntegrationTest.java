package io.github.hwestphal.odata;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionOperations;

import io.github.hwestphal.odata.client.OdataClient;
import io.github.hwestphal.odata.model.jpa.Customer;
import io.github.hwestphal.odata.model.jpa.Description;
import io.github.hwestphal.odata.model.jpa.Product;
import io.github.hwestphal.odata.server.OdataConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "spring.jpa.properties.hibernate.show_sql=true",
	"init-data=false" }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class OdataApplicationIntegrationTest {

    @LocalServerPort
    private int serverPort;

    private OdataClient client;

    private long customerId;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionOperations txTemplate;

    @Before
    public void initClient() {
	client = new OdataClient("http://localhost:" + serverPort + OdataConfiguration.SERVICE_URL);
    }

    @Before
    public void initData() {
	customerId = txTemplate.execute(status -> {
	    Customer customer = new Customer("C1");
	    customer.setName("Customer 1");
	    Product product1 = new Product("P1");
	    product1.setDescription(new Description("Product 1", "ABC"));
	    customer.addProduct(product1);
	    Product product2 = new Product("P2");
	    product2.setDescription(new Description("Product 2", "XYZ"));
	    customer.addProduct(product2);
	    entityManager.persist(customer);
	    return customer.getId();
	});
    }

    @After
    public void clearData() {
	txTemplate.execute(status -> {
	    entityManager.remove(entityManager.find(Customer.class, customerId));
	    return null;
	});
    }

    @Test
    public void odataMetadataCanBeFetched() throws ODataException, IOException {
	// when
	Edm edm = client.getMetadata();

	// then
	EdmEntityType customerType = edm.getEntityType(OdataConfiguration.NAMESPACE, "Customer");
	assertThat(customerType, is(notNullValue()));
	EdmEntityType productType = edm.getEntityType(OdataConfiguration.NAMESPACE, "Product");
	assertThat(productType, is(notNullValue()));
    }

    @Test
    public void customerCanBeRetrieved() throws IOException {
	// when
	io.github.hwestphal.odata.model.json.Customer customer = client
		.getEntity("Customers(" + customerId + ")?$expand=ProductDetails,ProductDetails/CustomerDetails",
			io.github.hwestphal.odata.model.json.Customer.class)
		.get();

	// then
	assertThat(customer.getId(), is(customerId));
	assertThat(customer.getCode(), is("C1"));
	assertThat(customer.getName(), is("Customer 1"));

	List<io.github.hwestphal.odata.model.json.Product> products = new ArrayList<>(customer.getProducts().get());
	assertThat(products, hasSize(2));
	products.sort((p1, p2) -> p1.getCode().compareTo(p2.getCode()));
	assertThat(products.get(0).getCode(), is("P1"));
	assertThat(products.get(1).getCode(), is("P2"));

	io.github.hwestphal.odata.model.json.Description description = products.get(0).getDescription();
	assertThat(description.getName(), is("Product 1"));
	assertThat(description.getProducer(), is("ABC"));

	io.github.hwestphal.odata.model.json.Customer productCustomer = products.get(0).getCustomer().get();
	assertThat(productCustomer.getId(), is(customerId));
	assertThat(productCustomer.getCode(), is("C1"));
	assertThat(productCustomer.getName(), is("Customer 1"));
	assertThat(productCustomer.getProducts().isPresent(), is(false));
    }

    @Test
    public void productsCanBeRetrieved() throws IOException {
	// when
	List<io.github.hwestphal.odata.model.json.Product> productResult = client
		.getEntities("Products", io.github.hwestphal.odata.model.json.Product.class)
		.collect(Collectors.toList());

	// then
	assertThat(productResult, hasSize(2));
	assertThat(productResult.get(0).getCustomer().isPresent(), is(false));
    }

}
