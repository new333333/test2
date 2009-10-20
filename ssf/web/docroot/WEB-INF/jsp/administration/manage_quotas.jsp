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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.manage.quotas") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.manage.quotas">

<div style="padding:10px;" id="ss_manageQuotas">
<span class="ss_titlebold"><ssf:nlt tag="administration.manage.quotas" /></span>
<br>
<br>

<c:if test="${!empty ssException}">
  <font color="red">
    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
  </font>
  <br/>
</c:if>

<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_quotas" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/></ssf:url>">
		
	<div>
	  <input type="checkbox" name="enableQuotas" 
	  <c:if test="${ss_quotasEnabled}">checked=checked</c:if>
	  />
	  <ssf:nlt tag="administration.quotas.enable"/>
	</div>
		
	<table>
	<tr>
	<td style="padding-left:20px;" valign="top">
	  <ssf:nlt tag="administration.quotas.default"/>
	</td>
	<td style="padding-left:4px;" valign="top">
	  <input type="text" size="6" name="defaultQuota" value="${ss_quotasDefault}"/>
	  <ssf:nlt tag="administration.quotas.mb"/>
	</td>
	</tr>
	<tr>
	<td style="padding-left:20px;" valign="top">
	  <ssf:nlt tag="administration.quotas.highWaterMark"/>
	</td>
	<td style="padding-left:4px;" valign="top">
	  <input type="text" size="6" name="highWaterMark" value="${ss_quotasHighWaterMark}"/>%
	</td>
	</tr>
	</table>
		
	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="self.window.close();return false;"/>
</form>
</div>
</ssf:form>
</div>

</div>
</body>
</html>
