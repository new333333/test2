<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="tag" value="move.folder"/>
<c:if test="${ssOperation == 'move'}">
<c:if test="${ssBinder.entityType != 'folder'}">
  <c:set var="tag" value="move.workspace"/>
</c:if>
</c:if>
<c:if test="${ssOperation != 'move'}">
<c:if test="${ssBinder.entityType == 'folder'}">
  <c:set var="tag" value="copy.folder"/>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <c:set var="tag" value="copy.workspace"/>
</c:if>
</c:if>
<jsp:useBean id="tag" type="String" />
<c:set var="ss_windowTitle" value='<%= NLT.get(tag) %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<c:set var="wsTreeName" value="${renderResponse.namespace}_wsTree"/>
<script type="text/javascript">
function ${wsTreeName}_showId(forum, obj, action) {
	return ss_checkTree(obj, "ss_tree_radio${wsTreeName}destination" + forum)
}
function ss_saveDestinationBinderId(id) {
	var formObj = document.getElementById("ss_move_form");
	formObj.idChoices.value = "destination_" + id
}
function ss_submitMoveBinderForm() {
	var formObj = document.getElementById("ss_move_form");
	formObj.submit();
}
</script>

<div class="ss_style ss_portlet">
<ssf:form titleTag="${tag}">

<br/>
<c:if test="${!empty ssException}">
	<span class="ss_errorLabel ss_largerprint"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span><br>
<br/>
<br/>
</c:if>
<c:if test="${ssBinder.entityType == 'folder'}">
  <span><ssf:nlt tag="move.currentFolder"/>: </span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span><ssf:nlt tag="move.currentWorkspace"/></span>
</c:if>
<span class="ss_bold"><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
<br/>
<form class="ss_style ss_form" method="post" name="ss_move_form" id="ss_move_form"> 
		
<br/>

<span class="ss_bold"><ssf:nlt tag="move.selectDestination"/></span>
<br/>
<div class="ss_indent_large">
<ssf:tree treeName="${wsTreeName}"
	treeDocument="${ssWsDomTree}"  
 	rootOpen="true"
	singleSelect="" 
	singleSelectName="destination" />

</div>

<br/>
<span class="ss_bold"><ssf:nlt tag="move.findDestination"/></span>
<br/>
<ssf:find formName="ss_move_form" 
    formElement="binderId" 
    type="places"
    width="180px" 
    singleItem="true"
    clickRoutine="ss_saveDestinationBinderId"
    /> 

<br/>
<br/>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />" 
  onClick="ss_startSpinner();setTimeout('ss_submitMoveBinderForm();', 500);return false;">
<input type="button" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelButtonCloseWindow();return false;">
<input type="hidden" name="okBtn" value="okBtn"/>
</form>
</ssf:form>
</div>

</body>
</html>
