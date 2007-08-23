<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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

		<script type="text/javascript">
			function ss_changeUserFilter(obj, filter) {
			<c:if test="${ssConfigJspStyle != 'template'}">
				var loading = "&nbsp;(<ssf:nlt tag="loading" text="loading"/>)&nbsp;"
				document.getElementById('ss_filterTitle').innerHTML = "&nbsp;"+obj.innerHTML+loading;
				document.forms.ss_filterSelect.select_filter.value = filter;
				document.forms.ss_filterSelect.submit();
			</c:if>
			}
		</script>
	
		<form class="ss_style" style="display: inline;"  
		    name="ss_filterSelect" 
			action="<portlet:actionURL windowState="maximized"><portlet:param 
				name="action" value="${action}"/><portlet:param 
				name="binderId" value="${ssFolder.id}"/><portlet:param 
				name="operation" value="select_filter"/></portlet:actionURL>" 
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
		<a class="ss_actions_bar_inline" href="<portlet:renderURL windowState="maximized"><portlet:param 
			name="action" value="build_filter"/><portlet:param 
			name="binderId" value="${ssBinder.id}"/><portlet:param 
			name="binderType" value="${ssBinder.entityType}"/></portlet:renderURL>"
		><ssf:nlt tag="Edit" text="Edit"/></a>
		</c:if>
		<c:if test="${ssConfigJspStyle == 'template'}">
		<ssf:nlt tag="Edit" text="Edit"/>
		</c:if>
</div>