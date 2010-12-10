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
<% //Event widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	String caption1 = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption1});
%>

<div class="ss_entryContent">
	<div class="ss_labelAbove" id='${property_name}_label'>
	${property_caption}<c:if test="${property_required}"><span 
	  id="ss_required_${property_name}" title="<%= caption2 %>" class="ss_required">*</span></c:if></div>
	<div id="${property_name}_startError" style="visibility:hidden; display:none;"><span 
	  class="ss_formError"><ssf:nlt tag="validation.startDateError"/></span></div>
	<div id="${property_name}_endError" style="visibility:hidden; display:none;"><span 
	  class="ss_formError"><ssf:nlt tag="validation.endDateError"/></span></div>
	<c:choose>
		<c:when test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
			<c:set var="ev" value="${ssDefinitionEntry.customAttributes[property_name].value}" />	
		</c:when>
		<c:when test="${!empty ssInitialEvent}">
			<c:set var="ev" value="${ssInitialEvent}" />	
		</c:when>	
	</c:choose>
	<ssf:eventeditor id="${property_name}" 
	     formName="${formName}" 
	     initEvent="${ev}"
	     required="${property_required}"
	     hasDuration="${property_hasDuration}"
	     hasRecurrence="${property_hasRecurrence}" 
	     isTimeZoneSensitiveActive="${property_timeZoneSensitiveActive}"
	     isFreeBusyActive="${property_freeBusyActive}" 
	     mobile="true"
	/>
</div>
