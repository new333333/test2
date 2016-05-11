/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.remoting.rest.v1.resource;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.InvalidEmailAddressException;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.BinderChange;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.netfolder.NetFolderUtil;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.NotModifiedException;
import org.kablink.teaming.remoting.rest.v1.exc.UnsupportedMediaTypeException;
import org.kablink.teaming.remoting.rest.v1.util.*;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.rest.v1.model.BinderChanges;
import org.kablink.teaming.rest.v1.model.DefinableEntity;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.InvokeException;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.ShareLists;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.util.*;
import org.kablink.util.HttpHeaders;
import org.kablink.util.Pair;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Locale;

public abstract class AbstractResource extends AbstractAllModulesInjected {

    protected static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    private static Set<WorkAreaOperation> ignoredOperations = new HashSet<WorkAreaOperation>() {
        {
            add(WorkAreaOperation.BINDER_ADMINISTRATION);
            add(WorkAreaOperation.ADD_COMMUNITY_TAGS);
            add(WorkAreaOperation.GENERATE_REPORTS);
            add(WorkAreaOperation.DOWNLOAD_FOLDER_AS_CSV);
            add(WorkAreaOperation.CHANGE_ACCESS_CONTROL);
        }
    };

    @javax.ws.rs.core.Context
    ServletContext context;

    protected Log logger = LogFactory.getLog(getClass());

    private AccessControlManager accessControlManager;

    public AccessControlManager getAccessControlManager() {
        if(accessControlManager == null)
            accessControlManager = (AccessControlManager) SpringContextUtil.getBean("accessControlManager");
        return accessControlManager;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    protected ServletContext getServletContext() {
        return context;
    }

    protected int toDomainFormat(String descriptionFormatStr) {
        if ("html".equals(descriptionFormatStr)) {
            return Description.FORMAT_HTML;
        } else {
            return Description.FORMAT_NONE;
        }
    }

    protected Long getLoggedInUserId() {
        return RequestContextHolder.getRequestContext().getUserId();
    }

    protected Date getIfModifiedSinceDate(HttpServletRequest request) {
        Date date = null;
        long longDate = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if (longDate != -1)
            date = new Date(longDate);
        return date;
    }

    protected org.kablink.teaming.domain.User getLoggedInUser() {
        Long userId = getLoggedInUserId();
        // Retrieve the raw entry.
        Principal entry = getProfileModule().getEntry(userId);

        if(!(entry instanceof org.kablink.teaming.domain.User))
            throw new IllegalArgumentException(userId + " does not represent an user. It is " + entry.getClass().getSimpleName());
        return (org.kablink.teaming.domain.User)entry;
    }

    protected org.kablink.teaming.domain.Workspace _getUserWorkspace() {
        Long workspaceId = getLoggedInUser().getWorkspaceId();
        if (workspaceId!=null) {
            return _getWorkspace(workspaceId);
        }
        throw new NotFoundException(ApiErrorCode.FOLDER_NOT_FOUND, "NOT FOUND");
    }

    protected org.kablink.teaming.domain.Folder _getHiddenFilesFolder() {
        Long folderId = SearchUtils.getMyFilesFolderId(this, getLoggedInUser(), true);
        if (folderId!=null) {
            return _getFolder(folderId);
        }
        throw new NotFoundException(ApiErrorCode.FOLDER_NOT_FOUND, "NOT FOUND");
    }

    protected org.kablink.teaming.domain.Folder _getHomeFolder() {
        Long folderId = SearchUtils.getHomeFolderId(this, getLoggedInUser().getWorkspaceId());
        if (folderId!=null) {
            return _getFolder(folderId);
        }
        throw new NotFoundException(ApiErrorCode.FOLDER_NOT_FOUND, "User has no home folder");
    }

    protected org.kablink.teaming.domain.Folder _getFolder(long id) {
        try{
            org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(id, false, true);
            if (binder instanceof org.kablink.teaming.domain.Folder) {
                org.kablink.teaming.domain.Folder folder = (org.kablink.teaming.domain.Folder) binder;
                if (!folder.isPreDeleted()) {
                    return folder;
                }
            }
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.FOLDER_NOT_FOUND, "NOT FOUND");
    }

    protected Workspace _getWorkspace(long id) {
        try{
            org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(id, false, true);
            if (binder instanceof Workspace) {
                Workspace workspace = (Workspace) binder;
                if (!workspace.isPreDeleted()) {
                    return workspace;
                }
            }
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.WORKSPACE_NOT_FOUND, "NOT FOUND");
    }

    protected org.kablink.teaming.domain.User _getUser(long userId) {
        Principal entry = getProfileModule().getEntry(userId);

        if(!(entry instanceof org.kablink.teaming.domain.User))
            throw new NoUserByTheIdException(userId);
        return (org.kablink.teaming.domain.User) entry;
    }

    protected org.kablink.teaming.domain.Group _getGroup(long groupId) {
        Principal entry = getProfileModule().getEntry(groupId);

        if(!(entry instanceof org.kablink.teaming.domain.Group))
            throw new NoGroupByTheIdException(groupId);
        return (org.kablink.teaming.domain.Group) entry;
    }

    protected org.kablink.teaming.domain.DefinableEntity findDefinableEntity(EntityIdentifier ei) {
        if (ei==null) {
            return null;
        }
        try {
            return findDefinableEntity(ei.getEntityType(), ei.getEntityId());
        } catch (NoObjectByTheIdException e) {
            // Ignore
        } catch (AccessControlException e) {
            // Ignore
        }
        return null;
    }

    protected org.kablink.teaming.domain.DefinableEntity findDefinableEntity(EntityIdentifier.EntityType et, long entityId)
            throws BadRequestException {
        org.kablink.teaming.domain.DefinableEntity entity;
        if (et == EntityIdentifier.EntityType.folderEntry) {
            entity = getFolderModule().getEntry(null, entityId);
            if (!((FolderEntry)entity).isTop() || _isPreDeleted((FolderEntry)entity)) {
                entity = null;
            }
        } else if (et == EntityIdentifier.EntityType.user) {
            entity = getProfileModule().getEntry(entityId);
            if (!(entity instanceof User))
                throw new BadRequestException(ApiErrorCode.NOT_USER, "Entity ID '" + entityId + "' does not represent a user");
        } else if (et == EntityIdentifier.EntityType.group) {
            entity = getProfileModule().getEntry(entityId);
            if (!(entity instanceof Group))
                throw new BadRequestException(ApiErrorCode.NOT_GROUP, "Entity ID '" + entityId + "' does not represent a group");
        } else if (et == EntityIdentifier.EntityType.workspace || et == EntityIdentifier.EntityType.folder || et == EntityIdentifier.EntityType.profiles) {
            entity = getBinderModule().getBinder(entityId, false, true);
            if (_isPreDeleted((Binder)entity)) {
                entity = null;
            }
        } else {
            throw new BadRequestException(ApiErrorCode.INVALID_ENTITY_TYPE, "Entity type '" + et.name() + "' is unknown or not supported by this method");
        }
        return entity;
    }

    protected boolean _isPreDeleted(FolderEntry entry) {
        return entry.isPreDeleted();
    }

    protected boolean _isPreDeleted(Binder binder) {
        if (binder instanceof Folder) {
            return ((Folder)binder).isPreDeleted();
        }
        else if (binder instanceof Workspace) {
            return ((Workspace)binder).isPreDeleted();
        }
        return false;
    }

    protected SearchResultList<SearchableObject> searchForLibraryEntities(String keyword, Criterion searchContext,
                                                                          boolean recursive, Integer offset, Integer maxCount,
                                                                          boolean includeBinders, boolean includeFolderEntries,
                                                                          boolean includeReplies, boolean includeFiles,
                                                                          boolean includeParentPaths, int descriptionFormat, String nextUrl) {
        keyword = SearchUtils.validateSearchText(keyword);
        Criteria crit = new Criteria();
        crit.add(SearchUtils.buildDocTypeCriterion(includeBinders, includeFolderEntries, includeFiles, includeReplies));
        crit.add(SearchUtils.buildLibraryCriterion(true, Boolean.FALSE));
        crit.add(searchContext);

        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("recursive", Boolean.toString(recursive));
        nextParams.put("binders", Boolean.toString(includeBinders));
        nextParams.put("folder_entries", Boolean.toString(includeFolderEntries));
        nextParams.put("files", Boolean.toString(includeFiles));
        nextParams.put("replies", Boolean.toString(includeReplies));
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        if (keyword!=null) {
            try {
                nextParams.put("keyword", URLEncoder.encode(keyword, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // Ignore
            }
        }
        if (descriptionFormat==Description.FORMAT_HTML) {
            nextParams.put("description_format", "html");
        } else {
            nextParams.put("description_format", "text");
        }
        SearchFilter searchFilter = new SearchFilter(true);
        if (keyword!=null) {
            keyword = keyword.trim();
            if (!keyword.equals("") && !keyword.equals("*")) {
                searchFilter.addText(keyword, false);
            }
        }

        Map options = new HashMap();
        options.put(ObjectKeys.SEARCH_CRITERIA_AND, crit);
        options.put(ObjectKeys.SEARCH_OFFSET, offset);
        options.put(ObjectKeys.SEARCH_MAX_HITS, maxCount);
        options.put(ObjectKeys.SEARCH_SORT_BY, ObjectKeys.SEARCH_SORT_BY_RELEVANCE);
        options.put(ObjectKeys.SEARCH_SORT_DESCEND, false);
        options.put(ObjectKeys.SEARCH_SORT_BY_SECONDARY, ObjectKeys.SEARCH_SORT_BY_DATE);
        options.put(ObjectKeys.SEARCH_SORT_DESCEND_SECONDARY, true);

        Map resultsMap = getBinderModule().executeSearchQuery(searchFilter.getFilter(), Constants.SEARCH_MODE_NORMAL, options);
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(this, descriptionFormat, false), resultsMap,
                nextUrl, nextParams, offset);
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    protected SearchResultList<RecentActivityEntry> _getRecentActivity(boolean includeParentPaths, int descriptionFormat,
                                                                    Integer offset, Integer maxCount, Criteria criteria,
                                                                    String nextUrl, Map<String, Object> nextParams) {
        Map resultsMap = getBinderModule().executeSearchQuery(criteria, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<RecentActivityEntry> results = new SearchResultList<RecentActivityEntry>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new RecentActivityFolderEntryBuilder(descriptionFormat), resultsMap,
                nextUrl, nextParams, offset);

        populateComments(results.getResults(), descriptionFormat);

        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }

        return results;
    }

    protected void populateComments(List<RecentActivityEntry> entries, int descriptionFormat) {
        String[] topEntryIds = new String[entries.size()];
        int i = 0;
        for (RecentActivityEntry entry:  entries) {
            // ...tracking each ASEntryData's entry ID.
            Long topEntryId = entry.getId();
            topEntryIds[i++] = String.valueOf(topEntryId);
        }

        // Are there any comments posted to any of these entries?
        Criteria searchCriteria = SearchUtils.entryReplies(topEntryIds, true);	// true -> All replies, at any level.
        Map       searchResults = getBinderModule().executeSearchQuery(searchCriteria, Constants.SEARCH_MODE_NORMAL, 0, (Integer.MAX_VALUE - 1), null);
        List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
        int       totalRecords  = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
        if ((0 >= totalRecords) || (null == searchEntries) || searchEntries.isEmpty()) {
            // No!  Then there's no comment data to complete.
            return;
        }

        ReplyBriefBuilder replyBuilder = new ReplyBriefBuilder(descriptionFormat);

        // Scan the comment entry search results Map's
        for (Map commentEntryMap:  searchEntries) {
            // Can we find the FolderEntry for the top level entry
            // for this comment?
            Long entryId = SearchResultBuilderUtil.getLong(commentEntryMap, Constants.ENTRY_TOP_ENTRY_ID_FIELD);
            RecentActivityEntry entry = findEntry(entries, entryId);
            if (null == entry) {
                // No!  Skip it.
                continue;
            }

            // Does this comment Map contain both a binder and
            // entry ID?
            Long commentBinderId = SearchResultBuilderUtil.getLong(commentEntryMap, Constants.BINDER_ID_FIELD);
            Long commentEntryId = SearchResultBuilderUtil.getLong(commentEntryMap, Constants.DOCID_FIELD);
            if (commentBinderId==null || commentEntryId==null) {
                // No!  Skip it.
                continue;
            }

            entry.addReply(replyBuilder.build(commentEntryMap), 2);
        }
    }

    private RecentActivityEntry findEntry(List<RecentActivityEntry> entries, Long entryId) {
        for (RecentActivityEntry entry : entries) {
            if (entryId.equals(entry.getId())) {
                return entry;
            }
        }
        return null;
    }

    protected boolean isBinderPreDeleted(Binder binder) {
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            return ((org.kablink.teaming.domain.Folder)binder).isPreDeleted();
        } else if (binder instanceof Workspace) {
            return ((Workspace)binder).isPreDeleted();
        }
        return false;
    }

    protected Date getPreDeletedDate(Binder binder) {
        Long when = null;
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            when = ((org.kablink.teaming.domain.Folder)binder).getPreDeletedWhen();
        } else if (binder instanceof Workspace) {
            when = ((Workspace)binder).getPreDeletedWhen();
        }
        if (when!=null) {
            return new Date(when);
        }
        return null;
    }

    protected Document getDocument(String xml) {
   		// Parse XML string into a document tree.
   		try {
            if (xml==null || xml.length()==0) {
                return DocumentHelper.createDocument(DocumentHelper.createElement("QUERY"));
            }
   			return XmlUtil.parseText(xml);
   		} catch (DocumentException e) {
   			throw new BadRequestException(ApiErrorCode.BAD_INPUT, "POST body did not contain valid XML: " + e.getLocalizedMessage());
   		}
   	}

    protected InputStream getInputStreamFromMultipartFormdata(HttpServletRequest request)
            throws WebApplicationException, UncheckedIOException {
        InputStream is;
        try {
            ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
            FileItemIterator fii = sfu.getItemIterator(request);
            if (fii.hasNext()) {
                FileItemStream item = fii.next();
                is = item.openStream();
            } else {
                throw new BadRequestException(ApiErrorCode.MISSING_MULTIPART_FORM_DATA, "Missing form data");
            }
        } catch (FileUploadException e) {
            logger.warn("Received bad multipart form data", e);
            throw new BadRequestException(ApiErrorCode.BAD_MULTIPART_FORM_DATA, "Received bad multipart form data");
        } catch (IOException e) {
            logger.error("Error reading multipart form data", e);
            throw new UncheckedIOException(e);
        }
        return is;
    }

    protected String getRawInputStreamAsString(HttpServletRequest request) {
        InputStream is = getRawInputStream(request);
        try {
            return IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            logger.error("Error reading data", e);
            throw new UncheckedIOException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    protected InputStream getRawInputStream(HttpServletRequest request)
            throws UncheckedIOException {
        if (MediaType.APPLICATION_FORM_URLENCODED.equals(request.getContentType())) {
            throw new UnsupportedMediaTypeException(MediaType.APPLICATION_FORM_URLENCODED + " is not a supported media type.");
        }
        if (MediaType.MULTIPART_FORM_DATA.equals(request.getContentType())) {
            throw new UnsupportedMediaTypeException(MediaType.MULTIPART_FORM_DATA + " is not a supported media type.");
        }
        try {
            return request.getInputStream();
        } catch (IOException e) {
            logger.error("Error reading data", e);
            throw new UncheckedIOException(e);
        }
    }

    protected ShareItemSelectSpec getSharedBySpec(Long userId) {
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setSharerId(userId);
        spec.setLatest(true);
        return spec;
    }

    protected Share shareEntity(org.kablink.teaming.domain.DefinableEntity entity, Share share,
                                boolean notifyRecipient, Set<String> notifyAddresses) {
        share.setSharedEntity(new EntityId(entity.getId(), entity.getEntityType().name(), null));
        ShareItem shareItem = toShareItem(share);
        validateNotifyParameters(notifyRecipient, notifyAddresses, shareItem);
        List<ShareItem> shareItems = new ArrayList<ShareItem>(2);
        shareItems.add(shareItem);

        boolean isExternal = false;

        String type = share.getRecipient().getType();
        if (type.equals(ShareRecipient.PUBLIC)) {
            shareItem.setIsPartOfPublicShare(true);
            shareItem.setRecipientType(ShareItem.RecipientType.user);
            shareItem.setRecipientId(Utils.getGuestId(this));
            ShareItem shareItem2 = new ShareItem(shareItem);
            shareItem2.setRecipientType(ShareItem.RecipientType.group);
            shareItem2.setRecipientId(Utils.getAllUsersGroupId());
            shareItems.add(shareItem2);
            notifyRecipient = false;
        } else if (type.equals(ShareRecipient.EXTERNAL_USER)) {
            ShareRecipient recipient = share.getRecipient();
            User user = null;
            if (recipient.getId()!=null && recipient.getId()>0) {
                user = _getUser(recipient.getId());
                if (user.getIdentityInfo().isInternal()) {
                    throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The user with id " + recipient.getId() + " is not an external user.");
                }
            }
            if (recipient.getEmailAddress()!=null) {
                if (user!=null) {
                    if (!recipient.getEmailAddress().equals(user.getEmailAddress())) {
                        throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The email address of user with id " +
                                recipient.getId() + " (" + user.getEmailAddress() + ") does not match the supplied email address: "
                                + recipient.getEmailAddress());
                    }
                } else {
                    if (!getSharingModule().isExternalAddressValid(share.getRecipient().getEmailAddress())) {
                        throw new InvalidEmailAddressException(share.getRecipient().getEmailAddress());
                    }
                    user = getProfileModule().findOrAddExternalUser(share.getRecipient().getEmailAddress());
                }
            }
            if (user==null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No valid external user could be found.");
            }
            shareItem.setRecipientId(user.getId());
            isExternal = true;
        }

        for (ShareItem item : shareItems) {
            ShareItem existing = findExistingShare(getLoggedInUserId(), entity.getEntityIdentifier(), item.getRecipientId(), item.getRecipientType());
            if (existing!=null) {
                shareItem = getSharingModule().modifyShareItem(item, existing.getId());
            } else {
                getSharingModule().addShareItem(item);
            }
        }
        if (notifyRecipient) {
            notifyShareRecipients(shareItem, entity, isExternal, notifyAddresses);
        }
        return ResourceUtil.buildShare(shareItem, entity, buildShareRecipient(shareItem), isGuestAccessEnabled());
    }

    protected void validateNotifyParameters(boolean notifyRecipient, Set<String> notifyAddresses, ShareItem shareItem) {
        if (notifyRecipient && shareItem.getRecipientType() == ShareItem.RecipientType.publicLink) {
            if (notifyAddresses==null || notifyAddresses.size()==0) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing notify_address query parameter.");
            }
            ShareLists shareLists = getSharingModule().getShareLists();
            for (String addr : notifyAddresses) {
                if (!getSharingModule().isExternalAddressValid(addr, shareLists)) {
                    throw new InvalidEmailAddressException(addr);
                }
            }
        }
    }

    protected void notifyShareRecipients(ShareItem shareItem, Set<String> notifyAddresses) {
        org.kablink.teaming.domain.DefinableEntity entity = null;
        if (shareItem.getRecipientType()== ShareItem.RecipientType.publicLink) {
            entity = findDefinableEntity(shareItem.getSharedEntityIdentifier());
            notifyShareRecipients(shareItem, entity, false, notifyAddresses);
        } else {
            notifyShareRecipients(shareItem, entity, false, null);
        }
    }

    protected void notifyShareRecipients(ShareItem shareItem, org.kablink.teaming.domain.DefinableEntity entity, boolean external, Set<String> notifyAddresses) {
        if (shareItem.getRecipientType()==ShareItem.RecipientType.publicLink) {
            Map<String, String> urls = ResourceUtil.buildPublicLinks(shareItem, entity);
            try {
                EmailHelper.sendEmailToPublicLinkRecipients(this, shareItem, getLoggedInUser(),
                        new ArrayList<String>(notifyAddresses), urls.get("view"), urls.get("download"));
            } catch (Exception e) {
                logger.warn("Failed to send share notification email", e);
            }
        } else {
            try {
                EmailHelper.sendEmailToRecipient(this, shareItem, external, getLoggedInUser());
            } catch (Exception e) {
                logger.warn("Failed to send share notification email", e);
            }
        }
    }

    protected ShareItem findExistingShare(Long sharer, EntityIdentifier sharedEntity, Long recipientId, ShareItem.RecipientType recipientType) {
        ShareItemSelectSpec spec = getSharedBySpec(sharer);
        spec.setSharedEntityIdentifier(sharedEntity);
        if (recipientType== ShareItem.RecipientType.user) {
            spec.setRecipients(recipientId, null, null);
        } else if (recipientType== ShareItem.RecipientType.group) {
            spec.setRecipients(null, recipientId, null);
        } else if (recipientType == ShareItem.RecipientType.team) {
            spec.setRecipients(null, null, recipientId);
        } else if (recipientType == ShareItem.RecipientType.publicLink) {
            spec.recipientType = ShareItem.RecipientType.publicLink;
        }

        List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> shares = getShareItems(spec, true, true, true);
        if (shares.size()==0) {
            return null;
        }
        return shares.get(0).getA();
    }

    protected List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> getShareItems(ShareItemSelectSpec spec,
        boolean includeExpired, boolean includePublic, boolean includeNonPublic) {
        return getShareItems(spec, null, includeExpired, includePublic, includeNonPublic, true);
    }

    protected List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> getShareItems(ShareItemSelectSpec spec,
            Long excludedSharer, boolean includeExpired, boolean includePublic, boolean includeNonPublic) {
        return getShareItems(spec, excludedSharer, includeExpired, includePublic, includeNonPublic, true);
    }

    protected List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> getShareItems(ShareItemSelectSpec spec,
            Long excludedSharer, boolean includeExpired, boolean includePublic, boolean includeNonPublic, boolean mergePublicParts) {
        List<ShareItem> shareItems = getSharingModule().getShareItems(spec);
        List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> filteredItems =
                new ArrayList<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>>(shareItems.size());
        Map<String, Boolean> publicIncludedMap = new HashMap<String, Boolean>();
        for (ShareItem item : shareItems) {
            if ((!item.isExpired() || includeExpired) && item.isLatest() &&
                    (excludedSharer==null || !excludedSharer.equals(item.getSharerId()))) {
                boolean partOfPublicShare = item.getIsPartOfPublicShare();
                if (includePublic && partOfPublicShare){
                    if (mergePublicParts) {
                        Boolean publicIncluded = publicIncludedMap.get(item.getSharedEntityIdentifier().toString());
                        if (publicIncluded==null || !publicIncluded) {
                            filteredItems.add(new Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>(item, null));
                            publicIncludedMap.put(item.getSharedEntityIdentifier().toString(), Boolean.TRUE);
                        }
                    } else {
                        filteredItems.add(new Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>(item, null));
                    }
                } else if (includeNonPublic && !partOfPublicShare) {
                    filteredItems.add(new Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>(item, null));
                }
            }
        }
        return filteredItems;
    }

    protected ShareRecipient buildShareRecipient(ShareItem shareItem) {
        Long id = shareItem.getRecipientId();
        String type = null;
        String email = null;
        if (shareItem.getRecipientType()== ShareItem.RecipientType.publicLink) {
            type = ShareRecipient.PUBLIC_LINK;
            id = null;
        } else if (shareItem.getIsPartOfPublicShare()) {
            type = ShareRecipient.PUBLIC;
            id = null;
        } else {
            ShareItem.RecipientType recipientType = shareItem.getRecipientType();
            if (recipientType == ShareItem.RecipientType.user) {
                User user = _getUser(id);
                if (user.getIdentityInfo().isInternal()) {
                    type = ShareRecipient.INTERNAL_USER;
                } else {
                    type = ShareRecipient.EXTERNAL_USER;
                    email = user.getEmailAddress();
                }
            } else if (recipientType == ShareItem.RecipientType.group) {
                type = ShareRecipient.GROUP;
            } else if (recipientType == ShareItem.RecipientType.team) {
                type = ShareRecipient.TEAM;
            }
        }
        return new ShareRecipient(id, type, email);
    }

    protected void populateTimestamps(Map options, DefinableEntity entry)
   	{
   		HistoryStamp creation = entry.getCreation();
   		if(creation != null) {
   			if(creation.getPrincipal() != null)
   				options.put(ObjectKeys.INPUT_OPTION_CREATION_ID, creation.getPrincipal().getId());
   			if(creation.getDate() != null)
   				options.put(ObjectKeys.INPUT_OPTION_CREATION_DATE, creation.getDate());
   		}
   		HistoryStamp modification = entry.getModification();
   		if(modification != null) {
            if(modification.getPrincipal() != null)
                options.put(ObjectKeys.INPUT_OPTION_CREATION_ID, modification.getPrincipal().getId());
   			if(modification.getDate() != null)
   				options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE, modification.getDate());
   		}
   	}

    protected Document buildQueryDocument(String query, Criterion additionalCriteria) {
        query = StringCheckUtil.check(query);

        Document queryDoc = getDocument(query);
        if (additionalCriteria!=null) {
            Element queryRoot = queryDoc.getRootElement();

            // Find the root junction element
            Iterator it = queryRoot.selectNodes("AND|OR|NOT").iterator();

            Element implicit = additionalCriteria.toQuery(queryRoot);

            // Add the user supplied query criteria into our implicit AND
            while(it.hasNext()) {
                implicit.add(((Element)it.next()).detach());
            }

            // Add the SORTBY back to the end of the document
            it = queryRoot.selectNodes("SORTBY").iterator();
            while(it.hasNext()) {
                queryRoot.add(((Element)it.next()).detach());
            }
        }
        return queryDoc;
    }

    protected ShareItem toShareItem(Share share) {
        if (share==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The request body must contain a 'Share' object.");
        }
        EntityId sharedEntity = share.getSharedEntity();
        if (sharedEntity==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'shared_entity' value.");
        }
        if (sharedEntity.getId()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'shared_entity.id' value.");
        }
        if (sharedEntity.getType()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'shared_entity.type' value.");
        }
        ShareRecipient recipient = share.getRecipient();
        if (recipient==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'recipient' value.");
        }
        String type = recipient.getType();
        if (type ==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'recipient.type' value.");
        }
        if (!type.equals(ShareRecipient.EXTERNAL_USER) && !type.equals(ShareRecipient.INTERNAL_USER) &&
                !type.equals(ShareRecipient.EXTERNAL_USER) && !type.equals(ShareRecipient.GROUP) &&
                !type.equals(ShareRecipient.PUBLIC) && !type.equals(ShareRecipient.TEAM) &&
                !type.equals(ShareRecipient.PUBLIC_LINK)) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'recipient.type' value must be one of the following: user, external_user, group, team, public, public_link.");
        }
        if (type.equals(ShareRecipient.PUBLIC)) {
            if (recipient.getId()!=null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'recipient.id' cannot be supplied with 'recipient.type'=='public'.");
            }
            if (recipient.getEmailAddress()!=null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'recipient.email' can be supplied with 'recipient.type'=='public'.");
            }
        } else if (type.equals(ShareRecipient.PUBLIC_LINK)) {
            if (recipient.getId()!=null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'recipient.id' cannot be supplied with 'recipient.type'=='public_link'.");
            }
            if (recipient.getEmailAddress()!=null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'recipient.email' can be supplied with 'recipient.type'=='public_link'.");
            }
        } else if (type.equals(ShareRecipient.EXTERNAL_USER)){
            if (recipient.getId()==null && recipient.getEmailAddress()==null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'recipient.id' or 'recipient.email' must be supplied with 'recipient.type'=='external_user'.");
            }
        } else {
            if (recipient.getId()==null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'recipient.id' value.");
            }
            if (recipient.getEmailAddress()!=null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'recipient.email' can be supplied with 'recipient.type'=='external_user'.");
            }
        }
        Access access = share.getAccess();
        if (type.equals(ShareRecipient.PUBLIC_LINK)) {
            if (access!=null && access.getRole()!=null && !access.getRole().equals(ShareItem.Role.VIEWER.name())) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'access.role' can only be 'VIEWER' for public link shares.");
            }
            access = new Access();
            access.setRole(ShareItem.Role.VIEWER.name());
            share.setAccess(access);
        } else {
            if (access==null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'access' value.");
            }
            if (access.getRole()==null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'access.role' value.");
            }
        }
        if (type.equals(ShareRecipient.PUBLIC_LINK) || type.equals(ShareRecipient.PUBLIC)) {
            SharingPermission sharePerms = access.getSharing();
            if (sharePerms!=null) {
                if (Boolean.TRUE.equals(sharePerms.getExternal()) || Boolean.TRUE.equals(sharePerms.getGrantReshare()) ||
                        Boolean.TRUE.equals(sharePerms.getPublic()) || Boolean.TRUE.equals(sharePerms.getPublicLink()) ||
                        Boolean.TRUE.equals(sharePerms.getInternal())) {
                    throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Cannot specify 'access.sharing' permissions for public shares.");
                }
            }
        }

        EntityIdentifier.EntityType entityType;
        try {
            entityType = EntityIdentifier.EntityType.valueOf(sharedEntity.getType());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The shared_entity.type value must be one of the following: folder, workspace, folderEntry");
        }
        EntityIdentifier entity = new EntityIdentifier(sharedEntity.getId(), entityType);
        ShareItem.RecipientType recType = null;
        if (type.equals(ShareRecipient.TEAM)) {
            recType = ShareItem.RecipientType.team;
        } else if (type.equals(ShareRecipient.INTERNAL_USER)) {
            recType = ShareItem.RecipientType.user;
        } else if (type.equals(ShareRecipient.EXTERNAL_USER)) {
            recType = ShareItem.RecipientType.user;
            // Temporarily set the recipient ID.  Can't be null in the ShareItem constructor.
            if (recipient.getId()==null) {
                recipient.setId(0L);
            }
        } else if (type.equals(ShareRecipient.GROUP)) {
            recType = ShareItem.RecipientType.group;
        } else if (type.equals(ShareRecipient.PUBLIC)) {
            recType = ShareItem.RecipientType.group;
            recipient.setId(Utils.getAllUsersGroupId());
        } else if (type.equals(ShareRecipient.PUBLIC_LINK)) {
            recType = ShareItem.RecipientType.publicLink;
            recipient.setId(0L);

            if (entity.getEntityType() != EntityIdentifier.EntityType.folderEntry) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'recipient.type' of 'public_link' is only valid for 'folderEntry' recipients.");
            }
        }
        ShareItem.Role role;
        try {
            role = ShareItem.Role.valueOf(access.getRole());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The 'access.role' value must be one of the following: VIEWER, EDITOR, CONTRIBUTOR");
        }
        if (share.getDaysToExpire()!=null && share.getDaysToExpire()<=0) {
            share.setDaysToExpire(null);
        }
        if (share.getDaysToExpire()!=null && share.getEndDate()!=null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "You cannot specify both 'days_to_expire' and 'expiration'.");
        }
        if (share.getEndDate()!=null && share.getEndDate().before(new Date())) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The 'expiration' value cannot be in the past.");
        }

        WorkAreaOperation.RightSet rights = (WorkAreaOperation.RightSet) role.getRightSet().clone();
        SharingPermission sharing = access.getSharing();
        if (sharing!=null) {
            if (Boolean.TRUE.equals(sharing.getInternal())) {
                rights.setAllowSharing(true);
            }
            if (Boolean.TRUE.equals(sharing.getExternal())) {
                rights.setAllowSharingExternal(true);
            }
            if (Boolean.TRUE.equals(sharing.getPublic())) {
                rights.setAllowSharingPublic(true);
            }
            if (Boolean.TRUE.equals(sharing.getPublicLink())) {
                rights.setAllowSharingPublicLinks(true);
            }
            if (rights.isAllowSharing() || rights.isAllowSharingExternal() || rights.isAllowSharingPublic()) {
                rights.setAllowSharingForward(true);
            }
        }
        ShareItem shareItem = new ShareItem(getLoggedInUserId(), entity, share.getComment(), share.getEndDate(), recType, recipient.getId(), rights);
        if (share.getDaysToExpire()!=null) {
            shareItem.setDaysToExpire(share.getDaysToExpire());
        }
        if (type.equals(ShareRecipient.PUBLIC)) {
            shareItem.setIsPartOfPublicShare(true);
        }
        return shareItem;
    }

    protected void populateParentBinderPaths(SearchResultList list) {
        Set<Long> binderIds = new HashSet<Long>();
        for (Object o : list.getResults()) {
            if (o instanceof FileProperties) {
                ParentBinder binder = ((FileProperties) o).getBinder();
                if (binder!=null) {
                    binderIds.add(binder.getId());
                }
            }
            else if (o instanceof DefinableEntity) {
                ParentBinder binder = ((DefinableEntity) o).getParentBinder();
                if (binder!=null) {
                    binderIds.add(binder.getId());
                }
            }
            else if (o instanceof DefinableEntityBrief) {
                ParentBinder binder = ((DefinableEntityBrief) o).getParentBinder();
                if (binder!=null) {
                    binderIds.add(binder.getId());
                }
            }
        }

        Map<Long, String> binderPaths = lookUpBinderPaths(binderIds, 500);
        for (Object o : list.getResults()) {
            if (o instanceof FileProperties) {
                ParentBinder binder = ((FileProperties) o).getBinder();
                if (binder!=null) {
                    binder.setPath(binderPaths.get(binder.getId()));
                }
            }
            else if (o instanceof DefinableEntity) {
                ParentBinder binder = ((DefinableEntity) o).getParentBinder();
                if (binder!=null) {
                    binder.setPath(binderPaths.get(binder.getId()));
                }
            }
            else if (o instanceof DefinableEntityBrief) {
                ParentBinder binder = ((DefinableEntityBrief) o).getParentBinder();
                if (binder!=null) {
                    binder.setPath(binderPaths.get(binder.getId()));
                }
            }
        }
    }

    protected Map<Long, String> lookUpBinderPaths(Set<Long> ids, int batchSize) {
        int count = 0;
        Map<Long, String> binderPaths = new HashMap<Long, String>();
        Junction idCriterion = null;
        for (Long id : ids) {
            if (idCriterion==null) {
                idCriterion = Restrictions.disjunction();
            }
            idCriterion.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
            count++;
            if (count>=batchSize) {
                fillPathMap(binderPaths, searchForBinders(idCriterion));
                idCriterion = null;
                count = 0;
            }
        }
        if (count>0) {
            fillPathMap(binderPaths, searchForBinders(idCriterion));
        }
        return binderPaths;
    }

    protected SearchResultList<SearchableObject> lookUpChildren(Criteria crit, int descriptionFormat, Integer offset, Integer maxCount, String nextUrl, Map<String, Object> nextParams, Date lastModified) {
        if (offset==null) {
            offset = 0;
        }
        if (maxCount==null) {
            maxCount = -1;
        }
        crit.addOrder(new Order(Constants.ENTITY_FIELD, true));
        crit.addOrder(new Order(Constants.SORT_TITLE_FIELD, true));
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>(offset);
        results.setLastModified(lastModified);
        SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(this, descriptionFormat, false), resultMap, nextUrl, nextParams, offset);
        
        if(results.getCount() == 0 && SPropsUtil.getBoolean("rest.api.log.zero.size.search.results", false)) {
        	StringBuilder sb = new StringBuilder("REST response: (")
					.append(RequestContextHolder.getRequestContext().getClientIdentity())
        			.append(") ")
        			.append("Empty result from lookUpChildren using node '")
        			.append(RequestContextHolder.getRequestContext().getLastSearchNodeName())
        			.append("'");
        	logger.warn(sb.toString());
        }

        return results;
    }

    protected SearchResultList<BinderBrief> lookUpBinders(Criteria crit, int descriptionFormat, Integer offset, Integer maxCount, String nextUrl, Map<String, Object> nextParams, Date lastModified) {
        if (offset==null) {
            offset = 0;
        }
        if (maxCount==null) {
            maxCount = -1;
        }
        crit.add(SearchUtils.buildBindersCriterion());
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        results.setLastModified(lastModified);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(descriptionFormat), resultMap, nextUrl, nextParams, offset);
        return results;
    }

    protected SearchResultList<FolderEntryBrief> lookUpEntries(Criteria crit, Integer offset, Integer maxCount, String nextUrl, Map<String, Object> nextParams) {
        if (offset==null) {
            offset = 0;
        }
        if (maxCount==null) {
            maxCount = -1;
        }
        crit.add(SearchUtils.buildEntriesCriterion());
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new FolderEntryBriefBuilder(), resultMap, nextUrl, nextParams, offset);
        return results;
    }

    protected SearchResultList<FileProperties> lookUpAttachments(Criteria crit, Integer offset, Integer maxCount, String nextUrl, Map<String, Object> nextParams, Date lastModified) {
        if (offset==null) {
            offset = 0;
        }
        if (maxCount==null) {
            maxCount = -1;
        }
        //crit.add(buildAttachmentsCriterion());
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>(offset);
        results.setLastModified(lastModified);
        SearchResultBuilderUtil.buildSearchResults(results, new FilePropertiesBuilder(this), resultMap, nextUrl, nextParams, offset);
        return results;
    }

    protected Map searchForBinders(Criterion criterion) {
        Junction outerCriterion = Restrictions.conjunction()
                .add(SearchUtils.buildBindersCriterion()).add(criterion);
        Criteria crit = new Criteria();
        crit.add(outerCriterion);
        return getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, -1, null);
    }

    protected void fillPathMap(Map<Long, String> binderPaths, Map resultMap) {
        List<Map> entries = (List<Map>)resultMap.get(ObjectKeys.SEARCH_ENTRIES);
        for (Map entry : entries) {
            String binderIdStr = (String) entry.get(Constants.DOCID_FIELD);
            Long binderId = (binderIdStr != null)? Long.valueOf(binderIdStr) : null;
            String path = (String) entry.get(Constants.ENTITY_PATH);
            if (binderId!=null && path!=null) {
                binderPaths.put(binderId, path);
            }
        }
    }

    protected SearchResultList<Tag> getBinderTags(org.kablink.teaming.domain.Binder binder, boolean hidden) {
        Collection<org.kablink.teaming.domain.Tag> tags = getBinderModule().getTags(binder);
        SearchResultList<Tag> results = new SearchResultList<Tag>();
        for (org.kablink.teaming.domain.Tag tag : tags) {
            Tag obj = ResourceUtil.buildTag(tag);
            if (hidden==isHidden(obj)) {
                results.append(obj);
            }
        }
        return results;
    }

    protected SearchResultList<Tag> getEntryTags(org.kablink.teaming.domain.FolderEntry entry, boolean hidden) {
        Collection<org.kablink.teaming.domain.Tag> tags = getFolderModule().getTags(entry);
        SearchResultList<Tag> results = new SearchResultList<Tag>();
        for (org.kablink.teaming.domain.Tag tag : tags) {
            Tag obj = ResourceUtil.buildTag(tag);
            if (hidden==isHidden(obj)) {
                results.append(obj);
            }
        }
        return results;
    }

    protected boolean isHidden(Tag tag) {
        return isHiddenInSharedByMe(tag) || isHiddenInSharedWithMe(tag);
    }

    protected boolean isHiddenInSharedByMe(Tag tag) {
        return ObjectKeys.HIDDEN_SHARED_BY_TAG.equals(tag.getName());
    }

    protected boolean isHiddenInSharedWithMe(Tag tag) {
        return ObjectKeys.HIDDEN_SHARED_WITH_TAG.equals(tag.getName());
    }

    protected ShareItemSelectSpec getSharedWithSpec(Long userId) {
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setRecipientsFromUserMembership(userId);
        spec.setLatest(true);
        return spec;
    }

    protected List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> getSharedByShareItems(Long userId, Boolean deleted) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        spec.deleted = deleted;
        return getShareItems(spec, null, true, true, true);
    }

    protected List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> getSharedWithShareItems(Long userId, Boolean deleted) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        spec.deleted = deleted;
        return getShareItems(spec, userId, true, false, true);
    }

    protected List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> getPublicShareItems(Boolean deleted) {
        ShareItemSelectSpec spec = getPublicSpec();
        spec.deleted = deleted;
        return getShareItems(spec, null, true, true, false);
    }

    protected ShareItemSelectSpec getPublicSpec() {
        User guest = getProfileModule().getGuestUser();
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setRecipients(guest.getId(), null, null);
        spec.setLatest(true);
        return spec;
    }

    protected Date getSharedByLibraryModifiedDate(Long userId, boolean recursive) {
        return getSharesLibraryModifiedDate(getSharedByShareItems(userId, null), recursive);
    }

    protected Date getSharedWithLibraryModifiedDate(Long userId, boolean recursive) {
        return getSharesLibraryModifiedDate(getSharedWithShareItems(userId, null), recursive);
    }

    protected Date getPublicSharesLibraryModifiedDate(boolean recursive) {
        return getSharesLibraryModifiedDate(getPublicShareItems(null), recursive);
    }

    protected Date getSharesLibraryModifiedDate(List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> shareItems,
                                                boolean recursive) {
        // Include deleted entries as well.
        Date libraryModifiedDateForShareItems = getLibraryModifiedDateForShareItems(recursive, shareItems);
        Date hideDate = getSharingModule().getHiddenShareModTimeForCurrentUser(true);
        return ResourceUtil.max(hideDate, libraryModifiedDateForShareItems);
    }

    protected Date getLibraryModifiedDateForShareItems(boolean recursive, List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> shareItems) {
        Date maxDate = new Date(0);
        List<Long> binderList = new ArrayList<Long>();
        for (Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity> pair : shareItems) {
            ShareItem item = pair.getA();
            if (item.isExpired()) {
                maxDate = ResourceUtil.max(maxDate, item.getEndDate());
            } else if (item.isDeleted()) {
                maxDate = ResourceUtil.max(maxDate, item.getDeletedDate());
            } else {
                maxDate = ResourceUtil.max(maxDate, item.getStartDate());
                org.kablink.teaming.domain.DefinableEntity entity = getDefinableEntity(pair, true);
                if (entity!=null) {
                    if (entity.getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                        FolderEntry entry = (FolderEntry) entity;
                        if (entry.isPreDeleted()) {
                            maxDate = ResourceUtil.max(maxDate, new Date(entry.getPreDeletedWhen()));
                        } else {
                            maxDate = ResourceUtil.max(maxDate, entry.getModificationDate());
                        }
                    } else if (entity.getEntityType().isBinder()) {
                        Binder binder = (Binder) entity;
                        if (isBinderPreDeleted(binder)) {
                            maxDate = ResourceUtil.max(maxDate, getPreDeletedDate(binder));
                        } else if (recursive) {
                            binderList.add(binder.getId());
                        }
                    }
                }
            }
        }
        maxDate = ResourceUtil.max(maxDate, getLibraryModifiedDate(binderList.toArray(new Long[binderList.size()]), recursive, false));
        return maxDate;
    }

    protected LibraryInfo getSharedByLibraryInfo(Long userId) {
        return getSharesLibraryInfo(null, getSharedBySpec(userId), true, true);
    }

    protected LibraryInfo getSharedWithLibraryInfo(Long userId) {
        return getSharesLibraryInfo(userId, getSharedWithSpec(userId), false, true);
    }

    protected LibraryInfo getPublicSharesLibraryInfo() {
        Long userId = getLoggedInUserId();
        return getSharesLibraryInfo(null, getSharedWithSpec(userId), true, false);
    }

    protected LibraryInfo getSharesLibraryInfo(Long userId, ShareItemSelectSpec spec, boolean includePublic, boolean includeNonPublic) {
        // Include deleted entries as well.
        spec.deleted = null;
        List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> shareItems =
                getShareItems(spec, userId, true, includePublic, includeNonPublic);
        LibraryInfo info = getLibraryInfoForShareItems(shareItems);
        Date hideDate = getSharingModule().getHiddenShareModTimeForCurrentUser(true);
        info.setModifiedDate(ResourceUtil.max(hideDate, info.getModifiedDate()));
        return info;
    }

    protected LibraryInfo getLibraryInfoForShareItems(List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> shareItems) {
        Date maxDate = new Date(0);
        int files = 0;
        int folders = 0;
        long diskSpace = 0;
        List<Long> binderList = new ArrayList<Long>();
        for (Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity> pair : shareItems) {
            ShareItem item = pair.getA();
            if (item.isExpired()) {
                maxDate = ResourceUtil.max(maxDate, item.getEndDate());
            } else if (item.isDeleted()) {
                maxDate = ResourceUtil.max(maxDate, item.getDeletedDate());
            } else {
                maxDate = ResourceUtil.max(maxDate, item.getStartDate());
                org.kablink.teaming.domain.DefinableEntity entity = getDefinableEntity(pair, true);
                if (entity!=null) {
                    if (entity.getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                        FolderEntry entry = (FolderEntry) entity;
                        if (entry.isPreDeleted()) {
                            maxDate = ResourceUtil.max(maxDate, new Date(entry.getPreDeletedWhen()));
                        } else {
                            maxDate = ResourceUtil.max(maxDate, entry.getModificationDate());
                        }
                        for (Attachment att : entry.getAttachments()) {
                            if (att instanceof FileAttachment) {
                                files++;
                                diskSpace += ((FileAttachment)att).getFileItem().getLength();
                            }
                        }
                    } else if (entity.getEntityType().isBinder()) {
                        Binder binder = (Binder) entity;
                        if (isBinderPreDeleted(binder)) {
                            maxDate = ResourceUtil.max(maxDate, getPreDeletedDate(binder));
                        } else {
                            binderList.add(binder.getId());
                            folders++;
                        }
                    }
                }
            }
        }
        LibraryInfo info = getLibraryInfo(binderList.toArray(new Long[binderList.size()]));
        info.setModifiedDate(ResourceUtil.max(maxDate, info.getModifiedDate()));
        info.setDiskSpace(info.getDiskSpace() + diskSpace);
        info.setFolderCount(info.getFolderCount() + folders);
        info.setFileCount(info.getFileCount() + files);
        return info;
    }

    protected org.kablink.teaming.domain.DefinableEntity getDefinableEntity(Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity> pair,
                                                                            boolean accessCheck) {
        org.kablink.teaming.domain.DefinableEntity entity = pair.getB();
        if (entity==null) {
            ShareItem item = pair.getA();
            if (accessCheck) {
                try {
                    entity = getSharingModule().getSharedEntity(item);
                } catch (AccessControlException e) {
                } catch (NoBinderByTheIdException e) {
                } catch (NoFolderEntryByTheIdException e) {
                }
            } else {
                try {
                    entity = getSharingModule().getSharedEntityWithoutAccessCheck(item);
                } catch (NoBinderByTheIdException e) {
                } catch (NoFolderEntryByTheIdException e) {
                }
            }
            pair.setB(entity);
        }
        return entity;
    }

    protected Date getLibraryModifiedDate(Long [] binderIds, boolean recursive, boolean allowJits) {
        if (binderIds.length==0) {
            return null;
        }
        if (allowJits && !recursive) {
            triggerJitsIfNecessary(binderIds);
        }
        Criteria crit = getLibraryCriteria(binderIds, false, recursive);

        Map resultsMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, 1, null);
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>();
        SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(this, Description.FORMAT_NONE, false), resultsMap, null, null, 0);
        return results.getLastModified();
    }

    protected void triggerJitsIfNecessary(Long [] binderIds) {
        for (Long id : binderIds) {
            try {
                Binder binder = getBinderModule().getBinder(id);
                if (binder instanceof Folder && binder.isMirrored()) {
                    getFolderModule().jitSynchronize((Folder)binder);
                }
            } catch (Exception e) {
            }
        }
    }

    protected LibraryInfo getLibraryInfo(Long [] binderIds) {
        return getLibraryInfo(binderIds, false);
    }

    protected LibraryInfo getLibraryInfo(Long [] binderIds, boolean mirrored) {
        if (binderIds.length==0) {
            return new LibraryInfo(0L, 0, 0, null);
        }
        Set<Long> idSet = new HashSet<Long>(Arrays.asList(binderIds));
        Criteria crit = getLibraryCriteria(binderIds, false, true);

        Map resultsMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, -1,
        		org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOC_TYPE_FIELD,Constants.FILE_SIZE_IN_BYTES_FIELD,Constants.DOCID_FIELD,Constants.MODIFICATION_DATE_FIELD));
        LibraryInfo info = new LibraryInfo();
        Date modDate = null;
        long diskSpace = 0;
        int files = 0;
        int folders = 0;
        List<Map> entries = (List<Map>)resultsMap.get(ObjectKeys.SEARCH_ENTRIES);
        if (entries!=null) {
            for (Map entry : entries) {
                String docType = (String) entry.get(Constants.DOC_TYPE_FIELD);
                if (Constants.DOC_TYPE_BINDER.equals(docType)) {
                    String binderIdStr = (String) entry.get(Constants.DOCID_FIELD);
                    Long binderId = (binderIdStr != null)? Long.valueOf(binderIdStr) : null;
                    if (!idSet.contains(binderId)) {
                        folders++;
                    }
                    modDate = ResourceUtil.max(modDate, (Date) entry.get(Constants.MODIFICATION_DATE_FIELD));
                } else if (Constants.DOC_TYPE_ENTRY.equals(docType)) {
                    files++;
                    String sizeStr = SearchResultBuilderUtil.getString(entry, Constants.FILE_SIZE_IN_BYTES_FIELD);
                    if(sizeStr != null)
                        try {
                            diskSpace += Long.valueOf(sizeStr);
                        } catch (NumberFormatException e) {
                        }
                    modDate = ResourceUtil.max(modDate, (Date) entry.get(Constants.MODIFICATION_DATE_FIELD));
                }
            }
        }
        info.setDiskSpace(diskSpace);
        info.setFileCount(files);
        info.setFolderCount(folders);
        info.setModifiedDate(modDate);
        if (mirrored) {
            populateMirroredLibraryInfo(info, idSet);
        } else {
            info.setMirrored(Boolean.FALSE);
        }
        return info;
    }

    protected Criteria getLibraryCriteria(Long[] binderIds, boolean includeAttachments, boolean recursive) {
        Criteria crit = new Criteria();
        if (recursive) {
            Junction or = Restrictions.disjunction();
            for (Long binderId : binderIds) {
                or.add(SearchUtils.buildAncentryCriterion(binderId));
            }
            crit.add(or);
            or = buildWorkspacesAndLibraryEntitiesCriterion(includeAttachments);
            crit.add(or);
        } else {
            Junction or = Restrictions.disjunction();
            for (Long binderId : binderIds) {
                or.add(SearchUtils.buildBinderCriterion(binderId));
                or.add(SearchUtils.buildParentBinderCriterion(binderId));
            }
            crit.add(or);
            crit.add(buildWorkspacesAndLibraryEntitiesCriterion(includeAttachments));
        }

        crit.addOrder(new Order(Constants.MODIFICATION_DATE_FIELD, false));
        return crit;
    }

    private Junction buildWorkspacesAndLibraryEntitiesCriterion(boolean includeAttachments) {
        Junction or;
        or = Restrictions.disjunction();
        or.add(SearchUtils.buildWorkspacesCriterion());

        Junction and = Restrictions.conjunction();
        or.add(and);
        and.add(SearchUtils.buildLibraryCriterion(Boolean.TRUE, Boolean.FALSE));
        Junction or2 = Restrictions.disjunction();
        or2.add(SearchUtils.buildFoldersCriterion());
        or2.add(SearchUtils.buildEntriesCriterion());
        if (includeAttachments) {
            or2.add(SearchUtils.buildAttachmentsCriterion());
        }
        and.add(or2);
        return or;
    }

    protected Date getMyFilesLibraryModifiedDate(boolean recursive, boolean allowJits) {
        Long [] ids;
        if (recursive) {
            ids = new Long[] {getMyFilesFolderParent().getId()};
        } else if (SearchUtils.useHomeAsMyFiles(this)) {
            List<Long> homeFolderIds = SearchUtils.getHomeFolderIds(this, getLoggedInUser());
            ids = homeFolderIds.toArray(new Long[homeFolderIds.size()]);
            if (allowJits && !recursive) {
                triggerJitsIfNecessary(ids);
            }
        } else {
            List<Long> hiddenFolderIds = new ArrayList<Long>();
        	Long mfId = SearchUtils.getMyFilesFolderId(this, getLoggedInUser(), false);
            if (null != mfId) {
            	hiddenFolderIds.add(mfId);
            }
            hiddenFolderIds.add(getMyFilesFolderParent().getId());
            ids = hiddenFolderIds.toArray(new Long[hiddenFolderIds.size()]);
        }
        return DateHelper.max(getLibraryModifiedDate(ids, recursive, false), AdminHelper.getEffectiveAdhocFolderSettingDate(this, getLoggedInUser()));
    }

    protected LibraryInfo getMyFilesLibraryInfo() {
        int hiddenFolders = 0;
        Long [] ids;
        List<Long> homeFolderIds = SearchUtils.getHomeFolderIds(this, getLoggedInUser());
        if (SearchUtils.useHomeAsMyFiles(this)) {
            ids = homeFolderIds.toArray(new Long[homeFolderIds.size()]);
        } else {
            ids = new Long[] {getMyFilesFolderParent().getId()};
            Long mfId = SearchUtils.getMyFilesFolderId(this, getLoggedInUser(), false);
            hiddenFolders = ((null == mfId) ? 0 : 1);
        }
        LibraryInfo libraryInfo = getLibraryInfo(ids);
        libraryInfo.setFolderCount(libraryInfo.getFolderCount() - hiddenFolders);
        if (homeFolderIds.size()>0) {
            populateMirroredLibraryInfo(libraryInfo, homeFolderIds);
            libraryInfo.setAllowClientInitiatedSync(true);
        }
        libraryInfo.setModifiedDate(DateHelper.max(libraryInfo.getModifiedDate(),
                AdminHelper.getEffectiveAdhocFolderSettingDate(this, getLoggedInUser())));
        return libraryInfo;
    }

    private void populateMirroredLibraryInfo(LibraryInfo libraryInfo, Iterable<Long> binderIds) {
        libraryInfo.setMirrored(Boolean.TRUE);
        Date syncDate = null;
        for (Long id : binderIds) {
            syncDate = ResourceUtil.max(syncDate, getFolderModule().getLastFullSyncCompletionTime(id));
        }
        libraryInfo.setLastMirroredSyncDate(syncDate);
    }

    protected Binder getMyFilesFolderParent() {
        org.kablink.teaming.domain.User loggedInUser = getLoggedInUser();
        if (SearchUtils.useHomeAsMyFiles(this, loggedInUser)) {
            return _getHomeFolder();
        } else {
            return _getUserWorkspace();
        }
    }

    protected Boolean getEffectivePublicCollectionSetting(org.kablink.teaming.domain.User user) {
        return AdminHelper.getEffectivePublicCollectionSetting(this, user);
    }

    protected BinderBrief getFakeMyFileFolders(boolean checkIfValid) {
        if (checkIfValid && !SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            return null;
        }
        org.kablink.teaming.domain.User user = getLoggedInUser();
        Binder folderParent = getMyFilesFolderParent();
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setId(ObjectKeys.MY_FILES_ID);
        binder.setTitle("My Files");
        binder.setPath(folderParent.getPathName());
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        binder.setPermaLink(PermaLinkUtil.getUserPermalink(null, user.getId().toString(), PermaLinkUtil.COLLECTION_MY_FILES));
        String baseUri = "/self/my_files";
        binder.setLink(baseUri);
        binder.setMirrored(SearchUtils.useHomeAsMyFiles(this, user));
        binder.addAdditionalLink("child_binders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        binder.addAdditionalLink("initial_sync", baseUri + "/initial_sync");
        binder.addAdditionalLink("library_changes", baseUri + "/library_changes");
        binder.addAdditionalLink("library_children", baseUri + "/library_children");
        binder.addAdditionalLink("library_info", baseUri + "/library_info");
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }

    protected BinderBrief getFakeSharedWithMe() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setId(ObjectKeys.SHARED_WITH_ME_ID);
        binder.setTitle("Shared with Me");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        Long userId = getLoggedInUserId();
        binder.setPermaLink(PermaLinkUtil.getUserPermalink(null, userId.toString(), PermaLinkUtil.COLLECTION_SHARED_WITH_ME));
        binder.setLink("/self/shared_with_me");
        String baseUri = "/shares/with_user/" + userId;
        binder.addAdditionalLink("child_binders", baseUri + "/binders");
        binder.addAdditionalLink("child_binder_tree", baseUri + "/binder_tree");
        binder.addAdditionalLink("child_entries", baseUri + "/entries");
        binder.addAdditionalLink("child_files", baseUri + "/files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        binder.addAdditionalLink("library_changes", baseUri + "/library_changes");
        binder.addAdditionalLink("library_children", baseUri + "/library_children");
        binder.addAdditionalLink("library_info", baseUri + "/library_info");
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }

    protected BinderBrief getFakeSharedByMe() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setId(ObjectKeys.SHARED_BY_ME_ID);
        binder.setTitle("Shared by Me");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        Long userId = getLoggedInUserId();
        binder.setPermaLink(PermaLinkUtil.getUserPermalink(null, userId.toString(), PermaLinkUtil.COLLECTION_SHARED_BY_ME));
        binder.setLink("/self/shared_by_me");
        String baseUri = "/shares/by_user/" + userId;
        binder.addAdditionalLink("child_binders", baseUri + "/binders");
//        binder.addAdditionalLink("child_binder_tree", baseUri + "/binder_tree");
        binder.addAdditionalLink("child_entries", baseUri + "/entries");
        binder.addAdditionalLink("child_files", baseUri + "/files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
        binder.addAdditionalLink("library_changes", baseUri + "/library_changes");
        binder.addAdditionalLink("library_children", baseUri + "/library_children");
        binder.addAdditionalLink("library_info", baseUri + "/library_info");
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }

    protected BinderBrief getFakeNetFolders() {
        BinderBrief binder = new BinderBrief();

        Binder netFoldersBinder = SearchUtils.getNetFoldersRootBinder();
        //TODO: localize
        binder.setId(ObjectKeys.NET_FOLDERS_ID);
        binder.setTitle("Net Folders");
        binder.setPath(netFoldersBinder.getPathName());
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        Long userId = getLoggedInUserId();
        binder.setLink("/self/net_folders");
        binder.setPermaLink(PermaLinkUtil.getUserPermalink(null, userId.toString(), PermaLinkUtil.COLLECTION_NET_FOLDERS));
        String baseUri = "/net_folders";
        binder.addAdditionalLink("child_binders", baseUri);
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_folders", baseUri);
        binder.addAdditionalLink("library_children", baseUri);
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }

    protected BinderBrief getFakePublicShares(boolean checkIfValid) {
        if (checkIfValid && !getEffectivePublicCollectionSetting(getLoggedInUser())) {
            return null;
        }

        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setId(ObjectKeys.PUBLIC_SHARES_ID);
        binder.setTitle("Public");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        Long userId = getLoggedInUserId();
        binder.setLink("/self/public_shares");
        binder.setPermaLink(PermaLinkUtil.getUserPermalink(null, userId.toString(), PermaLinkUtil.COLLECTION_SHARED_PUBLIC));
        String baseUri = "/shares/public";
        binder.addAdditionalLink("child_binders", baseUri + "/binders");
        binder.addAdditionalLink("child_binder_tree", baseUri + "/binder_tree");
        binder.addAdditionalLink("child_files", baseUri + "/files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        binder.addAdditionalLink("library_changes", baseUri + "/library_changes");
        binder.addAdditionalLink("library_children", baseUri + "/library_children");
        binder.addAdditionalLink("library_info", baseUri + "/library_info");
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }

    protected BinderBrief getFakeMyTeams() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setId(ObjectKeys.MY_TEAMS_ID);
        binder.setTitle("My Teams");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace_team.png"));
        Long userId = getLoggedInUserId();
        binder.setLink("/self/my_teams");
        String baseUri = LinkUriUtil.getUserLinkUri(userId);
        binder.addAdditionalLink("child_binders", baseUri + "/teams");
        binder.addAdditionalLink("child_library_folders", baseUri + "/teams");
        binder.addAdditionalLink("library_children", baseUri + "/teams");
        return binder;
    }

    protected BinderBrief getFakeMyFavorites() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setId(ObjectKeys.My_FAVORITES_ID);
        binder.setTitle("My Favorites");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace_star.png"));
        Long userId = getLoggedInUserId();
        binder.setLink("/self/my_favorites");
        String baseUri = LinkUriUtil.getUserLinkUri(userId);
        binder.addAdditionalLink("child_binders", baseUri + "/favorites");
        binder.addAdditionalLink("child_library_folders", baseUri + "/favorites");
        binder.addAdditionalLink("library_children", baseUri + "/favorites");
        return binder;
    }

    protected Access getAccessRole(org.kablink.teaming.domain.DefinableEntity definableEntity) {
        SharingModule.EntityShareRights shareRights = getSharingModule().calculateHighestEntityShareRights(definableEntity.getEntityIdentifier());

        Access access = new Access();
        access.setRole(shareRights.getTopRole().name());
        access.setSharing(getSharingPermission(definableEntity, shareRights.getMaxGrantRole()));

        return access;
    }

    private SharingPermission getSharingPermission(org.kablink.teaming.domain.DefinableEntity entity, ShareItem.Role role) {
        SharingPermission sharing = new SharingPermission();
        sharing.setMaxRole(role.name());
        if (role != ShareItem.Role.NONE) {
            SharingModule sharingModule = getSharingModule();
            sharing.setInternal(sharingModule.testAddShareEntityInternal(entity));
            sharing.setExternal(sharingModule.testAddShareEntityExternal(entity));
            sharing.setPublic(sharingModule.testAddShareEntityPublic(entity) && isGuestAccessEnabled());
            sharing.setPublicLink(sharingModule.testAddShareEntityPublicLinks(entity));
            sharing.setGrantReshare(sharingModule.testShareEntityForward(entity));
        } else {
            sharing.setInternal(false);
            sharing.setExternal(false);
            sharing.setPublic(false);
            sharing.setPublicLink(false);
            sharing.setGrantReshare(false);
        }
        return sharing;
    }

    private boolean testOperation(User user, FolderEntry entry, WorkAreaOperation operation) {
        try {
            AccessUtils.operationCheck(user, entry, operation);
            return true;
        } catch (AccessControlException e) {
            return false;
        }
    }

    protected void validateMandatoryField(Object obj, String... methodNames) {
        validateField(obj, true, false, null, methodNames);
    }

    protected void validateDisallowedField(Object obj, String reason, String... methodNames) {
        validateField(obj, false, true, reason, methodNames);
    }

    protected boolean isDefined(Object obj, String... methodNames) {
        Object currObj = obj;
        for (String methodName : methodNames) {
            try {
                Method method = currObj.getClass().getMethod(methodName);
                Object value = method.invoke(obj);
                if (value==null || (value instanceof String && ((String)value).length()==0)) {
                    return false;
                }
                currObj = value;
            } catch (NoSuchMethodException e) {
                throw new InvokeException("Error executing method " + methodName + " in class " + obj.getClass().getName(), e);
            } catch (InvocationTargetException e) {
                throw new InvokeException("Error executing method " + methodName + " in class " + obj.getClass().getName(), e);
            } catch (IllegalAccessException e) {
                throw new InvokeException("Error executing method " + methodName + " in class " + obj.getClass().getName(), e);
            }
        }
        return true;
    }

    protected void validateField(Object obj, boolean failIfMissing, boolean failIfExists, String disallowedReason, String... methodNames) {
        Object currObj = obj;
        String fieldName = null;
        for (String methodName : methodNames) {
            try {
                Method method = currObj.getClass().getMethod(methodName);
                String currFieldName = getFieldName(method, methodName);
                if (fieldName!=null) {
                    fieldName = fieldName + ".'" + currFieldName + "'";
                } else {
                    fieldName = "'" + currFieldName + "'";
                }
                Object value = method.invoke(currObj);
                if (failIfMissing) {
                    if (value==null || (value instanceof String && ((String)value).length()==0)) {
                        throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing mandatory field: " + fieldName );
                    }
                }
                if (failIfExists) {
                    if (value!=null && (value instanceof String && ((String)value).length()>0)) {
                        throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Field is not allowed: " + fieldName + ".  Reason: " + disallowedReason);
                    }
                }
                currObj = value;
            } catch (NoSuchMethodException e) {
                throw new InvokeException("Error executing method " + methodName + " in class " + obj.getClass().getName(), e);
            } catch (InvocationTargetException e) {
                throw new InvokeException("Error executing method " + methodName + " in class " + obj.getClass().getName(), e);
            } catch (IllegalAccessException e) {
                throw new InvokeException("Error executing method " + methodName + " in class " + obj.getClass().getName(), e);
            }
        }
    }

    private String getFieldName(Method method, String methodName) {
        String field = null;
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof XmlElement) {
                field = ((XmlElement)annotation).name();
            }
        }
        if (field==null) {
            if (methodName.startsWith("get")) {
                field = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            } else if (methodName.startsWith("get")) {
                field = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
            } else {
                field = methodName;
            }
        }
        return field;
    }

    protected <T extends Enum<T>> T toEnum(Class<T> enumType, String fieldName, String value) {
        try {
            if (value==null) {
                return null;
            }
            return Enum.valueOf(enumType, value);
        } catch (Exception e) {
            StringBuilder builder = new StringBuilder('\'').append(fieldName).append(" must be one of: ");
            T[] vals = enumType.getEnumConstants();
            boolean first = true;
            for (T val : vals) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append(val.name());
            }
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, builder.toString());
        }
    }

    protected ExternalSharingRestrictions _getExternalSharingRestrictions() {
        ExternalSharingRestrictions restrictions = new ExternalSharingRestrictions();
        ShareLists shareLists = getSharingModule().getShareLists();
        ShareLists.ShareListMode shareListMode = shareLists.getShareListMode();
        if (shareListMode== ShareLists.ShareListMode.DISABLED) {
            restrictions.setMode(ExternalSharingRestrictions.Mode.none.name());
        } else if (shareListMode == ShareLists.ShareListMode.BLACKLIST) {
            restrictions.setMode(ExternalSharingRestrictions.Mode.blacklist.name());
        } else {
            restrictions.setMode(ExternalSharingRestrictions.Mode.whitelist.name());
        }
        List<String> domains = shareLists.getDomains();
        if (domains==null) {
            restrictions.setDomainList(new ArrayList<String>(0));
        } else {
            restrictions.setDomainList(domains);
        }
        List<String> emailAddresses = shareLists.getEmailAddresses();
        if (emailAddresses==null) {
            restrictions.setEmailList(new ArrayList<String>(0));
        } else {
            restrictions.setEmailList(emailAddresses);
        }
        return restrictions;
    }

    protected BinderChanges getBinderChanges(Long [] binderIds, Long [] entryIds, String since, boolean recursive, String descriptionFormatStr, Integer maxCount, String nextUrl) {
        if (since==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'since' query parameter");
        }
        try {
            Date sinceDate = dateFormat.parse(since);
            org.kablink.teaming.domain.BinderChanges binderChanges;
            if (recursive) {
                binderChanges = getBinderModule().searchSubTreeForChanges(binderIds, entryIds, sinceDate, maxCount);
            } else {
                binderChanges = getBinderModule().searchOneLevelForChanges(binderIds[0], sinceDate, maxCount);
            }
            List<BaseBinderChange> changes = new ArrayList<BaseBinderChange>();
            for (BinderChange change : binderChanges.getChanges()) {
                org.kablink.teaming.domain.DefinableEntity definableEntity = null;
                try {
                    if (change.getAction() != BinderChange.Action.delete) {
                        definableEntity = findDefinableEntity(change.getEntityId());
                    }
                } catch (Exception e) {
                    logger.warn("Unable to look up entity: " + change.getEntityId(), e);
                }
                BaseBinderChange binderChange = ResourceUtil.buildBinderChange(change, definableEntity, true, toDomainFormat(descriptionFormatStr));
                if (binderChange!=null) {
                    changes.add(binderChange);
                }
            }
            BinderChanges results = ResourceUtil.buildBinderChanges(binderChanges, changes);
            if (results.getTotal()>results.getCount()) {
                HashMap<String, Object> nextParams = new HashMap<String, Object>();
                nextParams.put("since", dateFormat.format(results.getLastChange()));
                nextParams.put("description_format", descriptionFormatStr);
                nextParams.put("recursive", recursive ? "true" : "false");
                nextParams.put("count", maxCount.toString());
                results.setNext(nextUrl, nextParams);
            }
            return results;
        } catch (ParseException e) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Invalid date in the 'since' query parameter");
        }
    }

    protected boolean isGuestAccessEnabled() {
        return getAuthenticationModule().getAuthenticationConfig().isAllowAnonymousAccess();
    }

    protected static CoreDao getCoreDao() {
        return (CoreDao) SpringContextUtil.getBean("coreDao");
    }

    protected BinderState getBinderState(Long id) {
        BinderState state = (BinderState) getCoreDao().load(BinderState.class, id);
        getCoreDao().evict(state);
        return state;
    }

    protected Folder lookupNetFolder(Long id) {
        Folder folder;
        try {
            Binder binder = getBinderModule().getBinder(id);
            if (!(binder instanceof Folder)) {
                throw new NoFolderByTheIdException(id);
            }
            folder = (Folder) binder;
            if (!folder.isMirrored() || !folder.isTop()) {
                throw new NoFolderByTheIdException(id);
            }
        } catch (NoBinderByTheIdException e) {
            throw new NoFolderByTheIdException(id);
        }
        return folder;
    }

    protected boolean hasChildren(long id, boolean allowJits) {
        Binder binder = _getBinder(id);
        Map resultMap = searchForChildren(binder, null, true, true, allowJits, 0, 1);
        return 0<(Integer)resultMap.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED);
    }

    protected SearchResultList<SearchableObject> getChildren(long id, Criterion filter, String name, boolean binders, boolean entries, boolean files,
                                                             boolean allowJits, Integer offset, Integer maxCount,
                                                          String nextUrl, Map<String, Object> nextParams, int descriptionFormat,
                                                          Date modifiedSince) {
        Binder binder = _getBinder(id);
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>(offset, binder.getModificationDate());

        if (name!=null) {
            if (binder instanceof Folder) {
                if (allowJits) {
                    getFolderModule().jitSynchronize((Folder) binder);
                }
                if (entries || files) {
                    addChildFolderEntriesByName(results, id, name, files);
                }
            }
            if (binders) {
                Long binderId = binder.getId();
                addChildFolderByName(results, binderId, name);
            }
        } else {
            if (offset == null) {
                offset = 0;
            }
            if (maxCount == null) {
                maxCount = -1;
            }
            Map resultMap;
            resultMap = searchForChildren(binder, filter, binders, entries || files, allowJits, offset, maxCount);
            SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(this, descriptionFormat, files), resultMap, nextUrl, nextParams, offset);
            if (modifiedSince != null && results.getLastModified() != null && !modifiedSince.before(results.getLastModified())) {
                throw new NotModifiedException();
            }

            if (results.getCount() == 0 && SPropsUtil.getBoolean("rest.api.log.zero.size.search.results", false)) {
                StringBuilder sb = new StringBuilder("REST response: (")
                        .append(RequestContextHolder.getRequestContext().getClientIdentity())
                        .append(") ")
                        .append("Empty result from getChildren under binder ")
                        .append(id)
                        .append(" using node '")
                        .append(RequestContextHolder.getRequestContext().getLastSearchNodeName())
                        .append("'");
                logger.warn(sb.toString());
            }
        }
        return results;
    }

    private Map searchForChildren(Binder binder, Criterion filter, boolean binders, boolean entries, boolean allowJits, Integer offset, Integer maxCount) {
        Map resultMap;
        if (binder instanceof Folder) {
            resultMap = searchForFolderContents(binder.getId(), binders, entries, allowJits, offset, maxCount);
        } else {
            Criteria crit = new Criteria();
            if (filter != null) {
                crit.add(filter);
            }
            Junction or = Restrictions.disjunction();
            if (binders) {
                or.add(SearchUtils.buildBindersCriterion());
            }
            if (entries) {
                or.add(SearchUtils.buildEntriesCriterion());
            }
            //        if (files) {
            //            or.add(SearchUtils.buildAttachmentsCriterion());
            //        }
            crit.add(or);
            crit.add(SearchUtils.buildParentBinderCriterion(binder.getId()));
            crit.addOrder(new Order(Constants.ENTITY_FIELD, true));
            crit.addOrder(new Order(Constants.SORT_TITLE_FIELD, true));

            resultMap = getBinderModule().searchFolderOneLevelWithInferredAccess(crit, Constants.SEARCH_MODE_PREAPPROVED_PARENTS, offset, maxCount, binder, allowJits);
        }
        return resultMap;
    }

    private void addChildFolderByName(SearchResultList<SearchableObject> results, Long parentBinderId, String name) {
        Binder child = getFolderByName(parentBinderId, name);
        if (child != null) {
            results.append(ResourceUtil.buildBinderBrief(child));
        }
    }

    private void addChildFolderEntriesByName(SearchResultList<SearchableObject> results, Long parentFolderId, String name, boolean addAsFile) {
        FolderModule folderModule = getFolderModule();
        Folder parentFolder = folderModule.getFolder(parentFolderId);
        Set<FolderEntry> folderEntryByTitle = getFolderEntriesByName(parentFolder, name);
        Set<Long> ids = new HashSet<Long>();
        for (FolderEntry entry : folderEntryByTitle) {
            if (!entry.isPreDeleted() && !ids.contains(entry.getId())) {
                if (addAsFile) {
                    results.append(ResourceUtil.buildFileProperties(entry.getPrimaryFileAttachment()));
                } else {
                    results.append(ResourceUtil.buildFolderEntryBrief(entry));
                }
                ids.add(entry.getId());
            }
        }
    }

    protected Binder getFolderByName(Long parentBinderId, String name) {
        Set<String> namesToTry = getNormalizedNames(name);
        for (String nm : namesToTry) {
            Binder binder = getBinderModule().getBinderByParentAndTitle(parentBinderId, nm, true);
            if (binder != null) {
                if (binder instanceof Folder && !((Folder)binder).isPreDeleted()) {
                    return binder;
                }
            }
        }
        return null;
    }

    protected FolderEntry getLibraryFolderEntryByName(Folder parentFolder, String fileName) {
        Set<String> namesToTry = getNormalizedNames(fileName);
        for (String nm : namesToTry) {
            FolderEntry entry = getFolderModule().getLibraryFolderEntryByFileName(parentFolder, nm);
            if (entry != null) {
                if (!entry.isPreDeleted()) {
                    return entry;
                }
            }
        }
        return null;
    }

    protected Set<FolderEntry> getFolderEntriesByName(Folder parentFolder, String name) {
        FolderModule folderModule = getFolderModule();
        Set<FolderEntry> allEntries = new HashSet<FolderEntry>();
        Set<String> namesToTry = getNormalizedNames(name);
        for (String nm : namesToTry) {
            allEntries.addAll(folderModule.getFolderEntryByTitle(parentFolder.getId(), nm));
            FolderEntry entry = folderModule.getLibraryFolderEntryByFileName(parentFolder, nm);
            if (entry != null) {
                allEntries.add(entry);
            }
        }
        return allEntries;
    }

    protected FileAttachment findFileAttachmentByName(org.kablink.teaming.domain.DefinableEntity entity, String name) {
        Set<String> normalizedNames = getNormalizedNames(name);
        for (String nm : normalizedNames) {
            FileAttachment fa = entity.getFileAttachment(nm);
            if (fa != null) {
                return fa;
            }
        }
        return null;
    }

    protected Set<String> getNormalizedNames(String name) {
        Set<String> namesToTry = new HashSet<String>();
        namesToTry.add(Normalizer.normalize(name, Normalizer.Form.NFC));
        namesToTry.add(Normalizer.normalize(name, Normalizer.Form.NFD));
        return namesToTry;
    }

    private Map searchForFolderContents(long folderId, boolean includeSubFolders, boolean includeEntries, boolean allowJits, int offset, int maxCount) {
        Map options = new HashMap();
        options.put(ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS, includeSubFolders);
        options.put(ObjectKeys.SEARCH_INCLUDE_NESTED_ENTRIES, includeEntries);
        options.put(ObjectKeys.SEARCH_SORT_BY,                Constants.ENTITY_FIELD);
        options.put(ObjectKeys.SEARCH_SORT_DESCEND,           Boolean.FALSE);
        options.put(ObjectKeys.SEARCH_SORT_BY_SECONDARY,      Constants.SORT_TITLE_FIELD);
        options.put(ObjectKeys.SEARCH_SORT_DESCEND_SECONDARY, Boolean.FALSE);
        options.put(ObjectKeys.SEARCH_OFFSET, offset);
        options.put(ObjectKeys.SEARCH_MAX_HITS, maxCount);
        options.put(ObjectKeys.SEARCH_ALLOW_JITS, allowJits);

        return getFolderModule().getEntries(folderId, options);
    }


    protected Binder _getBinder(long id) {
        return _getBinderImpl(id);
    }

    protected Binder _getBinderImpl(long id) {
        try{
            return getBinderModule().getBinder(id, false, true);
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.BINDER_NOT_FOUND, "NOT FOUND");
    }

    protected SearchResultList<SearchableObject> _getMyFilesLibraryChildren(String name, Date ifModifiedSince, boolean folders, boolean entries, boolean files, boolean allowJits,
                                                                          int descriptionFormat, Integer offset, Integer maxCount, String nextUrl) {
        User user = getLoggedInUser();
        if (!SearchUtils.userCanAccessMyFiles(this, user)) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }

        Date lastModified = getMyFilesLibraryModifiedDate(false, allowJits);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        Map<String, Object> nextParams = new HashMap<String, Object>();
        if (descriptionFormat==Description.FORMAT_HTML) {
            nextParams.put("description_format", "html");
        } else {
            nextParams.put("description_format", "text");
        }
        SearchResultList<SearchableObject> results = null;
        if (SearchUtils.useHomeAsMyFiles(this, user)) {
            Long homeId = SearchUtils.getHomeFolderId(this, user);
            if (homeId!=null) {
                // If we are listing the home folder, use this API because it could trigger JITS.
                results = getChildren(homeId, SearchUtils.buildLibraryCriterion(true, Boolean.FALSE), name, folders, entries, files, allowJits,
                                      offset, maxCount, nextUrl, nextParams, descriptionFormat, ifModifiedSince);
                results.setLastModified(lastModified);
            }
        }
        if (results==null) {
            if (name!=null) {
                results = new SearchResultList<SearchableObject>();
                if (entries||files) {
                    Long myFilesFolderId = SearchUtils.getMyFilesFolderId(this, user, false);
                    if (myFilesFolderId!=null) {
                        addChildFolderEntriesByName(results, myFilesFolderId, name, files);
                    }
                }
                if (folders) {
                    addChildFolderByName(results, user.getWorkspaceId(), name);
                }
            } else {
                // In all other cases, this code will search across the various My Files locations.  JITS is irrelevant.
                Criteria crit = SearchUtils.getMyFilesSearchCriteria(this, user.getWorkspaceId(), folders, entries, false, files);
                results = lookUpChildren(crit, descriptionFormat, offset, maxCount, nextUrl, nextParams, lastModified);
            }
        }
        setMyFilesParents(results);
        results.updateLastModified(AdminHelper.getEffectiveAdhocFolderSettingDate(this, user));
        return results;
    }

    protected void setMyFilesParents(SearchResultList results) {
        List<Long> hiddenFolderIds = getEffectiveMyFilesFolderIds();
        Set<Long> allParentIds = new HashSet<Long>(hiddenFolderIds);
        allParentIds.add(getLoggedInUser().getWorkspaceId());
        ParentBinder parent = new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files");
        for (Object obj : results.getResults()) {
            if (obj instanceof FileProperties && allParentIds.contains(((FileProperties)obj).getBinder().getId())) {
                ((FileProperties)obj).setBinder(parent);
            } else if (obj instanceof DefinableEntity && allParentIds.contains(((DefinableEntity)obj).getParentBinder().getId())) {
                ((DefinableEntity)obj).setParentBinder(parent);
            } else if (obj instanceof DefinableEntityBrief && allParentIds.contains(((DefinableEntityBrief)obj).getParentBinder().getId())) {
                ((DefinableEntityBrief)obj).setParentBinder(parent);
            } else if (obj instanceof org.kablink.teaming.rest.v1.model.BinderChange) {
                org.kablink.teaming.rest.v1.model.BinderChange binderChange = (org.kablink.teaming.rest.v1.model.BinderChange) obj;
                if (allParentIds.contains(binderChange.getId()) &&
                        BinderChange.Action.modify.name().equals(binderChange.getAction())) {
                    BinderBrief fakeMyFileFolders = getFakeMyFileFolders(false);
                    binderChange.setId(fakeMyFileFolders.getId());
                    binderChange.setBinder(fakeMyFileFolders.asBinder());
                } else {
                    org.kablink.teaming.rest.v1.model.Binder binder = ((org.kablink.teaming.rest.v1.model.BinderChange)obj).getBinder();
                    if (binder!=null && allParentIds.contains(binder.getParentBinder().getId())) {
                        binder.setParentBinder(parent);
                    }
                }
            } else if (obj instanceof FileChange) {
                FileProperties file = ((FileChange)obj).getFile();
                if (file!=null && allParentIds.contains(file.getBinder().getId())) {
                    file.setBinder(parent);
                }
            } else if (obj instanceof FolderEntryChange) {
                org.kablink.teaming.rest.v1.model.FolderEntry entry = ((FolderEntryChange)obj).getEntry();
                if (entry!=null && allParentIds.contains(entry.getParentBinder().getId())) {
                    entry.setParentBinder(parent);
                }
            }
        }
    }

    protected List<Long> getEffectiveMyFilesFolderIds() {
        User user = getLoggedInUser();
        if (SearchUtils.useHomeAsMyFiles(this, user)) {
            return SearchUtils.getHomeFolderIds(this, user);
        } else {
        	List<Long> reply = new ArrayList<Long>();
        	Long mfId = SearchUtils.getMyFilesFolderId(this, user, false);
        	if (null != mfId) {
        		reply.add(mfId);
        	}
        	return reply;
        }
    }

    protected List<SearchableObject> getSharedByChildren(List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> shareItems, String name, boolean onlyLibrary,
                                                         boolean replaceParent, boolean showHidden, boolean showUnhidden)  {
        if (replaceParent) {
            return _getSharedEntities(shareItems, ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", name, onlyLibrary, showHidden,
                    showUnhidden, false, true, false, true);
        }
        return _getSharedEntities(shareItems, null, null, name, onlyLibrary, showHidden, showUnhidden, false, true, false, true);
    }

    protected List<SearchableObject> getSharedWithChildren(List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> shareItems, String name, boolean onlyLibrary, boolean replaceParent, boolean showHidden, boolean showUnhidden)  {
        if (replaceParent) {
            return _getSharedEntities(shareItems, ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", name, onlyLibrary, showHidden,
                    showUnhidden, true, true, false, true);
        }
        return _getSharedEntities(shareItems, null, null, name, onlyLibrary, showHidden, showUnhidden, true, true, false, true);
    }

    protected List<SearchableObject> getPublicChildren(List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> shareItems, String name, boolean onlyLibrary, boolean replaceParent,
                                                       boolean showHidden, boolean showUnhidden)  {
        if (replaceParent) {
            return _getSharedEntities(shareItems, ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", name, onlyLibrary, showHidden, showUnhidden, true, true, false, true);
        }
        return _getSharedEntities(shareItems, null, null, name, onlyLibrary, showHidden, showUnhidden, true, true, false, true);
    }

    protected List<SearchableObject> _getSharedEntities(List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> shareItems,
                                                        Long topId, String topHref, String name, boolean onlyLibrary,
                                                        boolean showHidden, boolean showUnhidden, boolean resolveDuplicateNames,
                                                        boolean folders, boolean entries, boolean files)  {
        boolean guestEnabled = isGuestAccessEnabled();

        List<Pair<org.kablink.teaming.domain.DefinableEntity, List<ShareItem>>> resultList = _getSharedItems(shareItems, topId, null, onlyLibrary,
                showHidden, showUnhidden, topId==ObjectKeys.SHARED_BY_ME_ID, false, folders, entries || files);

        List<SearchableObject> results = new ArrayList<SearchableObject>();
        for (Pair<org.kablink.teaming.domain.DefinableEntity, List<ShareItem>> entityShares : resultList) {
            try {
                org.kablink.teaming.domain.DefinableEntity entity = entityShares.getA();
                List<ShareItem> shares = entityShares.getB();
                if (entity instanceof org.kablink.teaming.domain.FolderEntry) {
                    if (entries) {
                        SharedFolderEntryBrief entryBrief = null;
                        for (ShareItem shareItem : shares) {
                            if (entryBrief!=null) {
                                entryBrief.addShare(ResourceUtil.buildShare(shareItem, entity, buildShareRecipient(shareItem), guestEnabled));
                            } else {
                                entryBrief = ResourceUtil.buildSharedFolderEntryBrief(shareItem, buildShareRecipient(shareItem), (FolderEntry) entity, guestEnabled);
                                if (topId!=null) {
                                    entryBrief.setParentBinder(new ParentBinder(topId, topHref));
                                }
                                results.add(entryBrief);
                            }
                        }
                    }
                    if (files) {
                        Set<Attachment> attachments = entity.getAttachments();
                        for (Attachment attachment : attachments) {
                            if (attachment instanceof FileAttachment) {
                                SharedFileProperties fileProps = null;
                                for (ShareItem shareItem : shares) {
                                    if (fileProps!=null) {
                                        fileProps.addShare(ResourceUtil.buildShare(shareItem, entity, buildShareRecipient(shareItem), guestEnabled));
                                    } else {
                                        fileProps = ResourceUtil.buildSharedFileProperties(shareItem, buildShareRecipient(shareItem), (FileAttachment) attachment, guestEnabled);
                                        fileProps.setBinder(new ParentBinder(topId, topHref));
                                        results.add(fileProps);
                                    }
                                }
                            }
                        }
                    }
                } else if (entity instanceof org.kablink.teaming.domain.Binder) {
                    SharedBinderBrief binderBrief = null;
                    for (ShareItem shareItem : shares) {
                        if (binderBrief!=null) {
                            binderBrief.addShare(ResourceUtil.buildShare(shareItem, entity, buildShareRecipient(shareItem), guestEnabled));
                        } else {
                            binderBrief = ResourceUtil.buildSharedBinderBrief(shareItem, buildShareRecipient(shareItem), (Binder) entity, guestEnabled);
                            if (topId!=null) {
                                binderBrief.setParentBinder(new ParentBinder(topId, topHref));
                            }
                            results.add(binderBrief);
                        }
                    }
                }
            } catch (AccessControlException e) {
                logger.warn("User " + getLoggedInUserId() + " does not have permission to read an entity that was shared with him/her: " + entityShares.getA().getEntityTypedId());
            }
        }

        if (resolveDuplicateNames) {
            findAndResolveDuplicateNames(results);
        }

        if (name!=null) {
            Set<String> namesToTry = getNormalizedNames(name);
            List<SearchableObject> newResults = new ArrayList<SearchableObject>();
            for (SearchableObject result : results) {
                for (String nm : namesToTry) {
                    if (result.getDisplayName().equalsIgnoreCase(nm)) {
                        newResults.add(result);
                    }
                }
            }
            results = newResults;
        }

        Collections.sort(results, new Comparator<SearchableObject>() {
            @Override
            public int compare(SearchableObject o1, SearchableObject o2) {
                int result = o1.getDocType().compareTo(o2.getDocType());
                if (result==0) {
                    result = o1.getDisplayName().compareTo(o2.getDisplayName());
                }
                return result;
            }
        });
        return results;
    }

    protected List<Pair<org.kablink.teaming.domain.DefinableEntity, List<ShareItem>>> _getSharedItems(
            List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> shareItems, Long topId, String name,
            boolean onlyLibrary, boolean showHidden, boolean showUnhidden, boolean showExpired, boolean showDeleted,
            boolean folders, boolean entries)  {
        Map<String, Pair<org.kablink.teaming.domain.DefinableEntity, List<ShareItem>>> resultMap =
                new LinkedHashMap<String, Pair<org.kablink.teaming.domain.DefinableEntity, List<ShareItem>>>();

        for (Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity> pair : shareItems) {
            ShareItem shareItem = pair.getA();
            if (shareItem.isDeleted() && !showDeleted) {
                // Ignore this share
            } else if (shareItem.isExpired() && !showExpired) {
                // Ignore this share
            } else {
                Pair<org.kablink.teaming.domain.DefinableEntity, List<ShareItem>> sharesByEntity = resultMap.get(shareItem.getSharedEntityIdentifier().toString());
                try {
                    if (entries && shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                        FolderEntry entry;
                        if (sharesByEntity!=null) {
                            entry = (FolderEntry) sharesByEntity.getA();
                        } else {
                            entry = (FolderEntry) getDefinableEntity(pair, !showExpired && !showDeleted);
                        }
                        if (showEntryToUser(entry, topId, name, showHidden, showUnhidden, showDeleted)) {
                            if (sharesByEntity==null) {
                                sharesByEntity = new Pair<org.kablink.teaming.domain.DefinableEntity, List<ShareItem>>(entry, new ArrayList<ShareItem>());
                                resultMap.put(entry.getEntityIdentifier().toString(), sharesByEntity);
                            }
                            sharesByEntity.getB().add(shareItem);
                        }
                    } else if (folders && shareItem.getSharedEntityIdentifier().getEntityType().isBinder()) {
                        Binder binder;
                        if (sharesByEntity!=null) {
                            binder = (Binder) sharesByEntity.getA();
                        } else {
                            binder = (Binder) getDefinableEntity(pair, !showExpired && !showDeleted);
                        }
                        if (showBinderToUser(binder, name, onlyLibrary, topId, showHidden, showUnhidden, showDeleted)) {
                            if (sharesByEntity==null) {
                                sharesByEntity = new Pair<org.kablink.teaming.domain.DefinableEntity, List<ShareItem>>(binder, new ArrayList<ShareItem>());
                                resultMap.put(binder.getEntityIdentifier().toString(), sharesByEntity);
                            }
                            sharesByEntity.getB().add(shareItem);
                        }
                    }
                } catch (AccessControlException e) {
                    logger.warn("User " + getLoggedInUserId() + " does not have permission to read an entity that was shared with him/her: " + shareItem.getEntityTypedId());
                }
            }
        }
        List<Pair<org.kablink.teaming.domain.DefinableEntity, List<ShareItem>>> results = new ArrayList<Pair<org.kablink.teaming.domain.DefinableEntity, List<ShareItem>>>();
        results.addAll(resultMap.values());
        return results;
    }

    protected boolean showBinderToUser(Binder binder, String name, boolean onlyLibrary, Long collectionId, boolean showHidden, boolean showUnhidden, boolean showDeleted) {
        if (binder==null) {
            return false;
        }
        if (name!=null && !binder.getTitle().equals(name)) {
            return false;
        }
        if ((!showDeleted && isBinderPreDeleted(binder)) || (onlyLibrary && binder.getEntityType() != EntityIdentifier.EntityType.workspace && !binder.isLibrary())) {
            return false;
        }
        if (showHidden && showUnhidden) {
            return true;
        }
        if (!showHidden && !showUnhidden) {
            return false;
        }
        SearchResultList<Tag> entryTags = getBinderTags(binder, true);
        for (Tag tag : entryTags.getResults()) {
            if ((collectionId == ObjectKeys.SHARED_WITH_ME_ID && isHiddenInSharedWithMe(tag)) ||
                    (collectionId == ObjectKeys.SHARED_BY_ME_ID && isHiddenInSharedByMe(tag))) {
                return showHidden;
            }
        }
        return showUnhidden;
    }

    protected boolean showEntryToUser(FolderEntry entry, Long collectionId, String name, boolean showHidden, boolean showUnhidden, boolean showDeleted) {
        if (entry==null) {
            return false;
        }
        if (name!=null) {
            boolean nameMatches = false;
            if (entry.getTitle().equals(name)) {
                nameMatches = true;
            } else {
                FileAttachment att = entry.getPrimaryFileAttachment();
                if (att!=null && att.getFileItem().getName().equals(name)) {
                    nameMatches = true;
                }
            }
            if (!nameMatches) {
                return false;
            }
        }
        if (!showDeleted && _isPreDeleted(entry)) {
            return false;
        }
        if (showHidden && showUnhidden) {
            return true;
        }
        if (!showHidden && !showUnhidden) {
            return false;
        }
        SearchResultList<Tag> entryTags = getEntryTags(entry, true);
        for (Tag tag : entryTags.getResults()) {
            if ((collectionId == ObjectKeys.SHARED_WITH_ME_ID && isHiddenInSharedWithMe(tag)) ||
                    (collectionId == ObjectKeys.SHARED_BY_ME_ID && isHiddenInSharedByMe(tag))) {
                return showHidden;
            }
        }
        return showUnhidden;
    }

    protected SearchResultList<NetFolderBrief> _getNetFolders(String name, int descriptionFormat, int offset, int maxCount, String nextUrl, Map<String, Object> nextParams) {
        Map map = SearchUtils.searchForNetFolders(this, null, new HashMap());
        Boolean partialListDueToError = (Boolean) map.get(ObjectKeys.SEARCH_PARTIAL_LIST_DUE_TO_ERROR);
        SearchResultList<NetFolderBrief> results = new SearchResultList<NetFolderBrief>();
        NetFolderBriefBuilder builder = new NetFolderBriefBuilder(descriptionFormat);
        if (name!=null) {
            List<Map> entries = (List<Map>)map.get(ObjectKeys.SEARCH_ENTRIES);
            if (entries!=null) {
                for (Map entry : entries) {
                    String title = (String) entry.get(Constants.TITLE_FIELD);
                    if (name.equals(title)) {
                        results.append(builder.build(entry));
                    }
                }
            }
        } else {
            SearchResultBuilderUtil.buildSearchResults(results, builder, map, nextUrl, nextParams, offset);
        }
        List<Long> ids = new ArrayList<Long>(results.getCount());
        for (NetFolderBrief binder : results.getResults()) {
            ids.add(binder.getId());
        }

        Map<Long, AppNetFolderSyncSettings> syncSettings = NetFolderUtil.getAppNetFolderSyncSettings(ids);

        for (NetFolderBrief binder : results.getResults()) {
            AppNetFolderSyncSettings settings = syncSettings.get(binder.getId());
            if (settings!=null) {
                binder.setAllowDesktopSync(settings.getAllowDesktopAppToSyncData());
                binder.setAllowMobileSync(settings.getAllowMobileAppsToSyncData());
            }
            binder.setParentBinder(new ParentBinder(ObjectKeys.NET_FOLDERS_ID, "/self/net_folders"));
        }
        return results;
    }

    protected List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> getAllPublicShareParts(ShareItem item) {
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setSharerId(item.getSharerId());
        spec.setLatest(true);
        spec.setSharedEntityIdentifier(item.getSharedEntityIdentifier());
        List<Pair<ShareItem, org.kablink.teaming.domain.DefinableEntity>> allShareItems = getShareItems(spec, null, false, true, false, false);
        if (allShareItems.size()==0) {
            throw new IllegalStateException("Could not find public shares corresponding to share with id: " + item.getId());
        }
        return allShareItems;
    }

    private void findAndResolveDuplicateNames(List<SearchableObject> results) {
        Map<String, SearchableObject> uniqueMap = new HashMap<String, SearchableObject>();
        Map<String, List<SearchableObject>> duplicateMap = new HashMap<String, List<SearchableObject>>();

        for (SearchableObject result : results) {
            addResult(result, uniqueMap, duplicateMap);
        }

        Set<String> uniqueNames = new HashSet<String>();
        uniqueNames.addAll(uniqueMap.keySet());
        for (List<SearchableObject> duplicates : duplicateMap.values()) {
            resolveDuplicateNames(duplicates, uniqueNames);
        }
    }

    private void resolveDuplicateNames(List<SearchableObject> initialDuplicates, Set<String> uniqueNames) {
        // First pass...add the sharer to the name of the file or folder
        Map<String, SearchableObject> uniqueMap = new HashMap<String, SearchableObject>();
        Map<String, List<SearchableObject>> intermediateDuplicateMap1 = new HashMap<String, List<SearchableObject>>();

        for (SearchableObject result : initialDuplicates) {
            if (result instanceof SharedSearchableObject) {
                List<Share> shares = ((SharedSearchableObject)result).getShares();
                result.setDisplayName(buildName(result, shares));
            }
            addResult(result, uniqueMap, intermediateDuplicateMap1);
        }

        uniqueNames.addAll(uniqueMap.keySet());

        // Second pass...add the share date to the name of the file or folder
        uniqueMap = new HashMap<String, SearchableObject>();
        Map<String, List<SearchableObject>> intermediateDuplicateMap2 = new HashMap<String, List<SearchableObject>>();
        for (List<SearchableObject> intermediateDuplicates : intermediateDuplicateMap1.values()) {
            for (SearchableObject result : intermediateDuplicates) {
                if (result instanceof SharedSearchableObject) {
                    List<Share> shares = ((SharedSearchableObject) result).getShares();
                    result.setDisplayName(insertShareDate(result, shares));
                }
                addResult(result, uniqueMap, intermediateDuplicateMap2);
            }
        }

        uniqueNames.addAll(uniqueMap.keySet());


        for (List<SearchableObject> finalDuplicates : intermediateDuplicateMap2.values()) {
            Collections.sort(finalDuplicates, new Comparator<SearchableObject>() {
                @Override
                public int compare(SearchableObject o1, SearchableObject o2) {
                    return o1.getCreateDate().compareTo(o2.getCreateDate());
                }
            });

            int i=1;
            for (SearchableObject duplicate : finalDuplicates) {
                Pair<String, String> parts = splitFileName(duplicate);
                String uniqueName = parts.getA() + " (" + i + ")" + parts.getB();
                while (uniqueNames.contains(uniqueName)) {
                    i++;
                    uniqueName = parts.getA() + " (" + i + ")" + parts.getB();
                }
                duplicate.setDisplayName(uniqueName);
                uniqueNames.add(uniqueName);
            }
        }
    }

    private void addResult(SearchableObject result,  Map<String, SearchableObject> uniqueMap,Map<String, List<SearchableObject>> duplicateMap) {
        String name = result.getDisplayName().toLowerCase();
        if (duplicateMap.containsKey(name)) {
            duplicateMap.get(name).add(result);
        } else if (uniqueMap.containsKey(name)) {
            List<SearchableObject> duplicates = new ArrayList<SearchableObject>();
            duplicates.add(uniqueMap.remove(name));
            duplicates.add(result);
            duplicateMap.put(name, duplicates);
        } else {
            uniqueMap.put(name, result);
        }

    }

    private String buildName(SearchableObject obj, List<Share> shares) {
        Collections.sort(shares, new Comparator<Share>() {
            @Override
            public int compare(Share o1, Share o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });

        return buildName(obj, shares.get(0));
    }

    private String buildName(SearchableObject obj, Share share) {
        Pair<String, String> parts = splitFileName(obj);
        Principal sharer = getProfileModule().getEntry(share.getSharer().getId());
        return parts.getA() + " (" + sharer.getTitle() + ")" + parts.getB();
    }

    private String insertShareDate(SearchableObject obj, List<Share> shares) {
        Collections.sort(shares, new Comparator<Share>() {
            @Override
            public int compare(Share o1, Share o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });

        return insertShareDate(obj, shares.get(0));
    }

    private String insertShareDate(SearchableObject obj, Share share) {
        Pair<String, String> parts = splitFileName(obj);

        Date startDate = share.getStartDate();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String base = parts.getA();
        if (base.endsWith(")")) {
            base = base.substring(0, base.length() - 1) + " " + formatter.format(startDate) + ")";
        } else {
            base = base + " " + formatter.format(startDate);
        }
        return base + parts.getB();
    }

    private Pair<String, String> splitFileName(SearchableObject obj) {
        String basename = "";
        String ext = "";
        if (obj instanceof FileProperties) {
            String filename = ((FileProperties) obj).getName();
            basename = FilenameUtils.getBaseName(filename);
            ext = FilenameUtils.getExtension(filename);
            if (!ext.isEmpty()) {
                ext = "." + ext;
            }
        } else if (obj instanceof DefinableEntityBrief) {
            basename = ((DefinableEntityBrief) obj).getTitle();
        } else if (obj instanceof DefinableEntity) {
            basename = ((DefinableEntity) obj).getTitle();
        } else {
            basename = obj.getDisplayName();
        }

        return new Pair<String, String>(basename, ext);
    }
}
