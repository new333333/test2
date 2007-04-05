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
	action="<portlet:actionURL><portlet:param 
		name="action" value="configure_index"/></portlet:actionURL>" 
	method="post" 
	name="<portlet:namespace />fm">

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.configure.index.select" text="Select the forums to be re-indexed:"/></span>
<br>
<br>
<a class="ss_linkButton ss_smallprint" 
  href="javascript:<portlet:namespace/>_doSelect(true);"
><ssf:nlt tag="button.selectAll"/></a>
&nbsp;&nbsp;&nbsp;
<a class="ss_linkButton ss_smallprint" 
  href="javascript:<portlet:namespace/>_doSelect(false);"
><ssf:nlt tag="button.clearAll"/></a>
<br>
<script type="text/javascript">
function <portlet:namespace/>_doSelect(newState) {
	ss_selectAll('<portlet:namespace/>fm', 'workspace_', newState);
	ss_selectAll('<portlet:namespace/>fm', 'folder_', newState);
	ss_selectAll('<portlet:namespace/>fm', 'people_', newState);
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
function ss_selectAll(formName, prefix, newState) {
    var totalElements = self.document[formName].elements.length;
    for ( var i=0; i < totalElements; i++) {
        var namestring = self.document.forms[formName].elements[i].name.substring(0,prefix.length)
        ss_debug("namestring="+namestring)
        if (namestring == prefix) {
            var e = self.document.forms[formName].elements[i];
            e.checked = newState;
        }
    }
}
</script>
<ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
  rootOpen="true" topId="${ssWsDomTreeBinderId}" 
  multiSelect="<%= new ArrayList() %>" multiSelectPrefix="$type_" />

<br>
<a class="ss_linkButton ss_smallprint" 
  href="javascript:<portlet:namespace/>_doSelect(true);"
><ssf:nlt tag="button.selectAll"/></a>
&nbsp;&nbsp;&nbsp;
<a class="ss_linkButton ss_smallprint" 
  href="javascript:<portlet:namespace/>_doSelect(false);"
><ssf:nlt tag="button.clearAll"/></a>
<br>
<br>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</form>
<br>
</td></tr></table>

