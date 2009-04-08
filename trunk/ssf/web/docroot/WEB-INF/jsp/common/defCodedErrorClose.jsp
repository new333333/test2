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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="tundra">

<div class="ss_style ss_portlet">

<h1><spring:message code="exception.codedError.title"/></h1>

<p>${exception.localizedMessage }<br/></p>

<ssf:ifnotadapter>
	<c:if test="${!empty ssUser.workspaceId}">
		<p style="text-align:center;"><a href="<ssf:url action="view_ws_listing"
					    		binderId="${ssUser.workspaceId}">
					    		<ssf:param name="newTab" value="1"/>
								</ssf:url>">- <spring:message code="button.home"/> -</a></p>
	</c:if>
</ssf:ifnotadapter>

<ssf:ifadapter>
<br/>
<input type="button" value="<ssf:nlt tag="button.close"/>" class="ss_submit" 
 onClick="self.window.close();return false;">
</ssf:ifadapter>

</div>

</body>
</html>
