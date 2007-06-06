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
<table class="ss_style" width="100%"><tr><td>

<form class="ss_style ss_form" 
	action="<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>" 
	method="post" 
	name="<portlet:namespace />fm"
	id="<portlet:namespace />fm"
	onSubmit="return ss_submitIndexingForm();" >

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_startSpinner();">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.configure.index.select" text="Select the forums to be re-indexed:"/></span>
<br>
<br>

<script type="text/javascript">

function ss_submitIndexingForm() {
	var formObj = document.forms['<portlet:namespace />fm'];
	formObj.btnClicked.value = ss_buttonSelected;
	if (ss_buttonSelected == 'okBtn') {
		formObj.action = '<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>'
		ss_submitFormViaAjax('<portlet:namespace />fm', 'ss_indexingDone');
		return false;
	} else {
		formObj.action = '<portlet:actionURL><portlet:param name="action" value="configure_index"/></portlet:actionURL>'
		return true;
	}
}

function ss_indexingDone() {
	ss_buttonSelect('closeBtn');
	var formObj = document.forms['<portlet:namespace />fm'];
	formObj.btnClicked.value = 'closeBtn';
	formObj.action = '<portlet:actionURL><portlet:param name="action" value="configure_index"/></portlet:actionURL>'
	formObj.submit();
}

function <%= wsTreeName %>_showId(forum, obj, action) {
	var prefix = action+"_";
	ss_createTreeCheckbox("<%= wsTreeName %>", prefix, forum);
	var name = prefix + forum;
	if (self.document.<portlet:namespace />fm[name] && self.document.<portlet:namespace />fm[name].checked) {
		self.document.<portlet:namespace />fm[name].checked=false;
		if (self.ss_treeIframeDiv && self.ss_treeIframeDiv.document) {
			var cbObj = self.ss_treeIframeDiv.document.getElementById("ss_tree_checkbox" + "<%= wsTreeName %>" + name)
			cbObj.checked = false;
		}
	} else {
		self.document.<portlet:namespace />fm[name].checked=true
		if (self.ss_treeIframeDiv && self.ss_treeIframeDiv.document) {
			var cbObj = self.ss_treeIframeDiv.document.getElementById("ss_tree_checkbox" + "<%= wsTreeName %>" + name)
			cbObj.checked = true;
		}
	}
	return false
}

</script>
<ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
  rootOpen="true" topId="${ssWsDomTreeBinderId}" 
  multiSelect="<%= new ArrayList() %>" multiSelectPrefix="$type_" />

<br>
<br>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="closeBtn" 
 value="<ssf:nlt tag="button.close" text="Close"/>" onClick="ss_buttonSelect('closeBtn');">
</div>
<input type="hidden" name="btnClicked"/>
</form>
<br>
</td></tr></table>

