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
<%
  List allColumns = new ArrayList();
%>
<c:set var="allColumns" value="<%= allColumns %>" />
<c:forEach var="colName1" items="${ssFolderColumnsSortOrder}" >
  <c:if test="${!empty ssFolderColumns[colName1]}" >
    <jsp:useBean id="colName1" type="java.lang.String" scope="page"/>
    <% 
      //Build a list of all columns with the selected ones first in the list
      allColumns.add(colName1); 
    %> 
  </c:if>
</c:forEach>
<%
	String[] defaultCols = "number|title|comments|size|download|html|state|author|date|rating".split("\\|");
	for (int i=0; i < defaultCols.length; i++) {
		if (!allColumns.contains(defaultCols[i])) allColumns.add(defaultCols[i]);
	}
%>
<c:forEach var="def" items="${ssEntryDefinitionElementDataMap}">
  	<c:forEach var="element" items="${def.value}">
       <c:if test="${element.value.type == 'selectbox' || 
                     element.value.type == 'radio' || 
                     element.value.type == 'checkbox' || 
       				 element.value.type == 'date'  || 
       				 element.value.type == 'date_time'  || 
       				 element.value.type == 'event'  || 
       				 element.value.type == 'text'  || 
       				 element.value.type == 'number'  || 
       				 element.value.type == 'url'  || 
       				 element.value.type == 'hidden'  || 
       				 element.value.type == 'user_list' || 
       				 element.value.type == 'userListSelectbox'}">
		  <c:set var="colName2" value="${def.key},${element.value.type},${element.key},${element.value.caption}"/>
		  <jsp:useBean id="colName2" type="java.lang.String" scope="page"/>
		  <%
			if (!allColumns.contains(colName2)) allColumns.add(colName2);
		  %>
		</c:if>
  	</c:forEach>
</c:forEach>

<script type="text/javascript">
var ss_saveFolderColumnsUrl = "<ssf:url action="${action}" actionUrl="true"
		binderId="${ssBinder.id}"><ssf:param 
		name="operation" value="save_folder_columns"/></ssf:url>";
</script>

<div class="ss_popup teamingDlgBox" style="width: 400px;">
   	<div class="teamingDlgBoxHeader"><ssf:nlt tag="misc.configureColumns"/></div>
	<div class="ss_popup_body">
	<form method="post" onSubmit="ss_setActionUrl(this, ss_saveFolderColumnsUrl);return true;" 
	  id="saveColumnsForm" name="saveColumnsForm">
	<div class="gray3" style="font-size: 12px !important; width: 270px;"><ssf:nlt tag="folder.selectColumns"/></div>
	<table cellpadding="4" class="ss_table_rounded">
	 <tbody>
	  <tr class="ss_tab_table_columnhead">
	   <th colspan="2" style="text-align: center;"><ssf:nlt tag="folder.column.columns"/></th>
	   <th colspan="1" style="text-align: center;"><ssf:nlt tag="folder.column.custom"/></th>
	   <th colspan="2" style="text-align: left;"><ssf:nlt tag="folder.column.sort"/></th>	   
	  </tr>
 
<c:forEach var="colName" items="${allColumns}">
  <jsp:useBean id="colName" type="java.lang.String" scope="page"/>
  <c:set var="thisItemChecked" value=""/>
  <c:if test="${!empty ssFolderColumns[colName] || (empty ssFolderColumns && 
  		(colName == 'title' || colName == 'comments' || colName == 'size' || 
  		colName == 'download' || colName == 'html' || colName == 'state' || 
  		colName == 'author' || colName == 'date' || colName == 'rating'))}">
    <c:set var="thisItemChecked" value="checked"/>
  </c:if>

  <%
	if (!colName.contains(",")) {
  %>
	  <c:set var="colText" value="${ssFolderColumnTitles[colName]}" />
	  <c:set var="colTextDefault"><ssf:nlt tag="folder.column.${colName}"/></c:set>
	  <c:set var="colIdName">${colName}</c:set>
	  <c:set var="colCbName">${colName}</c:set>
  <%
    } else if (colName.contains(",")) {
		String[] temp = colName.split(",");
		if (temp.length == 4) {
			String defId = temp[0];
			String eleType = temp[1];
			String eleName = temp[2];
			String eleCaption = temp[3];
			%>
			  <c:forEach var="def" items="${ssEntryDefinitionElementDataMap}">
			  	<c:forEach var="element" items="${def.value}">
			  	    <c:set var="cn" value="${def.key},${element.value.type},${element.key},${element.value.caption}"/>
					<c:if test="${colName == cn}">
					  <c:set var="colIdName">${colName}</c:set>
					  <c:set var="colCbName">customCol_${colName}</c:set>
			          <c:set var="colText" value="${ssFolderColumnTitles[colName]}" />
			          <c:set var="colTextDefault"><ssf:nlt tag="${ssEntryDefinitionMap[def.key].title}" checkIfTag="true"
			          /> / <ssf:nlt tag="<%= eleCaption %>" checkIfTag="true"/></c:set>
			        </c:if>
			  	</c:forEach>
			  </c:forEach>
			<%
		}
	}
  %>
  <c:if test="${!empty ssFolderColumnTitles[colIdName]}">
    <c:set var="colText">${ssFolderColumnTitles[colIdName]}</c:set>
  </c:if>

  <tr id="${colIdName}">
    <td><input type="checkbox" name="${colCbName}" ${thisItemChecked}></td>
    <td>${colTextDefault}</td>
    <td>
      <input type="text" name="ss_col_text_${colName}" style="width:150px;" value="${colText}" />
    </td>
	<c:set var="ss_folderColumnId" value="${colIdName}"/>
	<%@ include file="/WEB-INF/jsp/forum/configure_folder_columns_sort.jsp" %>
  </tr>
</c:forEach>   
</table>
    
<c:if test="${ss_accessControlMap[ssBinder.id]['modifyBinder'] and ssConfigJspStyle != 'template'}">

</c:if>

	<div>
		<input type="checkbox" name="setFolderDefaultColumns"/>
		<span class="ss_labelAfter">
			<label for="setFolderDefaultColumns"><ssf:nlt tag="misc.configureColumns.folderDefault"/></label>
		</span>
	</div>
	<table class="margintop3">
		<tr>
			<td>
				<input type="submit" name="defaultBtn" value="<ssf:nlt tag="button.restoreDefaults"/>">
			</td>
			<td width="100%" align="right">	
				<input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
				<input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="ss_cancelPopupDiv('ss_folder_column_menu');return false;">
			</td>
		</tr>
	</table>

	<input type="hidden" name="columns__order"/>
		<sec:csrfInput />
</form>
</div>
</div>
</c:otherwise>
</c:choose>
