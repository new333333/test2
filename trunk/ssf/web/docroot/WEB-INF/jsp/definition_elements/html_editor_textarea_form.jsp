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
<% // The html editor widget %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%

// TinyMCE options not current used:
//
//  plugins: "table,advimage,preview,contextmenu,paste,spellchecker", 
//  spellchecker_url: "/foxtrot/zone1/dispatch.cgi/_admin/mceSpellCheck", 
//  external_image_list_url: "/foxtrot/pics/mce_clipart.js", 


	String formName = (String) request.getAttribute("formName");
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String height = (String) request.getAttribute("property_height");
	if (height == null || height.equals("")) {
		height = "200";
	}
	if (Integer.valueOf(height).intValue() < 100) height = "100";
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br/>";
	}

	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
	
%>
<c:set var="textValue" value=""/>
<c:if test="${!empty ssDefinitionEntry}">
  <c:if test="${property_name == 'description'}" >
    <c:set var="textValue" value="${ssDefinitionEntry.description.text}"/>
  </c:if>
  <c:if test="${property_name != 'description'}" >
    <c:set var="textValue" value="${ssDefinitionEntry.customAttributes[property_name].value.text}"/>
  </c:if>
</c:if>
<div class="ss_entryContent">
  <span class="ss_labelLeft"><%= caption %><%= required %></span>
  <div>
  <ssf:htmleditor name="${property_name}"><ssf:markup type="form" 
    entity="${ssDefinitionEntry}"><c:out value="${textValue}"/></ssf:markup></ssf:htmleditor>
  </div>
</div>
