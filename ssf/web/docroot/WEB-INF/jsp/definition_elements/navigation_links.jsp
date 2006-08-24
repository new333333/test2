<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div class="ss_smallprint" style="margin-bottom:8px;">
<c:if test="${!empty ssDefinitionEntry.parentBinder.id}">
<c:set var="parentBinder" value="${ssDefinitionEntry.parentBinder}"/>
<jsp:useBean id="parentBinder" type="java.lang.Object" />
<%
	Stack parentTree = new Stack();
	while (parentBinder != null) {
		parentTree.push(parentBinder);
		parentBinder = ((Binder)parentBinder).getParentBinder();
	}
	while (!parentTree.empty()) {
		Binder nextBinder = (Binder) parentTree.pop();
%>
<c:set var="nextBinder" value="<%= nextBinder %>"/>
<a style="text-decoration: none;" 
<c:if test="${nextBinder.entityIdentifier.entityType == 'folder'}">
  href="<ssf:url 
  folderId="${nextBinder.id}" 
  action="view_folder_listing"/>"
</c:if>
<c:if test="${nextBinder.entityIdentifier.entityType == 'workspace'}">
  href="<ssf:url 
  folderId="${nextBinder.id}" 
  action="view_ws_listing"/>"
</c:if>
  onClick="return(ss_navigation_goto(this.href));"
>
<c:if test="${empty nextBinder.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${nextBinder.title}" /></a>&nbsp;&nbsp;//&nbsp;&nbsp;
<%
	}
%>
</c:if>
<span class="ss_bold">
<a style="text-decoration: none;" 
<c:if test="${ssDefinitionEntry.entityIdentifier.entityType == 'folderEntry'}">
  href="<ssf:url 
  folderId="${ssDefinitionEntry.parentBinder.id}" 
  entryId="${ssDefinitionEntry.id}" 
  action="view_folder_entry"/>"
</c:if>
<c:if test="${ssDefinitionEntry.entityIdentifier.entityType == 'folder'}">
  href="<ssf:url 
  folderId="${ssDefinitionEntry.id}" 
  action="view_folder_listing"/>"
</c:if>
<c:if test="${ssDefinitionEntry.entityIdentifier.entityType == 'workspace'}">
  href="<ssf:url 
  folderId="${ssDefinitionEntry.id}" 
  action="view_ws_listing"/>"
</c:if>
  onClick="return(ss_navigation_goto(this.href));"
>
<c:if test="${empty ssDefinitionEntry.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${ssDefinitionEntry.title}" /></a></span>
</div>

