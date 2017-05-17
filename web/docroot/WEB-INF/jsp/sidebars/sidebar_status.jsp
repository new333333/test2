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
<% // Status %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ page import="org.kablink.util.PropertyNotFoundException" %>
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/>
<c:if test="${ssUser.internalId != guestInternalId}">
  <c:if test="${empty ss_statusId}">
    <c:set var="ss_statusId" value="ss_myStatus${renderResponse.namespace}" scope="request"/>
  </c:if>

  <div class="ss_myStatusOuter" id="${ss_statusId}">
    <%@ include file="/WEB-INF/jsp/sidebars/sidebar_status2.jsp" %>
  </div>
</c:if>

<c:if test="${ssUser.internalId == guestInternalId}">
  <script type="text/javascript">
    function ss_setReturnUrl() {
    	var urlObj = self.document.getElementById('loginForm')['spring-security-redirect'];
    	var url = self.location.href;
    	urlObj.value = url;
    }
  </script>
 <ssf:sidebarPanel title="login.please" id="ss_login_sidebar"
    initOpen="true" sticky="true">
  <form name="loginForm" id="loginForm" method="post" action="${ss_loginPostUrl}" autocomplete="off"
    onSubmit="ss_setReturnUrl();return true;"
  >
     <table class="ss_table" border="0" cellpadding="2" width="95%">
      <tr>
        <td valign="top">
          <label for="j_username">
	          <span><ssf:nlt tag="login.name"/></span><br/>
	      </label>
          <input type="text" style="width:150px;" name="j_username" id="j_username"/>
        </td>
      </tr>
      <tr>
        <td valign="top">
          <label for="j_password">
          	<span><ssf:nlt tag="login.password"/></span><br/>
          </label>
          <input type="password" style="width:150px;" name="j_password" id="j_password"/>
        </td>
      </tr>
    </table>
    <br/>
    <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"
    onclick="ss_setReturnUrl();return true;"
    />
    <input type="hidden" name="spring-security-redirect" value=""/>
    <sec:csrfInput />
  </form>
 </ssf:sidebarPanel>  
</c:if>
