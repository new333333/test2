/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.binder.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.hibernate.NonUniqueObjectException;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.InternalException;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.comparator.BinderComparator;
import org.kablink.teaming.comparator.PrincipalComparator;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.domain.BasicAudit;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.BinderChange;
import org.kablink.teaming.domain.BinderChanges;
import org.kablink.teaming.domain.BinderQuota;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Group.GroupType;
import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.LibraryEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NotificationDef;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowControlledEntry;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FileAttachment.FileStatus;
import org.kablink.teaming.fi.connection.acl.AclResourceSession;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.lucene.util.TagObject;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.FilesErrors;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.shared.ObjectBuilder;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.runasync.RunAsyncCallback;
import org.kablink.teaming.runasync.RunAsyncManager;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.search.IndexSynchronizationManager;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.search.LuceneWriteSession;
import org.kablink.teaming.search.QueryBuilder;
import org.kablink.teaming.search.SearchObject;
import org.kablink.teaming.search.interceptor.IndexSynchronizationManagerInterceptor;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.ConcurrentStatusTicket;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.MergedOrderedListIterator;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Order;
import org.kablink.util.search.Restrictions;

import static org.kablink.util.search.Restrictions.between;
import static org.kablink.util.search.Restrictions.conjunction;
import static org.kablink.util.search.Restrictions.disjunction;
import static org.kablink.util.search.Restrictions.eq;
import static org.kablink.util.search.Restrictions.in;

import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * ?
 * 
 * @author Janet McCann
 */
@SuppressWarnings({"unchecked", "unused"})
public class BinderModuleImpl extends CommonDependencyInjection implements
		BinderModule {
	private TransactionTemplate transactionTemplate;
	private RunAsyncManager runAsyncManager;

	protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	protected RunAsyncManager getRunAsyncManager() {
		return runAsyncManager;
	}

	public void setRunAsyncManager(RunAsyncManager runAsyncManager) {
		this.runAsyncManager = runAsyncManager;
	}

	protected FileModule getFileModule() {
		// Can't use IoC due to circular dependency
		return (FileModule) SpringContextUtil.getBean("fileModule");
	}

	protected FolderModule getFolderModule() {
		// Can't use IoC due to circular dependency
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}

	protected BinderModule getBinderModule() {
		// Can't use IoC due to circular dependency
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}

	protected AdminModule getAdminModule() {
		// Can't use IoC due to circular dependency
		return (AdminModule) SpringContextUtil.getBean("adminModule");
	}

	protected ProfileModule getProfileModule() {
		// Can't use IoC due to circular dependency
		return (ProfileModule) SpringContextUtil.getBean("profileModule");
	}

	protected WorkflowModule getWorkflowModule() {
		// Can't use IoC due to circular dependency
		return (WorkflowModule) SpringContextUtil.getBean("workflowModule");
	}

	protected SharingModule getSharingModule() {
		// Can't use IoC due to circular dependency
		return (SharingModule) SpringContextUtil.getBean("sharingModule");
	}

	/*
	 * Check access to binder.
	 * 
	 * @see
	 * org.kablink.teaming.module.binder.BinderModule#checkAccess(org.kablink
	 * .teaming.domain.Binder, java.lang.String)
	 */
	@Override
	public boolean testAccess(Binder binder, BinderOperation operation) {
		return testAccess(null, binder, operation, Boolean.FALSE);
	}
	@Override
	public boolean testAccess(User user, Binder binder, BinderOperation operation, boolean thisLevelOnly) {
		try {
			checkAccess(user, binder, operation, thisLevelOnly);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}

	/**
	 * Use operation so application doesn't have the required knowledge. This
	 * also makes it easier to change what operations and allow multiple
	 * operations need to execute a method.
	 * 
	 * @param binder
	 * @param operation
	 * @throws AccessControlException
	 */
	@Override
	public void checkAccess(Binder binder, BinderOperation operation)
			throws AccessControlException {
		checkAccess(null, binder, operation, Boolean.FALSE);
	}
	@Override
	public void checkAccess(User user, Binder binder, BinderOperation operation)
			throws AccessControlException {
		checkAccess(user, binder, operation, Boolean.FALSE);
	}
	@Override
	public void checkAccess(User user, Binder binder, BinderOperation operation, boolean thisLevelOnly)
			throws AccessControlException {
        _checkAccess(user, binder, operation, thisLevelOnly);
    }

	private boolean _checkAccess(User user, Binder binder, BinderOperation operation, boolean thisLevelOnly)
			throws AccessControlException {
        boolean fullAccess = true;
		if (user == null) {
			user = RequestContextHolder.getRequestContext().getUser();
		}
		if (binder instanceof TemplateBinder) {
			Binder topBinder = binder;
			while (topBinder instanceof TemplateBinder && topBinder.getParentBinder() != null) {
				//Find the top TemplateBinder of this template
				topBinder = topBinder.getParentBinder();
			}
			if (((TemplateBinder)topBinder).getTemplateOwningBinderId() != null) {
				//This is a Local Template. Check that the user has Binder Administration rights to the owning binder of the local template
				Binder owningBinder = loadBinder(((TemplateBinder)topBinder).getTemplateOwningBinderId());
				getAccessControlManager().checkOperation(user, owningBinder,
						WorkAreaOperation.BINDER_ADMINISTRATION);
			} else {
				getAccessControlManager().checkOperation(user, 
						getCoreDao().loadZoneConfig(
								RequestContextHolder.getRequestContext()
										.getZoneId()),
						WorkAreaOperation.ZONE_ADMINISTRATION);
			}
		} else {
			if (user.isShared()) {
				//See if the user is only allowed "read only" rights
				ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
				if (zoneConfig.getAuthenticationConfig().isAnonymousReadOnly()) {
					//This is the guest account and it is read only. Only allow checks for read rights
					switch (operation) {
						case readEntries:
						case viewBinderTitle:
							//Allow these rights to be checked. All other rights will fail
							break;
						default:
							throw new AccessControlException(operation.toString(), new Object[] {});
					}
				}
			}
			switch (operation) {
			case addFolder:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.CREATE_FOLDERS);
				break;
			case addWorkspace:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.CREATE_WORKSPACES);
				break;
			case deleteEntries:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.DELETE_ENTRIES);
				break;
			case restoreBinder:
			case preDeleteBinder:
			case indexBinder:
			case indexTree:
			case manageMail:
			case modifyBinder:
			case setProperty:
			case manageConfiguration:
			case manageSimpleName:
			case changeEntryTimestamps:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.BINDER_ADMINISTRATION);
				break;
			case changeACL:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.CHANGE_ACCESS_CONTROL);
				break;
			case moveBinder:
				if(binder.isAclExternallyControlled()) { // Net Folder or its sub-folder
					getAccessControlManager().checkOperation(user, binder.getParentBinder(), WorkAreaOperation.DELETE_ENTRIES);
				}
				else { // Legacy Vibe folder
					getAccessControlManager().checkOperation(user, binder,
							WorkAreaOperation.BINDER_ADMINISTRATION);
				}
				break;
			case deleteBinder:
				if(binder.isAclExternallyControlled()) { // Net Folder or its sub-folder
					getAccessControlManager().checkOperation(user, binder.getParentBinder(), WorkAreaOperation.DELETE_ENTRIES);
				}
				else { // Legacy Vibe folder
					getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.BINDER_ADMINISTRATION);
				}
				break;				
			case renameBinder:
				if(binder.isAclExternallyControlled()) { // Net Folder or its sub-folder
					if (!binder.getParentBinder().isAclExternallyControlled() || binder.isHomeDir() || binder.isRoot()) {
						//We don't allow renaming the top net folder
						throw new AccessControlException(operation.toString(), new Object[] {});
					}
		        	// This is renaming of a Net Folder (or its sub-folder), which means that the user is attempting to rename a directory.
					// Do the checking in a way that is consistent with the file system semantic.				
		        	// Renaming a directory (a -> b) is like deleting a directory (a) and then adding another with a different name
		        	// (b) when looking at it from the directory membership point of view. So, we require the user to
		        	// have CREATE_FOLDERS right on the parent folder to allow for this operation.
					getAccessControlManager().checkOperation(binder.getParentBinder(), WorkAreaOperation.CREATE_FOLDERS);
					
					//OK, now check if this access right is due to sharing alone
					//TODO How do we make this not be a performance issue? Checking a right is supposed to be light weight.
					/**
					if (!getFolderModule().testFolderRenameAccess(user, binder.getParentBinder(), false)) {
						//This is a sharing right, so check if this is the top folder in a share
						boolean isTopSharedItem = false;
						ShareItemSelectSpec spec = new ShareItemSelectSpec();
						spec.setSharedEntityIdentifier( binder.getEntityIdentifier() );
						spec.setLatest( true );
						List<ShareItem> listOfShareItems = null;
						try {
							listOfShareItems = getSharingModule().getShareItems( spec );
						} catch ( Exception ex ) {
						}

						if (isTopSharedItem) {
							//We don't allow sharees to rename the top shared folder
							throw new AccessControlException(operation.toString(), new Object[] {});
						}
					}
					*/
				}
				else { // Legacy Vibe folder
					getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.BINDER_ADMINISTRATION);
				}
				break;				
			case copyBinder:
				if(binder.isAclExternallyControlled()) { // Net Folder or its sub-folder
					getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
				}
				else { // Legacy Vibe folder
					WorkAreaOperation copyOp;
					if (user.isShared())
					     copyOp = WorkAreaOperation.BINDER_ADMINISTRATION;	// Gust must be the binder admin to copy...
					else copyOp = WorkAreaOperation.READ_ENTRIES;			// ...everybody else just needs read access.
					getAccessControlManager().checkOperation(user, binder, copyOp);
				}
				break;
			case manageTeamMembers:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.BINDER_ADMINISTRATION);	// Bugzilla 947029:  Was CHANGE_ACCESS_CONTROL before this fix as per bug#763739.
				break;
			case manageTag:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.ADD_COMMUNITY_TAGS);
				break;
			case report:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.GENERATE_REPORTS);
				break;
			case downloadFolderAsCsv:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.DOWNLOAD_FOLDER_AS_CSV);
				break;
			case export:
				Boolean exportAllowedByBinderOwner = SPropsUtil.getBoolean("export.availableToBinderOwners", false);
				if (exportAllowedByBinderOwner) {
					try {
						getAccessControlManager().checkOperation(user, binder,
								WorkAreaOperation.BINDER_ADMINISTRATION);
						break;
					} catch(AccessControlException e) {}
				}
				getAccessControlManager().checkOperation(user, 
						getCoreDao().loadZoneConfig(
								RequestContextHolder.getRequestContext()
										.getZoneId()),
						WorkAreaOperation.ZONE_ADMINISTRATION);
				break;
			case readEntries:
				try {
					getAccessControlManager().checkOperation(user, binder,
							WorkAreaOperation.READ_ENTRIES);
				} catch(AccessControlException e) {
					// Bugzilla 944231:  If they can't read entries,
					// can they read their own entries?
					getAccessControlManager().checkOperation(user, binder,
							WorkAreaOperation.CREATOR_READ);
				}
				break;
			case viewBinderTitle:
				try {
					if (SPropsUtil.getBoolean("accessControl.viewBinderTitle.enabled", false)) {
						try {
							getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.VIEW_BINDER_TITLE);
						} catch(AccessControlException e) {
							//If VIEW_BINDER_TITLE is not explicitly set, try READ_ENTRIES.
							//  The READ_ENTRIES right also gives the user the right to view the binder title
							getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
						}
					} else {
						getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
					}
				} catch(AccessControlException e) {
					if (!thisLevelOnly) {
						//This check failed, so try to see if there is a sub-folder down the line the you can access
						SimpleProfiler.start("BinderModule.CheckAccess.LookForSubFolders");
						//First, see if we have cached this result from a previous call
						HttpSession session = ZoneContextHolder.getHttpSession();
						if (session != null) {
							Long cacheUserId = (Long)session.getAttribute(ObjectKeys.SESSION_ACL_CACHE_USER_ID);
							Map aclCache = (Map)session.getAttribute(ObjectKeys.SESSION_ACL_CACHE);
							if (cacheUserId == null || aclCache == null || !user.getId().equals(cacheUserId)) {
								cacheUserId = user.getId();
								aclCache = new HashMap();
								session.setAttribute(ObjectKeys.SESSION_ACL_CACHE_USER_ID, cacheUserId);
								session.setAttribute(ObjectKeys.SESSION_ACL_CACHE, aclCache);
							}
							Date now = new Date();
							if (user.getId().equals(cacheUserId) && aclCache.containsKey(binder.getId())) {
								//If aclCache has an entry for a binder, it means the user had access to some sub-folder in it
								Long time = (Long)aclCache.get(binder.getId());
								if (now.getTime() < time + ObjectKeys.SESSION_ACL_CACHE_TIMEOUT) {
									//There is a folder down below this and the acl check is within the cache time window
									SimpleProfiler.start("BinderModule.CheckAccess.LookForSubFolders.Cached");
									fullAccess = false;
									SimpleProfiler.stop("BinderModule.CheckAccess.LookForSubFolders.Cached");
								} else {
									//This has aged too long, remove it cached value
									aclCache.remove(binder.getId());
									if (!getBinderModule().testInferredAccessToBinder(user, binder)) {
										//There are no sub-binders to see, so return no access
										throw e;
									}
			                        fullAccess = false;
			                        //Add this into the cache
			                        aclCache.put(binder.getId(), Long.valueOf(now.getTime()));
								}
							} else {
								if (!getBinderModule().testInferredAccessToBinder(user, binder)) {
									//There are no sub-binders to see, so return no access
									throw e;
								}
		                        fullAccess = false;
		                        //Add this into the cache
		                        aclCache.put(binder.getId(), Long.valueOf(now.getTime()));
							}
						} else {
							if(!getBinderModule().testInferredAccessToBinder(user, binder)) {
								//There are no sub-binders to see, so return no access
								throw e;
							}
	                        fullAccess = false;
						}
						SimpleProfiler.stop("BinderModule.CheckAccess.LookForSubFolders");
					} else {
						//We aren't looking for any potential sub-binders. So, just throw the access control error
						throw e;
					}
				}
				break;
			case allowSharing:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.ALLOW_SHARING_INTERNAL);
				break;
			case allowSharingExternal:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.ALLOW_SHARING_EXTERNAL);
				break;
			case allowSharingPublic:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.ALLOW_SHARING_PUBLIC);
				break;
			case allowSharingPublicLinks:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.ALLOW_SHARING_PUBLIC_LINKS);
				break;
			case allowSharingForward:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.ALLOW_SHARING_FORWARD);
				break;
			case allowAccessNetFolder:
				getAccessControlManager().checkOperation(user, binder,
						WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER);
				break;
			default:
				throw new NotSupportedException(operation.toString(),
						"checkAccess");

			}
		}
        return fullAccess;
	}

	private Binder loadBinder(Long binderId) {
		return loadBinder(binderId, RequestContextHolder.getRequestContext()
				.getZoneId());
	}

	private Binder loadBinder(Long binderId, Long zoneId) {
		Binder binder = getCoreDao().loadBinder(binderId, zoneId);
		if (binder.isDeleted()) {
			throw new NoBinderByTheIdException(binderId);
		}
		return binder;
	}

	private BinderProcessor loadBinderProcessor(Binder binder) {
		// This is nothing but a dispatcher to an appropriate processor.
		// Shared logic, if exists, must be put into the corresponding method in
		// org.kablink.teaming.module.folder.AbstractfolderCoreProcessor class,
		// not
		// in this method.

		return (BinderProcessor) getProcessorManager().getProcessor(binder,
				binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));

	}

	@Override
	public Binder getBinder(Long binderId) throws NoBinderByTheIdException,
			AccessControlException {
		return getBinder(binderId, Boolean.FALSE);
	}
	@Override
	public Binder getBinder(Long binderId, boolean thisLevelOnly) throws NoBinderByTheIdException,
            AccessControlException {
        return getBinder(binderId, thisLevelOnly, Boolean.FALSE);
    }

	@Override
	public Binder getBinder(Long binderId, boolean thisLevelOnly, boolean returnLimitedBinderIfInferredAccess) throws NoBinderByTheIdException,
			AccessControlException {
		Binder binder = loadBinder(binderId);
		// Check if the user has "read" access to the binder.
		if (!(binder instanceof TemplateBinder)) {
			try {
				checkAccess(null, binder, BinderOperation.readEntries, thisLevelOnly);
			} catch(AccessControlException ace) {
				try {
					boolean fullAccess = _checkAccess(null, binder, BinderOperation.viewBinderTitle, thisLevelOnly);
                    if (!fullAccess && returnLimitedBinderIfInferredAccess) {
                        binder = binder.asLimitedBinder(true);
                    } else {
                    	//If the caller does not want a limited binder, return the full one
                    	return binder;
                    }
				} catch(AccessControlException ace2) {
					throw ace;
				}
			}
		}

		return binder;
	}

	@Override
	public Binder getBinderWithoutAccessCheck(Long binderId) throws NoBinderByTheIdException {
		return loadBinder(binderId);
	}

	@Override
	public boolean checkAccess(Long binderId, User user) {
		boolean value = false;
		Binder binder = null;
		try {
			binder = loadBinder(binderId, user.getZoneId());
		} catch (NoBinderByTheIdException e) {
			return false;
		}
		// Check if the user has "read" access to the binder.
		if (binder != null && !(binder instanceof TemplateBinder)) {
			value = getAccessControlManager().testOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
			if (!value) {
				value = getAccessControlManager().testOperation(user, binder, WorkAreaOperation.VIEW_BINDER_TITLE);
			}
		}
		return value;
	}

	@Override
	public SortedSet<Binder> getBinders(Collection<Long> binderIds) {
		return getBinders(binderIds, Boolean.TRUE);
	}
	@Override
	public SortedSet<Binder> getBinders(Collection<Long> binderIds, boolean doAccessCheck) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Comparator c = new BinderComparator(user.getLocale(),
				BinderComparator.SortByField.title);
		TreeSet<Binder> result = new TreeSet<Binder>(c);
		for (Long id : binderIds) {
			try {// access check done by getBinder
				// assume most binders are cached
				if (doAccessCheck) {
					result.add(getBinder(id));
				} else {
					result.add(getBinderWithoutAccessCheck(id));
				}
			} catch (NoObjectByTheIdException ex) {
			} catch (AccessControlException ax) {
			}

		}
		return result;
	}

	// Use search engine
	@Override
	public Map getBinders(Binder binder, Map options) {
		// assume have access to binder cause have a reference
		BinderProcessor processor = loadBinderProcessor(binder);
		return processor.getBinders(binder, options);
	}

	@Override
	public Map getBinders(Binder binder, List binderIds, Map options) {
		// assume have access to binder cause have a reference
		BinderProcessor processor = loadBinderProcessor(binder);
		return processor.getBinders(binder, binderIds, options);
	}

    // no transaction by default
	@Override
	public Binder addBinder(Long parentBinderId, String definitionId,
			InputDataAccessor inputData, Map fileItems, Map options)
			throws AccessControlException, WriteFilesException, WriteEntryDataException {
		long begin = System.nanoTime();
		Binder binder = null;
		Binder parentBinder = loadBinder(parentBinderId);
		if (parentBinder.isMyFilesDir()) {
			throw new NotSupportedException("errorcode.notsupported.addbinder");
		}
		Definition def = null;
		if (Validator.isNotNull(definitionId)) {
			def = getCoreDao().loadDefinition(definitionId,
					RequestContextHolder.getRequestContext().getZoneId());
		} else {
			def = parentBinder.getEntryDef();
		}

		if (options != null
				&& (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || options
						.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(parentBinder, BinderOperation.changeEntryTimestamps);

		if (def.getType() == Definition.FOLDER_VIEW) {
			checkAccess(parentBinder, BinderOperation.addFolder);
			binder = loadBinderProcessor(parentBinder).addBinder(
					parentBinder, def, Folder.class, inputData, fileItems,
					options);
			if (parentBinder instanceof Folder && parentBinder.isMirrored()
					&& binder.isMirrored()) {
				// Because addBinder method is not running inside a write
				// transaction, setDefinitionsInherited
				// method needs to start its own transaction in order for the
				// changes to be persisted to
				// the database. To do that, we need to invoke the method on the
				// proxy, instead of local
				// invocation.
				((BinderModule) SpringContextUtil.getBean("binderModule"))
						.setDefinitionsInherited(binder.getId(), true, false);
			}
		} else {
			if (!(parentBinder instanceof Workspace))
				throw new NotSupportedException(
						"errorcode.notsupported.addbinder.noWorkspace");
			// allow users workspaces to be created for all users
			if (parentBinder.isReserved()
					&& ObjectKeys.PROFILE_ROOT_INTERNALID.equals(parentBinder
							.getInternalId())) {
				if ((def == null)
						|| ((def.getType() != Definition.USER_WORKSPACE_VIEW) 
						&& (def.getType() != Definition.EXTERNAL_USER_WORKSPACE_VIEW))) {
					checkAccess(parentBinder, BinderOperation.addWorkspace);
				}
			} else {
				checkAccess(parentBinder, BinderOperation.addWorkspace);
			}
			binder = loadBinderProcessor(parentBinder).addBinder(parentBinder,
					def, Workspace.class, inputData, fileItems, options);
		}
		
		end(begin, "addBinder");
		return binder;
	}

	@Override
	public Set<Long> indexTree(Long binderId) {
		Set<Long> ids = new HashSet();
		ids.add(binderId);
		return indexTree(ids, StatusTicket.NULL_TICKET, null);
	}

	// optimization so we can manage the deletion to the searchEngine
	@Override
	public Set<Long> indexTree(Collection binderIds, StatusTicket statusTicket,
			String[] nodeNames) {
		IndexErrors errors = new IndexErrors();
		return indexTree(binderIds, statusTicket, nodeNames, errors, false, false);
	}

	@Override
	public Set<Long> indexTree(Collection binderIds, StatusTicket statusTicket,
			String[] nodeNames, IndexErrors errors, boolean allowUseOfHelperThreads, boolean skipFileContentIndexing) {
		return indexTreeWithHelper(binderIds, statusTicket, nodeNames, errors, allowUseOfHelperThreads, skipFileContentIndexing);				
	}

	private Set<Long> indexTreeWithHelper(Collection binderIds, StatusTicket statusTicket,
			String[] nodeNames, IndexErrors errors, boolean canUseHelperThreads, boolean skipFileContentIndexing) {
		long startTime = System.nanoTime();
		getCoreDao().flush(); // just incase
		if(statusTicket != null && canUseHelperThreads) {
			// Wrap the status ticket so that we can use thread-unsafe data structure in thread safe way.
			// This doesn't necessarily mean that the areas being reindexed can be processed in parallel though.
			// This also signals lower level code that this code is being executed in the context of reindexing.
			statusTicket = new ConcurrentStatusTicket(statusTicket); 
		}
		try {
			// make list of binders we have access to first
			if(logger.isDebugEnabled())
				logger.debug("Validating binders " + binderIds);
			boolean clearAll = false;
			List<Binder> binders = getCoreDao().loadObjects(binderIds,
					Binder.class,
					RequestContextHolder.getRequestContext().getZoneId());
			List<Binder> checked = new ArrayList();
			for (Binder binder : binders) {
				try {
					checkAccess(binder, BinderOperation.indexTree);
					if (binder.isDeleted())
						continue;
					if (binder.isZone())
						clearAll = true;
					checked.add(binder);
				} catch (AccessControlException ex) {					
					// Skip the ones we cannot access
					logger.warn("Skipping binder [" + binder.getPathName() + "] (id=" + binder.getId() + ") from indexing because user '" + RequestContextHolder.getRequestContext().getUserName() + "' has no rights");
				} catch (Exception ex) {
					logger.error("Error indexing binder " + binder, ex);
					errors.addError(binder);
				}

			}
			
			if (!checked.isEmpty() && statusTicket instanceof ConcurrentStatusTicket) {
				FilterControls filterControls = new FilterControls();
				filterControls.add(org.kablink.teaming.dao.util.Restrictions.eq("deleted", Boolean.FALSE));
				StringBuffer filterClauses = new StringBuffer();
				filterClauses.append(" and (");
				for(int i = 0; i < checked.size(); i++) {	
					if(i > 0)
						filterClauses.append(" or ");
					filterClauses.append("x.binderKey.sortKey like '")
					.append(checked.get(i).getBinderKey().getSortKey())
					.append("%'");
				}
				filterClauses.append(")");
				long estimatedTotalNumberOfBindersToIndex = getCoreDao().countObjects(Binder.class, filterControls, RequestContextHolder.getRequestContext().getZoneId(), filterClauses);
				((ConcurrentStatusTicket)statusTicket).setEstimatedTotalNumberOfBindersToIndex(estimatedTotalNumberOfBindersToIndex);
			}
			
			Set<Long> done = new HashSet();
			if (!checked.isEmpty()) {
				if(logger.isDebugEnabled())
					logger.debug("Setting indexers to " + StringUtil.toString(nodeNames));
				IndexSynchronizationManager.setNodeNames(nodeNames);
				try {
					if (clearAll) {
						if(logger.isDebugEnabled())
							logger.debug("Purging indexes on " + StringUtil.toString(nodeNames));
						LuceneWriteSession luceneSession = getLuceneSessionFactory()
								.openWriteSession(nodeNames);
						try {
							luceneSession.clearIndex();
						} catch (Exception e) {
							logger.warn("Exception:" + e);
						} finally {
							luceneSession.close();
						}
					} else {
						// delete all sub-binders - walk the ancestry list
						// and delete all the entries under each folderid.
						if(logger.isDebugEnabled())
							logger.debug("Deleting from indexes all binders at or below " + checked);
						for (Binder binder : checked) {
							IndexSynchronizationManager.deleteDocuments(new Term(
									Constants.ENTRY_ANCESTRY, binder.getId()
											.toString()));
						}
				  		IndexSynchronizationManager.applyChanges();
					}
					
					List<Long> concurrentBinderIds = new ArrayList<Long>();
					if(canUseHelperThreads) {
						List<Binder> nonConcurrentBinders = new ArrayList<Binder>();
						for (Binder binder : checked) {
							if(binder.isZone()) {
								concurrentBinderIds.addAll(obtainConcurrentBinderIdsFromSite(binder, done, (ConcurrentStatusTicket)statusTicket, errors, skipFileContentIndexing));
							}
							else if(ObjectKeys.NET_FOLDERS_ROOT_INTERNALID.equals(binder.getInternalId())) {
								concurrentBinderIds.addAll(obtainConcurrentBinderIdsFromNetFolders(binder, done, (ConcurrentStatusTicket)statusTicket, errors, skipFileContentIndexing));
							}
							else if(ObjectKeys.PROFILE_ROOT_INTERNALID.equals(binder.getInternalId())) {
								concurrentBinderIds.addAll(obtainConcurrentBinderIdsFromPersonalWorkspaces(binder, done, (ConcurrentStatusTicket)statusTicket, errors, skipFileContentIndexing));								
							}
							else {
								nonConcurrentBinders.add(binder);
							}
						}
						checked = nonConcurrentBinders;
					}
					
					// Flush all remaining index changes associated with this main thread.
					if(logger.isDebugEnabled())
						logger.debug("Applying remaining changes to index if any");
			  		IndexSynchronizationManager.applyChanges();

			  		// If there are branches that we can process in parallel, try executing them as concurrently as we can.
					if(!concurrentBinderIds.isEmpty()) {
						done.addAll(indexTreeConcurrent(concurrentBinderIds, done, (ConcurrentStatusTicket) statusTicket, nodeNames, errors, skipFileContentIndexing));
					}
					
					// The rest of the binders must be processed synchronously and sequentially.
					for (Binder binder : checked) {
						done.addAll(loadBinderProcessor(binder).indexTree(binder,
								done, statusTicket, errors, skipFileContentIndexing));
					}
					
					// Normally, all updates to the index are managed by the
					// framework so that
					// the index update won't be made until after the related
					// database transaction
					// has committed successfully. This is to avoid the index going
					// out of synch
					// with the database under rollback situation. However, in this
					// particular
					// case, we need to take an exception and flush out all index
					// changes before
					// returning from the method so that the select node ids set
					// above can be
					// applied during the flush. This does not violate the original
					// design intention
					// because, unlike other business operations, this operation is
					// specifically
					// written for index update only, and there is no corresponding
					// update transaction
					// on the database.
					if(logger.isDebugEnabled())
						logger.debug("Applying remaining changes to index if any");
					IndexSynchronizationManager.applyChanges();
					
					// If complete re-indexing, put the index files in an optimized
					// state for subsequent searches. It will also help cut down on
					// the number of file descriptors opened during the indexing.
					if (clearAll) {
						logger.info("Optimizing indexes");
						final LuceneWriteSession luceneSession = getLuceneSessionFactory()
								.openWriteSession(nodeNames);
						try {
							long dbConnHearbeatInterval = SPropsUtil.getLong("database.connection.heartbeat.interval", 180000);
							String dbConnHearbeatQuery = SPropsUtil.getString("database.connection.heartbeat.query", "SELECT 1");
							Runnable optimizeTask = new Runnable() {
								@Override
								public void run() {
									if(logger.isDebugEnabled())
										logger.debug("Executing index optimize in a separate thread");
									try {
										luceneSession.optimize();
									}
									catch(Exception e) {
										logger.error("Error during index optimize", e);
									}
									if(logger.isDebugEnabled())
										logger.debug("Completed index optimize in a separate thread");
								}
							};
							Thread optimizeThread = new Thread(optimizeTask, Thread.currentThread().getName() + "-optimize");
							optimizeThread.start();
							do {
								if(logger.isDebugEnabled())
									logger.debug("Executing heartbeat query on the connection during optimize");
								getCoreDao().executeHeartbeatQuery(dbConnHearbeatQuery);
								try {
									optimizeThread.join(dbConnHearbeatInterval);
								} catch (InterruptedException e) {}
							} while(optimizeThread.isAlive());
						}
						finally {
							luceneSession.close();
						}
					}
					
					if(statusTicket instanceof ConcurrentStatusTicket) {
						((ConcurrentStatusTicket)statusTicket).indexingCompleted();
					}
					else {
				    	statusTicket.setStatus(NLT.get("index.finished") + "<br/><br/>" + NLT.get("index.indexingBinder", new Object[] {String.valueOf(done.size()), String.valueOf(done.size())}));
				    	statusTicket.setState(WebKeys.AJAX_STATUS_STATE_COMPLETED);
					}
				} finally {
					if(logger.isDebugEnabled())
						logger.debug("Unsetting indexers");
					IndexSynchronizationManager.clearNodeNames();
				}
			}
			logger.info("Completed indexing of tree with " + done.size() + " binders. Time taken for indexing is " + (System.nanoTime()-startTime)/1000000.0 + " ms");
			return done;
		} finally {
			// It is important to call this at the end of the processing no
			// matter how it went.
			if (statusTicket != null)
				statusTicket.done();
		}
	}

	private void indexOneBinder(Binder binder, ConcurrentStatusTicket concurrentStatusTicket, IndexErrors errors, Collection<Long> done, boolean skipFileContentIndexing) {
	    BinderProcessor processor = loadBinderProcessor(binder);
		concurrentStatusTicket.incrementCurrentAndTotalCounts();
		if(logger.isDebugEnabled())
			logger.debug("Indexing binder [" + binder.getPathName() + "] (id=" + binder.getId() + ") - Progress (global estimate): " + concurrentStatusTicket);	    
		IndexErrors binderErrors = processor.indexBinder(binder, true, false, null, skipFileContentIndexing);
	    errors.add(binderErrors);
		done.add(binder.getId());
		if(logger.isTraceEnabled())
			logger.trace("Applying changes to index");
  		IndexSynchronizationManager.applyChanges(SPropsUtil.getInt("lucene.flush.threshold", 100));
	}
	
	private List<Long> obtainConcurrentBinderIdsFromNetFolders(Binder netFoldersWorkspace, Collection<Long> done, ConcurrentStatusTicket statusTicket, IndexErrors errors, boolean skipFileContentIndexing) {
		// Index "/Home Workspace/Net Folders" workspace (one level only)/ This workspace has no entries to index.
	    indexOneBinder(netFoldersWorkspace, statusTicket, errors, done, skipFileContentIndexing);
		List<Long> netFolderIds = getCoreDao().getSubBinderIds(netFoldersWorkspace);
		if(logger.isDebugEnabled())
			logger.debug("Identified " + netFolderIds.size() + " net folders to index independently: " + netFolderIds.toString());
		else
			logger.info("Identified " + netFolderIds.size() + " net folders to index independently");
		if(logger.isTraceEnabled())
			logger.trace("Applying changes to index");
  		IndexSynchronizationManager.applyChanges(SPropsUtil.getInt("lucene.flush.threshold", 100));
		return netFolderIds;
	}
	
	private List<Long> obtainConcurrentBinderIdsFromPersonalWorkspaces(Binder personalWorkspaces, Collection<Long> done, ConcurrentStatusTicket statusTicket, IndexErrors errors, boolean skipFileContentIndexing) {		
		// Index "/Home Workspace/Personal Workspaces" workspace (one level only).
		// This is a special workspace in which all principal objects (users and groups)
		// reside. So this will synchronously index all users and groups right here
		// rather than delegating the work to an asynchronous thread.
		// However, because this is one-level only indexing, it will NOT index users' 
		// personal workspaces (which include home folders).
		logger.info("Started indexing of all principals (users and groups) - Progress (global estimate): " + statusTicket);	    
	    indexOneBinder(personalWorkspaces, statusTicket, errors, done, skipFileContentIndexing);
		logger.info("Completed indexing of all principals (users and groups) - Progress (global estimate): " + statusTicket);	    
		List<Long> personalWorkspaceIds = getCoreDao().getSubBinderIds(personalWorkspaces);
		if(logger.isDebugEnabled())
			logger.debug("Identified " + personalWorkspaceIds.size() + " personal workspaces to index independently: " + personalWorkspaceIds.toString());
		else
			logger.info("Identified " + personalWorkspaceIds.size() + " personal workspaces to index independently");
		if(logger.isTraceEnabled())
			logger.trace("Applying changes to index");
  		IndexSynchronizationManager.applyChanges(SPropsUtil.getInt("lucene.flush.threshold", 100));
		return personalWorkspaceIds;
	}
	
	private List<Long> obtainConcurrentBinderIdsFromSite(Binder siteWorkspace, Collection<Long> done, ConcurrentStatusTicket statusTicket, IndexErrors errors, boolean skipFileContentIndexing) {
		// Index the very top workspace (= /Home Workspace)
		indexOneBinder(siteWorkspace, statusTicket, errors, done, skipFileContentIndexing);
		
		List<Long> netFolderIds = null;
		List<Long> personalWorkspaceIds = null;
		List<Long> remainingIds = null;
		List<Binder> subBinders = siteWorkspace.getBinders();
		BinderProcessor processor;
		
		for(Binder subBinder:subBinders) {
			if(ObjectKeys.NET_FOLDERS_ROOT_INTERNALID.equals(subBinder.getInternalId())) {
				netFolderIds = obtainConcurrentBinderIdsFromNetFolders(subBinder, done, statusTicket, errors, skipFileContentIndexing);
			}
			else if(ObjectKeys.PROFILE_ROOT_INTERNALID.equals(subBinder.getInternalId())) {
				personalWorkspaceIds = obtainConcurrentBinderIdsFromPersonalWorkspaces(subBinder, done, statusTicket, errors, skipFileContentIndexing); 
			}
			else {
				if(remainingIds == null)
					remainingIds = new ArrayList<Long>();
				remainingIds.add(subBinder.getId());
			}
		}
		
		if(logger.isTraceEnabled())
			logger.trace("Applying changes to index");
  		IndexSynchronizationManager.applyChanges(SPropsUtil.getInt("lucene.flush.threshold", 100));

		// We want to arrange the order in which binders are indexed such that 
		// 1) users' personal workspaces (including home folders)
		// 2) net folders
		// 3) the rest
		List<Long> binderIds = new ArrayList<Long>();
		if(personalWorkspaceIds != null) {
			 binderIds.addAll(personalWorkspaceIds);
		}
		if(netFolderIds != null) {
			binderIds.addAll(netFolderIds);
		}
		if(remainingIds != null) {
			binderIds.addAll(remainingIds);
		}

		if(logger.isDebugEnabled())
			logger.debug("Identified " + binderIds.size() + " branches to index independently: " + binderIds.toString());
		else
			logger.info("Identified " + binderIds.size() + " branches to index independently");
			
		return binderIds;
	}
	
	private Collection<Long> indexTreeConcurrent(List<Long> binderIds, Collection<Long> done, ConcurrentStatusTicket statusTicket, String[] nodeNames, IndexErrors errors, boolean skipFileContentIndexing) {
		int threadsSize = SPropsUtil.getInt("index.tree.helper.threads.size", 5);
		
		// Set up a queue and pre-populate it fully.
		BinderToIndexQueue queue = new BinderToIndexQueue(binderIds.size() + threadsSize);
		for(Long id:binderIds) {
			try {
				queue.put(id);
			} catch (InterruptedException e) {}
		}
		// Append poison pills to the queue.
		Long poisonPill = Long.valueOf(-1L);
		for(int i = 0; i < threadsSize; i++) {
			try {
				queue.put(poisonPill);
			} catch (InterruptedException e) {}
		}

		// Set up and start helper threads.
		Thread[] helperThreads = new Thread[threadsSize];
		Thread helperThread;
		long now = System.currentTimeMillis();
		if(logger.isDebugEnabled())
			logger.debug("Creating a queue with size " + queue.getPutCount() + " and " + threadsSize + " helper threads");
		for(int i = 0; i < threadsSize; i++) {
			helperThread = new Thread(new IndexHelper(statusTicket, nodeNames, errors, queue, RequestContextHolder.getRequestContext(), done, skipFileContentIndexing),
					Thread.currentThread().getName() + "-(" + (i+1) + "-" + now + ")");
			helperThreads[i] = helperThread;
			helperThread.start();
		}

		if(logger.isDebugEnabled())
			logger.debug("Waiting for helper threads to terminate");
		long dbConnHearbeatInterval = SPropsUtil.getLong("database.connection.heartbeat.interval", 180000);
		String dbConnHearbeatQuery = SPropsUtil.getString("database.connection.heartbeat.query", "SELECT 1");
		long lastHeartbeatTime = System.currentTimeMillis();
		for(int i = 0; i < threadsSize; i++) {
			while(helperThreads[i].isAlive()) {
				try {
					helperThreads[i].join(dbConnHearbeatInterval);
					if(System.currentTimeMillis() - lastHeartbeatTime >= dbConnHearbeatInterval) {
						if(logger.isDebugEnabled())
							logger.debug("Executing heartbeat query on the connection during indexing");
						getCoreDao().executeHeartbeatQuery(dbConnHearbeatQuery);
						lastHeartbeatTime = System.currentTimeMillis();
					}
				} catch (InterruptedException e) {}
			}
		}
			
		if(logger.isDebugEnabled())
			logger.debug("All helper threads terminated: Total number of binders indexed is " + done.size());
		
		return done;
	}
	
	@Override
	public IndexErrors indexBinder(Long binderId) {
		return indexBinder(binderId, false);
	}

	@Override
	public IndexErrors indexBinder(Long binderId, boolean includeEntries) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.indexBinder);
		return loadBinderProcessor(binder).indexBinder(binder, includeEntries);
	}

	@Override
	public IndexErrors indexBinderIncremental(Long binderId,
			boolean includeEntries) {
		return indexBinderIncremental(binderId, includeEntries, false);
	}
	
	@Override
	public IndexErrors indexBinderIncremental(Long binderId,
			boolean includeEntries, boolean skipFileContentIndexing) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.indexBinder);
		return loadBinderProcessor(binder).indexBinderIncremental(binder,
				includeEntries, skipFileContentIndexing);
	}
	
	//Routine to look through all binders and validate that the quota data is correct
	@Override
	public Set<Long> validateBinderQuotaTree(Binder binder, StatusTicket statusTicket, List<Long> errorIds) 
			throws AccessControlException {
		long startTime = System.nanoTime();
		getCoreDao().flush(); // just incase
		try {
			Set<Long> done = new HashSet();
			done.addAll(loadBinderProcessor(binder).validateBinderQuotasTree(binder,
								statusTicket, errorIds));
			if(logger.isDebugEnabled())
				logger.debug("validateBinderQuotasTree took " + (System.nanoTime()-startTime)/1000000.0 + " ms");
			return done;
		} finally {
			// It is important to call this at the end of the processing no
			// matter how it went.
			if (statusTicket != null)
				statusTicket.done();
		}
	}

	
    //no transaction
    @Override
	public void modifyBinder(Long binderId, String fileDataItemName, String fileName, InputStream content)
			throws AccessControlException, WriteFilesException, WriteEntryDataException {
    	MultipartFile mf = new SimpleMultipartFile(fileName, content);
    	Map<String, MultipartFile> fileItems = new HashMap<String, MultipartFile>();
    	if(fileDataItemName == null)
    		fileDataItemName = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER + "1";
    	fileItems.put(fileDataItemName, mf);
    	modifyBinder(binderId, new EmptyInputData(), fileItems, null, null);
    }

    // no transaction
	@Override
	public void modifyBinder(Long binderId, InputDataAccessor inputData,
			Map fileItems, Collection<String> deleteAttachments, Map options)
			throws AccessControlException, WriteFilesException, WriteEntryDataException {
		final Binder binder = loadBinder(binderId);

		if (inputData.exists(ObjectKeys.FIELD_BINDER_MIRRORED)) {
			boolean mirrored = Boolean.valueOf(inputData
					.getSingleValue(ObjectKeys.FIELD_BINDER_MIRRORED));
			if (mirrored && !binder.isMirrored() && binder.getBinderCount() > 0) {
				// We allow changing regular binder to mirrored one only when it
				// has no child binders.
				// It is ok for the binder to have existing entries though.
				throw new NotSupportedException(
						"errorcode.notsupported.not.leaf." + (binder.isAclExternallyControlled() ? "net" : "mirrored"));
			}
		}

		// save library flag here since it will be changed to the new value during modifyBinder() call
		boolean oldLibrary = binder.isLibrary();
		boolean oldUnique = binder.isUniqueTitles();

		if(binder.isAclExternallyControlled() &&
        		inputData.exists("title") &&
        		!inputData.getSingleValue("title").equals(binder.getTitle())) { 
        	// This is renaming of a Net Folder (or its sub-folder), which means that the user is attempting to rename a directory.
			// Do the checking in a way that is consistent with the file system semantic.				
        	// Renaming a directory (a -> b) is like deleting a directory (a) and then adding another with a different name
        	// (b) when looking at it from the directory membership point of view. So, we require the user to
        	// have CREATE_FOLDERS right on the parent folder to allow for this operation.
			getAccessControlManager().checkOperation(binder.getParentBinder(), WorkAreaOperation.CREATE_FOLDERS);
		}
		else {
			checkAccess(binder, BinderOperation.modifyBinder);
		}
	
		List atts = new ArrayList();
		if (deleteAttachments != null) {
			for (String id : deleteAttachments) {
				Attachment a = binder.getAttachment(id);
				if (a != null)
					atts.add(a);
			}
		}
		loadBinderProcessor(binder).modifyBinder(binder, inputData, fileItems,
				atts, options);
		if (inputData.exists(ObjectKeys.FIELD_BINDER_LIBRARY)) {
			final boolean newLibrary = Boolean.valueOf(inputData
					.getSingleValue(ObjectKeys.FIELD_BINDER_LIBRARY));
			if (oldLibrary != newLibrary) {
				// wrap in a transaction
				Boolean successful = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus status) {
						boolean successful = true;
						// remove old reserved names
						getCoreDao().clearFileNames(binder);
						if (newLibrary) {
							// add new ones
							// get all attachments in this binder
							FilterControls filter = new FilterControls(
									new String[] { "owner.owningBinderId",
											"type" }, new Object[] {
											binder.getId(), 'f' });
							filter.setZoneCheck(false); // skip zone, binder
							// good enough
							ObjectControls objs = new ObjectControls(
									FileAttachment.class, new String[] {
											"fileItem.name", "owner.ownerId" });
							SFQuery query = getCoreDao().queryObjects(objs,
									filter, binder.getZoneId());
							try {
								while (query.hasNext()) {
									Object[] result = (Object[]) query.next();
									// skip files attached to the binder itself
									if (result[1].equals(binder.getId()))
										continue;
									LibraryEntry le = new LibraryEntry(binder
											.getId(), LibraryEntry.FILE,
											(String) result[0]);
									le.setEntityId((Long) result[1]);
									getCoreDao().save(le);
								}
							} catch (Exception e) {
								successful = false;
							} finally {
								query.close();
							}
						}
						// Unnecessary since the new value was stored into the binder during modifyBinder() call
						//binder.setLibrary(newLibrary);
						return successful;
					}
				});
				
				if (newLibrary && !successful) {
					//The change to a library folder didn't happen, so we have to turn off the library flag
					Map tempData = new HashMap();
					tempData.put(ObjectKeys.FIELD_BINDER_LIBRARY, Boolean.FALSE);
					loadBinderProcessor(binder).modifyBinder(binder, new MapInputData(tempData), new HashMap(),
							new HashSet(), null);
					
					throw new ConfigurationException("errorcode.cannot.make.library", (Object[]) null);
				}
			}
		}
		if (inputData.exists(ObjectKeys.FIELD_BINDER_UNIQUETITLES)) {
			final boolean newUnique = Boolean.valueOf(inputData
					.getSingleValue(ObjectKeys.FIELD_BINDER_UNIQUETITLES));
			if (newUnique != oldUnique) {
				// wrap in a transaction
				getTransactionTemplate().execute(new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus status) {
						// remove old reserved names
						getCoreDao().clearTitles(binder);
						if (newUnique) {
							List<Binder> binders = binder.getBinders();
							// first add subfolder titles
							try {
								for (Binder b : binders) {
									getCoreDao().updateTitle(binder, b, null,
											b.getNormalTitle());
								}
							} catch (HibernateSystemException he) {
								if (he.contains(NonUniqueObjectException.class)) {
									throw new ConfigurationException(
											"errorcode.cannot.make.unique",
											(Object[]) null);
								}
							}
							// add entry titles
							if (binder instanceof Folder) {
								Folder parentFolder = (Folder) binder;
								SFQuery query = getFolderDao().queryEntries(
										parentFolder,
										new FilterControls("HKey.level",
												Integer.valueOf(1)));

								try {
									while (query.hasNext()) {
										Object obj = query.next();
										if (obj instanceof Object[])
											obj = ((Object[]) obj)[0];
										FolderEntry entry = (FolderEntry) obj;
										LibraryEntry le = new LibraryEntry(
												binder.getId(),
												LibraryEntry.TITLE, entry
														.getNormalTitle());
										le.setEntityId(entry.getId());
										getCoreDao().save(le);
									}
								} catch (HibernateSystemException he) {
									if (he
											.contains(NonUniqueObjectException.class)) {
										throw new ConfigurationException(
												"errorcode.cannot.make.unique",
												(Object[]) null);
									}

								} finally {
									query.close();
								}
							}
						}
						binder.setUniqueTitles(newUnique);
						return null;
					}
				});

			}
		}
	}

	// inside write transaction
	@Override
	public void setProperty(Long binderId, String property, Object value) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.setProperty);
		binder.setProperty(property, value);
	}

	// inside write transaction
	@Override
	public void restoreBinder(Long binderId, Object renameData) throws WriteEntryDataException, WriteFilesException {
		restoreBinder(binderId, renameData, true);
	}
	@Override
	public void restoreBinder(Long binderId, Object renameData, boolean reindex) throws WriteEntryDataException, WriteFilesException {
		restoreBinder(binderId, renameData, true, null, reindex);
	}

	// inside write transaction
	@Override
	public void restoreBinder(Long binderId, Object renameData, boolean deleteMirroredSource, Map options) throws WriteEntryDataException, WriteFilesException {
		restoreBinder(binderId, renameData, deleteMirroredSource, options, true);
	}
	@Override
	// in write transaction
	public void restoreBinder(Long binderId, Object renameData, boolean deleteMirroredSource, Map options, boolean reindex) throws WriteEntryDataException, WriteFilesException {
		// Can we access the Binder as a non-mirrored binder?
		Binder binder = loadBinder(binderId);
		if ((null != binder) && (!(binder.isMirrored()))) {
			// Yes!  Is it preDeleted?
			EntityType et = binder.getEntityType();
			boolean isPreDeleted = false;
			boolean isFolder = (EntityType.folder == et);
			Folder folder = null;
			Workspace ws = null;
			if (isFolder) {
	        	folder = ((Folder) binder);
				isPreDeleted = folder.isPreDeleted();
			}
			else {
				boolean isWorkspace = (EntityType.workspace == et);
				if (isWorkspace) {
		        	ws = ((Workspace) binder);
					isPreDeleted = ws.isPreDeleted();
				}
			}
			if (isPreDeleted) {
				// Yes!  Validate we can restore it...
		        checkAccess(binder, BinderOperation.restoreBinder);
		        
		        // ...restore it...
		        if (isFolder) {
		        	folder.setPreDeleted(null);
		        	folder.setPreDeletedWhen(null);
		        	folder.setPreDeletedBy(null);
		        }
		        
		        else {
		        	ws.setPreDeleted(null);
		        	ws.setPreDeletedWhen(null);
		        	ws.setPreDeletedBy(null);
		        	
		        	if (BinderHelper.isBinderUserWorkspace(binder)) {
		        		// Note:  Won't work for guest, but guest will
		        		// never get here because of the
		        		// isBinderSystemUserWS() check at the top of
		        		// the method.
		        		getProfileModule().setUserWorkspacePreDeleted(ws.getOwnerId(), false);
		        	}
		        }

		        // ...log the restoration...
				BinderProcessor processor = loadBinderProcessor(binder);
				TrashHelper.changeBinder_Log(processor, binder, ChangeLog.RESTOREBINDER);
				
				// ...register the names so any naming conflicts get
				// ...handled...
		        TrashHelper.registerBinderNames(getCoreDao(), binder, renameData);
		        
		        // ...and finally, if requested to do so...
		        if (reindex) {
			        // ...re-index the Binder.
		        	processor.indexBinder(binder, true);
		        }
		        processor.updateParentModTime(binder.getParentBinder(), options, reindex);
			}
		}
	}

	//Check if this binder is over quota
	@Override
	public boolean isBinderDiskHighWaterMarkExceeded(Binder binder) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		ZoneConfig zoneConf = getCoreDao().loadZoneConfig(zoneId);
		Integer highWaterMarkPercentage = zoneConf.getDiskQuotasHighwaterPercentage();
		if (zoneConf.isBinderQuotaEnabled() && zoneConf.isBinderQuotaInitialized()) {
			Binder parentBinder = binder;
			while (parentBinder != null) {
				BinderQuota binderQuota = getCoreDao().loadBinderQuota(zoneId, parentBinder.getId());
				Long quota = binderQuota.getDiskQuota();
				Long diskSpaceUsed = binderQuota.getDiskSpaceUsedCumulative();
				if (quota != null && diskSpaceUsed != null && 
						diskSpaceUsed > quota * highWaterMarkPercentage / 100) {
					return true;
				}
				parentBinder = parentBinder.getParentBinder();
			}
			return false;
		} else {
			return false;
		}
	}
	
	//Check if quotas are enabled
	@Override
	public boolean isBinderDiskQuotaEnabled() {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		ZoneConfig zoneConf = getCoreDao().loadZoneConfig(zoneId);
		if (zoneConf.isBinderQuotaEnabled() && zoneConf.isBinderQuotaInitialized()) {
			return true;
		} else {
			return false;
		}
	}
		
	//Check if this binder is over quota
	@Override
	public boolean isBinderDiskQuotaExceeded(Binder binder) {
		boolean result = isBinderDiskQuotaOk(binder, 0L);
		return !result;
	}
	//Check if adding a file would exceed the quota
	@Override
	public boolean isBinderDiskQuotaOk(Binder binder, long fileSize) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		ZoneConfig zoneConf = getCoreDao().loadZoneConfig(zoneId);
		if (zoneConf.isBinderQuotaEnabled() && zoneConf.isBinderQuotaInitialized()) {
			Binder parentBinder = binder;
			while (parentBinder != null) {
				try {
					BinderQuota binderQuota = getCoreDao().loadBinderQuota(zoneId, parentBinder.getId());
					if (binderQuota.getDiskQuota() != null) {
						Long quota = binderQuota.getDiskQuota();
						Long diskSpaceUsedCumulative = binderQuota.getDiskSpaceUsedCumulative();
						if (diskSpaceUsedCumulative == null) {
							diskSpaceUsedCumulative = 0L;
						}
						if (diskSpaceUsedCumulative + fileSize > quota) {
							//This will exceed the quota
							return false;
						}
					}
				} catch(NoObjectByTheIdException e) {
					//Skip any binders that don't have a quota set up (shouldn't happen, but...)
				}
				parentBinder = parentBinder.getParentBinder();
			}
		}
		return true;
	}
	
	//Get the lowest parent quota
	@Override
	public Long getMinParentBinderQuota(Binder binder) {
		Long leastQuota = null;
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		ZoneConfig zoneConf = getCoreDao().loadZoneConfig(zoneId);
		if (zoneConf.isBinderQuotaEnabled() && zoneConf.isBinderQuotaInitialized()) {
			Binder parentBinder = binder.getParentBinder();
			while (parentBinder != null) {
				try {
					BinderQuota binderQuota = getCoreDao().loadBinderQuota(zoneId, parentBinder.getId());
					if (binderQuota.getDiskQuota() != null) {
						Long quota = binderQuota.getDiskQuota();
						if (leastQuota == null || (quota != null && quota  < leastQuota)) {
							//A new low
							leastQuota = quota;
						}
					}
				} catch(NoObjectByTheIdException e) {
					//Skip any binders that don't have a quota set up (shouldn't happen, but...)
				}
				parentBinder = parentBinder.getParentBinder();
			}
		}
		return leastQuota;
	}
	
	//Get the most that this binder will allow for disk usage
	@Override
	public Long getMinBinderQuotaLeft(Binder binder) {
		Long leastQuotaLeft = null;
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		ZoneConfig zoneConf = getCoreDao().loadZoneConfig(zoneId);
		if (zoneConf.isBinderQuotaEnabled() && zoneConf.isBinderQuotaInitialized()) {
			Binder parentBinder = binder;
			while (parentBinder != null) {
				try {
					BinderQuota binderQuota = getCoreDao().loadBinderQuota(zoneId, parentBinder.getId());
					if (binderQuota.getDiskQuota() != null) {
						Long quota = binderQuota.getDiskQuota();
						Long diskSpaceUsedCumulative = binderQuota.getDiskSpaceUsedCumulative();
						if (leastQuotaLeft == null || quota - diskSpaceUsedCumulative < leastQuotaLeft) {
							//A new low
							leastQuotaLeft = quota - diskSpaceUsedCumulative;
							if (leastQuotaLeft < 0) {
								leastQuotaLeft = 0L;
								break;
							}
						}
					}
				} catch(NoObjectByTheIdException e) {
					//Skip any binders that don't have a quota set up (shouldn't happen, but...)
				}
				parentBinder = parentBinder.getParentBinder();
			}
		}
		return leastQuotaLeft;
	}
	
	//Get the most that this binder will allow for disk usage
	@Override
	public Binder getMinBinderQuotaLeftBinder(Binder binder) {
		Long leastQuotaLeft = null;
		Binder result = null;
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		ZoneConfig zoneConf = getCoreDao().loadZoneConfig(zoneId);
		if (zoneConf.isBinderQuotaEnabled() && zoneConf.isBinderQuotaInitialized()) {
			Binder parentBinder = binder;
			while (parentBinder != null) {
				try {
					BinderQuota binderQuota = getCoreDao().loadBinderQuota(zoneId, parentBinder.getId());
					if (binderQuota.getDiskQuota() != null) {
						Long quota = binderQuota.getDiskQuota();
						Long diskSpaceUsedCumulative = binderQuota.getDiskSpaceUsedCumulative();
						if (leastQuotaLeft == null || quota - diskSpaceUsedCumulative < leastQuotaLeft) {
							//A new low
							result = parentBinder;
							leastQuotaLeft = quota - diskSpaceUsedCumulative;
							if (leastQuotaLeft < 0) {
								leastQuotaLeft = 0L;
								break;
							}
						}
					}
				} catch(NoObjectByTheIdException e) {
					//Skip any binders that don't have a quota set up (shouldn't happen, but...)
				}
				parentBinder = parentBinder.getParentBinder();
			}
		}
		return result;
	}
	
	//Increment the disk space used in this binder. Update the cumulative counts in the parent binders
	@Override
	public void incrementDiskSpaceUsed(Binder binder, long fileSize) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		try {
			BinderQuota bq = getCoreDao().loadBinderQuota(zoneId, binder.getId());
			Long diskSpaceUsed = bq.getDiskSpaceUsed();
			diskSpaceUsed += fileSize;
			bq.setDiskSpaceUsed(diskSpaceUsed);
		} catch(Exception e) {
			//Oops, not there. This will have to be cleaned up with a "validate" command
			return;
		}
		Binder parentBinder = binder;
		while (parentBinder != null) {
			try {
				BinderQuota bq = getCoreDao().loadBinderQuota(zoneId, parentBinder.getId());
				Long diskSpaceUsedCumulative = bq.getDiskSpaceUsedCumulative();
				diskSpaceUsedCumulative += fileSize;
				bq.setDiskSpaceUsedCumulative(diskSpaceUsedCumulative);
			} catch(Exception e) {
				//Oops, not there. This will have to be cleaned up with a "validate" command
				break;
			}
			parentBinder = parentBinder.getParentBinder();
		}
	}

	//Decrement the disk space used in this binder. Update the cumulative counts in the parent binders
	@Override
	public void decrementDiskSpaceUsed(Binder binder, long fileSize) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		try {
			BinderQuota bq = getCoreDao().loadBinderQuota(zoneId, binder.getId());
			Long diskSpaceUsed = bq.getDiskSpaceUsed();
			diskSpaceUsed -= fileSize;
			bq.setDiskSpaceUsed(diskSpaceUsed);
		} catch(Exception e) {
			//Oops, not there. This will have to be cleaned up with a "validate" command
			return;
		}
		Binder parentBinder = binder;
		while (parentBinder != null) {
			try {
				BinderQuota bq = getCoreDao().loadBinderQuota(zoneId, parentBinder.getId());
				Long diskSpaceUsedCumulative = bq.getDiskSpaceUsedCumulative();
				diskSpaceUsedCumulative -= fileSize;
				bq.setDiskSpaceUsedCumulative(diskSpaceUsedCumulative);
			} catch(Exception e) {
				//Oops, not there. This will have to be cleaned up with a "validate" command
				break;
			}
			parentBinder = parentBinder.getParentBinder();
		}
	}

	// inside write transaction
	@Override
	public void preDeleteBinder(Long binderId, Long userId) {
		preDeleteBinder(binderId, userId, true);
	}
	@Override
	public void preDeleteBinder(Long binderId, Long userId, boolean reindex) {
		preDeleteBinder(binderId, userId, true, null, reindex);
	}

	// inside write transaction
	@Override
	public void preDeleteBinder(Long binderId, Long userId, boolean deleteMirroredSource, Map options) {
		preDeleteBinder(binderId, userId, deleteMirroredSource, options, true);
	}
	@Override
	// in write transaction
	public void preDeleteBinder(Long binderId, Long userId, boolean deleteMirroredSource, Map options, boolean reindex) {
		Binder binder = loadBinder(binderId);
		if (BinderHelper.isBinderSystemUserWS(binder)) {
			throw new NotSupportedException("errorcode.notsupported.preDeleteBinder.systemUserWS");
		}
		if ((null != binder) && (!(binder.isMirrored()))) {
			EntityType et = binder.getEntityType();
			boolean isFolder    = (EntityType.folder    == et);
			boolean isWorkspace = (EntityType.workspace == et);
			if (isFolder || isWorkspace) {
		        checkAccess(binder, BinderOperation.preDeleteBinder);
		        if (isFolder) {
		        	Folder folder = ((Folder) binder);
		        	folder.setPreDeleted(Boolean.TRUE);
		        	folder.setPreDeletedWhen(System.currentTimeMillis());
		        	folder.setPreDeletedBy(userId);
		        }
		        
		        else {
		        	Workspace ws = ((Workspace) binder);
		        	ws.setPreDeleted(Boolean.TRUE);
		        	ws.setPreDeletedWhen(System.currentTimeMillis());
		        	ws.setPreDeletedBy(userId);
		        	
		        	if (BinderHelper.isBinderUserWorkspace(binder)) {
		        		// Note:  Won't work for guest, but guest will
		        		// never get here because of the
		        		// isBinderSystemUserWS() check at the top of
		        		// the method.
		        		getProfileModule().setUserWorkspacePreDeleted(ws.getOwnerId(), true);
		        	}
		        }

		        Binder parentBinder = binder.getParentBinder();
		        BinderProcessor processor = loadBinderProcessor(binder);
				TrashHelper.changeBinder_Log(processor, binder, ChangeLog.PREDELETEBINDER);
		        TrashHelper.unRegisterBinderNames(getCoreDao(), binder);
		        if (reindex) {
		        	processor.indexBinder(binder, true);
		        }
		        processor.updateParentModTime(parentBinder, options, reindex);
			}
		}
	}

	// no transaction
	@Override
	public void deleteBinder(Long binderId) {
		deleteBinder(binderId, true, null, false);
	}

	// no transaction
	@Override
	public void deleteBinder(Long binderId, boolean deleteMirroredSource,
			Map options) {
		deleteBinder(binderId, deleteMirroredSource, options, false);
	}
	
	// no transaction
	@Override
	public void deleteBinder(Long binderId, boolean deleteMirroredSource,
			Map options, boolean phase1Only) {
		deleteBinder(binderId, deleteMirroredSource, options, phase1Only, false);
	}
	
	// no transaction
	@Override
	public void deleteBinder(Long binderId, boolean deleteMirroredSource,
			Map options, boolean phase1Only, boolean createDbLogForTopBinderOnly) {
		Binder binder;
		try {
			binder = loadBinder(binderId);
		}
		catch(NoBinderByTheIdException e) {
			// The binder is no longer found, meaning there's nothing more we need to do.
			return;
		}
		Binder parentBinder = null;
		if(binder != null)
			parentBinder = binder.getParentBinder();
		
		//Guard against deleting the whole mirrored source by accident
		if (binder.isAclExternallyControlled() && 
				parentBinder != null && parentBinder.isAclExternallyControlled() &&
				parentBinder.getResourceDriverName().equals(binder.getResourceDriverName())) {
			
			//This is a sub-folder of a net folder. Check that it has a proper resource path
			if (binder.getResourcePath() == null || binder.getResourcePath().equals("") || 
					binder.getResourcePath().equals("/")) {
				//Don't allow deleting of this source because it looks like the configuration wasn't properly completed.
				deleteMirroredSource = false;
			}
		}
		
		deleteBinderPhase1(binderId, deleteMirroredSource, options, createDbLogForTopBinderOnly);
		if (!phase1Only) {
			deleteBinderPhase2();
		}
	}

	// no transaction
	@Override
	public void deleteBinderFinish() {
		deleteBinderPhase2();
	}
	
	// inside write transaction
	@Override
	public Binder moveBinder(Long fromId, Long toId, Map options) throws NotSupportedException {
		Binder source = loadBinder(fromId);
		Binder sourceParent = source.getParentBinder();
		checkAccess(source, BinderOperation.moveBinder);
		Binder destination = loadBinder(toId);

        Binder newBinder;
		//See if moving from a regular folder to a mirrored folder
		if (!source.isMirrored() && destination.isMirrored()) {
            if (options==null) {
                options = new HashMap();
            }
			//This is a special case move. Do it by copying the folder then deleting it
			options.put(ObjectKeys.INPUT_OPTION_MOVE_SHARE_ITEMS, Boolean.TRUE);	//Also move the share items to the new binder
			newBinder = copyBinder(fromId, toId, true, options);
			//Note that if the delete fails, the copied binder will still remain
			//However, some of the original source binders may also be left behind
			//It was felt that it is better to leave everything to the user to clean up.
			deleteBinder(source.getId(), false, null);
			
		} else {
            newBinder = source;
			if (loadBinderProcessor(source).checkMoveBinderQuota(source, destination)) {
				if (source.getEntityType().equals(EntityType.folder)) {
					getAccessControlManager().checkOperation(destination,
							WorkAreaOperation.CREATE_FOLDERS);
				} else {
					getAccessControlManager().checkOperation(destination,
							WorkAreaOperation.CREATE_WORKSPACES);
				}
				// move whole tree at once
				loadBinderProcessor(source).moveBinder(source, destination, options);
				
				if(sourceParent != null)
					loadBinderProcessor(sourceParent).updateParentModTime(sourceParent, options);
				
				if(sourceParent != destination)
					loadBinderProcessor(destination).updateParentModTime(destination, options);
				
			} else {
				throw new NotSupportedException(NLT.get("quota.binder.exceeded"));
			}
		}
        return newBinder;
	}

	// no transaction
	@Override
	public Binder copyBinder(Long fromId, Long toId, boolean cascade,
			Map options) throws NotSupportedException {
		Binder source = loadBinder(fromId);
		checkAccess(source, BinderOperation.copyBinder);
		Binder destinationParent = loadBinder(toId);
		//See if there is enough quota to do this
		if (loadBinderProcessor(source).checkMoveBinderQuota(source, destinationParent)) {
			if (source.getEntityType().equals(EntityType.folder)) {
				getAccessControlManager().checkOperation(destinationParent,
						WorkAreaOperation.CREATE_FOLDERS);
			} else {
				getAccessControlManager().checkOperation(destinationParent,
						WorkAreaOperation.CREATE_WORKSPACES);
			}
    		//We must guard against invalid copy attempts (such as complex entries copied to mirrored folder)
    		if (!source.isAclExternallyControlled() && destinationParent.isAclExternallyControlled()) {
    			copyFolderCheckMirrored(source, destinationParent, cascade);
    		}
			Map params = new HashMap();
			if (options != null)
				params.putAll(options);
			params.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.TRUE);
			params.put(ObjectKeys.INPUT_OPTION_PRESERVE_DOCNUMBER, Boolean.TRUE);
			// lock top level
			Binder binder = loadBinderProcessor(source).copyBinder(source,
					destinationParent, params);
			if (cascade)
				doCopyChildren(source, binder, params);

			return binder;
		} else {
			throw new NotSupportedException(NLT.get("quota.binder.exceeded"));
		}
	}
	
	private void copyFolderCheckMirrored(Binder source, Binder destination, boolean cascade) {
		//This type of request could have invalid entries, so check each one
		Map getEntriesOptions = new HashMap();
		//Specify if this request is to copy children binders, too.
		getEntriesOptions.put(ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS, new Boolean(cascade));
  		Map folderEntries = getFolderModule().getEntries(source.getId(), getEntriesOptions);
      	List<Map> searchEntries = (List)folderEntries.get(ObjectKeys.SEARCH_ENTRIES);

		for (Map se : searchEntries) {
			String entityIdStr   = ((String) se.get(Constants.DOCID_FIELD ));
			String entityTypeStr = ((String) se.get(Constants.ENTITY_FIELD));
			if (MiscUtil.hasString(entityIdStr) && MiscUtil.hasString(entityTypeStr)) {
				if (entityTypeStr.equalsIgnoreCase(EntityType.folderEntry.name())) {
					Long  entryId = Long.valueOf(entityIdStr);
					Entry entry   = getFolderModule().getEntry(null, entryId);
					try {
						BinderHelper.copyEntryCheckMirrored(source, entry, destination);
					} catch(Exception e) {
						//This entry cannot be copied, so don't copy this binder
						throw new NotSupportedException("errorcode.notsupported.copyEntry.complexEntryToMirrored." + (destination.isAclExternallyControlled() ? "net" : "mirrored"));
					}
				}
				else if (entityTypeStr.equalsIgnoreCase(EntityType.folder.name())) {
					Long   folderId = Long.valueOf(entityIdStr);
					Folder folder   = getFolderModule().getFolder(folderId);
					copyFolderCheckMirrored(folder, destination, cascade);
				}
				else {
					//This entry cannot be copied, so don't copy this binder
					throw new NotSupportedException("errorcode.notsupported.copyEntry.complexEntryToMirrored." + (destination.isAclExternallyControlled() ? "net" : "mirrored"));
				}
			}
			else {
				//This entry cannot be copied, so don't copy this binder
				throw new NotSupportedException("errorcode.notsupported.copyEntry.complexEntryToMirrored." + (destination.isAclExternallyControlled() ? "net" : "mirrored"));
			}
		}
	}

	private void doCopyChildren(Binder source, Binder destinationParent, Map options) {
		Map params = new HashMap();
		params.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.FALSE);
		params.put(ObjectKeys.INPUT_OPTION_PRESERVE_DOCNUMBER, Boolean.TRUE);
		if (options.containsKey(ObjectKeys.INPUT_OPTION_MOVE_SHARE_ITEMS) && 
				(Boolean)options.get(ObjectKeys.INPUT_OPTION_MOVE_SHARE_ITEMS)) {
			params.put(ObjectKeys.INPUT_OPTION_MOVE_SHARE_ITEMS, (Boolean)options.get(ObjectKeys.INPUT_OPTION_MOVE_SHARE_ITEMS));
		}
		List<Binder> children = source.getBinders();
		for (Binder child : children) {
			// If the user doesn't have rights to copy this binder...
			if (!(testAccess(child, BinderOperation.copyBinder))) {
				// ...skip it.
				continue;
			}
			
			// If the binder is not in the trash...
			if (!(TrashHelper.isBinderPredeleted(child))) {
				// ...recursively copy that too.
				Binder binder = loadBinderProcessor(child).copyBinder(child,
						destinationParent, params);
				doCopyChildren(child, binder, params);
			}
		}
	}
	
	//Change entry types
	// no transaction
	@Override
	public void changeEntryTypes(Long binderId, String oldDefId, final String newDefId) {
		Binder binder = loadBinder(binderId);
		if (!(binder instanceof Folder)) return;
		final Folder folder = (Folder)binder;
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		checkAccess(binder, BinderOperation.manageConfiguration);
		final List<Long> entryIds = getFolderDao().getFolderEntriesByType(zoneId, folder, oldDefId);
		if (!entryIds.isEmpty()) {
			getTransactionTemplate().execute(
					new TransactionCallback() {
						@Override
						public Object doInTransaction(TransactionStatus status) {
							getFolderDao().setFolderEntryType(folder, entryIds, newDefId);
							return null;
						}
					});
			BinderProcessor processor = loadBinderProcessor(binder);
			processor.indexBinder(binder, true);
		}
	}

	// inside write transaction
	@Override
	public Binder setDefinitionsInherited(Long binderId, boolean inheritFromParent) {
		return setDefinitionsInherited(binderId, inheritFromParent, true);
	}
	// inside write transaction
	@Override
	public Binder setDefinitionsInherited(Long binderId, boolean inheritFromParent, boolean doAccessCheck) {
		Binder binder = loadBinder(binderId);
		if (doAccessCheck) {
			checkAccess(binder, BinderOperation.manageConfiguration);
		}
		boolean oldInherit = binder.isDefinitionsInherited();
		if (inheritFromParent != oldInherit) {
			if (inheritFromParent) {
				// remove old mappings
				Map m = binder.getWorkflowAssociations();
				m.clear();
				binder.setWorkflowAssociations(m);
				List l = binder.getDefinitions();
				l.clear();
				binder.setDefinitions(l);
			} else {
				// copy parents definitions to this binder before changing
				// setting
				binder
						.setWorkflowAssociations(binder
								.getWorkflowAssociations());
				binder.setDefinitions(binder.getDefinitions());
			}
			binder.setDefinitionsInherited(inheritFromParent);
		}
		return binder;

	}

	// inside write transaction
	@Override
	public Binder setDefinitions(Long binderId, List<String> definitionIds,
			Map<String, String> workflowAssociations)
			throws AccessControlException {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageConfiguration);
		List definitions = new ArrayList();
		Definition def;
		// Build up new set - domain object will handle associations
		if (definitionIds != null) {
			for (String id : definitionIds) {
				try {
					// should be cached
					def = getCoreDao().loadDefinition(
							id,
							RequestContextHolder.getRequestContext()
									.getZoneId());
					definitions.add(def);
				} catch (NoDefinitionByTheIdException nd) {
				}
			}
		}

		binder.setDefinitions(definitions);
		binder.setDefinitionsInherited(false);

		Map wf = new HashMap();
		if (workflowAssociations != null) {
			for (Iterator iter = workflowAssociations.entrySet().iterator(); iter
					.hasNext();) {
				Map.Entry me = (Map.Entry) iter.next();
				try {
					// should be cached
					def = getCoreDao().loadDefinition(
							(String) me.getValue(),
							RequestContextHolder.getRequestContext()
									.getZoneId());
					wf.put(me.getKey(), def);
				} catch (NoDefinitionByTheIdException nd) {
				}
			}
		}
		binder.setWorkflowAssociations(wf);

		BinderProcessor processor = loadBinderProcessor(binder);
		processor.indexBinder(binder, false);
		
		return binder;
	}

	/**
	 * Get tags owned by this binder or current user
	 */
	@Override
	public Collection<Tag> getTags(Binder binder) {
		// have binder - so assume read access
		// bulk load tags
		return getCoreDao().loadEntityTags(
				binder.getEntityIdentifier(),
				RequestContextHolder.getRequestContext().getUser()
						.getEntityIdentifier());
	}

	/**
	 * Add a new tag, to binder
	 */
	// inside write transaction
	@Override
	public Tag [] setTag(Long binderId, String newTag, boolean community) {
		Binder binder = loadBinder(binderId);
		if (community)
			checkAccess(binder, BinderOperation.manageTag);
		if (Validator.isNull(newTag))
			return null;
		Collection<String> newTags = TagUtil.buildTags(newTag);
		if (newTags.size() == 0)
			return null;
		User user = RequestContextHolder.getRequestContext().getUser();
		EntityIdentifier uei = user.getEntityIdentifier();
		EntityIdentifier bei = binder.getEntityIdentifier();
        List<Tag> tags = new ArrayList<Tag>();
		for (String tagName : newTags) {
			Tag tag = new Tag();
			// community tags belong to the binder - don't care who created it
			if (!community)
				tag.setOwnerIdentifier(uei);
			tag.setEntityIdentifier(bei);
			tag.setPublic(community);
			tag.setName(tagName);
			getCoreDao().save(tag);
            tags.add(tag);
		}
		loadBinderProcessor(binder).indexBinder(binder, false);
        return tags.toArray(new Tag[tags.size()]);
	}

	/**
	 * Delete a tag on this binder
	 */
	// inside write transaction
	@Override
	public void deleteTag(Long binderId, String tagId) {
		Binder binder = loadBinder(binderId);
		Tag tag;
		try {
			tag = coreDao.loadTag(tagId, binder.getZoneId());
		} catch (Exception ex) {
			return;
		}
		if (tag.isPublic())
			checkAccess(binder, BinderOperation.manageTag);
		else if (!tag.isOwner(RequestContextHolder.getRequestContext()
				.getUser()))
			return;
		getCoreDao().delete(tag);
		loadBinderProcessor(binder).indexBinder(binder, false);
	}

	// inside write transaction
	@Override
	public void setSubscription(User user, Long binderId, Map<Integer, String[]> styles) {
		Binder binder;
		boolean deleteSub = ((null == styles) || styles.isEmpty());
		if (deleteSub)
		     binder = getBinderWithoutAccessCheck(binderId);	// We allow a subscription to be deleted without access to the item.
		else binder = getBinder(                  binderId);	// getBinder() does read check.
		
		Subscription s = getProfileDao().loadSubscription(user.getId(), binder.getEntityIdentifier());
		
		if (deleteSub) {
			if (s != null) {
				getCoreDao().delete(s);
			}
		}
		else if (s == null) {
			s = new Subscription(user.getId(), binder.getEntityIdentifier());
			s.setStyles(styles);
			getCoreDao().save(s);
		}
		else {
			s.setStyles(styles);
		}
	}

	// inside write transaction
	@Override
	public void setSubscription(Long binderId, Map<Integer, String[]> styles) {
		// Always use the initial form of the method.
		setSubscription(RequestContextHolder.getRequestContext().getUser(), binderId, styles);
	}
	
	/**
	 * Returns a user's subscriptions to a binder.
	 * 
	 * @param user
	 * @param binder
	 * 
	 * @return
	 */
	@Override
	public Subscription getSubscription(User user, Binder binder) {
		return getProfileDao().loadSubscription(user.getId(), binder.getEntityIdentifier());
	}
	
	@Override
	public Subscription getSubscription(Binder binder) {
		// Always use the initial form of the method.
		return getSubscription(RequestContextHolder.getRequestContext().getUser(), binder);
	}

	@Override
	@Deprecated
    public Map executeSearchQuery(Criteria crit, int offset, int maxResults) {
		return this.executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);
	}

	@Override
	public Map executeSearchQuery(Criteria crit, int searchMode, int offset, int maxResults, List<String> fieldNames) {
		return executeSearchQuery(crit, searchMode, offset, maxResults, fieldNames, false);
	}
	@Override
	public Map executeSearchQuery(Criteria crit, int searchMode, int offset, int maxResults, List<String> fieldNames, boolean preDeleted) {
		return executeSearchQuery(crit.toQuery(), searchMode, offset, maxResults, fieldNames, preDeleted);
	}

	@Override
	public Map executeSearchQuery(Criteria crit, int searchMode, int offset, int maxResults, List<String> fieldNames, boolean preDeleted, boolean ignoreAcls) {
		return executeSearchQuery(crit.toQuery(), searchMode, offset, maxResults, fieldNames, preDeleted, ignoreAcls);
	}

	@Override
	public Map executeSearchQuery(Criteria crit, int searchMode, int offset, int maxResults, List<String> fieldNames,
			Long asUserId) {
		return executeSearchQuery(crit, searchMode, offset, maxResults, fieldNames, asUserId, false);
	}
	@Override
	public Map executeSearchQuery(Criteria crit, int searchMode, int offset, int maxResults, List<String> fieldNames,
			Long asUserId, boolean preDeleted, boolean ignoreAcls) {
		return executeSearchQuery(crit.toQuery(), searchMode, offset, maxResults, fieldNames, asUserId, preDeleted, ignoreAcls);
	}

	@Override
	public Map executeSearchQuery(Criteria crit, int searchMode, int offset, int maxResults, List<String> fieldNames,
			Long asUserId, boolean preDeleted) {
		return executeSearchQuery(crit.toQuery(), searchMode, offset, maxResults, fieldNames, asUserId, preDeleted);
	}

	@Override
	public Map executeSearchQuery(Document query, int searchMode, int offset, int maxResults, List<String> fieldNames) {
		return executeSearchQuery(query, searchMode, offset, maxResults, fieldNames, false);
	}
	@Override
	public Map executeSearchQuery(Document query, int searchMode, int offset, int maxResults, List<String> fieldNames, boolean preDeleted) {
		return executeSearchQuery(query, searchMode, offset, maxResults, fieldNames, preDeleted, false);
	}
	@Override
	public Map executeSearchQuery(Document query, int searchMode, int offset, int maxResults, List<String> fieldNames, boolean preDeleted, boolean ignoreAcls) {
		// Create the Lucene query
		QueryBuilder qb = new QueryBuilder(!ignoreAcls, preDeleted);
		SearchObject so = qb.buildQuery(query);

		return _executeSearchQuery(so, searchMode, offset, maxResults, fieldNames);
	}

	@Override
	public Map executeSearchQuery(Document query, int searchMode, int offset, int maxResults, List<String> fieldNames,
			Long asUserId) {
		return executeSearchQuery(query, searchMode, offset, maxResults, fieldNames, false);
	}
	@Override
	public Map executeSearchQuery(Document query, int searchMode, int offset, int maxResults, List<String> fieldNames,
			Long asUserId, boolean preDeleted) {
		return executeSearchQuery(query, searchMode, offset, maxResults, fieldNames, asUserId, preDeleted, false);
	}
	@Override
	public Map executeSearchQuery(Document query, int searchMode, int offset, int maxResults, List<String> fieldNames,
			Long asUserId, boolean preDeleted, boolean ignoreAcls) {
		// Create the Lucene query
		QueryBuilder qb = new QueryBuilder(!ignoreAcls, preDeleted, asUserId);
		SearchObject so = qb.buildQuery(query);

		return _executeSearchQuery(so, searchMode, offset, maxResults, fieldNames);
	}

	@Override
	public Map executeSearchQuery(Document searchQuery, int searchMode, Map options) {
		SearchObject so;
		
		Document qTree = SearchUtils.getInitalSearchDocument(searchQuery,
				options);
		SearchUtils.getQueryFields(qTree, options);

		// Create the Lucene query
		QueryBuilder qb;
		if (options.containsKey(ObjectKeys.SEARCH_PRE_DELETED)) {
			qb = new QueryBuilder(true, true);
		} else {
			qb = new QueryBuilder(true, false);
		}

		so = qb.buildQuery(qTree);
		
		// Set the sort order
		SortField[] fields = SearchUtils.getSortFields(options);
		so.setSortBy(fields);

		if (logger.isTraceEnabled() && searchQuery != null) {
			logger.trace("Query in executeSearchQuery: "
					+ searchQuery.asXML());
		}

		int maxResults = 10;
		int offset = 0;
		if (options != null) {
			if (options.containsKey(ObjectKeys.SEARCH_MAX_HITS))
				maxResults = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
			if (options.containsKey(ObjectKeys.SEARCH_OFFSET))
				offset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);
		}

		return _executeSearchQuery(so, searchMode, offset, maxResults, null);
	}

	protected Map _executeSearchQuery(SearchObject so, int searchMode, int offset, int maxResults, List<String> fieldNames) {
		Hits hits = executeLuceneQueryInternal(so, searchMode, offset, maxResults, fieldNames);
		return returnSearchQuery(hits);
	}

	protected Map returnSearchQuery(Hits hits) {
		List entries = new ArrayList();
		entries = SearchUtils.getSearchEntries(hits);
		SearchUtils.extendPrincipalsInfo(entries, getProfileDao(),
				Constants.CREATORID_FIELD);

		Map retMap = new HashMap();
		retMap.put(ObjectKeys.SEARCH_ENTRIES, entries);
		retMap.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(hits
				.getTotalHits()));
		retMap.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new Integer(hits
				.length()));

		retMap.put(ObjectKeys.TOTAL_SEARCH_COUNT, new Integer(hits.getTotalHits()));

        retMap.put(ObjectKeys.SEARCH_COUNT_TOTAL_APPROXIMATE, new Boolean(hits.isTotalHitsApproximate()));
        retMap.put(ObjectKeys.SEARCH_THERE_IS_MORE,           new Boolean(hits.getThereIsMore()        ));
		
		return retMap;
	}

	private Hits executeLuceneQueryInternal(SearchObject so, int searchMode, int offset, int maxResults, List<String> fieldNames) {
		Hits hits = new Hits(0);

		Query soQuery = so.getLuceneQuery(); // Get the query into a variable to avoid
		// doing this very slow operation twice

		if (logger.isTraceEnabled()) {
			logger.trace("Query in executeLuceneQuery: "
					+ soQuery.toString());
		}

		LuceneReadSession luceneSession = getLuceneSessionFactory()
				.openReadSession();
		try {
			hits = luceneSession.search(RequestContextHolder.getRequestContext().getUserId(),
					so.getBaseAclQueryStr(), so.getExtendedAclQueryStr(), searchMode, soQuery, fieldNames, so.getSortBy(), offset,
					maxResults);
		} catch (RuntimeException e) {
			logger.error("Error searching index", e);
			throw e;
		} finally {
			luceneSession.close();
		}
		return hits;

	}

	private Hits executeNetFolderLuceneQuery(SearchObject so, int searchMode, int offset, int maxResults, Binder parentBinder, boolean allowJits) {
		Hits hits = new Hits(0);

		LuceneReadSession luceneSession = getLuceneSessionFactory()
				.openReadSession();
		try {
			hits = SearchUtils.searchFolderOneLevelWithInferredAccess(luceneSession, RequestContextHolder.getRequestContext().getUserId(),
					so, searchMode, offset,
					maxResults, parentBinder, allowJits);
		} catch (RuntimeException e) {
			logger.error("Error searching index", e);
			throw e;
		} finally {
			luceneSession.close();
		}
		return hits;

	}

	@Override
	public List<Map> getSearchTags(String wordroot, String type) {
		ArrayList tags;

		User user = RequestContextHolder.getRequestContext().getUser();
		SearchObject so = null;
		if (!user.isSuper()) {
			// Top of query doc
			Document qTree = DocumentHelper.createDocument();
			qTree.addElement(Constants.QUERY_ELEMENT);
			// Create the query
			QueryBuilder qb = new QueryBuilder(true, false);
			so = qb.buildQuery(qTree);
		}
		LuceneReadSession luceneSession = getLuceneSessionFactory()
				.openReadSession();

		try {
			tags = luceneSession.getTags(so != null ? so.getAclQueryStr() : null,
					wordroot, type);
		} finally {
			luceneSession.close();
		}
		ArrayList tagList = new ArrayList();
		if (tags != null) {
			for (int j = 0; j < tags.size(); j++) {
				HashMap tag = new HashMap();
				String strTag = (String) tags.get(j);
				tag.put(WebKeys.TAG_NAME, strTag);
				tagList.add(tag);
			}
		}
		return tagList;
	}

	@Override
	public List<Map> getSearchTagsWithFrequencies(String wordroot, String type) {
		ArrayList tags;

		User user = RequestContextHolder.getRequestContext().getUser();
		SearchObject so = null;
		if (!user.isSuper()) {
			// Top of query doc
			Document qTree = DocumentHelper.createDocument();
			qTree.addElement(Constants.QUERY_ELEMENT);
			// Create the query
			QueryBuilder qb = new QueryBuilder(true, false);
			so = qb.buildQuery(qTree);
		}
		LuceneReadSession luceneSession = getLuceneSessionFactory()
				.openReadSession();

		try {
			tags = luceneSession.getTagsWithFrequency(so != null ? so.getAclQueryStr() : null, wordroot, type);
		} finally {
			luceneSession.close();
		}
		ArrayList tagList = new ArrayList();
		if (tags != null) {
			for (int j = 0; j < tags.size(); j++) {
				HashMap tag = new HashMap();
				TagObject tagObj = (TagObject) tags.get(j);
				tag.put(WebKeys.TAG_NAME_FREQ, tagObj);
				tagList.add(tag);
			}
		}
		return tagList;
	}

	@Override
	public Binder getBinderByPathName(String pathName)
			throws AccessControlException {
		List<Binder> binders = getCoreDao().loadBindersByPathName(pathName, 
				RequestContextHolder.getRequestContext().getZoneId());

		// only maximum of one matching non-deleted binder
		Long binderId = null;
		for (Binder binder : binders) {
			if (binder.isDeleted())
				continue;
			else if(binder instanceof Folder) {
				if(((Folder) binder).isPreDeleted())
					continue;
			}
			else if(binder instanceof Workspace) {
				if(((Workspace) binder).isPreDeleted())
					continue;						
			}
			binderId = binder.getId();
			break;
		}

		if(binderId != null)
			return this.getBinder(binderId); // This will do all the access checking necessary
		else 
			return null;
	}

    @Override
	public Binder getBinderByParentAndTitle(Long parentBinderId, String title) throws AccessControlException {
        Binder binder = getCoreDao().loadBinderByParentAndName(parentBinderId, title,
                RequestContextHolder.getRequestContext().getZoneId());
        if (binder!=null) {
            if (binder.isDeleted() || (binder instanceof Folder && ((Folder)binder).isPreDeleted())
                    || (binder instanceof Workspace && ((Workspace)binder).isPreDeleted())) {
                binder = null;
            } else {
                try {
                    getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
                } catch(AccessControlException ace) {
                    try {
                        getAccessControlManager().checkOperation(binder, WorkAreaOperation.VIEW_BINDER_TITLE);
                    } catch(AccessControlException ace2) {
                        throw ace;
                    }
                }
            }
        }
        return binder;
    }

    /**
     * Construct the description for the "team group" associated with the given binder.
     */
    private String constructTeamGroupDesc( Binder binder )
    {
    	String desc = "";
    	
    	if ( binder != null )
    	{
    		String binderTitle;
    		String binderPath;
    		int len;
    		
    		len = SPropsUtil.getInt( "team.group.desc.maxlen", 100 );
    		desc = NLT.get( "team.group.desc" );
			len -= desc.length();
			
			binderTitle = binder.getTitle();
			if ( len > 0 && binderTitle != null )
			{
				if ( binderTitle.length() > len )
					binderTitle = binderTitle.substring( 0, len ) + "...";
				
				desc += " " + binderTitle;

				len -= binderTitle.length();
			}

			binderPath = binder.getPathName();
			if ( len > 0 && binderPath != null )
			{
				if ( binderPath.length() > len )
					binderPath = binderPath.substring( 0, len ) + "...";
				
				desc += "\n(" + binderPath + ")";
			}
    	}
    	
    	return desc;
    }
    
    /**
     * Construct the name of the "team group" associated with the given binder
     */
    private String constructTeamGroupName( Binder binder )
    {
    	String name = "";
    	
    	if ( binder != null )
    	{
    		String title;
    		int len;
    		
    		len = SPropsUtil.getInt( "team.group.name.maxlen", 80 );
    		title = binder.getTitle();
    		if ( title != null )
    		{
    			if ( title.length() > len )
        			title = title.substring( 0, len ) + "... ";
    		}
    		else
    			title = " ";
    		
			name = "team - " + title + " (" + binder.getId() + ")"; 
    	}

    	return name;
    }
    
    /**
     * Construct the title of the "team group" associated with the given binder.
     */
    private String constructTeamGroupTitle( Binder binder )
    {
    	String title = "";
    	
    	if ( binder != null )
    	{
    		String binderTitle;
    		String binderPath;
    		int len;
    		
    		len = SPropsUtil.getInt( "team.group.title.maxlen", 100 );
			title = NLT.get( "team.group" );
			len -= title.length();
			
			binderTitle = binder.getTitle();
			if ( len > 0 && binderTitle != null )
			{
				if ( binderTitle.length() > len )
					binderTitle = binderTitle.substring( 0, len ) + "...";
				
				title += " " + binderTitle;

				len -= binderTitle.length();
			}

			binderPath = binder.getPathName();
			if ( len > 0 && binderPath != null )
			{
				if ( binderPath.length() > len )
					binderPath = binderPath.substring( 0, len ) + "...";
				
				title += " (" + binderPath + ")";
			}
    	}
    	
    	return title;
    }
    
    /**
     * Create a "team group" for the given binder.
     */
    private Group createTeamGroup( final Binder binder )
    {
		RunasCallback callback;
		Object retValue;

		callback = new RunasCallback()
		{
			@Override
			public Object doAs()
			{
				String name;
		    	String title;
		    	String desc;
				HashMap<String, Object> inputMap = new HashMap<String, Object>();
				Group newGroup = null;
				GroupType groupType;
				
				name = constructTeamGroupName( binder );
				title = constructTeamGroupTitle( binder );
				desc = constructTeamGroupDesc( binder );
				groupType = GroupType.team;
				
				inputMap.put( ObjectKeys.FIELD_PRINCIPAL_NAME, name );
				inputMap.put( ObjectKeys.FIELD_ENTITY_TITLE, title );
				inputMap.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc );
				inputMap.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf( Description.FORMAT_NONE ) );  
				inputMap.put( ObjectKeys.FIELD_GROUP_DYNAMIC, Boolean.FALSE );
				inputMap.put( ObjectKeys.FIELD_GROUP_LDAP_QUERY, null );
				inputMap.put( ObjectKeys.FIELD_GROUP_TYPE, groupType );

				// Add the identity information.
				IdentityInfo identityInfo = new IdentityInfo();
				identityInfo.setFromLocal( true );
				identityInfo.setInternal( true );
				inputMap.put( ObjectKeys.FIELD_USER_PRINCIPAL_IDENTITY_INFO, identityInfo );
			
				MapInputData inputData = new MapInputData(inputMap);

				HashMap<String, Object> emptyFileMap = new HashMap<String, Object>();
				newGroup = null;
				try
				{
					// Create the new group.  Is this a debug request to create multiple groups?
					ProfileModule pm = getProfileModule();
				
					newGroup = pm.addGroup( null, inputData, emptyFileMap, null );
					
					// Update the binder's teamGroupId field with the id of the group we just created.
					setBinderTeamGroupId( binder, newGroup.getId() );
				}
				catch( Exception ex )
				{
					logger.error( "Unable to create team group: " + name, ex );
				}
				
				return newGroup;
			}
		};
		
		// Do the necessary work as the admin user.
		retValue = RunasTemplate.runasAdmin(
										callback,
										RequestContextHolder.getRequestContext().getZoneName() );
		
		if ( retValue != null && retValue instanceof Group )
			return (Group) retValue;
		
		return null;
    }
    
    /**
     * Delete the team group associated with the given binder.
     */
    private void deleteTeamGroup( final Binder binder, final Group group )
    {
		RunasCallback callback;
		Object retValue;

		callback = new RunasCallback()
		{
			@Override
			public Object doAs()
			{
				Boolean retValue;
				
				try
				{
					getProfileModule().deleteEntry( group.getId(), null );
					
					// Set the binder's teamGroupId to null
					setBinderTeamGroupId( binder, null );

					retValue = Boolean.TRUE;
				}
				catch ( Exception ex )
				{
					logger.error( "Unable to delete team group: " + group.getName(), ex );
					retValue = Boolean.FALSE;
				} 
				
				return retValue;
			}
		};

		// Do the necessary work as the admin user.
		retValue = RunasTemplate.runasAdmin(
										callback,
										RequestContextHolder.getRequestContext().getZoneName() );
		
    }
    
    /**
     * If the given binder has a "team group" associated with it, rename the group using the
     * binder's title.
     */
    @Override
    public void fixupTeamGroupName( final Binder binder )
    {
    	Long groupId;
    	
    	// Is there a "team group" associated with this binder?
    	groupId = binder.getTeamGroupId();
    	if ( groupId != null )
    	{
    		final Group group;
    		
    		// Yes
    		group = getTeamGroup( binder );
    		if ( group != null )
    		{
				RunasCallback callback;
				Object retValue;

				callback = new RunasCallback()
				{
					@Override
					public Object doAs()
					{
						Map updates;
						Boolean retValue;
						String name;
						String title;
						String desc;

						updates = new HashMap();
						name = constructTeamGroupName( binder );
						title = constructTeamGroupTitle( binder );
						desc = constructTeamGroupDesc( binder );
						updates.put( ObjectKeys.FIELD_PRINCIPAL_NAME, name );
						updates.put( ObjectKeys.FIELD_ENTITY_TITLE, title );
						updates.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc );
						updates.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf( Description.FORMAT_NONE ) );  
						
						try
						{
							getProfileModule().modifyEntry( group.getId(), new MapInputData( updates ) );
							
							retValue = Boolean.TRUE;
						}
			   			catch ( Exception ex )
			   			{
			   				logger.error( "Unable to fixup group team name: " + name, ex );
			   				retValue = Boolean.FALSE;
			   			}
						
						return retValue;
					}
				};

				// Do the necessary work as the admin user.
				retValue = RunasTemplate.runasAdmin(
												callback,
												RequestContextHolder.getRequestContext().getZoneName() );
    		}
    	}
    }
    
    /**
     * Return the "team group" that holds the membership for this binder.
     */
    private Group getTeamGroup( Binder binder )
    {
    	Group teamGroup = null;
    	
    	if ( binder != null )
    	{
    		Long groupId;
    		
    		groupId = binder.getTeamGroupId();
    		if ( groupId != null )
    		{
    			try
    			{
    				ArrayList<Long> groupIds;
    				List<Group> groups;
				    User user = RequestContextHolder.getRequestContext().getUser();
    				
    				groupIds = new ArrayList<Long>();
    				groupIds.add( groupId );
    				
   			       	groups = getProfileDao().loadGroups( groupIds, user.getZoneId() );

    				if ( groups != null && groups.size() > 0 )
    					teamGroup = groups.get( 0 );
    			}
    			catch ( Exception ex )
    			{
    				logger.error( "In getTeamGroup() unable to get the group for the binder: " + binder.getTitle(), ex );
    			}
    		}
    	}
    	
    	return teamGroup;
    }
    
    /**
     * Return the team member ids from the property stored on the Binder object.
     * This is how we used to store team membership until we switched over and started using a
     * "team group" to store group membership.
     * 
     * This method will only read team membership if membership is not inherited.
     * 
     * The code that converts team membership from being stored in a property to being stored in
     * a "team group" needs this method.
     */
    public Set<Long> getTeamMemberIdsDeprecated( Binder binder )
    {
    	String members = null;

    	if ( binder.isTeamMembershipInherited() == false )
    		members = (String)binder.getProperty( ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS );
    	
    	return LongIdUtil.getIdsAsLongSet( members );
    }
    
	/**
	 * Return whether the given binder has team members associated with it either directly or through inheritence
	 */
    @Override
	public boolean doesBinderHaveTeamMembers( Binder binder )
    {
    	Set<Long> setOfMemberIds;
    	
    	setOfMemberIds = getTeamMemberIds( binder );
    	if ( setOfMemberIds == null || setOfMemberIds.size() == 0 )
    		return false;
    	
    	return true;
    }
	
    /**
     * Return the team member ids
     * @return
     */
    @Override
	public Set<Long> getTeamMemberIds( Binder binder )
	{
    	Group teamGroup;
    	HashSet<Long> setOfMemberIds;
    	
    	if ( !binder.isRoot() && binder.isTeamMembershipInherited() )
    		return getTeamMemberIds( binder.getParentBinder() );
    	
    	setOfMemberIds = new HashSet<Long>();

    	teamGroup = getTeamGroup( binder );
    	if ( teamGroup != null )
    	{
    		List<Principal> listOfMembers;
    		
    		listOfMembers = teamGroup.getMembers();
    		if ( listOfMembers != null && listOfMembers.size() > 0 )
    		{
    			for ( Principal nextGroupMember : listOfMembers )
    			{
    				setOfMemberIds.add( nextGroupMember.getId() );
    			}
    		}
    	}

    	return setOfMemberIds;
    }
    
    
    /**
     * 
     */
	@Override
	public Set<Long> getTeamMemberIds( Long binderId, boolean explodeGroups )
	{
		// getBinder does read check
		Binder binder = getBinder( binderId );
		
		Set ids = getTeamMemberIds( binder );
		
		// explode groups
		if ( explodeGroups )
			return getProfileDao().explodeGroups( ids, binder.getZoneId() );
		
		return ids;
	}

    /**
     * Return acl index string representing team membership
     * @return
     */
	@Override
    public String getTeamMemberString( Binder binder )
	{
		Set<Long> setOfMemberIds;
		
    	if ( !binder.isRoot() && binder.isTeamMembershipInherited() )
    		return getTeamMemberString( binder.getParentBinder() );
    	
    	setOfMemberIds = getTeamMemberIds( binder );
    	if ( setOfMemberIds != null && setOfMemberIds.size() > 0 )
    		return LongIdUtil.getIdsAsString( setOfMemberIds );

   		return Constants.EMPTY_ACL_FIELD;
    }

    /**
     * 
     */
    @Override
	public SortedSet<Principal> getTeamMembers(
		Binder binder,
		boolean explodeGroups )
	{
    	Set<Long> ids;
    	
		// If have binder , can read so no more access checking is needed
    	ids = getTeamMemberIds( binder );
    	
		// explode groups
		if ( explodeGroups )
			ids = getProfileDao().explodeGroups( ids, binder.getZoneId() );
		
		// turn ids into real Principals
		User user = RequestContextHolder.getRequestContext().getUser();
		Comparator c = new PrincipalComparator(user.getLocale());

		TreeSet<Principal> result = new TreeSet<Principal>(c);
		
		if ( explodeGroups )
		{
			List<User> listOfUsers;
			
			// empty teams can end up in the list of ids, this will prune them
			listOfUsers = getProfileDao().loadUsers( ids, RequestContextHolder.getRequestContext().getZoneId() );
			result.addAll( listOfUsers );
		}
		else
		{
			List<UserPrincipal> listOfUserPrincipals;
			
			listOfUserPrincipals = getProfileDao().loadUserPrincipals(
																	ids,
																	RequestContextHolder.getRequestContext().getZoneId(),
																	true ); 
			result.addAll( listOfUserPrincipals );
		}
		
		return result;
	}

	// no transaction
	@Override
	public void setTeamMembershipInherited(Long binderId, final boolean inherit) {
        setTeamMembershipInherited(binderId, inherit, true);
    }
	@Override
	public void setTeamMembershipInherited(Long binderId, final boolean inherit, final boolean doAccessCheck) {
		final Binder binder = loadBinder(binderId);
        if (doAccessCheck) {
		    checkAccess(binder, BinderOperation.manageTeamMembers);
        }
		Boolean index = (Boolean) getTransactionTemplate().execute(
				new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus status) {
						Set oldMbrs = getTeamMemberIds( binder );
						if (inherit) {
							setTeamMembers( binder.getId(), null, doAccessCheck);
						} else if (binder.isTeamMembershipInherited()) {
							// going from was inheriting to not inheriting =>
							// copy
							Set ids = new HashSet( getTeamMemberIds( binder ) );
							setTeamMembers( binder.getId(), ids );
						}
						// see if there is a real change
						if (binder.isTeamMembershipInherited() != inherit) {
							binder.setTeamMembershipInherited(inherit);
							if (!(binder instanceof TemplateBinder)) {
								User user = RequestContextHolder
										.getRequestContext().getUser();
								binder.incrLogVersion();
								binder.setModification(new HistoryStamp(user));
								BinderProcessor processor = loadBinderProcessor(binder);
								processor.processChangeLog(binder,
										ChangeLog.ACCESSMODIFY);
								// Always reindex top binder to update the team
								// members field
								processor.indexBinder(binder, false);
								// just changed from not inheritting to inherit
								// = need to update index acls
								// if changed from inherit to not, acls remains
								// the same
								if (inherit
										&& !oldMbrs.equals( getTeamMemberIds( binder )))
									return Boolean.TRUE;
							}
						}
						return Boolean.FALSE;
					}
				});
		// only index if change occured
		if (index) {
			loadBinderProcessor(binder).indexTeamMembership(binder, true);
		}

	}

	// no transaction
	@Override
	public void setTeamMembers(Long binderId, final Collection<Long> memberIds)
			throws AccessControlException {
		setTeamMembers(binderId, memberIds, true);
	}

	private void setTeamMembers(Long binderId, final Collection<Long> memberIds, boolean doAccessCheck)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);

		if (doAccessCheck) {
			checkAccess(binder, BinderOperation.manageTeamMembers);
		}

		// If membership didn't change then bail.
		if ( getTeamMemberIds( binder ).equals(memberIds) )
			return;

		//See if the guest user is included in the list
		User guest = getProfileModule().getGuestUser();
		if ( doAccessCheck && memberIds != null && memberIds.contains(guest.getId())) {
			//If adding guest to a team, the user must be allowed to do it from the zone
			Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
			getAccessControlManager().checkOperation(zoneConfig, WorkAreaOperation.ADD_GUEST_ACCESS);
		}
		
		RunasCallback callback;
		Object retValue;

		callback = new RunasCallback()
		{
			@Override
			public Object doAs()
			{
				final BinderProcessor processor = loadBinderProcessor(binder);
				TransactionCallback transCallback;
				
				transCallback = new TransactionCallback()
				{
					@Override
					public Object doInTransaction( TransactionStatus status )
					{
						// Modify the group's membership.
						setTeamMembers( binder, memberIds );

						binder.setTeamMembershipInherited( false );
					
						if ( !(binder instanceof TemplateBinder) )
						{
							User user = RequestContextHolder.getRequestContext().getUser();
							binder.incrLogVersion();
							binder.setModification(new HistoryStamp(user));
							processor.processChangeLog(binder, ChangeLog.ACCESSMODIFY);
							return Boolean.TRUE;
						}
						
						return Boolean.FALSE;
					}
				};
				
				Boolean index = (Boolean) getTransactionTemplate().execute( transCallback );
				
				if (index) {
					// Always reindex top binder to update the team members field
					processor.indexBinder(binder, false);

					// update readAcl on binders and entries
					processor.indexTeamMembership(binder, true);
				}
				
				return index;
			}
		};

		// Do the necessary work as the admin user.
		retValue = RunasTemplate.runasAdmin(
										callback,
										RequestContextHolder.getRequestContext().getZoneName() );
		
	}

	/**
	 * This should be removed after we get the team membership stored in a "team group" working 
	 */
	public void setTeamMembersDeprecated( Binder binder, Collection<Long> memberIds)
	{
    	//setting inherited flag handled separate
    	if ( (memberIds == null) || memberIds.isEmpty() )
    		binder.removeProperty( ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS );
    	else
    		binder.setProperty( ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS, LongIdUtil.getIdsAsString( memberIds ) );
    }
	
	/**
	 * 
	 */
	private boolean setTeamMembers( Binder binder, final Collection<Long> memberIds )
	{
		Group teamGroup;
		final Group finalTeamGroup;
		boolean result = true;

		teamGroup = getTeamGroup( binder );
		
		// Do we have any group members?
		if ( memberIds != null && memberIds.size() > 0 )
		{
			final boolean workingWithExistingGroup;

			// Yes
			// Do we have a team group?
			if ( teamGroup == null )
			{
				// No, create one.
				teamGroup = createTeamGroup( binder );

				workingWithExistingGroup = false;
			}
			else
				workingWithExistingGroup = true;

			finalTeamGroup = teamGroup;

			if ( finalTeamGroup != null )
			{
				RunasCallback callback;
				Object retValue;

				callback = new RunasCallback()
				{
					@Override
					public Object doAs()
					{
						Map updates;
						SortedSet<Principal> principals;
						Boolean retValue;

						principals = getProfileModule().getPrincipals( memberIds );

						updates = new HashMap();

						if ( principals != null && principals.size() > 0 )
						{
							// Are we working with an existing group?
							if ( workingWithExistingGroup )
							{
								IdentityInfo identityInfo;

								// Yes
								// Does the group allow external users/groups as members?
								identityInfo = finalTeamGroup.getIdentityInfo();
								if ( identityInfo.isInternal() )
								{
									Iterator<Principal> iterator;

									// No
									// Remove any external users/groups from the membership
									iterator = principals.iterator();
									while ( iterator.hasNext() )
									{
										Principal nextPrincipal;

										nextPrincipal = iterator.next();

										identityInfo = nextPrincipal.getIdentityInfo();
										if ( identityInfo.isInternal() == false )
										{
											iterator.remove();
										}
									}
								}
							}
							else
							{
								// No, we are working with a new group we just created.
								// Are there any external members?
								for ( Principal nextPrincipal : principals )
								{
									IdentityInfo identityInfo;

									identityInfo = nextPrincipal.getIdentityInfo();
									if ( identityInfo.isInternal() == false )
									{
										// Mark the team group as external.
										identityInfo = finalTeamGroup.getIdentityInfo();
										identityInfo.setInternal( false );
										updates.put( ObjectKeys.FIELD_USER_PRINCIPAL_IDENTITY_INFO, identityInfo );

										break;
									}
								}
							}
						}

						updates.put( ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, principals );

						try
						{
							getProfileModule().modifyEntry( finalTeamGroup.getId(), new MapInputData( updates ) );
							
							retValue = Boolean.TRUE;
						}
			   			catch ( Exception ex )
			   			{
			   				logger.error( "Error saving team membership for group: " + finalTeamGroup.getName(), ex );
			   				retValue = Boolean.FALSE;
			   			}
						
						return retValue;
					}
				};

				// Do the necessary work as the admin user.
				retValue = RunasTemplate.runasAdmin(
												callback,
												RequestContextHolder.getRequestContext().getZoneName() );
				
				result = ((Boolean)retValue).booleanValue();
			}
		}
		else
		{
			// No members
			// Do we have a team group?
			if ( teamGroup != null )
			{
				// Yes, because there are no members we don't need the group.  Delete the team group
				deleteTeamGroup( binder, teamGroup );
				teamGroup = null;
			}
			
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Find all binders that have team membership defined in a property on the binder and the
	 * team membership is not inherited and there is at least one member defined in the team. 
	 */
	private ArrayList<Binder> findAllBindersWithTeamMembershipDeprecated()
	{
		List<Binder> listOfAllBinders;
		ArrayList<Binder> listOfBindersWithTeamMembership;
		
		listOfBindersWithTeamMembership = new ArrayList<Binder>();
		listOfAllBinders = null;
		
		// Find all binders
		{
	    	FilterControls filter;

	    	// Don't use the search index to look for binders!
	    	filter = new FilterControls();
	    	filter.add( "deleted", Boolean.FALSE );
	    	filter.add( "teamMembershipInherited", Boolean.FALSE );
			listOfAllBinders = getCoreDao().loadObjects( Binder.class, filter, RequestContextHolder.getRequestContext().getZoneId() );
			if ( listOfAllBinders != null )
			{
				logger.info( "------------> Found " + listOfAllBinders.size() + " binders that have might team membership that needs to be upgraded." );
			}
		}
		
		// Look for binders that have team membership
		if ( listOfAllBinders != null && listOfAllBinders.size() > 0 )
		{
			Iterator<Binder> binderIter;
			
			binderIter = listOfAllBinders.iterator();
			while ( binderIter.hasNext() )
			{
				Binder binder;
				Set<Long> membership;

				binder = binderIter.next();
				
				// Does this binder have team membership defined?
				membership = getTeamMemberIdsDeprecated( binder );
				if ( membership != null && membership.size() > 0 )
				{
					// Yes
					listOfBindersWithTeamMembership.add( binder );
				}
				else {
					// No - We won't be needing this binder. Kick it out of the session to keep memory usage from going out of control
					getCoreDao().evict(binder);
				}
			}
		}
		
		return listOfBindersWithTeamMembership;
	}
	
	/**
	 * Copy the team membership that is stored in a property on the given binder and create a
	 * "team group" and store the team membership in that group.
	 */
	private void upgradeTeamMembership( Binder binder )
	{
		Set<Long> teamMemberIds;
		
		if ( binder == null )
			return;
		
		logger.info( "Upgrading team membership for binder: " + binder.getTitle() + " (" + binder.getPathName() + " )" );

		// Get the team membership that is stored in a property on the binder.
		teamMemberIds = getTeamMemberIdsDeprecated( binder );
		
		if ( teamMemberIds != null && teamMemberIds.size() > 0 )
		{
			if ( setTeamMembers( binder, teamMemberIds ) )
			{		
				// Remove the team membership from the property
				binder.removeProperty( ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS );
			}
		}
	}
	
	/**
	 * Find all binders that have team membership defined in a property on the binder
	 * and copy the team membership into a "team group".
	 * 
	 * Delete the team membership that is stored in a property on the binder.
	 */
	@Override
	public void upgradeTeamMembership()
	{
		ArrayList<Binder> listOfBinders;
		
		logger.info( "Upgrading team membership..." );
		
		// Find all binders that are not inheriting team membership and have a least one team member
		listOfBinders = findAllBindersWithTeamMembershipDeprecated();
		
		if ( listOfBinders != null )
		{
			for ( Binder nextBinder : listOfBinders )
			{
				// Move the team membership into a "team group"
				upgradeTeamMembership( nextBinder );
			}
		}
		
		logger.info( "Finished upgrading team membership" );
	}

	// return binders this user is a team_member of
	@Override
	public List<Map> getTeamMemberships(Long userId, List<String> fieldNames) {

		// We use search engine to get the list of binders.
		Criteria crit = new Criteria().add(
				eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER))
				.addOrder(new Order(Constants.SORT_TITLE_FIELD, true));

		// look for user
		Principal prin = RequestContextHolder.getRequestContext().getUser();
		if (!userId.equals(prin.getId())) {
			try {
				prin = getProfileDao().loadPrincipal(userId, prin.getZoneId(),
						true);
			} catch (Exception e) {
				return Collections.EMPTY_LIST;
			}
		}
		Set<Long> ids = getProfileDao().getApplicationLevelPrincipalIds(prin);
		if (ids.isEmpty())
			return Collections.EMPTY_LIST;
		crit.add(in(Constants.TEAM_MEMBERS_FIELD, LongIdUtil
				.getIdsAsStringSet(ids)));
		QueryBuilder qb = new QueryBuilder(true, false);
		SearchObject so = qb.buildQuery(crit.toQuery());

		Hits hits = executeLuceneQueryInternal(so, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, Integer.MAX_VALUE, fieldNames);
		if (hits == null)
			return new ArrayList();
		return SearchUtils.getSearchEntries(hits);
	}
	
    //inside write transaction    	
	@Override
	public void setMyFilesDir(Long binderId, boolean value) {
		//getBinder does read check
		Binder binder = getBinder(binderId);
		// (Bug #879737) - To minimize the possibility of optimistic locking failure
		getCoreDao().refresh(binder);
		binder.setMyFilesDir(value);
	}
	

	// inside write transaction
	@Override
	public void setPosting(Long binderId, String emailAddress, String password) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageMail);
		PostingDef post = binder.getPosting();
		if (Validator.isNull(emailAddress)) {
			// if posting exists for this binder, remove it
			if (post == null)
				return;
			binder.setPosting(null);
			getCoreDao().delete(post);
			return;
		} else {
			// see if it exists already
			emailAddress = emailAddress.toLowerCase();
			// see if assigned to someone else
			if ((post == null) || !emailAddress.equals(post.getEmailAddress())) {
				List results = getCoreDao().loadObjects(PostingDef.class,
						new FilterControls("emailAddress", emailAddress),
						binder.getZoneId());
				if (!results.isEmpty()) {
					// exists, see if it is assigned
					PostingDef oldPost = (PostingDef) results.get(0);
					// if address is assigned, cannot continue
					if (oldPost.getBinder() != null) {
						if (!oldPost.getBinder().equals(binder)) {
							throw new NotSupportedException(
									"errorcode.posting.assigned",
									new String[] { emailAddress });
						}
					}
					if (post != null)
						getCoreDao().delete(post);
					post = oldPost;
				}
			}
		}
		if (post == null) {
			post = new PostingDef();
			post
					.setZoneId(RequestContextHolder.getRequestContext()
							.getZoneId());
			getCoreDao().save(post);
		}
		post.setBinder(binder);
		post.setEnabled(true);
		post.setReplyPostingOption(PostingDef.REPLY_POST_AS_A_REPLY);
		post.setEmailAddress(emailAddress);
		post.setCredentials(password);
		binder.setPosting(post);
	}

	/**
	 * Set the notification definition for a folder.
	 * 
	 * @param id
	 * @param updates
	 * @param principals
	 *            - if null, don't change list.
	 */
	// inside write transaction
	@Override
	public void modifyNotification(Long binderId,
			Collection<Long> principalIds, Map updates) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageMail);
		NotificationDef current = binder.getNotificationDef();
		if (current == null) {
			current = new NotificationDef();
			binder.setNotificationDef(current);
		}
		ObjectBuilder.updateObject(current, updates);
		if (principalIds == null)
			return;
		// Pre-load for performance
		List notifyUsers = getProfileDao().loadUserPrincipals(principalIds,
				binder.getZoneId(), true);
		current.setDistribution(notifyUsers);
	}

	@Override
	public org.dom4j.Document getDomBinderTree(Long id,
			DomTreeBuilder domTreeHelper, int levels)
			throws AccessControlException {
		// getWorkspace does access check
		Binder top = getBinder(id);

		User user = RequestContextHolder.getRequestContext().getUser();
		Comparator c = new BinderComparator(user.getLocale(),
				GwtUIHelper.useSearchTitles() ? BinderComparator.SortByField.searchTitle : BinderComparator.SortByField.title);
		Document wsTree = DocumentHelper.createDocument();
		Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
		buildBinderDomTree(rootElement, top, c, domTreeHelper, levels);
		return wsTree;
	}

	@Override
	public org.dom4j.Document getDomBinderTree(Long topId, Long bottomId,
			DomTreeBuilder domTreeHelper) throws AccessControlException {
		User user = RequestContextHolder.getRequestContext().getUser();
		// getWorkspace does access check
		Binder top = getBinder(topId);
		Binder bottom = (Binder) getCoreDao().loadBinder(bottomId,
				user.getZoneId());

		List<Binder> ancestors = new ArrayList<Binder>();
		Binder parent = bottom;
		// build inverted list of parents
		while ((parent != null) && !parent.equals(top)) {
			ancestors.add(parent);
			parent = (Binder) parent.getParentBinder();
		}
		if (parent == null)
			throw new InternalException("Top is not a parent");
		ancestors.add(parent);
		Comparator c = new BinderComparator(user.getLocale(),
				GwtUIHelper.useSearchTitles() ? BinderComparator.SortByField.searchTitle : BinderComparator.SortByField.title);
		Document wsTree = DocumentHelper.createDocument();
		Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
		for (int i = ancestors.size() - 1; i >= 0; --i) {
			buildBinderDomTree(rootElement, (Binder) ancestors.get(i), c,
					domTreeHelper, 1);
			if (i != 0) {
				parent = ancestors.get(i - 1);
				String parentId = parent.getId().toString();
				Iterator itRootElements = rootElement.selectNodes(
						"./" + DomTreeBuilder.NODE_CHILD).iterator();
				rootElement = null;
				while (itRootElements.hasNext()) {
					Element childNode = (Element) itRootElements.next();
					String id = childNode.attributeValue("id");
					int n = id.indexOf(".");
					if (n >= 0)
						id = id.substring(0, n);
					if (id.equals(parentId)) {
						rootElement = childNode;
						break;
					}
				}
				if (rootElement == null)
					break;
			}
		}
		return wsTree;
	}

	protected void buildBinderDomTree(Element current, Binder top,
			Comparator c, DomTreeBuilder domTreeHelper, int levels) {
		Element next;
		int maxBucketSize = SPropsUtil.getInt("wsTree.maxBucketSize");
		int domTreeType;
		if (EntityIdentifier.EntityType.folder.equals(top.getEntityType())) {
			domTreeType = DomTreeBuilder.TYPE_FOLDER;
		} else {
			domTreeType = DomTreeBuilder.TYPE_WORKSPACE;
		}

		// callback to setup tree
		domTreeHelper.setupDomElement(domTreeType, top, current);
		if (levels == 0)
			return;
		--levels;
		TreeSet ws = new TreeSet(c);
		List searchBinders = null;
		//Always find the binders by searching.
		//  This will get any intermediate binders that may be inaccessible
		if (domTreeHelper.getPage().equals("")) {
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_MAX_HITS, Integer
					.valueOf(SPropsUtil.getInt("wsTree.maxBucketSize")));
			Map searchResults = getBinders(top, options);
			searchBinders = (List) searchResults
					.get(ObjectKeys.SEARCH_ENTRIES);
			int results = (Integer) searchResults
					.get(ObjectKeys.TOTAL_SEARCH_COUNT);
			if (results > SPropsUtil.getInt("wsTree.maxBucketSize")) { // just
				// to
				// get
				// started
				searchResults = buildBinderVirtualTree(current, top,
						domTreeHelper, results, maxBucketSize);
				// If no results are returned, the work was completed in
				// buildBinderVirtualTree and we can exit now
				if (searchResults == null)
					return;
				searchBinders = (List) searchResults
						.get(ObjectKeys.SEARCH_ENTRIES);
			}
		} else {
			// We are looking for a virtual page
			Map searchResults = buildBinderVirtualTree(current, top,
					domTreeHelper, 0, maxBucketSize);
			// If no results are returned, the work was completed in
			// buildBinderVirtualTree and we can exit now
			if (searchResults == null)
				return;
			searchBinders = (List) searchResults
					.get(ObjectKeys.SEARCH_ENTRIES);
		}
		if (domTreeHelper.supportsType(DomTreeBuilder.TYPE_FOLDER, null)) {
			// get folders
			for (int i = 0; i < searchBinders.size(); ++i) {
				Map search = (Map) searchBinders.get(i);
				String entityType = (String) search
						.get(Constants.ENTITY_FIELD);
				if (EntityType.folder.name().equals(entityType)) {
					String sId = (String) search.get(Constants.DOCID_FIELD);
					try {
						Long id = Long.valueOf(sId);
						Object obj = getCoreDao().load(Folder.class, id);
						if (obj != null)
							ws.add(obj);
					} catch (Exception ex) {
						continue;
					}
				}
			}
			for (Iterator iter = ws.iterator(); iter.hasNext();) {
				Folder f = (Folder) iter.next();
				if (f.isDeleted() || f.isPreDeleted())
					continue;
				// Check if the user has "read" access to the folder.
				next = current.addElement(DomTreeBuilder.NODE_CHILD);
				if (domTreeHelper.setupDomElement(
						DomTreeBuilder.TYPE_FOLDER, f, next) == null)
					current.remove(next);
			}
		}
		ws.clear();
		// get workspaces and profiles binder
		for (int i = 0; i < searchBinders.size(); ++i) {
			Map search = (Map) searchBinders.get(i);
			String entityType = (String) search.get(Constants.ENTITY_FIELD);
			if (EntityType.workspace.name().equals(entityType)) {
				String sId = (String) search.get(Constants.DOCID_FIELD);
				try {
					Long id = Long.valueOf(sId);
					Object obj = getCoreDao().load(Workspace.class, id);
					if (obj != null)
						ws.add(obj);
				} catch (Exception ex) {
					continue;
				}
			} else if (EntityType.profiles.name().equals(entityType)) {
				String sId = (String) search.get(Constants.DOCID_FIELD);
				try {
					Long id = Long.valueOf(sId);
					Object obj = getCoreDao().load(ProfileBinder.class, id);
					if (obj != null)
						ws.add(obj);
				} catch (Exception ex) {
					continue;
				}
			}
		}

		for (Iterator iter = ws.iterator(); iter.hasNext();) {
			Workspace w = (Workspace) iter.next();
			if (w.isDeleted() || w.isPreDeleted())
				continue;
			next = current.addElement(DomTreeBuilder.NODE_CHILD);
			buildBinderDomTree(next, w, c, domTreeHelper, levels);
		}
	}

	// Build a list of buckets (or get the final page)
	/**
	 * protected Map buildBinderVirtualTree(Element current, Binder top,
	 * DomTreeBuilder domTreeHelper, int totalHits, int maxBucketSize) { Element
	 * next; int skipLength = maxBucketSize; if (totalHits > maxBucketSize) {
	 * skipLength = totalHits / maxBucketSize; if (skipLength < maxBucketSize)
	 * skipLength = maxBucketSize; }
	 * 
	 * //See if this has a page already set List tuple =
	 * domTreeHelper.getTuple(); String tuple1 = ""; String tuple2 = ""; if
	 * (tuple != null && tuple.size() >= 2) { tuple1 = (String) tuple.get(0);
	 * tuple2 = (String) tuple.get(1); }
	 * 
	 * Document queryTree = DocumentHelper.createDocument(); Element
	 * qTreeRootElement = queryTree.addElement(Constants.QUERY_ELEMENT); Element
	 * qTreeAndElement = qTreeRootElement.addElement(Constants.AND_ELEMENT);
	 * 
	 * Element field = qTreeAndElement.addElement(Constants.FIELD_ELEMENT);
	 * field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.
	 * BINDERS_PARENT_ID_FIELD); Element child =
	 * field.addElement(Constants.FIELD_TERMS_ELEMENT);
	 * child.setText(top.getId().toString());
	 * 
	 * field = qTreeAndElement.addElement(Constants.FIELD_ELEMENT);
	 * field.addAttribute
	 * (Constants.FIELD_NAME_ATTRIBUTE,Constants.DOC_TYPE_FIELD); child =
	 * field.addElement(Constants.FIELD_TERMS_ELEMENT);
	 * child.setText(Constants.DOC_TYPE_BINDER); //Create the Lucene query
	 * QueryBuilder qb = new QueryBuilder(true); SearchObject so =
	 * qb.buildQuery(queryTree); if(logger.isDebugEnabled()) {
	 * logger.debug("Query is: " + queryTree.asXML()); }
	 * 
	 * //Set the sort order SortField[] fields = new SortField[1]; String sortBy
	 * = Constants.NORM_TITLE;
	 * 
	 * fields[0] = new SortField(sortBy, SortField.AUTO, true);
	 * so.setSortBy(fields); Query soQuery = so.getQuery(); //Get the query into
	 * a variable to avoid doing this very slow operation twice
	 * if(logger.isDebugEnabled()) { logger.debug("Query is: " +
	 * soQuery.toString()); }
	 * 
	 * //Before doing the search, create another query in case the buckets are
	 * exhausted Document queryTreeFinal = DocumentHelper.createDocument();
	 * qTreeRootElement = queryTreeFinal.addElement(Constants.QUERY_ELEMENT);
	 * qTreeAndElement = qTreeRootElement.addElement(Constants.AND_ELEMENT);
	 * 
	 * field = qTreeAndElement.addElement(Constants.FIELD_ELEMENT);
	 * field.addAttribute
	 * (Constants.FIELD_NAME_ATTRIBUTE,Constants.BINDERS_PARENT_ID_FIELD); child
	 * = field.addElement(Constants.FIELD_TERMS_ELEMENT);
	 * child.setText(top.getId().toString());
	 * 
	 * field = qTreeAndElement.addElement(Constants.FIELD_ELEMENT);
	 * field.addAttribute
	 * (Constants.FIELD_NAME_ATTRIBUTE,Constants.DOC_TYPE_FIELD); child =
	 * field.addElement(Constants.FIELD_TERMS_ELEMENT);
	 * child.setText(Constants.DOC_TYPE_BINDER);
	 * 
	 * QueryBuilder qbFinal = new QueryBuilder(true); SearchObject
	 * singleBucketSO = qbFinal.buildQuery(queryTreeFinal);
	 * 
	 * Element range = qTreeAndElement.addElement(Constants.RANGE_ELEMENT);
	 * range.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, Constants.NORM_TITLE);
	 * range.addAttribute(Constants.INCLUSIVE_ATTRIBUTE,
	 * Constants.INCLUSIVE_TRUE); Element start =
	 * range.addElement(Constants.RANGE_START); start.setText(tuple1); Element
	 * end = range.addElement(Constants.RANGE_FINISH); end.setText(tuple2);
	 * 
	 * //Create the Lucene query SearchObject soFinal =
	 * qbFinal.buildQuery(queryTreeFinal); if(logger.isDebugEnabled()) {
	 * logger.debug("Final query is: " + queryTreeFinal.asXML()); }
	 * 
	 * //Set the sort order SortField[] fieldsFinal = new SortField[1]; String
	 * sortByFinal = Constants.NORM_TITLE;
	 * 
	 * fieldsFinal[0] = new SortField(sortByFinal, SortField.AUTO, true);
	 * soFinal.setSortBy(fieldsFinal); Query soQueryFinal = soFinal.getQuery();
	 * //Get the query into a variable to avoid doing this very slow operation
	 * twice if(logger.isDebugEnabled()) { logger.debug("Query is: " +
	 * soQueryFinal.toString()); } LuceneReadSession luceneSession =
	 * getLuceneSessionFactory().openReadSession();
	 * 
	 * List results = new ArrayList(); Hits hits = null; try { if (totalHits ==
	 * 0) { //We have to figure out the size of the pool before building the
	 * buckets Hits testHits = luceneSession.search(soQueryFinal,
	 * soFinal.getSortBy(), 0, maxBucketSize); totalHits =
	 * testHits.getTotalHits(); if (totalHits > maxBucketSize) { skipLength =
	 * testHits.getTotalHits() / maxBucketSize; if (skipLength < maxBucketSize)
	 * skipLength = maxBucketSize; } } if (totalHits > skipLength) results =
	 * luceneSession.getSortedTitles(soQuery, tuple1, tuple2, skipLength); if
	 * (results == null || results.size() <= 1) { //We must be at the end of the
	 * buckets; now get the real entries if ("".equals(tuple1) &&
	 * "".equals(tuple2)) { singleBucketSO.setSortBy(fieldsFinal); soQueryFinal
	 * = singleBucketSO.getQuery(); //Get the query into a variable to avoid
	 * doing this very slow operation twice } hits =
	 * luceneSession.search(soQueryFinal, soFinal.getSortBy(), 0, -1); } }
	 * finally { luceneSession.close(); } //See if we are at the end of the
	 * bucket search if (hits != null) { List entries =
	 * SearchUtils.getSearchEntries(hits);
	 * //SearchUtils.extendPrincipalsInfo(entries, getProfileDao());
	 * 
	 * Map retMap = new HashMap();
	 * retMap.put(ObjectKeys.SEARCH_ENTRIES,entries);
	 * retMap.put(ObjectKeys.SEARCH_COUNT_TOTAL, new
	 * Integer(hits.getTotalHits()));
	 * retMap.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new
	 * Integer(hits.length()));
	 * 
	 * domTreeHelper.setPage(""); return retMap; } //Build the virtual tree
	 * String page = domTreeHelper.getPage(); if (!page.equals("")) page += ".";
	 * for (int i = 0; i < results.size(); i++) { List result = (List)
	 * results.get(i); Map skipMap = new HashMap();
	 * skipMap.put(DomTreeBuilder.SKIP_TUPLE, result);
	 * skipMap.put(DomTreeBuilder.SKIP_PAGE, page + String.valueOf(i));
	 * skipMap.put(DomTreeBuilder.SKIP_BINDER_ID, top.getId().toString()); next
	 * = current.addElement(DomTreeBuilder.NODE_CHILD); if
	 * (domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_SKIPLIST, skipMap,
	 * next) == null) current.remove(next); } return null; }
	 **/
	// Build a list of buckets (or get the final page)
	protected Map buildBinderVirtualTree(Element current, Binder top,
			DomTreeBuilder domTreeHelper, int totalHits, int maxBucketSize) {
		boolean debugLogging = logger.isDebugEnabled();
		if (debugLogging) {
			logger.debug("buildBinderVirtualTree(entry): totalHits=" + totalHits + ", maxBucketSize=" + maxBucketSize);
		}
		Element next;
		int skipLength = maxBucketSize;
		if (totalHits > maxBucketSize) {
			skipLength = totalHits / maxBucketSize;
			if (skipLength < maxBucketSize)
				skipLength = maxBucketSize;
		}

		String bucketSortKey;
		String straightSortKey;
		if (GwtUIHelper.useSearchTitles())
		     {bucketSortKey = null;                              straightSortKey = Constants.NORM_TITLE;             }
		else {bucketSortKey = Constants.BINDER_SORT_TITLE_FIELD; straightSortKey = Constants.BINDER_SORT_TITLE_FIELD;}
		
		// See if this has a page already set
		List tuple = domTreeHelper.getTuple();
		String tuple1 = "";
		String tuple2 = "";
		if (tuple != null && tuple.size() >= 2) {
			tuple1 = (String) tuple.get(0);
			tuple2 = (String) tuple.get(1);
		}
		QueryBuilder qb = new QueryBuilder(true, false);

		List results = new ArrayList();
		Hits hits = null;
		LuceneReadSession luceneSession = getLuceneSessionFactory()
				.openReadSession();
		if (debugLogging) {
			logger.debug("buildBinderVirtualTree(building search criteria): tuple1=" + tuple1 + ", tuple2=" + tuple2);
		}
		try {
			Criteria crit = new Criteria().add(
					eq(Constants.BINDERS_PARENT_ID_FIELD, top.getId()
							.toString())).add(
					eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
			if (totalHits == 0) {
				crit.add(between(straightSortKey, tuple1, tuple2))
						.addOrder(new Order(straightSortKey, true));

				// Create the Lucene query
				SearchObject searchObject = qb.buildQuery(crit.toQuery());

				// We have to figure out the size of the pool before building
				// the buckets
				Hits testHits = SearchUtils.searchFolderOneLevelWithInferredAccess(luceneSession, RequestContextHolder.getRequestContext().getUserId(), 
						searchObject, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY,
						0, maxBucketSize, top, true);
				totalHits = testHits.getTotalHits();
				if (totalHits > maxBucketSize) {
					skipLength = testHits.getTotalHits() / maxBucketSize;
					if (skipLength < maxBucketSize)
						skipLength = maxBucketSize;
				}
				if (debugLogging) {
					logger.debug("buildBinderVirtualTree(post pool size calculation): totalHits=" + totalHits + ", skipLength=" + skipLength);
				}
			}
			if (totalHits > skipLength) {
				SearchObject searchObject = qb.buildQuery(crit.toQuery());
				Query query = searchObject.getLuceneQuery(); // Get the query into a
				// variable to avoid
				// doing this very slow
				// operation twice
				if (logger.isTraceEnabled()) {
					logger.trace("Query in buildBinderVirtualTree: " + searchObject.toString());
				}
				// no order here
				if (debugLogging) {
					logger.debug("buildBinderVirtualTree(calling luceneSession.getSortedTitles()): bucketSortKey=" + bucketSortKey + ", tuple1=" + tuple1 + ", tuple2=" + tuple2 + ", skipLength=" + skipLength + ", searchObject=" + searchObject.getLuceneQuery().toString());
				}
				results = luceneSession.getSortedTitles(query, bucketSortKey, tuple1, tuple2,
						skipLength);
			}
			if (results == null || results.size() <= 1) {
				// We must be at the end of the buckets; now get the real
				// entries
				String tuples = null;
				if ("".equals(tuple1) && "".equals(tuple2)) {
					crit.addOrder(new Order(straightSortKey, true));
					if (debugLogging) tuples = "*no tuples*"; 
				} else {
					crit.add(between(straightSortKey, tuple1, tuple2))
							.addOrder(new Order(straightSortKey, true));
					if (debugLogging) tuples = ("tuple1=" + tuple1 + ", tuple2=" + tuple2);

				}
				SearchObject searchObject = qb.buildQuery(crit.toQuery());
				if (debugLogging) {
					logger.debug("buildBinderVirtualTree(end of buckets, get real entries): " + tuples + ", searchObject=" + searchObject.getLuceneQuery().toString());
				}
				hits = SearchUtils.searchFolderOneLevelWithInferredAccess
						(luceneSession, RequestContextHolder.getRequestContext().getUserId(), searchObject, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0,
						-1, top, true);
			}
		} finally {
			luceneSession.close();
		}
		// See if we are at the end of the bucket search
		if (hits != null) {
			List entries = SearchUtils.getSearchEntries(hits);
			// SearchUtils.extendPrincipalsInfo(entries, getProfileDao());

			Map retMap = new HashMap();
			retMap.put(ObjectKeys.SEARCH_ENTRIES, entries);
			retMap.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(hits
					.getTotalHits()));
			retMap.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new Integer(
					hits.length()));

			domTreeHelper.setPage("");
			return retMap;
		}
		// Build the virtual tree
		String page = domTreeHelper.getPage();
		if (!page.equals(""))
			page += ".";
		if (debugLogging) {
			logger.debug("buildBinderVirtualTree(virtual tree): page=" + ((0 == page.length()) ? "*no page*" : page));
		}
		for (int i = 0; i < results.size(); i++) {
			List result = (List) results.get(i);
			Map skipMap = new HashMap();
			skipMap.put(DomTreeBuilder.SKIP_TUPLE, result);
			skipMap.put(DomTreeBuilder.SKIP_PAGE, page + String.valueOf(i));
			skipMap.put(DomTreeBuilder.SKIP_BINDER_ID, top.getId().toString());
			next = current.addElement(DomTreeBuilder.NODE_CHILD);
			Element e = domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_SKIPLIST, skipMap, next);
			if (debugLogging) {
				logger.debug("...next Element:  " + ((null == e) ? "*no element*" : e.asXML()));
			}
			if (e == null) {
				current.remove(next);
			}
		}
		return null;
	}

	// Build a sorted list of children binders and their recent entries
	public SortedSet buildWorkspaceBinderMap(Long id) {
		SortedSet binderMap = new TreeSet();
		return binderMap;
	}

	@Override
	public SimpleName getSimpleName(String name) {
		// Do we need access check here or not?
		return getCoreDao().loadSimpleName(name.toLowerCase(),
				RequestContextHolder.getRequestContext().getZoneId());
	}

	@Override
	public SimpleName getSimpleNameByEmailAddress(String emailAddress) {
		// Do we need access check here or not?
		return getCoreDao().loadSimpleNameByEmailAddress(
				emailAddress.toLowerCase(),
				RequestContextHolder.getRequestContext().getZoneId());
	}

	@Override
	public void addSimpleName(String name, Long binderId, String binderType) 
			throws SimpleNameAlreadyExistsException {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageSimpleName);
		SimpleName simpleName = new SimpleName(RequestContextHolder
				.getRequestContext().getZoneId(), name.toLowerCase(), binderId,
				binderType);

		//Make sure this name doesn't map into an email address that is already in use
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		SimpleName testName = getCoreDao().loadSimpleNameByEmailAddress(simpleName.getEmailAddress(), zoneId);
		if (testName != null) {
			//A simple name with this email address already exists. Make sure it is the same as what we are trying to create
			if (!testName.getId().equals(simpleName.getId())) {
				//There is already a different simple name with the same email address. Reject this new one.
				throw new SimpleNameAlreadyExistsException(testName.getName());
			}
		}
		
		getCoreDao().save(simpleName);
	}

	@Override
	public void deleteSimpleName(String name) {
		SimpleName simpleName = getCoreDao().loadSimpleName(name.toLowerCase(),
				RequestContextHolder.getRequestContext().getZoneId());
		Binder binder = loadBinder(simpleName.getBinderId());
		checkAccess(binder, BinderOperation.manageSimpleName);
		getCoreDao().delete(simpleName);
	}

	@Override
	public List<SimpleName> getSimpleNames(Long binderId) {
		return getCoreDao().loadSimpleNames(binderId,
				RequestContextHolder.getRequestContext().getZoneId());
	}
	
	// no transaction
	@Override
	public void setBinderVersionsInherited(Long binderId, final Boolean binderVersionsInherited)
			throws AccessControlException {
        setBinderVersionsInherited(binderId, binderVersionsInherited, true);
    }

	@Override
	public void setBinderVersionsInherited(Long binderId, final Boolean binderVersionsInherited, boolean doAccessCheck)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
        if (doAccessCheck) {
		    checkAccess(binder, BinderOperation.manageConfiguration);
        }
		if (binderVersionsInherited) {
			getTransactionTemplate().execute(new TransactionCallback() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					binder.setVersionsInherited();
					return binderVersionsInherited;
				}
			});
		}
	}

	//Get the versionsEnabled setting from the first binder it is set in up the ancestor chain
	@Override
	public Boolean getBinderVersionsEnabled(Binder binder) {
		Boolean result = binder.getVersionsEnabled();
		Binder parent = binder;
		while (parent != null) {
			if (result != null) break;
			result = parent.getVersionsEnabled();
			parent = parent.getParentBinder();
		}
		if (result == null) {
			//This value has never been set anywhere, so default to true
			result = Boolean.TRUE;
		}
		return result;
	}

	// no transaction
	@Override
	public void setBinderVersionsEnabled(Long binderId, final Boolean binderVersionsEnabled)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageConfiguration);
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				binder.setVersionsEnabled(binderVersionsEnabled);
				return binderVersionsEnabled;
			}
		});
	}

    //Get the versionsToKeep setting from the first binder it is set in up the ancestor chain
    @Override
	public Long getBinderVersionsToKeep(Binder binder) {
    	Boolean versionsEnabled = binder.getVersionsEnabled();
    	if (Utils.checkIfFilr() || (versionsEnabled != null && !versionsEnabled)) {
    		//Filr systems do not support versions in V1
    		//Versions have been explicitly turned off. Simulate no versions by returning 0
    		return 0L;
    	} else if (versionsEnabled != null && versionsEnabled) {
    		//Versions have been explicitly turned on, so don't inherit anything
    		return binder.getVersionsToKeep();
    	}
    	Long result = binder.getVersionsToKeep();
		Binder parent = binder;
		while (parent != null) {
			if (result != null) break;
			result = parent.getVersionsToKeep();
			parent = parent.getParentBinder();
		}
		return result;
    }
    @Override
	public void setBinderVersionsToKeep(Long binderId, final Long binderVersionsToKeep)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageConfiguration);
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				binder.setVersionsToKeep(binderVersionsToKeep);
				return binderVersionsToKeep;
			}
		});
	}

	//Get the versionsEnabled setting from the first binder it is set in up the ancestor chain
	@Override
	public Boolean getBinderVersionAgingEnabled(Binder binder) {
		Boolean result = binder.getVersionAgingEnabled();
		if (result == null) {
			//This value has never been set anywhere, so default to true
			result = Boolean.TRUE;
		}
		return result;
	}

	// no transaction
	@Override
	public void setBinderVersionAgingEnabled(Long binderId, final Boolean enabled)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageConfiguration);
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				binder.setVersionAgingEnabled(enabled);
				return enabled;
			}
		});
	}

	//Get the versionAgingDays setting from the binder
    @Override
	public Long getBinderVersionAgingDays(Binder binder) {
    	return binder.getVersionAgingDays();
    }

    @Override
	public void setBinderVersionAgingDays(Long binderId, final Long binderVersionAgingDays)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageConfiguration);
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				binder.setVersionAgingDays(binderVersionAgingDays);
				return binderVersionAgingDays;
			}
		});
	}
    
	//Routine to calculate the aging date for each file in a binder
	@Override
	public void setBinderFileAgingDates(Binder binder) {
		checkAccess(binder, BinderOperation.manageConfiguration);
		loadBinderProcessor(binder).setFileAgingDates(binder);
	}
	
	//Routines to set the branding
    @Override
	public void setBinderBranding(Long binderId, final String branding)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageConfiguration);
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				binder.setBranding(branding);
				return branding;
			}
		});
	}
    @Override
	public void setBinderBrandingExt(Long binderId, final String brandingExt)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageConfiguration);
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				binder.setBrandingExt(brandingExt);
				return brandingExt;
			}
		});
    }



	//Get the maxFileSize setting from the first binder it is set in up the ancestor chain
	@Override
	public Long getBinderMaxFileSize(Binder binder) {
		Long result = binder.getMaxFileSize();
		Binder parent = binder;
		while (parent != null) {
			if (result != null) break;
			result = parent.getMaxFileSize();
			parent = parent.getParentBinder();
		}
		return result;
	}

    @Override
	public void setBinderMaxFileSize(Long binderId, final Long maxFileSize)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageConfiguration);
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				binder.setMaxFileSize(maxFileSize);
				return maxFileSize;
			}
		});
	}

	// no transaction
	@Override
	public void setBinderFileEncryptionInherited(Long binderId, final Boolean binderEncryptionInherited)
			throws AccessControlException {
        setBinderFileEncryptionInherited(binderId, binderEncryptionInherited, true);
    }
	
	@Override
	public void setBinderFileEncryptionInherited(Long binderId, final Boolean binderEncryptionInherited, boolean doAccessCheck)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
        if (doAccessCheck) {
		    checkAccess(binder, BinderOperation.manageConfiguration);
        }
		if (binderEncryptionInherited) {
			getTransactionTemplate().execute(new TransactionCallback() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					binder.setFileEncryptionInherited();
					return binderEncryptionInherited;
				}
			});
		}
	}
	
	@Override
	public Set<Long> getUnEncryptedBinderEntryIds(Long binderId, boolean onlyCheckEncryptedFolders) {
		final Binder binder = loadBinder(binderId);
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		List folderIds = new ArrayList();
		folderIds.add(binder.getId().toString());
		Criteria crit = new Criteria();
		crit.add(in(Constants.DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER}))
			.add(in(Constants.ENTRY_ANCESTRY, folderIds));
		crit.addOrder(Order.asc(Constants.SORTNUMBER_FIELD));
		Map binderMap = executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS, SearchUtils.fieldNamesList(Constants.DOCID_FIELD));
		Map binderMapDeleted = executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS, SearchUtils.fieldNamesList(Constants.DOCID_FIELD), true);

		List binderMapList = (List)binderMap.get(ObjectKeys.SEARCH_ENTRIES); 
		List binderMapListDeleted = (List)binderMapDeleted.get(ObjectKeys.SEARCH_ENTRIES); 
		List binderIdList = new ArrayList();
      	for (Iterator iter=binderMapList.iterator(); iter.hasNext();) {
      		Map entryMap = (Map) iter.next();
      		binderIdList.add(new Long((String)entryMap.get(Constants.DOCID_FIELD)));
      	}
      	for (Iterator iter=binderMapListDeleted.iterator(); iter.hasNext();) {
      		Map entryMap = (Map) iter.next();
      		binderIdList.add(new Long((String)entryMap.get(Constants.DOCID_FIELD)));
      	}
      	SortedSet<Binder> binderList = getBinders(binderIdList);
      	List<Long> binderIds = new ArrayList<Long>();
      	for (Binder b : binderList) {
      		if (!onlyCheckEncryptedFolders || b.isFileEncryptionEnabled()) {
      			binderIds.add(b.getId());
      		}
      	}
      	//Now get the list of entries in those folders where there is an attached file that is not encrypted
      	return getFolderDao().findFolderUnEncryptedEntries(binderIds);
	}
	
	// no transaction
	@Override
	public void setBinderFileEncryptionEnabled(Long binderId, final Boolean fileEncryptionEnabled, 
			FilesErrors errors) throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		if (binder.isMirrored() && fileEncryptionEnabled) {
			//Don't enable encryption on mirrored folders
			return;
		}
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		checkAccess(binder, BinderOperation.manageConfiguration);
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				binder.setFileEncryptionEnabled(fileEncryptionEnabled);
				return fileEncryptionEnabled;
			}
		});
		
		//Now, check to see if there are any files that need to be encrypted.
		//First, get the list of sub-binders plus this binder
		if (binder.isFileEncryptionEnabled()) {
	      	//Get the list of entries in those folders where there is an attached file that is not encrypted
	      	Set<Long> entryIds = getUnEncryptedBinderEntryIds(binderId, true);
	      	for (Long id : entryIds) {
	      		FolderEntry entry = getFolderDao().loadFolderEntry(id, zoneId);
	      		
	      		//Delete any cached files for this entry
	      		getFileModule().deleteCachedFiles(binder, entry);
	      		
	      		Set<Attachment> atts = entry.getAttachments();
	      		for (Attachment att : atts) {
	      			if (att instanceof FileAttachment) {
	      				FileAttachment fAtt = (FileAttachment)att;
	      				Set<VersionAttachment> vAtts = fAtt.getFileVersions();
	      				List<VersionAttachment> reverseVAtts = new ArrayList<VersionAttachment>();
	      				for (VersionAttachment vAtt : vAtts) {
	      					//Reverse the order of this set
	      					reverseVAtts.add(0, vAtt);
	      				}
	      				for (VersionAttachment vAtt : reverseVAtts) {
		      				getFileModule().encryptVersion(binder, entry, vAtt, errors);
	      				}
	      			}
	      		}
	      	}
		}
	}

	//Get the fileEncryption setting from the first folder it is set in up the ancestor chain
	@Override
	public Boolean isBinderFileEncryptionEnabled(Binder binder) {
		Boolean result = binder.isFileEncryptionEnabled();
		if (!result && binder.getFileEncryptionEnabled() == null) {
			//The current binder isn't explicitly enabled or disabled for file encryption, check for inheritance
			Binder parent = binder.getParentBinder();
			while (parent != null && parent instanceof Folder) {
				result = parent.isFileEncryptionEnabled();
				if (result) break;
				parent = parent.getParentBinder();
			}
		}
		return result;
	}


	/**
	 * Set the group id associated with the given binder
	 */
	private void setBinderTeamGroupId( final Binder binder, Long groupId )
	{
		TransactionCallback callback;

		callback = new TransactionCallback()
		{
			@Override
			public Object doInTransaction( TransactionStatus status )
			{
				if ( !(binder instanceof TemplateBinder) )
				{
					User user = RequestContextHolder.getRequestContext().getUser();
					
					binder.incrLogVersion();
					binder.setModification( new HistoryStamp( user ) );
					BinderProcessor processor = loadBinderProcessor( binder );
					processor.processChangeLog( binder, ChangeLog.ACCESSMODIFY );
				}
				
				return Boolean.TRUE;
			}
		};

		binder.setTeamGroupId( groupId );
		getTransactionTemplate().execute( callback );
	}
	
	// no transaction
	@Override
	public void setPostingEnabled(Long binderId, final Boolean postingEnabled)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.modifyBinder);
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				binder.setPostingEnabled(postingEnabled);
				return postingEnabled;
			}
		});
	}

	@Override
	public void export(Long binderId, Long entityId, OutputStream out,
			Map options, Collection<Long> binderIds, Boolean noSubBinders, 
			StatusTicket statusTicket, Map reportMap) throws Exception {

		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.export);
		
		ExportHelper.export(binderId, entityId, out, options, binderIds, noSubBinders, 
				statusTicket, reportMap);

	}

	@Override
	public String filename8BitSingleByteOnly(FileAttachment attachment,
			boolean _8BitSingleByteOnly) {
		String fileName = attachment.getFileItem().getName();

		String fileExt = EntityIndexUtils.getFileExtension(attachment
				.getFileItem().getName());

		if (!_8BitSingleByteOnly) {
			return fileName;
		} else {
			for (int i = 0; i < fileName.length(); i++) {
				int c = (int) fileName.charAt(i);

				if (c >= 0x20 && c < 0x7F) {
					// it's ok
				} else
					return attachment.getId() + "." + fileExt;
			}

			return fileName;
		}
	}
	
	@Override
	public String filename8BitSingleByteOnly(String fileName, String fallbackName, 
			boolean _8BitSingleByteOnly) {
		if (!_8BitSingleByteOnly) {
			return fileName;
		} else {
			for (int i = 0; i < fileName.length(); i++) {
				int c = (int) fileName.charAt(i);

				if (c >= 0x20 && c < 0x7F) {
					// it's ok
				} else {
					//The file name has non-single byte characters. See if there is an extension we can use
					if (fileName.lastIndexOf(".") > 0 && fallbackName.indexOf(".") < 0) {
						String ext = fileName.substring(fileName.lastIndexOf("."));
						for (int j = 0; j < ext.length(); j++) {
							int c2 = (int) ext.charAt(j);

							if (c2 >= 0x20 && c2 < 0x7F) {
								//ok so far
							} else {
								return fallbackName;
							}
						}
						//The extension is ok, stick that on the end of the fallback name
						return fallbackName + ext;
					}
					return fallbackName;
				}
			}

			return fileName;
		}
	}
	
	@Override
	public Long getZoneBinderId(Long binderId, String zoneUUID, String entityType) {
		if (Validator.isNull(zoneUUID)) return binderId;
		List<Long> ids = getCoreDao().findZoneEntityIds(binderId, zoneUUID, entityType);
		if (ids.isEmpty()) {
			ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
			if (zoneInfo.getId().equals(zoneUUID)) return binderId;
			return null;
		}
		return ids.get(0);
	}
	
	// no transaction
	protected void deleteBinderPhase1(Long binderId, final boolean deleteMirroredSource,
			final Map options, final boolean createDbLogForTopBinderOnly) {
		final Binder top = loadBinder(binderId);
		if (BinderHelper.isBinderSystemUserWS(top)) {
			throw new NotSupportedException("errorcode.notsupported.deleteBinder.systemUserWS");
		}
		checkAccess(top, BinderOperation.deleteBinder);
		List<Long> ids = new ArrayList();// maintain order, bottom up
		Map params = new HashMap();
		params.put("deleted", Boolean.FALSE);
		// get list of ids, so don't have to load large trees all at once
		List<Object[]> objs = getCoreDao()
				.loadObjects(
						"select x.id,x.binderKey.sortKey from org.kablink.teaming.domain.Binder x where x.binderKey.sortKey like '"
								+ top.getBinderKey().getSortKey()
								+ "%' and deleted=:deleted order by x.binderKey.sortKey desc",
						params);
		// convert to list of ids
		for (Object row[] : objs) {
			ids.add((Long) row[0]);
		}
		ids.remove(top.getId());
		for (Long id : ids) {
			try {
				final Binder child = getCoreDao().loadBinder(id,
						top.getZoneId());
				if (child.isDeleted())
					continue;
				checkAccess(child, BinderOperation.deleteBinder);
				// determine if need to deleted mirrored for this binder, or if
				// parent will take care of it
				boolean deleteMirroredSourceForChildren = deleteMirroredSource;
				if (top.isMirrored() && deleteMirroredSource)
					deleteMirroredSourceForChildren = false;
				if (deleteMirroredSourceForChildren == true) {
					// top is not mirrored; see if a parent is
					Binder parent = child.getParentBinder();
					while (parent != top) {
						if (parent.isMirrored()) {
							deleteMirroredSourceForChildren = false;
							break;
						}
						parent = parent.getParentBinder();
					}
				}
				final boolean doMirrored = deleteMirroredSourceForChildren;
				getTransactionTemplate().execute(new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus status) {
						loadBinderProcessor(child).deleteBinder(child,
								doMirrored, options, (createDbLogForTopBinderOnly)? true:false);
						return null;
					}
				});
				// get updates committed, this is needed if their is another
				// transaction wrapping the call to delete binder
				// This is the case with delete user workspace !!
				getCoreDao().flush();

				getCoreDao().evict(child); // update committed

			} catch (NoObjectByTheIdException musthavebeendeleted) {
				continue;
			}
		}
		getTransactionTemplate().execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				BinderProcessor processor = loadBinderProcessor(top);
				// Need to save reference to parent binder before it gets nullified.
				Binder parentBinder = top.getParentBinder();
				processor.deleteBinder(top, deleteMirroredSource, options, false);
				processor.updateParentModTime(parentBinder, options);			
				return null;
			}
		});

	}

	protected void deleteBinderPhase2() {
		// Do NOT set this property to true (see bug 815697)
		if(SPropsUtil.getBoolean("binder.delete.immediate", false)) {
			// Initiate the phase 2 of the process needed for deleting a binder hierarchy. 
			// This part is executed asynchronously and we do not check its outcome.
			getRunAsyncManager().execute(new RunAsyncCallback() {
				@Override
				public Object doAsynchronously() throws Exception {
			    	FolderModule folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");
			    	folderModule.cleanupFolders();
			    	return null;
				}
				@Override
				public String toString() {
					return "folderModule.cleanupFolders()";
				}
			}, RunAsyncManager.TaskType.MISC);
		}
	}

	@Override
	public void incrementFileMajorVersion(DefinableEntity entity, FileAttachment fileAtt) {
		checkModifyFileAccess(entity);
		getFileModule().incrementMajorFileVersion(entity, fileAtt);
		BinderHelper.indexEntity(entity);
		if (entity instanceof WorkflowControlledEntry) {
			//This is a workflow entity, so see if anything needs to be triggered on modify
			getWorkflowModule().modifyWorkflowStateOnUpdate((WorkflowSupport) entity);
		}
	}
	
	@Override
	public void setFileVersionNote(DefinableEntity entity, FileAttachment fileAtt, String text) {
		checkModifyFileAccess(entity);
		Description description = new Description(text);
		getFileModule().modifyFileComment(entity, fileAtt, description);
		BinderHelper.indexEntity(entity);
		if (entity instanceof WorkflowControlledEntry) {
			//This is a workflow entity, so see if anything needs to be triggered on modify
			getWorkflowModule().modifyWorkflowStateOnUpdate((WorkflowSupport) entity);
		}
	}
	
	@Override
	public void promoteFileVersionCurrent(DefinableEntity entity, VersionAttachment va) {
		checkModifyFileAccess(entity);
		if(entity.getParentBinder().isMirrored()) return;
		getFileModule().revertFileVersion(entity, va);
		BinderHelper.indexEntity(entity);
		if (entity instanceof WorkflowControlledEntry) {
			//This is a workflow entity, so see if anything needs to be triggered on modify
			getWorkflowModule().modifyWorkflowStateOnUpdate((WorkflowSupport) entity);
		}
	}
	
	@Override
	public void deleteFileVersion(Binder binder, DefinableEntity entity, FileAttachment fileAtt) {
		checkDeleteFileAccess(entity);
		FilesErrors errors = new FilesErrors();
		//Delete this version
		if (fileAtt instanceof VersionAttachment) {
			getFileModule().deleteVersion(binder, entity, (VersionAttachment)fileAtt);
			BinderHelper.indexEntity(entity);
		} else if (fileAtt instanceof FileAttachment) {
			//This is the top file in the version list
			if (fileAtt.getFileVersionsUnsorted().size() <= 1) {
				//This is the only version
				getFileModule().deleteFile(binder, entity, fileAtt, errors);
				BinderHelper.indexEntity(entity);  //Must re-index since there is a new top file version
			} else {
				//There are some versions, so one of them will become the new top file
				getFileModule().deleteVersion(binder, entity, fileAtt.getHighestVersion());
				BinderHelper.indexEntity(entity);  //Must re-index since there is a new top file version
			}
			if (entity instanceof WorkflowControlledEntry) {
				//This is a workflow entity, so see if anything needs to be triggered on modify
				getWorkflowModule().modifyWorkflowStateOnUpdate((WorkflowSupport) entity);
			}
		}
	}
	
	@Override
	public void setFileVersionStatus(DefinableEntity entity, FileAttachment fa, int status) {
		checkModifyFileAccess(entity);
		FileStatus fileStatus = FileStatus.valueOf(status);
		getFileModule().modifyFileStatus(entity, fa, fileStatus);
		BinderHelper.indexEntity(entity);
		if (entity instanceof WorkflowControlledEntry) {
			//This is a workflow entity, so see if anything needs to be triggered on modify
			getWorkflowModule().modifyWorkflowStateOnUpdate((WorkflowSupport) entity);
		}
	}
	
	@Override
	public boolean isBinderEmpty(Binder binder) {
		BinderProcessor processor = loadBinderProcessor(binder);
		return processor.isFolderEmpty(binder);
	}

	protected void checkModifyFileAccess(DefinableEntity entity) {
		if (entity instanceof FolderEntry) {
			getFolderModule().checkAccess((FolderEntry)entity, FolderOperation.modifyEntry);
		} else if (entity instanceof Principal) {
			getProfileModule().checkAccess((Principal)entity, ProfileOperation.modifyEntry);
		} else if (entity instanceof Binder) {
			checkAccess((Binder)entity, BinderOperation.modifyBinder);
		} else {
			throw new AccessControlException();
		}
	}

	protected void checkDeleteFileAccess(DefinableEntity entity) {
		if (entity instanceof FolderEntry) {
			getFolderModule().checkAccess((FolderEntry)entity, FolderOperation.deleteEntry);
		} else if (entity instanceof Principal) {
			getProfileModule().checkAccess((Principal)entity, ProfileOperation.deleteEntry);
		} else if (entity instanceof Binder) {
			checkAccess((Binder)entity, BinderOperation.deleteBinder);
		} else {
			throw new AccessControlException();
		}
	}
		
	@Override
    public Map searchFolderOneLevelWithInferredAccess(Criteria crit, int searchMode, int offset, int maxResults, Binder parentBinder) {
        return searchFolderOneLevelWithInferredAccess(crit, searchMode, offset, maxResults, parentBinder, true);
    }

	@Override
    public Map searchFolderOneLevelWithInferredAccess(Criteria crit, int searchMode, int offset, int maxResults, Binder parentBinder, boolean allowJits) {
		// No access checking in this method, because we expect the caller to check access on the parent binder before calling this method.
    	boolean preDeleted = false;
    	boolean ignoreAcls = false;
    	
		QueryBuilder qb = new QueryBuilder(!ignoreAcls, preDeleted);
		SearchObject so = qb.buildQuery(crit.toQuery());
		
		Hits hits = executeNetFolderLuceneQuery(so, searchMode, offset, maxResults, parentBinder, allowJits);

		return returnSearchQuery(hits);
    }

	@Override
	public boolean testInferredAccessToBinder(User user, Binder binder) {
		if (binder.isAclExternallyControlled()) { 		// Net Folder or its sub-folder
			//We must ensure the user has the net folder access right to view this net folder
			getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER);
		}
		if(binder.noAclDredgedWithEntries()) {
			// This binder is a net folder which does not store file ACLs in the search index.
			// Consequently inferred access computation is not always possible with the information
			// stored in the search index. Specifically, when user has access to a file but not
			// to any of its ancestor folders, the search index would not be able to compute it.
			// So we have to ask the file system directly.
			AclResourceSession session = SearchUtils.openAclResourceSession(binder.getResourceDriver(), FolderUtils.getNetFolderOwnerId((Folder)binder));
			if(session == null)
				return false; // cannot obtain session for the user
			try {
				session.setPath(binder.getResourcePath(), binder.getResourceHandle(), Boolean.TRUE);
				try {
					return session.isVisible(AccessUtils.getFileSystemGroupIds(binder.getResourceDriver()));
				} catch (Exception e) {
					logger.error("Error checking visibility on folder resource [" + binder.getResourcePath() + "]", e);
					return false; // fails the test
				}
			}
			finally {
				session.close();
			}
		}
		else {
	       	//Create the Lucene query
	    	QueryBuilder qb = new QueryBuilder(true, false);
	    	String aclQueryStr = qb.buildAclClause();

	    	LuceneReadSession luceneSession = getLuceneSessionFactory().openReadSession();
	        
	        try {
	        	return luceneSession.testInferredAccessToNonNetFolder(user.getId(), aclQueryStr, binder.getPathName());
	        }
	        finally {
	            luceneSession.close();
	        }
		}
	}

    @Override
	public BinderChanges searchOneLevelForChanges(Long binderId, Date sinceDate, int maxResults) {
        return searchForChanges(new Long[] {binderId}, null, sinceDate, false, maxResults);
    }

    @Override
	public BinderChanges searchSubTreeForChanges(Long[] binderIds, Long[] entryIds, Date sinceDate, int maxResults) {
        return searchForChanges(binderIds, entryIds, sinceDate, true, maxResults);
    }

    private BinderChanges searchForChanges(Long[] binderIds, Long[] entryIds, Date sinceDate, boolean recursive, int maxResults) {
		verifyInSupportedBinderChangesWindow(sinceDate);
        List<Binder> binders = getBinders(binderIds);
        if (binders.size()==0 && (entryIds==null || entryIds.length==0)) {
            return new BinderChanges();
        }
        List<HKey> binderKeys = getHKeys(binders);
        if (binderKeys.size()>0) {
            if (recursive && haveAclsChangedSinceDate(binderKeys, sinceDate)) {
                throw new AclChangeException();
            } else if (!recursive && haveFolderRightsChangedSinceDate(new HashSet<Long>(Arrays.asList(binderIds)), sinceDate)) {
                throw new AclChangeException();
            }
        }

        Set<Long> rootNetFolders = new HashSet<Long>();
        for (Binder binder : binders) {
            if (binder.isMirrored()) {
                if (((Folder)binder).isTop()) {
                    rootNetFolders.add(binder.getId());
                } else {
                    rootNetFolders.add(((Folder)binder).getTopFolder().getId());
                }
            }
        }
        if (rootNetFolders.size()>0 && haveFolderRightsChangedSinceDate(rootNetFolders, sinceDate)) {
            throw new AclChangeException();
        }

        Map map = searchForChangedEntities(binderIds, entryIds, sinceDate, recursive, true, true, true, false, maxResults);
		List deletedEntries1 = null;
		List deletedEntries2 = null;
		if (binderKeys!=null && binderKeys.size()>0) {
			deletedEntries1 = getDeletedAuditTrailEntriesByBinder(binderKeys, recursive, sinceDate, maxResults);
		}
		if (entryIds!=null && entryIds.length>0) {
			List entryList = Arrays.asList(entryIds);
			deletedEntries2 = getDeletedAuditTrailEntriesByEntity(entryList, sinceDate, maxResults);
		}

        Integer searchCount = (Integer)map.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED);
        Integer searchTotal = (Integer)map.get(ObjectKeys.TOTAL_SEARCH_COUNT);
        List<Map> searchEntries = (List<Map>)map.get(ObjectKeys.SEARCH_ENTRIES);

		MergedOrderedListIterator resultIterator = new MergedOrderedListIterator(new BinderChangesComparator(), searchEntries, deletedEntries1, deletedEntries2);

        List<BinderChange> mergedResults = new ArrayList<BinderChange>(maxResults);
        boolean done = false;
        while (!done) {
            if (mergedResults.size()>=maxResults || !resultIterator.hasNext()) {
                done = true;
            } else {
				Object next = resultIterator.next();

                if (next instanceof BasicAudit) {
                	BasicAudit at = (BasicAudit) next;
                    EntityIdentifier entityId = at.getEntityIdentifier();
                    if (entityId!=null) {
                        BinderChange change = new BinderChange();
                        change.setEntityId(entityId);
                        change.setPrimaryFileId(at.getFileId());
                        change.setAction(BinderChange.Action.delete);
                        change.setDate(at.getEventDate());
                        mergedResults.add(change);
                    }
                } else if (next instanceof Map) {
					Map nextMap = (Map) next;
                    EntityIdentifier entityId = buildIdentifier(nextMap);
                    if (entityId!=null) {
                        BinderChange change = new BinderChange();
                        change.setEntityId(entityId);
                        if (getResultCreateDate(nextMap).after(sinceDate)) {
                            change.setAction(BinderChange.Action.add);
                        } else {
                            change.setAction(BinderChange.Action.modify);
                        }
                        change.setDate(getResultModDate(nextMap));
                        change.setSearchMap(nextMap);
                        mergedResults.add(change);
                    }
                }
            }
        }
        BinderChanges changes = new BinderChanges();
        changes.setChanges(mergedResults);
        changes.setCount(mergedResults.size());
		int total = searchTotal;
		if (deletedEntries1!=null) {
			total += deletedEntries1.size();
		}
		if (deletedEntries2!=null) {
			total += deletedEntries2.size();
		}
        changes.setTotal(total);
        return changes;
    }

    private EntityIdentifier buildIdentifier(Map entry) {
        String idStr = (String) entry.get(Constants.DOCID_FIELD);
        String typeStr = (String) entry.get(Constants.ENTITY_FIELD);
        try {
            return new EntityIdentifier(Long.parseLong(idStr), EntityType.valueOf(typeStr));
        } catch (Exception e) {
        }
        return null;
    }

    private Date getResultCreateDate(Map entry) {
        return (Date) entry.get(Constants.CREATION_DATE_FIELD);
    }

    private Date getResultModDate(Map entry) {
        return (Date) entry.get(Constants.MODIFICATION_DATE_FIELD);
    }

    private boolean haveAclsChangedSinceDate(List<HKey> binderKeys, Date sinceDate) {
        Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
        List aclChanges = getCoreDao().getBasicAuditEntries(zoneId, sinceDate, binderKeys, true,
                new AuditType[]{AuditType.acl}, null, 1);
        return aclChanges.size()>0;
    }

    private boolean haveFolderRightsChangedSinceDate(Set<Long> folderIds, Date sinceDate) {
        Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
        List aclChanges = getCoreDao().getBasicAuditEntries(zoneId, sinceDate, new ArrayList<Long>(folderIds),
                new AuditType[]{AuditType.acl}, null, 1);
        return aclChanges.size()>0;
    }

    private List<HKey> getHKeys(List<Binder> binders) {
        List<HKey> keys = new ArrayList<HKey>(binders.size());
        for (Binder binder : binders) {
            keys.add(binder.getBinderKey());
        }
        return keys;
    }

    private List<Binder> getBinders(Long[] binderIds) {
        List<Binder> binders = new ArrayList<Binder>(binderIds.length);
        for (Long id : binderIds) {
            try {
                Binder binder = getBinder(id);
                binders.add(binder);
            } catch (AccessControlException e) {
                // Ignore
            } catch (NoBinderByTheIdException e) {
                // Ignore
            }
        }
        return binders;
    }

    private List getDeletedAuditTrailEntriesByBinder(List<HKey>  binderKeys, boolean recursive, Date sinceDate, int maxResults) {
        Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		return getCoreDao().getBasicAuditEntries(zoneId, sinceDate, binderKeys, recursive,
				new AuditType[]{AuditType.delete, AuditType.preDelete},
				new EntityType[] {EntityType.folderEntry,EntityType.folder,EntityType.workspace}, maxResults);
    }

    private List getDeletedAuditTrailEntriesByEntity(List<Long> entryIds, Date sinceDate, int maxResults) {
        Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		return getCoreDao().getBasicAuditEntries(zoneId, sinceDate, entryIds,
				new AuditType[]{AuditType.delete, AuditType.preDelete},
				new EntityType[] {EntityType.folderEntry,EntityType.folder,EntityType.workspace}, maxResults);
    }

    private Map searchForChangedEntities(Long [] binderIds, Long [] entryIds, Date sinceDate, boolean recursive, boolean libraryOnly, boolean binders, boolean entries, boolean attachments, int maxResults){
        Criteria crit = new Criteria();
        Junction or = disjunction();
        if (binderIds!=null) {
            if (recursive) {
                for (Long binderId : binderIds) {
                    or.add(org.kablink.teaming.search.SearchUtils.buildAncentryCriterion(binderId));
                }
            } else {
                for (Long binderId : binderIds) {
                    or.add(org.kablink.teaming.search.SearchUtils.buildParentBinderCriterion(binderId));
                }
                Junction and = conjunction();
                and.add(org.kablink.teaming.search.SearchUtils.buildBindersCriterion());
                List<String> idStrs = new ArrayList<String>(entryIds.length);
                for (Long binderId : binderIds) {
                    idStrs.add(binderId.toString());
                }
                and.add(in(Constants.DOCID_FIELD, idStrs));
                or.add(and);
            }
        }
        if (entryIds!=null) {
            Junction and = conjunction();
            and.add(org.kablink.teaming.search.SearchUtils.buildEntriesCriterion());
            List<String> idStrs = new ArrayList<String>(entryIds.length);
            for (Long entryId : entryIds) {
                idStrs.add(entryId.toString());
            }
            and.add(in(Constants.DOCID_FIELD, idStrs));
            or.add(and);
        }
        crit.add(or);
        crit.add(org.kablink.teaming.search.SearchUtils.buildDocTypeCriterion(binders, entries || attachments, false, false));
        if (libraryOnly) {
            crit.add(org.kablink.teaming.search.SearchUtils.buildLibraryCriterion(true, true));
        }
        crit.add(Restrictions.between(Constants.MODIFICATION_DATE_FIELD, DateTools.dateToString(sinceDate, DateTools.Resolution.SECOND),
                DateTools.dateToString(new Date(), DateTools.Resolution.SECOND)));
        crit.addOrder(new Order(Constants.MODIFICATION_DATE_FIELD, true));
        return executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, maxResults,
        		SearchUtils.fieldNamesList(Constants.MODIFICATION_DATE_FIELD,Constants.DOCID_FIELD,Constants.ENTITY_FIELD,Constants.CREATION_DATE_FIELD));
    }
    
	class IndexHelper implements Runnable {
		
		private ConcurrentStatusTicket statusTicket;
		String[] nodeNames;
		private IndexErrors errors;
		private BinderToIndexQueue queue;
		private RequestContext parentRequestContext;
		// This must be guarded by itself.
		private Collection<Long> done;
		private boolean skipFileContentIndexing;
		
		IndexHelper(ConcurrentStatusTicket statusTicket, String[] nodeNames, IndexErrors errors, BinderToIndexQueue queue, RequestContext parentRequestContext, Collection<Long> done, boolean skipFileContentIndexing) {
			this.statusTicket = statusTicket;
			this.nodeNames = nodeNames;
			this.errors = errors;
			this.queue = queue;
			this.parentRequestContext = parentRequestContext;
			this.done = done;
			this.skipFileContentIndexing = skipFileContentIndexing;
		}

		@Override
		public void run() {
			try {
				if(logger.isDebugEnabled())
					logger.debug("Starting");
				doRun();
			}
			catch(final Exception e) {
				logger.error("Error in index helper", e);
				throw e;
			}
			finally {
				logger.info("Terminating");
			}
		}	
		
		private void doRun() {
			if(logger.isTraceEnabled())
				logger.trace("Setting up Hibernate session");
			SessionUtil.sessionStartup();	// Set up Hibernate session (for database)
			try {
				// Copy parent/calling thread's request context
				if(logger.isTraceEnabled())
					logger.trace("Setting up request context");
				RequestContextHolder.setRequestContext(parentRequestContext);
				try {
					int indexFlushThreshold = SPropsUtil.getInt("lucene.flush.threshold", 100);
					if(logger.isTraceEnabled())
						logger.trace("Setting index flush threshold to " + indexFlushThreshold + " with IndexSynchronizationManagerInterceptor");
					IndexSynchronizationManagerInterceptor.setThreshold(indexFlushThreshold);
					try {
						if(logger.isDebugEnabled())
							logger.debug("Setting indexers to " + StringUtil.toString(nodeNames));
						IndexSynchronizationManager.setNodeNames(nodeNames);
						try {
							Long binderId;
							Binder binder;
							Collection result;
							while(true) {
								try {
									binderId = queue.take();
									if(binderId.longValue() != -1L) {
										binder = loadBinder(binderId);
										result = loadBinderProcessor(binder).indexTree(binder, done, statusTicket, errors, skipFileContentIndexing);
										synchronized(done) {
											done.addAll(result);
										}
									}
									else {
										// Poison pill encountered
										break;
									}
								} catch(NoBinderByTheIdException e) {
									logger.warn(e.toString());
								} catch (InterruptedException e1) {
									Thread.currentThread().interrupt(); // Restore the interrupt
								}
							}
	
							if(logger.isDebugEnabled())
								logger.debug("Applying remaining changes to index if any");
							IndexSynchronizationManager.applyChanges();
						}
						finally {
							if(logger.isDebugEnabled())
								logger.debug("Unsetting indexers");
							IndexSynchronizationManager.clearNodeNames();
						}
					}
					finally {
						IndexSynchronizationManagerInterceptor.clearThreshold();	
					}
				}
				finally {
					if(logger.isTraceEnabled())
						logger.trace("Clearing request context");
					RequestContextHolder.clear();
				}
			}
			finally {
				if(logger.isTraceEnabled())
					logger.trace("Tearing down Hibernate session");
				SessionUtil.sessionStop();
			}
		}	
	}

	static class BinderToIndexQueue extends ArrayBlockingQueue<Long> {
		private static final long serialVersionUID = 1L;
		
		// Number of items put into the queue so far since creation of the queue
		private AtomicInteger putCount = new AtomicInteger();
		// Number of items taken from the queue so far since creation of the queue
		private AtomicInteger takenCount = new AtomicInteger();
		// Number of total items known to be put into the queue since creation of the queue.
		// This includes both those items that have already been put into the queue and those
		// that are yet to be put into the queue (i.e., future items).
		private AtomicInteger totalExpectedCount = new AtomicInteger();
		// Number of items actually processed by helper threads.
		// Since helper threads take items off the queue in a batch, the actual
		// number of items processed trail the number of items taken off the queue.
		private AtomicInteger processedCount = new AtomicInteger();
		
		public BinderToIndexQueue(int capacity) {
			super(capacity);
		}
		
		@Override
	    public void put(Long binderId) throws InterruptedException {
	    	super.put(binderId);
	    	if(binderId.longValue() != -1L)
	    		putCount.incrementAndGet();
	    }
		
		@Override
		public Long take() throws InterruptedException {
			 Long binderId = super.take();
			 if(binderId.longValue() != -1L)
				 takenCount.incrementAndGet();
			 return binderId;
		}
	    
		int getPutCount() {
			return putCount.intValue();
		}
		
		int getTakenCount() {
			return takenCount.intValue();
		}
		
		int getTotalExpectedCount() {
			return totalExpectedCount.intValue();
		}
		
		void incrementTotalExpectedCount(int delta) {
			totalExpectedCount.addAndGet(delta);
		}
		
		int getProcessedCount() {
			return processedCount.intValue();
		}
		
		int incrementProcessedCount() {
			return processedCount.incrementAndGet();
		}
		
		@Override
		public String toString() {
			return "(size=" + this.size() + ",putCount=" + this.getPutCount() + ",takenCount=" + this.getTakenCount() + ",processedCount=" + this.getProcessedCount() + ",totalExpectedCount=" + this.getTotalExpectedCount() + ")";
		}
	}

	private class BinderChangesComparator implements Comparator {

		private Date getDate(Object o) {
			if (o instanceof BasicAudit) {
				return ((BasicAudit) o).getEventDate();
			} else if (o instanceof Map) {
				return getResultModDate((Map) o);
			}
			return null;
		}

		@Override
		public int compare(Object o1, Object o2) {
			Date d1 = getDate(o1);
			Date d2 = getDate(o2);

			return d1.compareTo(d2);
		}
	}

	private void verifyInSupportedBinderChangesWindow(Date sinceDate) {
		int binderChangeSupportedDays = SPropsUtil.getInt("binder.changes.allowed.days", 7);
		Date supportedDate= new Date(System.currentTimeMillis() - ((long)binderChangeSupportedDays)*1000L*60L*60L*24L);
		if (sinceDate.before(supportedDate)) {
			throw new OutsideOfBinderChangesWindowException();
		}
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Date purgeDate = getCoreDao().getAuditTrailPurgeDate(zoneId);
		if (purgeDate!=null && purgeDate.after(sinceDate)) {
			throw new AuditTrailPurgedException();
		}
	}
}
