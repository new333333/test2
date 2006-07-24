<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!empty ssDefinitionEntry.parentBinder.id}">
<script type="text/javascript">
function ss_navigation_goto(url) {
	if (self.window != self.top) {
		parent.location.reload(true);
		return false;
	} else {
		return(ss_openUrlInPortlet(url));
	}
}
</script>
<div class="ss_smallprint">
<table>
<tr>
<c:if test="${!empty ssDefinitionEntry.parentBinder.iconName}">
<td valign="middle"><img 
  src="<html:imagesPath/>${ssDefinitionEntry.parentBinder.iconName}"/></td>
</c:if>
<td valign="middle">
<a style="text-decoration: none;" 
<c:if test="${ssDefinitionEntry.parentBinder.entityIdentifier.entityType == 'folder'}">
  href="<ssf:url 
  folderId="${ssDefinitionEntry.parentBinder.id}" 
  action="view_folder_listing"/>"
</c:if>
<c:if test="${ssDefinitionEntry.parentBinder.entityIdentifier.entityType == 'workspace'}">
  href="<ssf:url 
  folderId="${ssDefinitionEntry.parentBinder.id}" 
  action="view_ws_listing"/>"
</c:if>
  onClick="return(ss_navigation_goto(this.href));"
>
<c:if test="${empty ssDefinitionEntry.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${ssDefinitionEntry.parentBinder.title}" /></a></td>
</tr>
</table>
</div>
</c:if>

