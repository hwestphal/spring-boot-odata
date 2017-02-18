package io.github.hwestphal.odata.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class OdataClient {

    private final String serviceUrl;
    private final ObjectMapper mapper;
    private final ObjectReader reader;

    public OdataClient(String serviceUrl) {
	this.serviceUrl = Objects.requireNonNull(serviceUrl, "serviceUrl must not be null");
	mapper = new ObjectMapper();
	mapper.registerModule(new SimpleModule().addDeserializer(Optional.class, new DeferredDeserializer(mapper)));
	reader = mapper.reader(DeserializationFeature.UNWRAP_ROOT_VALUE).withRootName("d");
    }

    public Edm getMetadata() throws IOException {
	return get("$metadata", MediaType.APPLICATION_XML, is -> EntityProvider.readMetadata(is, true));
    }

    public <T> Optional<T> getEntity(String path, Class<T> type) throws IOException {
	try {
	    return Optional.of(get(path, MediaType.APPLICATION_JSON_UTF8, is -> reader.forType(type).readValue(is)));
	} catch (FileNotFoundException e) {
	    return Optional.empty();
	}
    }

    public <T> Stream<T> getEntities(String path, Class<T> type) throws IOException {
	try {
	    ArrayNode productResults = (ArrayNode) get(path, MediaType.APPLICATION_JSON_UTF8,
		    is -> reader.readTree(is).get("results"));
	    return StreamSupport.stream(productResults.spliterator(), false).map(n -> mapper.convertValue(n, type));
	} catch (FileNotFoundException e) {
	    return Stream.empty();
	}
    }

    private <T> T get(String path, MediaType mediaType, InputStreamHandler<T> handler) throws IOException {
	HttpURLConnection conn = (HttpURLConnection) new URL(serviceUrl + path).openConnection();
	try {
	    conn.setRequestProperty("Accept", mediaType.toString());
	    try (InputStream is = conn.getInputStream()) {
		try {
		    return handler.handleInputStream(is);
		} catch (IOException | RuntimeException e) {
		    throw e;
		} catch (Exception e) {
		    throw new RuntimeException(e);
		}
	    }
	} finally {
	    conn.disconnect();
	}
    }

    private interface InputStreamHandler<T> {
	T handleInputStream(InputStream is) throws Exception;
    }

}
