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
<% //Folder list.  %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ssUserFolderProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.Binder" scope="request" />
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />
<c:set var="ss_folderViewColumnsType" value="empty" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/folder_column_defaults.jsp" %>

  <c:if test="${!empty ssFolders}">
  <div class="folders">
	<c:forEach var="folder" items="${ssFolders}" >
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${folder.id}" 
				action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />">
	      <div class="folder-item">
            <img class="margin5r" src="<html:rootPath/>images/mobile/folder.png" align="absmiddle" />
		  <c:if test="${empty folder.title}">
		    (<ssf:nlt tag="folder.noTitle"/>)
		  </c:if>
		  <c:out value="${folder.title}" escapeXml="true"/>
		</div>
		</a>
	</c:forEach>
	
  </div>
  </c:if>

<c:if test="${ssDefinitionFamily != 'calendar' && ssDefinitionFamily != 'task'}">
  <div class="folders">
    <div class="folder-head">
       <ssf:nlt tag="mobile.entries"/>
    </div>
    
	  <c:if test="${!empty ssFolderEntries}">
	    <c:forEach var="entryFol" items="${ssFolderEntries}">
	    	<jsp:useBean id="entryFol" type="java.util.Map" />
			<div class="entry">
			  <div class="entry-title">
			    <% if (!ssSeenMap.checkIfSeen(entryFol)) { %>
					<span><img src="<html:rootPath/>images/pics/discussion/sunburst.png" 
					  	align="absmiddle" border="0" <ssf:alt tag="alt.unseen"/> /></span>
  				<% } %>
			    <c:if test="${!empty ssFolderColumns['number'] && !empty entryFol._docNum}">
			    <span style="font-weight: normal !important;">${entryFol._docNum}. </span>
			    </c:if>
			    <a href="<ssf:url adapter="true" portletName="ss_forum" 
				  folderId="${entryFol._binderId}"  entryId="${entryFol._docId}"
				  action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />"
			    >
			  	  <span><c:if test="${empty entryFol.title}">--<ssf:nlt tag="entry.noTitle"/>--</c:if>
			  	    <ssf:makeWrapable><c:out value="${entryFol.title}" escapeXml="true"/></ssf:makeWrapable>
			  	  </span>
			    </a>
			  </div>
			  <c:if test="${!empty entryFol._totalReplyCount}">
			    <div class="entry-comment-label">${entryFol._totalReplyCount}</div>
			  </c:if>
			  
			  <div class="entry-signature">
				<span class="entry-author"><a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="__ajax_mobile"
				    operation="mobile_show_workspace"
				    binderId="${entryFol._principal.workspaceId}" />"
				  ><c:out value="${entryFol._principal.title}" escapeXml="true"/></a></span>
				<span class="entry-date"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		          	value="${entryFol._modificationDate}" type="both" 
			      	timeStyle="short" dateStyle="medium" />
			    </span>
			  </div>
		 
		  	  
			  <c:if test="${!empty entryFol._desc}">
			    <div class="entry-content">
			    	<ssf:textFormat 
			      	  formatAction="limitedDescription" 
			          textMaxWords="10"><ssf:markup search="${entryFol}" mobile="true">${entryFol._desc}</ssf:markup></ssf:textFormat>
		  	    <div class="ss_clear"></div>
		  	    </div>
			  </c:if>
		
	    	</div>
		</c:forEach>
	    <div class="entry-actions">
	      <%@ include file="/WEB-INF/jsp/mobile/folder_next_prev.jsp" %>
	    </div>
	  </c:if>
	  <c:if test="${empty ssFolderEntries}">
		<div style="padding: 20px; color: #c4c4c4;"><ssf:nlt tag="folder.NoResults"/></div>
	  </c:if>

  </div>
</c:if>
<c:if test="${ssDefinitionFamily == 'calendar'}">
	<%@ include file="/WEB-INF/jsp/mobile/show_calendar.jsp" %>
</c:if>
<c:if test="${ssDefinitionFamily == 'task'}">
	<%@ include file="/WEB-INF/jsp/mobile/show_task_folder.jsp" %>
</c:if>
		
<c:set var="ss_mobileBinderListShown" value="true" scope="request"/>
