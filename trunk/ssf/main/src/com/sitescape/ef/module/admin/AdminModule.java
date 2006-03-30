/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.module.admin;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public void addFunction(Function function);
    public List getFunctions();
    public void modifyFunction(Long functionId, Map updates);
    public void modifyNotification(Long binderId, Map updates, Set users); 
    public void setEnableNotification(Long binderId, boolean enable);
    public ScheduleInfo getNotificationConfig(Long binderId);
    public void setNotificationConfig(Long binderId, ScheduleInfo config);
    public void setEnablePostings(boolean enable);
    public ScheduleInfo getPostingSchedule();
    public void setPostingSchedule(ScheduleInfo config) throws ParseException;
    public List getPostingDefs();
    public void modifyPosting(Long binderId, String postingId, Map updates);
    public void addPosting(Long binderId, Map updates);
    public void deletePosting(Long binderId, String postingId);
    public List getEmailAliases();
    public void modifyEmailAlias(String aliasId, Map updates);
    public void addEmailAlias(Map updates);
    public void deleteEmailAlias(String aliasId);
    public void addWorkAreaFunctionMembership(WorkArea workArea, WorkAreaFunctionMembership membership);
    public void modifyWorkAreaFunctionMembership(WorkArea workArea, WorkAreaFunctionMembership membership);
    public void deleteWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 
    public List getWorkAreaFunctionMemberships(WorkArea workArea);
	public List getWorkAreaFunctionMembershipsInherited(WorkArea workArea);
    public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) throws AccessControlException;
}