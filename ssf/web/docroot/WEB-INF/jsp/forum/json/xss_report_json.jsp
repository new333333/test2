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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.lang.String" %>
<%@ page import="org.kablink.teaming.module.report.ReportModule" %>

<% // This is a JSON AJAX response %>
<% // Do we have an xss report? %>
<c:if test="${!empty xssReport}">
	<% // Yes %>
	<jsp:useBean id="xssReport" type="java.util.List" scope="request" />

<%
	int		i;
	String	name;
	String	seperator;

%>
{
	status : 1,
	errDesc : null,

	reportData : [
<%
	for (i = 0; i < xssReport.size(); ++i)
	{
		Map		nextItem;
		String	separator;
		Long	entityId;
		String	entityIdStr;
		String	entityType;
		String	entityPath;
		Long	entityPathId;
		String	entityPathIdStr;
		String	entityTitle;
		Long	entityCreatorId;
		String	entityCreatorTitle;
		
		nextItem = (Map) xssReport.get( i );
		entityId = (Long)nextItem.get( ReportModule.ENTITY_ID );
		entityIdStr = entityId.toString();
		entityType = (String)nextItem.get( ReportModule.ENTITY_TYPE );
		entityPath = (String)nextItem.get( ReportModule.ENTITY_PATH );
		entityPathId = (Long)nextItem.get( ReportModule.ENTITY_PATH_ID );
		entityPathIdStr = entityPathId.toString();
		entityTitle = (String)nextItem.get( ReportModule.ENTITY_TITLE );
		entityCreatorId = (Long)nextItem.get( ReportModule.ENTITY_CREATOR_ID );
		entityCreatorTitle = "";
		java.util.Set ids = new java.util.HashSet();
		ids.add(entityCreatorId);
		java.util.List users = org.kablink.teaming.util.ResolveIds.getPrincipals(ids, false);
		if (!users.isEmpty()) {
			java.util.Iterator itUsers = users.iterator();
			while (itUsers.hasNext()) {
				org.kablink.teaming.domain.User u = (org.kablink.teaming.domain.User) itUsers.next();
				if (u != null) entityCreatorTitle = u.getTitle();
				break;
			}
		}

		// If this is not the first entity, add a ',' before we add another entity.
		if ( i != 0 )
			separator = ",";
		else
			separator = "";
%>
		<%= separator %>{ entityType : '<%= entityType %>', id : <%= entityIdStr %>, path: '<ssf:escapeJavaScript value="<%= entityPath %>" />', title: '<ssf:escapeJavaScript value="<%= entityTitle %>" />', pathId: <%= entityPathIdStr %>, creatorName: '<ssf:escapeJavaScript value="<%= entityCreatorTitle %>" />' }
<%
	}// end for()
%>
	]
}
</c:if>

<% // Do we have an xss report? %>
<c:if test="${empty xssReport}">
	<% // No %>
{
	status : 1,
	errDesc : null,
	reportData : []
}
</c:if>
