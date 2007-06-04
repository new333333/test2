/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */

package com.sitescape.team.module.admin;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
/**
 * @author Janet McCann
 *
 */
public interface AdminModule {
	/**
	 * The method name to be called is used as the operation.   This
	 * allows the adminModule to check for multiple rights or change requirments in the future.
	 * @param operation
	 * @return
	 */
   	public boolean testAccess(String operation);
   	/**
   	 * Same as <code>testAccess</code> 
   	 * @param workArea
   	 * @param operation
   	 * @return
   	 */
   	public boolean testAccess(WorkArea workArea, String operation);
   	/**
   	 * Create a new binder from an existing template.  If title is null, use the title from the template
   	 * @param templateId
   	 * @param parentBinderId
   	 * @param title
   	 * @param name - can be null
   	 * @return
   	 * @throws AccessControlException
   	 * @throws WriteFilesException
   	 */

    public Long addBinderFromTemplate(Long templateId, Long parentBinderId, String title, String name) throws AccessControlException, WriteFilesException;
    /**
     * Create default template.  Should already exist
     * @param type
     * @return
   	 * @throws AccessControlException
   	 * @throws WriteFilesException
    */
    public TemplateBinder addDefaultTemplate(int type);
    /**
     * Create a template from a document
     * @param document
     * @return
     */
	public Long addTemplate(Document document) throws AccessControlException;
	/**
	 * Create a template
	 * @param type
	 * @param updates
	 * @return
   	 * @throws AccessControlException
	 */
	public Long addTemplate(int type, Map updates) throws AccessControlException;
	/**
	 * Create a new template from an existing template
	 * @param parentId
	 * @param srcTemplateId
	 * @return
   	 * @throws AccessControlException
   	 * @throws WriteFilesException
	 */
	public Long addTemplate(Long parentId, Long srcTemplateId) throws AccessControlException, WriteFilesException;
	/**
	 * Create a template using an existing binder as the source.  Recurses through sub-binders
	 * @param binderId
	 * @return
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public Long addTemplateFromBinder(Long binderId) throws AccessControlException, WriteFilesException;
	/**
	 * Add a new function
	 * @param name
	 * @param operations
	 * @throws AccessControlException
	 */
	public void addFunction(String name, Set<WorkAreaOperation>operations) throws AccessControlException;
	/**
	 * Add a posting definition, used to receive incoming mail
	 * @param updates
	 * @throws AccessControlException
	 */
	public void addPosting(Map updates) throws AccessControlException;
	/**
	 * Delete an existing function
	 * @param functionId
	 * @throws AccessControlException
	 */
    public void deleteFunction(Long functionId) throws AccessControlException;
    /**
     * Delete a email posting definition
     * @param postingId
     * @throws AccessControlException
     */
    public void deletePosting(String postingId) throws AccessControlException;
    /**
     * Remove a function from the workArea
     * @param workArea
     * @param functionId
     * @throws AccessControlException
     */
    public void deleteWorkAreaFunctionMembership(WorkArea workArea, Long functionId) throws AccessControlException; 
    /**
     * Get change logs for a binder and its entries.  Null operation returns all changes.  Must have had access to 
     * the entity at the time of the change to see the change log.
     * @param binderId
     * @param operation
     * @return ChangeLogs ordered by entity and operationDate
     */
    public List<ChangeLog> getChanges(Long binderId, String operation);
    /**
     * Same as <code>getChanges</code> but only return changes for one entity. 
     * @param entityIdentifier
     * @param operation
     * @return ChangeLogs ordered by entity and operationDate
     */
    public List<ChangeLog> getChanges(EntityIdentifier entityIdentifier, String operation);
    /**
     * Get system functions
     * @return
     */
    public List<Function> getFunctions();
    /**
     * Get system posting definitions
     * @return
     */
    public List<PostingDef> getPostings();
    /**
     * Get schedule information for receiving incoming email
     * @return
     */
    public ScheduleInfo getPostingSchedule();
    /**
     * Get a template
     * @param id
     * @return
     */
 	public TemplateBinder getTemplate(Long id); 
 	/**
 	 * Get all system top level templates
 	 * @return
 	 */
	public List<TemplateBinder> getTemplates();
	/**
	 * Build a document used to export/import templates
	 * @param template
	 * @return
	 */
	public Document getTemplateAsXml(TemplateBinder template);
	/**
	 * Get all top level templates of the specified type
	 * @param type
	 * @return
	 */
	public List<TemplateBinder> getTemplates(int type);
	
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 
    public List<WorkAreaFunctionMembership> getWorkAreaFunctionMemberships(WorkArea workArea);
	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMembershipsInherited(WorkArea workArea);
 
    public void modifyFunction(Long functionId, Map updates);
    public void modifyPosting(String postingId, Map updates);
	public void modifyTemplate(Long id, Map updates);
 
	/**
	 * Send a mail message to a collection of users and/or explicit email address.  Include attachments and ICals from entries if specified  
	 * @param ids
	 * @param emailAddresses
	 * @param subject
	 * @param body
	 * @param entries
	 * @param sendAttachments
	 * @return
	 * @throws Exception
	 */
    public Map<String, Object> sendMail(Collection<Long> ids, Collection<String> emailAddresses, String subject, Description body, Collection<DefinableEntity> entries, boolean sendAttachments) throws Exception;

    public void setPostingSchedule(ScheduleInfo config) throws ParseException;
	public void setWorkAreaFunctionMemberships(WorkArea workArea, Map functionMemberships);
    public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) throws AccessControlException;
    public void setWorkAreaOwner(WorkArea workArea, Long userId);
    
 
 }