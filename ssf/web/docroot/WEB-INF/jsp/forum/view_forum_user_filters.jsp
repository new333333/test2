<% // User filters %>
<%@ page import="java.util.List" %>
<%@ page import="com.sitescape.ef.util.NLT" %>
<%@ page import="com.sitescape.ef.domain.UserProperties" %>
<%
	UserProperties userFolderProperties = (UserProperties) request.getAttribute("ssUserFolderProperties");
	String filterName = "--" + NLT.get("none") + "--";
	if (userFolderProperties != null) {
		Map searchFilters = (Map) userFolderProperties.getProperty("searchFilters");
		if (searchFilters == null) searchFilters = new java.util.HashMap();
		String userFilter = (String) userFolderProperties.getProperty("userFilter");
		if (userFilter != null && !userFilter.equals("")) filterName = userFilter;
		
		renderRequest.setAttribute("ss_searchFilters", searchFilters);
	}
%>
<div style="display:inline;" id="<portlet:namespace/>ss_filter_select">
<form name="ss_filterSelect" class="ss_style" 
	action="<portlet:renderURL windowState="maximized">
		<portlet:param name="action" value="view_listing"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="operation" value="select_filter"/>
		</portlet:renderURL>" method="post" style="display:inline;">
<span class="ss_bold"><%= NLT.get("filter.filter") %>:&nbsp;
<div style='display:inline; border:1px solid black; background:#ffffff;'>
<ssf:menu title="<%= filterName %>">
<ul>
<li><a href="javascript: ;" 
  onClick="ss_changeUserFilter('<c:out value=""/>');return false;"
>--<ssf:nlt tag="none" text="none"/>--</a></li>
<c:forEach var="filter" items="${ss_searchFilters}">
<li><a href="javascript: ;" 
  onClick="ss_changeUserFilter('<c:out value="${filter.key}"/>');return false;"
><c:out value="${filter.key}"/></a></li>
</c:forEach>
</ul>
</ssf:menu>
</div>&nbsp;&nbsp;&nbsp;
</span>
<a href="<portlet:renderURL windowState="maximized">
		<portlet:param name="action" value="build_filter"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		</portlet:renderURL>"
><span class="ss_fineprint"><ssf:nlt tag="edit" text="edit"/></span></a>

<input type="hidden" name="select_filter">
</form></div>
<script type="text/javascript">
function ss_changeUserFilter(filter) {
	document.forms.ss_filterSelect.select_filter.value = filter;
	document.forms.ss_filterSelect.submit();
}
</script>

