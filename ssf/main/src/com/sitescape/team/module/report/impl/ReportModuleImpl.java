package com.sitescape.team.module.report.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Criteria;
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
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowStateHistory;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.AccessControlManager;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.SPropsUtil;

public class ReportModuleImpl extends HibernateDaoSupport implements ReportModule {
	protected Set enabledTypes=new HashSet();
	protected boolean allEnabled=false;
	protected CoreDao coreDao;
	protected AccessControlManager accessControlManager;
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

	public void addWorkflowStateHistory(WorkflowStateHistory workflowStateHistory) {
		addAuditTrail(workflowStateHistory);
		
	}
	public void addWorkflowStateHistory(WorkflowState state, HistoryStamp end, boolean isEnded) {
		addAuditTrail(new WorkflowStateHistory(state, end, isEnded));		
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
	
	protected void generateReport(List<Map<String, Object>> report, Binder binder, final boolean byUser, final Date startDate, final Date endDate) {
		checkAccess(binder, "report");
		final Long binderId = binder.getId();
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
					if(byUser) { crit.addOrder(Order.asc("startBy")); }
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
				row = new HashMap<String,Object>();
				row.put(ReportModule.BINDER_ID, binder.getId());
				row.put(ReportModule.BINDER_TITLE, binder.getTitle());
				if(binder.getParentBinder() != null) {
					row.put(ReportModule.BINDER_PARENT, binder.getParentBinder().getId());
				}
				if(byUser) {
					row.put(ReportModule.USER_ID, userId);
				}
				for(String t : activityTypes) {
					row.put(t, new Integer(0));
				}
				report.add(row);
				lastUserId = userId;
			}
			row.put((String) col[0], col[1]);
		}
	}
	
	public List<Map<String,Object>> generateUserReport(final Date startDate, final Date endDate)
	{	
		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List auditTrail = null;
				try {
				auditTrail = session.createCriteria(AuditTrail.class)
						.setProjection(Projections.projectionList()
								.add(Projections.groupProperty("startBy"))
								.add(Projections.rowCount())
								.add(Projections.groupProperty("transactionType")))
						.add(Restrictions.ge("startDate", startDate))
						.add(Restrictions.lt("startDate", endDate))
						.add(Restrictions.in("transactionType", activityTypes))
						.addOrder(Order.asc("startBy"))
						.list();
				} catch(Exception e) {
				}
				return auditTrail;
			}});
		
		List<Map<String, Object>> report = new LinkedList<Map<String, Object>>();
		Long lastId = new Long(-1);
		HashMap<String,Object> row = null;
		for(Object o : result) {
			Object[] col = (Object []) o;
			Long id = (Long) col[0];
			if(!id.equals(lastId)) {
				row = new HashMap<String,Object>();
				row.put(ReportModule.USER_ID, id);
				for(String t : activityTypes) {
					row.put(t, new Integer(0));
				}
				report.add(row);
				lastId = id;
			}
			row.put((String) col[2], col[1]);
		}

		return report;
	}
	
	public List<Map<String,Object>> generateLoginReport(final Date after, final Date before) {
		checkAccess("generateLoginReport");
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		List<LoginInfo>result = (List<LoginInfo>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		
				List<LoginInfo> auditTrail = session.createCriteria(LoginInfo.class)
					.add(Restrictions.gt("startDate", after))
					.add(Restrictions.lt("endDate", before))
					.list();
				return auditTrail;
			}});
		//TODO: actually generate a report 
    	return report;
	}
	public List<Map<String,Object>> generateWorkflowStateReport(final String definitionId, final String state) {
		checkAccess("generateWorkflowStateReport");
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		List<WorkflowStateHistory>result = (List<WorkflowStateHistory>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		
				List<WorkflowStateHistory> auditTrail = session.createCriteria(WorkflowStateHistory.class)
					.add(Restrictions.eq("definitionId", definitionId))
					.add(Restrictions.lt("state", state))
					.list();
				return auditTrail;
			}});
		//TODO: actually generate a report 
    	return report;
	}

	public boolean testAccess(String operation) {
		try {
			checkAccess(operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	
	protected void checkAccess(Binder binder, String operation) {
		checkAccess(operation);
	}
	
	protected void checkAccess(String operation) {
		getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(), WorkAreaOperation.SITE_ADMINISTRATION);
	}


}
