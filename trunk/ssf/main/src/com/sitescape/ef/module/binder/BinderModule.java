
package com.sitescape.ef.module.binder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.security.AccessControlException;

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
	
    public void modifyPosting(Long binderId, Map updates);
    public void setPosting(Long binderId, String postingId);
    public void deletePosting(Long binderId);

    public boolean hasBinders(Binder binder);
    
    public void modifyBinder(Long binderId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, WriteFilesException;
    public void modifyBinder(Long binderId, InputDataAccessor inputData) 
    	throws AccessControlException, WriteFilesException;
    public void setProperty(Long binderId, String property, Object value);
    public void checkModifyBinderAllowed(Binder binder) throws AccessControlException;
    public void deleteBinder(Long binderId) throws AccessControlException;
    public void checkDeleteBinderAllowed(Binder binder) throws AccessControlException;
    public void checkAdminBinderAllowed(Binder binder) throws AccessControlException;
    public void moveBinder(Long fromId, Long toId);
    public void checkMoveBinderAllowed(Binder binder);
    public Map executeSearchQuery(Document searchQuery);
    public Map executeSearchQuery(Binder binder, Document searchQuery);
    public Map executePeopleSearchQuery(Document searchQuery);
    public Map executePeopleSearchQuery(Binder binder, Document searchQuery);
    
    public void indexTree(Long binderId);
    public void indexBinder(Long binderId);

    
	public List getTeamMembers(Long binderId);
}
