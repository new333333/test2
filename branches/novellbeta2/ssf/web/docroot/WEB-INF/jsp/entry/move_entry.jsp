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
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<c:set var="wsTreeName" value="${renderResponse.namespace}_wsTree"/>
<script type="text/javascript">
function ${wsTreeName}_showId(id, obj, action) {
	var formObj = ss_getContainingForm(obj);
	var r = formObj.destination;
	for (var i = 0; i < r.length; i++) {
		r[i].checked = false;
		if (r[i].value == id) {
			r[i].checked = true;
		}
	}
	return false;
}
</script>

<div class="ss_style ss_portlet">
<div style="padding:4px;">
<span class="ss_bold ss_largerprint"><ssf:nlt tag="move.entry"/></span>
<br/>
<br/>
<span><ssf:nlt tag="move.currentEntry"/>: </span>
<span><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
  //
<span class="ss_bold">${ssEntry.title}</span>
  
<br/>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url
	action="modify_folder_entry"
	operation="move"
	folderId="${ssBinder.id}"
	entryId="${ssEntry.id}"/>" name="<portlet:namespace />fm">
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
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
