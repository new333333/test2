<%
// The dashboard "guestbook summary" component
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="treeName" value="${ssComponentId}${renderResponse.namespace}"/>
<script type="text/javascript">
function ${treeName}_showId(forum, obj) {
	return ss_checkTree(obj, "ss_tree_radio${treeName}ss_folder_id" + forum)
}

</script>

<br/>
<br/>
<c:set var="resultsCount" value="${ssDashboard.dashboard.components[ssComponentId].data.resultsCount}"/>
<c:if test="${empty resultsCount}"><c:set var="resultsCount" value="5"/></c:if>
<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount}"/>
<c:if test="${empty summaryWordCount}"><c:set var="summaryWordCount" value="20"/></c:if>
<table>
<tr>
<td><label for="data_resultsCount"><span><ssf:nlt tag="dashboard.search.resultsCount"/></span></label></td>
<td style="padding-left:10px;"><input type="text" name="data_resultsCount" 
  id="data_resultsCount" size="5" value="${resultsCount}"/></td>
</tr>
<tr>
<td><label for="data_summaryWordCount"><span><ssf:nlt tag="dashboard.search.summardWordCount"/></span></label></td>
<td style="padding-left:10px;"><input type="text" name="data_summaryWordCount" 
  id="data_summaryWordCount" size="5" value="${summaryWordCount}"/></td>
</tr>
</table>

<br/>
<br/>
<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssBinder}">
<span class="ss_bold"><ssf:nlt tag="portlet.forum.selected.folder"/></span>
${ssDashboard.beans[ssComponentId].ssBinder.title}
</c:if>
<br/><br/>

<span class="ss_bold">
  <ssf:nlt tag="dashboard.guestbook.selectGuestbookFolder"/>
</span>
<br>
<br>
<div class="ss_indent_large">
<c:if test="${ssDashboard.scope == 'binder' || ssDashboard.scope == 'local' }">
<table><tr><td>&nbsp;&nbsp;&nbsp;<input type="checkbox" name="chooseFolder" id="chooseFolder" 
	<c:if test="${!empty ssDashboard.dashboard.components[ssComponentId].data.chooseViewType}">checked="checked"</c:if>>
	<label for="chooseFolder"><span>
  <ssf:nlt tag="dashboard.guestbook.selectFolderRelative"/>
</span></label></td></tr></table>
</c:if>
<ssf:tree 
  treeName="${treeName}"
  treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
  rootOpen="true" 
  singleSelect="${ssDashboard.beans[ssComponentId].ssBinder.id}" 
  singleSelectName="ss_folder_id"
/>
</div>
</td></tr></table>
<br/>