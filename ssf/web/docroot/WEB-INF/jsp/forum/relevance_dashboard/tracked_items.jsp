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
		<div id="ss_hints"><em><br />
			<ssf:nlt tag="relevance.foldersBeingTracked"/>
		</em></div>
		<c:forEach var="binder" items="${ss_trackedBinders}">
		  <c:if test="${binder.entityType == 'folder'}">
			  <c:set var="showThisBinder" value="true"/>
			  <c:forEach var="calendar" items="${ss_trackedCalendars}">
			    <c:if test="${calendar.id == binder.id}"><c:set var="showThisBinder" value="false"/></c:if>
			  </c:forEach>
			
			  <c:if test="${showThisBinder}">
			    <table cellpadding="0" cellspacing="0"><tbody><tr><td>
			    <a class="ss_link_5"
			      href="<ssf:permalink entity="${binder}"/>"
				  onclick="return ss_gotoPermalink('${binder.id}', '${binder.id}', 'folder', '${ss_namespace}', 'yes');"
			    ><span>${binder.parentBinder.title} // ${binder.title}</span> </a>
			    &nbsp;<img src="<html:rootPath/>images/icons/folder_green_sm.png" <ssf:alt tag="entry.Folder"/> width="11" height="10" hspace="2" border="0" align="absmiddle" />
			    <c:if test="${ssBinderId == ssUser.workspaceId}">
			      <img style="padding:4px 0px 0px 2px;" align="texttop"
		            src="<html:rootPath/>images/pics/delete.gif"
		            onclick="ss_trackedItemsDelete(this, '${binder.id}');"
		            <ssf:alt tag="alt.delete"/>/>
		        </c:if>
			    </td></tr></tbody></table>
			  </c:if>
		  </c:if>
		</c:forEach>
		<br/>
		<br/>
		<div id="ss_hints"><em>
			<ssf:nlt tag="relevance.workspacesBeingTracked"/>
		</em></div>
		<c:forEach var="binder" items="${ss_trackedBinders}">
		  <c:if test="${binder.entityType == 'workspace'}">
			    <table cellpadding="0" cellspacing="0"><tbody><tr><td>
			    <a class="ss_link_5"
			      href="<ssf:permalink entity="${binder}"/>"
				  onclick="return ss_gotoPermalink('${binder.id}', '${binder.id}', 'workspace', '${ss_namespace}', 'yes');"
			    ><span>${binder.parentBinder.title} // ${binder.title}</span> </a><img src="<html:rootPath/>images/icons/folder_cyan_sm.png" <ssf:alt tag="entry.Folder"/> width="11" height="10" hspace="2" border="0" align="absmiddle" />&nbsp;
			    <c:if test="${ssBinderId == ssUser.workspaceId}">
			      <img style="padding:4px 0px 0px 2px;" align="texttop" src="<html:rootPath/>images/pics/delete.gif"
		            onclick="ss_trackedItemsDelete(this, '${binder.id}');" <ssf:alt tag="alt.delete"/>/>
		        </c:if>
		      </td></tr></tbody></table>
		  </c:if>
		</c:forEach>
	</div>	<!-- end of ss_today -->
</div>		<!-- end of ss_para -->

<div class="ss_clear_float"></div>


