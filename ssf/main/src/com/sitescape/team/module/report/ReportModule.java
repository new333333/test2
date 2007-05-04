package com.sitescape.team.module.report;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.LoginInfo;

public interface ReportModule {

	public static final String BINDER_ID = "binder_id";
	public static final String BINDER_NAME = "binder_name";
		
	public void addLoginInfo(LoginInfo loginInfo);
	
	public List<Map<String, Object>> generateReport(Collection ids);

    public boolean testAccess(String operation);
}
