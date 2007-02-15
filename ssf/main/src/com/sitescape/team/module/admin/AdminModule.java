
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


	public void addZone(String zoneName);
	//TODO: temporary
	public void setZone1(String zoneName);
	public void setZone2(String zoneName);

	public void addFunction(String name, Set operations);
    public void modifyFunction(Long functionId, Map updates);
    public void deleteFunction(Long functionId);
    public List getFunctions();

    public ScheduleInfo getPostingSchedule();
    public void setPostingSchedule(ScheduleInfo config) throws ParseException;

    public List getPostings();
    public void modifyPosting(String postingId, Map updates);
    public void addPosting(Map updates);
    public void deletePosting(String postingId);

    public TemplateBinder createDefaultTemplate(int type);
	public Long addTemplate(int type, Map updates);
	public Long addTemplate(Long parentId, Long srcConfigId);
	public Long addTemplateFromBinder(Long binderId) throws AccessControlException, WriteFilesException;
	public void modifyTemplate(Long id, Map updates);
	public TemplateBinder getTemplate(Long id); 
	public List getTemplates();
	public List getTemplates(int type);
    public Long addBinderFromTemplate(Long configId, Long parentBinderId, String title) throws AccessControlException, WriteFilesException;

	public void setWorkAreaFunctionMemberships(WorkArea workArea, Map functionMemberships);
    public void deleteWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 
    public List getWorkAreaFunctionMemberships(WorkArea workArea);
	public List getWorkAreaFunctionMembershipsInherited(WorkArea workArea);
    public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) throws AccessControlException;
    public List getTeamMemberships(Long id);
    
    public List getChanges(Long binderId, String operation);
    public List getChanges(Long entityId, String entityType, String operation);

    /**
     * Send mail
     * @param ids - Set on principal ids
     * @param emailAddresses - Set if Strings
     * @param subject
     * @param body
     * @return - Map - status and list of errors
     **/
    public Map sendMail(Set ids, Set emailAddresses, String subject, Description body, List entries) throws Exception;
}