<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
String wsTreeName = "search_" + renderResponse.getNamespace();
%>
<c:set var="formName"><portlet:namespace />fm</c:set>
<script type="text/javascript">
var ssReportURL="<portlet:actionURL><portlet:param 
		name="action" value="activity_report"/><portlet:param 
	name="binderId" value="${ssBinder.id}"/><portlet:param 
	name="binderType" value="${ssBinder.entityType}"/></portlet:actionURL>";
</script>

<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<ssf:url webPath="reportDownload"/>" 
	method="post" 
	name="${formName}">
  <input type="hidden" name="ss_reportType" id="ss_reportType" value="binder"/>
  <div class="ss_buttonBarRight">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="document.<portlet:namespace/>fm.action=ssReportURL; document.<portlet:namespace/>fm.submit()">
  </div>
  <br/>
  <ssf:nlt tag="administration.report.dates"/>
  <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
				 immediateMode="false" altText="<ssf:nlt tag='calendar.view.popupAltText'/>"
				 />
  </div>
  <div id="ss_startPopup" class="ss_calPopupDiv"></div>
  <ssf:nlt tag="smallWords.and"/>
  <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
				 immediateMode="false" altText="<ssf:nlt tag='calendar.view.popupAltText'/>"
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
   <input type="radio" class="ss_radio" name="reportFlavor" value="workflow" onclick="showOptions(this);return true;"/><label class="ss_radio_label"><ssf:nlt tag="administration.report.label.workflow"/></label>
   <div id="ss_report_workflowFlavorOptions" style="margin-left:20px;">
     <input type="radio" class="ss_radio" name="ss_reportFlavor" value="averages" checked="checked"/><label class="ss_radio_label"><ssf:nlt tag="administration.report.label.averages"/></label><br/>
     <input type="radio" class="ss_radio" name="ss_reportFlavor" value="current"/><label class="ss_radio_label"><ssf:nlt tag="administration.report.label.current"/></label><br/>   <br/>
   </div>
   <br/>
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.forum"/></span>
   <br/>
   <br/>
   <ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
     rootOpen="true" topId="${ssWsDomTreeBinderId}" 
     multiSelect="<%= new ArrayList() %>" multiSelectPrefix="$type_" />

   <br/>
   <br/>
  </div>
  <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="document.<portlet:namespace/>fm.action=ssReportURL; document.<portlet:namespace/>fm.submit()">
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
	var prefix = action+"_";
	ss_createTreeCheckbox("<%= wsTreeName %>", prefix, forum);
	var name = prefix + forum;
	if (self.document.${formName}[name] && self.document.${formName}[name].checked) {
		self.document.${formName}[name].checked=false;
		if (self.ss_treeIframeDiv && self.ss_treeIframeDiv.document) {
			var cbObj = self.ss_treeIframeDiv.document.getElementById("ss_tree_checkbox" + "<%= wsTreeName %>" + name)
			cbObj.checked = false;
		}
	} else {
		self.document.${formName}[name].checked=true
		if (self.ss_treeIframeDiv && self.ss_treeIframeDiv.document) {
			var cbObj = self.ss_treeIframeDiv.document.getElementById("ss_tree_checkbox" + "<%= wsTreeName %>" + name)
			cbObj.checked = true;
		}
	}
	return false
}
</script>
