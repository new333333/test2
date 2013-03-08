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
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.UnsupportedMediaTypeException;
import org.kablink.teaming.remoting.rest.v1.util.*;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.rest.v1.model.DefinableEntity;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.util.EmailHelper;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.HttpHeaders;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public abstract class AbstractResource extends AbstractAllModulesInjected {

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
        Long folderId = SearchUtils.getMyFilesFolderId(this, getLoggedInUser().getWorkspaceId(), true);
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
            throw new IllegalArgumentException(userId + " does not represent an user. It is " + entry.getClass().getSimpleName());
        return (org.kablink.teaming.domain.User) entry;
    }

    protected SearchResultList<SearchableObject> searchForLibraryEntities(String keyword, Criterion searchContext, boolean recursive, Integer offset, Integer maxCount, boolean includeBinders, boolean includeFolderEntries, boolean includeReplies, boolean includeFiles, boolean includeParentPaths, boolean textDescriptions, String nextUrl) {
        keyword = SearchUtils.validateSearchText(keyword);
        Criteria crit = new Criteria();
        crit.add(buildDocTypeCriterion(includeBinders, includeFolderEntries, includeFiles, includeReplies));
        crit.add(buildLibraryCriterion(true));
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
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
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
        SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(textDescriptions), resultsMap,
                nextUrl, nextParams, offset);
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    protected SearchResultList<RecentActivityEntry> _getRecentActivity(boolean includeParentPaths, boolean textDescriptions,
                                                                    Integer offset, Integer maxCount, Criteria criteria,
                                                                    String nextUrl, Map<String, Object> nextParams) {
        Map resultsMap = getBinderModule().executeSearchQuery(criteria, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<RecentActivityEntry> results = new SearchResultList<RecentActivityEntry>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new RecentActivityFolderEntryBuilder(textDescriptions), resultsMap,
                nextUrl, nextParams, offset);

        populateComments(results.getResults(), textDescriptions);

        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }

        return results;
    }

    protected void populateComments(List<RecentActivityEntry> entries, boolean textDescriptions) {
        String[] topEntryIds = new String[entries.size()];
        int i = 0;
        for (RecentActivityEntry entry:  entries) {
            // ...tracking each ASEntryData's entry ID.
            Long topEntryId = entry.getId();
            topEntryIds[i++] = String.valueOf(topEntryId);
        }

        // Are there any comments posted to any of these entries?
        Criteria searchCriteria = SearchUtils.entryReplies(topEntryIds, true);	// true -> All replies, at any level.
        Map       searchResults = getBinderModule().executeSearchQuery(searchCriteria, Constants.SEARCH_MODE_NORMAL, 0, (Integer.MAX_VALUE - 1));
        List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
        int       totalRecords  = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
        if ((0 >= totalRecords) || (null == searchEntries) || searchEntries.isEmpty()) {
            // No!  Then there's no comment data to complete.
            return;
        }

        ReplyBriefBuilder replyBuilder = new ReplyBriefBuilder(textDescriptions);

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
   			return DocumentHelper.parseText(xml);
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

    protected Share shareEntity(org.kablink.teaming.domain.DefinableEntity entity, Share share, boolean notifyRecipient) {
        share.setSharedEntity(new EntityId(entity.getId(), entity.getEntityType().name(), null));
        ShareItem item = toShareItem(share);
        ShareItem existing = findExistingShare(getLoggedInUserId(), entity.getEntityIdentifier(), item.getRecipientId(), item.getRecipientType());
        if (existing!=null) {
            getSharingModule().modifyShareItem(item, existing.getId());
        } else {
            getSharingModule().addShareItem(item);
        }
        if (notifyRecipient) {
            try {
                EmailHelper.sendEmailToRecipient(this, item, false, getLoggedInUser());
            } catch (Exception e) {
                logger.warn("Failed to send share notification email", e);
            }
        }
        return ResourceUtil.buildShare(item);
    }

    protected ShareItem findExistingShare(Long sharer, EntityIdentifier sharedEntity, Long recipientId, ShareItem.RecipientType recipientType) {
        ShareItemSelectSpec spec = getSharedBySpec(sharer);
        spec.setSharedEntityIdentifier(sharedEntity);
        if (recipientType== ShareItem.RecipientType.user) {
            spec.setRecipients(recipientId, null, null);
        } else if (recipientType== ShareItem.RecipientType.group) {
            spec.setRecipients(null, recipientId, null);
        } else {
            spec.setRecipients(null, null, recipientId);
        }

        List<ShareItem> shares = getShareItems(spec, true);
        if (shares.size()==0) {
            return null;
        }
        return shares.get(0);
    }

    protected List<ShareItem> getShareItems(ShareItemSelectSpec spec, boolean includeExpired) {
        return getShareItems(spec, null, includeExpired);
    }

    protected List<ShareItem> getShareItems(ShareItemSelectSpec spec, Long excludedSharer, boolean includeExpired) {
        List<ShareItem> shareItems = getSharingModule().getShareItems(spec);
        List<ShareItem> filteredItems = new ArrayList<ShareItem>(shareItems.size());
        for (ShareItem item : shareItems) {
            if ((!item.isExpired() || includeExpired) && item.isLatest() &&
                    (excludedSharer==null || !excludedSharer.equals(item.getSharerId()))) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
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

    protected Criterion buildLibraryTreeCriterion() {
        Junction criteria = Restrictions.disjunction();
        criteria.add(SearchUtils.libraryFolders());
        criteria.add(buildWorkspacesCriterion());
        return criteria;
    }

    protected Criterion buildDocTypeCriterion(boolean includeBinders, boolean includeFolderEntries, boolean includeFiles, boolean includeReplies) {
        Junction types = Restrictions.disjunction();
        // Include a restriction that will always evaluate to false.  That way if all of the include* parameters are false
        // no results will be returned (instead of all results being returned)
        types.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, "_fake_"));
        if (includeBinders) {
            types.add(buildBindersCriterion());
        }
        if (includeFiles) {
            types.add(buildAttachmentsCriterion());
        }
        if (includeFolderEntries) {
            types.add(buildEntriesCriterion());
        }
        if (includeReplies) {
            types.add(buildRepliesCriterion());
        }
        return types;
    }

    protected Criterion buildEntryCriterion(Long id) {
        return Restrictions.conjunction()
        			.add(buildEntriesAndRepliesCriterion())
                    .add(Restrictions.disjunction()
                            .add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()))
                            .add(Restrictions.eq(Constants.ENTRY_TOP_ENTRY_ID_FIELD, id.toString())));
    }

    protected Criterion buildAttachmentsCriterion() {
        return Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ATTACHMENT);
    }

    protected Criterion buildAttachmentCriterion(Long entryId) {
        return Restrictions.conjunction()
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ATTACHMENT))
                .add(Restrictions.eq(Constants.ENTRY_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
                .add(Restrictions.eq(Constants.DOCID_FIELD, entryId.toString()));
    }

    protected Criterion buildEntriesAndRepliesCriterion() {
        return Restrictions.conjunction()
        			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
        			.add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[]{Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY}));
    }

    protected Criterion buildEntriesCriterion() {
        return Restrictions.conjunction()
        			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
        			.add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[]{Constants.ENTRY_TYPE_ENTRY}));
    }

    protected Criterion buildRepliesCriterion() {
        return Restrictions.conjunction()
        			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
        			.add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[]{Constants.ENTRY_TYPE_REPLY}));
    }

    protected Criterion buildFoldersCriterion() {
        return Restrictions.conjunction()
        			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER))
        			.add(Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_FOLDER));
    }

    protected Criterion buildWorkspacesCriterion() {
        return Restrictions.conjunction()
        			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER))
        			.add(Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_WORKSPACE));
    }

    protected Criterion buildBinderCriterion(Long id) {
        return Restrictions.conjunction()
        			.add(buildBindersCriterion())
                    .add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
    }

    protected Criterion buildUsersCriterion() {
        return Restrictions.conjunction()
                .add(Restrictions.eq(Constants.PERSONFLAG_FIELD, Boolean.TRUE.toString()))
                .add(Restrictions.eq(Constants.IDENTITY_INTERNAL_FIELD, Boolean.TRUE.toString()))
                .add(Restrictions.eq(Constants.DISABLED_USER_FIELD, Boolean.FALSE.toString()))
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
                .add(Restrictions.eq(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_USER));
    }

    protected Criterion buildBindersCriterion() {
        return Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER);
    }

    protected Criterion buildAncentryCriterion(Long id) {
        return Restrictions.eq(Constants.ENTRY_ANCESTRY, id.toString());
    }

    protected Criterion buildParentBinderCriterion(Long id) {
        return  Restrictions.disjunction()
                .add(Restrictions.eq(Constants.BINDER_ID_FIELD, id.toString()))
                .add(Restrictions.eq(Constants.BINDERS_PARENT_ID_FIELD, id.toString()));
    }

    protected Criterion buildSearchBinderCriterion(Long id, boolean recursive) {
        if (recursive) {
            return buildAncentryCriterion(id);
        } else {
            return buildParentBinderCriterion(id);
        }
    }

    protected Criterion buildLibraryCriterion(Boolean onlyLibrary) {
        return Restrictions.eq(Constants.IS_LIBRARY_FIELD, ((Boolean) onlyLibrary).toString());
    }

    protected Criterion buildFileNameCriterion(String fileName) {
        return Restrictions.like(Constants.FILENAME_FIELD, fileName);
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
        EntityId recipient = share.getRecipient();
        if (recipient==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'recipient' value.");
        }
        if (recipient.getId()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'recipient.id' value.");
        }
        if (recipient.getType()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'recipient.type' value.");
        }
        Access access = share.getAccess();
        if (access==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'access' value.");
        }
        if (access.getRole()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'access.role' value.");
        }

        EntityIdentifier.EntityType entityType;
        try {
            entityType = EntityIdentifier.EntityType.valueOf(sharedEntity.getType());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The shared_entity.type value must be one of the following: folder, workspace, folderEntry");
        }
        EntityIdentifier entity = new EntityIdentifier(sharedEntity.getId(), entityType);
        ShareItem.RecipientType recType;
        try {
            recType = ShareItem.RecipientType.valueOf(recipient.getType());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The recipient.type value must be one of the following: user, group, team");
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
            if (rights.isAllowSharing() || rights.isAllowSharingExternal() || rights.isAllowSharingPublic()) {
                rights.setAllowSharingForward(true);
            }
        }
        ShareItem shareItem = new ShareItem(getLoggedInUserId(), entity, share.getComment(), share.getEndDate(), recType, recipient.getId(), rights);
        if (share.getDaysToExpire()!=null) {
            shareItem.setDaysToExpire(share.getDaysToExpire());
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

    protected SearchResultList<BinderBrief> lookUpBinders(Criteria crit, boolean textDescriptions, Integer offset, Integer maxCount, String nextUrl, Map<String, Object> nextParams, Date lastModified) {
        if (offset==null) {
            offset = 0;
        }
        if (maxCount==null) {
            maxCount = -1;
        }
        crit.add(buildBindersCriterion());
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        results.setLastModified(lastModified);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(textDescriptions), resultMap, nextUrl, nextParams, offset);
        return results;
    }

    protected SearchResultList<FolderEntryBrief> lookUpEntries(Criteria crit, Integer offset, Integer maxCount, String nextUrl, Map<String, Object> nextParams) {
        if (offset==null) {
            offset = 0;
        }
        if (maxCount==null) {
            maxCount = -1;
        }
        crit.add(buildEntriesCriterion());
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
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
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>(offset);
        results.setLastModified(lastModified);
        SearchResultBuilderUtil.buildSearchResults(results, new FilePropertiesBuilder(), resultMap, nextUrl, nextParams, offset);
        return results;
    }

    protected Map searchForBinders(Criterion criterion) {
        Junction outerCriterion = Restrictions.conjunction()
                .add(buildBindersCriterion()).add(criterion);
        Criteria crit = new Criteria();
        crit.add(outerCriterion);
        return getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, -1);
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

    protected Date max(Date d1, Date d2) {
        if (d1==null) {
            return d2;
        }
        if (d2==null) {
            return d1;
        }
        if (d1.compareTo(d2)>=0) {
            return d1;
        } else {
            return d2;
        }
    }

    protected Date getSharedWithLibraryModifiedDate(Long userId, boolean recursive) {
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        // Include deleted entries as well.
        spec.deleted = null;
        List<ShareItem> shareItems = getShareItems(spec, userId, true);
        Date libraryModifiedDateForShareItems = getLibraryModifiedDateForShareItems(recursive, shareItems);
        Date hideDate = getSharingModule().getHiddenShareModTimeForCurrentUser(true);
        return max(hideDate, libraryModifiedDateForShareItems);
    }

    protected Date getLibraryModifiedDateForShareItems(boolean recursive, List<ShareItem> shareItems) {
        Date maxDate = null;
        List<Long> binderList = new ArrayList<Long>();
        for (ShareItem item : shareItems) {
            if (item.isExpired()) {
                maxDate = max(maxDate, item.getEndDate());
            } else if (item.isDeleted()) {
                maxDate = max(maxDate, item.getDeletedDate());
            } else {
                maxDate = max(maxDate, item.getStartDate());
                EntityIdentifier entityId = item.getSharedEntityIdentifier();
                if (entityId.getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                    FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(item);
                    if (entry.isPreDeleted()) {
                        maxDate = max(maxDate, new Date(entry.getPreDeletedWhen()));
                    } else {
                        maxDate = max(maxDate, entry.getModificationDate());
                    }
                } else if (entityId.getEntityType()== EntityIdentifier.EntityType.folder || entityId.getEntityType()== EntityIdentifier.EntityType.workspace) {
                    Binder binder = (Binder) getSharingModule().getSharedEntity(item);
                    if (isBinderPreDeleted(binder)) {
                        maxDate = max(maxDate, getPreDeletedDate(binder));
                    } else if (recursive) {
                        binderList.add(binder.getId());
                    }
                }
            }
        }
        maxDate = max(maxDate, getLibraryModifiedDate(binderList.toArray(new Long[binderList.size()]), recursive));
        return maxDate;
    }

    protected LibraryInfo getSharedWithLibraryInfo(Long userId) {
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        // Include deleted entries as well.
        spec.deleted = null;
        List<ShareItem> shareItems = getShareItems(spec, userId, true);
        LibraryInfo info = getLibraryInfoForShareItems(shareItems);
        Date hideDate = getSharingModule().getHiddenShareModTimeForCurrentUser(true);
        info.setModifiedDate(max(hideDate, info.getModifiedDate()));
        return info;
    }

    protected LibraryInfo getLibraryInfoForShareItems(List<ShareItem> shareItems) {
        Date maxDate = null;
        int files = 0;
        int folders = 0;
        long diskSpace = 0;
        List<Long> binderList = new ArrayList<Long>();
        for (ShareItem item : shareItems) {
            if (item.isExpired()) {
                maxDate = max(maxDate, item.getEndDate());
            } else if (item.isDeleted()) {
                maxDate = max(maxDate, item.getDeletedDate());
            } else {
                maxDate = max(maxDate, item.getStartDate());
                EntityIdentifier entityId = item.getSharedEntityIdentifier();
                if (entityId.getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                    FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(item);
                    if (entry.isPreDeleted()) {
                        maxDate = max(maxDate, new Date(entry.getPreDeletedWhen()));
                    } else {
                        maxDate = max(maxDate, entry.getModificationDate());
                    }
                    for (Attachment att : entry.getAttachments()) {
                        if (att instanceof FileAttachment) {
                            files++;
                            diskSpace += ((FileAttachment)att).getFileItem().getLength();
                        }
                    }
                } else if (entityId.getEntityType()== EntityIdentifier.EntityType.folder || entityId.getEntityType()== EntityIdentifier.EntityType.workspace) {
                    Binder binder = (Binder) getSharingModule().getSharedEntity(item);
                    if (isBinderPreDeleted(binder)) {
                        maxDate = max(maxDate, getPreDeletedDate(binder));
                    } else {
                        binderList.add(binder.getId());
                        folders++;
                    }
                }
            }
        }
        LibraryInfo info = getLibraryInfo(binderList.toArray(new Long[binderList.size()]));
        info.setModifiedDate(max(maxDate, info.getModifiedDate()));
        info.setDiskSpace(info.getDiskSpace() + diskSpace);
        info.setFolderCount(info.getFolderCount() + folders);
        info.setFileCount(info.getFileCount() + files);
        return info;
    }

    protected Date getLibraryModifiedDate(Long [] binderIds, boolean recursive) {
        if (binderIds.length==0) {
            return null;
        }
        for (Long id : binderIds) {
            try {
                Binder binder = getBinderModule().getBinder(id);
                if (binder instanceof Folder && binder.isMirrored()) {
                    getFolderModule().jitSynchronize((Folder)binder);
                }
            } catch (Exception e) {
            }
        }
        Criteria crit = getLibraryCriteria(binderIds, false, recursive);

        Map resultsMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, 1);
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>();
        SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(false), resultsMap, null, null, 0);
        return results.getLastModified();
    }

    protected LibraryInfo getLibraryInfo(Long [] binderIds) {
        if (binderIds.length==0) {
            return new LibraryInfo(0L, 0, 0, null);
        }
        Set<Long> idSet = new HashSet<Long>(Arrays.asList(binderIds));
        Criteria crit = getLibraryCriteria(binderIds, true, true);

        Map resultsMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, -1);
        LibraryInfo info = new LibraryInfo();
        Date modDate = null;
        long diskSpace = 0;
        int files = 0;
        int folders = 0;
        List<Map> entries = (List<Map>)resultsMap.get(ObjectKeys.SEARCH_ENTRIES);
        if (entries!=null) {
            for (Map entry : entries) {
                String docType = (String) entry.get(Constants.DOC_TYPE_FIELD);
                if (Constants.DOC_TYPE_ATTACHMENT.equals(docType)) {
                    files++;
                    String sizeStr = (String) entry.get(Constants.FILE_SIZE_IN_BYTES_FIELD);
                    if(sizeStr != null)
                        try {
                            diskSpace += Long.valueOf(sizeStr);
                        } catch (NumberFormatException e) {
                        }
                } else if (Constants.DOC_TYPE_BINDER.equals(docType)) {
                    String binderIdStr = (String) entry.get(Constants.DOCID_FIELD);
                    Long binderId = (binderIdStr != null)? Long.valueOf(binderIdStr) : null;
                    if (!idSet.contains(binderId)) {
                        folders++;
                    }
                    modDate = max(modDate, (Date) entry.get(Constants.MODIFICATION_DATE_FIELD));
                } else if (Constants.DOC_TYPE_ENTRY.equals(docType)) {
                    modDate = max(modDate, (Date) entry.get(Constants.MODIFICATION_DATE_FIELD));
                }
            }
        }
        info.setDiskSpace(diskSpace);
        info.setFileCount(files);
        info.setFolderCount(folders);
        info.setModifiedDate(modDate);
        return info;
    }

    protected Criteria getLibraryCriteria(Long[] binderIds, boolean includeAttachments, boolean recursive) {
        Criteria crit = new Criteria();
        if (recursive) {
            Junction or = Restrictions.disjunction();
            for (Long binderId : binderIds) {
                or.add(buildAncentryCriterion(binderId));
            }
            crit.add(or);
            or = buildWorkspacesAndLibraryEntitiesCriterion(includeAttachments);
            crit.add(or);
        } else {
            Junction or = Restrictions.disjunction();
            for (Long binderId : binderIds) {
                or.add(buildBinderCriterion(binderId));
                or.add(buildParentBinderCriterion(binderId));
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
        or.add(buildWorkspacesCriterion());

        Junction and = Restrictions.conjunction();
        or.add(and);
        and.add(buildLibraryCriterion(Boolean.TRUE));
        or = Restrictions.disjunction();
        or.add(buildFoldersCriterion());
        or.add(buildEntriesCriterion());
        if (includeAttachments) {
            or.add(buildAttachmentsCriterion());
        }
        and.add(or);
        return or;
    }

    protected Date getMyFilesLibraryModifiedDate(boolean recursive) {
        Long [] ids;
        if (recursive) {
            ids = new Long[] {getMyFilesFolderParent().getId()};
        } else if (SearchUtils.useHomeAsMyFiles(this)) {
            List<Long> homeFolderIds = SearchUtils.getHomeFolderIds(this, getLoggedInUser());
            try {
                for (Long id : homeFolderIds) {
                    Folder folder = getFolderModule().getFolder(id);
                    getFolderModule().jitSynchronize(folder);
                }
            } catch (Exception e) {
            }
            ids = homeFolderIds.toArray(new Long[homeFolderIds.size()]);
        } else {
            List<Long> hiddenFolderIds = SearchUtils.getMyFilesFolderIds(this, getLoggedInUser());
            hiddenFolderIds.add(getMyFilesFolderParent().getId());
            ids = hiddenFolderIds.toArray(new Long[hiddenFolderIds.size()]);
        }
        return getLibraryModifiedDate(ids, recursive);
    }

    protected LibraryInfo getMyFilesLibraryInfo() {
        int hiddenFolders = 0;
        Long [] ids;
        if (SearchUtils.useHomeAsMyFiles(this)) {
            List<Long> homeFolderIds = SearchUtils.getHomeFolderIds(this, getLoggedInUser());
            ids = homeFolderIds.toArray(new Long[homeFolderIds.size()]);
        } else {
            ids = new Long[] {getMyFilesFolderParent().getId()};
            hiddenFolders = SearchUtils.getMyFilesFolderIds(this, getLoggedInUser()).size();
        }
        LibraryInfo libraryInfo = getLibraryInfo(ids);
        libraryInfo.setFolderCount(libraryInfo.getFolderCount() - hiddenFolders);
        return libraryInfo;
    }

    protected Binder getMyFilesFolderParent() {
        org.kablink.teaming.domain.User loggedInUser = getLoggedInUser();
        if (SearchUtils.useHomeAsMyFiles(this, loggedInUser)) {
            return _getHomeFolder();
        } else {
            return _getUserWorkspace();
        }
    }

    protected BinderBrief getFakeMyFileFolders() {
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
        binder.addAdditionalLink("child_binders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
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
//        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
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
        //binder.addAdditionalLink("child_binder_tree", baseUri + "/binder_tree");
        //binder.addAdditionalLink("child_files", baseUri + "/files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        //binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri);
        //binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }

    protected Access getAccessRole(org.kablink.teaming.domain.FolderEntry entry) {
        AccessControlManager accessControlManager = getAccessControlManager();
        User loggedInUser = getLoggedInUser();

        ShareItem.Role foundRole = null;
        ShareItem.Role [] roles = new ShareItem.Role[] {ShareItem.Role.EDITOR, ShareItem.Role.VIEWER};
        for (ShareItem.Role role : roles) {
            WorkAreaOperation[] rights = role.getWorkAreaOperations();
            boolean match = true;
            for (WorkAreaOperation operation : rights) {
                if (!accessControlManager.testOperation(loggedInUser, entry, operation)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                foundRole = role;
                break;
            }
        }

        Access access = new Access();
        SharingPermission sharing = new SharingPermission();
        access.setSharing(sharing);
        if (foundRole!=null) {
            access.setRole(foundRole.name());
            if (accessControlManager.testOperation(loggedInUser, entry, WorkAreaOperation.ALLOW_SHARING_FORWARD)) {
                sharing.setInternal(accessControlManager.testOperation(loggedInUser, entry, WorkAreaOperation.ALLOW_SHARING_INTERNAL));
                sharing.setExternal(accessControlManager.testOperation(loggedInUser, entry, WorkAreaOperation.ALLOW_SHARING_EXTERNAL));
                sharing.setPublic(accessControlManager.testOperation(loggedInUser, entry, WorkAreaOperation.ALLOW_SHARING_PUBLIC));
            } else {
                sharing.setInternal(false);
                sharing.setExternal(false);
                sharing.setPublic(false);
            }
        } else {
            access.setRole(ShareItem.Role.NONE.name());
            sharing.setInternal(false);
            sharing.setExternal(false);
            sharing.setPublic(false);
        }

        return access;
    }

    protected Access getAccessRole(org.kablink.teaming.domain.Binder binder) {
        AccessControlManager accessControlManager = getAccessControlManager();
        User loggedInUser = getLoggedInUser();

        ShareItem.Role foundRole = null;
        ShareItem.Role [] roles = new ShareItem.Role[] {ShareItem.Role.CONTRIBUTOR, ShareItem.Role.EDITOR, ShareItem.Role.VIEWER};
        for (ShareItem.Role role : roles) {
            WorkAreaOperation[] rights = role.getWorkAreaOperations();
            boolean match = true;
            for (WorkAreaOperation operation : rights) {
                if (!accessControlManager.testOperation(loggedInUser, binder, operation)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                foundRole = role;
                break;
            }
        }

        Access access = new Access();
        SharingPermission sharing = new SharingPermission();
        access.setSharing(sharing);
        if (foundRole!=null) {
            access.setRole(foundRole.name());
            if (accessControlManager.testOperation(loggedInUser, binder, WorkAreaOperation.ALLOW_SHARING_FORWARD)) {
                sharing.setInternal(accessControlManager.testOperation(loggedInUser, binder, WorkAreaOperation.ALLOW_SHARING_INTERNAL));
                sharing.setExternal(accessControlManager.testOperation(loggedInUser, binder, WorkAreaOperation.ALLOW_SHARING_EXTERNAL));
                sharing.setPublic(accessControlManager.testOperation(loggedInUser, binder, WorkAreaOperation.ALLOW_SHARING_PUBLIC));
            } else {
                sharing.setInternal(false);
                sharing.setExternal(false);
                sharing.setPublic(false);
            }
        } else {
            access.setRole(ShareItem.Role.NONE.name());
            sharing.setInternal(false);
            sharing.setExternal(false);
            sharing.setPublic(false);
        }

        return access;
    }

}
