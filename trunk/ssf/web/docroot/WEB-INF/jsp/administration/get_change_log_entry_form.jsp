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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd">
<html xmlns:svg="http://www.w3.org/2000/svg-20000303-stylable">
<head>

<c:set var="ss_servlet" value="true" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>

<script type="text/javascript">
function ss_saveChangeLogEntryId(id) {
	var formObj = self.parent.document.getElementById('ss_changeLogForm')
	formObj['entityId'].value = id;
}
</script>

</head>
<body>
<c:if test="${empty ssBinderId}">
  <span class="ss_fineprint"><ssf:nlt tag="changeLog.selectFolder"/></span>
</c:if>
<c:if test="${!empty ssBinderId}">
<form class="ss_portlet_style ss_form" style="background-color: #eeeeee;"
  id="change_logEntryForm" 
  name="change_logEntryForm" method="post" 
  action="<portlet:renderURL/>">

  <div id="ss_changeLogEntryForm" style="background-color: #eeeeee;">
	 <ssf:find formName="change_logEntryForm" 
	    formElement="entryId" 
	    type="entries"
	    width="140px" 
	    binderId="${ssBinderId}"
	    searchSubFolders="false"
	    singleItem="true"
	    clickRoutine="ss_saveChangeLogEntryId"
	    /> 
  </div>
</form>
</c:if>
</body>
</html>
