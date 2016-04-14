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
package org.kablink.teaming.module.report.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
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
import org.kablink.teaming.domain.BasicAudit;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.DeletedBinder;
import org.kablink.teaming.domain.EmailLog;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.domain.LicenseStats;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.SharingAudit;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowHistory;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.domain.EmailLog.EmailLogStatus;
import org.kablink.teaming.domain.EmailLog.EmailLogType;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.stringcheck.StringCheckException;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"deprecation", "unchecked", "unused"})
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
	protected Map<String, String> authenticatorFrequency = new HashMap<String, String>();

	private final static String AUTHENTICATOR_FREQUENCY_DAILY_STRICT = "daily_strict";
	private final static String AUTHENTICATOR_FREQUENCY_DAILY_SOFT = "daily_soft";
	private final static String AUTHENTICATOR_FREQUENCY_ALL = "all";
	private final static String AUTHENTICATOR_FREQUENCY_NONE = "none";
	private final static String AUTHENTICATOR_FREQUENCY_DEFAULT = AUTHENTICATOR_FREQUENCY_DAILY_SOFT;
	private final static long MEGABYTES = 1024L * 1024L;
	
	private static ConcurrentHashMap<String, Integer> loginInfoLastDays = new ConcurrentHashMap<String, Integer>();
	
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
	@Override
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
 		initAuthenticatorFrequency(LoginAudit.AUTHENTICATOR_ICAL);
 		initAuthenticatorFrequency(LoginAudit.AUTHENTICATOR_PORTAL);
 		initAuthenticatorFrequency(LoginAudit.AUTHENTICATOR_REMOTING_B);
 		initAuthenticatorFrequency(LoginAudit.AUTHENTICATOR_REMOTING_T);
 		initAuthenticatorFrequency(LoginAudit.AUTHENTICATOR_RSS);
 		initAuthenticatorFrequency(LoginAudit.AUTHENTICATOR_UNKNOWN);
 		initAuthenticatorFrequency(LoginAudit.AUTHENTICATOR_WEB);
 		initAuthenticatorFrequency(LoginAudit.AUTHENTICATOR_WEBDAV);
 		initAuthenticatorFrequency(LoginAudit.AUTHENTICATOR_WS);
 	}

	private void initAuthenticatorFrequency(String authenticator) {
 		authenticatorFrequency.put(authenticator, 
 				SPropsUtil.getString("audit.login.authenticator.frequency." + authenticator, AUTHENTICATOR_FREQUENCY_DEFAULT));
	}
	
	private String getAuthenticatorFrequency(String authenticator) {
		String freq = authenticatorFrequency.get(authenticator);
		if(freq != null)
			return freq;
		else
			return AUTHENTICATOR_FREQUENCY_DEFAULT;
	}
	
	@Override
	public void addAuditTrail(BasicAudit auditTrail) {
		//only log if enabled
		if (getAdminModule().isAuditTrailEnabled()) {
			if (allEnabled || enabledTypes.contains(auditTrail.getAuditType())) {
				getCoreDao().save(auditTrail);
			}
		}
	}
	public static int getLoginInfoLastDaySize() {
		return loginInfoLastDays.size();
	}
	private Integer getLoginInfoLastDay(Long zoneId, Long userId, String authenticatorName) {
		return loginInfoLastDays.get(zoneId + "." + userId + "." + authenticatorName);
	}
	private void setLoginInfoLastDay(Long zoneId, Long userId, String authenticatorName, int day) {
		loginInfoLastDays.put(zoneId + "." + userId + "." + authenticatorName, Integer.valueOf(day));
	}
	private Date getBeginningOfToday() {
		Calendar c = Calendar.getInstance(); // current time
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	@Override
	public void addAuditTrail(AuditType type, DefinableEntity entity) {
		addAuditTrail(new BasicAudit(type, RequestContextHolder.getRequestContext().getUser(), entity));
	}
	@Override
	public void addAuditTrail(AuditType type, User user, DefinableEntity entity) {
		addAuditTrail(new BasicAudit(type, user, entity));
	}
	@Override
	public void addAuditTrail(AuditType type, DefinableEntity entity, Date startDate) {
		addAuditTrail(new BasicAudit(type, RequestContextHolder.getRequestContext().getUser(), entity, startDate));	
	}
	@Override
	public void addAuditTrail(AuditType type, DefinableEntity entity, Date startDate, String description) {
		BasicAudit auditTrail = new BasicAudit(type, RequestContextHolder.getRequestContext().getUser(), entity, startDate);
		auditTrail.setAuxiliaryData(description);
		addAuditTrail(auditTrail);	
	}
	@Override
	public void addAuditTrail(AuditType type, DefinableEntity entity, String description) {
		BasicAudit auditTrail = new BasicAudit(type, RequestContextHolder.getRequestContext().getUser(), entity);
		auditTrail.setAuxiliaryData(description);
		addAuditTrail(auditTrail);
	}
	@Override
	public void addLoginInfo(LoginAudit loginInfo) {
		//only log if enabled
		if (getAdminModule().isAuditTrailEnabled()) {
			if (allEnabled || enabledTypes.contains(AuditType.login)) {
				String authenticatorFrequency = getAuthenticatorFrequency(loginInfo.getAuthenticatorName());
				if(authenticatorFrequency.equals(AUTHENTICATOR_FREQUENCY_ALL)) {
					// each event causes a new record
					getCoreDao().save(loginInfo);
				}
				else if(authenticatorFrequency.equals(AUTHENTICATOR_FREQUENCY_DAILY_STRICT)) {
					// only once record a day for this type
					List<String> loginInfoIds = getCoreDao().getLoginInfoIds(RequestContextHolder.getRequestContext().getZoneId(), 
							// NEVER get the user ID from request context. Since this method is being executed with AsAdmin context
							// during authentication, the value from the request context will always be admin.
							loginInfo.getUserId(),
							loginInfo.getAuthenticatorName(), 
							getBeginningOfToday(), 
							1);
					if(loginInfoIds.size() == 0)
						getCoreDao().save(loginInfo);
				}
				else if(authenticatorFrequency.equals(AUTHENTICATOR_FREQUENCY_DAILY_SOFT)) {
					Integer loginInfoLastDay = getLoginInfoLastDay(RequestContextHolder.getRequestContext().getZoneId(),
							loginInfo.getUserId(),
							loginInfo.getAuthenticatorName());
					int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
					if(loginInfoLastDay == null || (loginInfoLastDay.intValue() != dayOfYear)) {
						getCoreDao().save(loginInfo);
						setLoginInfoLastDay(RequestContextHolder.getRequestContext().getZoneId(),
								loginInfo.getUserId(),
								loginInfo.getAuthenticatorName(), 
							dayOfYear);
					}
				}
			}
		}	
	}

	@Override
	public void addStatusInfo(User user) {
		if (user.getStatus() != null && !user.getStatus().equals("")) {
			BasicAudit auditTrail = new BasicAudit(AuditType.userStatus, user, user);
			auditTrail.setAuxiliaryData(user.getStatus());
			addAuditTrail(auditTrail);
		}
	}

	@Override
	public void addFileInfo(AuditType type, FileAttachment attachment) {
		User user = RequestContextHolder.getRequestContext().getUser();
		addFileInfo(type, attachment, user);
	}

	@Override
	public void addFileInfo(AuditType type, FileAttachment attachment, User asUser) {
		BasicAudit audit = new BasicAudit(type, asUser, attachment.getOwner().getEntity());
		audit.setAuxiliaryData(attachment.getFileItem().getName());
		audit.setFileId(attachment.getId());
		addAuditTrail(audit);
		
	}
	
	@Override
	public void addEmailLog(EmailLog emailLog) {
		try {
			getCoreDao().save(emailLog);
		} catch (Exception ex) {
			logger.error("Failed to add an email log", ex);
		}
	}
	
	@Override
	public void addLicenseStats(LicenseStats stats) {
		getCoreDao().save(stats);
	}
	@Override
	public List<User> getUsersActivity(final DefinableEntity entity, final AuditType type, final Date startDate, final Date endDate) {
		
		List ids = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(BasicAudit.class)
					.setProjection(Projections.distinct(Projections.projectionList() 
                                                          .add(Projections.property("userId"))))
				.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, entity.getZoneId()))
				.add(Restrictions.eq("entityId", entity.getEntityIdentifier().getEntityId()))
				.add(Restrictions.eq("entityType", new Short((short)entity.getEntityIdentifier().getEntityType().getValue())))
				.add(Restrictions.eq("eventType", type.getValue()))
				.add(Restrictions.ge("eventDate", startDate))
				.add(Restrictions.lt("eventDate", endDate));
				return crit.list();
				
			}});
		return getProfileDao().loadUsers(ids, entity.getZoneId());
	}
	
	@Override
	public List<Map<String,Object>> getEntriesViewed(final Long ownerId, 
			final Date startDate, final Date endDate, Integer returnCount) {
		
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		List data = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(BasicAudit.class)
					.setProjection(Projections.distinct(Projections.projectionList() 
							.add(Projections.property("owningBinderId"))
							.add(Projections.property("entityId"))
							.add(Projections.property("eventDate"))
							.add(Projections.property("fileId"))
							.add(Projections.property("eventType"))
							.add(Projections.property("auxiliaryData"))))
					.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
					.add(Restrictions.eq("entityType", new Short((short)EntityType.folderEntry.getValue())))
				    .add(Restrictions.in("eventType", new Object[] {AuditType.view.getValue()}))
					.add(Restrictions.eq("userId", ownerId))
					.add(Restrictions.ge("eventDate", startDate))
					.add(Restrictions.lt("eventDate", endDate));
				crit.addOrder(Order.desc("eventDate"));
				return crit.list();
				
			}});
		List entriesSeen = new ArrayList();
		List filesSeen = new ArrayList();
		for(Object o : data) {
			Object[] cols = (Object[]) o;
			DefinableEntity entity = null;
			if (cols[4].equals(AuditType.view.getValue()) && entriesSeen.contains(cols[1])) continue;
			if (cols[4].equals(AuditType.download.getValue()) && filesSeen.contains(cols[3])) continue;
			Map<String, Object> row = new HashMap<String, Object>();
			try {
				entity = getFolderModule().getEntry((Long) cols[0], (Long) cols[1]);
			} catch(Exception skipThis) {
				continue;
			}
			if (entity == null || entity.isDeleted() || 
					(entity instanceof FolderEntry && ((FolderEntry)entity).isPreDeleted())) continue;
			row.put(ReportModule.ENTITY, entity);
			row.put(ReportModule.DATE, cols[2]);
			row.put(ReportModule.FILE_ID, cols[3]);
			row.put(ReportModule.TYPE, cols[4]);
			row.put(ReportModule.DESCRIPTION, cols[5]);
			report.add(row);
			if (cols[4].equals(AuditType.view.getValue())) entriesSeen.add(cols[1]);
			if (cols[4].equals(AuditType.download.getValue())) filesSeen.add(cols[3]);
			if (report.size() >= returnCount) break;
		}
		return report;
	}
	
	@Override
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
			Map results = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults,
					org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.CREATORID_FIELD,Constants.DESC_FIELD,Constants.MODIFICATION_DATE_FIELD));

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
					try {
						user = getProfileDao().loadPrincipal(userId, zoneId, true);
					} catch(Exception e) {}
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
	
	@Override
	public Collection<ActivityInfo> getActivity(final AuditType limitType, 
			final Date startDate, final Date endDate, final Binder binder) {
		Short[] entityTypes = new Short[] {(short) EntityType.folder.getValue(), 
				(short) EntityType.workspace.getValue(),
				 (short) EntityType.folderEntry.getValue()};
		return getActivity(limitType, startDate, endDate, entityTypes,
				Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBoxMax")), binder);
		
	}
	@Override
	public Collection<ActivityInfo> getActivity(final AuditType limitType, 
			final Date startDate, final Date endDate, final Short[] entityTypes, final Integer returnCount) {
		return getActivity(limitType, startDate, endDate, entityTypes, returnCount, null);
	}
	
	@Override
	public Collection<ActivityInfo> getActivity(final AuditType limitType, 
			final Date startDate, final Date endDate, final Short[] entityTypes, final Integer returnCount, final Binder binder) {
		List data = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(BasicAudit.class)
					.setProjection(Projections.projectionList() 
												.add(Projections.groupProperty("owningBinderId"))
												.add(Projections.groupProperty("entityId"))
												.add(Projections.groupProperty("entityType"))
												.add(Projections.alias(Projections.rowCount(), "hits"))
												.add(Projections.max("eventDate")))
					.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
				    .add(Restrictions.ge("eventDate", startDate))
				    .add(Restrictions.lt("eventDate", endDate))
				    .add(Restrictions.in("entityType", entityTypes));
				if(limitType != null) {
					crit.add(Restrictions.eq("eventType", limitType.getValue()));
				} else {
					crit.add(Restrictions.in("eventType", new Object[] {AuditType.view.getValue(), AuditType.modify.getValue(), AuditType.download.getValue()}));
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
			Short entityType = (Short) col[2];
			DefinableEntity entity = null;
			try {
				if (entityType.equals(EntityType.folder.getValue()) || entityType.equals(EntityType.workspace.getValue())) {
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
					list.add(new ActivityInfo(entity, ((Long) col[3]).intValue(), (Date) col[4]));
			} catch (Exception ignoreAccess) {continue;}
			if (list.size() >= returnCount.intValue()) break;
		}
		return list;
	}

	@Override
	public List<DeletedBinder> getDeletedBinderInfo(Set<Long> binderIds) {
		if (binderIds == null || binderIds.isEmpty()) return new ArrayList<DeletedBinder>();
		
		final Long[] eIds = new Long[binderIds.size()];
		Iterator iEntryIds = binderIds.iterator();
		int i = 0;
		while (iEntryIds.hasNext()) {
			eIds[i++] = new Long((Long)iEntryIds.next());
		}

		List ids = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(DeletedBinder.class)
				.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()));
				crit.add(Restrictions.in("binderId", eIds));
				crit.addOrder(Order.asc("deletedDate"));
				return crit.list();
			}});
		return ids;
	}

	@Override
	public List<ChangeLog> getDeletedBinderLogs(Set<Long> binderIds) {
		if (binderIds == null || binderIds.isEmpty()) return new ArrayList<ChangeLog>();
		
		final Long[] eIds = new Long[binderIds.size()];
		Iterator iEntryIds = binderIds.iterator();
		int i = 0;
		while (iEntryIds.hasNext()) {
			eIds[i++] = new Long((Long)iEntryIds.next());
		}
		final String[] entityTypes = new String[2];
		entityTypes[0] = EntityIdentifier.EntityType.folder.name();
		entityTypes[1] = EntityIdentifier.EntityType.workspace.name();

		List ids = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(ChangeLog.class)
				.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
				.add(Restrictions.in("entityType", entityTypes));
				crit.add(Restrictions.in("entityId", eIds));
				crit.addOrder(Order.asc("operationDate"));
				return crit.list();
			}});
		return ids;
	}

	@Override
	public List<ChangeLog> getDeletedEntryLogs(Set<Long> entryIds) {
		if (entryIds == null || entryIds.isEmpty()) return new ArrayList<ChangeLog>();
		
		final Long[] eIds = new Long[entryIds.size()];
		Iterator iEntryIds = entryIds.iterator();
		int i = 0;
		while (iEntryIds.hasNext()) {
			eIds[i++] = new Long((Long)iEntryIds.next());
		}

		List ids = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(ChangeLog.class)
				.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
				.add(Restrictions.eq("entityType", EntityIdentifier.EntityType.folderEntry.name()));
				crit.add(Restrictions.in("entityId", eIds));
				crit.addOrder(Order.asc("operationDate"));
				return crit.list();
			}});
		return ids;
	}


	@Override
	public LicenseStats getLicenseHighWaterMark(final Calendar startDate, final Calendar endDate)
	{
		List marks = (List) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				List result = null;
				try {
					result = session.createCriteria(LicenseStats.class)
					.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
					.add(Restrictions.ge("snapshotDate", startDate.getTime()))
					.add(Restrictions.lt("snapshotDate", endDate.getTime()))
					.setProjection(Projections.projectionList()
							.add(Projections.max("internalUserCount"))
							.add(Projections.max("externalUserCount"))
							.add(Projections.max("openIdUserCount"))
							.add(Projections.max("otherExtUserCount"))
							.add(Projections.max("guestAccessEnabled")))
							.list();
				} 
				catch (Exception ex) {
				}
				
				return result;
			}});
		LicenseStats stats = new LicenseStats();
		try {
			if(marks != null && marks.size() > 0) {
				Object cols[] = (Object[]) marks.get(0);
				stats.setInternalUserCount(((Long) cols[0]).longValue());
				stats.setExternalUserCount(((Long) cols[1]).longValue());
				Long openIdCount = (Long) cols[2];
				if (openIdCount == null) openIdCount = 0L;
				stats.setOpenIdUserCount(openIdCount.longValue());
				Long otherExtCount = (Long) cols[3];
				if (otherExtCount == null) otherExtCount = 0L;
				stats.setOtherExtUserCount(otherExtCount.longValue());
				Boolean guestAccessEnabled = (Boolean) cols[4];
				if (guestAccessEnabled == null) guestAccessEnabled = Boolean.FALSE;
				stats.setGuestAccessEnabled(guestAccessEnabled);
			}
		} catch(Exception e) {
			// Ignore problems at startup that cause cols[] to have nulls
		}
		return stats;
	}

	@Override
	public List<Map<String,Object>> generateReport(Collection binderIds, boolean byTeamMembers, boolean byAllUsers, 
			Date startDate, Date endDate) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
    	List<Binder> binders = getCoreDao().loadObjects(binderIds, Binder.class, 
    			RequestContextHolder.getRequestContext().getZoneId());
    	
    	for (Binder binder:binders) {
    		try {
   			if (binder.isDeleted()) continue;
    			generateReport(report, binder, byTeamMembers, byAllUsers, startDate, endDate);
    		} catch (Exception ex) {};
    		
    	}

    	return report;
	}

	private static final String[] activityTypeNames = new String[]
            {AuditType.add.name(), AuditType.view.name(), AuditType.download.name(),
			  AuditType.modify.name(), AuditType.rename.name(), AuditType.delete.name(),
			  AuditType.preDelete.name(), AuditType.restore.name(),
			  AuditType.acl.name(), 
			  AuditType.shareAdd.name(), AuditType.shareModify.name(), AuditType.shareDelete.name()};

	private static final Short[] activityTypeValues = new Short[]
            {AuditType.add.getValue(), AuditType.view.getValue(), AuditType.download.getValue(),
			  AuditType.modify.getValue(), AuditType.rename.getValue(), AuditType.delete.getValue(),
			  AuditType.preDelete.getValue(), AuditType.restore.getValue(),
			  AuditType.acl.getValue(), AuditType.shareAdd.getValue(), AuditType.shareModify.getValue(), AuditType.shareDelete.getValue()};

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
		for(String t : activityTypeNames) {
			row.put(t, new Integer(0));
		}
		return row;
	}
	
	protected void generateReport(List<Map<String, Object>> report, Binder binder, 
			final boolean byTeamMembers, final boolean byAllUsers, 
			final Date startDate, final Date endDate) {
		getBinderModule().checkAccess(binder, BinderOperation.report);
		final Long binderId = binder.getId();
		final Collection<Long> userIds;
		if(byTeamMembers) {
				userIds = getBinderModule().getTeamMemberIds(binderId, true);
				if (userIds.isEmpty()) {
					addBlankRow(report, binder, byTeamMembers, null);
					return;
				}
		} else {
			userIds = null;
		}
		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
				List auditTrail = null;
				try {
					ProjectionList proj = Projections.projectionList()
									.add(Projections.groupProperty("eventType"))
									.add(Projections.rowCount());
					if (byTeamMembers || byAllUsers) {
						proj.add(Projections.groupProperty("userId"));
					}
					Criteria crit = session.createCriteria(BasicAudit.class)
						.setProjection(proj)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, binder.getZoneId()))
						.add(Restrictions.like("owningBinderKey", binder.getBinderKey().getSortKey() + "%"))
						.add(Restrictions.ge("eventDate", startDate))
						.add(Restrictions.lt("eventDate", endDate))
						.add(Restrictions.in("eventType", activityTypeValues));
					if (byTeamMembers) {
						crit.add(Restrictions.in("userId", userIds));
					}
					if (byTeamMembers || byAllUsers) {
						crit.addOrder(Order.asc("userId"));
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
			if (byTeamMembers || byAllUsers) { userId = (Long) col[2]; }
			if (row == null || ((byTeamMembers || byAllUsers) && !lastUserId.equals(userId))) {
				if ((byTeamMembers || byAllUsers) && !lastUserId.equals(userId)) {
					row = addBlankRow(report, binder, true, userId);
				} else {
					row = addBlankRow(report, binder, false, userId);
				}
				lastUserId = userId;
				if (byTeamMembers || byAllUsers) {
					if (userIds != null && userIds.contains(userId)) userIds.remove(userId);
				}
			}
			row.put(AuditType.valueOf((Short)col[0]).name(), col[1]);
		}
		if (byTeamMembers || byAllUsers) {
			for(Long id : userIds) {
				addBlankRow(report, binder, true, id);
			}
		}
		if (!byTeamMembers && !byAllUsers && result.size() == 0) {
			addBlankRow(report, binder, false, null);
		}
	}
	
	@Override
	public List<Map<String,Object>> generateActivityReport(final Long binderId, final Long entryId) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		final FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
		Binder binder = entry.getParentBinder();
		if (getFolderModule().testAccess(entry, FolderOperation.report) || 
				(entry.hasEntryAcl() && entry.isIncludeFolderAcl() && 
						getBinderModule().testAccess(binder, BinderOperation.report))) {

			List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
				@Override
				public Object doInHibernate(Session session) throws HibernateException {
					List auditTrail = null;
					try {
						ProjectionList proj = Projections.projectionList()
										.add(Projections.groupProperty("eventType"))
										.add(Projections.rowCount())
										.add(Projections.groupProperty("userId"));
						Criteria crit = session.createCriteria(BasicAudit.class)
							.setProjection(proj)
							.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, entry.getZoneId()))
							.add(Restrictions.eq("owningBinderId", binderId))
							.add(Restrictions.eq("entityType", new Short((short)entry.getEntityType().getValue())))
							.add(Restrictions.eq("entityId", entryId))
							.add(Restrictions.in("eventType", activityTypeValues))
							.addOrder(Order.asc("userId"));
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
					for(String t : activityTypeNames) {
						row.put(t, new Integer(0));
					}
					lastUserId = userId;
				}
				row.put(AuditType.valueOf((Short) col[0]).name(), col[1]);
			}
		}
    	return report;
	}

	@Override
	public List<Map<String,Object>> generateActivityReportByUser(final Set<Long> userIdsToReport,
			final Set<Long> userIdsToSkip, final Date startDate, final Date endDate, final String reportType) {
        final User user = RequestContextHolder.getRequestContext().getUser();
        getAdminModule().checkAccess(AdminOperation.report);

		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				List auditTrail = null;
				try {
					ProjectionList proj = Projections.projectionList();
					if (reportType.equals(ReportModule.REPORT_TYPE_SUMMARY)) {
						proj.add(Projections.groupProperty("userId"));
						proj.add(Projections.groupProperty("eventType"));
						proj.add(Projections.max("eventDate"));
						proj.add(Projections.rowCount());
					} else {
						proj.add(Projections.groupProperty("userId"));
						proj.add(Projections.groupProperty("eventType"));
						proj.add(Projections.groupProperty("eventDate"));
						proj.add(Projections.alias(Projections.rowCount(), "hits"))
									.add(Projections.groupProperty("owningBinderId"))
									.add(Projections.groupProperty("entityId"))
									.add(Projections.groupProperty("entityType"))
									.add(Projections.groupProperty("auxiliaryData"));
					}
					Criteria crit = session.createCriteria(BasicAudit.class)
						.setProjection(proj)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, user.getZoneId()))
						.add(Restrictions.in("eventType", activityTypeValues))
						.add(Restrictions.ge("eventDate", startDate))
						.add(Restrictions.le("eventDate", endDate));
					if (!userIdsToSkip.isEmpty()) {
						crit.add(Restrictions.not(Restrictions.in("userId", userIdsToSkip)));
					}
					if (!userIdsToReport.isEmpty()) crit.add(Restrictions.in("userId", userIdsToReport));
					if (reportType.equals(ReportModule.REPORT_TYPE_SUMMARY)) {
						crit.addOrder(Order.asc("userId"));
					} else {
						crit.addOrder(Order.asc("eventDate"));
					}
					auditTrail = crit.list();
				} catch(Exception e) {
				}
				return auditTrail;
			}});
		
		List shareResult = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				List shareItems = null;
				try {
					ProjectionList proj = Projections.projectionList();
					if (reportType.equals(ReportModule.REPORT_TYPE_SUMMARY)) {
						proj.add(Projections.groupProperty("sharerId"));
						proj.add(Projections.groupProperty("actionType"));  
						proj.add(Projections.max("actionDate"));
						proj.add(Projections.rowCount());
					} else {
						proj.add(Projections.groupProperty("sharerId"))
							.add(Projections.groupProperty("actionType"))
							.add(Projections.groupProperty("actionDate"));
						proj.add(Projections.alias(Projections.rowCount(), "hits"))
									.add(Projections.groupProperty("owningBinderId"))
									.add(Projections.groupProperty("recipientId"))
									.add(Projections.groupProperty("recipientType"))
									.add(Projections.groupProperty("entityId"))
									.add(Projections.groupProperty("entityType"))
									.add(Projections.groupProperty("roleNameValue"))
									.add(Projections.groupProperty("entryTitle"));
					}
					Criteria crit = session.createCriteria(SharingAudit.class)
						.setProjection(proj)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, user.getZoneId()))
						.add(Restrictions.ge("actionDate", startDate))
						.add(Restrictions.le("actionDate", endDate));
					if (!userIdsToSkip.isEmpty()) {
						crit.add(Restrictions.not(Restrictions.in("sharerId", userIdsToSkip)));
					}
					if (!userIdsToReport.isEmpty()) crit.add(Restrictions.in("sharerId", userIdsToReport));
					if (reportType.equals(ReportModule.REPORT_TYPE_SUMMARY)) {
						crit.addOrder(Order.asc("sharerId"));
					} else {
						crit.addOrder(Order.asc("actionDate"));
					}
					shareItems = crit.list();
				} catch(Exception e) {
					e.getMessage();
				}
				return shareItems;
			}});
		
		return generateShortActivityByUserReportList(result, shareResult, reportType);
	}

	@Override
	public List<Map<String,Object>> generateAccessReportByUser(final Long userId,
			final Date startDate, final Date endDate, final String reportType) {
        final User user = RequestContextHolder.getRequestContext().getUser();
        getAdminModule().checkAccess(AdminOperation.report);

        int returnCount = 1000000;
        
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();

		//Get the documents bean for the documents the the user just authored or modified
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
		
		org.kablink.util.search.Criteria crit = SearchUtils.bindersByAccess(userId);
		Map results = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, offset, maxResults, 
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD,Constants.ENTITY_PATH,Constants.ENTITY_FIELD),
				userId);

    	List<Map> items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);

		for(Map item : items) {
			Long binderId = Long.valueOf((String)item.get(Constants.DOCID_FIELD));
			String binderPath = (String)item.get(Constants.ENTITY_PATH);
			String binderType = (String)item.get(Constants.ENTITY_FIELD);
			Map<String, Object> row = new HashMap<String, Object>();
			report.add(row);
			row.put(ReportModule.BINDER_ID, binderId);
			row.put(ReportModule.ENTITY_TYPE, binderType);
			row.put(ReportModule.ENTITY_PATH, binderPath);
		}
		return report;
	}

	@Override
	public List<Map<String,Object>> generateEmailReport(final Date startDate, final Date endDate, final String reportType) {
        final User user = RequestContextHolder.getRequestContext().getUser();
        getAdminModule().checkAccess(AdminOperation.report);

		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				List emailLog = null;
				try {
					ProjectionList proj = Projections.projectionList()
								.add(Projections.property("sendDate"))
								.add(Projections.property("from"))
								.add(Projections.property("subj"))
								.add(Projections.property("comments"))
								.add(Projections.property("typeStr"))
								.add(Projections.property("statusStr"))
								.add(Projections.property("toEmailAddressesStr"))
								.add(Projections.property("fileAttachmentsStr"));
					Criteria crit = session.createCriteria(EmailLog.class)
						.setProjection(proj)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()));
					
					if (ReportModule.EMAIL_REPORT_TYPE_SEND.equals(reportType)) {
						crit.add(Restrictions.in("typeStr", new Object[] {EmailLogType.binderNotification.name(), 
																	EmailLogType.sendMail.name(), 
																	EmailLogType.retry.name(), 
																	EmailLogType.workflowNotification.name(), 
																	EmailLogType.unknown.name()}));
					} else if (ReportModule.EMAIL_REPORT_TYPE_RECEIVE.equals(reportType)) {
						crit.add(Restrictions.in("typeStr", new Object[] {EmailLogType.emailPosting.name()}));
					} else if (ReportModule.EMAIL_REPORT_TYPE_ERRORS.equals(reportType)) {
						crit.add(Restrictions.in("statusStr", new Object[] {EmailLogStatus.error.name()}));
					}
					crit.add(Restrictions.ge("sendDate", startDate))
						.add(Restrictions.lt("sendDate", endDate));
					crit.addOrder(Order.desc("sendDate"));
					emailLog = crit.list();
				} catch(Exception e) {
				}
				return emailLog;

			}});
		
		return generateEmailReportList(result, reportType);
	}

	@Override
	public List<Map<String,Object>> generateXssReport(final List binderIds, final Date startDate, final Date endDate, 
			final String reportType) {
        final User user = RequestContextHolder.getRequestContext().getUser();
        getAdminModule().checkAccess(AdminOperation.report);

        int returnCount = 10000;
        int page = 0;
        int loopDetect = 1000;
    	List<Long> foundBinderIds = new ArrayList<Long>();
    	List<Long> foundItemIds = new ArrayList<Long>();
        
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();

		while (loopDetect > 0) {
			loopDetect--;
			//Get the documents bean for the documents the the user just authored or modified
			Map options = new HashMap();
			
			String entriesPerPage = String.valueOf(returnCount);
			options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(entriesPerPage));
			
			Integer searchUserOffset = 0;
			Integer searchLuceneOffset = page * returnCount;
			options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
			options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
			
			Integer maxHits = new Integer(entriesPerPage);
			options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
			
			Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits;
			if (searchUserOffset > 0) {
				intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
			}
			options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);
	
			int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
			int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
			
			org.kablink.util.search.Criteria crit = SearchUtils.entitiesByDateAndAncestor(binderIds, null, null);
			Map results = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, 
					org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD,Constants.ENTITY_FIELD,Constants.ENTITY_PATH,Constants.TITLE_FIELD,Constants.CREATORID_FIELD),
					user.getId());
			page++;
	
	    	List<Map> items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
	    	if (items.isEmpty()) break;
	
			for(Map item : items) {
				Long entityId = Long.valueOf((String)item.get(Constants.DOCID_FIELD));
				String entityType = (String)item.get(Constants.ENTITY_FIELD);
				String entityPath = (String)item.get(Constants.ENTITY_PATH);
				Long entityPathId = null;
				String entityTitle = (String)item.get(Constants.TITLE_FIELD);
				Long entityCreatorId = Long.valueOf((String)item.get(Constants.CREATORID_FIELD));
				if (EntityIdentifier.EntityType.workspace.name().equals(entityType) ||
						EntityIdentifier.EntityType.folder.name().equals(entityType)) {
					//Do the binders by loading them from the database
					foundBinderIds.add(entityId);
				}
				try {
					//See if this data has any XSS issues
					StringCheckUtil.check(item, true);
				} catch(StringCheckException e) {
					//Yes, this entity is tainted. Add it to the report
					if (EntityType.folderEntry.name().equals(entityType)) {
				    	FolderEntry fe = null;
				    	fe = (FolderEntry) coreDao.load(FolderEntry.class, Long.valueOf(entityId));
				    	if (fe != null && fe.getParentFolder() != null) {
				    		entityPath = fe.getParentFolder().getPathName() + "/" + fe.getParentFolder().getTitle();
				    		entityPathId = fe.getParentFolder().getId();
				    	}
					} else if (EntityType.workspace.name().equals(entityType) || EntityType.folder.name().equals(entityType)) {
				    	Binder b = null;
				    	b = (Binder) coreDao.load(Binder.class, Long.valueOf(entityId));
				    	if (b != null && b.getParentBinder() != null) {
				    		entityPath = b.getPathName();
				    		entityPathId = b.getParentBinder().getId();
				    	}
					}
					Map<String, Object> row = new HashMap<String, Object>();
					report.add(row);
					row.put(ReportModule.ENTITY_ID, entityId);
					row.put(ReportModule.ENTITY_TYPE, entityType);
					row.put(ReportModule.ENTITY_PATH, entityPath);
					row.put(ReportModule.ENTITY_PATH_ID, entityPathId);
					row.put(ReportModule.ENTITY_TITLE, entityTitle);
					row.put(ReportModule.ENTITY_CREATOR_ID, entityCreatorId);
					if (EntityIdentifier.EntityType.workspace.name().equals(entityType) ||
							EntityIdentifier.EntityType.folder.name().equals(entityType)) {
						//Do the binders by loading them from the database
						foundItemIds.add(Long.valueOf(entityId));
					}
				}
			}
		}
		//Now check any binders that were found
		int inClauseLimit = SPropsUtil.getInt("db.clause.limit", 1000);
		int nextId = 0;
		while (nextId < foundBinderIds.size()) {
			int lastId = nextId + inClauseLimit;
			if (lastId > foundBinderIds.size()) lastId = foundBinderIds.size();
			List<Long> ids = new ArrayList<Long>();
			ids.addAll(foundBinderIds.subList(nextId, lastId));
			nextId = nextId + inClauseLimit;
	    	List<Binder> binders = getCoreDao().loadObjects(ids, Binder.class, 
	    			RequestContextHolder.getRequestContext().getZoneId());
			for (Binder binder : binders) {
				if (!foundItemIds.contains(binder.getId())) {
					try {
						StringCheckUtil.check(binder.getDescription().getText(), true);
						StringCheckUtil.check(binder.getBranding(), true);
						StringCheckUtil.check(binder.getBrandingExt(), true);
					} catch(StringCheckException e) {
						//Yes, this entity is tainted. Add it to the report
						Map<String, Object> row = new HashMap<String, Object>();
						report.add(row);
						row.put(ReportModule.ENTITY_ID, binder.getId());
						row.put(ReportModule.ENTITY_TYPE, binder.getEntityType().name());
						row.put(ReportModule.ENTITY_PATH, binder.getPathName());
						row.put(ReportModule.ENTITY_PATH_ID, binder.getParentBinder().getId());
						row.put(ReportModule.ENTITY_TITLE, binder.getTitle());
						row.put(ReportModule.ENTITY_CREATOR_ID, binder.getOwnerId());
					} 
				}
			}
		}
		return report;
	}
	
	@Override
	public Map<String, Object> generateEntryAclReport(final Folder folder) {
		getFolderModule().checkAccess(folder, FolderOperation.report);
		Map<String, Object> report = new HashMap<String, Object>();
		//First, get the number of entries in the binder
		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				List auditTrail = null;
				try {
					ProjectionList proj = Projections.projectionList()
									.add(Projections.property("id"))
									.add(Projections.property("hasEntryAcl"))
									.add(Projections.property("checkFolderAcl"))
									.add(Projections.property("topEntry"));
					Criteria crit = session.createCriteria(FolderEntry.class)
						.setProjection(proj)
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, folder.getZoneId()))
						.add(Restrictions.like("owningBinderKey", folder.getBinderKey().getSortKey() + "%"));
					auditTrail = crit.list();
				} catch(Exception e) {
				}
				return auditTrail;
			}});

		Map<String, Principal> creators = new HashMap();
		List<Long> creatorIds = new ArrayList();
		List entryIds = new ArrayList();
		Long totalEntries = 0L;
		Long totalEntriesWithAcl = 0L;
		int i = 0;
		if (result == null) result = new ArrayList();
		while ( i < result.size() )
		{
			Object o = result.get( i );
			Object[] col = (Object []) o;
			//Filter out replies
			if (col[3] == null) {
				Map<String, Object> entry = new HashMap<String, Object>();
				entry.put(ReportModule.ENTRY_ID, col[0]);
				entry.put(ReportModule.HAS_ENTRY_ACL, col[1]);
				entry.put(ReportModule.CHECK_FOLDER_ACL, col[2]);
				if (col[1] != null && col[1].equals(Boolean.TRUE)) {
					totalEntriesWithAcl++;
					if (col[2] != null && !col[2].equals(Boolean.TRUE)) {
						//This entry has an acl and is not including the folder acl
						entryIds.add(Long.valueOf(col[0].toString()));
					}
				}
				totalEntries++;
			}
			i++;
		}
		report.put("totalEntriesWithAcl", totalEntriesWithAcl);
		report.put("totalEntries", totalEntries);
		
		//Now build the list of entries that cannot be seen
		Set<Long> allEntryIds = new HashSet<Long>(entryIds);
		Map options = new HashMap();
		String page = "0";
		int returnCount = 1000000;
		
		String entriesPerPage = String.valueOf(returnCount);
		options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(100000));
		
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
		
		org.kablink.util.search.Criteria crit = SearchUtils.entries(entryIds);
		Map results = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD,Constants.CREATORID_FIELD));

    	List<Map> items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);

		for(Map item : items) {
			Long entryId = Long.valueOf((String)item.get(Constants.DOCID_FIELD));
			Long creatorId = Long.valueOf((String)item.get(Constants.CREATORID_FIELD));
			allEntryIds.remove(entryId);
			if (!creatorIds.contains(creatorId)) 
				creatorIds.add(Long.valueOf(creatorId));
		}
		List<FolderEntry> hiddenEntries = getCoreDao().loadObjects(allEntryIds, FolderEntry.class, folder.getZoneId(), new ArrayList());
		List<Map> hiddenEntryData = new ArrayList();
		for (FolderEntry e : hiddenEntries) {
			Map he = new HashMap();
			he.put("id", e.getId());
			Principal p = e.getCreation().getPrincipal();
			p = Utils.fixProxy(p);
			he.put("creator", p);
			he.put("creationDate", e.getCreation().getDate());
			hiddenEntryData.add(he);
		}
 		report.put("totalHiddenEntries", Long.valueOf(allEntryIds.size()));
		report.put("hiddenEntries", hiddenEntryData);
		
		return report;
	}

	@Override
	public List<Map<String,Object>> generateLoginReport(final Date startDate, final Date endDate, 
			String optionType, String sortType, String sortType2, Set memberIds) {
		getAdminModule().checkAccess(AdminOperation.report);
		
		List result = new ArrayList();
		
		if(optionType.equals(WebKeys.URL_REPORT_OPTION_TYPE_SHORT)) {

			if(memberIds.size() != 0) {
				
				final Set tempIds = memberIds;
				
				result = (List) getHibernateTemplate().execute(new HibernateCallback() {
					@Override
					public Object doInHibernate(Session session) throws HibernateException {
			
						List auditTrail = session.createCriteria(LoginAudit.class)
							.setProjection(Projections.projectionList()
											.add(Projections.groupProperty("userId"))
											.add(Projections.max("loginTime"))
											.add(Projections.rowCount()))
								.add(Expression.in("userId",tempIds.toArray()))											
								.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
								.add(Restrictions.ge("loginTime", startDate))
								.add(Restrictions.lt("loginTime", endDate))
							.list();
						return auditTrail;
					}});				
			}
			else {
				result = (List) getHibernateTemplate().execute(new HibernateCallback() {
					@Override
					public Object doInHibernate(Session session) throws HibernateException {
			
						List auditTrail = session.createCriteria(LoginAudit.class)
							.setProjection(Projections.projectionList()
											.add(Projections.groupProperty("userId"))
											.add(Projections.max("loginTime"))
											.add(Projections.rowCount()))
								.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
								.add(Restrictions.ge("loginTime", startDate))
								.add(Restrictions.lt("loginTime", endDate))
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
					@Override
					public Object doInHibernate(Session session) throws HibernateException {
			
						List auditTrail = session.createCriteria(LoginAudit.class)
							.setProjection(Projections.projectionList()
											.add(Projections.property("userId"))
											.add(Projections.property("loginTime"))
											.add(Projections.property("clientAddr")))
								.add(Expression.in("userId",tempIds.toArray()))
								.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
								.add(Restrictions.ge("loginTime", startDate))
								.add(Restrictions.lt("loginTime", endDate))
								.addOrder(Order.asc("loginTime"))
							.list();
						return auditTrail;
				}});				
			}
			else {
				result = (List) getHibernateTemplate().execute(new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session) throws HibernateException {
				
							List auditTrail = session.createCriteria(LoginAudit.class)
								.setProjection(Projections.projectionList()
												.add(Projections.property("userId"))
												.add(Projections.property("loginTime"))
												.add(Projections.property("clientAddr")))
									.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
									.add(Restrictions.ge("loginTime", startDate))
									.add(Restrictions.lt("loginTime", endDate))
									.addOrder(Order.asc("loginTime"))
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
	
	private LinkedList<Map<String,Object>> generateEmailReportList(List emailLogs, String reportType) {
		User user = RequestContextHolder.getRequestContext().getUser();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, user.getLocale());
		df.setTimeZone(user.getTimeZone());
		
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		if (emailLogs == null || emailLogs.isEmpty()) return report;
		
		for(Object o : emailLogs) {
			Object[] cols = (Object[]) o;
			Map<String, Object> row = new HashMap<String, Object>();
			report.add(row);
			Timestamp sendDate = ((Timestamp) cols[ReportModule.EMAIL_LOG_SEND_DATE_INDEX]);
			
			row.put(ReportModule.EMAIL_LOG_SEND_DATE, df.format(sendDate.getTime()));
			row.put(ReportModule.EMAIL_LOG_FROM_ADDRESS, cols[ReportModule.EMAIL_LOG_FROM_ADDRESS_INDEX]);
			row.put(ReportModule.EMAIL_LOG_SUBJECT, cols[ReportModule.EMAIL_LOG_SUBJECT_INDEX]);
			row.put(ReportModule.EMAIL_LOG_COMMENT, cols[ReportModule.EMAIL_LOG_COMMENT_INDEX]);
			row.put(ReportModule.EMAIL_LOG_TYPE, cols[ReportModule.EMAIL_LOG_TYPE_INDEX]);
			row.put(ReportModule.EMAIL_LOG_STATUS, cols[ReportModule.EMAIL_LOG_STATUS_INDEX]);
			row.put(ReportModule.EMAIL_LOG_TO_ADDRESSES, cols[ReportModule.EMAIL_LOG_TO_ADDRESSES_INDEX]);
			row.put(ReportModule.EMAIL_LOG_ATTACHED_FILES, cols[ReportModule.EMAIL_LOG_ATTACHED_FILES_INDEX]);
		}
		return report;
	}
	
	private LinkedList<Map<String,Object>> generateShortLoginReportList(List logins) {
		User user = RequestContextHolder.getRequestContext().getUser();
		SimpleDateFormat sdFormat = new SimpleDateFormat(LOGIN_REPORT_DATE_FORMAT);
		sdFormat.setTimeZone(user.getTimeZone());
		
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
		for(Object o : logins) {
			Object[] cols = (Object[]) o;
			Map<String, Object> row = new HashMap<String, Object>();
			report.add(row);
			row.put(ReportModule.USER_ID, cols[0]);
			row.put(ReportModule.USER_TYPE, cols[0]);
			
			Timestamp temp = ((Timestamp) cols[1]);
			
			row.put(ReportModule.LAST_LOGIN, sdFormat.format(temp.getTime()));
			row.put(ReportModule.LOGIN_COUNT, cols[2]);
		}
		return report;
	}
	
	private LinkedList<Map<String,Object>> generateLongLoginReportList(List logins) {
		User user = RequestContextHolder.getRequestContext().getUser();
		SimpleDateFormat sdFormat = new SimpleDateFormat(LOGIN_REPORT_DATE_FORMAT);
		sdFormat.setTimeZone(user.getTimeZone());
		
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
		for(Object o : logins) {
			Object[] cols = (Object[]) o;
			Map<String, Object> row = new HashMap<String, Object>();
			report.add(row);
			row.put(ReportModule.USER_ID, cols[0]);
			row.put(ReportModule.USER_TYPE, cols[0]);
			
			Timestamp temp = ((Timestamp) cols[1]);
			row.put(ReportModule.LOGIN_DATE, sdFormat.format(temp.getTime()));
			
			row.put(ReportModule.LOGIN_CLIENT_ADDR, cols[2]);
		}
		return report;
	}
	
	private LinkedList<Map<String,Object>> generateShortActivityByUserReportList(List activities, List shareActivities, String reportType) {
		User user = RequestContextHolder.getRequestContext().getUser();
		SimpleDateFormat sdFormat = new SimpleDateFormat(LOGIN_REPORT_DATE_FORMAT);
		sdFormat.setTimeZone(user.getTimeZone());
		if (activities == null) activities = new ArrayList();
		if (shareActivities == null) shareActivities = new ArrayList();
		
		//Merge the two lists
		List mergedActivities = new ArrayList();
		if (reportType.equals(ReportModule.REPORT_TYPE_SUMMARY)) {
			//For the summary, we want one row per userId, sorted by userId
			Map<Long, List<Object[]>> shareColsById = new HashMap<Long, List<Object[]>>();
			Iterator shareActivityIter = shareActivities.iterator();
			//Build a set of lists of columns per userId
			while (shareActivityIter.hasNext()) {
				Object so = shareActivityIter.next();
				Object[] sCols = (Object[]) so;
				sCols[ReportModule.SHARE_ACTIVITY_TYPE_INDEX] = convertShareActionTypeToAuditType((Short)sCols[ReportModule.SHARE_ACTIVITY_TYPE_INDEX]);
				Long userId = (Long) sCols[ReportModule.USER_ID_INDEX];
				if (!shareColsById.containsKey(userId)) {
					shareColsById.put(userId, new ArrayList<Object[]>());
				}
				List<Object[]> shareCols = shareColsById.get(userId);
				shareCols.add(sCols);
			}
			//Now merge the columns
			for(Object o : activities) {
				Object[] cols = (Object[]) o;
				Long userId = (Long) cols[ReportModule.USER_ID_INDEX];
				if (shareColsById.containsKey(userId)) {
					List<Object[]> shareCols = shareColsById.get(userId);
					for (Object[] so : shareCols) {
						mergedActivities.add(so);
					}
					shareColsById.remove(userId);
				}
				mergedActivities.add(o);
			}			
			
		} else {
			//For the full report we want merged rows sorted by date
			Iterator shareActivityIter = shareActivities.iterator();
			Object so = null;
			Object[] sCols = null;
			if (shareActivityIter.hasNext()) {
				so = shareActivityIter.next();
				sCols = (Object[]) so;
				sCols[ReportModule.SHARE_ACTIVITY_TYPE_INDEX] = convertShareActionTypeToAuditType((Short)sCols[ReportModule.SHARE_ACTIVITY_TYPE_INDEX]);
			}
			for(Object o : activities) {
				Object[] cols = (Object[]) o;
				if (so != null) {
					Timestamp temp = ((Timestamp) cols[ReportModule.ACTIVITY_DATE_INDEX]);
					Timestamp sTemp = ((Timestamp) sCols[ReportModule.SHARE_ACTIVITY_DATE_INDEX]);
					if (temp.before(sTemp)) {
						mergedActivities.add(o);
					} else {
						mergedActivities.add(so);
						so = null;
						while (shareActivityIter.hasNext()) {
							so = shareActivityIter.next();
							sCols = (Object[]) so;
							sCols[ReportModule.SHARE_ACTIVITY_TYPE_INDEX] = convertShareActionTypeToAuditType((Short)sCols[ReportModule.SHARE_ACTIVITY_TYPE_INDEX]);
							Timestamp sTemp1 = ((Timestamp) sCols[ReportModule.SHARE_ACTIVITY_DATE_INDEX]);
							if ((temp.before(sTemp1))) {
								mergedActivities.add(o);
								o = null;
								break;
							} else {
								mergedActivities.add(so);
								so = null;
							}
						}
						if (o != null) {
							mergedActivities.add(o);
						}
					}
				} else {
					mergedActivities.add(o);
				}
			}
			if (so != null) {
				mergedActivities.add(so);
			}
			while (shareActivityIter.hasNext()) {
				so = shareActivityIter.next();
				sCols = (Object[]) so;
				sCols[ReportModule.SHARE_ACTIVITY_TYPE_INDEX] = convertShareActionTypeToAuditType((Short)sCols[ReportModule.SHARE_ACTIVITY_TYPE_INDEX]);
				mergedActivities.add(so);
			}
		}

		
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		if (mergedActivities == null) return report;
		
		if (reportType.equals(ReportModule.REPORT_TYPE_SUMMARY)) {
			Long lastUserId = new Long(-1);
			Long userId = null;
			HashMap<String,Object> row = null;
			for(Object o : mergedActivities) {
				Object[] col = (Object []) o;
				userId = (Long) col[ReportModule.USER_ID_INDEX];
				if (row == null || !lastUserId.equals(userId)) {
					row = new HashMap<String,Object>();
					report.add(row);
					row.put(ReportModule.USER_ID, userId);
					for(String t : activityTypeNames) {
						row.put(t, new Integer(0));
					}
					lastUserId = userId;
				}
				row.put(AuditType.valueOf((Short) col[ReportModule.ACTIVITY_TYPE_INDEX]).name(), col[ReportModule.ACTIVITY_COUNT_INDEX]);
			}
		} else {
			Date now = new Date();
			for(Object o : mergedActivities) {
				Object[] cols = (Object[]) o;
				Map<String, Object> row = new HashMap<String, Object>();
				report.add(row);
				row.put(ReportModule.USER_ID, cols[ReportModule.USER_ID_INDEX]);
				Timestamp temp = ((Timestamp) cols[ReportModule.ACTIVITY_DATE_INDEX]);
				row.put(ReportModule.ACTIVITY_DATE, sdFormat.format(temp.getTime()));
				AuditType auditType = AuditType.valueOf((Short) cols[ReportModule.ACTIVITY_TYPE_INDEX]);
				row.put(ReportModule.ACTIVITY_TYPE, auditType.name());
				
				if (auditType.equals(AuditType.shareAdd) || auditType.equals(AuditType.shareModify) || auditType.equals(AuditType.shareDelete)) {
					row.put(ReportModule.ACTIVITY_TYPE, ((String) row.get(ReportModule.ACTIVITY_TYPE)));
					row.put(ReportModule.ENTRY_ID, cols[ReportModule.SHARE_ACTIVITY_ENTITY_ID_INDEX]);
					row.put(ReportModule.ENTITY, EntityIdentifier.EntityType.valueOf(Integer.valueOf((Short) cols[ReportModule.SHARE_ACTIVITY_ENTITY_TYPE_INDEX])).name());
					row.put(ReportModule.SHARE_BINDER_ID, cols[ReportModule.SHARE_ACTIVITY_BINDER_ID_INDEX]);
					row.put(ReportModule.SHARE_RECIPIENT_ID, cols[ReportModule.SHARE_ACTIVITY_RECIPIENT_ID_INDEX]);
					row.put(ReportModule.SHARE_RECIPIENT_TYPE, ShareItem.RecipientType.valueOf((Short) cols[ReportModule.SHARE_ACTIVITY_RECIPIENT_TYPE_INDEX]).name());
					row.put(ReportModule.SHARE_ROLE, SharingAudit.RoleName.valueOf((Short) cols[ReportModule.SHARE_ACTIVITY_ROLE_NAME_INDEX]));
					row.put(ReportModule.SHARE_ENTRY_TITLE,  cols[ReportModule.SHARE_ACTIVITY_ENTRY_TITLE_INDEX]);
					
				} else {
					row.put(ReportModule.BINDER_ID, cols[ReportModule.ACTIVITY_BINDER_ID_INDEX]);
					row.put(ReportModule.ENTRY_ID, cols[ReportModule.ACTIVITY_ENTRY_ID_INDEX]);
					row.put(ReportModule.ENTITY, EntityIdentifier.EntityType.valueOf((Short) cols[ReportModule.ACTIVITY_ENTITY_TYPE_INDEX]).name());
					row.put(ReportModule.DESCRIPTION, cols[ReportModule.ACTIVITY_ENTITY_DESCRIPTION_INDEX]);
				}
				
				// Add the count of how many times this activity happened.
				row.put( ReportModule.COUNT, cols[ReportModule.ACTIVITY_COUNT_INDEX] );
			}
		}
		return report;
	}
	
	private Short convertShareActionTypeToAuditType(Short shareActionType) {
		AuditType auditType = AuditType.unknown;
		if (shareActionType.equals(SharingAudit.ActionType.add.getValue())) {
			auditType = AuditType.shareAdd;
		} else if (shareActionType.equals(SharingAudit.ActionType.modify.getValue())) {
			auditType = AuditType.shareModify;
		} else if (shareActionType.equals(SharingAudit.ActionType.delete.getValue())) {
			auditType = AuditType.shareDelete;
		}
		return auditType.getValue();
	}
	
	@Override
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
		SimpleDateFormat dateFormat;
		int i;

		if (!getBinderModule().testAccess(binder, BinderOperation.report)) return;
		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				List auditTrail = null;
				try {
					ProjectionList proj = Projections.projectionList()
									.add(Projections.property("definitionId"))
									.add(Projections.property("state"))
									.add(Projections.property("startDate"))
									.add(Projections.property("endDate"));
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

		// Create a SimpleDateFormat that will be used to parse the average start date and average end date.
		dateFormat = new SimpleDateFormat( "yyyyMMddHHmmssZ" );

		i = 0;
		while ( i < result.size() )
		{
			Object o;
			
			o = result.get( i );
			Object[] col = (Object []) o;
			Map<String,Object> row = addBlankRow(report, binder);
			row.put(ReportModule.DEFINITION_ID, col[0]);
			row.put(ReportModule.STATE, col[1]);
			
			i = getAverageTimeInWorkflowState( result, i, row );
		}
	}
	
	/**
	 * Get the average time spent in the given workflow state.
	 */
	private int getAverageTimeInWorkflowState(
		List results,			// Results of a db query
		int startIndex,			// Start looking at this index.
		Map<String,Object> row )// Put the average time in this row.
	{
		long avg = 0;
		long total = 0;
		int count = 0;
		boolean done;
		String definitionId;
		String stateName;
		Object obj;
		Object[] nextResults;
		
		// Get the definition id of the workflow and the name of the state.
		obj = results.get( startIndex );
		nextResults = (Object []) obj;
		definitionId = (String)nextResults[0];
		stateName = (String)nextResults[1];
		
		done = false;
		while ( done == false && startIndex < results.size() )
		{
			// Get the next result from the list of db query results.
			obj = results.get( startIndex );
			nextResults = (Object []) obj;

			// Is this result dealing with the workflow and state we are looking for? 
			if ( definitionId.equalsIgnoreCase( (String)nextResults[0] ) && stateName.equalsIgnoreCase( (String)nextResults[1] ) )
			{
				Timestamp timestamp;
				long start;
				long end;
				
				timestamp =(Timestamp)nextResults[2]; 
				start = timestamp.getTime();
				timestamp = (Timestamp)nextResults[3];
				end = timestamp.getTime();
				
				// Add the time spent in this workflow state.
				total += (end - start) / 1000;
				
				++count;
				++startIndex;
			}
			else
				done = true;
		}
		
		if ( count > 0 )
		{
			avg = total / count;
		}
		
		row.put( ReportModule.AVERAGE, avg );
		row.put( ReportModule.AVERAGE_TI, new TimeInterval((double)avg) );

		return startIndex;
	}// end getAverageTimeInWorkflowState()


	@Override
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
			@Override
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
		@Override
		public boolean equals(Object o) {
			if(o instanceof QKey) {
				QKey other = (QKey) o;
				return this.binderKey == other.binderKey &&
					this.userId == other.userId;
			}
			return false;
		}
		
		@Override
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
		
		@Override
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
	@Override
	public List<Map<String,Object>> generateQuotaReport(final QuotaOption option, Long threshold) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
		List sizes = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
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
				String[] ancestors = key.getAncestorKeys();
				if (ancestors != null) {
					for(String k : ancestors) {
						mapKey = new QKey(k, userId);
						accumulateValue(distributedSizes, mapKey, size);
					}
				}
			}
		}

		List binders = null;
		HashMap<String, Binder> binderMap = new HashMap<String, Binder>();
		if(option != QuotaOption.UsersOnly) {
			binders = (List)getHibernateTemplate().execute(new HibernateCallback() {
				@Override
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

		long thresholdBytes = threshold.longValue() * MEGABYTES;
		for(QKey k : distributedSizes.keySet()) {
			try {
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
					row.put(ReportModule.SIZE, (size+MEGABYTES-1) / MEGABYTES);
					report.add(row);
				}
			} catch(Exception e) {
				System.out.println("Bad row in Quota Report, skipped: " +  e.getMessage());
			}
		}

		return report;
	}
	
	@Override
	public List<Map<String,Object>> generateExceededDiskQuotaReport() {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
		Integer defaultQuota = getAdminModule().getQuotaDefault();
		final int i_defaultQuota = defaultQuota;
		List results = null;
		results = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				List l = null;
				try {
					
					String sql = "Select w.id, w.diskSpaceUsed, w.diskQuota, w.maxGroupsQuota "
						+ " FROM org.kablink.teaming.domain.UserPrincipal w "
						+ " WHERE w.zoneId = :zoneId"
						+ " AND w.type = 'user'"
						+ " AND ((w.diskQuota IS NOT NULL AND w.diskQuota > 0) OR (w.maxGroupsQuota IS NOT NULL AND w.maxGroupsQuota > 0)) "
						+ " AND w.diskSpaceUsed > CASE WHEN w.diskQuota IS NOT NULL AND w.diskQuota > 0 THEN (w.diskQuota * 1024 * 1024) "
						+ " ELSE (w.maxGroupsQuota * 1024 * 1024)"
						+ " END ";
					
					Query query = session.createQuery(sql)
                   	.setLong("zoneId", RequestContextHolder.getRequestContext().getZoneId());
										
					l = query.list();

					String sql2 = "Select w.id, w.diskSpaceUsed, w.diskQuota, w.maxGroupsQuota "
						+ " FROM org.kablink.teaming.domain.UserPrincipal w "
						+ " WHERE w.zoneId = :zoneId"
						+ " AND w.type = 'user'"
						+ " AND (w.diskQuota IS NULL OR w.diskQuota = '0') "
						+ " AND (w.maxGroupsQuota IS NULL OR w.maxGroupsQuota = '0') "
						+ " AND w.diskSpaceUsed > (:defaultQuota * 1024 * 1024) ";
					
					Query query2 = session.createQuery(sql2)
                   	.setLong("defaultQuota", i_defaultQuota)
                   	.setLong("zoneId", RequestContextHolder.getRequestContext().getZoneId());
										
					l.addAll(query2.list());

				} catch(Exception e) {
					System.out.println("Unable to query for quotas: " +  e.getMessage());
				}
				return l;
			}});
		
		for(int i=0; i< results.size(); i++) {
			Object[] result = (Object[]) results.get(i);
			HashMap<String,Object> row = new HashMap<String,Object>();
				row.put(ReportModule.USER_ID, result[0]);
				row.put(ReportModule.DISK_SPACE_USED, ((Long)result[1]+MEGABYTES-1) / MEGABYTES);
				row.put(ReportModule.DISKQUOTA, (result[2] == null ? 0 : (Long)result[2]));
				row.put(ReportModule.MAX_GROUPS_QUOTA, (result[3] == null ? 0 : (Long)result[3]));
				row.put(ReportModule.DEFAULT_QUOTA, defaultQuota.toString());
				report.add(row);
			}

		return report;
	}
	
	@Override
	public List<Map<String,Object>> generateExceededHighWaterDiskQuotaReport() {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
		Integer defaultQuota = getAdminModule().getQuotaDefault();
		Integer highWaterPercentage = getAdminModule().getQuotaHighWaterMark();
		final int i_defaultQuota = defaultQuota;
		final int i_highWaterPercentage = highWaterPercentage.intValue();
		
		List results = null;
		results = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				List l = null;
				try {
					String sql = "Select w.id, w.diskSpaceUsed, w.diskQuota, w.maxGroupsQuota "
						+ " FROM org.kablink.teaming.domain.UserPrincipal w "
						+ " WHERE w.zoneId = :zoneId"
						+ " AND w.type = 'user'"
						+ " AND ((w.diskQuota IS NOT NULL AND w.diskQuota > 0) OR (w.maxGroupsQuota IS NOT NULL AND w.maxGroupsQuota > 0)) "
						+ " AND w.diskSpaceUsed > ((CASE WHEN w.diskQuota IS NOT NULL AND w.diskQuota > 0 THEN (w.diskQuota * 1024 * 1024) "
						+ " ELSE (w.maxGroupsQuota * 1024 * 1024) END ) * :highWaterPercentage / 100)";

					Query query = session.createQuery(sql)
                   	.setLong("zoneId", RequestContextHolder.getRequestContext().getZoneId())
                   	.setLong("highWaterPercentage", i_highWaterPercentage);
					
					l = query.list();
					
					String sql2 = "Select w.id, w.diskSpaceUsed, w.diskQuota, w.maxGroupsQuota "
						+ " FROM org.kablink.teaming.domain.UserPrincipal w "
						+ " WHERE w.zoneId = :zoneId"
						+ " AND w.type = 'user'"
						+ " AND (w.diskQuota IS NULL OR w.diskQuota = '0') "
						+ " AND (w.maxGroupsQuota IS NULL OR w.maxGroupsQuota = '0') "
						+ " AND w.diskSpaceUsed > (:defaultQuota * 1024 * 1024 * :highWaterPercentage / 100)";
					
					Query query2 = session.createQuery(sql2)
                   	.setLong("zoneId", RequestContextHolder.getRequestContext().getZoneId())
                   	.setLong("defaultQuota", i_defaultQuota)
                   	.setLong("highWaterPercentage", i_highWaterPercentage);
										
					l.addAll(query2.list());
					
				} catch(Exception e) {
					System.out.println("Unable to query for quotas: " +  e.getMessage());
				}
				return l;
			}});
		
		for(int i=0; i< results.size(); i++) {
			Object[] result = (Object[]) results.get(i);
			HashMap<String,Object> row = new HashMap<String,Object>();
				row.put(ReportModule.USER_ID, (Long)result[0]);
				row.put(ReportModule.DISK_SPACE_USED, ((Long)result[1] + MEGABYTES - 1) / MEGABYTES);
				row.put(ReportModule.DISKQUOTA, (result[2] == null ? 0 : (Long)result[2]));
				row.put(ReportModule.MAX_GROUPS_QUOTA, (result[3] == null ? 0 : (Long)result[3]));
				row.put(ReportModule.DEFAULT_QUOTA, defaultQuota.toString());
				report.add(row);
			}

		return report;
	}
	
	@Override
	public List<Map<String,Object>> generateExternalUserReport(final Set<Long> userIds, final Set<Long> userIdsToSkip, final Date startDate, final Date endDate) {
        final User user = RequestContextHolder.getRequestContext().getUser();
        getAdminModule().checkAccess(AdminOperation.report);
        LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
        
        List results = null;
		results = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				List l = null;
				try {
					String sql = "Select w.id, w.firstName, w.lastName, w.emailAddress, w.creation.date,w.termsAndConditionsAcceptDate"
						+ " FROM org.kablink.teaming.domain.Principal w "
						+ " WHERE w.zoneId = :zoneId"
						+ " AND w.type = 'user'"
						+ " AND w.identityInfo.internal = 0 "
						+ " AND w.deleted = 0 "
						+ " AND w.disabled = 0 ";
					if(userIds!=null && userIds.size()>0){
						sql+=" AND w.id in (";
						int count=0;
						for(Long userId:userIds){
							if(count>0) sql+=",";
							sql+="'"+userId+"'";
							count++;
						}
						sql+=")";
					}

					Query query = session.createQuery(sql).setLong("zoneId", RequestContextHolder.getRequestContext().getZoneId());
					l = query.list();
				} catch(Exception e) {
					System.out.println("Unable to query for quotas: " +  e.getMessage());
				}
				return l;
			}});

		if(results!=null){
			for(int i=0; i< results.size(); i++) {
				Object[] result = (Object[]) results.get(i);
				HashMap<String,Object> row = new HashMap<String,Object>();
					row.put(ReportModule.EXTERNAL_USER_ID, (Long)result[0]);
					row.put(ReportModule.EXTERNAL_USER_FIRSTNAME,result[1]);
					row.put(ReportModule.EXTERNAL_USER_LASTNAME, result[2]);
					row.put(ReportModule.EXTERNAL_USER_EMAIL, result[3]);
					row.put(ReportModule.EXTERNAL_USER_CREATION_DATE, result[4]);
					row.put(ReportModule.EXTERNAL_USER_TERMS_ACCEPT_DATE, result[5]);
					report.add(row);
				}
		}
		return report;
	}	
	
	@Override
	public List<Map<String,Object>> generateUserDiskUsageReport(final UserQuotaOption option) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		List results = null;
		results = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				String orderBy = "w.modification_date ASC";
				if ( option != UserQuotaOption.Age) { 
					orderBy = "w.fileLength DESC"; 
				}
				
				List l = null;
				try {					
					
					String hsql = "Select w.creation_principal, w.fileLength, w.modification_date "
						+ " FROM org.kablink.teaming.domain.FileAttachment w "
						+ " WHERE w.zoneId = :zoneId"
						+ " WHERE w.creation_principal = :userId"
						+ " AND w.repositoryName != ':repo'"
						+ " ORDER BY :orderBy";
					
					Query query = session.createQuery(hsql)
                   	.setLong("zoneId", RequestContextHolder.getRequestContext().getZoneId())
                   	.setLong("userId", RequestContextHolder.getRequestContext().getUser().getId())
                   	.setString("repo", ObjectKeys.FI_ADAPTER)
                   	.setString("orderBy", orderBy);
					
					l = query.list();
					
				} catch(Exception e) {
					System.out.println("Unable to query for quotas: " +  e.getMessage());
				}
				return l;
			}});
		
		for(int i=0; i< results.size(); i++) {
			Object[] result = (Object[]) results.get(i);
			HashMap<String,Object> row = new HashMap<String,Object>();
				row.put(ReportModule.USER_ID, (Long)result[0]);
				row.put(ReportModule.SIZE, ((Long)result[1]+MEGABYTES-1)/MEGABYTES);
				row.put(ReportModule.CREATIONDATE, (Date)result[2]);

				report.add(row);
			}

		return report;
	}
	@Override
	public List<LicenseStats> generateLicenseReport(final Date startDate, final Date endDate) {
		getAdminModule().checkAccess(AdminOperation.report);
		return (List<LicenseStats>) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
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
	
	@Override
	public List<Long> getDeletedFolderEntryIds(final String family, final Date startDate, final Date endDate) {
		List data = getAuditTrails(new Short[]{AuditType.delete.getValue(), AuditType.preDelete.getValue(), AuditType.restore.getValue()}, null, family, startDate, endDate);
		
		TreeSet<Long>[] sets = effectiveAuditTrailAfterNormalizingBetweenDeletePredeleteRestore(data);
		
		TreeSet<Long> deletes = sets[0];
		TreeSet<Long> preDeletes = sets[1];
		
		deletes.addAll(preDeletes);
		
		return new ArrayList<Long>(deletes);
	}

	@Override
	public List<Long> getDeletedFolderEntryIds(final long[] folderIds, final String family, final Date startDate, final Date endDate) {
		List data = getAuditTrails(new Short[]{AuditType.delete.getValue(), AuditType.preDelete.getValue(), AuditType.restore.getValue()}, folderIds, family, startDate, endDate);

		TreeSet<Long>[] sets = effectiveAuditTrailAfterNormalizingBetweenDeletePredeleteRestore(data);
		
		TreeSet<Long> deletes = sets[0];
		TreeSet<Long> preDeletes = sets[1];
		
		deletes.addAll(preDeletes);
		
		return new ArrayList<Long>(deletes);
	}

	@Override
	public List<Long> getRestoredFolderEntryIds(final long[] folderIds, final String family, final Date startDate, final Date endDate) {
		List data = getAuditTrails(new Short[]{AuditType.delete.getValue(), AuditType.preDelete.getValue(), AuditType.restore.getValue()}, folderIds, family, startDate, endDate);

		TreeSet<Long>[] sets = effectiveAuditTrailAfterNormalizingBetweenDeletePredeleteRestore(data);
		
		TreeSet<Long> restores = sets[2];
		
		return new ArrayList<Long>(restores);
	}
	
	private List<Object> getAuditTrails(final Short[] auditTypes, final long[] folderIds, final String family, final Date startDate, final Date endDate) {
		
		List<Object> result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(BasicAudit.class)
				.setProjection(Projections.projectionList() 
						.add(Projections.property("entityId"))
						.add(Projections.property("eventType")))
				.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
				.add(Restrictions.eq("entityType", new Short((short)EntityIdentifier.EntityType.folderEntry.getValue())))
				.add(Restrictions.lt("eventDate", endDate));
				if(auditTypes != null && auditTypes.length > 0)
					crit.add(Restrictions.in("eventType", auditTypes));
				if(folderIds != null && folderIds.length > 0) {
					Long[] fIds = new Long[folderIds.length];
					for(int i = 0; i < fIds.length; i++)
						fIds[i] = Long.valueOf(folderIds[i]);
					crit.add(Restrictions.in("owningBinderId", fIds));
				}
				if(Validator.isNotNull(family))
					crit.add(Restrictions.eq("entityFamily", family));
				if(startDate != null)
					crit.add(Restrictions.ge("eventDate", startDate));
				crit.addOrder(Order.asc("eventDate"));
				return crit.list();
			}});
		
		return result;
	}
	
	private TreeSet<Long>[] effectiveAuditTrailAfterNormalizingBetweenDeletePredeleteRestore(List<Object> data) {
		TreeSet<Long> deletes = new TreeSet<Long>();
		TreeSet<Long> preDeletes = new TreeSet<Long>();
		TreeSet<Long> restores = new TreeSet<Long>();
		
		Map<Long, AuditType> entries = new HashMap<Long, AuditType>();
		
		for(Object o : data) {
			Object[] cols = (Object[]) o;
			Long entityId = (Long) cols[0];
			Short auditTypeValue = (Short) cols[1];
			if(auditTypeValue.equals(AuditType.delete.getValue())) {
				if(restores.contains(entityId)) {
					restores.remove(entityId);
				}
				else {
					if(preDeletes.contains(entityId))
						preDeletes.remove(entityId);
					deletes.add(entityId);
				}
			}
			else if(auditTypeValue.equals(AuditType.preDelete.getValue())) {
				if(restores.contains(entityId))
					restores.remove(entityId);
				else
					preDeletes.add(entityId);
			}
			else { // AuditType.restore
				if(preDeletes.contains(entityId))
					preDeletes.remove(entityId);
				else
					restores.add(entityId);
			}
		}
		return new TreeSet[] {deletes, preDeletes, restores};
	}
	
	@Override
	public List<Long> getMovedFolderEntryIds(final Date startDate, final Date endDate) {
		List ids = (List)getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(ChangeLog.class)
					.setProjection(Projections.distinct(Projections.projectionList() 
                                                          .add(Projections.property("entityId"))))
				.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
				.add(Restrictions.eq("entityType", EntityIdentifier.EntityType.folderEntry.name()))
				.add(Restrictions.eq("operation", ChangeLog.MOVEENTRY))
				.add(Restrictions.lt("operationDate", endDate));
				if(startDate != null)
					crit.add(Restrictions.ge("operationDate", startDate));
				return crit.list();
			}});
		return ids;
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
	
	@Override
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
