<% // User filters %>
<%@ page import="java.util.List" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ page import="com.sitescape.team.domain.UserProperties" %>
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
<table cellspacing="0" cellpadding="0" class="ss_actions_bar_background">
<tr>
<td valign="top"><ssf:nlt tag="filter.filter" text="Filter"/>:&nbsp;</td>
<td valign="top"><form class="ss_compact ss_actions_bar_background" 
    name="ss_filterSelect" style="display:inline;"
	action="<portlet:actionURL windowState="maximized">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="operation" value="select_filter"/>
		</portlet:actionURL>" method="post" >
<ssf:menu title="<%= filterName %>" 
  titleId="ss_filterTitle" 
  titleClass="ss_compact"
  menuClass="ss_actions_bar_submenu">
<ul class="ss_actions_bar2 ss_actions_bar_submenu" style="width:250px;">
<li><a href="javascript: ;" 
  onClick="ss_changeUserFilter(this, '<c:out value=""/>');return false;"
>--<ssf:nlt tag="none" text="none"/>--</a></li>
<c:forEach var="filter" items="${ss_searchFilters}">
<li><a href="javascript: ;" 
  onClick="ss_changeUserFilter(this, '<c:out value="${filter.key}"/>');return false;"
><c:out value="${filter.key}"/></a></li>
</c:forEach>
</ul>
</ssf:menu>
<input type="hidden" name="select_filter">
</form>
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
</td>
<td valign="top" nowrap>
<c:if test="${ssConfigJspStyle != 'template'}">
<a href="<portlet:renderURL windowState="maximized">
		<portlet:param name="action" value="build_filter"/>
		<portlet:param name="binderId" value="${ssBinder.id}"/>
		<portlet:param name="binderType" value="${ssBinder.entityType}"/>
		</portlet:renderURL>"
><span class="ss_fineprint">&nbsp;&nbsp;&nbsp;<ssf:nlt tag="edit" text="edit"/></span></a>
</c:if>
<c:if test="${ssConfigJspStyle == 'template'}">
<span class="ss_fineprint">&nbsp;&nbsp;&nbsp;<ssf:nlt tag="edit" text="edit"/></span>
</c:if>
</td>
</tr>
</table>

