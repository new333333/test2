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
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="document.<portlet:namespace/>fm.action=ssReportURL; document.<portlet:namespace/>fm.submit()">
</div>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.report.select"/></span>
<br>
<br>

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

</script>
<div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_startPopup" id="ss_startDate"
				 immediateMode="false" altText="<ssf:nlt tag='calendar.view.popupAltText'/>"
				 /></div>
				 <div id="ss_startPopup" class="ss_calPopupDiv"></div>
<div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_endPopup" id="ss_endDate"
				 immediateMode="false" altText="<ssf:nlt tag='calendar.view.popupAltText'/>"
				 /></div>
				 <div id="ss_endPopup" class="ss_calPopupDiv"></div>
				 
<ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
  rootOpen="true" topId="${ssWsDomTreeBinderId}" 
  multiSelect="<%= new ArrayList() %>" multiSelectPrefix="$type_" />

<br>
<br>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="document.<portlet:namespace/>fm.action=ssReportURL; document.<portlet:namespace/>fm.submit()">
</div>
</form>
<br>
</td></tr></table>

