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

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<c:set var="ss_showUnseenNote" value="0"/>
	<c:forEach var="folderId" items="${ss_unseenCountsBinderIds}">
	  <c:set var="folderIdFound" value="0"/>
	  <c:forEach var="entry" items="${ss_unseenCounts}">
	    <c:if test="${entry.key.id == folderId}">
	      <c:set var="folderIdFound" value="1"/>
	    </c:if>
	  </c:forEach>
	  <c:if test="${folderIdFound == '0'}">
	    <taconite-replace contextNodeID="${ssNamespace}_count_${folderId}" 
	    parseInBrowser="true"><span id="${ssNamespace}_count_${folderId}" 
	    >*</span></taconite-replace>
	    <c:set var="ss_showUnseenNote" value="1"/>
	  </c:if>
	</c:forEach>
	
	<c:forEach var="entry" items="${ss_unseenCounts}">
	<taconite-replace contextNodeID="${ssNamespace}_count_${entry.key.id}" 
	parseInBrowser="true"><span id="${ssNamespace}_count_${entry.key.id}" 
	>${entry.value}</span></taconite-replace>
	</c:forEach>
	<c:if test="${ss_showUnseenNote == '1'}">
	  <taconite-replace contextNodeID="${ssNamespace}_note" parseInBrowser="true">
	    <div id="${ssNamespace}_note" class="ss_indent_large">
	    <span>* <ssf:nlt tag="unseen.foldersOnly"/></span>
	    </div>
	  </taconite-replace>
	</c:if>
</c:if>
</taconite-root>
