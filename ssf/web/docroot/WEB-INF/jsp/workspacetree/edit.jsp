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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
String wsTreeName = renderResponse.getNamespace();
%>
<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssBinder}">
<b><ssf:nlt tag="portlet.workspace.selected.workspace"/></b>
<c:out value="${ssBinder.title}" />
<br/>
</c:if>
<form class="ss_style ss_form" 
 action="<portlet:actionURL windowState="maximized"/>" 
 method="post" 
 name="${renderResponse.namespace}fm">

<br/>
<span class="ss_bold"><ssf:nlt tag="portlet.workspace.select.workspace" /></span>
<br>
<script type="text/javascript">
function <%= wsTreeName %>_showId(id, obj) {
	return ss_checkTree(obj, "ss_tree_radio<%= wsTreeName %>topWorkspace" + id);
}
</script>
<c:set var="singleSelect" value=""/>
<c:if test="${!empty ssBinder}">
	<c:set var="singleSelect" value="${ssBinder.id}"/>
</c:if>
<ssf:tree treeName="<%= wsTreeName %>"  treeDocument="${ssWsDomTree}" 
 	topId="${ssWsDomTreeBinderId}" rootOpen="true"
	 singleSelect="${singleSelect}" singleSelectName="topWorkspace" />

<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
<input style="margin-left:15px;" type="submit" class="ss_submit" name="closeBtn"
 value="<ssf:nlt tag="button.close"/>"
 onClick="self.location.href='<portlet:renderURL windowState="normal" portletMode="view"/>';return false;"/>
  <sec:csrfInput />
</form>
<br>

</td></tr></table>

