
package com.sitescape.ef.module.binder;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import org.dom4j.Document;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.shared.InputDataAccessor;
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
    public List executeSearchQuery(Binder binder, Document searchQuery);
}
