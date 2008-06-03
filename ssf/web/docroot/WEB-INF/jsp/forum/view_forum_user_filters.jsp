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
<% // User filters %>
<%@ page import="java.util.List" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ page import="com.sitescape.team.domain.UserProperties" %>
<%
	UserProperties userFolderProperties = (UserProperties) request.getAttribute("ssUserFolderProperties");
	String filterName = NLT.get("Select");
	if (userFolderProperties != null) {
		Map searchFilters = (Map) userFolderProperties.getProperty("searchFilters");
		if (searchFilters == null) searchFilters = new java.util.HashMap();
		String userFilter = (String) userFolderProperties.getProperty("userFilter");
		if (userFilter != null && !userFilter.equals("")) filterName = userFilter;
		
		renderRequest.setAttribute("ss_searchFilters", searchFilters);
		renderRequest.setAttribute("currentFilter", filterName);
	}
%>
<div class="ss_style">
<ssf:nlt tag="filter.filter" text="Filter"/>:<ssHelpSpot 
  helpId="workspaces_folders/menus_toolbars/folder_toolbar" offsetX="-45" offsetY="-5" 
  title="<ssf:nlt tag="helpSpot.folderControlAndFiltering"/>"></ssHelpSpot>&nbsp;</span>

		<form class="ss_style" style="display: inline;"  
		    name="ss_filterSelect" 
			action="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssFolder.id}"/><ssf:param 
				name="operation" value="select_filter"/></ssf:url>" 
			method="post" >
			
			
			<%
			//	if (filterName.length() > 10) {
			//		filterName = filterName.substring(0, 6) + "...";
			//	}
			%>

			  <ssf:ifnotaccessible>
					<select name="select_filter" onchange="ss_submitParentForm(this);">
					<option value="">--<ssf:nlt tag="none" text="none"/>--</option>
					<c:forEach var="filter" items="${ss_searchFilters}">
					<option value="${filter.key}"
					<c:if test="${filter.key == currentFilter}"> selected="true"</c:if>					
					><c:out value="${filter.key}"/></option>
					</c:forEach>
					</select>					
			  </ssf:ifnotaccessible>
			 				
		</form>
		<c:if test="${ssConfigJspStyle != 'template'}">
		<a class="ss_actions_bar_inline" href="<ssf:url ><ssf:param 
			name="action" value="build_filter"/><ssf:param 
			name="binderId" value="${ssBinder.id}"/><ssf:param 
			name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
		><ssf:nlt tag="Edit" text="Edit"/></a>
		</c:if>
		<c:if test="${ssConfigJspStyle == 'template'}">
		<ssf:nlt tag="Edit" text="Edit"/>
		</c:if>
</div>