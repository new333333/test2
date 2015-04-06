/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.client.rest.v1;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.ErrorInfo;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.Share;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author jong
 *
 */
public class BaseApiImpl {

	protected ApiClient conn;

	protected BaseApiImpl(ApiClient conn) {
		this.conn = conn;
	}

    protected <T> SearchResultList<T> buildSearchResultList(Class<T> clss, SearchResultList results) {
        SearchResultList<T> actualResults = new SearchResultList<T>(results.getFirst());
        ObjectMapper mapper = this.conn.getObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (Object obj : results.getResults()) {
            Map map = (HashMap) obj;
            actualResults.append(mapper.convertValue(map, clss));
        }
        actualResults.setLastModified(results.getLastModified());
        actualResults.setNext(results.getNext());
        actualResults.setTotal(results.getTotal());

        return actualResults;
    }

    protected SearchResultList<BinderBrief> buildBinderBriefSearchResultList(SearchResultList results) {
        SearchResultList<BinderBrief> actualResults = new SearchResultList<BinderBrief>(results.getFirst());
        ObjectMapper mapper = this.conn.getObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (Object obj : results.getResults()) {
            BinderBrief binder = null;
            Map map = (HashMap) obj;
            binder = mapper.convertValue(map, BinderBrief.class);
            actualResults.append(binder);
        }
        actualResults.setLastModified(results.getLastModified());
        actualResults.setNext(results.getNext());
        actualResults.setTotal(results.getTotal());

        return actualResults;
    }

    protected SearchResultList<SearchableObject> buildSearchableObjectSearchResultList(SearchResultList results) {
        SearchResultList<SearchableObject> actualResults = new SearchResultList<SearchableObject>(results.getFirst());
        ObjectMapper mapper = this.conn.getObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (Object obj : results.getResults()) {
            SearchableObject searchObject = null;
            Map map = (HashMap) obj;
            populateType(map);
            String docType = (String) map.get("doc_type");
            if ("binder".equals(docType)) {
                searchObject = mapper.convertValue(map, Binder.class);
            } else if ("file".equals(docType)) {
                searchObject = mapper.convertValue(map, FileProperties.class);
            }
            actualResults.append(searchObject);
        }
        actualResults.setLastModified(results.getLastModified());
        actualResults.setNext(results.getNext());
        actualResults.setTotal(results.getTotal());

        return actualResults;
    }

    protected void populateType(Map entity) {
        String docType = (String) entity.get("doc_type");
        String entityType = (String) entity.get("entity_type");
        if ("binder".equals(docType)) {
            if ("folder".equals(entityType)) {
                entity.put("@type", ".Folder");
            } else if ("workspace".equals(entityType)) {
                entity.put("@type", ".Workspace");
            }
        } else if ("file".equals(docType)) {
            entity.put("@type", ".FileProperties");
        }
    }

    protected Map<String, Object> getFirstAndCountParams(Integer first, Integer count) {
        Map<String, Object> params = null;
        if (first!=null || count!=null) {
            params = new HashMap<String, Object>();
            if (first!=null) {
                params.put("first", first);
            }
            if (count!=null) {
                params.put("count", count);
            }
        }
        return params;
    }

    protected WebResource.Builder getJSONResourceBuilder(String href) {
        WebResource r = getResource(href);
        return r.accept(MediaType.APPLICATION_JSON_TYPE);
    }

    protected WebResource.Builder getJSONResourceBuilder(String href, Map<String, Object> queryParams) {
        WebResource r = getResource(href, queryParams);
        return r.accept(MediaType.APPLICATION_JSON_TYPE);
    }

    protected WebResource getResource(String href) {
        return getResource(href, null);
    }

    protected WebResource getResource(String href, Map<String, Object> queryParams) {
        if (href==null) {
            throw new NullPointerException("href is null");
        }
        UriBuilder ub = UriBuilder.fromUri(conn.getBaseUrl() + href);
        if (queryParams!=null) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Iterable) {
                    for (Object val : (Iterable) value) {
                        ub.queryParam(entry.getKey(), val);
                    }
                } else if (value.getClass().isArray()) {
                    for (Object val : (Object []) value) {
                        ub.queryParam(entry.getKey(), val);
                    }
                } else if (value instanceof Date) {
                    ub.queryParam(entry.getKey(), ISO8601FromDate((Date) value));
                } else {
                    ub.queryParam(entry.getKey(), entry.getValue());
                }
            }
        }
        URI resourceUri = ub.build();
        Client c = conn.getClient();
        return c.resource(resourceUri);
    }

    protected String ISO8601FromDate(Date date) {
		String dateStr = null;
		if(date != null) {
			DateTime dateTime = new DateTime(date);
			dateStr = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC().print(dateTime);
		}
		return dateStr;
	}

    protected String Rfc1123FromDate(Date date) {
		String dateStr = null;
		if(date != null) {
			DateTime dateTime = new DateTime(date);
			dateStr = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'").withZoneUTC().withLocale(Locale.US).print(dateTime);
		}

		return dateStr;
	}

    protected <T> T get(UniformInterface uniformInterface, Class<T> clss) {
        ClientResponse response = uniformInterface.get(ClientResponse.class);
        if (response.getStatus()!=200) {
            handleError(response);
        }
        return response.getEntity(clss);
    }

    protected <T> T post(UniformInterface uniformInterface, Class<T> clss, Object requestEntity) {
        ClientResponse response = uniformInterface.post(ClientResponse.class, requestEntity);
        if (response.getStatus()!=200) {
            handleError(response);
        }
        return response.getEntity(clss);
    }

    protected void delete(UniformInterface uniformInterface) {
        ClientResponse response = uniformInterface.delete(ClientResponse.class);
        if (response.getStatus()!=200 && response.getStatus()!=204) {
            handleError(response);
        }
    }

    protected void handleError(ClientResponse response) {
        HttpException exception = null;
        ErrorInfo err = null;
        List<String> contentTypeList = response.getHeaders().get("Content-Type");
        if (contentTypeList!=null && contentTypeList.contains("application/json")) {
            err = response.getEntity(ErrorInfo.class);
        }
        if (exception==null) {
            int code = response.getStatus();
            if (code==304) {
                throw new NotModifiedException(err);
            } else if (code==409) {
                throw new ConflictException(err);
            }
        }
    }

}
