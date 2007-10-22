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
<% //Event widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<%
	Event initEvent = new Event();

	//Get the formName of event being displayed
	Element item = (Element) request.getAttribute("item");
	Element nameProperty = (Element) item.selectSingleNode("properties/property[@name='name']");
	String elementName = "event";
	if (nameProperty != null) {
		elementName = nameProperty.attributeValue("value", "event");
	}
	String formName = (String) request.getAttribute("formName");
	String caption = (String) request.getAttribute("property_caption");
	String hD = (String) request.getAttribute("property_hasDuration");
	String hR = (String) request.getAttribute("property_hasRecurrence");
	Boolean hasDur = new Boolean(hD);
	Boolean hasRecur = new Boolean(hR);
	if (caption == null) {caption = "";}
	String required = (String) request.getAttribute("property_required");
	Boolean req = Boolean.parseBoolean(required);
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<div class="ss_entryContent">
<div class="ss_labelAbove" id='<%= elementName %>_label'><%= caption %><%= required %></div>
<div id="<%= elementName %>_startError" style="visibility:hidden; display:none;"><span class="ss_formError"><ssf:nlt tag="validation.startDateError"/></span></div>
<div id="<%= elementName %>_endError" style="visibility:hidden; display:none;"><span class="ss_formError"><ssf:nlt tag="validation.endDateError"/></span></div>
<c:choose>
	<c:when test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
		<c:set var="ev" value="${ssDefinitionEntry.customAttributes[property_name].value}" />	
	</c:when>
	<c:when test="${!empty ssInitialEvent}">
		<c:set var="ev" value="${ssInitialEvent}" />	
	</c:when>	
</c:choose>

<ssf:eventeditor id="<%= elementName %>" 
         formName="<%= formName %>" 
         initEvent="${ev}"
         required="<%= req %>"
         hasDuration="<%= hasDur %>"
         hasRecurrence="<%= hasRecur %>" />
</div>
</div>
