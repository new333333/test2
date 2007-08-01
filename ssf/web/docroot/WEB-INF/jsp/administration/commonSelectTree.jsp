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

<br>
<br>
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm', 'id_', true);"
><ssf:nlt tag="button.selectAll"/></a>
&nbsp;&nbsp;&nbsp;
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm', 'id_', false);"
><ssf:nlt tag="button.clearAll"/></a>
<br>
<script type="text/javascript">
function t_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_tree_showId(forum, obj) {
	if (self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm["id_"+forum] && self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm["id_"+forum].checked) {
		self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm["id_"+forum].checked=false
	} else {
		self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm["id_"+forum].checked=true
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
<ssf:tree treeName="<%= "t_" + renderResponse.getNamespace()+ "_tree" %>" treeDocument="${ssDomTree}"  
  rootOpen="true" multiSelect="<%= new java.util.ArrayList() %>" multiSelectPrefix="id_" />

<br>
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm', 'id_', true);"
><ssf:nlt tag="button.selectAll"/></a>
&nbsp;&nbsp;&nbsp;
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm', 'id_', false);"
><ssf:nlt tag="button.clearAll"/></a>
<br>
<br>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel" text="Cancel"/>">

