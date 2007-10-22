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
package com.sitescape.team.module.report.impl;

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

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.domain.AuditTrail;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.HKey;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.LicenseStats;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.VersionAttachment;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowStateHistory;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.admin.AdminModule.AdminOperation;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.security.AccessControlManager;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SpringContextUtil;

public class ReportModuleImpl extends HibernateDaoSupport implements ReportModule {
	protected Set enabledTypes=new HashSet();
	protected boolean allEnabled=false;
	protected CoreDao coreDao;
	protected AccessControlManager accessControlManager;
	protected BinderModule binderModule;
	protected AdminModule adminModule;
	public void setAccessControlManager(AccessControlManager accessControlManager) {
		this.accessControlManager = accessControlManager;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}
	protected AccessControlManager getAccessControlManager() {
		return accessControlManager;
	}
	protected CoreDao getCoreDao() {
		return coreDao;
	}
	
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}

	public BinderModule getBinderModule() {
		return this.binderModule;
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

	public void addFileInfo(AuditTrail.AuditType type, FileAttachment attachment) {
		AuditTrail audit = new AuditTrail(type, RequestContextHolder.getRequestContext().getUser(), attachment.getOwner().getEntity());
		audit.setDescription(attachment.getFileItem().getName());
		audit.setFileId(attachment.getId());
		addAuditTrail(audit);
		
	}
	public void addWorkflowStateHistory(WorkflowStateHistory workflowStateHistory) {
		addAuditTrail(workflowStateHistory);
		
	}
	public void addWorkflowStateHistory(WorkflowState state, HistoryStamp end, boolean isEnded) {
		addAuditTrail(new WorkflowStateHistory(state, end, isEnded));		
	}

	public void addLicenseStats(LicenseStats stats) {
		getCoreDao().save(stats);
	}
	
	public LicenseStats getLicenseHighWaterMark(final Calendar startDate, final Calendar endDate)
	{
		List marks = (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		
				List result = session.createCriteria(LicenseStats.class)
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
		Binder binder = getBinderModule().getBinder(binderId);
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

	public List<Map<String,Object>> generateLoginReport(final Date startDate, final Date endDate) {
		getAdminModule().checkAccess(AdminOperation.report);
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		List result = (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		
				List auditTrail = session.createCriteria(LoginInfo.class)
					.setProjection(Projections.projectionList()
									.add(Projections.groupProperty("startBy"))
									.add(Projections.max("startDate"))
									.add(Projections.rowCount()))
						.add(Restrictions.ge("startDate", startDate))
						.add(Restrictions.lt("startDate", endDate))
					.list();
				return auditTrail;
			}});
		//TODO: actually generate a report 
		for(Object o : result) {
			Object[] cols = (Object[]) o;
			Map<String, Object> row = new HashMap<String, Object>();
			report.add(row);
			row.put(ReportModule.USER_ID, cols[0]);
			row.put(ReportModule.LAST_LOGIN, cols[1]);
			row.put(ReportModule.LOGIN_COUNT, cols[2]);
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
					Criteria crit = session.createCriteria(WorkflowStateHistory.class)
						.setProjection(proj)
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
						.add(Restrictions.ge("snapshotDate", startDate))
						.add(Restrictions.lt("snapshotDate", endDate))
						.addOrder(Order.asc("zoneId"))
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
