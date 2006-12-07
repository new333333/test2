/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.module.admin;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.domain.BinderConfig;
import com.sitescape.ef.jobs.ScheduleInfo;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.ef.security.function.WorkAreaFunctionMembership;
/**
 * @author Janet McCann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface AdminModule {
	public void addZone(String zoneName);
	//TODO: temporary
	public void setZone1();
	public void setZone2();

	public void addFunction(Function function);
    public void modifyFunction(Long functionId, Map updates);
    public void deleteFunction(Long functionId);
    public List getFunctions();

    public ScheduleInfo getPostingSchedule();
    public void setPostingSchedule(ScheduleInfo config) throws ParseException;

    public List getPostings();
    public void modifyPosting(String postingId, Map updates);
    public void addPosting(Map updates);
    public void deletePosting(String postingId);

    public BinderConfig createDefaultConfiguration(int type);
	public String addConfiguration(int type, String title);
	public void modifyConfiguration(String id, Map updates);
	public void deleteConfiguration(String id);
	public BinderConfig getConfiguration(String id); 
	public List getConfigurations();
	public List getConfigurations(int type);

	public void addWorkAreaFunctionMembership(WorkArea workArea, Long functionId, Set memberIds);
    public void modifyWorkAreaFunctionMembership(WorkArea workArea, WorkAreaFunctionMembership membership);
    public void deleteWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 
    public List getWorkAreaFunctionMemberships(WorkArea workArea);
	public List getWorkAreaFunctionMembershipsInherited(WorkArea workArea);
    public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) throws AccessControlException;
    public List getTeamMemberships(Long id);

}