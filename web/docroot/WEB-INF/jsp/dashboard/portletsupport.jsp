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

<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />

<%
int entryWindowWidth = 0;
if (ssUserProperties.containsKey("folderEntryWidth")) {
	entryWindowWidth = Integer.parseInt((String) ssUserProperties.get("folderEntryWidth"));
}
int entryWindowTop = 0;
if (ssUserProperties.containsKey("folderEntryTop")) {
	entryWindowTop = Integer.parseInt((String) ssUserProperties.get("folderEntryTop"));
}
int entryWindowLeft = 0;
if (ssUserProperties.containsKey("folderEntryLeft")) {
	entryWindowLeft = Integer.parseInt((String) ssUserProperties.get("folderEntryLeft"));
}
int entryWindowHeight = 0;
if (ssUserProperties.containsKey("folderEntryHeight")) {
	entryWindowHeight = Integer.parseInt((String) ssUserProperties.get("folderEntryHeight"));
}
String autoScroll = "true";
request.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
request.setAttribute("ss_entryWindowTop", new Integer(entryWindowTop));
request.setAttribute("ss_entryWindowLeft", new Integer(entryWindowLeft));
request.setAttribute("ss_entryWindowHeight", new Integer(entryWindowHeight));

%>

<script type="text/javascript">
    			
var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";
</script>

<!-- portlet iframe div -->
<%@ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- portlet iframe div -->	