<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% //Date widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.kablink.teaming.util.CalendarHelper" %>

<c:if test="${empty ssReadOnlyFields[property_name]}">

<div class="ss_entryContent">
<span class="ss_labelAbove" id='${property_name}_label'>
${property_caption}<c:if test="${property_required}"><span class="ss_required">*</span></c:if></span>
	
	<c:set var="initDate" value="<%= new Date() %>"/>
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
		<c:set var="initDate" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
		<c:set var="property_initialSetting" value="entry"/>
	</c:if>

	<br/>
	<input type="text" 
		id="date_${property_name}_${prefix}" 
		name="${property_name}_fullDate" 
		<c:if test="${property_initialSetting != 'none'}">
		  value="<fmt:formatDate value="${initDate}" pattern="yyyy-MM-dd" 
			timeZone="${ssUser.timeZone.ID}"/>"
		</c:if>
		<c:if test="${property_initialSetting == 'none'}">
		  value=""
		</c:if>
	/>
	<div><span class="ss_mobile_small"><ssf:nlt tag="mobile.dateFormat"/></span></div>

		<input type="hidden" name="${property_name}_timezoneid" value="${ssUser.timeZone.ID}" />
		<input type="hidden" name="${property_name}_skipTime" value="false" />
		<input type="hidden" name="${property_name}" value="" />
		
</div>
</c:if>
<c:if test="${!empty ssReadOnlyFields[property_name]}">

<div class="ss_entryContent">
<span class="ss_labelAbove">${property_caption}</span>
<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
<fmt:formatDate value="${ssDefinitionEntry.customAttributes[property_name].value}" pattern="yyyy-MM-dd" 
			timeZone="${ssUser.timeZone.ID}"/>
</c:if>
</div>
</c:if>