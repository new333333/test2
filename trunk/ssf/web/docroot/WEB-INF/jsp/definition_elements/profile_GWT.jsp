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
<% //Business card view %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/initializeGWT.jsp"     %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>

<script type="text/javascript" language="javascript">
		// Save away information such as the binder id and the adapted url for the request we are working with.
		// Through an overlay we will access m_requestInfo from java.
		var profileRequestInfo = {
			binderId : '${ssBinder.id}',
			userName : '<ssf:userTitle user="${ssProfileConfigEntry}"/>',
			adaptedUrl : '<ssf:url crawlable="true"
				adapter="true" portletName="ss_forum"
					folderId="${ssBinder.id}" 
						 action="view_ws_listing" ><ssf:param 
							name="profile" value="0" /></ssf:url>',
			imagesPath : '<ssf:escapeJavaScript><html:imagesPath/></ssf:escapeJavaScript>',
			myWSUrl : '<ssf:url crawlable="true"
				adapter="true" portletName="ss_forum"
					folderId="${ssUser.workspaceId}" 
						 action="view_ws_listing" ><ssf:param 
						name="profile" value="1" /></ssf:url>',
		};
</script>

<script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/gwtteaming.nocache.js"></script>
<div id="gwtProfileDiv">
</div>
