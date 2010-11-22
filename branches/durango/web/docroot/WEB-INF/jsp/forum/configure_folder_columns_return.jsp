<%
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
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>
<jsp:useBean id="ssUserFolderProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.Binder" scope="request" />

<c:choose>
<c:when test="${!empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
<span><ssf:nlt tag="general.notLoggedIn"/></span>
</c:when>
<c:otherwise>

<c:set var="ss_folderViewColumnsType" value="${ssFolderViewType}" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_column_defaults.jsp" %>

<c:set var="folderChecked" value=""/>
<c:if test="${empty ssFolderColumns || !empty ssFolderColumns.folder}"><c:set var="folderChecked" value="checked"/></c:if>
<c:set var="numberChecked" value=""/>
<c:if test="${!empty ssFolderColumns.number}"><c:set var="numberChecked" value="checked"/></c:if>
<c:set var="titleChecked" value=""/>
<c:if test="${empty ssFolderColumns || !empty ssFolderColumns.title}"><c:set var="titleChecked" value="checked"/></c:if>
<c:set var="commentsChecked" value=""/>
<c:if test="${empty ssFolderColumns || !empty ssFolderColumns.comments}"><c:set var="commentsChecked" value="checked"/></c:if>
<c:set var="sizeChecked" value=""/>
<c:if test="${empty ssFolderColumns || !empty ssFolderColumns.size}"><c:set var="sizeChecked" value="checked"/></c:if>
<c:set var="downloadChecked" value=""/>
<c:if test="${empty ssFolderColumns || !empty ssFolderColumns.download}"><c:set var="downloadChecked" value="checked"/></c:if>
<c:set var="htmlChecked" value=""/>
<c:if test="${empty ssFolderColumns || !empty ssFolderColumns.html}"><c:set var="htmlChecked" value="checked"/></c:if>
<c:set var="stateChecked" value=""/>
<c:if test="${empty ssFolderColumns || !empty ssFolderColumns.state}"><c:set var="stateChecked" value="checked"/></c:if>
<c:set var="authorChecked" value=""/>
<c:if test="${empty ssFolderColumns || !empty ssFolderColumns.author}"><c:set var="authorChecked" value="checked"/></c:if>
<c:set var="dateChecked" value=""/>
<c:if test="${empty ssFolderColumns || !empty ssFolderColumns.date}"><c:set var="dateChecked" value="checked"/></c:if>
<c:set var="ratingChecked" value=""/>
<c:if test="${empty ssFolderColumns || !empty ssFolderColumns.rating}"><c:set var="ratingChecked" value="checked"/></c:if>


<table class="ss_popup" cellpadding="0" cellspacing="0" border="0" style="width: ${width}; height:420px; overflow:scroll;">
 <tbody>
  <tr>
   <td valign="top" width="100%"><div class="ss_popup_top">
   <div class="ss_popup_title"><ssf:nlt tag="folder.selectColumns"/></div></div>
      </td>
  </tr>
  <tr><td valign="top">
   <div class="ss_popup_body">
<form method="post" onSubmit="ss_setActionUrl(this, ss_saveFolderColumnsUrl);">
<div class="ss_indent_medium">
  <c:if test="${ssFolderType == 'search'}">
    <input type="checkbox" name="folder" ${folderChecked}> <ssf:nlt tag="folder.column.Folder"/><br/>
  </c:if>
  <input type="checkbox" name="number" ${numberChecked}> <ssf:nlt tag="folder.column.Number"/><br/>
  <input type="checkbox" name="title" ${titleChecked}> <ssf:nlt tag="folder.column.Title"/><br/>
  <input type="checkbox" name="author" ${authorChecked}> <ssf:nlt tag="folder.column.Author"/><br/>
  <input type="checkbox" name="comments" ${commentsChecked}> <ssf:nlt tag="folder.column.CommentsOrReplies"/><br/>
  <input type="checkbox" name="size" ${sizeChecked}> <ssf:nlt tag="folder.column.Size"/><br/>
  <input type="checkbox" name="download" ${downloadChecked}> <ssf:nlt tag="folder.column.Download"/><br/>
  <input type="checkbox" name="html" ${htmlChecked}> <ssf:nlt tag="folder.column.Html"/><br/>
  <input type="checkbox" name="state" ${stateChecked}> <ssf:nlt tag="folder.column.State"/><br/>
  <input type="checkbox" name="date" ${dateChecked}> <ssf:nlt tag="folder.column.LastActivity"/><br/>
  <input type="checkbox" name="rating" ${ratingChecked}> <ssf:nlt tag="folder.column.Rating"/><br/>
  <br/>
  
  <c:forEach var="def" items="${ssEntryDefinitionElementDataMap}">
  	
  	<c:forEach var="element" items="${def.value}">
       <c:if test="${element.value.type == 'selectbox' || 
                     element.value.type == 'radio' || 
                     element.value.type == 'checkbox' || 
       				 element.value.type == 'date'  || 
       				 element.value.type == 'text'  || 
       				 element.value.type == 'user_list' || 
       				 element.value.type == 'userListSelectbox'}">
		<c:set var="checked" value=""/>
		<c:set var="colName" value="${def.key},${element.value.type},${element.key},${element.value.caption}"/>
		<c:if test="${!empty ssFolderColumns[colName]}"><c:set var="checked" value="checked"/></c:if>
         <input type="checkbox" name="customCol_${colName}" ${checked}> 
         <ssf:nlt tag="${ssEntryDefinitionMap[def.key].title}" checkIfTag="true"/> / 
         <ssf:nlt tag="${element.value.caption}" checkIfTag="true"/><br/>
       </c:if>
       <c:if test="${element.value.type == 'entryAttributes'}">
         <c:forEach var="attributeSetName" items="${ssBinder.customAttributes[element.key].valueSet}">
			<c:set var="checked" value=""/>
			<c:set var="colName" value="${def.key},${element.value.type},${element.key}__set__${attributeSetName},${attributeSetName}"/>
			<c:if test="${!empty ssFolderColumns[colName]}"><c:set var="checked" value="checked"/></c:if>
	         <input type="checkbox" name="customCol_${colName}" ${checked}> 
	         <ssf:nlt tag="${ssEntryDefinitionMap[def.key].title}" checkIfTag="true"/> / 
	         <ssf:nlt tag="${element.value.caption}: ${attributeSetName}" checkIfTag="true"/><br/>
	     </c:forEach>
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
</td></tr></tbody></table>
</c:otherwise>
</c:choose>
