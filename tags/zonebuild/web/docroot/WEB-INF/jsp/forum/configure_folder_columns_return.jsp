<%
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
<c:set var="commentsChecked" value=""/>
<c:if test="${empty folderColumns || !empty folderColumns.comments}"><c:set var="commentsChecked" value="checked"/></c:if>
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
  <input type="checkbox" name="comments" ${commentsChecked}> <ssf:nlt tag="folder.column.Comments"/><br/>
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
