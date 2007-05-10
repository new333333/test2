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
String wsTreeName = renderResponse.getNamespace();
%>
<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssBinder}">
<b><ssf:nlt tag="portlet.workspace.selected.workspace"/></b>
<c:out value="${ssBinder.title}" />
<br/>
</c:if>
<form class="ss_style ss_form" action="<portlet:actionURL/>" method="post" name="<portlet:namespace />fm">

<br/>
<span class="ss_bold"><ssf:nlt tag="portlet.workspace.select.workspace" /></span>
<br>
<script type="text/javascript">
function <%= wsTreeName %>_showId(forum, obj) {
	var r = self.document.<portlet:namespace />fm.topWorkspace;
    for (var b = 0; b < r.length; b++) {
      if (r[b].value == forum) 	r[b].checked=true;
	}
	ss_clearSingleSelect('<%= wsTreeName %>');
	
	return false;
}
</script>
<c:set var="singleSelect" value=""/>
<c:if test="${!empty ssBinder}">
	<c:set var="singleSelect" value="${ssBinder.id}"/>
</c:if>
<ssf:tree treeName="<%= wsTreeName %>"  treeDocument="${ssWsDomTree}" 
 	topId="${ssWsDomTreeBinderId}" rootOpen="true"
	 singleSelect="${singleSelect}" singleSelectName="topWorkspace" />

<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
<input style="margin-left:15px;" type="submit" class="ss_submit" name="closeBtn"
 value="<ssf:nlt tag="button.close"/>"
 onClick="self.location.href='<portlet:renderURL windowState="normal" portletMode="view"/>';return false;"
</form>
<br>

</td></tr></table>

