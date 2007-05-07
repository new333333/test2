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
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

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

	public List<Map<String,Object>> generateReport(Collection binderIds) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
    	List<Binder> binders = getCoreDao().loadObjects(binderIds, Binder.class, RequestContextHolder.getRequestContext().getZoneId());
    	
    	for (Binder binder:binders) {
    		try {
   			if (binder.isDeleted()) continue;
    			report.add(generateReport(binder));
    		} catch (Exception ex) {};
    		
    	}

    	return report;
	}
	/*
	 * Get all activity in a binder. Includes auditlogs on binder and its entries.  May include child binders and 
	 * their entries.
	 */
	public List<Map<String,Object>> generateReport(final Long binderId, final boolean includeChildren) {
		checkAccess("generateReport");
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		List<AuditTrail>result = (List<AuditTrail>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
				List<AuditTrail> auditTrail;
				if (includeChildren) {
					auditTrail = session.createCriteria(AuditTrail.class)
						.add(Expression.like("owningBinderKey", binder.getBinderKey().getSortKey()))
						.addOrder(Order.asc("binderId"))
						.addOrder(Order.asc("entityType"))
						.addOrder(Order.asc("entityId"))
						.list();
				} else {
					//child child binders
					auditTrail = session.createCriteria(AuditTrail.class)
						.add(Expression.eq("owningBinderId", binder.getId()))
						.addOrder(Order.asc("binderId"))
						.addOrder(Order.asc("entityType"))
						.addOrder(Order.asc("entityId"))
						.list();
				}
				
				return auditTrail;
			}});
		//TODO: actually generate a report 	- this will include workflowStateHistory items also	 
    	return report;
	}
	public List<Map<String,Object>> generateLoginReport(final Date after, final Date before) {
		checkAccess("generateLoginReport");
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		List<LoginInfo>result = (List<LoginInfo>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		
				List<LoginInfo> auditTrail = session.createCriteria(LoginInfo.class)
					.add(Expression.gt("startDate", after))
					.add(Expression.lt("endDate", before))
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
					.add(Expression.eq("definitionId", definitionId))
					.add(Expression.lt("state", state))
					.list();
				return auditTrail;
			}});
		//TODO: actually generate a report 
    	return report;
	}
	protected Map<String,Object> generateReport(Binder binder) {
		HashMap<String,Object> report = new HashMap<String,Object>();
		
		checkAccess(binder, "report");
		report.put(ReportModule.BINDER_ID, binder.getId());
		report.put(ReportModule.BINDER_NAME, binder.getName());
		//TODO: actually generate a report for this binder
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
