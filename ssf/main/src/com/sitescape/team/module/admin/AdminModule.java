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

package com.sitescape.team.module.admin;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sitescape.team.domain.AuthenticationConfig;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.search.Node;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
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
		manageErrorLogs,
		manageAuthentication
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
	 * @param ids
	 * @param emailAddresses
	 * @param subject
	 * @param body
	 * @param sendAttachments
	 * @return
	 * @throws Exception
	 */
    public Map<String, Object> sendMail(Entry entry, Collection<Long> userIds, Collection<String> emailAddresses, String subject, Description body,  boolean sendAttachments) throws Exception;
    /**
     * Send a mail message to a collection of users and/or explicit email address. 
     * @param ids
     * @param emailAddresses
     * @param subject
     * @param body
     * @return
     * @throws Exception
     */
    public Map<String, Object> sendMail(Collection<Long> ids, Collection<String> emailAddresses, String subject, Description body) throws Exception;
    	 
    /**
     * Set the schedule by which digest notifications are sent.  Use this to both enable and disable notifications
     * @param binderId
     * @param config
     */
    public void setNotificationSchedule(ScheduleInfo config) throws AccessControlException;  
    public void setPostingSchedule(ScheduleInfo config) throws ParseException, AccessControlException;;
	public void setWorkAreaFunctionMemberships(WorkArea workArea, Map<Long, Set<Long>> functionMemberships) throws AccessControlException;
    public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) throws AccessControlException;
    public void setWorkAreaOwner(WorkArea workArea, Long userId, boolean propagate) throws AccessControlException;
	public void updateDefaultDefinitions(Long topId);
    
	public List<Node> getSearchNodes();
	public List<AuthenticationConfig> getAuthenticationConfigs(Long zoneId);
	public List<AuthenticationConfig> getAuthenticationConfigs();
	public void setAuthenticationConfigs(List<AuthenticationConfig> configs);
 }