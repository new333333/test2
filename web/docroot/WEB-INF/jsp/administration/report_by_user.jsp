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
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.report.title.activityByUser") %>' scope="request"/>
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

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<c:set var="formName">${renderResponse.namespace}fm</c:set>

<ssf:form title='<%= NLT.get("administration.report.title.activityByUser") %>'>

<div class="ss_style">
	<div class="ss_buttonBarRight">
		<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="return handleCloseBtn();">
	</div>
	<div class="ss_largeprint ss_bold"><ssf:nlt tag="administration.report.activity"/></div>
	<div class="ss_largeprint ss_bold margintop3"><ssf:nlt tag="administration.report.dates"/></div>
	<div class="roundcornerSM margintop1" style="border: 1px solid #cccccc; padding: 5px; background-color: #ededed;">

	<form class="ss_form" 
		action="<ssf:url webPath="reportDownload"/>" 
		method="post" 
		name="${formName}">
	  <input type="hidden" name="ss_reportType" id="ss_reportType" value="activityByUser"/>

		<div class="n_date_picker" style="display:inline; vertical-align: middle; padding-right: 10px;">
			<ssf:datepicker formName="${formName}" showSelectors="true" 
			 popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
			 immediateMode="false" 
			 altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
			 />
		</div>
		
		<div id="ss_startPopup" class="ss_calPopupDiv"></div>
		<ssf:nlt tag="smallWords.and"/>
		<div class="n_date_picker" style="display:inline; vertical-align: middle; padding-right: 10px;">
			<ssf:datepicker formName="${formName}" showSelectors="true" 
			 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
			 immediateMode="false" 
			 altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
			 />
		</div>
		<div id="ss_endPopup" class="ss_calPopupDiv"></div>
	  <div class="margintop3" id="ss_report_panel_forum">

		<div class="marginleft1">
			   <div><ssf:nlt tag="administration.report.label.selectUsers"/></div>

		<ssf:find formName="${formName}" formElement="users" 
		type="user" width="200px" />
		</div>
	   <div>
	   <input type="radio" class="ss_radio" name="ss_reportFlavor" value="summary" id="summary" checked="checked" />
	   <label class="ss_radio_label" for="summary"><ssf:nlt tag="administration.report.label.activitySummary"/></label>
	   </div>
	   <div>
	   <input type="radio" class="ss_radio" name="ss_reportFlavor" value="activity" id="activity" />
	   <label class="ss_radio_label" for="activity"><ssf:nlt tag="administration.report.label.activity"/></label>
	   </div>
	   
	  </div>
			<div class="margintop3 marginbottom1" style="margin-left: 5px;">
		   		<input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="administration.create.report" />">
			</div>
		<sec:csrfInput />
	</form>
	</div>
   <div class="ss_buttonBarRight" style="margin-top: 10px;">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" />"
		  onClick="return handleCloseBtn();"/>
   </div>
</div>
</ssf:form>

</body>
</html>
