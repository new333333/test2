
package com.sitescape.team.module.admin;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
/**
 * @author Janet McCann
 *
 */
public interface AdminModule {
   	public boolean testAccess(String operation);
   	public boolean testAccess(WorkArea workArea, String operation);

    public Long addBinderFromTemplate(Long configId, Long parentBinderId, String title, String name) throws AccessControlException, WriteFilesException;
    public TemplateBinder addDefaultTemplate(int type);
    public TemplateBinder addDefaultTemplate(int type, String viewStyle);
	public Long addTemplate(int type, Map updates);
	public Long addTemplate(Long parentId, Long srcConfigId);
	public Long addTemplateFromBinder(Long binderId) throws AccessControlException, WriteFilesException;

	public void addFunction(String name, Set operations);
    public void addPosting(Map updates);
    public void deleteFunction(Long functionId);
    public void deletePosting(String postingId);
    public void deleteWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 

    public List getChanges(Long binderId, String operation);
    public List getChanges(Long entityId, String entityType, String operation);
    public List getFunctions();
    public List getPostings();
    public ScheduleInfo getPostingSchedule();
    public List getTeamMemberships(Long id);
	public TemplateBinder getTemplate(Long id); 
	public List getTemplates();
	public List getTemplates(int type);
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 
    public List getWorkAreaFunctionMemberships(WorkArea workArea);
	public List getWorkAreaFunctionMembershipsInherited(WorkArea workArea);
 
    public void modifyFunction(Long functionId, Map updates);
    public void modifyPosting(String postingId, Map updates);
	public void modifyTemplate(Long id, Map updates);
 
    public Map sendMail(Set ids, Set emailAddresses, String subject, Description body, List entries) throws Exception;

    public void setPostingSchedule(ScheduleInfo config) throws ParseException;
	public void setWorkAreaFunctionMemberships(WorkArea workArea, Map functionMemberships);
    public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) throws AccessControlException;
    public void setWorkAreaOwner(WorkArea workArea, Long userId);
    
 
 }