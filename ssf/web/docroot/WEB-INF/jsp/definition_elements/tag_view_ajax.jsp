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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_tagViewNamespace" value="${ss_namespace}" scope="request"/>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
  <taconite-replace contextNodeID="<c:out value="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}"/>" 
    parseInBrowser="true">
	<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data_cloud.jsp" />
  </taconite-replace>
  <taconite-replace contextNodeID="<c:out value="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_data_pane_p"/>" 
    parseInBrowser="true">
	<c:set var="ssTags" value="${ssPersonalTags}" scope="request" />
	<c:set var="ssTagsType" value="p" scope="request" />
	<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
  </taconite-replace>
  <taconite-replace contextNodeID="<c:out value="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_data_pane_c"/>" 
    parseInBrowser="true">
	<c:set var="ssTags" value="${ssCommunityTags}" scope="request" />
	<c:set var="ssTagsType" value="c" scope="request" />
	<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
  </taconite-replace>
</c:if>
</taconite-root>
