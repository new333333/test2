<%
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
%>
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_dashboard_content" class="ss_tricolumn">
  <div class="ss_colmid">
    <div class="ss_colleft">
      <div id="ss_col1" class="ss_col1">
      
	<ssf:canvas id="relevanceFolders" type="inline" styleId="ss_trackedItems">
	<ssf:param name="title" value="<%= NLT.get("relevance.trackedFolders") %>"/>
<ul>
<c:forEach var="binder" items="${ss_trackedBinders}">
  <c:if test="${binder.entityType == 'folder'}">
	<li>
	    <table cellpadding="0" cellspacing="0"><tbody><tr><td><a
	      href="<ssf:url action="view_folder_listing" binderId="${binder.id}"/>"
	    >${binder.title}</a></td><td valign="top"><img style="padding:6px 0px 0px 4px;"
	      src="<html:rootPath/>images/pics/delete.gif"
	      onClick="ss_trackedItemsDelete(this, '${binder.id}');"/></td></tr></tbody></table>
	</li>
  </c:if>
</c:forEach>
</ul>
	</ssf:canvas>

        </div><!-- end of ss_col 1 -->
      <div id="ss_col2" class="ss_col2">

	<ssf:canvas id="relevanceWorkspaces" type="inline" styleId="ss_trackedItems">
	<ssf:param name="title" value="<%= NLT.get("relevance.trackedWorkspaces") %>"/>
<ul>
<c:forEach var="binder" items="${ss_trackedBinders}">
  <c:if test="${binder.entityType == 'workspace' && binder.definitionType == 8}">
	<li>
	    <table cellpadding="0" cellspacing="0"><tbody><tr><td><a
	      href="<ssf:url action="view_ws_listing" binderId="${binder.id}"/>"
	    >${binder.title}</a></td><td valign="top"><img style="padding:4px 0px 0px 4px;"
	      src="<html:rootPath/>images/pics/delete.gif"
	      onClick="ss_trackedItemsDelete(this, '${binder.id}');"/></td></tr></tbody></table>
	</li>
  </c:if>
</c:forEach>
</ul>
	</ssf:canvas>

      </div><!-- end of col2 -->
      <div id="ss_col3" class="ss_col3">

	<ssf:canvas id="relevancePeople" type="inline" styleId="ss_people">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title"><ssf:nlt tag="relevance.trackedPeople"/></div>
	</ssf:param>
<ul>
<c:forEach var="user" items="${ss_trackedPeople}">
	<li>
	    <table cellpadding="0" cellspacing="0"><tbody><tr><td>
	    <ssf:showUser user="${user}" />
	    <c:if test="${!empty user.status}">
	    <br/><span class="ss_smallprint ss_italic" style="padding-left:20px;">${user.status}</span>
	    </c:if>
	    </td><td valign="top"><img style="padding:4px 0px 0px 2px;"
	      src="<html:rootPath/>images/pics/delete.gif"
	      onClick="ss_trackedItemsDelete(this, '${user.workspaceId}');"/></td></tr></tbody></table>
	</li>
</c:forEach>
</ul>
	</ssf:canvas>

      </div><!-- end of col3 -->
    </div><!-- end of col left -->
  </div><!-- end of col mid -->
</div><!-- end of content -->
<div class="ss_clear_float"></div>
