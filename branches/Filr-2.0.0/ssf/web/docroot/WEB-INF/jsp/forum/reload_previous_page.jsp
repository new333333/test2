<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script type="text/javascript">
	function displayReloadDetails() {
		var details = "<ssf:escapeJavaScript>${ss_errorMessagePackedDetails}</ssf:escapeJavaScript>";
		if (details && (0 < details.length)) {
			var detailList = ss_unpack(details);
			var count = detailList.length;
			if (0 < count) {
				var divObj = document.getElementById('reloadDetailsDiv');
				divObj.appendChild(document.createElement("br"));
				for (var i = 0; i < count; i += 1) {
					var span = document.createElement("span");
					divObj.appendChild(span);
					span.appendChild(document.createTextNode(detailList[i]));
					divObj.appendChild(document.createElement("br"));
				}
			}
		}
	}
	ss_createOnLoadObj("displayReloadDetails", displayReloadDetails);
</script>

<ssf:form title='<%= NLT.get("general.error") %>'>
<c:if test="${!empty ss_errorMessage}">
  <span>${ss_errorMessage}</span>
</c:if>
<c:if test="${!empty ss_errorMessagePackedDetails}">
	<div id="reloadDetailsDiv">
	</div>
</c:if>
<br/>
<br/>
<input type="button" value="<ssf:nlt tag="button.goBack"/>" 
  onClick="self.window.history.back();return false;" >
</ssf:form>
