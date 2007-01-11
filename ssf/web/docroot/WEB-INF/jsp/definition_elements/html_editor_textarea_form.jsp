<% // The html editor widget %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<script type="text/javascript" src="<html:rootPath/>js/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript">tinyMCE.init(
 {mode: "specific_textareas", editor_selector: "mceEditable",
  language: "en", 
  content_css: "<html:rootPath/>css/editor.css", 
  relative_urls: false, accessibility_focus: false,
  remove_script_host: false,
  plugins: "table,advimage,preview,contextmenu,paste", 
  theme_advanced_toolbar_location: "top", theme_advanced_toolbar_align: "top", 
  theme_advanced_toolbar_align: "left", theme_advanced_statusbar_location: "bottom", 
  theme_advanced_resizing: true, 
  theme_advanced_buttons2_add: "pastetext,pasteword,advimage,spellchecker",
  theme_advanced_buttons3_add: "tablecontrols", 
  theme_advanced_resizing_use_cookie : false});</script>

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
		caption = "<b>"+caption+"</b><br>";
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
<div style="border:1px solid #CECECE;">
<textarea class="ss_style mceEditable" rows="20" cols="80"
  name="${property_name}"><c:out value="${textValue}"/></textarea>
</div>
