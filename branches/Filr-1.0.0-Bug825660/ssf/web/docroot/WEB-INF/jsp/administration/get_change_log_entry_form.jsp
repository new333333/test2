<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html<% if (org.kablink.teaming.web.util.MiscUtil.isHtmlQuirksMode()) { %> PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"<% } %>>
<html <c:if test="${!empty ssUser && !empty ssUser.locale}"> lang="${ssUser.locale}"</c:if>>
<head>
<META http-equiv="Content-Script-Type" content="text/javascript">
<META http-equiv="Content-Style-Type" content="text/css">

<c:set var="ss_servlet" value="true" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>
<c:set var="ss_findType" value="entries"/>
<c:if test="${ssBinderId == ssProfilesBinderId}"><c:set var="ss_findType" value="user"/></c:if>
<script type="text/javascript">
function ss_saveChangeLogEntryId(id) {
	var formObj = self.parent.document.getElementById('ss_changeLogForm')
	formObj['entityId'].value = id;
}
</script>

</head>
<body class="tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<c:if test="${empty ssBinderId}">
  <span class="ss_fineprint"><ssf:nlt tag="changeLog.selectFolder"/></span>
</c:if>
<c:if test="${!empty ssBinderId}">
<div class="ss_style ss_portlet">
<form class="ss_portlet_style ss_form" style="background-color: #eeeeee;"
  id="change_logEntryForm" 
  name="change_logEntryForm" method="post" >

  <div id="ss_changeLogEntryForm" class="ss_searchtext" style="background-color: #eeeeee;">
	 <ssf:find formName="change_logEntryForm" 
	    formElement="entryId" 
	    type="${ss_findType}"
	    width="300px" 
	    binderId="${ssBinderId}"
	    searchSubFolders="false"
	    singleItem="true"
	    clickRoutine="ss_saveChangeLogEntryId"
	    /> 
  </div>
</form>
</div>
</c:if>
</body>
</html>
