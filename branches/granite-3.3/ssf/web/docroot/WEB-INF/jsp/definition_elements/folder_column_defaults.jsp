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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
  <%
	Map ssFolderColumns = (Map) ssUserFolderProperties.get("userFolderColumns");
	Map ssFolderColumnTitles = (Map) ssUserFolderProperties.get("userFolderColumnTitles");
	String ssFolderColumnSort = (String) ssUserFolderProperties.get("userFolderColumnSortOrder");
  %>
<c:choose>
<c:when test="${ss_folderViewColumnsType == 'file'}">
  <%
	if (ssFolderColumns == null) {
		ssFolderColumns = (Map)ssBinder.getProperty("folderColumns");
		ssFolderColumnTitles = (Map)ssBinder.getProperty("folderColumnTitles");
		ssFolderColumnSort = (String)ssBinder.getProperty("folderColumnSortOrder");
	}
	if (ssFolderColumns == null) {
		ssFolderColumns = new java.util.HashMap();
		ssFolderColumns.put("title", "title");
		ssFolderColumns.put("comments", "comments");
		ssFolderColumns.put("size", "size");
		ssFolderColumns.put("download", "download");
		ssFolderColumns.put("html", "html");
		ssFolderColumns.put("state", "state");
		ssFolderColumns.put("author", "author");
		ssFolderColumns.put("date", "date");
	}
	if (ssFolderColumnTitles == null) ssFolderColumnTitles = new java.util.HashMap();
	if (ssFolderColumnSort == null) ssFolderColumnSort = "title|comments|size|download|html|state|author|date";
  %>
</c:when>

<c:when test="${ss_folderViewColumnsType == 'folder'}">
  <%
	if (ssFolderColumns == null) {
		ssFolderColumns = (Map)ssBinder.getProperty("folderColumns");
		ssFolderColumnTitles = (Map)ssBinder.getProperty("folderColumnTitles");
		ssFolderColumnSort = (String)ssBinder.getProperty("folderColumnSortOrder");
	}
	if (ssFolderColumns == null) {
		ssFolderColumns = new java.util.HashMap();
		ssFolderColumns.put("number", "number");
		ssFolderColumns.put("title", "title");
		ssFolderColumns.put("comments", "comments");
		ssFolderColumns.put("state", "state");
		ssFolderColumns.put("author", "author");
		ssFolderColumns.put("date", "date");
		ssFolderColumns.put("rating", "rating");
	}
	if (ssFolderColumnTitles == null) ssFolderColumnTitles = new java.util.HashMap();
	if (ssFolderColumnSort == null) ssFolderColumnSort = "number|title|comments|state|author|date|rating";
  %>
</c:when>

<c:otherwise>
  <%
	if (ssFolderColumns == null) {
		ssFolderColumns = (Map)ssBinder.getProperty("folderColumns");
		ssFolderColumnTitles = (Map)ssBinder.getProperty("folderColumnTitles");
		ssFolderColumnSort = (String)ssBinder.getProperty("folderColumnSortOrder");
	}
	if (ssFolderColumns == null) {
		ssFolderColumns = new java.util.HashMap();
	}
	if (ssFolderColumnTitles == null) ssFolderColumnTitles = new java.util.HashMap();
	if (ssFolderColumnSort == null) ssFolderColumnSort = "";
  %>
</c:otherwise>

</c:choose>

<c:set var="ssFolderColumns" value="<%= ssFolderColumns %>" scope="request"/>
<c:set var="ssFolderColumnTitles" value="<%= ssFolderColumnTitles %>" scope="request"/>
<%
	List ssFolderColumnSortOrder = new ArrayList();
	String[] sortOrder = ssFolderColumnSort.split("\\|");
	for (int i=0; i < sortOrder.length; i++) {
		if (!sortOrder[i].equals("")) ssFolderColumnSortOrder.add(sortOrder[i]);
	}
%>
  <c:forEach var="column" items="${ssFolderColumns}">
    <c:set var="columnName" value="${column.key}"/>
	<jsp:useBean id="columnName" type="java.lang.String" scope="page"/>
    <c:choose>
      <c:when test="${!empty column.value}">
	  <%
		//Handle settings from older releases that might not be in the sort list yet
		if (!ssFolderColumnSortOrder.contains(columnName)) ssFolderColumnSortOrder.add(columnName);
	  %>
      </c:when>
      <c:when test='<%= columnName.contains(",") %>'>
	  <%
		//Handle settings from older releases that might not be in the sort list yet
		if (!ssFolderColumnSortOrder.contains(columnName)) ssFolderColumnSortOrder.add(columnName);
	  %>
      </c:when>
    </c:choose>
  </c:forEach>

<c:set var="ssFolderColumnsSortOrder" value="<%= ssFolderColumnSortOrder %>" scope="request"/>
