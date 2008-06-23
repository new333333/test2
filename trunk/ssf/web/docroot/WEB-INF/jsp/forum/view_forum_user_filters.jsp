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
	String filterName = "";
	if (userFolderProperties != null) {
		Map searchFilters = (Map) userFolderProperties.getProperty("searchFilters");
		if (searchFilters == null) searchFilters = new java.util.HashMap();
		String userFilter = (String) userFolderProperties.getProperty("userFilter");
		if (userFilter != null && !userFilter.equals("")) filterName = userFilter;
		
		renderRequest.setAttribute("ss_searchFilters", searchFilters);
		renderRequest.setAttribute("currentFilter", filterName);
	}
%>
<table>
<tbody>
<tr><td align="right" width="10%">
<div class="ss_style ss_bold ss_fineprint">
<ssf:nlt tag="filter.filter" text="Filter"/>:<ssHelpSpot 
  helpId="workspaces_folders/menus_toolbars/folder_toolbar" offsetX="-45" offsetY="-5" 
  title="<ssf:nlt tag="helpSpot.folderControlAndFiltering"/>"></ssHelpSpot>&nbsp;</span>
  </div>
</td>
<td>
		<div id="ss_navbar_inline" class="ss_style ss_fineprint ss_normal">
			
					<ul>
					<li>
					<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssFolder.id}"/><ssf:param 
				name="operation" value="select_filter"/><ssf:param 
				name="select_filter" value=""/></ssf:url>">
					<span 
					<c:if test="${currentFilter == ''}"> class="ss_largeprint ss_bold"</c:if>
					<c:if test="${currentFilter != ''}"> class="ss_fineprint ss_normal"</c:if>
					>
						<ssf:nlt tag="None"/>
					</a></li>
					
					<c:forEach var="filter" items="${ss_searchFilters}">
					<li><a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssFolder.id}"/><ssf:param 
				name="operation" value="select_filter"/><ssf:param 
				name="select_filter" value="${filter.key}"/></ssf:url>">
					<span 
					<c:if test="${filter.key == currentFilter}"> class="ss_largeprint ss_bold"</c:if>
					<c:if test="${filter.key != currentFilter}"> class="ss_fineprint ss_normal"</c:if>
					>				
					<c:out value="${filter.key}"/></span>
					</a></li>
					</c:forEach>
					</ul>
				
		</div>

</td>
</tr>
<td>					 				
<div>
	<ssf:ifaccessible>
	<ul style="padding-top: 2px; padding-left: 5px;">
	<li>
		<c:if test="${ssConfigJspStyle != 'template'}">
		<a href="<ssf:url ><ssf:param 
			name="action" value="build_filter"/><ssf:param 
			name="binderId" value="${ssBinder.id}"/><ssf:param 
			name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
		><span class="ss_tabs_title"><ssf:nlt tag="sidebar.tags.filter" text="Add a Filter"/></span></a>
		</c:if>
		<c:if test="${ssConfigJspStyle == 'template'}">
		<span class="ss_tabs_title"><ssf:nlt tag="filter.add" text="Add a New Filter"/></span>
		</c:if>
	</li>
	</ul>	
	</ssf:ifaccessible>
</div>
</td>
</tr>
</tbody>
</table>