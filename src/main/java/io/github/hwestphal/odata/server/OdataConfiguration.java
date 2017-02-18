package io.github.hwestphal.odata.server;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OdataConfiguration {

    public static final String NAMESPACE = "Spring_Boot_OData";
    public static final String SERVICE_URL = "/odata/v2/";

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    ServletRegistrationBean odataServlet() {
	return new ServletRegistrationBean(
		new OdataServlet(new JpaOdataServiceFactory(entityManagerFactory, NAMESPACE)), SERVICE_URL + "*");
    }

}
