<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("login.please") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>

<div id="wrapper" class="header">
  <div id="header">
    <ul>
    </ul>
  </div>
  <div class="ss_text_login" id="ss_fieldset_login">
  <form name="loginForm" id="loginForm" method="post" action="${ss_loginPostUrl}">
     <table class="ss_table" border="0" cellpadding="2">
      <tr>
        <td width="67%"><h4><ssf:nlt tag="login.please"/></h4></td>
      </tr>
      <tr>
        <td valign="top">
          <label for="j_username"><span><ssf:nlt tag="login.name"/></span><br/></label>
          <input type="text" style="width:150px;" name="j_username" id="j_username"/>
        </td>
      </tr>
      <tr>
        <td valign="top">
          <label for="j_password"><span><ssf:nlt tag="login.password"/></span><br/></label>
          <input type="password" style="width:150px;" name="j_password" id="j_password"/>
        </td>
      </tr>
    </table>
    <br/>
    <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"/>
    <input type="hidden" name="spring-security-redirect" value="${ssUrl}"/>
  </form>
  </div>
	<script type="text/javascript">
		var formObj = self.document.getElementById('loginForm');
		formObj.j_username.focus();
	</script>
    <br/>
    <br/>
    <br/>
</div>

</body>
</html>
