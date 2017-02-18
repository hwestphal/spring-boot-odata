package io.github.hwestphal.odata.server;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityUriInfo;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAQueryExtensionEntityListener;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPAException;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLContext;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLContextType;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLStatement;
import org.apache.olingo.odata2.jpa.processor.core.access.data.JPAQueryBuilder.UriInfoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomJpaQueryBuilder extends ODataJPAQueryExtensionEntityListener {

    private static final Logger LOG = LoggerFactory.getLogger(CustomJpaQueryBuilder.class);

    @Override
    public Query getQuery(GetEntitySetUriInfo uriInfo, EntityManager em) {
	return buildQuery((UriInfo) uriInfo, UriInfoType.GetEntitySet, em);
    }

    @Override
    public Query getQuery(GetEntityUriInfo uriInfo, EntityManager em) {
	return buildQuery((UriInfo) uriInfo, UriInfoType.GetEntity, em);
    }

    @Override
    public Query getQuery(GetEntityCountUriInfo uriInfo, EntityManager em) {
	return buildQuery((UriInfo) uriInfo, UriInfoType.GetEntityCount, em);
    }

    @Override
    public Query getQuery(GetEntitySetCountUriInfo uriInfo, EntityManager em) {
	return buildQuery((UriInfo) uriInfo, UriInfoType.GetEntitySetCount, em);
    }

    private Query buildQuery(UriInfo uriParserResultView, UriInfoType type, EntityManager em) {
	JPQLContextType contextType = determineJPQLContextType(uriParserResultView, type);
	try {
	    JPQLContext jpqlContext = buildJPQLContext(contextType, uriParserResultView);
	    JPQLStatement jpqlStatement = JPQLStatement.createBuilder(jpqlContext).build();
	    LOG.info("Query: {}", jpqlStatement);
	    return em.createQuery(jpqlStatement.toString());
	} catch (ODataJPAException e) {
	    throw new RuntimeException(e);
	}
    }

    private JPQLContextType determineJPQLContextType(UriInfo uriParserResultView, UriInfoType type) {
	if (uriParserResultView.getNavigationSegments().size() > 0) {
	    if (type == UriInfoType.GetEntitySet) {
		return JPQLContextType.JOIN;
	    }
	    if (type == UriInfoType.GetEntity) {
		return JPQLContextType.JOIN_SINGLE;
	    }
	    if (type == UriInfoType.GetEntitySetCount || type == UriInfoType.GetEntityCount) {
		return JPQLContextType.JOIN_COUNT;
	    }
	} else {
	    if (type == UriInfoType.GetEntitySet) {
		return JPQLContextType.SELECT;
	    }
	    if (type == UriInfoType.GetEntity) {
		return JPQLContextType.SELECT_SINGLE;
	    }
	    if (type == UriInfoType.GetEntitySetCount || type == UriInfoType.GetEntityCount) {
		return JPQLContextType.SELECT_COUNT;
	    }
	}
	throw new IllegalArgumentException(type + " is not supported");
    }

    private JPQLContext buildJPQLContext(JPQLContextType contextType, UriInfo uriParserResultView)
	    throws ODataJPAException {
	return JPQLContext.createBuilder(contextType, uriParserResultView,
		contextType == JPQLContextType.SELECT || contextType == JPQLContextType.JOIN).build();
    }

}
