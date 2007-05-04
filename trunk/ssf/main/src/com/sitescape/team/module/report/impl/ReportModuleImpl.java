package com.sitescape.team.module.report.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;

public class ReportModuleImpl extends CommonDependencyInjection implements ReportModule {

	public void addLoginInfo(LoginInfo loginInfo) {
		// TODO To be written by Janet
		
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
