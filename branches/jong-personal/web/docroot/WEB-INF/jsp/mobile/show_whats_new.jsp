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
<%@ page import="org.kablink.teaming.util.NLT" %>
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("mobile.whatsNew") %>' scope="request"/>
<c:set var="ss_pageTitle2" value="mobile.whatsNewWorkspace" />
<c:if test="${ssBinder.entityType == 'folder'}">
  <c:set var="ss_pageTitle2" value="mobile.whatsNewFolder" />
</c:if>
<c:if test="${ss_whatsNewSite}">
  <c:set var="ss_pageTitle2" value="mobile.whatsNewSiteWide" />
</c:if>
<c:if test="${ss_type == 'unseen'}">
  <c:set var="ss_windowTitle" value='<%= NLT.get("mobile.whatsUnread") %>' scope="request"/>
  <c:set var="ss_pageTitle2" value="mobile.whatsUnreadIn" />
  <c:if test="${ss_whatsNewSite}">
    <c:set var="ss_pageTitle2" value="mobile.whatsUnreadSiteWide" />
  </c:if>
</c:if>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>

<c:set var="ss_pageTitle" value="${ss_windowTitle}" scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>

<div class="content">

<c:set var="ss_hideMiniBlog" value="true" scope="request" />
<c:set var="ss_showWhatsNewNextPrev" value="true" scope="request" />
<%@ include file="/WEB-INF/jsp/mobile/action_bar.jsp" %>

  <div class="folder-content">
    <div class="folder-head" style="letter-spacing: 0; padding: 5px;">
		<form id="whatsNewForm" name="whatsNewForm" 
		  method="post" 
		  action="<ssf:url adapter="true" portletName="ss_forum" 
		    action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="true" ><ssf:param 
			name="operation2" value="${operation2}"/></ssf:url>"
		>
        <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
          <td valign="top" width="40%" nowrap>
          
          <a href="javascript: ;" 
      		  onClick="ss_toggleDivVisibility('whats-new-menu');return false;" style="color: #fff; font-size: 1.4em;">
      	    <span>
              <c:if test="${ss_type == 'teams'}"><ssf:nlt tag="navigation.myTeams"/></c:if>
              <c:if test="${ss_type == 'tracked'}"><ssf:nlt tag="mobile.whatsNewTracked"/></c:if>
              <c:if test="${ss_type == 'favorites'}"><ssf:nlt tag="mobile.whatsNewFavorites"/></c:if>
              <c:if test="${ss_type == 'site'}"><ssf:nlt tag="mobile.whatsNewSite"/></c:if>
              <c:if test="${ss_type == 'microblog'}"><ssf:nlt tag="mobile.whatsNewMicroBlogs"/></c:if>
            </span>&nbsp;<img border="0" 
      		  src="<html:rootPath/>images/pics/menudown.gif"/>
      	  </a>
          
			<div id="whats-new-menu" class="action-dialog" style="display:none; z-index:2; font-size: 1.2em;">
			    <div class="dialog-content">
		      		<div class="menu-item">
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_whats_new" 
				        actionUrl="true" ><ssf:param name="ss_type" value="teams"/><ssf:param 
				        name="operation2" value="${operation2}"/></ssf:url>"
				      ><ssf:nlt tag="navigation.myTeams"/></a>
				    </div>
				    <div class="menu-item">		      
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_whats_new" 
				        actionUrl="true" ><ssf:param name="ss_type" value="tracked"/><ssf:param 
				        name="operation2" value="${operation2}"/></ssf:url>"
				      ><ssf:nlt tag="mobile.whatsNewTracked"/></a>
				    </div>
				    <div class="menu-item">		      
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_whats_new" 
				        actionUrl="true" ><ssf:param name="ss_type" value="favorites"/><ssf:param 
				        name="operation2" value="${operation2}"/></ssf:url>"
				      ><ssf:nlt tag="mobile.whatsNewFavorites"/></a>
				    </div>
				    <div class="menu-item">		      
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_whats_new" 
				        actionUrl="true" ><ssf:param name="ss_type" value="site"/><ssf:param 
				        name="operation2" value="${operation2}"/></ssf:url>"
				      ><ssf:nlt tag="mobile.whatsNewSite"/></a>
				    </div>
				    <div class="menu-item">		      
				      <a href="<ssf:url adapter="true" portletName="ss_forum" 
				        action="__ajax_mobile" operation="mobile_whats_new" 
				        actionUrl="true" ><ssf:param name="ss_type" value="microblog"/><ssf:param 
				        name="operation2" value="${operation2}"/></ssf:url>"
				      ><ssf:nlt tag="mobile.whatsNewMicroBlogs"/></a>
				    </div>
				</div>
			</div>
		  </td>
		  
		</tr>
		</table>
		</form>
    </div>

	<c:if test="${empty ss_whatsNewBinder && ss_pageNumber > '0'}">
		  <div style="padding: 20px; color: #c4c4c4;" class="ss_italic"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></div>
		</c:if>
		<c:if test="${empty ss_whatsNewBinder && (empty ss_pageNumber || ss_pageNumber <= '0')}">
		  <div style="padding: 20px; color: #c4c4c4;" class="ss_italic"><ssf:nlt tag="whatsnew.noEntriesFound"/></div>
		</c:if>
		
        <c:forEach var="entryWn" items="${ss_whatsNewBinder}">
	      <jsp:useBean id="entryWn" type="java.util.Map" />
	      <div class="entry">
			<div class="entry-title">
			    <% if (!ssSeenMap.checkIfSeen(entryWn)) { %>
					<span><img src="<html:rootPath/>images/pics/discussion/sunburst.png" 
					  	align="absmiddle" border="0" <ssf:alt tag="alt.unseen"/> /></span>
  				<% } %>
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
				 <span class="entry-author"><a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="__ajax_mobile"
				    operation="mobile_show_workspace"
				    binderId="${entryWn._principal.workspaceId}" />"
				 ><c:out value="${entryWn._principal.title}" escapeXml="true"/></a></span>
	
			  	 <span class="entry-date">
			    	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		          	value="${entryWn._modificationDate}" type="both" 
			      	timeStyle="short" dateStyle="medium" />
			  	 </span>
		   
		  	</div>
		  
		  	<div class="entry-type">
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
					 >
			  		 <span>${title}</span></a>
				</c:if>
			</div>
			
			  <c:if test="${!empty entryWn._desc}">
			    <c:set var="truncatedDescription" ><ssf:textFormat 
			      	  formatAction="limitedDescription" 
			          textMaxWords="20"><ssf:markup 
			          search="${entryWn}" mobile="true">${entryWn._desc}</ssf:markup></ssf:textFormat></c:set>
			    <c:if test="${!empty truncatedDescription}">
			      <div class="entry-content">
			    	<span style="border:#cecece solid 1px;">
			        </span>
			        <div class="ss_clear"></div>
		  	      </div>
		  	    </c:if>
			  </c:if>
		
	      </div>
        </c:forEach>
  
	  <div class="entry-actions">
	    <%@ include file="/WEB-INF/jsp/mobile/whats_new_next_prev.jsp" %>
	  </div>
    </div>
  </div>  

</div>
</body>
</html>
