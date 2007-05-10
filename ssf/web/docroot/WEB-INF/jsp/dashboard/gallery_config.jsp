<%
// The dashboard "gallery" component
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
<script type="text/javascript">
function ${treeName}_showId(forum, obj) {
	var formObj = ss_getContainingForm(obj);
	if (formObj["ss_folder_id_"+forum] && formObj["ss_folder_id_"+forum].checked) {
		formObj["ss_folder_id_"+forum].checked=false
	} else {
		formObj["ss_folder_id_"+forum].checked=true
	}
	return false
}
</script>
<br/>
<table class="ss_style" width="100%"><tr><td>

<c:set var="resultsCount" value="${ssDashboard.dashboard.components[ssComponentId].data.resultsCount[0]}"/>
<c:if test="${empty resultsCount}"><c:set var="resultsCount" value="5"/></c:if>
<table>
<tr>
<td><span><ssf:nlt tag="dashboard.gallery.photoCount"/></span></td>
<td style="padding-left:10px;"><input type="text" name="data_resultsCount" size="5"
  value="${resultsCount}"/></td>
</tr>
<tr>
<td><span><ssf:nlt tag="dashboard.gallery.imageSize"/></span></td>
<td style="padding-left:10px;">
<input type="radio" id="galleryImageSizeHandle" name="data_galleryImageSize" value="big">Big
<input type="radio" id="galleryImageSizeHandle" name="data_galleryImageSize" value="small">Small
</td>
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

<span class="ss_bold">
  <ssf:nlt tag="dashboard.gallery.selectFolder"/>
</span>
<br>
<br>
<div class="ss_indent_large">
<c:if test="${ssDashboard.scope == 'binder' || ssDashboard.scope == 'local' }">
<table><tr><td>&nbsp;&nbsp;&nbsp;<input type="checkbox" name="chooseFirst" 
	<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.chooseFirst}">checked="checked"</c:if>><span>
  <ssf:nlt tag="dashboard.gallery.selectFolderRelative"/>
</span></td></tr></table>
</c:if>
<ssf:tree 
  treeName="${treeName}" 
  treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
  rootOpen="false" 
  multiSelect="${ssDashboard.beans[ssComponentId].ssBinderIdList}" 
  multiSelectPrefix="ss_folder_id_"
/>
</div>
</td></tr>
</table>

<script type="text/javascript">
function ss_setRadioCheckedByValue(id, value) {
  if (value == "") value = "small";
  var rbHandle = document.getElementById(id);
  var formObj, buttonName, radioGroup;
  if (rbHandle) {
  	formObj = rbHandle.form;
  	buttonName = rbHandle.name;
  	radioGroup = formObj.elements[buttonName];
  	for (var i = 0; i < radioGroup.length; i++) {
  		if (radioGroup[i].value == value) {
  			radioGroup[i].checked = true;
  		}
  	}
  }
}
ss_setRadioCheckedByValue('galleryImageSizeHandle','${ssDashboard.dashboard.components[ssComponentId].data.galleryImageSize[0]}');
</script>
