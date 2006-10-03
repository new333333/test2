
package com.sitescape.ef.module.binder;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import org.dom4j.Document;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.BinderConfig;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.search.LuceneSessionFactory;
import com.sitescape.ef.security.AccessControlException;

/**
 * @author Janet McCann
 *
 */
public interface BinderModule {
	/**
     * Find a binder of given name. 
     * 
     * @param binderName
     * @return
     * @throws NoBinderByTheNameException
     * @throws AccessControlException
     */
    public Binder getBinderByName(String binderName) 
		throws NoBinderByTheNameException, AccessControlException;
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
    
	public Binder setConfiguration(Long binderId, boolean inheritFromParent)  throws AccessControlException;
    /**
     * Modify the list of definitions assocated with a binder
     * @param binderId
     * @param definitionIds
     * @throws AccessControlException
     */
    public Binder setConfiguration(Long binderId, List definitionIds) throws AccessControlException;
    /**
     * Modify the list of definitions and workflows assocated with a binder
     * @param binderId
     * @param definitionIds
     * @param workflowAssociations
     * @throws AccessControlException
     */
    public Binder setConfiguration(Long binderId, List definitionIds, Map workflowAssociations) throws AccessControlException;
    public List getCommunityTags(Long binderId) throws AccessControlException;
    public List getPersonalTags(Long binderId) throws AccessControlException;
	public void modifyTag(Long binderId, String tagId, String newTag) throws AccessControlException; 
	public void setTag(Long binderId, String newtag, boolean community) throws AccessControlException;
	public void deleteTag(Long binderId, String tagId) throws AccessControlException;

	public void addSubscription(Long binderId, int style);
	public Subscription getSubscription(Long binderId);
	public void deleteSubscription(Long binderId);
	public void modifySubscription(Long binderId, Map updates);
	
    public boolean hasBinders(Binder binder);
    
    public void modifyBinder(Long binderId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, WriteFilesException;
    public void modifyBinder(Long binderId, InputDataAccessor inputData) 
    	throws AccessControlException, WriteFilesException;
    public void setProperty(Long binderId, String property, Object value);
    public void checkModifyBinderAllowed(Binder binder) throws AccessControlException;
    public void deleteBinder(Long binderId) throws AccessControlException;
    public void checkDeleteBinderAllowed(Binder binder) throws AccessControlException;

    public void moveBinder(Long fromId, Long toId);
    public void checkMoveBinderAllowed(Binder binder);
    public List executeSearchQuery(Document searchQuery);
    public List executeSearchQuery(Binder binder, Document searchQuery);
    public List executePeopleSearchQuery(Document searchQuery);
    public List executePeopleSearchQuery(Binder binder, Document searchQuery);
    
    public void indexTree(Long binderId);
    public void indexBinder(Long binderId);

    
	public BinderConfig createDefaultConfiguration(int type);
	public String addConfiguration(int type, String title);
	public void modifyConfiguration(String id, Map updates);
	public void deleteConfiguration(String id);
	public BinderConfig getConfiguration(String id); 
	public List getConfigurations();
	public List getConfigurations(int type);
}
