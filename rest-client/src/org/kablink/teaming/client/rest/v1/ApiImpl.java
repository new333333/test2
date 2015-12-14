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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.map.ObjectMapper;

import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderChanges;
import org.kablink.teaming.rest.v1.model.BinderChildren;
import org.kablink.teaming.rest.v1.model.EntityId;
import org.kablink.teaming.rest.v1.model.ErrorInfo;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.ReleaseInfo;
import org.kablink.teaming.rest.v1.model.RootRestObject;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.Share;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.ZoneConfig;

/**
 * @author jong
 *
 */
public class ApiImpl extends BaseApiImpl implements Api {

    private RootRestObject root;

	public ApiImpl(ApiClient conn) {
		super(conn);
	}

    @Override
    public Date getCurrentServerTime() {
        ClientResponse response = getJSONResourceBuilder("").get(ClientResponse.class);
        return response.getResponseDate();
    }

    public RootRestObject getRoot() {
        if (root==null) {
            root = get(getJSONResourceBuilder(""), RootRestObject.class);
        }
        return root;
    }

    @Override
    public Binder getMyFiles() {
        String my_files = getSelfHref("my_files");
        return get(getJSONResourceBuilder(my_files), Binder.class);
    }

    @Override
    public Binder getNetFolders() {
        return get(getJSONResourceBuilder(getSelfHref("net_folders")), Binder.class);
    }

    @Override
    public ReleaseInfo getReleaseInfo() {
        return get(getJSONResourceBuilder(getRootHref("release_info")), ReleaseInfo.class);
    }

    @Override
    public User getSelf() {
        return get(getJSONResourceBuilder(getRootHref("self")), User.class);
    }

    @Override
    public Binder getSharedByMe() {
        return get(getJSONResourceBuilder(getSelfHref("shared_by_me")), Binder.class);
    }

    @Override
    public Binder getSharedWithMe() {
        return get(getJSONResourceBuilder(getSelfHref("shared_with_me")), Binder.class);
    }

    @Override
    public Binder getPublicShares() {
        return get(getJSONResourceBuilder(getSelfHref("public_shares")), Binder.class);
    }

    @Override
    public ZoneConfig getZoneConfig() {
        return get(getJSONResourceBuilder(getRootHref("zone_config")), ZoneConfig.class);
    }

    @Override
    public Folder createLibraryFolder(Binder parentBinder, String name) {
        BinderBrief folder = new BinderBrief();
        folder.setTitle(name);
        try {
            return post(getJSONResourceBuilder(parentBinder.findRelatedLink("child_library_folders")),
                    Folder.class, folder);
        } catch (ConflictException e) {
            ErrorInfo err = e.getError();
            if (err != null && err.data!=null) {
                ObjectMapper mapper = this.conn.getObjectMapper();
                populateType((Map) err.data);
                err.data = mapper.convertValue(err.data, Folder.class);
            }
            throw e;
        }
    }

    @Override
    public Folder createLibraryFolderIfNecessary(Binder parentBinder, String name) {
        try {
            return createLibraryFolder(parentBinder, name);
        } catch (ConflictException e) {
            if (e.getError()!=null && e.getError().data instanceof Folder) {
                return (Folder) e.getError().data;
            }
            throw e;
        }
    }

    public FileProperties uploadFile(Binder parent, String fileName, boolean overwriteExisting, InputStream content) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("file_name", fileName);
        params.put("overwrite_existing", overwriteExisting);
        return post(getJSONResourceBuilder(parent.findRelatedLink("child_library_files"), params).type("application/octet-stream"),
                FileProperties.class, content);
    }

    @Override
    public Share shareFolder(Folder folder, Share share) {
        share.setSharedEntity(new EntityId(folder.getId(), folder.getEntityType(), null));
        return post(getJSONResourceBuilder(folder.findRelatedLink("shares")), Share.class, share);
    }

    @Override
    public Share shareFile(FileProperties file, Share share) {
        share.setSharedEntity(file.getOwningEntity());
        return post(getJSONResourceBuilder(file.findRelatedLink("shares")), Share.class, share);
    }

    @Override
    public SearchResultList<BinderBrief> getTopLevelFolders() {
        SearchResultList results = get(getJSONResourceBuilder(getSelfHref("roots")), SearchResultList.class);
        return buildBinderBriefSearchResultList(results);
    }

    @Override
    public SearchResultList<BinderBrief> getBinders(Long[] binderIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", binderIds);
        SearchResultList results = get(getJSONResourceBuilder(getRootHref("binders"), params), SearchResultList.class);
        return buildBinderBriefSearchResultList(results);
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
        List results = get(getJSONResourceBuilder(getRootHref("binder_library_children"), params), List.class);
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
        return listChildren(binder, null, null, null);
    }

    @Override
    public SearchResultList<SearchableObject> listChildren(Binder binder, Date ifModifiedSince) {
        return listChildren(binder, null, null, ifModifiedSince);
    }

    @Override
    public SearchResultList<SearchableObject> listChildren(Binder binder, Integer first, Integer count) {
        return listChildren(binder, first, count, null);
    }

    @Override
    public SearchResultList<SearchableObject> listChildren(Binder binder, Integer first, Integer count, Date ifModifiedSince) {
        Map<String, Object> params = getFirstAndCountParams(first, count);
        WebResource.Builder builder = getJSONResourceBuilder(binder.findRelatedLink("library_children"), params);
        if (ifModifiedSince!=null) {
            builder.header("If-Modified-Since", Rfc1123FromDate(ifModifiedSince));
        }
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus()!=200) {
            handleError(response);
        }
        SearchResultList results = response.getEntity(SearchResultList.class);
        results.setLastModified(response.getLastModified());
        return buildSearchableObjectSearchResultList(results);
    }

    @Override
    public BinderChanges listChanges(Binder binder, Date since, Integer count) {
        Map<String, Object> params = getFirstAndCountParams(null, count);
        params.put("since", since);
        WebResource.Builder builder = getJSONResourceBuilder(binder.findRelatedLink("library_changes"), params);
        return get(builder, BinderChanges.class);
    }

    @Override
    public void delete(FileProperties file, boolean purge) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("purge", purge);
        WebResource.Builder builder = getJSONResourceBuilder(file.getOwningEntity().getLink(), params);
        delete(builder);
    }

    @Override
    public void delete(Binder folder, boolean purge) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("purge", purge);
        WebResource.Builder builder = getJSONResourceBuilder(folder.getLink(), params);
        delete(builder);
    }

    private String getRootHref(String name) {
        RootRestObject root = getRoot();
        return root.findRelatedLink(name);
    }

    private String getSelfHref(String name) {
        User self = getSelf();
        return self.findRelatedLink(name);
    }
}
