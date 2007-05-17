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
	}
%>
<span class="ss_toolBarItemTxt"><ssf:nlt tag="filter.filter" text="Filter"/>:<ssHelpSpot 
  helpId="tools/folder_control_and_filtering" offsetX="0" 
  title="<ssf:nlt tag="helpSpot.folderControlAndFiltering"/>"></ssHelpSpot>&nbsp;</span>
<ul>
	<li>
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
	
		<form class="ss_compact ss_actions_bar_background" 
		    name="ss_filterSelect" 
			action="<portlet:actionURL windowState="maximized">
				<portlet:param name="action" value="${action}"/>
				<portlet:param name="binderId" value="${ssFolder.id}"/>
				<portlet:param name="operation" value="select_filter"/>
				</portlet:actionURL>" method="post" >
			
			
			<%
			//	if (filterName.length() > 10) {
			//		filterName = filterName.substring(0, 6) + "...";
			//	}
			%>

			<ssf:menu title="<%= filterName %>" 
			  titleId="ss_filterTitle" 
			  titleClass="ss_compact"
			  menuClass="ss_actions_bar_submenu"
			  menuImage="pics/menudown.gif">
			  
			  <ssf:ifnotaccessible>
					<ul class="ss_actions_bar2 ss_actions_bar_submenu ss_actions_bar_filters" style="width:250px;">
					<li><a href="javascript: ;" 
					  onClick="ss_changeUserFilter(this, '<c:out value=""/>');return false;"
					>--<ssf:nlt tag="none" text="none"/>--</a></li>
					<c:forEach var="filter" items="${ss_searchFilters}">
					<li><a href="javascript: ;" 
					  onClick="ss_changeUserFilter(this, '<c:out value="${filter.key}"/>');return false;"
					><c:out value="${filter.key}"/></a></li>
					</c:forEach>
					</ul>
			  </ssf:ifnotaccessible>
			  
			  <ssf:ifaccessible>

				<a href="javascript: ;" onClick="ss_changeUserFilter(this, '<c:out value=""/>');return false;">--<ssf:nlt tag="none" text="none"/>--</a>

				<c:forEach var="filter" items="${ss_searchFilters}">
				<a href="javascript: ;" onClick="ss_changeUserFilter(this, '<c:out value="${filter.key}"/>');return false;"><c:out value="${filter.key}"/></a>
				</c:forEach>
			  
			  </ssf:ifaccessible>
				
			</ssf:menu>
			
		
			<input type="hidden" name="select_filter">
		</form>
	</li>
	
	<li>
		<c:if test="${ssConfigJspStyle != 'template'}">
		<a href="<portlet:renderURL windowState="maximized">
				<portlet:param name="action" value="build_filter"/>
				<portlet:param name="binderId" value="${ssBinder.id}"/>
				<portlet:param name="binderType" value="${ssBinder.entityType}"/>
				</portlet:renderURL>"
		><ssf:nlt tag="Edit" text="Edit"/></a>
		</c:if>
		<c:if test="${ssConfigJspStyle == 'template'}">
		<ssf:nlt tag="Edit" text="Edit"/>
		</c:if>
	</li>

</ul>
