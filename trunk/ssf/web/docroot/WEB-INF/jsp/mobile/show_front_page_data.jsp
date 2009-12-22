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

<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>

<div class="content">

<%@ include file="/WEB-INF/jsp/mobile/action_bar.jsp" %>

<c:set var="ssProfileUser" value="${ssUser}" scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/user_profile.jsp" %>

<div class="folders">
  <div class="folder-content">
    <div class="my-item myws-a">
	  <img src="<html:rootPath/>images/mobile/iphone_teaming_workspace1.png"/>
      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="__ajax_mobile"
				    operation="mobile_show_workspace"
				    binderId="${ssUser.workspaceId}" />"><ssf:nlt tag="navigation.myWorkspace"/></a>
    </div>
    <div class="my-item myfavorites-a">
      <img src="<html:rootPath/>images/mobile/iphone_teaming_favorite1.png"/>
      <a id="myfavorites-a" href="<ssf:url adapter="true" portletName="ss_forum" 
							action="__ajax_mobile" actionUrl="false" 
							binderId="${ssBinder.id}"
							operation="mobile_show_favorites" />"><ssf:nlt tag="navigation.myFavorites"/></a>
    </div>
	<div class="my-item myteams-a">
	  <img src="<html:rootPath/>images/mobile/iphone_teaming_teams1.png"/>
	  <a id="myteams-a" href="<ssf:url adapter="true" portletName="ss_forum" 
							action="__ajax_mobile" actionUrl="false" 
							binderId="${ssBinder.id}"
							operation="mobile_show_teams" />"><ssf:nlt tag="navigation.myTeams"/></a>
	</div>    
  </div>

  <div class="folder-content">
    <div class="folder-head" style="letter-spacing: 0">
		<form id="whatsNewForm" name="whatsNewForm" 
		  method="post" 
		  action="<ssf:url adapter="true" portletName="ss_forum" 
		    action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="true" />"
		>
        <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
          <td valign="top" width="40%" nowrap>
          <span>&nbsp;<ssf:nlt tag="mobile.whatsNew"/></span>
          <a href="javascript: ;" 
      		  onClick="ss_toggleDivVisibility('whats-new-menu');return false;">
      	    <span>
              <c:if test="${ss_whatsNewType == 'teams'}"><ssf:nlt tag="navigation.myTeams"/></c:if>
              <c:if test="${ss_whatsNewType == 'tracked'}"><ssf:nlt tag="mobile.whatsNewTracked"/></c:if>
              <c:if test="${ss_whatsNewType == 'site'}"><ssf:nlt tag="mobile.whatsNewSite"/></c:if>
              <c:if test="${ss_whatsNewType == 'microblog'}"><ssf:nlt tag="mobile.whatsNewMicroBlogs"/></c:if>
            </span><img border="0" 
      		  src="<html:rootPath/>images/pics/menudown.gif"/>
      	  </a>
          
			<div id="whats-new-menu" class="action-dialog" style="display:none; z-index:2;">
			    <div class="dialog-content">
		      		<div class="menu-item">
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_show_front_page" 
				        actionUrl="true" ><ssf:param name="whats_new" value="teams"/></ssf:url>"
				      ><ssf:nlt tag="navigation.myTeams"/></a>
				    </div>
				    <div class="menu-item">		      
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_show_front_page" 
				        actionUrl="true" ><ssf:param name="whats_new" value="tracked"/></ssf:url>"
				      ><ssf:nlt tag="mobile.whatsNewTracked"/></a>
				    </div>
				    <div class="menu-item">		      
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_show_front_page" 
				        actionUrl="true" ><ssf:param name="whats_new" value="site"/></ssf:url>"
				      ><ssf:nlt tag="mobile.whatsNewSite"/></a>
				    </div>
				    <div class="menu-item">		      
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_show_front_page" 
				        actionUrl="true" ><ssf:param name="whats_new" value="microblog"/></ssf:url>"
				      ><ssf:nlt tag="mobile.whatsNewMicroBlogs"/></a>
				    </div>
				</div>
			</div>
		  </td>
		  <td valign="top" align="right" width="40%">
	  		<table cellspacing="0" cellpadding="0">
				<tr>
		  		<td>
					<c:if test="${!empty ss_prevPage}">
			  		<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${ssBinder.id}" 
						action="__ajax_mobile" 
						operation="mobile_show_front_page" 
						actionUrl="false" ><ssf:param 
						name="quickSearch" value="true"/><ssf:param 
						name="searchText" value="${ss_searchText}"/><ssf:param 
						name="tabId" value="${ss_tab_id}"/><ssf:param 
						name="pageNumber" value="${ss_pageNumber-1}"/><ssf:param 
						name="ss_queryName" value="${ss_queryName}" /></ssf:url>"
			  		><img border="0" src="<html:rootPath/>images/mobile/nl_left_20.png"/></a>
					</c:if>
					<c:if test="${empty ss_prevPage}">
			  		  <img border="0" src="<html:rootPath/>images/mobile/nl_left_dis_20.png"/>
					</c:if>
		  		</td>
		  		<td style="padding-left:20px;">
					<c:if test="${!empty ss_nextPage}">
			  		<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${ssBinder.id}" 
						action="__ajax_mobile" 
						operation="mobile_show_front_page" 
						actionUrl="false" ><ssf:param 
						name="quickSearch" value="true"/><ssf:param 
						name="searchText" value="${ss_searchText}"/><ssf:param 
						name="tabId" value="${ss_tab_id}"/><ssf:param 
						name="pageNumber" value="${ss_pageNumber+1}"/><ssf:param 
						name="ss_queryName" value="${ss_queryName}" /></ssf:url>"
			  		><img border="0" src="<html:rootPath/>images/mobile/nl_right_20.png"/></a>
					</c:if>
					<c:if test="${empty ss_nextPage}">
			  		  <img border="0" src="<html:rootPath/>images/mobile/nl_right_dis_20.png"/>
					</c:if>
	      		</td>
				</tr>
	  		</table>
		  </td>
		</tr>
		</table>
		</form>
    </div>
    
	    <c:if test="${ss_whatsNewType != 'microblog'}">
	      <c:forEach var="entryWn" items="${ss_whatsNewBinder}">
	    	<jsp:useBean id="entryWn" type="java.util.Map" />
	    	<div class="entry">
	    	  <div class="entry-title">
			    <a href="<ssf:url adapter="true" portletName="ss_forum" 
				  folderId="${entryWn._binderId}"  entryId="${entryWn._docId}"
				  action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />"
			    >
			  	  <span><c:if test="${empty entryWn.title}">--<ssf:nlt tag="entry.noTitle"/>--</c:if>
			  	    <ssf:makeWrapable><c:out value="${entryWn.title}" escapeXml="true"/></ssf:makeWrapable>
			  	  </span>
			    </a>
			  </div>
		 
		  	  <div class="entry-signature">
		  	    <span class="entry-author">
				  <a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="__ajax_mobile"
				    operation="mobile_show_workspace"
				    binderId="${entryWn._principal.workspaceId}" />"
				  ><c:out value="${entryWn._principal.title}" escapeXml="true"/></a>
				</span>
	
			  	<div class="entry-date">
			    	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		          	value="${entryWn._modificationDate}" type="both" 
			      	timeStyle="short" dateStyle="medium" />
			  	</div>
		  	  </div>
		  
		  	  <div class="entry-type">
		  	    <span>
				<c:set var="path" value=""/>
				<c:if test="${!empty ss_whatsNewBinderFolders[entryWn._binderId]}">
				  <c:set var="path" value="${ss_whatsNewBinderFolders[entryWn._binderId]}"/>
				  <c:set var="title" value="${ss_whatsNewBinderFolders[entryWn._binderId].parentBinder.title} // ${ss_whatsNewBinderFolders[entryWn._binderId].title}"/>
				</c:if>
				<c:set var="isDashboard" value="yes"/>
				<c:if test="${!empty path}">
	     			<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${entryWn._binderId}" 
						action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />"
					 >${title}</a>
				</c:if>
				</span>
			  </div>
			
			  <c:if test="${!empty entryWn._desc}">
			    <div class="entry-content">
			    	<ssf:textFormat 
			      	  formatAction="limitedDescription" 
			          textMaxWords="20"><ssf:markup search="${entryWn}">${entryWn._desc}</ssf:markup></ssf:textFormat>
		  	      <div class="ss_clear"></div>
		  	    </div>
			  </c:if>
		
	    	</div>
          </c:forEach>
        </c:if>
        <c:if test="${ss_whatsNewType == 'microblog'}">
	  		<c:forEach var="activity" items="${ss_activities}">
	    	<div class="entry">
	    	  <div class="entry-title">
		    	<a href="<ssf:url adapter="true" portletName="ss_forum" 
				  binderId="${activity.user.workspaceId}"
				  action="__ajax_mobile" 
				  operation="mobile_show_workspace" />" ><ssf:userTitle user="${activity.user}"/></a>
		      </div>
		  	  <div>
			  	<span class="entry-date">
			    	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					      value="${activity.date}" type="both" 
						  timeStyle="short" dateStyle="short" />
			  	</span>
		  	  </div>
		  
			  <c:if test="${!empty activity.description}">
			    <div class="entry-content">
			    	<span>${activity.description}</span>
		  	    </div>
			  </c:if>
			  </div>
	  		</c:forEach>
        </c:if>
  </div>
</div>

