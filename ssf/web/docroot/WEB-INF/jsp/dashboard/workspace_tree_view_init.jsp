<%
// The dashboard "workspace tree" init component
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
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<c:set var="treeName" value="wsTree_${ss_namespace}_${componentId}"/>
<script type="text/javascript">
function ${treeName}_showId(id, obj, action) {
<c:if test="${ssConfigJspStyle != 'template'}">
	//Build a url to go to
	var url = "<ssf:url action="ssActionPlaceHolder" actionUrl="false" ><ssf:param 
			name="binderId" value="ssBinderIdPlaceHolder"/></ssf:url>"
	var url = "<ssf:url crawlable="true" adapter="true" portletName="ss_forum"
																					action="ssActionPlaceHolder"
																					binderId="ssBinderIdPlaceHolder">
	<%--<ssf:param name="entityType" value="${mashupBinder.entityType}"/>--%>
	<ssf:param name="seen_by_gwt" value="1" />
	</ssf:url>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	window.top.ss_setContentLocation(url);
</c:if>
	return false;
}
</script>


<ssf:tree treeName="${treeName}" 
  treeDocument="${ssDashboard.beans[componentId].workspaceTree}" 
  topId="${ssDashboard.beans[componentId].topId}" 
  highlightNode="${ssDashboard.beans[componentId].topId}" 
  initOnly="true"/>


