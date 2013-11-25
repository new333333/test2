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
//This file is included for summary portlets, dashboards and ajax callbacks for both
//In the portlet case include.jsp has already been called and include files have been handled
//In the dashboard case if not in an adapter, the variables to skip the includes are setup
//In the ajax callback case, we don't want to send the includes
//So in all cases at this point, all files included from "include.jsp" are handled.
//Set flags to skip them, but still need rest of the setup in "include.jsp"
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ssf_snippet" value="1" scope="request"/>
<c:set var="ssf_support_files_loaded" value="1" scope="request"/>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:if test="${empty ss_namespace}">
<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
</c:if>
<c:set var="componentId" value="${ssComponentId}" scope="request"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" scope="request"/>
</c:if>

<c:set var="ss_divId" value="ss_results_${ss_namespace}_${componentId}"  scope="request"/>
