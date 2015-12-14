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
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.Binder" scope="request" />
<%
java.util.List binderIds = new ArrayList();
binderIds.add(ssBinder.getId());
%>

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.report.title.activityByFolder") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body tundra">

<%
String wsTreeName = "search_" + renderResponse.getNamespace();
%>
<c:set var="formName">${renderResponse.namespace}fm</c:set>

<ssf:form title='<%= NLT.get("administration.report.title.activityByFolder") %>'>
<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<ssf:url webPath="reportDownload"/>" 
	method="post" 
	name="${formName}">
  <input type="hidden" name="ss_reportType" id="ss_reportType" value="binder"/>
  <div class="ss_buttonBarRight">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" 
      onclick="ss_cancelButtonCloseWindow(); return false;">
  </div>
  <br/>
  <ssf:nlt tag="administration.report.dates"/>
  <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
				 immediateMode="false" 
				 altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
				 />
  </div>
  <div id="ss_startPopup" class="ss_calPopupDiv"></div>
  <ssf:nlt tag="smallWords.and"/>
  <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
				 immediateMode="false" 
				 altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
				 />
  </div>
  <div id="ss_endPopup" class="ss_calPopupDiv"></div>
  <br/>				 
  <div id="ss_report_panel_forum">
   <br/>
   <input type="radio" class="ss_radio" name="reportFlavor" value="activity" id="activity" checked="checked" 
     onclick="showOptions(this);return true;"/>
     <label class="ss_radio_label" for="activity"><ssf:nlt tag="administration.report.label.activity"/></label>
   <div style="margin-left:30px;">
     <ssf:nlt tag="general.users"/>
   </div>
   <div id="ss_report_activityFlavorOptions" style="margin-left:40px;">
   <input type="radio" class="ss_radio" name="ss_byUser" id="ss_byTeamMembers" value="ss_byTeamMembers" />
   <label class="ss_checkbox_label" for="ss_byTeamMembers"><ssf:nlt tag="team.members"/></label><br/>
   <input type="radio" class="ss_radio" name="ss_byUser" id="ss_byAllUsers" value="ss_byAllUsers" checked="checked"/>
   <label class="ss_checkbox_label" for="ss_byAllUsers"><ssf:nlt tag="administration.initial.group.alluser.title"/></label><br/>
   <input type="radio" class="ss_radio" name="ss_byUser" id="ss_none" value="ss_none"/>
   <label class="ss_checkbox_label" for="ss_none"><ssf:nlt tag="general.summary"/></label><br/>
   </div>
   <input type="radio" class="ss_radio" name="reportFlavor" value="workflow" id="workflow" onclick="showOptions(this);return true;"/>
     <label class="ss_radio_label" for="workflow"><ssf:nlt tag="administration.report.label.workflow"/></label>
   <div id="ss_report_workflowFlavorOptions" style="margin-left:20px;">
     <input type="radio" class="ss_radio" name="ss_reportFlavor" value="averages" id="averages" checked="checked"/>
       <label class="ss_radio_label" for="averages"><ssf:nlt tag="administration.report.label.averages"/></label><br/>
     <input type="radio" class="ss_radio" name="ss_reportFlavor" value="current" id="current"/>
       <label class="ss_radio_label" for="current"><ssf:nlt tag="administration.report.label.current"/></label><br/>
       <br/>
   </div>
   <br/>
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.forum"/></span>
   <br/>
   <br/>
   <ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
     rootOpen="true" topId="${ssWsDomTreeBinderId}" 
     multiSelect="<%= binderIds %>" multiSelectPrefix="id" />

   <br/>
   <br/>
  </div>
  <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" 
      onclick="ss_cancelButtonCloseWindow(); return false;">
  </div>
</form>
<br>
</td></tr></table>

<script type="text/javascript">

dojo.addOnLoad(function () {
  var rb = document.getElementsByName('reportFlavor');
  for(var i = 0; i < rb.length; i++) {
    if(rb[i].checked) {showOptions(rb[i]);}
  } 
});

function showOptions(sel)
{
  var aOpt = document.getElementById('ss_report_activityFlavorOptions');
  var wOpt = document.getElementById('ss_report_workflowFlavorOptions');
  if(sel.value == "activity") {
  	dojo.fadeIn({node:aOpt, delay:100}).play();
  	dojo.fadeOut({node:wOpt, end:0.3, delay:1000}).play();
  	document.getElementById('ss_reportType').value='binder';
  } else {
  	dojo.fadeOut({node:aOpt, end:0.3, delay:1000}).play();
  	dojo.fadeIn({node:wOpt, delay:100}).play();
  	document.getElementById('ss_reportType').value='workflow';
  }
}
function <%= wsTreeName %>_showId(id, obj, action) {
	return ss_checkTree(obj, "ss_tree_checkbox<%= wsTreeName %>id" + id);	
}
</script>

</ssf:form>

</body>
</html>
