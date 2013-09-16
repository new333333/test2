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
<% //Date widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.kablink.teaming.util.CalendarHelper" %>
<c:if test="${empty ssReadOnlyFields[property_name]}">
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	String caption1 = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption1});
%>

<div class="ss_entryContent">
	<span class="ss_labelAbove" id='${property_name}_label'>
	${property_caption}<c:if test="${property_required}"><span 
	  id="ss_required_${property_name}" title="<%= caption2 %>" class="ss_required">*</span></c:if></span>

	<c:set var="initDate" value="<%= new Date() %>"/>
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
		<c:set var="initDate" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
		<c:set var="property_initialSetting" value="entry"/>
	</c:if>

	<table cellpadding="0" border="0">
		<tr>
			<td valign="top">
    			<c:set var="ss_dateWidgetId" value="${property_name}" scope="request"/>
				<c:set var="ss_dateWidgetDate" value="" scope="request"/>
				<c:if test="${property_initialSetting != 'none'}">
      			  <c:set var="ss_dateWidgetDate" value="${initDate}" scope="request"/>
    			</c:if>
    			<%@ include file="/WEB-INF/jsp/mobile/date_widget.jsp" %>
			</td>
			<td valign="top">
				<input type="text" size="7"
					id="date_time_${property_name}_${prefix}" 
					name="${property_name}_0_fullTime" 
					<c:if test="${property_initialSetting != 'none'}">
					  value="<fmt:formatDate value="${initDate}" 
					    pattern="HH:mm" timeZone="${ssUser.timeZone.ID}"/>"
					</c:if>
					<c:if test="${property_initialSetting == 'none'}">
					  value=""
					</c:if>
				>
				<div><span class="ss_mobile_small"><ssf:nlt tag="mobile.timeFormat"/></span></div>
				<input type="hidden" name="${property_name}_timezoneid" value="${ssUser.timeZone.ID}" />
			</td>
		</tr>
	</table>
	
	<input type="hidden" name="${property_name}" value="" />
	
</div>
</c:if>
<c:if test="${!empty ssReadOnlyFields[property_name]}">
<div class="ss_entryContent">
<span class="ss_labelAbove">${property_caption}</span>
<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				      value="${ssDefinitionEntry.customAttributes[property_name].value}" 
				      type="both" dateStyle="medium" timeStyle="short" /> &#134;
</c:if>
</div>
</c:if>