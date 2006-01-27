<% // User filters %>
<%@ page import="java.util.List" %>
<%@ page import="com.sitescape.ef.domain.UserProperties" %>
<%
	UserProperties userFolderProperties = (UserProperties) request.getAttribute("ssUserFolderProperties");
	if (userFolderProperties != null) {
		Map searchFilters = (Map) userFolderProperties.getProperty("searchFilters");
		if (searchFilters == null) searchFilters = new java.util.HashMap();
		
		renderRequest.setAttribute("ss_searchFilters", searchFilters);
	}
%>
<div style="display:inline;" id="<portlet:namespace/>ss_filter_select">
<span class="ss_bold"><ssf:nlt tag="filter.filter" text="Filter"/></span>&nbsp;<select>
<option value="">--<ssf:nlt tag="none" text="none"/>--</option>
<c:forEach var="filter" items="${ss_searchFilters}">
<option value="<c:out value="${filter.key}"/>"><c:out value="${filter.key}"/></option>
</c:forEach>
</select>
<a href="<portlet:renderURL windowState="maximized">
		<portlet:param name="action" value="build_filter"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		</portlet:renderURL>"
><span class="ss_fineprint"><ssf:nlt tag="edit" text="edit"/></span></a>
</div>
<script type="text/javascript">
	var spanObj = ss_createSpannedAreaObj("<portlet:namespace/>ss_filter_select");
    spanObj.setShowRoutine('ss_showObjInline', "<portlet:namespace/>ss_filter_select")
    spanObj.setHideRoutine('ss_hideObjInline', "<portlet:namespace/>ss_filter_select")
</script>
