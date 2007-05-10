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
		name="action" value="report"/></portlet:actionURL>";
</script>

<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<ssf:url webPath="reportDownload"/>" 
	method="post" 
	name="${formName}">

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="document.<portlet:namespace/>fm.action=ssReportURL; document.<portlet:namespace/>fm.submit()">
</div>
<br/>
   <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_startPopup" id="ss_startDate"
				 immediateMode="false" altText="<ssf:nlt tag='calendar.view.popupAltText'/>"
				 />
   </div>
   <div id="ss_startPopup" class="ss_calPopupDiv"></div>
   <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_endPopup" id="ss_endDate"
				 immediateMode="false" altText="<ssf:nlt tag='calendar.view.popupAltText'/>"
				 />
   </div>
   <div id="ss_endPopup" class="ss_calPopupDiv"></div>
<br/>				 
<div id="ss_report" class="ss_tab_container">
 <div id="ss_report_tabs" class="ss_tabbed">
  <ul>
    <li><a href="#" onclick="ss_report_selectTab(this, 'ss_report_panel_forum');" class='ss_current_tab' id="ss_report_initial_tab"><span><ssf:nlt tag="administration.report.tab.forum"/></span></a></li>
    <li><a href="#" onclick="ss_report_selectTab(this, 'ss_report_panel_user');"><span><ssf:nlt tag="administration.report.tab.user"/></span></a></li>
    <li><a href="#" onclick="ss_report_selectTab(this, 'ss_report_panel_workflow');"><span><ssf:nlt tag="administration.report.tab.workflow"/></span></a></li>
  </ul>
 </div>
 <div id="ss_report_panel" class="ss_tab_pane">
  <div id="ss_report_panel_forum">
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.forum"/></span>
   <br/>
   <br/>

   <ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
     rootOpen="true" topId="${ssWsDomTreeBinderId}" 
     multiSelect="<%= new ArrayList() %>" multiSelectPrefix="$type_" />

   <br/>
   <br/>
   <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="document.<portlet:namespace/>fm.action=ssReportURL; document.<portlet:namespace/>fm.submit()">
   </div>
  </div>
  <div id="ss_report_panel_user" style="visibility:hidden; display:none">
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.user"/></span>
   <br/>
   <br/>
   <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="userOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="document.<portlet:namespace/>fm.action=ssReportURL; document.<portlet:namespace/>fm.submit()">
   </div>
  </div>
  <div id="ss_report_panel_workflow" style="visibility:hidden; display:none">
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.workflow"/></span>
   <br/>
  </div>
 </div>
</div>
</form>
<br>
</td></tr></table>

<script type="text/javascript">

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

var ss_report_selectedPanel = 'ss_report_panel_forum';
var ss_report_selectedTab = document.getElementById('ss_report_initial_tab');

function ss_report_selectTab(tab, panelId)
{
	if(ss_report_selectedPanel != panelId) {
		var elt=document.getElementById(ss_report_selectedPanel);
		elt.style.visibility = 'hidden';
		elt.style.display = 'none';
		ss_removeClassFromElement(ss_report_selectedTab, 'ss_current_tab');
		
		elt = document.getElementById(panelId);
		elt.style.visibility = 'visible';
		elt.style.display = 'block';
		ss_addClassToElement(tab, 'ss_current_tab');
		
		ss_report_selectedPanel = panelId;
		ss_report_selectedTab = tab;
	}
}
</script>
