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
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
	<c:if test="${!empty ss_prevPage}">
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
			action="__ajax_mobile" 
			operation="mobile_find_places" 
			actionUrl="false" ><ssf:param 
			name="searchText" value="${ss_searchText}"/><ssf:param 
			name="pageNumber" value="${ss_prevPage}"/></ssf:url>"
		><img border="0" src="<html:rootPath/>images/mobile/nl_left_20.png"/></a>
	</c:if>
	<c:if test="${empty ss_prevPage}">
	  <img border="0" src="<html:rootPath/>images/mobile/nl_left_dis_20.png"
	  	<ssf:alt tag=""/> />
	</c:if>

	<c:if test="${!empty ss_nextPage}">
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
			action="__ajax_mobile" 
			operation="mobile_find_places" 
			actionUrl="false" ><ssf:param 
			name="searchText" value="${ss_searchText}"/><ssf:param 
			name="pageNumber" value="${ss_nextPage}"/></ssf:url>"
		><img border="0" src="<html:rootPath/>images/mobile/nl_right_20.png"/></a>
	</c:if>
	<c:if test="${empty ss_nextPage}">
	  <img border="0" src="<html:rootPath/>images/mobile/nl_right_dis_20.png"
	 	<ssf:alt tag=""/> />
	</c:if>
