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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<form class="ss_style ss_form" method="post" 
		  action="<ssf:url
		 action="modify_folder_entry"
		 operation="move"
		 folderId="${ssBinder.id}"
		 entryId="${ssEntry.id}"/>" name="<portlet:namespace />fm">
<div class="ss_style ss_portlet">
<br>

EnterID:
<input type="text" name="destination"/>


<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</div>
</form>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
