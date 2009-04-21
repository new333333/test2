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

<br>
<br>
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('${renderResponse.namespace}fm', 'id_', true);"
><ssf:nlt tag="button.selectAll"/></a>
&nbsp;&nbsp;&nbsp;
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('${renderResponse.namespace}fm', 'id_', false);"
><ssf:nlt tag="button.clearAll"/></a>
<br>
<script type="text/javascript">
function t_${renderResponse.namespace}_tree_showId(forum, obj) {
	return ss_checkTree(obj, "ss_tree_checkboxt_${renderResponse.namespace}_treeid" + forum)

}
function ss_selectAll(formName, prefix, newState) {
    var totalElements = self.document[formName].elements.length;
    for ( var i=0; i < totalElements; i++) {
        var namestring = self.document.forms[formName].elements[i].name.substring(0,prefix.length)
        if (namestring == prefix) {
            var e = self.document.forms[formName].elements[i];
            e.checked = newState;
        }
    }
}
function ss_selectAllIfNoneSelected(prefix) {
    if (typeof this.elements == 'undefined') return true;
    var totalElements = this.elements.length;
    for ( var i=0; i < totalElements; i++) {
        var namestring = this.elements[i].name.substring(0,prefix.length)
        if (namestring == prefix) {
            var e = this.elements[i];
			if(e.checked) {
				return true;
			}
        }
    }
	ss_selectAll(this.name, prefix, true);
	return true;
}
</script>
<ssf:tree treeName='<%= "t_" + renderResponse.getNamespace()+ "_tree" %>' treeDocument="${ssDomTree}"  
  rootOpen="true" multiSelect="<%= new java.util.ArrayList() %>" multiSelectPrefix="id" />

<br>
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('${renderResponse.namespace}fm', 'id_', true);"
><ssf:nlt tag="button.selectAll"/></a>
&nbsp;&nbsp;&nbsp;
<a class="ss_linkButton ss_smallprint" 
  href="javascript:ss_selectAll('${renderResponse.namespace}fm', 'id_', false);"
><ssf:nlt tag="button.clearAll"/></a>
<br>
<br>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>"
  onClick="ss_selectAllIfNoneSelected.call(this,'id_');">

