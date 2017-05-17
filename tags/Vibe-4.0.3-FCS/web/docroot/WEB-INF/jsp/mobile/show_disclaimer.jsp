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
<c:set var="ss_windowTitle" value="Mobile access is not supported" scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>

<div id="wrapper">
 <div id="pagebody">
  <div id="header">
  <ul>
	<li>
	  <span>
	    <ssf:nlt tag="mobile.welcome">
	      <ssf:param name="value" useBody="true"><ssf:userTitle user="${ssUser}"/></ssf:param>
	    </ssf:nlt>
	  </span>
	</li>
  </ul>
  </div>
  <br/>
  <div class="pagebody">
	<div class="maincontent">
	  <form method="post" action="<ssf:url adapter="true" portletName="ss_forum" 
							action="__ajax_mobile" actionUrl="true" 
							operation="mobile_show_front_page" />">
		Notice: The Teaming Mobile Interface is not supported in Teaming V2. 
		It has not undergone the rigorous testing that Novell performs on its supported code.
		Therefore you may experience problems when using this interface.
		<br/>
		<br/>
		Would you like to continue using this interface under the agreement that it is not supported?
		<br/>
		<br/>
		<input type="submit" name="acceptBtn" value="<ssf:nlt tag="button.accept"/>"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.decline"/>"
		  onClick="self.location.href='<ssf:url 
		    	adapter="true" 
		    	portletName="ss_forum" 
		    	action="view_ws_listing" 
		    	actionUrl="false" 
		    	binderId="${ssUser.workspaceId}"/>';return false;"/>
			<sec:csrfInput />
		</form>
	</div>
  </div>
 </div>
</div>
</body>
</html>
