<%
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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.kablink.teaming.domain.AnyOwner" %>
<%@ page import="org.kablink.teaming.domain.DefinableEntity" %>
<%@ page import="org.kablink.teaming.domain.FileAttachment" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.domain.Workspace" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.web.util.MiscUtil" %>
<%@ page import="org.kablink.teaming.web.util.PermaLinkUtil" %>

<jsp:useBean id="relatedAttachments"   type="java.util.Set"    scope="request" />
<jsp:useBean id="relatedUsers"         type="java.util.Set"    scope="request" />
<jsp:useBean id="relatedWorkspaces"    type="java.util.Set"    scope="request" />
<jsp:useBean id="relationshipErrorKey" type="java.lang.String" scope="request" />

<%
	int count;
%>

<c:if test="${!empty relatedAttachments && !empty relatedUsers && !empty relatedWorkspaces}">

{
	status : 1,
	errDesc : null,

	relatedFiles : [
		<%
			count = 0;
			for (Iterator raIT = relatedAttachments.iterator(); raIT.hasNext();) {
				count += 1;
				FileAttachment fa = ((FileAttachment) raIT.next());
				AnyOwner faOwner = fa.getOwner();
				DefinableEntity faDE = faOwner.getEntity();
				if (1 < count) {
					%>,<%
				}
				%>[
					"<%= StringEscapeUtils.escapeJavaScript(fa.getFileItem().getName()) %>",
					"<%= StringEscapeUtils.escapeJavaScript(PermaLinkUtil.getFilePermalink(fa)) %>",
					
					"<%= StringEscapeUtils.escapeJavaScript(faDE.getTitle()) %>",
					"<%= StringEscapeUtils.escapeJavaScript(PermaLinkUtil.getPermalink(faDE)) %>"
				]<%
			}
		%>
	],
	
	
	relatedUsers : [
		<%
			count = 0;
			for (Iterator usersIT = relatedUsers.iterator(); usersIT.hasNext();) {
				count += 1;
				User user = ((User) usersIT.next());
				if (1 < count) {
					%>,<%
				}
				%>[
					"<%= StringEscapeUtils.escapeJavaScript(user.getTitle()) %>",
					"<%= StringEscapeUtils.escapeJavaScript(PermaLinkUtil.getPermalink(user)) %>"
				]<%
			}
		%>
	],
	
	
	relatedWorkspaces : [
		<%
			count = 0;
			for (Iterator wsIT = relatedWorkspaces.iterator(); wsIT.hasNext();) {
				count += 1;
				Workspace ws = ((Workspace) wsIT.next());
				if (1 < count) {
					%>,<%
				}
				%>[
					"<%= StringEscapeUtils.escapeJavaScript(ws.getTitle()) %>",
					"<%= StringEscapeUtils.escapeJavaScript(PermaLinkUtil.getPermalink(ws)) %>"
				]<%
			}
		%>
	]
}
</c:if>

<c:if test="${empty relatedAttachments || empty relatedUsers || empty relatedWorkspaces}">
{
	<%
		if (!(MiscUtil.hasString(relationshipErrorKey))) {
			relationshipErrorKey = "entry.relatedDataMissing";
		}
	%>
	status : 0,
	errDesc : "<%= StringEscapeUtils.escapeJavaScript(NLT.get(relationshipErrorKey)) %>",

	relatedFiles : [],
	relatedUsers : [],
	relatedWorkspaces : []
}
</c:if>
