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
<%@ page import="java.util.ArrayList" %>

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.report.title.activityByUser") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<c:set var="formName">${renderResponse.namespace}fm</c:set>

<ssf:form title='<%= NLT.get("administration.report.title.activityByUser") %>'>
<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<ssf:url webPath="reportDownload"/>" 
	method="post" 
	name="${formName}">
  <input type="hidden" name="ss_reportType" id="ss_reportType" value="activityByUser"/>
  <div class="ss_buttonBarRight">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="self.window.close(); return false;">
  </div>
  <br/>
  <ssf:nlt tag="administration.report.dates"/>
  <div class="ss_toolbar_color" style="display:inline;">
    <ssf:datepicker formName="${formName}" showSelectors="true" 
	 popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
	 immediateMode="false" 
	 altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
	 />
  </div>
  <div id="ss_startPopup" class="ss_calPopupDiv"></div>
  <ssf:nlt tag="smallWords.and"/>
  <div class="ss_toolbar_color" style="display:inline;">
    <ssf:datepicker formName="${formName}" showSelectors="true" 
	 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
	 immediateMode="false" 
	 altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
	 />
  </div>
  <div id="ss_endPopup" class="ss_calPopupDiv"></div>
  <br/>				 
  <div id="ss_report_panel_forum">
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.label.selectUsers"/></span>
   <br/>
  <ssf:find formName="${formName}" formElement="users" 
    type="user" />
   <br/>
   <input type="radio" class="ss_radio" name="ss_reportFlavor" value="summary" checked="checked" />
   <label class="ss_radio_label"><ssf:nlt tag="administration.report.label.activitySummary"/></label>
   <br/>
   <input type="radio" class="ss_radio" name="ss_reportFlavor" value="activity" />
   <label class="ss_radio_label"><ssf:nlt tag="administration.report.label.activity"/></label>
   <br/>
  </div>
   <br/>
  <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="self.window.close(); return false;">
  </div>
</form>
<br>
</td></tr></table>
</ssf:form>

</body>
</html>
