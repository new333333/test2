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

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderChildren;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.ReleaseInfo;
import org.kablink.teaming.rest.v1.model.RootRestObject;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.Workspace;
import org.kablink.teaming.rest.v1.model.ZoneConfig;

/**
 * @author jong
 *
 */
public class ApiImpl implements Api {

	private ApiClient conn;
    private RootRestObject root;
    private User self;
	
	ApiImpl(ApiClient conn) {
		this.conn = conn;
	}

    public RootRestObject getRoot() {
        if (root==null) {
            root = getJSONResourceBuilder("").get(RootRestObject.class);
        }
        return root;
    }

    @Override
    public Binder getMyFiles() {
        return getJSONResourceBuilder(getSelfHref("my_files")).get(Binder.class);
    }

    @Override
    public Binder getNetFolders() {
        return getJSONResourceBuilder(getSelfHref("net_folders")).get(Binder.class);
    }

    @Override
    public ReleaseInfo getReleaseInfo() {
        return getJSONResourceBuilder(getRootHref("release_info")).get(ReleaseInfo.class);
    }

    @Override
    public User getSelf() {
        if (self==null) {
            self = getJSONResourceBuilder(getRootHref("self")).get(User.class);
        }
        return self;
    }

    @Override
    public Binder getSharedByMe() {
        return getJSONResourceBuilder(getSelfHref("shared_by_me")).get(Binder.class);
    }

    @Override
    public Binder getSharedWithMe() {
        return getJSONResourceBuilder(getSelfHref("shared_with_me")).get(Binder.class);
    }

    @Override
    public ZoneConfig getZoneConfig() {
        return getJSONResourceBuilder(getRootHref("zone_config")).get(ZoneConfig.class);
    }

    @Override
    public List<BinderChildren> listBinderChildren(Long[] binderIds, Integer count) {
        return listBinderChildren(binderIds, null, null, count);
    }

    @Override
    public List<BinderChildren> listBinderChildren(Long[] binderIds, Long startingBinderId, Integer first, Integer count) {
        Map<String, Object> params = getFirstAndCountParams(first, count);
        if (params==null) {
            params = new HashMap<String, Object>();
        }
        params.put("id", binderIds);
        if (startingBinderId!=null) {
            params.put("first_id", startingBinderId);
        }
        List results = getJSONResourceBuilder(getRootHref("binder_library_children"), params).get(List.class);
        List<BinderChildren> finalResults = new ArrayList<BinderChildren>();
        ObjectMapper mapper = this.conn.getObjectMapper();
        for (Object result : results) {
            SearchResultList<SearchableObject> items = null;
            if (((Map)result).containsKey("children")) {
                Map searchResultMap = (Map) ((Map) result).remove("children");
                if (searchResultMap!=null) {
                    items = buildSearchableObjectSearchResultList(mapper.convertValue(searchResultMap, SearchResultList.class));
                }
            }
            BinderChildren binderChildren = mapper.convertValue(result, BinderChildren.class);
            binderChildren.setChildren(items);
            finalResults.add(binderChildren);
        }

        return finalResults;
    }

    @Override
    public SearchResultList<SearchableObject> listChildren(Binder binder) {
        return listChildren(binder, null, null);
    }

    public SearchResultList<SearchableObject> listChildren(Binder binder, Integer first, Integer count) {
        Map<String, Object> params = getFirstAndCountParams(first, count);
        SearchResultList results = getJSONResourceBuilder(binder.findRelatedLink("library_children"), params).get(SearchResultList.class);
        return buildSearchableObjectSearchResultList(results);
    }

    private SearchResultList<SearchableObject> buildSearchableObjectSearchResultList(SearchResultList results) {
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

    private void populateType(Map entity) {
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

    private Map<String, Object> getFirstAndCountParams(Integer first, Integer count) {
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


    private String getRootHref(String name) {
        RootRestObject root = getRoot();
        return root.findRelatedLink(name);
    }

    private String getSelfHref(String name) {
        User self = getSelf();
        return self.findRelatedLink(name);
    }

    private WebResource.Builder getJSONResourceBuilder(String href) {
        WebResource r = getResource(href);
        return r.accept(MediaType.APPLICATION_JSON_TYPE);
    }

    private WebResource.Builder getJSONResourceBuilder(String href, Map<String, Object> queryParams) {
        WebResource r = getResource(href, queryParams);
        return r.accept(MediaType.APPLICATION_JSON_TYPE);
    }

    private WebResource getResource(String href) {
        return getResource(href, null);
    }

    private WebResource getResource(String href, Map<String, Object> queryParams) {
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
                } else {
                    ub.queryParam(entry.getKey(), entry.getValue());
                }
            }
        }
        URI resourceUri = ub.build();
        Client c = conn.getClient();
        return c.resource(resourceUri);
    }

    private String ISO8601FromDate(Date date) {
		String dateStr = null;
		if(date != null) {
			DateTime dateTime = new DateTime(date);
			dateStr = ISODateTimeFormat.dateTime().print(dateTime);
		}
		return dateStr;
	}
}
