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
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderChildren;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.ReleaseInfo;
import org.kablink.teaming.rest.v1.model.RootRestObject;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.Share;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.ZoneConfig;
import org.kablink.teaming.rest.v1.model.admin.PersonalStorage;
import org.kablink.teaming.rest.v1.model.admin.WebAppConfig;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author david
 *
 */
public class AdminApiImpl extends BaseApiImpl implements AdminApi {

    private RootRestObject root;

    public AdminApiImpl(ApiClient conn) {
		super(conn);
        root = getRoot();
	}

    public RootRestObject getRoot() {
        if (root==null) {
            root = getJSONResourceBuilder("/admin").get(RootRestObject.class);
        }
        return root;
    }

    @Override
    public SearchResultList<Share> getSharesByUser(User user) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("shared_by", user.getId());
        SearchResultList shareList = getJSONResourceBuilder(getRootHref("shares"), params).get(SearchResultList.class);
        return buildSearchResultList(Share.class, shareList);
    }

    @Override
    public SearchResultList<Share> getSharesWithUser(User user) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("shared_with", user.getId());
        SearchResultList shareList = getJSONResourceBuilder(getRootHref("shares"), params).get(SearchResultList.class);
        return buildSearchResultList(Share.class, shareList);
    }

    @Override
    public SearchResultList<Share> getPublicShares() {
        SearchResultList shareList = getJSONResourceBuilder(getRootHref("public_shares")).get(SearchResultList.class);
        return buildSearchResultList(Share.class, shareList);
    }

    @Override
    public void deleteShare(Share share) {
        WebResource.Builder builder = getJSONResourceBuilder(share.getLink());
        builder.delete();
    }

    @Override
    public PersonalStorage getPersonalStorage() {
        return getJSONResourceBuilder(getRootHref("personal_storage")).get(PersonalStorage.class);
    }

    @Override
    public PersonalStorage setPersonalStorage(PersonalStorage personalStorage) {
        WebResource.Builder builder = getJSONResourceBuilder(getRootHref("personal_storage"));
        return builder.put(PersonalStorage.class, personalStorage);
    }

    @Override
    public WebAppConfig getWebAppConfig() {
        return getJSONResourceBuilder(getRootHref("web_application")).get(WebAppConfig.class);
    }

    @Override
    public WebAppConfig setWebAppConfig(WebAppConfig config) {
        WebResource.Builder builder = getJSONResourceBuilder(getRootHref("web_application"));
        return builder.put(WebAppConfig.class, config);
    }

    private String getRootHref(String name) {
        RootRestObject root = getRoot();
        return root.findRelatedLink(name);
    }

}
