<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!empty ssDefinitionEntry.parentBinder.id}">
<div class="ss_smallprint">
<table>
<tr>
<td valign="middle"><img 
  src="<html:imagesPath/>${ssDefinitionEntry.parentBinder.iconName}"/></td>
<td valign="middle"><a style="text-decoration: none;" 
  href="<ssf:url 
  folderId="${ssDefinitionEntry.parentFolder.id}" 
  action="view_folder_listing"/>">
<c:if test="${empty ssDefinitionEntry.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${ssDefinitionEntry.parentBinder.title}" /></a></td>
</tr>
</table>
</div>
</c:if>

