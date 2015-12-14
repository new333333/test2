<%
// The task summary portlet
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
<div class="ss_portlet_style ss_portlet">
<c:if test="${ss_windowState == 'maximized'}">
<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
</c:if>
<div class="ss_style" style="padding:4px;">

<script type="text/javascript">
//Define the url of this page in case the "add entry" operation needs to reload this page
var ss_reloadUrl${ssBinder.id} = "<portlet:renderURL />";
var ss_reloadUrl = ss_reloadUrl${ssBinder.id};
</script>
<table class="ss_style" width="100%"><tr><td>
<div>
  <div>
    <div class="ss_content_window">
	<%@ include file="/WEB-INF/jsp/dashboard/portletsupport.jsp" %>
	<%@ include file="/WEB-INF/jsp/dashboard/task_view.jsp" %>
</div></div></div>
</td></tr></table>
<div align="right">
  <a class="ss_linkButton" href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />">
    <span><ssf:nlt tag="button.configure"/></span>
  </a>
</div>
</div>
</div>
