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
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<taconite-replace contextNodeID="ss_indexing_done_div" parseInBrowser="true">

<div id="ss_indexing_done_div" class="teamingDlgBox" style="position:absolute;display:none;">
	<div class="popupContent" style="padding: 20px;">
		<span class="ss_bold"><ssf:nlt tag="index.finished"/></span>
		<div class="margintop1">
		  <c:if test="${!empty ssErrorIndexingCount}">
			<ssf:nlt tag="index.errorsFound"><ssf:param name="value" value="${ssErrorIndexingCount}"/></ssf:nlt>
		  </c:if>
		</div>
		<c:if test="${!empty ssErrorIndexingStrings}">
			<div class="margintop1" style="padding-left:12px;">
			    <span class="ss_bold"><ssf:nlt tag="index.generalErrors"/></span>
			    <br/>
			    <c:forEach var="errorString" items="${ssErrorIndexingStrings}">
			    	<div class="ss_normal" style="padding:4px 0px 0px 6px;">${errorString}</div>
			    </c:forEach>
			</div>
		</c:if>
		<div class="margintop3" style="text-align: center;"><input type="button" value="<ssf:nlt tag="button.close"/>" onClick="return handleCloseBtn();" /></div>
	</div>
</div>

</taconite-replace>

</taconite-root>
