/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */

package org.kablink.teaming.module.admin;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.IndexNode;
import org.kablink.teaming.domain.MailConfig;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;

/**
 * @author Janet McCann
 *
 */
public interface AdminModule {
	public enum AdminOperation {
		manageFunction,
		manageMail,
		manageTemplate,
		report,
		manageFunctionMembership,
		manageErrorLogs
	}
	/**
	 * The method name to be called is used as the operation.   This
	 * allows the adminModule to check for multiple rights or change requirments in the future.
	 * @param operation
	 * @return
	 */
   	public boolean testAccess(AdminOperation operation);
   	public void checkAccess(AdminOperation operation) throws AccessControlException;
   	/**
   	 * Same as <code>testAccess</code> 
   	 * @param workArea
   	 * @param operation
   	 * @return
   	 */
   	public boolean testAccess(WorkArea workArea, AdminOperation operation);
   	public void checkAccess(WorkArea workArea, AdminOperation operation) throws AccessControlException;
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
	 * @return List of workspace function memberships
	 */
    public List deleteFunction(Long functionId);
    /**
     * Delete a email posting definition
     * @param postingId
     * @throws AccessControlException
     */
    public void deletePosting(String postingId) throws AccessControlException;
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
    public MailConfig getMailConfig();
    public List<ChangeLog> getWorkflowChanges(EntityIdentifier entityIdentifier, String operation);
    /**
     * Get system functions
     * @return
     */
    public List<Function> getFunctions();
    /**
     * Get the current schedule information for digest email notifications.
    * @return
     */
    public ScheduleInfo getNotificationSchedule();
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
	
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 
    public List<WorkAreaFunctionMembership> getWorkAreaFunctionMemberships(WorkArea workArea);
	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMembershipsInherited(WorkArea workArea);
 
    public void modifyFunction(Long functionId, Map<String, Object> updates) throws AccessControlException;
    public void modifyPosting(String postingId, Map<String, Object> updates)throws AccessControlException;
	/**
	 * Send a mail message to a collection of users and/or explicit email address.  Include attachments  from entries if specified  
	 * @param entry - may be null
	 * @param ids - toList
	 * @param emailAddresses
	 * @param ccIds - ccoList
	 * @param bccIds - bccList
	 * @param subject
	 * @param body
	 * @param sendAttachments
	 * @return
	 * @throws Exception
	 */
    public Map<String, Object> sendMail(Entry entry, Collection<Long> userIds, Collection<Long> teamIds, Collection<String> emailAddresses, Collection<Long> ccIds, 
    		Collection<Long> bccIds, String subject, Description body,  boolean sendAttachments) throws Exception;
    /**
     * Send a mail message to a collection of users and/or explicit email address. 
     * @param ids
     * @param emailAddresses
 	 * @param ccIds - ccoList
	 * @param bccIds - bccList
    * @param subject
     * @param body
     * @return
     * @throws Exception
     */
    public Map<String, Object> sendMail(Collection<Long> ids, Collection<Long> teamIds, Collection<String> emailAddresses, Collection<Long> ccIds, 
    		Collection<Long> bccIds, String subject, Description body) throws Exception;
    	 
    public void setMailConfigAndSchedules(MailConfig mailConfig, ScheduleInfo notifications, ScheduleInfo postings) throws AccessControlException;  
 	public void setWorkAreaFunctionMemberships(WorkArea workArea, Map<Long, Set<Long>> functionMemberships) throws AccessControlException;
    public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) throws AccessControlException;
    public void setWorkAreaOwner(WorkArea workArea, Long userId, boolean propagate) throws AccessControlException;
	public void updateDefaultDefinitions(Long topId, Boolean newDefinitionsOnly);
	public void updateDefaultDefinitions(Long topId, Boolean newDefinitionsOnly, Collection ids);
	
	/**
	 * Get a list of <code>IndexNode</code>. Each IndexNode represents the state
	 * of the corresponding Lucene node in the context of current zone.
	 * @return
	 */
	public List<IndexNode> retrieveIndexNodes();
	
	/**
	 * Update the index node with the state information.
	 * 
	 * @param indexNodeId
	 * @param accessMode
	 * @param inSynch
	 */
	public void updateIndexNode(String indexNodeId, String accessMode, Boolean inSynch);

	/**
	 * Synchronize the Lucene node.
	 * 
	 * @param luceneNodeName
	 */
	public void synchronizeIndexOnNode(IndexNode indexNode);
 }