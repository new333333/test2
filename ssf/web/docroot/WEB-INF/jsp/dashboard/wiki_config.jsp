<%
// The dashboard "wiki summary" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<br/>
<c:set var="treeName" value="${ssComponentId}${renderResponse.namespace}"/>
<script type="text/javascript">
function ${treeName}_showId(forum, obj) {
	var formObj = ss_getContainingForm(obj);
	var r = formObj.ss_folder_id;
    for (var b = 0; b < r.length; b++) {
      if (r[b].value == forum) 	r[b].checked=true;
	}
	ss_clearSingleSelect('${treeName}');
	
	return false;
}
</script>
<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssBinder}">
<span><ssf:nlt tag="portlet.forum.selected.folder"/></span>
<span class="ss_bold">${ssDashboard.beans[ssComponentId].ssBinder.title}</span>
</c:if>
<br/><br/>
<span class="ss_bold">
  <ssf:nlt tag="dashboard.wiki.selectWikiFolder"/>
</span>
<br/>
<div class="ss_indent_large">
<ssf:tree treeName="${treeName}"
	treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
 	rootOpen="true"
	singleSelect="${ssDashboard.beans[ssComponentId].ssBinder.id}" 
	singleSelectName="ss_folder_id" />

</div>

<br/>