/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.module.report.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.AuditTrail;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.domain.LicenseStats;
import org.kablink.teaming.domain.LoginInfo;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowHistory;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.AuditTrail.AuditType;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.search.Constants;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


public class ReportModuleImpl extends HibernateDaoSupport implements ReportModule {
	protected Set enabledTypes=new HashSet();
	protected boolean allEnabled=false;
	protected CoreDao coreDao;
	protected ProfileDao profileDao;
	protected AccessControlManager accessControlManager;
	protected BinderModule binderModule;
	protected WorkspaceModule workspaceModule;
	protected FolderModule folderModule;
	protected AdminModule adminModule;
	protected ProfileModule profileModule;
	public void setAccessControlManager(AccessControlManager accessControlManager) {
		this.accessControlManager = accessControlManager;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}
	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	protected AccessControlManager getAccessControlManager() {
		return accessControlManager;
	}
	protected CoreDao getCoreDao() {
		return coreDao;
	}
	
	protected ProfileDao getProfileDao() {
		return profileDao;
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}

	public BinderModule getBinderModule() {
		return this.binderModule;
	}

	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}

	public WorkspaceModule getWorkspaceModule() {
		return this.workspaceModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	public FolderModule getFolderModule() {
		return this.folderModule;
	}

	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}

	public ProfileModule getProfileModule() {
		return this.profileModule;
	}

	//circular dependencies prevent spring from setting this up
	private synchronized AdminModule getAdminModule() {
		if (adminModule != null) return adminModule;
		adminModule = (AdminModule)SpringContextUtil.getBean("adminModule");
		return adminModule;
	}

    /**
     * Called after bean is initialized.  
     */
	protected void initDao() throws Exception {
 		//build set of enabled types
 		String [] audits = SPropsUtil.getStringArray("audit", ",");
 		for (int i=0; i<audits.length; ++i) {
 			if (audits[i].equalsIgnoreCase("all")) {
 				allEnabled = true;
 				break;
 			}
 			for (AuditType eT : AuditType.values()) {
 				if (audits[i].equalsIgnoreCase(eT.name())) {
 					enabledTypes.add(eT);
 					break;
 				}
 			}

 		}
 	}

	public void addAuditTrail(AuditTrail auditTrail) {
		//only log if enabled
		if (allEnabled || enabledTypes.contains(auditTrail.getAuditType()))
			getCoreDao().save(auditTrail);
	}
	public void addAuditTrail(AuditType type, DefinableEntity entity) {
		addAuditTrail(new AuditTrail(type, RequestContextHolder.getRequestContext().getUser(), entity));
	}
	public void addAuditTrail(AuditType type, User user, DefinableEntity entity) {
		addAuditTrail(new AuditTrail(type, user, entity));
	}
	public void addLoginInfo(LoginInfo loginInfo) {
		addAuditTrail(loginInfo);		
	}

	public void addStatusInfo(User user) {
		if (user.getStatus() != null && !user.getStatus().equals("")) {
			AuditTrail auditTrail = new AuditTrail(AuditType.userStatus, user, user);
			auditTrail.setDescription(user.getStatus());
			addAuditTrail(auditTrail);
		}
	}

	public void addFileInfo(AuditTrail.AuditType type, FileAttachment attachment) {
		AuditTrail audit = new AuditTrail(type, RequestContextHolder.getRequestContext().getUser(), attachment.getOwner().getEntity());
		audit.setDescription(attachment.getFileItem().getName());
		audit.setFileId(attachment.getId());
		addAuditTrail(audit);
		
	}

	public void addLicenseStats(LicenseStats stats) {
		getCoreDao().save(stats);
	}
	public List<User> getUsersActivity(final DefinableEntity entity, final AuditType type, final Date startDate, final Date endDate) {
		
		List ids = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(AuditTrail.class)
					.setProjection(Projections.distinct(Projections.projectionList() 
                                                          .add(Projections.property("startBy"))))
				.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, entity.getZoneId()))
				.add(Restrictions.eq("entityId", entity.getEntityIdentifier().getEntityId()))
				.add(Restrictions.eq("entityType", entity.getEntityIdentifier().getEntityType().name()))
				.add(Restrictions.eq("transactionType", type.name()))
				.add(Restrictions.ge("startDate", startDate))
				.add(Restrictions.lt("startDate", endDate));
				return crit.list();
				
			}});
		return getProfileDao().loadUsers(ids, entity.getZoneId());
	}
	
	public List<Map<String,Object>> getEntriesViewed(final Long ownerId, 
			final Date startDate, final Date endDate, Integer returnCount) {
		
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		List data = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(AuditTrail.class)
					.setProjection(Projections.distinct(Projections.projectionList() 
							.add(Projections.property("owningBinderId"))
							.add(Projections.property("entityId"))
							.add(Projections.property("startDate"))
							.add(Projections.property("fileId"))
							.add(Projections.property("transactionType"))
							.add(Projections.property("description"))))
					.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
					.add(Restrictions.eq("entityType", EntityType.folderEntry.toString()))
				    .add(Restrictions.in("transactionType", new Object[] {AuditType.view.name(), 
				    													AuditType.download.name()}))
					.add(Restrictions.eq("startBy", ownerId))
					.add(Restrictions.ge("startDate", startDate))
					.add(Restrictions.lt("startDate", endDate));
				crit.addOrder(Order.desc("startDate"));
				return crit.list();
				
			}});
		List<DefinableEntity> list = new LinkedList<DefinableEntity>();
		List entriesSeen = new ArrayList();
		List filesSeen = new ArrayList();
		for(Object o : data) {
			Object[] col = (Object []) o;
			DefinableEntity entity = null;
			try {
				entity = getFolderModule().getEntry((Long) col[0], (Long) col[1]);
			} catch(Exception skipThis) {
				continue;
			}
			if (entity == null) continue;
			if (!list.contains(entity)) list.add(entity);
			if (list.size() >= returnCount.intValue()) break;
		}
		for(Object o : data) {
			Object[] cols = (Object[]) o;
			DefinableEntity entity = null;
			if (cols[4].equals(AuditType.view.name()) && entriesSeen.contains(cols[1])) continue;
			if (cols[4].equals(AuditType.download.name()) && filesSeen.contains(cols[3])) continue;
			Map<String, Object> row = new HashMap<String, Object>();
			try {
				entity = getFolderModule().getEntry((Long) cols[0], (Long) cols[1]);
			} catch(Exception skipThis) {
				continue;
			}
			if (entity == null || entity.isDeleted()) continue;
			row.put(ReportModule.ENTITY, entity);
			row.put(ReportModule.DATE, cols[2]);
			row.put(ReportModule.FILE_ID, cols[3]);
			row.put(ReportModule.TYPE, cols[4]);
			row.put(ReportModule.DESCRIPTION, cols[5]);
			report.add(row);
			if (cols[4].equals(AuditType.view.name())) entriesSeen.add(cols[1]);
			if (cols[4].equals(AuditType.download.name())) filesSeen.add(cols[3]);
			if (report.size() >= returnCount) break;
		}
		return report;
	}
	
	public List<Map<String,Object>> getUsersStatuses(final Long[] userIds,
			final Date startDate, final Date endDate, Integer returnCount) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();

		//Get the documents bean for the documents th the user just authored or modified
		Map options = new HashMap();
		String page = "0";
		
		String entriesPerPage = String.valueOf(returnCount);
		options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(entriesPerPage));
		
		Integer searchUserOffset = 0;
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = new Integer(entriesPerPage);
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits;
		if (searchUserOffset > 0) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf("0"));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		if (userIds.length > 0) {
			org.kablink.util.search.Criteria crit = SearchUtils.entriesForTrackedMiniBlogs(userIds);
			Map results = getBinderModule().executeSearchQuery(crit, offset, maxResults);

	    	List<Map> items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);

			Map<Long, Principal> userMap = new HashMap();
			for(Map item : items) {
				Long userId = Long.valueOf((String)item.get(Constants.CREATORID_FIELD));
				if (userMap.containsKey(userId) && userMap.get(userId) == null) continue;
				Principal user = null;
				if (userMap.containsKey(userId)) {
					//If there is a user object in this table, it must be ok to see activities for this user
					user = userMap.get(userId);
				} else if (!userMap.containsKey(userId)) {
					user = getProfileDao().loadPrincipal(userId, zoneId, true);
					userMap.put(userId, null);
					if (user != null) {
						//See if the current user has access to this user's workspace
						try {
							getBinderModule().getBinder(user.getWorkspaceId());
						} catch(Exception e) {
							//No access (or whatever) to the user workspace, so skip this
							continue;
						}
					}
					userMap.put(userId, user);
				}
				if (user == null) continue;
				Map<String, Object> row = new HashMap<String, Object>();
				report.add(row);
				row.put(ReportModule.USER, user);
				row.put(ReportModule.DESCRIPTION, item.get(Constants.DESC_FIELD));
				row.put(ReportModule.DATE, item.get(Constants.MODIFICATION_DATE_FIELD));
				if (report.size() >= returnCount) break;
			}
		}
		return report;
	}
	
	public Collection<ActivityInfo> culaEsCaliente(final AuditType limitType, 
			final Date startDate, final Date endDate, final Binder binder) {
		Object[] entityTypes = new Object[] {EntityType.folder.name(), EntityType.workspace.name(),
				 EntityType.folderEntry.name()};
		return culaEsCaliente(limitType, startDate, endDate, entityTypes,
				Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBoxMax")), binder);
		
	}
	public Collection<ActivityInfo> culaEsCaliente(final AuditType limitType, 
			final Date startDate, final Date endDate, final Object[] entityTypes, final Integer returnCount) {
		return culaEsCaliente(limitType, startDate, endDate, entityTypes, returnCount, null);
	}
	
	public Collection<ActivityInfo> culaEsCaliente(final AuditType limitType, 
			final Date startDate, final Date endDate, final Object[] entityTypes, final Integer returnCount, final Binder binder) {
		List data = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(AuditTrail.class)
					.setProjection(Projections.projectionList() 
												.add(Projections.groupProperty("owningBinderId"))
												.add(Projections.groupProperty("entityId"))
												.add(Projections.groupProperty("entityType"))
												.add(Projections.alias(Projections.rowCount(), "hits"))
												.add(Projections.max("startDate")))
					.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
				    .add(Restrictions.ge("startDate", startDate))
				    .add(Restrictions.lt("startDate", endDate))
				    .add(Restrictions.in("entityType", entityTypes));
				if(limitType != null) {
					crit.add(Restrictions.eq("transactionType", limitType.name()));
				} else {
					crit.add(Restrictions.in("transactionType", new Object[] {AuditType.view.name(), AuditType.modify.name(), AuditType.download.name()}));
				}
				if(binder != null) {
					crit.add(Restrictions.like("owningBinderKey", binder.getBinderKey().getSortKey() + "%"));
				}
				crit.addOrder(Order.desc("hits"));

				return crit.list();
				
			}});
		
		List<ActivityInfo> list = new LinkedList<ActivityInfo>();
		for(Object o : data) {
			Object[] col = (Object []) o;
			String entityType = (String) col[2];
			DefinableEntity entity = null;
			try {
				if (entityType.equals(EntityType.folder.name()) || entityType.equals(EntityType.workspace.name())) {
					try {
						entity = getBinderModule().getBinder((Long) col[1]);
					} catch(Exception skipThis) {
						continue;
					}
				} else {
					try {
						entity = getFolderModule().getEntry((Long) col[0], (Long) col[1]);
					} catch(Exception skipThis) {
						continue;
					}
				}
				if (entity != null && !entity.isDeleted()) 
					list.add(new ActivityInfo(entity, (Integer) col[3], (Date) col[4]));
			} catch (Exception ignoreAccess) {continue;}
			if (list.size() >= returnCount.intValue()) break;
		}
		return list;
	}
	
	public LicenseStats getLicenseHighWaterMark(final Calendar startDate, final Calendar endDate)
	{
		List marks = (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		
				List result = session.createCriteria(LicenseStats.class)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
						.add(Restrictions.ge("snapshotDate", startDate.getTime()))
						.add(Restrictions.lt("snapshotDate", endDate.getTime()))
						.setProjection(Projections.projectionList()
								.add(Projections.max("internalUserCount"))
								.add(Projections.max("externalUserCount")))
					.list();
				return result;
			}});
		LicenseStats stats = new LicenseStats();
		try {
			Object cols[] = (Object[]) marks.get(0);
			stats.setInternalUserCount(((Long) cols[0]).longValue());
			stats.setExternalUserCount(((Long) cols[1]).longValue());
		} catch(Exception e) {
			// Ignore problems at startup that cause cols[] to have nulls
		}
		return stats;
	}

	public List<Map<String,Object>> generateReport(Collection binderIds, boolean byUser, Date startDate, Date endDate) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
    	List<Binder> binders = getCoreDao().loadObjects(binderIds, Binder.class, RequestContextHolder.getRequestContext().getZoneId());
    	
    	for (Binder binder:binders) {
    		try {
   			if (binder.isDeleted()) continue;
    			generateReport(report, binder, byUser, startDate, endDate);
    		} catch (Exception ex) {};
    		
    	}

    	return report;
	}

	private static final String[] activityTypes = new String[]
	             {AuditTrail.AuditType.add.name(), AuditTrail.AuditType.view.name(),
				  AuditTrail.AuditType.modify.name(), AuditTrail.AuditType.delete.name()};
	
	protected HashMap<String,Object> addBlankRow(List<Map<String, Object>> report, Long binderId, String title, Long parentId) {
		HashMap<String,Object> row = new HashMap<String,Object>();
		row.put(ReportModule.BINDER_ID, binderId);
		row.put(ReportModule.BINDER_TITLE, title);
		if(parentId != null) {
			row.put(ReportModule.BINDER_PARENT, parentId);
		}
		report.add(row);
		return row;
	}
	
	protected HashMap<String,Object> addBlankRow(List<Map<String, Object>> report, Binder binder) {
		return addBlankRow(report, binder.getId(), binder.getPathName(),
							binder.getParentBinder().getId());
	}

	protected HashMap<String,Object> addBlankRow(List<Map<String, Object>> report, Binder binder, final boolean byUser, Long userId) {
		HashMap<String,Object> row = addBlankRow(report, binder);
		if(byUser && userId != null) {
			row.put(ReportModule.USER_ID, userId);
		}
		for(String t : activityTypes) {
			row.put(t, new Integer(0));
		}
		return row;
	}
	
	protected void generateReport(List<Map<String, Object>> report, Binder binder, final boolean byUser, final Date startDate, final Date endDate) {
		getBinderModule().checkAccess(binder, BinderOperation.report);
		final Long binderId = binder.getId();
		final Collection<Long> userIds;
		if(byUser) {
				userIds = getBinderModule().getTeamMemberIds(binderId, true);
				if (userIds.isEmpty()) {
					addBlankRow(report, binder, byUser, null);
					return;
				}
		} else {
			userIds = null;
		}
		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
				List auditTrail = null;
				try {
					ProjectionList proj = Projections.projectionList()
									.add(Projections.groupProperty("transactionType"))
									.add(Projections.rowCount());
					if(byUser) {
						proj.add(Projections.groupProperty("startBy"));
					}
					Criteria crit = session.createCriteria(AuditTrail.class)
						.setProjection(proj)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, binder.getZoneId()))
						.add(Restrictions.like("owningBinderKey", binder.getBinderKey().getSortKey() + "%"))
						.add(Restrictions.ge("startDate", startDate))
						.add(Restrictions.lt("startDate", endDate))
						.add(Restrictions.in("transactionType", activityTypes));
					if(byUser) {
						crit.add(Restrictions.in("startBy", userIds));
						crit.addOrder(Order.asc("startBy"));
					}
					auditTrail = crit.list();
				} catch(Exception e) {
				}
				return auditTrail;
			}});
		Long lastUserId = new Long(-1);
		Long userId = null;
		HashMap<String,Object> row = null;
		for(Object o : result) {
			Object[] col = (Object []) o;
			if(byUser) { userId = (Long) col[2]; }
			if(row == null || (byUser && !lastUserId.equals(userId))) {
				row = addBlankRow(report, binder, byUser, userId);
				lastUserId = userId;
				if(byUser) {
					userIds.remove(userId);
				}
			}
			row.put((String) col[0], col[1]);
		}
		if(byUser) {
			for(Long id : userIds) {
				addBlankRow(report, binder, byUser, id);
			}
		}
		if(!byUser && result.size() == 0) {
			addBlankRow(report, binder, byUser, null);
		}
	}
	
	public List<Map<String,Object>> generateActivityReport(final Long binderId, final Long entryId) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		final Binder binder = getBinderModule().getBinder(binderId);
		getBinderModule().checkAccess(binder, BinderOperation.report);

		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List auditTrail = null;
				try {
					ProjectionList proj = Projections.projectionList()
									.add(Projections.groupProperty("transactionType"))
									.add(Projections.rowCount())
									.add(Projections.groupProperty("startBy"));
					Criteria crit = session.createCriteria(AuditTrail.class)
						.setProjection(proj)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, binder.getZoneId()))
						.add(Restrictions.eq("owningBinderId", binderId))
						.add(Restrictions.eq("entityId", entryId))
						.add(Restrictions.in("transactionType", activityTypes))
						.addOrder(Order.asc("startBy"));
					auditTrail = crit.list();
				} catch(Exception e) {
				}
				return auditTrail;
			}});
		Long lastUserId = new Long(-1);
		Long userId = null;
		HashMap<String,Object> row = null;
		for(Object o : result) {
			Object[] col = (Object []) o;
			userId = (Long) col[2];
			if(row == null || !lastUserId.equals(userId)) {
				row = new HashMap<String,Object>();
				report.add(row);
				row.put(ReportModule.USER_ID, userId);
				for(String t : activityTypes) {
					row.put(t, new Integer(0));
				}
				lastUserId = userId;
			}
			row.put((String) col[0], col[1]);
		}
    	return report;
	}

	public List<Map<String,Object>> generateActivityReportByUser(final Set<Long> userIdsToReport,
			final Date startDate, final Date endDate, final String reportType) {
        final User user = RequestContextHolder.getRequestContext().getUser();
        getAdminModule().testAccess(AdminOperation.report);

		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List auditTrail = null;
				try {
					ProjectionList proj = Projections.projectionList()
									.add(Projections.groupProperty("startBy"))
									.add(Projections.max("startDate"))
									.add(Projections.groupProperty("transactionType"));
					if (reportType.equals(ReportModule.REPORT_TYPE_SUMMARY)) {
						proj.add(Projections.rowCount());
					} else {
						proj.add(Projections.alias(Projections.rowCount(), "hits"))
									.add(Projections.groupProperty("owningBinderId"))
									.add(Projections.groupProperty("entityId"))
									.add(Projections.groupProperty("entityType"));
					}
					Criteria crit = session.createCriteria(AuditTrail.class)
						.setProjection(proj)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, user.getZoneId()))
						.add(Restrictions.in("transactionType", activityTypes))
						.add(Restrictions.ge("startDate", startDate))
						.add(Restrictions.lt("startDate", endDate))
						.add(Restrictions.in("startBy", userIdsToReport))
						.addOrder(Order.asc("startBy"));
					auditTrail = crit.list();
				} catch(Exception e) {
				}
				return auditTrail;
			}});
		
		return generateShortActivityByUserReportList(result, reportType);
	}

	public List<Map<String,Object>> generateLoginReport(final Date startDate, final Date endDate, 
			String optionType, String sortType, String sortType2, Set memberIds) {
		getAdminModule().checkAccess(AdminOperation.report);
		
		List result = new ArrayList();
		
		if(optionType.equals(WebKeys.URL_REPORT_OPTION_TYPE_SHORT)) {

			if(memberIds.size() != 0) {
				
				final Set tempIds = memberIds;
				
				result = (List) getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException {
			
						List auditTrail = session.createCriteria(LoginInfo.class)
							.setProjection(Projections.projectionList()
											.add(Projections.groupProperty("startBy"))
											.add(Projections.max("startDate"))
											.add(Projections.rowCount()))
								.add(Expression.in("startBy",tempIds.toArray()))											
								.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
								.add(Restrictions.ge("startDate", startDate))
								.add(Restrictions.lt("startDate", endDate))
							.list();
						return auditTrail;
					}});				
			}
			else {
				result = (List) getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException {
			
						List auditTrail = session.createCriteria(LoginInfo.class)
							.setProjection(Projections.projectionList()
											.add(Projections.groupProperty("startBy"))
											.add(Projections.max("startDate"))
											.add(Projections.rowCount()))
								.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
								.add(Restrictions.ge("startDate", startDate))
								.add(Restrictions.lt("startDate", endDate))
							.list();
						return auditTrail;
					}});
			}
			
			if(sortType.equals(ReportModule.USER_SORT)) {	
				Collection sortedresult = sortLoginData(result, ReportModule.USER_ID_INDEX);
				LinkedList userSortedList = new LinkedList(sortedresult);
				return generateShortLoginReportList(userSortedList);
			}
			else if(sortType.equals(ReportModule.LAST_LOGIN_SORT)) {
				Collection sortedresult = sortLoginData(result, ReportModule.LAST_LOGIN_INDEX);
				LinkedList lastLoginSortedList = new LinkedList(sortedresult);
				return generateShortLoginReportList(lastLoginSortedList);
			}
			else if(sortType.equals(ReportModule.LOGIN_COUNT_SORT)) {
				Collection sortedresult = sortLoginData(result, ReportModule.LOGIN_COUNT_INDEX);
				LinkedList loginCountSortedList = new LinkedList(sortedresult);
				return generateShortLoginReportList(loginCountSortedList);
			}
			
			return generateShortLoginReportList(result);
		}
		
		else if(optionType.equals(WebKeys.URL_REPORT_OPTION_TYPE_LONG)) {
			
			if(memberIds.size() != 0) {
				
				final Set tempIds = memberIds;
				
				result = (List) getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException {
			
						List auditTrail = session.createCriteria(LoginInfo.class)
							.setProjection(Projections.projectionList()
											.add(Projections.property("startBy"))
											.add(Projections.property("startDate")))
								.add(Expression.in("startBy",tempIds.toArray()))
								.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
								.add(Restrictions.ge("startDate", startDate))
								.add(Restrictions.lt("startDate", endDate))
								.addOrder(Order.asc("startDate"))
							.list();
						return auditTrail;
				}});				
			}
			else {
				result = (List) getHibernateTemplate().execute(new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException {
				
							List auditTrail = session.createCriteria(LoginInfo.class)
								.setProjection(Projections.projectionList()
												.add(Projections.property("startBy"))
												.add(Projections.property("startDate")))
									.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
									.add(Restrictions.ge("startDate", startDate))
									.add(Restrictions.lt("startDate", endDate))
									.addOrder(Order.asc("startDate"))
								.list();
							return auditTrail;
					}});
			}
			if(sortType2.equals(ReportModule.USER_SORT)) {	
				Collection sortedresult = sortLoginData(result, ReportModule.USER_ID_INDEX);
				LinkedList userSortedList = new LinkedList(sortedresult);
				return generateLongLoginReportList(userSortedList);
			}
			
			//default sort is by login date - nothing needs to be done
			return generateLongLoginReportList(result);
		}
		return result;
	}
	
	private Collection sortLoginData(List result, int index) {
		
		TreeMap sortedresult = new TreeMap<Object, Object[]>();
		
		for(Object o : result) {
			Object[] cols = (Object[]) o;
			
			if(index == ReportModule.LOGIN_COUNT_INDEX)
				sortedresult.put(Float.parseFloat(cols[index] + "." + cols[ReportModule.USER_ID_INDEX]), cols);
			else if (index == ReportModule.USER_ID_INDEX)
				sortedresult.put(getProfileDao().loadUser((Long)cols[ReportModule.USER_ID_INDEX],RequestContextHolder.getRequestContext().getZoneId()).getName() 
						+ cols[ReportModule.LOGIN_DATE_INDEX], cols);
			else
				sortedresult.put(cols[index], cols);	
		}
		return sortedresult.values();
	}
	
	private LinkedList<Map<String,Object>> generateShortLoginReportList(List logins) {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss aa");
		
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
		for(Object o : logins) {
			Object[] cols = (Object[]) o;
			Map<String, Object> row = new HashMap<String, Object>();
			report.add(row);
			row.put(ReportModule.USER_ID, cols[0]);
			
			Timestamp temp = ((Timestamp) cols[1]);
			
			row.put(ReportModule.LAST_LOGIN, sdFormat.format(temp.getTime()));
			row.put(ReportModule.LOGIN_COUNT, cols[2]);
		}
		return report;
	}
	
	private LinkedList<Map<String,Object>> generateLongLoginReportList(List logins) {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss aa");
		
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
		for(Object o : logins) {
			Object[] cols = (Object[]) o;
			Map<String, Object> row = new HashMap<String, Object>();
			report.add(row);
			row.put(ReportModule.USER_ID, cols[0]);
			
			Timestamp temp = ((Timestamp) cols[1]);
			
			row.put(ReportModule.LOGIN_DATE, sdFormat.format(temp.getTime()));
		}
		return report;
	}
	
	private LinkedList<Map<String,Object>> generateShortActivityByUserReportList(List activities, String reportType) {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss aa");
		
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
		if (reportType.equals(ReportModule.REPORT_TYPE_SUMMARY)) {
			Long lastUserId = new Long(-1);
			Long userId = null;
			HashMap<String,Object> row = null;
			for(Object o : activities) {
				Object[] col = (Object []) o;
				userId = (Long) col[ReportModule.USER_ID_INDEX];
				if (row == null || !lastUserId.equals(userId)) {
					row = new HashMap<String,Object>();
					report.add(row);
					row.put(ReportModule.USER_ID, userId);
					for(String t : activityTypes) {
						row.put(t, new Integer(0));
					}
					lastUserId = userId;
				}
				row.put((String) col[ReportModule.ACTIVITY_TYPE_INDEX], col[ReportModule.ACTIVITY_COUNT_INDEX]);
			}
		} else {
			for(Object o : activities) {
				Object[] cols = (Object[]) o;
				Map<String, Object> row = new HashMap<String, Object>();
				report.add(row);
				row.put(ReportModule.USER_ID, cols[ReportModule.USER_ID_INDEX]);
				
				Timestamp temp = ((Timestamp) cols[ReportModule.ACTIVITY_DATE_INDEX]);
				
				row.put(ReportModule.ACTIVITY_DATE, sdFormat.format(temp.getTime()));
				row.put(ReportModule.ACTIVITY_TYPE, cols[ReportModule.ACTIVITY_TYPE_INDEX]);
				row.put(ReportModule.BINDER_ID, cols[ReportModule.ACTIVITY_BINDER_ID_INDEX]);
				row.put(ReportModule.ENTRY_ID, cols[ReportModule.ACTIVITY_ENTRY_ID_INDEX]);
				row.put(ReportModule.ENTITY, cols[ReportModule.ACTIVITY_ENTITY_TYPE_INDEX]);
			}
		}
		return report;
	}
	
	public List<Map<String,Object>> generateWorkflowStateReport(Collection binderIds, Date startDate, Date endDate) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
    	List<Binder> binders = getCoreDao().loadObjects(binderIds, Binder.class, RequestContextHolder.getRequestContext().getZoneId());
    	
    	for (Binder binder:binders) {
    		try {
   			if (binder.isDeleted()) continue;
   			generateWorkflowStateRow(report, binder, startDate, endDate);
    		} catch (Exception ex) {};
    		
    	}

    	return report;
	}

	protected void generateWorkflowStateRow(List<Map<String, Object>> report, final Binder binder, final Date startDate, final Date endDate) {
		if (!getBinderModule().testAccess(binder, BinderOperation.report)) return;
		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List auditTrail = null;
				try {
					ProjectionList proj = Projections.projectionList()
									.add(Projections.groupProperty("definitionId"))
									.add(Projections.groupProperty("state"))
									.add(Projections.avg("startDate"))
									.add(Projections.avg("endDate"));
					Criteria crit = session.createCriteria(WorkflowHistory.class)
						.setProjection(proj)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, binder.getZoneId()))
						.add(Restrictions.like("owningBinderKey", binder.getBinderKey().getSortKey() + "%"))
						.add(Restrictions.ge("startDate", startDate))
						.add(Restrictions.lt("startDate", endDate))
						.addOrder(Order.asc("definitionId"))
						.addOrder(Order.asc("state"));
					auditTrail = crit.list();
				} catch(Exception e) {
				}
				return auditTrail;
			}});

		for(Object o : result) {
			Object[] col = (Object []) o;
			Map<String,Object> row = addBlankRow(report, binder);
			row.put(ReportModule.DEFINITION_ID, col[0]);
			row.put(ReportModule.STATE, col[1]);
			double seconds = ((Double)col[3]).doubleValue() - ((Double)col[2]).doubleValue();
			row.put(ReportModule.AVERAGE, seconds);
			row.put(ReportModule.AVERAGE_TI, new TimeInterval(seconds));
		}
	}

	public List<Map<String,Object>> generateWorkflowStateCountReport(Collection binderIds) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
    	List<Binder> binders = getCoreDao().loadObjects(binderIds, Binder.class, RequestContextHolder.getRequestContext().getZoneId());
    	
    	for (Binder binder:binders) {
    		try {
   			if (binder.isDeleted()) continue;
    			generateWorkflowStateCountRow(report, binder);
    		} catch (Exception ex) {};
    		
    	}

    	return report;
	}
	protected void generateWorkflowStateCountRow(List<Map<String, Object>> report, final Binder binder) {
		if (!getBinderModule().testAccess(binder, BinderOperation.report)) return;
		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List states = null;
				try {
					ProjectionList proj = Projections.projectionList()
						.add(Projections.groupProperty("definition.id"))
						.add(Projections.groupProperty("state"))
						.add(Projections.rowCount());
					states = session.createCriteria(WorkflowState.class)
						.setProjection(proj)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, binder.getZoneId()))
						.add(Restrictions.like("owner.owningBinderKey", binder.getBinderKey().getSortKey() + "%"))
						.addOrder(Order.asc("definition.id"))
						.addOrder(Order.asc("state"))
						.list();
				} catch(Exception e) {
				}
				return states;
			}});

		long now = Calendar.getInstance().getTime().getTime();
		for(Object o : result) {
			Object[] col = (Object []) o;
			Map<String,Object> row = addBlankRow(report, binder);
			row.put(ReportModule.DEFINITION_ID, col[0]);
			row.put(ReportModule.STATE, col[1]);
			row.put(ReportModule.COUNT, col[2]);
		}
	}

	private void accumulateValue(Map<QKey, Long> map, QKey key, Long value)
	{
		long val = 0;
		if(map.containsKey(key)) {
			val = map.get(key).longValue();
		}
		val += value.longValue();
		map.put(key, new Long(val));
	}
	
	private class QKey implements Comparable<QKey> {
		public String binderKey;
		public Long userId;
		
		public QKey(String binderKey, Long userId) {
			this.binderKey = binderKey;
			this.userId = userId;
		}
		public boolean equals(Object o) {
			if(o instanceof QKey) {
				QKey other = (QKey) o;
				return this.binderKey == other.binderKey &&
					this.userId == other.userId;
			}
			return false;
		}
		
		public int hashCode() {
			int code = 0;
			if(binderKey != null) {
				code += binderKey.hashCode();
			}
			if(userId != null) {
				code += userId.hashCode();
			}
			return code;
		}
		
		public int compareTo(QKey other) {
			int res = 0;
			if(userId != null) {
				res = userId.compareTo(other.userId);
			}
			if(res == 0 && binderKey != null) {
				res = binderKey.compareTo(other.binderKey);
			}
			return res;
		}
	}
	public List<Map<String,Object>> generateQuotaReport(final QuotaOption option, Long threshold) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
		List sizes = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List l = null;
				try {
					ProjectionList proj = Projections.projectionList()
									.add(Projections.sum("fileItem.length"));
					if(option != QuotaOption.UsersOnly) {
						proj.add(Projections.groupProperty("owner.owningBinderKey"));
					}
					if(option != QuotaOption.WorkspacesOnly) {
						proj.add(Projections.groupProperty("creation.principal.id"));
					}
					Criteria crit = session.createCriteria(VersionAttachment.class)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
						.setProjection(proj);
					l = crit.list();
				} catch(Exception e) {
					System.out.println("Unable to query for attachments: " +  e.getMessage());
				}
				return l;
			}});

		TreeMap<QKey, Long> distributedSizes = new TreeMap<QKey, Long>();
		for(Object o : sizes) {
			Object[] col = (Object []) o;
			Long size = (Long) col[0];
			int index = 1;
			String binderKey = null;
			Long userId = null;
			if(option != QuotaOption.UsersOnly) {
				binderKey = (String) col[index++];
			}
			if(option != QuotaOption.WorkspacesOnly) {
				userId = (Long) col[index++];
			}
			QKey mapKey = new QKey(binderKey, userId);
			accumulateValue(distributedSizes, mapKey, size);
			if(option != QuotaOption.UsersOnly) {
				HKey key = new HKey(binderKey);
				for(String k : key.getAncestorKeys()) {
					mapKey = new QKey(k, userId);
					accumulateValue(distributedSizes, mapKey, size);
				}
			}
		}

		List binders = null;
		HashMap<String, Binder> binderMap = new HashMap<String, Binder>();
		if(option != QuotaOption.UsersOnly) {
			binders = (List)getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					List l = null;
					try {
						l = session.createCriteria(Binder.class)
						.addOrder(Order.asc("binderKey.sortKey"))
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
						.add(Restrictions.ne("type", "template"))
						.list();
					} catch(Exception e) {
						System.out.println("Unable to load binder information" +  e.getMessage());
					}
					return l;
				}});
			for(Object o : binders) {
				Binder b = (Binder) o;
				binderMap.put(b.getBinderKey().getSortKey(), b);
			}
		}

		long thresholdBytes = threshold.longValue() * 1024 * 1024;
		for(QKey k : distributedSizes.keySet()) {
			Long size = distributedSizes.get(k);
			if(size.longValue() > thresholdBytes) {
				HashMap<String,Object> row = new HashMap<String,Object>();
				if(option != QuotaOption.UsersOnly) {
					Binder b = binderMap.get(k.binderKey);
					row.put(ReportModule.BINDER_ID, b.getId());
					row.put(ReportModule.BINDER_TITLE, b.getPathName());
				}
				if(option != QuotaOption.WorkspacesOnly) {
					row.put(ReportModule.USER_ID, k.userId);
				}
				row.put(ReportModule.SIZE, size);
				report.add(row);
			}
		}

		return report;
	}

	@SuppressWarnings("unchecked")
	public List<LicenseStats> generateLicenseReport(final Date startDate, final Date endDate) {
		getAdminModule().checkAccess(AdminOperation.report);
		return (List<LicenseStats>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		
				List auditTrail = session.createCriteria(LicenseStats.class)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
						.add(Restrictions.ge("snapshotDate", startDate))
						.add(Restrictions.lt("snapshotDate", endDate))
						.addOrder(Order.asc("snapshotDate"))
					.list();
				return auditTrail;
			}});
	}
}

class TimeInterval
{
	long weeks;
	long days;
	long hours;
	long minutes;
	long seconds;
	
	final long SECONDS_PER_MINUTE = 60;
	final long SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;
	final long SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR;
	final long SECONDS_PER_WEEK = 7 * SECONDS_PER_DAY;
	
	public TimeInterval(Double seconds)
	{
		setSeconds((long) seconds.doubleValue());
	}
	
	
	private void setSeconds(long seconds)
	{
		if(seconds < 0) {
			seconds = 0;
		}
		this.weeks = seconds / SECONDS_PER_WEEK;
		seconds -= this.weeks * SECONDS_PER_WEEK;
		this.days = seconds / SECONDS_PER_DAY;
		seconds -= this.days * SECONDS_PER_DAY;
		this.hours = seconds / SECONDS_PER_HOUR;
		seconds -= this.hours * SECONDS_PER_HOUR;
		this.minutes = seconds / SECONDS_PER_MINUTE;
		seconds -= this.minutes * SECONDS_PER_MINUTE;
		this.seconds = seconds;
	}
	
	private void appendUnit(StringBuffer buf, long n, String label)
	{
		if(n > 0) {
				buf.append(n);
			buf.append(" ");
			buf.append(label);
			buf.append(" ");
		}
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		appendUnit(buf, this.weeks, NLT.get("smallWords.weeks"));
		appendUnit(buf, this.days, NLT.get("smallWords.days"));
		appendUnit(buf, this.hours, NLT.get("smallWords.hours"));
		appendUnit(buf, this.minutes, NLT.get("smallWords.minutes"));
		appendUnit(buf, this.seconds, NLT.get("smallWords.seconds"));
		if(buf.length() > 0) {
			return buf.substring(0, buf.length() - 1);
		}
		return buf.toString();
	}
}
