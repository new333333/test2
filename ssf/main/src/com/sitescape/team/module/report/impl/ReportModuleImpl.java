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
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;

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

	public List<Map<String,Object>> generateReport(Collection binderIds, Date startDate, Date endDate) {
		LinkedList<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
    	List<Binder> binders = getCoreDao().loadObjects(binderIds, Binder.class, RequestContextHolder.getRequestContext().getZoneId());
    	
    	for (Binder binder:binders) {
    		try {
   			if (binder.isDeleted()) continue;
    			report.add(generateReport(binder, startDate, endDate));
    		} catch (Exception ex) {};
    		
    	}

    	return report;
	}

	protected Map<String,Object> generateReport(Binder binder, final Date startDate, final Date endDate) {
		final HashMap<String,Object> report = new HashMap<String,Object>();
		
		checkAccess(binder, "report");
		final Long binderId = binder.getId();
		List result = (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
				List auditTrail = null;
				try {
				auditTrail = session.createCriteria(AuditTrail.class)
						.setProjection(Projections.projectionList()
								.add(Projections.property("owningBinderId"))
								.add(Projections.rowCount())
								.add(Projections.groupProperty("transactionType")))
						.add(Restrictions.like("owningBinderKey", binder.getBinderKey().getSortKey() + "%"))
						.add(Restrictions.ge("startDate", startDate))
						.add(Restrictions.lt("startDate", endDate))
						.addOrder(Order.asc("owningBinderKey"))
						.list();
				} catch(Exception e) {
					report.put("error", e);
				}
				return auditTrail;
			}});
		report.put(ReportModule.BINDER_ID, binder.getId());
		report.put(ReportModule.BINDER_TITLE, binder.getTitle());
		if(binder.getParentBinder() != null) {
			report.put(ReportModule.BINDER_PARENT, binder.getParentBinder().getId());
		}
		for(Object o : result) {
			Object[] col = (Object []) o;
			report.put((String) col[2], col[1]);
		}
		for(AuditTrail.AuditType t : AuditTrail.AuditType.values()) {
			if(! report.containsKey(t.name())) {
				report.put(t.name(), new Integer(0));
			}
		}
		//TODO: actually generate a report for this binder
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
