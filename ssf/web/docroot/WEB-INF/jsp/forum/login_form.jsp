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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
  <form name="loginForm" id="loginForm" method="post" action="${ss_loginPostUrl}">
   <fieldset class="ss_fieldset_login">
    <div class="ss_legend_login"><ssf:nlt tag="login.please"/></div>
     <table><tbody>
      <tr>
        <td valign="middle" align="right">
          <span><ssf:nlt tag="login.name"/></span>
        </td>
        <th valign="top" style="padding-left:4px;">
          <input type="text" class="ss_text_login" size="40" name="j_username"/>
        </td>
		<td>&nbsp;</td>
      </tr>
      <tr>
        <td valign="middle" align="right">
          <span><ssf:nlt tag="login.password"/></span>
        </td>
        <td valign="top" style="padding-left:4px;">
          <input class="ss_text_login" type="password" size="40" name="j_password"/>
        </td>
      </tr>
      <tr>
        <td valign="middle" align="right">
          <span><ssf:nlt tag="login.remember"/></span>
        </td>
        <td valign="top" align="left" style="padding-left:6px;">
          <input type="checkbox" name="remember"/>
        </td>
      </tr>
      <c:if test="${!empty ss_loginError}">
       <tr>
		 <td>&nbsp;</td>
         <td style="color: red;" colspan="2"><ssf:nlt tag="errorcode.login.failed"/></td>
       </tr>
      </c:if>
 		<tr>
   			<td colspan="2" align="center">
    		  <br/>
    		  <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"/>
    		  <input type="reset" class="ss_submit" style="margin-left:20px;" value="<ssf:nlt tag="button.reset"/>"/>
			  <br/>
			</td>
		</tr>		  
	 </table></tbody>
	  <c:if test="${!empty ssUrl}">
    <input type="hidden" name="spring-security-redirect" value="${ssUrl}"/>
    </div> 
    </fieldset>
</c:if>
  </form>
<script type="text/javascript">
	var formObj = self.document.getElementById('loginForm');
	formObj.j_username.focus();
</script>
