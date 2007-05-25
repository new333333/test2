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

