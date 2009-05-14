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
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.kablink.teaming.util.NLT" %>

<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<c:set var="ss_windowTitle" value='<%= NLT.get( "administration.report.title.guest_user_access" ) %>' scope="request"/>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<script type="text/javascript">
var ssReportURL="<ssf:url action="guest_user_access_report" actionUrl="true"><ssf:param name="binderId" value="${ssBinder.id}"/><ssf:param name="binderType" value="${ssBinder.entityType}"/></ssf:url>";
function ss_selectUser${renderResponse.namespace}(id, obj) {
	var formObj = document.getElementById("reportForm");
	formObj.users.value = id;
}
</script>

<body class="ss_style_body tundra">
	<div class="ss_pseudoPortal">
		<div class="ss_style ss_portlet">
			<c:set var="formName">${renderResponse.namespace}fm</c:set>

			<ssf:form titleTag="administration.report.title.guest_user_access">





<form class="ss_style ss_form" 
	action="<ssf:url webPath="reportDownload"/>" 
	method="post" 
	name="reportForm"
	id="reportForm">
  <br/>
  <br/>
  <ssf:find formName="${formName}" formElement="user" 
	type="user" 
	singleItem="true"
	width="60"
	clickRoutine="ss_selectUser${renderResponse.namespace}" />
  <br/>
  <br/>
  
  <input type="hidden" name="users" id="users" />
  <input type="hidden" name="ss_reportType" id="ss_reportType" value="accessByGuest"/>
  <input type="submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" 
      value="<ssf:nlt tag="button.close" text="Close"/>" 
      onclick="self.window.close(); return false;">
</form>








				<table cellspacing="0" cellpadding="3">
					<tr>
						<td>
							<div style="margin-top: .7em;">
								<span><ssf:nlt tag="administration.report.guestAccess.header1" /></span>
							</div>
							<div style="margin-bottom: .6em;">
								<span><ssf:nlt tag="administration.report.guestAccess.header2" /></span>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<table width="100%" class="ss_style" cellspacing="0" cellpadding="3" style="border: 1px solid black;">
								<tr style="font-family: arial, sans-serif; background-color: #EDEEEC; border-bottom: 1px solid black; color: black; font-size: .75em; font-weight: bold;">
									<td align="left">
										<span>&nbsp;<ssf:nlt tag="administration.report.guestAccess.col1" /><span>
									</td>
								</tr>
								<tr>
									<td>
										<a onclick="return ss_openUrlInPortlet( this.href, true, '', '');"
										   href="http://localhost:8080/ssf/a/do?p_name=ss_forum&p_action=0&workAreaId=40&action=configure_access_control&workAreaType=workspace">
											<span>Some Workspace</span>
										</a>
									</td>
								</tr>
								<tr>
									<td>
										<a onclick="return ss_openUrlInPortlet( this.href, true, '', '');"
										   href="http://localhost:8080/ssf/a/do?p_name=ss_forum&p_action=0&workAreaId=40&action=configure_access_control&workAreaType=workspace">
											<span>Marketing Workspace</span>
										</a>
									</td>
								</tr>
								<tr>
									<td>
										<a onclick="return ss_openUrlInPortlet( this.href, true, '', '');"
										   href="http://localhost:8080/ssf/a/do?p_name=ss_forum&p_action=0&workAreaId=40&action=configure_access_control&workAreaType=workspace">
											<span>QA Workspace</span>
										</a>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</ssf:form>
		</div>
	</div>
</body>
</html>
