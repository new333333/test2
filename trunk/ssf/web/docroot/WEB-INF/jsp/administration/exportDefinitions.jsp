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
<jsp:useBean id="ssDomTree" type="org.dom4j.Document" scope="request" />

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<table class="ss_style" width="100%"><tr><td>

<form class="ss_style ss_form" action="<portlet:actionURL>
			  <portlet:param name="action" value="export_definition"/>
		      </portlet:actionURL>" method="post" name="<portlet:namespace />fm">

<br>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.export.definitions.select"/></span>
<br>
<br>
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('<portlet:namespace />fm', 'id_', true);"
><ssf:nlt tag="button.selectAll"/></a>
&nbsp;&nbsp;&nbsp;
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('<portlet:namespace />fm', 'id_', false);"
><ssf:nlt tag="button.clearAll"/></a>
<br>
<script type="text/javascript">
function t_<portlet:namespace/>_tree_showId(forum, obj) {
	if (self.document.<portlet:namespace />fm["id_"+forum] && self.document.<portlet:namespace />fm["id_"+forum].checked) {
		self.document.<portlet:namespace />fm["id_"+forum].checked=false
	} else {
		self.document.<portlet:namespace />fm["id_"+forum].checked=true
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
<ssf:tree treeName="<%= "t_" + renderResponse.getNamespace()+ "_tree" %>" treeDocument="<%= ssDomTree %>"  
  rootOpen="true" multiSelect="<%= new ArrayList() %>" multiSelectPrefix="id_" />

<br>
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('<portlet:namespace />fm', 'id_', true);"
><ssf:nlt tag="button.selectAll"/></a>
&nbsp;&nbsp;&nbsp;
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('<portlet:namespace />fm', 'id_', false);"
><ssf:nlt tag="button.clearAll"/></a>
<br>
<br>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
</form>
<br>
</td></tr></table>

