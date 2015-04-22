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
<% // Do we have an email report? %>
<c:if test="${!empty emailReport}">
	<% // Yes %>
	<jsp:useBean id="emailReport" type="java.util.List" scope="request" />

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
	for (i = 0; i < emailReport.size(); ++i)
	{
		Map		nextItem;
		String	separator;
		
		nextItem = (Map) emailReport.get( i );
		String sendDate = (String)nextItem.get( ReportModule.EMAIL_LOG_SEND_DATE );
		String from = (String)nextItem.get( ReportModule.EMAIL_LOG_FROM_ADDRESS );
		if (from == null) from = "";
		String subject = (String)nextItem.get( ReportModule.EMAIL_LOG_SUBJECT );
		if (subject == null) subject = "";
		String comment = (String)nextItem.get( ReportModule.EMAIL_LOG_COMMENT );
		if (comment == null) comment = "";
		String logType = (String)nextItem.get( ReportModule.EMAIL_LOG_TYPE );
		String logStatus = (String)nextItem.get( ReportModule.EMAIL_LOG_STATUS );
		String toAddresses = (String)nextItem.get( ReportModule.EMAIL_LOG_TO_ADDRESSES );
		if (toAddresses == null) toAddresses = "";
		String attachedFiles = (String)nextItem.get( ReportModule.EMAIL_LOG_ATTACHED_FILES );
		if (attachedFiles == null) attachedFiles = "";

		// If this is not the first entity, add a ',' before we add another entity.
		if ( i != 0 )
			separator = ",";
		else
			separator = "";
%>
		<%= separator %>{ sendDate : '<%= sendDate %>', from : '<%= from %>', subject: '<ssf:escapeJavaScript value="<%= subject %>" />', comment: '<ssf:escapeJavaScript value="<%= comment %>" />', logType: '<%= logType %>', logStatus: '<%= logStatus %>', toAddresses: '<ssf:escapeJavaScript value="<%= toAddresses %>" />', attachedFiles: '<ssf:escapeJavaScript value="<%= attachedFiles %>" />' }
<%
	}// end for()
%>
	]
}
</c:if>

<% // Do we have an email report? %>
<c:if test="${empty emailReport}">
	<% // No %>
{
	status : 1,
	errDesc : null,
	reportData : []
}
</c:if>
