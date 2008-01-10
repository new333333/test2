<%
// The dashboard "task summary" component
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="treeName" value="${ssComponentId}${renderResponse.namespace}"/>

<script type="text/javascript">
	dojo.require('dojo.widget.*');
	dojo.require('sitescape.widget.SelectPageable');

	function ${treeName}_showId(forum, obj) {
		var formObj = ss_getContainingForm(obj);
		var r = formObj["ss_folder_id_" + forum];
		if (r && r.checked) {
			r.checked=false
		} else {
			r.checked=true
		}
		return false
	}

</script>

<br/>
<br/>
<c:set var="resultsCount" value="${ssDashboard.dashboard.components[ssComponentId].data.resultsCount}"/>
<c:if test="${empty resultsCount}">
	<c:set var="resultsCount" value="5"/>
</c:if>

<table>
	<tr>
		<td><span><ssf:nlt tag="dashboard.search.resultsCount"/></span></td>
		<td style="padding-left:10px;"><input type="text" name="data_resultsCount" size="5"
  			value="${resultsCount}"/></td>
	</tr>
</table>
<br/>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssFolderList}">
<table class="ss_style" cellspacing="0" cellpadding="0">
<tr><th align="left"><ssf:nlt tag="portlet.forum.selected.forums"/></th></tr>
<tr><td>&nbsp;</td></tr>
<c:forEach var="folder" items="${ssDashboard.beans[ssComponentId].ssFolderList}">
<tr>
  <td>
    <input type="checkbox" name="del_${folder.id}"/>
    <c:if test="${!empty folder.parentBinder}">
    	${folder.parentBinder.title} // 
    </c:if>
    ${folder.title}
  </td>
</tr>
</c:forEach>
</table>
  <span class="ss_fineprint" style="padding-left:4px;">[<ssf:nlt tag="portlet.forum.delete.select.forums"/>]</span>
  <br/>
</c:if>

<br/>
<table class="ss_style" width="100%">
	<tr>
		<td>
			<ssf:nlt tag="dashboard.task.assignedTo"/>:  
			<div dojoType="SelectPageable" widgetId="assignedTo_${renderResponse.namespace}" name="assignedTo" 
						id="ss_authors_options_${renderResponse.namespace}"
						maxListLength="12"
						autoComplete="false"
						dataUrl="<ssf:url 
								adapter="true" 
								portletName="ss_forum" 
								action="__ajax_request" 
								actionUrl="true">
									<ssf:param name="operation" value="get_users_widget" />
									<ssf:param name="operation" value="check_status" />
								</ssf:url>&searchText=%{searchString}&pager=%{pagerString}"							
				>
			</div>
			
			<c:if test="${!empty ssDashboard.beans[ssComponentId].ssBinder}">
				<span class="ss_bold"><ssf:nlt tag="portlet.forum.selected.folder"/></span>
				${ssDashboard.beans[ssComponentId].ssBinder.title}
			</c:if>
			<br/><br/>
	
			<span class="ss_bold">
			  <ssf:nlt tag="dashboard.task.selectTaskFolder"/>
			</span>
			<br/><br/>
			
			<div class="ss_indent_large">
				<c:if test="${ssDashboard.scope == 'binder' || ssDashboard.scope == 'local' }">
					<table><tr><td>&nbsp;&nbsp;&nbsp;<input type="checkbox" name="chooseFolder" 
						<c:if test="${!empty ssDashboard.dashboard.components[ssComponentId].data.chooseViewType}">checked="checked"</c:if>>
						<span><ssf:nlt tag="dashboard.task.selectFolderRelative"/></span></td></tr>
					</table>
				</c:if>
				
				<ssf:tree 
				  treeName="${treeName}" 
				  treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
				  rootOpen="true" 
				  multiSelect="${ssDashboard.beans[ssComponentId].ssBinderIdList}" 
				  multiSelectPrefix="ss_folder_id_"
				/>
			</div>
</td></tr></table>
<script type="text/javascript">

	djConfig.searchIds.push("ss_authors_options_${renderResponse.namespace}");

	<c:if test="${! empty ssDashboard.dashboard.components[ssComponentId].data.assignedTo}">
	dojo.addOnLoad(function () {
		var assignetToWidget${renderResponse.namespace} = dojo.widget.byId("assignedTo_${renderResponse.namespace}");
		assignetToWidget${renderResponse.namespace}.setValue("${ssDashboard.dashboard.components[ssComponentId].data.assignedTo}");
		assignetToWidget${renderResponse.namespace}.setLabel("${ssDashboard.dashboard.components[ssComponentId].data.assignedToName}");
	});
	</c:if>

</script>
