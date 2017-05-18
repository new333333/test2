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
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
<script type="text/javascript">
//How long to sleep between auto refresh; 
//  set this to 0 for no auto refresh (defaulted to 0 to keep iChain session timeouts from being voided)
//  set to 300000 for 5 minutes between refreshes
var ss_presenceTimeoutTimeToSleep = 0;

function ss_presenceObj(url, divId, name) {
	this.url = url;
	this.divId = divId;
	this.timeout = '';
	this.name=name;
	this.getPresence=function() {
		ss_setupStatusMessageDiv();
		if (this.timeout != '') {
			clearTimeout(this.timeout);
			this.timeout = null;
		}
		ss_fetch_div(url, divId, "false");
		if (ss_presenceTimeoutTimeToSleep > 0) {
			this.timeout = setTimeout(name + ".getPresence()", ss_presenceTimeoutTimeToSleep);
		}
	}
}

var ${ss_namespace}_${componentId}_presence = new ss_presenceObj("<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__view_presence" 
    	actionUrl="false" 
    	binderId="${ssBinder.id}">
    	<ssf:param name="namespace" value="${ss_namespace}"/>
    	<ssf:param name="operation2" value="${componentId}"/>
    	<ssf:param name="userList" value="${ss_userList}"/>
    	</ssf:url>", '${ss_divId}', '${ss_namespace}_${componentId}_presence');
if (ss_presenceTimeoutTimeToSleep > 0) {
	${ss_namespace}_${componentId}_presence.timeout = setTimeout("${ss_namespace}_${componentId}_presence.getPresence()", ss_presenceTimeoutTimeToSleep);
}
</script>
