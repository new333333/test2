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
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
String wsTreeName = "search_" + renderResponse.getNamespace();
%>
<c:set var="formName"><ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm</c:set>


<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<ssf:url webPath="reportDownload"/>" 
	method="post" 
	name="${formName}">
  <input type="hidden" name="ss_reportType" id="ss_reportType" value="binder"/>
  <div class="ss_buttonBarRight">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="self.window.close(); return false;">
  </div>
  <br/>
  <ssf:nlt tag="administration.report.dates"/>
  <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
				 immediateMode="false" 
				 altText="<%= com.sitescape.team.util.NLT.get("calendar.view.popupAltText") %>"
				 />
  </div>
  <div id="ss_startPopup" class="ss_calPopupDiv"></div>
  <ssf:nlt tag="smallWords.and"/>
  <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
				 immediateMode="false" 
				 altText="<%= com.sitescape.team.util.NLT.get("calendar.view.popupAltText") %>"
				 />
  </div>
  <div id="ss_endPopup" class="ss_calPopupDiv"></div>
  <br/>				 
  <div id="ss_report_panel_forum">
   <br/>
   <input type="radio" class="ss_radio" name="reportFlavor" value="activity" checked="checked" onclick="showOptions(this);return true;"/><label class="ss_radio_label"><ssf:nlt tag="administration.report.label.activity"/></label>
   <div id="ss_report_activityFlavorOptions" style="margin-left:20px;">
   <input type="checkbox" class="ss_check" name="ss_byUser" id="ss_byUser"/><label class="ss_checkbox_label"><ssf:nlt tag="administration.report.label.breakDown"/></label>
   </div>
<ssf:ifAuthorizedByLicense featureName="com.sitescape.team.module.workflow.Workflow">
   <input type="radio" class="ss_radio" name="reportFlavor" value="workflow" onclick="showOptions(this);return true;"/><label class="ss_radio_label"><ssf:nlt tag="administration.report.label.workflow"/></label>
   <div id="ss_report_workflowFlavorOptions" style="margin-left:20px;">
     <input type="radio" class="ss_radio" name="ss_reportFlavor" value="averages" checked="checked"/><label class="ss_radio_label"><ssf:nlt tag="administration.report.label.averages"/></label><br/>
     <input type="radio" class="ss_radio" name="ss_reportFlavor" value="current"/><label class="ss_radio_label"><ssf:nlt tag="administration.report.label.current"/></label><br/>   <br/>
   </div>
</ssf:ifAuthorizedByLicense>
   <br/>
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.forum"/></span>
   <br/>
   <br/>
   <ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
     rootOpen="true" topId="${ssWsDomTreeBinderId}" 
     multiSelect="<%= new ArrayList() %>" multiSelectPrefix="$type" />

   <br/>
   <br/>
  </div>
  <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="self.window.close(); return false;">
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
  	dojo.lfx.html.fadeIn(aOpt, 100).play();
  	dojo.lfx.html.fade(wOpt,  {end:0.3}, 1000).play();
  	document.getElementById('ss_reportType').value='binder';
  } else {
  	dojo.lfx.html.fade(aOpt,  {end:0.3}, 1000).play();
  	dojo.lfx.html.fadeIn(wOpt, 100).play();
  	document.getElementById('ss_reportType').value='workflow';
  }
}
function <%= wsTreeName %>_showId(forum, obj, action) {
	if (obj.ownerDocument) {
		var cDocument = obj.ownerDocument;
	} else if (obj.document) {
		cDocument = obj.document;
	}
	if (cDocument) {
		var r = cDocument.getElementById("ss_tree_checkbox<%= wsTreeName %>" + action + forum);
		if (r) {
			if (r.checked !== undefined) {
				r.checked = !r.checked;
			}
			if (r.onclick !== undefined) {
				r.onclick();
			}
		}
	}
	return false;
}
</script>
