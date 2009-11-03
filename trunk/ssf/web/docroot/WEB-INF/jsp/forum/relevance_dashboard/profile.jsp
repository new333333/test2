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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${!empty ssConfigDefinition}">
	<div id="ss_dashboard_content">
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="${ssRelevanceDashboardConfigElement}" 
	  configJspStyle="${ssConfigJspStyle}"
	  entry="${ssDefinitionEntry}" />
	</div><!-- end of content -->
	<div class="ss_clear_float"></div>
</c:if>

<ssf:ifLoggedIn>
  <c:if test="${ssUser == ssDefinitionEntry}">
	<c:if test="${ss_quotasEnabled}">
	  <div>
	    <span><ssf:nlt tag="quota.diskQuota"><ssf:param name="value" value="${ssUser.diskQuota}"/></ssf:nlt></span>
	  </div>
	  <div>
	    <span><ssf:nlt tag="quota.diskSpaceUsed"><ssf:param name="value" useBody="true"
	    ><fmt:formatNumber value="${ssUser.diskSpaceUsed/1048576}" maxFractionDigits="2"/></ssf:param></ssf:nlt></span>
	  </div>
	  <c:if test="${ss_diskQuotaExceeded}">
	    <div>
	      <span class="ss_errorLabel"><ssf:nlt tag="quota.diskQuotaNowExceeded"/></span>
	    </div>
	  </c:if>
	  <c:if test="${ss_diskQuotaHighWaterMarkExceeded && !ss_diskQuotaExceeded}">
	    <div>
	      <span><ssf:nlt tag="quota.diskQuotaAlmostExceeded"/></span>
	    </div>
	  </c:if>
	</c:if>
  </c:if>
</ssf:ifLoggedIn>
