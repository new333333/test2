<%
/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>

<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.report.title.disk_quota_exceeded") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<script type="text/javascript">
	/**
	 * 
	 */
	function handleCloseBtn()
	{
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Tell the Teaming GWT ui to close the administration content panel.
			if ( window.parent.ss_closeAdministrationContentPanel ) {
				window.parent.ss_closeAdministrationContentPanel();
			} else {
				ss_cancelButtonCloseWindow();
			}

			return false;
	<% 	}
		else { %>
			ss_cancelButtonCloseWindow();
			return false;
	<%	} %>
	
	}// end handleCloseBtn()
</script>

<c:if test="${GwtReport == 'true'}">
	<br />
</c:if>
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">
<script type="text/javascript">
function ss_numbers_only(evt)
  {
     var charCode = (evt.which) ? evt.which : event.keyCode
     if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;

     return true;
  }
</script>
<c:set var="formName">${renderResponse.namespace}fm</c:set>
<script type="text/javascript">
var ssReportURL="<ssf:url action="quota_report" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="binderType" value="${ssBinder.entityType}"/></ssf:url>";
</script>

<c:if test="${ss_quotasEnabled}">
<ssf:form titleTag="administration.report.title.disk_quota_exceeded" ignore="${GwtReport}">
	<table class="ss_style" width="100%"><tr><td>
		<form class="ss_style ss_form" 
			action="<ssf:url webPath="reportDownload"/>" 
			method="post" 
			name="${formName}">

			<input type="hidden" name="ss_reportType" value="quota_exceeded"/>
			<c:if test="${GwtReport != 'true'}">
				<div class="ss_buttonBarRight">
					<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
						  onClick="return handleCloseBtn();"/>
				</div>
			</c:if>
			
			<div class="marginbottom2 ss_bold"><ssf:nlt tag="administration.report.disk_quota_exceeded"/></div>
		
			<div class="roundcornerSM" style="border: 1px solid #cccccc; padding: 10px; background-color: #ededed;">
				<div class="margintop3 marginbottom1" style="margin-left: 5px;">
					<input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="administration.create.report" />">
				</div>
			</div>
			<sec:csrfInput />
		</form>

	</td></tr></table>
</ssf:form>
</c:if>

<c:if test="${!ss_quotasEnabled}">
  <ssf:form titleTag="administration.quotas.notEnabled" ignore="${GwtReport}">
	<c:if test="${GwtReport == 'true'}">
		<div class="marginbottom2 ss_bold"><ssf:nlt tag="administration.quotas.notEnabled"/></div>
	</c:if>
	<c:if test="${GwtReport != 'true'}">
	    <br/>
	    <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
			  onClick="return handleCloseBtn();"/>
	</c:if>
  </ssf:form>
</c:if>

</div>
</div>
</body>
</html>