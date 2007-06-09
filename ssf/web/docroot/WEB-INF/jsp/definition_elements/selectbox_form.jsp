<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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
  configJspStyle="${ssConfigJspStyle}" />task
</select>
</div>
