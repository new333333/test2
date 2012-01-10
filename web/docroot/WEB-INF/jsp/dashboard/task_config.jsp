<%
// The dashboard "task summary" component
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<jsp:useBean id="ssDashboard" type="java.util.Map" scope="request" />
<jsp:useBean id="ssComponentId" type="java.lang.String" scope="request" />

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
	function ${treeName}_showId(forum, obj) {
		return ss_checkTree(obj, "ss_tree_checkbox${treeName}ss_folder_id" + forum);	
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
		<td><label for="data_resultsCount"><span><ssf:nlt tag="dashboard.search.resultsCount"/></span></label></td>
		<td style="padding-left:10px;"><input type="text" name="data_resultsCount" 
			id="data_resultsCount" size="5"	value="${resultsCount}"/></td>
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
    <input type="checkbox" name="del_${folder.id}" id="del_${folder.id}"/>
    <label for="del_${folder.id}">
	    <c:if test="${!empty folder.parentBinder}">
	    	${folder.parentBinder.title} // 
	    </c:if>
	    ${folder.title}
    </label>
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
				<ssf:nlt tag="dashboard.task.assignedTo"/>
				
				<br/><br/>
				<%
					java.util.Set userListSet = new java.util.HashSet();
					java.util.Set groupListSet = new java.util.HashSet();
					Set teamList = new java.util.HashSet();;
	
					Map components = null;
					Map component = null;
					Map data = null;
					Map dashboard = (Map)ssDashboard.get("dashboard");
					if (dashboard != null) components = (Map)dashboard.get("components");
					if (components != null) component = (Map)components.get(ssComponentId.toString());
					if (component != null) data = (Map)component.get("data");
					if (data != null && data.containsKey("assignedTo")) {
						List userList = org.kablink.teaming.util.ResolveIds.getPrincipals((Set)data.get("assignedTo"));
						if (userList != null) {
							userListSet.addAll(userList);
						}
					}
					if (data != null && data.containsKey("assignedToGroups")) {
						List groupList = org.kablink.teaming.util.ResolveIds.getPrincipals((Set)data.get("assignedToGroups"));
						if (groupList != null) {
							groupListSet.addAll(groupList);
						}
					}	
					if (data != null && data.containsKey("assignedToTeams")) {
						teamList = org.kablink.teaming.util.ResolveIds.getBinders((Set)data.get("assignedToTeams"));
					}									
				%>		
				<div style="margin-left: 20px;">
					<ssf:nlt tag="dashboard.task.assignedToUser"/>
					<ssf:find formName="${formName}" formElement="assignedTo" type="user" 
					  userList="<%= userListSet %>" addCurrentUser="true" width="150px"/>
				</div>
			<br/>
				<ssf:nlt tag="dashboard.task.assignedToOr"/>
			<br/><br/>
				<div style="margin-left: 20px;">
					<ssf:nlt tag="dashboard.task.assignedToGroup"/>
					<ssf:find formName="${formName}" formElement="assignedToGroup" type="group" 
					  userList="<%= groupListSet %>" width="150px"/>
				</div>
			<br/>
				<ssf:nlt tag="dashboard.task.assignedToOr"/>
				<br/><br/>
				<div style="margin-left: 20px;">
					<ssf:nlt tag="dashboard.task.assignedToTeam"/>
					<ssf:find formName="${formName}" formElement="assignedToTeam" type="teams" 
					  userList="<%= teamList %>" width="150px"/>	
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
					<table><tr><td>&nbsp;&nbsp;&nbsp;<input type="checkbox" name="chooseFolder" id="chooseFolder"
						<c:if test="${!empty ssDashboard.dashboard.components[ssComponentId].data.chooseViewType}">checked="checked"</c:if>>
						<label for="chooseFolder"><span><ssf:nlt tag="dashboard.task.selectFolderRelative"/></span></label></td></tr>
					</table>
				</c:if>
				
				<ssf:tree 
				  treeName="${treeName}" 
				  treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
				  rootOpen="true" 
				  multiSelect="${ssDashboard.beans[ssComponentId].ssBinderIdList}" 
				  multiSelectPrefix="ss_folder_id"
				/>
			</div>
</td></tr></table>
