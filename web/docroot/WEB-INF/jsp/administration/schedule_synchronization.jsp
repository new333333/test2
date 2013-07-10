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
<%@ page import="org.kablink.teaming.util.NLT" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">
var ${renderResponse.namespace}_savedIndex;
</script>
<div class="ss_style ss_portlet">

<form class="ss_style ss_form" name="${renderResponse.namespace}fm" 
	id="${renderResponse.namespace}fm" method="post" 
	action="<ssf:url action="schedule_synchronization" actionUrl="true">
	<ssf:param 
		    name="binderId" value="${ssBinder.id}"/></ssf:url>">
	
		<%--			<c:if test="${!empty ssBinder}">
					<input type="hidden" name="binderId" value="${ssBinder.id}" />
				</c:if>
	--%>
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<%--<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">--%>
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
  onClick="ss_cancelButtonCloseWindow();return false;"/>
</div>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="synchronization.schedule.header" /></legend>	
<table class="ss_style" border="0" cellspacing="3" cellpadding="3">
<tr>
<td valign="top">
<input type="checkbox" class="ss_style" id="enabled" name="enabled" <c:if test="${ssScheduleInfo.enabled}">checked</c:if> />
<span class="ss_labelRight"><ssf:nlt tag="synchronization.schedule.enable"/> </span><ssf:inlineHelp jsp="workspaces_folders/misc_tools/sync_schedule"/>
<br/>
<c:set var="schedule" value="${ssScheduleInfo.schedule}"/>
<c:set var="schedPrefix" value="sync"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
</td></tr></table>
</fieldset>
<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<%--<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">--%>
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
  onClick="ss_cancelButtonCloseWindow();return false;"/>
</div>
</form>

