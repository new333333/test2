<%
// The dashboard "task summary" component
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
<c:set var="treeName" value="${ssComponentId}${renderResponse.namespace}"/>

<script type="text/javascript" src="<html:rootPath/>js/widget/SelectPageable.js"></script>
<script type="text/javascript">
	dojo.require('dojo.widget.*');

	function ${treeName}_showId(forum, obj) {
		var formObj = ss_getContainingForm(obj);
		var r = formObj.ss_folder_id;
	    for (var b = 0; b < r.length; b++) {
	      if (r[b].value == forum) 	r[b].checked=true;
		}
		ss_clearSingleSelect('${treeName}');
		
		return false;
	}
	
	function ss_addAuthor(authorId, authorName) {
		var div = document.createElement('div');
	
		var aDiv = document.createElement('div');
		aDiv.id = "placeholderAuthor";

		div.appendChild(document.createTextNode("<ssf:nlt tag="dashboard.task.assignedTo"/>: "));
		div.appendChild(aDiv);
		document.getElementById("ss_authors_options").appendChild(div);

		var url = ss_AjaxBaseUrl + "&operation=get_users_widget&searchText=%{searchString}&pager=%{pagerString}&randomNumber="+ss_random++;
		var props = {name : "assignedTo", 
						id : "assignedTo", 
						dataUrl:url,
						maxListLength : 12,
						autoComplete: false};
		var usersWidget = dojo.widget.createWidget("SelectPageable", props, document.getElementById("placeholderAuthor"));
		if (authorId && authorName && authorId!="" && authorName!="") {
			usersWidget.setValue(authorId);
			usersWidget.setLabel(authorName);
		}
	}
	

</script>

<br/>
<br/>
<c:set var="resultsCount" value="${ssDashboard.dashboard.components[ssComponentId].data.resultsCount}"/>
<c:if test="${empty resultsCount}">
	<c:set var="resultsCount" value="5"/>
</c:if>
<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount}"/>
<c:if test="${empty summaryWordCount}">
	<c:set var="summaryWordCount" value="20"/>
</c:if>
<table>
	<tr>
		<td><span><ssf:nlt tag="dashboard.search.resultsCount"/></span></td>
		<td style="padding-left:10px;"><input type="text" name="data_resultsCount" size="5"
  			value="${resultsCount}"/></td>
	</tr>
	<tr>
		<td><span><ssf:nlt tag="dashboard.search.summardWordCount"/></span></td>
		<td style="padding-left:10px;"><input type="text" name="data_summaryWordCount" size="5" 
  			value="${summaryWordCount}"/></td>
	</tr>
</table>

<br/>
<br/>
<table class="ss_style" width="100%">
	<tr>
		<td>
			<!--
			<div>
				<input type="checkbox" name="filterAssignedToCurrentUser" id="filterAssignedToCurrentUser${renderResponse.namespace}" 
					<c:if test="${!empty ssDashboard.dashboard.components[ssComponentId].data.assignedToCurrentUser}">checked="checked"</c:if>>
				<label for="filterAssignedToCurrentUser${renderResponse.namespace}"><ssf:nlt tag="dashboard.task.assignedToCurrentUser"/></label>
			</div>

			<div>
				<input type="checkbox" name="filterAssignedToMe" id="filterAssignedToMe${renderResponse.namespace}" 
					<c:if test="${!empty ssDashboard.dashboard.components[ssComponentId].data.assignedToMe}">checked="checked"</c:if>>
				<label for="filterAssignedToMe${renderResponse.namespace}"><ssf:nlt tag="dashboard.task.assignedToMe"/></label>
			</div>
			-->
			
			<div id="ss_authors_options"></div>
			
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
				  rootOpen="false" 
				  multiSelect="${ssDashboard.beans[ssComponentId].ssBinderIdList}" 
				  multiSelectPrefix="ss_folder_id_"
				/>
			</div>

<script type="text/javascript">

	<c:if test="${! empty ssDashboard.dashboard.components[ssComponentId].data.assignedTo}">
		ss_addAuthor("${ssDashboard.dashboard.components[ssComponentId].data.assignedTo}", "${ssDashboard.dashboard.components[ssComponentId].data.assignedToName}");
	</c:if>
	
	<c:if test="${empty ssDashboard.dashboard.components[ssComponentId].data.assignedTo}">
		ss_addAuthor();
	</c:if>
</script>
