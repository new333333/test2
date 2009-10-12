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
	  <img src="<html:imagesPath/>mobile/iphone_teaming_workspace1.png"/>
      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="__ajax_mobile"
				    operation="mobile_show_workspace"
				    binderId="${ssUser.workspaceId}" />"><ssf:nlt tag="navigation.myWorkspace"/></a>
    </div>
    <div class="my-item myfavorites-a">
      <img src="<html:imagesPath/>mobile/iphone_teaming_favorite1.png"/>
      <a id="myfavorites-a" href="<ssf:url adapter="true" portletName="ss_forum" 
							action="__ajax_mobile" actionUrl="false" 
							operation="mobile_show_favorites" />"><ssf:nlt tag="navigation.myFavorites"/></a>
    </div>
	<div class="my-item myteams-a">
	  <img src="<html:imagesPath/>mobile/iphone_teaming_teams1.png"/>
	  <a id="myteams-a" href="<ssf:url adapter="true" portletName="ss_forum" 
							action="__ajax_mobile" actionUrl="false" 
							operation="mobile_show_teams" />"><ssf:nlt tag="navigation.myTeams"/></a>
	</div>    
  </div>

  <div class="folder-content">
    <div class="folder-head">
		<form id="whatsNewForm" name="whatsNewForm" 
		  method="post" 
		  action="<ssf:url adapter="true" portletName="ss_forum" 
		    action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="true" />"
		>
          <span><ssf:nlt tag="mobile.whatsNew"/></span>
		  <span class="pad-left20">
		    <select size="1" name="whats_new" 
		      onChange="self.document.getElementById('whatsNewForm').submit();">
		    
		      <option value="teams" <c:if test="${ss_whatsNewType == 'teams'}">selected</c:if>
		      ><ssf:nlt tag="navigation.myTeams"/></option>
		      
		      <option value="tracked" <c:if test="${ss_whatsNewType == 'tracked'}">selected</c:if>
		      ><ssf:nlt tag="mobile.whatsNewTracked"/></option>
		      
		      <option value="site" <c:if test="${ss_whatsNewType == 'site'}">selected</c:if>
		      ><ssf:nlt tag="mobile.whatsNewSite"/></option>
		      
		      <option value="microblog" <c:if test="${ss_whatsNewType == 'microblog'}">selected</c:if>
		      ><ssf:nlt tag="mobile.whatsNewMicroBlogs"/></option>
		      
		    </select>
		    <input type="hidden" name="whatsNewBtn" value="whats_new"/>
		  </span>
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
		 
		  	  <div>
		  	    <span class="entry-author">
				  <a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="__ajax_mobile"
				    operation="mobile_show_workspace"
				    binderId="${entryWn._principal.workspaceId}" />"
				  ><span class="ss_mobile_light ss_mobile_small"
				  ><c:out value="${entryWn._principal.title}" escapeXml="true"/></span></a>
				</span>
	
			  	<span class="entry-date">
			    	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		          	value="${entryWn._modificationDate}" type="both" 
			      	timeStyle="short" dateStyle="medium" />
			  	</span>
		  	  </div>
		  
		  	  <div>
		  	    <span class="entry-type">
				<c:set var="path" value=""/>
				<c:if test="${!empty ss_whatsNewBinderFolders[entryWn._binderId]}">
				  <c:set var="path" value="${ss_whatsNewBinderFolders[entryWn._binderId]}"/>
				  <c:set var="title" value="${ss_whatsNewBinderFolders[entryWn._binderId].title} (${ss_whatsNewBinderFolders[entryWn._binderId].parentBinder.title})"/>
				</c:if>
				<c:set var="isDashboard" value="yes"/>
				<c:if test="${!empty path}">
	     			<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${entryWn._binderId}" 
						action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />"
					 >
			  		 <span>${title}</span></a>
				</c:if>
				</span>
			  </div>
			
			  <c:if test="${!empty entryWn._desc}">
			    <div class="entry-content">
			    	<span style="border:#cecece solid 1px;"><ssf:textFormat 
			      	  formatAction="limitedDescription" 
			          textMaxWords="20"><ssf:markup search="${entryWn}">${entryWn._desc}</ssf:markup></ssf:textFormat>
			        </span>
		  	    </div>
			  </c:if>
		
	    	</div>
          </c:forEach>
        </c:if>
        <c:if test="${ss_whatsNewType == 'microblog'}">
	  		<c:forEach var="activity" items="${ss_activities}">
	    	<div class="entry">
	    	  <div class="entry-title">
		    	<ssf:showUser user="${activity.user}" titleStyle="ss_link_1"/>
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
			    	<span style="border:#cecece solid 1px;">${activity.description}</span>
		  	    </div>
			  </c:if>
			  </div>
	  		</c:forEach>
        </c:if>
  </div>
</div>

