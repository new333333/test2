
package com.sitescape.team.module.binder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;

/**
 * @author Janet McCann
 *
 */
public interface BinderModule {

    /**
     * 
     * @param binderId
     * @return
     * @throws NoBinderByTheIdException
     * @throws AccessControlException
     */
    public Binder getBinder(Long binderId)
		throws NoBinderByTheIdException, AccessControlException;

    /**
     * Finds a binder by path name. If no binder exists with the path name,
     * it returns <code>null</code>. If a matching binder exists but the
     * user has no access to it, it throws <code>AccessControlException</code>.
     * 
     * @param pathName
     * @return
     * @throws AccessControlException
     */
    public Binder getBinderByPathName(String pathName) throws AccessControlException;
    
	public Binder setDefinitions(Long binderId, boolean inheritFromParent)  throws AccessControlException;
    /**
     * Modify the list of definitions assocated with a binder
     * @param binderId
     * @param definitionIds
     * @throws AccessControlException
     */
    public Binder setDefinitions(Long binderId, List definitionIds) throws AccessControlException;
    /**
     * Modify the list of definitions and workflows assocated with a binder
     * @param binderId
     * @param definitionIds
     * @param workflowAssociations
     * @throws AccessControlException
     */
    public Binder setDefinitions(Long binderId, List definitionIds, Map workflowAssociations) throws AccessControlException;
    public List getCommunityTags(Long binderId) throws AccessControlException;
    public List getPersonalTags(Long binderId) throws AccessControlException;
	public void modifyTag(Long binderId, String tagId, String newTag) throws AccessControlException; 
	public void setTag(Long binderId, String newtag, boolean community) throws AccessControlException;
	public void deleteTag(Long binderId, String tagId) throws AccessControlException;

	public void addSubscription(Long binderId, int style);
	public Subscription getSubscription(Long binderId);
	public void deleteSubscription(Long binderId);
	
    public void setPosting(Long binderId, Map updates);
    public void setPosting(Long binderId, String emailAddress);
    
    public boolean hasBinders(Binder binder);
    public boolean hasBinders(Binder binder, EntityType binderType);

    public void modifyBinder(Long binderId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, WriteFilesException;
    public void modifyBinder(Long binderId, InputDataAccessor inputData) 
    	throws AccessControlException, WriteFilesException;
    public void setProperty(Long binderId, String property, Object value);
    public void deleteBinder(Long binderId) throws AccessControlException;
    public void moveBinder(Long fromId, Long toId);
    public Map executeSearchQuery(Document searchQuery);
    public Map executeSearchQuery(Document searchQuery, Map options);
    public Map executePeopleSearchQuery(Document searchQuery);
    public Map executePeopleSearchQuery(Binder binder, Document searchQuery);
    public ArrayList getSearchTags(String wordroot); 
    /**
     * Index a binder and its child binders.  Include entries
     * @param binderId
     * @param exclusions
     * @return Collection binderIds indexed
     */
    public Collection indexTree(Long binderId, Collection exclusions);
    public Collection indexTree(Long binderId);
    public void indexBinder(Long binderId);
    public void indexBinder(Long binderId, boolean includeEntries);

    public void modifyNotification(Long binderId, Map updates, Collection principals); 
    public ScheduleInfo getNotificationConfig(Long binderId);
    public void setNotificationConfig(Long binderId, ScheduleInfo config);
 
    
	public List getTeamMembers(Long binderId);
	
	/**
	 * Same as <code>getTeamMembers</code> except to access checks
	 * @param binder
	 * @return
	 */
	public List getTeamMembers(Binder binder);

	public List getTeamUserMembers(Long binderId);
	
	public List getTeamUserMembers(Binder binder);
	
	public Set getTeamUserMembersIds(Long binderId);
	
	public Set getTeamUserMembersIds(Binder binder);

	public boolean hasTeamMembers(Long binderId);
	
	public boolean hasTeamMembers(Binder binder);

	public boolean hasTeamUserMembers(Long binderId);
	
	public boolean hasTeamUserMembers(Binder binder);

	public boolean testAccess(Binder binder, String operation);

	public boolean testAccess(Long binderId, String operation);
		
	public boolean testAccessGetTeamMembers(Long binderId);
	
	public boolean testAccessGetTeamMembers(Binder binder);
	
}
