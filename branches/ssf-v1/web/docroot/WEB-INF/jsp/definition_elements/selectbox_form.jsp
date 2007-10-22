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
<% // The selectbox form element %>
<%@ page import="com.sitescape.team.web.util.DefinitionHelper" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String elementName = (String) request.getAttribute("property_name");
	request.setAttribute("selectboxName", elementName);
	String caption = (String) request.getAttribute("property_caption");
	String multiple = (String) request.getAttribute("property_multipleAllowed");
	if (multiple != null && multiple.equals("true")) {
		multiple = "multiple";
	} else {
		multiple = "";
	}
	String size = (String)request.getAttribute("property_size");
	if (size == null || size.equals("")) {
		size = "";
	} else {
		size = "size='" + size + "'";
	}
	if (caption != null && !caption.equals("")) {
		caption += "<br>\n";
	}
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
	
	// get form type
	String formType = "";
	if(ssConfigDefinition != null) {
		formType = DefinitionHelper.findFormType(ssConfigDefinition);
	}
	
%>
<c:set var="formType" value="<%= formType %>"/>

<c:if test="${formType == 'task'}">
<script type="text/javascript" src="<html:rootPath/>js/common/ss_tasks.js"></script>
</c:if>

<div class="ss_entryContent">
<div class="ss_labelLeft"><%= caption %><%= required %></div><select 
  name="<%= elementName %>" <%= multiple %> <%= size %>
  <c:if test="${formType == 'task'}">
  	onchange="ss_tasks.adjustFormAttributes(this.name);"
  </c:if>>
  >
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />
</select>
</div>
