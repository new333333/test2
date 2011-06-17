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
<c:set var="tag" value="folder.manageFolderVersionControls"/>
<jsp:useBean id="tag" type="String" />
<c:set var="ss_windowTitle" value='<%= NLT.get(tag) %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body tundra">

<script type="text/javascript">

function ss_checkIfNumberValid(s) {
	if (ss_trim(s) == '') return true;   //Blank is ok
	
	var pattern1 = new RegExp("^[0-9]+$");
	if (pattern1.test(ss_trim(s))) {
		if (ss_trim(s).length >= 8) {
			alert("<ssf:escapeJavaScript><ssf:nlt tag="error.numberTooBig"/></ssf:escapeJavaScript>");
			return false;
		}
		return true;
	}
	alert("<ssf:escapeJavaScript><ssf:nlt tag="error.mustBeANumber"/></ssf:escapeJavaScript>");
	return false;
}

</script>

<div class="ss_style ss_portlet">
<ssf:form titleTag="${tag}">
<br/>
<c:if test="${ssBinder.entityType == 'folder'}">
  <span><ssf:nlt tag="access.currentFolder"/></span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span><ssf:nlt tag="access.currentWorkspace"/></span>
</c:if>
<% //need to check tags for templates %>
<span class="ss_bold"><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
<div align="right">
<form class="ss_form" method="post" style="display:inline;" 
	action="<ssf:url action="manage_binder_quota" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="binderType" value="${ssBinder.entityType}"/></ssf:url>">
  <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />" >
  <input type="submit" class="ss_submit" name="closeBtn" 
    value="<ssf:nlt tag="button.close"/>" onClick="ss_cancelButtonCloseWindow();return false;">
</form>
</div>

<br/>
<form class="ss_form" method="post" style="display:inline;" 
	action="<ssf:url action="manage_binder_version_controls" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/></ssf:url>">
	
    <fieldset class="ss_fieldset">
      <legend class="ss_legend"><ssf:nlt tag="versions.enableDisable"/></legend>
      
      <div style="padding:10px 10px 0px 10px;">
        The checkbox for turning on and off versions goes here...
      </div>
    </fieldset>
<br/>
<br/>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />" >
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
  onClick="ss_cancelButtonCloseWindow();return false;">
</form>
</ssf:form>
</div>

</body>
</html>
