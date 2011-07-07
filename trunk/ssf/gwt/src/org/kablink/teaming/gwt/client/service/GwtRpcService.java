/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.client.service;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.DiskUsageInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * This interface defines the methods that can be called when we want to make a remote
 * procedure call.
 * 
 * @author jwootton
 */
@RemoteServiceRelativePath("gwtTeaming.rpc")
public interface GwtRpcService extends RemoteService
{
	// Execute the given command.
	public VibeRpcResponse executeCommand( HttpRequestInfo ri, VibeRpcCmd cmd ) throws GwtTeamingException;
	
	
	
	// The following are used to manage the tracking of information.
	public List<String> getTrackedPeople( HttpRequestInfo ri                  );
	public List<String> getTrackedPlaces( HttpRequestInfo ri                  );
	public Boolean      isPersonTracked(  HttpRequestInfo ri, String binderId );
	public Boolean      trackBinder(      HttpRequestInfo ri, String binderId );
	public Boolean      untrackBinder(    HttpRequestInfo ri, String binderId );
	public Boolean      untrackPerson(    HttpRequestInfo ri, String binderId );
	
	// The following are used in the implementation of the
	// User Profiles
	public ProfileInfo 		getProfileInfo(    HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public ProfileStats     getProfileStats(   HttpRequestInfo ri, String binderId, String userId) throws GwtTeamingException;
	public ProfileAttribute getProfileAvatars( HttpRequestInfo ri, String binderId );
	public ProfileInfo 		getQuickViewInfo(  HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public List<TeamInfo> 	getTeams(          HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public List<GroupInfo> 	getGroups(         HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public String 			getMicrBlogUrl(    HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public Boolean 			isPresenceEnabled( HttpRequestInfo ri);
	public String 			getImUrl(          HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public GwtPresenceInfo  getPresenceInfo(   HttpRequestInfo ri, String binderId )               throws GwtTeamingException;

	// Return the URL for the start/schedule meeting page
	public String getAddMeetingUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException;

	// The following are used in the implementation of the
	// UserStatusControl.
	public Boolean saveUserStatus(HttpRequestInfo ri, String status) throws GwtTeamingException;
	public UserStatus getUserStatus(HttpRequestInfo ri, String binderId) throws GwtTeamingException; 
	
	// Get DiskUsageInfo.
	public  DiskUsageInfo getDiskUsageInfo( HttpRequestInfo ri, String binderId ) throws GwtTeamingException;
}// end GwtRpcService
