<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div class="ss_breadcrumb">
<ul style="margin-left:-15px;">
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
<li style="float:left;">
<c:if test="${empty ssNavigationLinkTree[nextBinder.id]}">
<a
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
<c:out value="${nextBinder.title}" /></a>
</c:if>
<c:if test="${!empty ssNavigationLinkTree[nextBinder.id]}">
<div style="display:inline">
<ssf:tree treeName="wsTree${nextBinder.id}" treeDocument="${ssNavigationLinkTree[nextBinder.id]}" 
  topId="${nextBinder.id}" rootOpen="false" showImages="false" dynamic="true" showIdRoutine="ss_treeShowId" />
</div>
</c:if>
</li>
<li style="float:left; padding-top:2px;">&nbsp;&nbsp;//&nbsp;&nbsp;</li>
<%
	}
%>
</c:if>
<li style="float:left;">
<c:if test="${empty ssNavigationLinkTree[ssDefinitionEntry.id]}">
<a class="ss_bold"
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
<c:out value="${ssDefinitionEntry.title}" /></a>
</c:if>
<c:if test="${!empty ssNavigationLinkTree[ssDefinitionEntry.id]}">
<div style="display:inline">
<ssf:tree treeName="wsTree${ssDefinitionEntry.id}" 
  treeDocument="${ssNavigationLinkTree[ssDefinitionEntry.id]}" 
  topId="${ssDefinitionEntry.id}" rootOpen="false" 
  showImages="false" dynamic="true" showIdRoutine="ss_treeShowId" 
  highlightNode="${ssDefinitionEntry.id}" />
</div>
</c:if>
</li>
</ul>
</div>
<div class="ss_clear"></div>

