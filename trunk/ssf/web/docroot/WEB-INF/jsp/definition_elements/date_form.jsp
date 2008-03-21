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
<%@ page import="com.sitescape.team.util.CalendarHelper" %>
<%
	//Get the formName of date being displayed
	Element item = (Element) request.getAttribute("item");
	Element nameProperty = (Element) item.selectSingleNode("properties/property[@name='name']");
	String elementName = "date";
	if (nameProperty != null) {
		elementName = nameProperty.attributeValue("value", "date");
	}
	String formName = (String) request.getAttribute("formName");
	String name = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	if (caption != null && !caption.equals("")) {
		caption = caption;
	}
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<c:set var="elementName" value="<%= elementName %>" />
<div class="ss_entryContent">
<span class="ss_labelAbove" id='<%= elementName %>_label'><%= caption %><%= required %></span>
<div id="<%= elementName %>_error" style="visibility:hidden; display:none;">
  <span class="ss_formError">Please enter a valid date.</span>
</div>

	
	
	<c:set var="initDate" value="<%= new Date() %>"/>
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
		<c:set var="initDate" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
		<c:set var="property_initialSetting" value="entry"/>
	</c:if>

	<div dojoType="DropdownDatePickerActivateByInput" 
		widgetId="date_${elementName}_${prefix}" 
		id="date_${elementName}_${prefix}"
		name="${elementName}_fullDate" 
		lang="<ssf:convertLocaleToDojoStyle />" 
		weekStartsOn="<%= CalendarHelper.getFirstDayOfWeek() - 1 %>"
		<c:if test="${property_initialSetting != 'none'}">
		  value="<fmt:formatDate value="${initDate}" pattern="yyyy-MM-dd" 
			timeZone="${ssUser.timeZone.ID}"/>"
		</c:if>
		<c:if test="${property_initialSetting == 'none'}">
		  value=""
		</c:if>
	></div>

		<input type="hidden" name="${elementName}_timezoneid" value="${ssUser.timeZone.ID}" />
		<input type="hidden" name="${elementName}_skipTime" value="false" />
		
	<script type="text/javascript">
		dojo.require("sitescape.widget.DropdownDatePickerActivateByInput");
		djConfig.searchIds.push("date_${elementName}_${prefix}");
	</script>
</div>
