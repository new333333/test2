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

<body>

<script type="text/javascript">
function showUrl(params) {
	if (parent.ss_showUrlInPortlet) {
		alert('Calling parent portlet: '+ params)
		parent.ss_showUrlInPortlet(params)
		return false
	} else if (self.opener && self.opener.ss_showUrlInPortlet) {
		alert('Calling opener portlet: '+ params)
		self.opener.ss_showUrlInPortlet(params)
		setTimeout('self.window.close();', 200)
		return false
	} else {
		alert('no parent or opener found')
		return true
	}
}
</script>

<div class="ss_style ss_portlet" align="left">
<a href="javascript: ;" onClick="return showUrl('action=fragment&')">
return to portlet url
</a>

<br>

<a href="<ssf:url 
    adapter="true" 
    portletName="ss_widgettest" 
    action="fragment" >
	<ssf:param name="operation" value="viewFragment" />
    </ssf:url>" onClick="alert(this.href)">
show url in iframe
</a>

</div>

</body>
</html>
