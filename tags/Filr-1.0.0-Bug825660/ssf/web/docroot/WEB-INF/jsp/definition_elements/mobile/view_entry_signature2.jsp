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
%><%--
--%><% //Entry signature view %><%--
--%><%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_entryContent">
	<div>
		<%@ include file="/WEB-INF/jsp/definition_elements/mobile/view_entry_creator.jsp" %>
	</div>
	<div>
	  <c:set var="property_caption" value=""/>
		<%@ include file="/WEB-INF/jsp/definition_elements/mobile/view_entry_date.jsp" %>
	</div>

	<c:if test="${!empty ss_thisWasTurnedOff}">
	<c:if test="${!empty ssDefinitionEntry.modification.principal && 
	  ssDefinitionEntry.modification.date > ssDefinitionEntry.creation.date}"> 
		<div style="border:none;">
		  <span class="ss_mobile_light ss_mobile_small"><ssf:nlt tag="entry.modifiedBy"/></span>
		  <a style="text-decoration: none !important;" href="<ssf:url adapter="true" portletName="ss_forum" 
			action="__ajax_mobile"
			operation="mobile_show_workspace"
			binderId="${ssDefinitionEntry.modification.principal.workspaceId}" />"
		  ><span class="ss_mobile_light ss_mobile_small"
		  ><ssf:userTitle user="${ssDefinitionEntry.modification.principal}"/></span></a>
		</div>
		<div>
		<span class="ss_mobile_light ss_mobile_small"><fmt:formatDate 
			timeZone="${ssUser.timeZone.ID}"
			value="${ssDefinitionEntry.modification.date}" type="both" 
			timeStyle="short" dateStyle="medium" /></span>
		</div>
	
	</c:if>
	</c:if>
	
	<c:if test="${!empty ssDefinitionEntry.reservation.principal}">
		<div>
			<span><img <ssf:alt tag="alt.locked"/> 
				src="<html:imagesPath/>pics/sym_s_caution.gif"/>
				<ssf:nlt tag="entry.reservedBy"/>&nbsp;
				<ssf:showUser user="${ssDefinitionEntry.reservation.principal}"/>
			</span>
		</div>
	</c:if>
</div>
