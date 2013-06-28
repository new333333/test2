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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<div id="ss_para">
	<div id="ss_today">
	
		<!-- Tracked Folders List -->
		<c:set var="foldersTracked" value="false"/>
		<c:forEach var="binder" items="${ss_trackedBinders}">
		  <c:if test="${binder.entityType == 'folder'}">
			  <c:set var="showThisBinder" value="true"/>
			  <c:forEach var="calendar" items="${ss_trackedCalendars}">
			    <c:if test="${calendar.id == binder.id}"><c:set var="showThisBinder" value="false"/></c:if>
			  </c:forEach>
			
			  <c:if test="${showThisBinder}">
			    <c:set var="foldersTracked" value="true"/>
				<div class="margintop1 marginleft1">
					<img align="absmiddle" src="<html:rootPath/>images/icons/folder_green_sm.png" <ssf:alt tag="entry.Folder"/> border="0" />&nbsp;<a
			   			href="<ssf:permalink entity="${binder}"/>"
				  		onclick="return ss_gotoPermalink('${binder.id}', '${binder.id}', 'folder', '${ss_namespace}', 'yes');"
			    		>
					<span class="ss_link_2">${binder.parentBinder.title} // ${binder.title}</span> </a>
			    	<c:if test="${ssBinderId == ssUser.workspaceId}">
			      <img class="display-pointer" align="absmiddle"
		            src="<html:rootPath/>images/pics/delete.png"
		            onclick="ss_trackedItemsDelete(this, '${binder.id}');" title="<ssf:nlt tag="relevance.trackThisFolderNot"/>"
		            <ssf:alt tag="alt.delete"/>/>
		        </c:if>
			    </div>
			  </c:if>
		  </c:if>
		</c:forEach>
<c:if test="${!foldersTracked}">
<span style="padding: 5px 15px;"><ssf:nlt tag="relevance.none"/></span>
</c:if>
	
		<!-- Tracked Workspaces List -->
		<div class="margintop3">
			<div id="ss_title" class="ss_pt_title ss_green">
			  <ssf:nlt tag="relevance.trackedWorkspaces"/>
			</div>

			<c:set var="workspacesTracked" value="false"/>
			<c:forEach var="binder" items="${ss_trackedBinders}">
			  <c:if test="${binder.entityType == 'workspace'}">
			    <c:set var="workspacesTracked" value="true"/>
			  	<div class="margintop1 marginleft1">
					<img align="absmiddle" src="<html:rootPath/>images/icons/workspace_generic.png" <ssf:alt tag="general.type.workspace"/> width="12" height="12" border="0" />&nbsp;<a
					  href="<ssf:permalink entity="${binder}"/>"
					  onclick="return ss_gotoPermalink('${binder.id}', '${binder.id}', 'workspace', '${ss_namespace}', 'yes');"
					><span class="ss_link_2">${binder.parentBinder.title} // ${binder.title}</span> </a>
					<c:if test="${ssBinderId == ssUser.workspaceId}">
					  <img class="display-pointer" align="absmiddle" src="<html:rootPath/>images/pics/delete.png"
						onclick="ss_trackedItemsDelete(this, '${binder.id}');" title="<ssf:nlt tag="relevance.trackThisWorkspaceNot"/>" <ssf:alt tag="alt.delete"/>/>
					</c:if>
				</div>
			  </c:if>
			</c:forEach>
<c:if test="${!workspacesTracked}">
<span style="padding: 5px 15px;"><ssf:nlt tag="relevance.none"/></span>
</c:if>
		</div>
	</div>	<!-- end of ss_today -->
</div>		<!-- end of ss_para -->

<div class="ss_clear_float"></div>


