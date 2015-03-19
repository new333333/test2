<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
%>
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />

<%@ include file="/WEB-INF/jsp/mobile/masthead_new.jsp" %>
<%@ include file="/WEB-INF/jsp/mobile/navigation.jsp" %>

<div style="position:relative; background-color: #fff;" class="content">
<div class="masthead-username">
  <span>${ssUser.title}</span>
</div>
<table class="mobile-main" style="background-color: transparent; "  cellpadding="0" cellspacing="0">
  	<tr>
		<td class="main-cell">
			<a id="myworkspace-a" href="<ssf:url adapter="true" portletName="ss_forum" 
								action="__ajax_mobile" actionUrl="false" 
								binderId="${ssBinder.id}"
								operation="mobile_show_workspace" />">
				<div class="main-item myws-a">
				  <div><img src="<html:rootPath/>images/mobile/main_home_128b.png"/></div>
				  <div class="main-item-label"><ssf:nlt tag="navigation.myWorkspace"/></div>
				</div>    
			</a>
		</td>  
		<td class="main-cell">
			<a id="myfavorites-a" href="<ssf:url adapter="true" portletName="ss_forum" 
									action="__ajax_mobile" actionUrl="false" 
									binderId="${ssBinder.id}"
									operation="mobile_show_favorites" />">
				<div class="main-item myteams-a">
				  <div><img src="<html:rootPath/>images/mobile/main_favorites_128b.png"/></div>
				  <div class="main-item-label"><ssf:nlt tag="navigation.myFavorites"/></div>
				</div>    
			</a>
		</td>  
		<td class="main-cell">
			<a id="myteams-a" href="<ssf:url adapter="true" portletName="ss_forum" 
									action="__ajax_mobile" actionUrl="false" 
									binderId="${ssBinder.id}"
									operation="mobile_show_teams" />">
				<div class="main-item myteams-a">
				  <div><img src="<html:rootPath/>images/mobile/main_team_128b.png"/></div>
				  <div class="main-item-label"><ssf:nlt tag="navigation.myTeams"/></div>
				</div>    
			</a>
		</td>
	</tr>
	<tr>
		<td class="main-cell">
			<a id="whatsnew-a" href="<ssf:url adapter="true" portletName="ss_forum" 
									action="__ajax_mobile" actionUrl="false" 
									operation="mobile_whats_new" />">
				<div class="main-item myteams-a">
				  <div><img src="<html:rootPath/>images/mobile/main_whatsnew_128b.png"/></div>
				  <div class="main-item-label"><ssf:nlt tag="mobile.whatsNew"/></div>
				</div>    
			</a>
		</td>  
		<td class="main-cell">
			<a id="following-a" href="<ssf:url adapter="true" portletName="ss_forum" 
									action="__ajax_mobile" actionUrl="false" 
									binderId="${ssBinder.id}"
									operation="mobile_show_following" />">
				<div class="main-item myfavorites-a">
				  <div><img src="<html:rootPath/>images/mobile/main_follow_128b.png"/></div>
				  <div class="main-item-label"><ssf:nlt tag="navigation.following"/></div>
				</div>
			</a>
		</td>  
		<td class="main-cell">
			<a id="settings-a" href="<ssf:url adapter="true" portletName="ss_forum" 
									action="__ajax_mobile" actionUrl="false" 
									binderId="${ssBinder.id}"
									operation="mobile_show_recent_places" />">
				<div class="main-item myws-a">
				  <div><img src="<html:rootPath/>images/mobile/main_recent_128b.png"/></div>
				  <div class="main-item-label"><ssf:nlt tag="navigation.recentPlaces"/></div>
				</div>    
			</a>  
		</td>  
	</tr>  	
	<tr>  
		<td class="main-cell main-cell-bottom">
			<a id="myprofile-a" href="<ssf:url adapter="true" portletName="ss_forum" 
									action="__ajax_mobile" actionUrl="false" 
									entryId="${ssUser.id}"
									operation="mobile_show_user" />">
				<div class="main-item myws-a">
				  <div><img src="<html:rootPath/>images/mobile/main_profile_128b.png"/></div>
				  <div class="main-item-label"><ssf:nlt tag="navigation.myProfile"/></div>
				</div>    
			</a>
		</td>  
		<td class="main-cell main-cell-bottom">
				<a class="hierarchy-a" href="javascript: ;" onClick="ss_toggleDivVisibility('hierarchy-dialog');window.scroll(0,0);return false;"/>
					<div class="main-item navigation-a">
					  <div><img src="<html:rootPath/>images/mobile/main_hierarchy_128b.png"/></div>
					  <div class="main-item-label"><ssf:nlt tag="navigation.hierarchy"/></div>
					</div>
				</a>
		</td>  
		<td class="main-cell main-cell-bottom">
				<a class="hierarchy-a" href="javascript: ;" onClick="ss_mobileLogoff();return false;"/>
					<div class="main-item logout-a">
					  <div><img src="<html:rootPath/>images/mobile/main_logout_128b.png"/></div>
					  <div class="main-item-label"><ssf:nlt tag="logout"/></div>
					</div>
				</a>
		</td>  
	</tr>  			
</table>		
</div>
