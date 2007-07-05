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
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>

<c:choose>
<c:when test="${!empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
<span><ssf:nlt tag="general.notLoggedIn"/></span>
</c:when>
<c:otherwise>

<c:set var="folderChecked" value=""/>
<c:if test="${empty folderColumns || !empty folderColumns.folder}"><c:set var="folderChecked" value="checked"/></c:if>
<c:set var="numberChecked" value=""/>
<c:if test="${empty folderColumns || !empty folderColumns.number}"><c:set var="numberChecked" value="checked"/></c:if>
<c:set var="titleChecked" value=""/>
<c:if test="${empty folderColumns || !empty folderColumns.title}"><c:set var="titleChecked" value="checked"/></c:if>
<c:set var="sizeChecked" value=""/>
<c:if test="${empty folderColumns || !empty folderColumns.size}"><c:set var="sizeChecked" value="checked"/></c:if>
<c:set var="downloadChecked" value=""/>
<c:if test="${empty folderColumns || !empty folderColumns.download}"><c:set var="downloadChecked" value="checked"/></c:if>
<c:set var="htmlChecked" value=""/>
<c:if test="${empty folderColumns || !empty folderColumns.html}"><c:set var="htmlChecked" value="checked"/></c:if>
<c:set var="stateChecked" value=""/>
<c:if test="${empty folderColumns || !empty folderColumns.state}"><c:set var="stateChecked" value="checked"/></c:if>
<c:set var="authorChecked" value=""/>
<c:if test="${empty folderColumns || !empty folderColumns.author}"><c:set var="authorChecked" value="checked"/></c:if>
<c:set var="dateChecked" value=""/>
<c:if test="${empty folderColumns || !empty folderColumns.date}"><c:set var="dateChecked" value="checked"/></c:if>

<div class="ss_style" align="left">
<form method="post" onSubmit="ss_setActionUrl(this, ss_saveFolderColumnsUrl);">
<span class="ss_largerprint ss_bold"><ssf:nlt tag="folder.selectColumns"/></span>
<br/>
<br/>
<div class="ss_indent_medium">
  <c:if test="${ssFolderType == 'search'}">
    <input type="checkbox" name="folder" ${folderChecked}> <ssf:nlt tag="folder.column.Folder"/><br/>
  </c:if>
  <input type="checkbox" name="number" ${numberChecked}> <ssf:nlt tag="folder.column.Number"/><br/>
  <input type="checkbox" name="title" ${titleChecked}> <ssf:nlt tag="folder.column.Title"/><br/>
  <input type="checkbox" name="size" ${sizeChecked}> <ssf:nlt tag="folder.column.Size"/><br/>
  <input type="checkbox" name="download" ${downloadChecked}> <ssf:nlt tag="folder.column.Download"/><br/>
  <input type="checkbox" name="html" ${htmlChecked}> <ssf:nlt tag="folder.column.Html"/><br/>
  <input type="checkbox" name="state" ${stateChecked}> <ssf:nlt tag="folder.column.State"/><br/>
  <input type="checkbox" name="author" ${authorChecked}> <ssf:nlt tag="folder.column.Author"/><br/>
  <input type="checkbox" name="date" ${dateChecked}> <ssf:nlt tag="folder.column.LastActivity"/><br/>
  <br/>
  
  <c:forEach var="def" items="${ssEntryDefinitionElementDataMap}">
  	
  	<c:forEach var="element" items="${def.value}">
       <c:if test="${element.value.type == 'selectbox' || 
                     element.value.type == 'radio' || 
                     element.value.type == 'checkbox' || 
       				 element.value.type == 'date'  || 
       				 element.value.type == 'user_list' || 
       				 element.value.type == 'userListSelectbox'}">
		<c:set var="checked" value=""/>
		<c:set var="colName" value="${def.key},${element.value.type},${element.key},${element.value.caption}"/>
		<c:if test="${!empty folderColumns[colName]}"><c:set var="checked" value="checked"/></c:if>
         <input type="checkbox" name="customCol_${colName}" ${checked}> 
         <ssf:nlt tag="${ssEntryDefinitionMap[def.key].title}" checkIfTag="true"/> / 
         <ssf:nlt tag="${element.value.caption}" checkIfTag="true"/><br/>
       </c:if>
  	</c:forEach>
  </c:forEach>
   
<c:if test="${ss_accessControlMap[ssBinder.id]['modifyBinder'] and ssConfigJspStyle != 'template'}">
  <br/>
  <input type="checkbox" name="setFolderDefaultColumns"/>
  <span class="ss_labelAfter"><label for="setFolderDefaultColumns">
	 <ssf:nlt tag="misc.configureColumns.folderDefault"/>
  </label></span>
</c:if>

  <br/>
  <br/>
  <input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
  &nbsp;&nbsp;&nbsp;
  <input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelPopupDiv('ss_folder_column_menu');return false;">
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <input type="submit" name="defaultBtn" value="<ssf:nlt tag="button.restoreDefaults"/>">
  
</div>
</form>
</div>
</c:otherwise>
</c:choose>
