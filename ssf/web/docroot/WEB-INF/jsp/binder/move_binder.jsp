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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
String wsTreeName = renderResponse.getNamespace() + "_wsTree";
%>
<c:set var="wsTreeName" value="${renderResponse.namespace}_wsTree"/>
<script type="text/javascript">
function ${wsTreeName}_showId(id, obj, action) {
	var formObj = ss_getContainingForm(obj);
	var r = formObj.destination;
	r.value = id;
	return false;
}

</script>

<div class="ss_style ss_portlet">
<div style="padding:4px;">
<c:if test="${ssBinder.entityType == 'folder'}">
  <span class="ss_bold ss_largerprint"><ssf:nlt tag="move.folder"/></span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span class="ss_bold ss_largerprint"><ssf:nlt tag="move.workspace"/></span>
</c:if>
<br/>
<br/>
<c:if test="${ssBinder.entityType == 'folder'}">
  <span><ssf:nlt tag="move.currentFolder"/>: </span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span><ssf:nlt tag="move.currentWorkspace"/></span>
</c:if>
<span class="ss_bold"><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
<br/>
<form class="ss_style ss_form" method="post" 
		  action="<portlet:actionURL>
		 <portlet:param name="action" value="modify_binder"/>		  
		 <portlet:param name="operation" value="move"/>
		 <portlet:param name="binderId" value="${ssBinder.id}"/>
		 <portlet:param name="binderType" value="${ssBinder.entityType}"/>
		 </portlet:actionURL>" name="<portlet:namespace />fm">
<br/>

<span class="ss_bold"><ssf:nlt tag="move.selectDestination"/></span>
<br/>
<div class="ss_indent_large">
<ssf:tree treeName="${wsTreeName}"
	treeDocument="${ssWsDomTree}"  
 	rootOpen="true"
	singleSelect="true" 
	singleSelectName="destination" />

</div>

<br/>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</form>
</div>
</div>
