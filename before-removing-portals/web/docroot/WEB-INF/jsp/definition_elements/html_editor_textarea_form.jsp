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
<% // The html editor widget %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%

// TinyMCE options not current used:
//
//  plugins: "table,advimage,preview,contextmenu,paste,spellchecker", 
//  spellchecker_url: "/foxtrot/zone1/dispatch.cgi/_admin/mceSpellCheck", 
//  external_image_list_url: "/foxtrot/pics/mce_clipart.js", 


	String caption = (String) request.getAttribute("property_caption");
	String height = (String) request.getAttribute("property_height");
	if (height == null || height.equals("")) {
		height = "300";
	}
	height = height.replaceAll("px", "").trim();
	try {
		//Don't crash if someone put in a bogus value for the height
		if (Integer.valueOf(height).intValue() < 100) height = "100";
	}
	catch(Exception e) {}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b>";
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
  <c:if test="${property_name == 'branding'}" >
    <c:set var="textValue" value="${ssDefinitionEntry.branding}"/>
  </c:if>
  <c:if test="${property_name != 'description' && property_name != 'branding'}" >
    <c:set var="textValue" value="${ssDefinitionEntry.customAttributes[property_name].value.text}"/>
  </c:if>
  <c:if test="${!empty ss_sectionText}"><c:set var="textValue" value="${ss_sectionText}"/></c:if>
</c:if>
<div class="ss_entryContent">

  <span class="ss_labelLeft"><%= caption %><%= required %>   </span>
  	<div class="ss_editorHints" style="padding-left:10px;">
  	<c:if test="${!empty ss_html_editor_textarea_form_helpicon}" >
  		<c:if test="${!empty ss_html_editor_textarea_form_helpicon_prefix}">
  			<em><ssf:nlt tag="${ss_html_editor_textarea_form_helpicon_prefix}"/> </em>
  		</c:if>	
  		<ssf:inlineHelp tag="${ss_html_editor_textarea_form_helpicon}"/>
  	</c:if>
  	</div>

  <div>
  <ssf:htmleditor name="${property_name}" id="ss_htmleditor_${property_name}" 
    height="<%= height %>"><ssf:markup type="form" 
    entity="${ssDefinitionEntry}"><c:out value="${textValue}"/></ssf:markup></ssf:htmleditor>
  </div>
</div>
<c:set var="ss_html_editor_textarea_form_helpicon" value="" scope="request" />
<c:set var="ss_html_editor_textarea_form_helpicon_prefix" value="" scope="request" />