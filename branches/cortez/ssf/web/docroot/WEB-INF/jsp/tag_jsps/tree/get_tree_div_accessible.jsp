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
<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>
<body>

<jsp:useBean id="ss_tree_treeName" type="java.lang.String" scope="request" />
<jsp:useBean id="ss_tree_showIdRoutine" type="java.lang.String" scope="request" />
<jsp:useBean id="ss_tree_binderId" type="java.lang.String" scope="request" />
<jsp:useBean id="ss_tree_topId" type="java.lang.String" scope="request" />
<jsp:useBean id="ss_tree_select_id" type="java.lang.String" scope="request" />
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
<%
	if (ss_tree_showIdRoutine.startsWith("parent.")) {
		ss_tree_showIdRoutine = ss_tree_showIdRoutine.substring(7);
	}
%>
<c:if test="${!empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
<div><span class="ss_bold"><ssf:nlt tag="general.notLoggedIn"/></span></div>
</c:if>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
<div class="ss_style">

	<c:choose>
	<c:when test="${ss_tree_select_type == '0'}">	
		<ssf:tree treeName="${ss_tree_treeName}" 
		  initOnly="true"
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="${ss_tree_binderId}"
		  topId="${ss_tree_topId}"
		  indentKey="${ss_tree_indentKey}"
		  rootOpen="true" 
		  showIdRoutine="parent.${ss_tree_showIdRoutine}"
		/>
		<ssf:tree treeName="${ss_tree_treeName}" 
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="${ss_tree_binderId}"
		  topId="${ss_tree_topId}"
		  indentKey="${ss_tree_indentKey}"
		  rootOpen="true" 
		  showIdRoutine="parent.${ss_tree_showIdRoutine}"
		/>
	</c:when>
	<c:when test="${ss_tree_select_type == '1'}">
		<ssf:tree treeName="${ss_tree_treeName}" 
		  initOnly="true"
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="${ss_tree_binderId}"
		  topId="${ss_tree_topId}"
		  indentKey="${ss_tree_indentKey}"
		  rootOpen="true" 
		  showIdRoutine="parent.${ss_tree_showIdRoutine}"
		  singleSelect="${ss_tree_select}"
		  singleSelectName="${ss_tree_select_id}"
		/>
		<ssf:tree treeName="${ss_tree_treeName}" 
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="${ss_tree_binderId}"
		  topId="${ss_tree_topId}"
		  indentKey="${ss_tree_indentKey}"
		  rootOpen="true" 
		  showIdRoutine="parent.${ss_tree_showIdRoutine}"
		  singleSelect="${ss_tree_select}"
		  singleSelectName="${ss_tree_select_id}"
		/>
	</c:when>
	<c:when test="${ss_tree_select_type == '2'}">
<jsp:useBean id="ss_tree_select" type="java.util.List" scope="request" />
		<ssf:tree treeName="${ss_tree_treeName}" 
		  initOnly="true"
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="${ss_tree_binderId}"
		  topId="${ss_tree_topId}"
		  indentKey="${ss_tree_indentKey}"
		  rootOpen="true" 
		  showIdRoutine="parent.${ss_tree_showIdRoutine}"
		  multiSelect="<%= ss_tree_select %>"
		  multiSelectPrefix="${ss_tree_select_id}"
		/>
		<ssf:tree treeName="${ss_tree_treeName}" 
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="${ss_tree_binderId}"
		  topId="${ss_tree_topId}"
		  indentKey="${ss_tree_indentKey}"
		  rootOpen="true" 
		  showIdRoutine="parent.${ss_tree_showIdRoutine}"
		  multiSelect="<%= ss_tree_select %>"
		  multiSelectPrefix="${ss_tree_select_id}"
		/>
	</c:when>
</c:choose>
</div>
<script type="text/javascript">
function ss_tree_accessible_open_${ss_tree_treeName}_${ss_tree_binderId}() {
ss_treeOpen('${ss_tree_treeName}', '${ss_tree_binderId}', '${ss_tree_parentId}', '${ss_tree_bottom}', '${ss_tree_type}');
}
ss_createOnLoadObj("ss_tree_accessible_open_${ss_tree_treeName}_${ss_tree_binderId}", ss_tree_accessible_open_${ss_tree_treeName}_${ss_tree_binderId});
self.window.focus();
</script>
</c:if>

</body>
</html>

