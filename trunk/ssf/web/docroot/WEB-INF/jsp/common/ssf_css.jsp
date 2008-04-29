<%
/* *
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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<c:if test="${!empty ss_loadCssStylesInline && ss_loadCssStylesInline == true}">
<%@ page contentType="text/css" %>
</c:if>
<%
//Color themes: 
//    icib - ICEcore Icy Blue  (default, fallback)
//    iccg - ICEcore Cool Green
//    icwg - ICEcore Wintry Gray
//    cust - Custom
%>
<c:set var="ss_color_theme" value="icib" scope="request"/>
<c:if test="${!empty ssCssTheme}">
  <c:set var="ss_color_theme" value="${ssCssTheme}" scope="request"/>
</c:if>
<c:choose>
 <c:when test="${ss_color_theme == 'iccg'}">
  <jsp:include page="/WEB-INF/jsp/common/css_theme_defaultgreen.jsp" />
 </c:when>
 <c:when test="${ss_color_theme == 'icwg'}">
  <jsp:include page="/WEB-INF/jsp/common/css_theme_defaultgray.jsp" />
 </c:when>
 <c:when test="${ss_color_theme == 'cust'}">
  <jsp:include page="/WEB-INF/jsp/common/css_theme_cust.jsp" />
 </c:when>
 <c:otherwise>
  <jsp:include page="/WEB-INF/jsp/common/css_theme_defaultblue.jsp" />
 </c:otherwise>
</c:choose>
<c:if test="${empty ss_skipCssStyles || ss_skipCssStyles != true}">
.ss_portlet_style {
<c:if test="${!empty ss_portlet_style_background_color}">
  background-color: ${ss_portlet_style_background_color};
</c:if>
<c:if test="${!empty ss_portlet_style_text_color}">
  color: ${ss_portlet_style_text_color};
</c:if>
<c:if test="${ss_portlet_style_inherit_font_specification}">
  font-family: ${ss_style_font_family};
  font-weight: inherit;
  font-size: ${ss_style_font_size}; 
</c:if>
}

<c:if test="<%= isIE %>">
html { filter: expression(document.execCommand("BackgroundImageCache", false, true)); } 
</c:if>


body.ss_style_body {
	margin: 0px 0px 0px 0px;
	background: none;
}

.ss_style {
  font-family: ${ss_style_font_family};
  font-weight: inherit;
  font-size: ${ss_style_font_size}; 
  background-color: ${ss_style_background_color};
  color: ${ss_style_text_color};
}

.ss_style td, .ss_style th {
  font-family: ${ss_style_folder_view_font_family};
  font-size: 12px; 
  color: ${ss_style_text_color};
}

.ss_style_trans , .ss_style_trans td, .ss_style_trans th{
  font-family: arial, sans serif;
  font-weight: inherit;
  font-size: ${ss_style_font_size}; 
  background-color: transparent;
  color: ${ss_style_text_color};
}

.ss_style img {
  border:0px none;
}
.ss_style ul li {
  list-style:none;
  list-style-image: url(<html:imagesPath/>pics/1pix.gif);
}
  
/* LINKS */
.ss_style a {
  color: ${ss_style_link_color};
  text-decoration: none;
<ssf:ifnotaccessible>
  outline: none;
</ssf:ifnotaccessible>  
}
.ss_style a:visited {
  color: ${ss_style_link_visited_color};
}
.ss_style a:hover {
  color: ${ss_style_link_hover_color};
  text-decoration: none;
<ssf:ifaccessible>
  outline: dotted 1px gray;
</ssf:ifaccessible>  
}
.ss_title_link {
  text-decoration:underline;
}

div.ss_entryContent a {
  text-decoration:underline;
}

a img.ss_icon_link {
  border: 0px solid black;
}

a.ss_download_link {
	border: 0px solid #666666;
	font-size: ${ss_style_font_finestprint};
	background-image: url(<html:imagesPath/>icons/accessory_move_down.gif);
	background-repeat: no-repeat;
    background-position:  left center;
	padding-left: 15px;
	padding-right: 0px;
	line-height: 11px;
}

.ss_largestprint {
  font-size: ${ss_style_font_largestprint} !important; 
}  
.ss_largerprint {
  font-size: ${ss_style_font_largerprint} !important; 
}
.ss_largeprint {
  font-size: ${ss_style_font_largeprint} !important; 
}
.ss_normalprint {
  font-size: ${ss_style_font_normalprint} !important; 
}
.ss_smallprint {
  font-size: ${ss_style_font_smallprint} !important; 
}
.ss_fineprint {
  font-size: ${ss_style_font_fineprint} !important; 
}
.ss_finestprint {
  font-size: ${ss_style_font_finestprint} !important; 
}

<%-- 
  -- Rich text editor font styles 
  -- Must match equivalent declarations in editor_css.jsp
  --%>
.ss_size_8px  { font-size: 8px  !important;}
.ss_size_9px  { font-size: 9px  !important;}
.ss_size_10px { font-size: 10px !important;}
.ss_size_11px { font-size: 11px !important;}
.ss_size_12px { font-size: 12px !important;}
.ss_size_13px { font-size: 13px !important;}
.ss_size_14px { font-size: 14px !important;}
.ss_size_15px { font-size: 15px !important;}
.ss_size_16px { font-size: 16px !important;}



.ss_nowrap {
  white-space: nowrap;
}
.ss_transparent {
  background-color: transparent !important;
}

.ss_brightest {
 opacity: ${ss_style_brightest};
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=${(ss_style_brightest*100)});
 </c:if>
}
.ss_brighter {
 opacity: ${ss_style_brighter};
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=${(ss_style_brighter*100)});
 </c:if>
}
.ss_bright {
 opacity: ${ss_style_bright};
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=${(ss_style_bright*100)});
 </c:if>
}
.ss_dim {
 opacity: ${ss_style_dim};
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=${(ss_style_dim*100)});
 </c:if>
}
.ss_very_dim {
 opacity: ${ss_style_very_dim};
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=${(ss_style_very_dim*100)});
 </c:if> 
}

.ss_gray {
  color: ${ss_style_gray_color};   
  }

.ss_light {
  color: ${ss_style_light_color};   
  }

.ss_border_light {
  border: 1px ${ss_style_border_color} solid;
  }

.ss_shadowbox {
  background-color: #CCCCCC;
  position: relative;
  top: 2px;
  left: 2px;
}
.ss_shadowbox2 {
  position: relative;
  top: -2px;
  left: -2px;
}

ul.ss_icon_list li {
  display: inline;
  padding-right: 25px;
  list-style-type: none;
}


.ss_form, .ss_form table, .ss_style form {
  color: ${ss_form_text_color};
  background-color: ${ss_form_background_color};
  }
    
.ss_replies_background {
  background-color: ${ss_replies_background_color} !important;
  }
      
div.ss_replies div.ss_entryContent {
	margin-left: 30px;
	margin-top: 2px;
}

div.ss_replies div.ss_entryDescription {
	background-color: ${ss_replies_background_color};
}

div.ss_entryDescription.ss_entryContent table {
	border-collapse: separate;
}

.ss_style_color,.ss_style_color table, .ss_style form {
  color: ${ss_style_text_color};
  background-color: ${ss_style_background_color};
 
  }

.ss_content_rule {
	border-bottom: 1px solid #CCC;
	padding-bottom: 3px;
}

.ss_style_color {
	
}
.ss_form_color {
  color: ${ss_form_text_color};
  background-color: ${ss_form_background_color};
  }
    
.ss_form.ss_gray {
  color: #333333;
  }

.ss_form select, .ss_form option {
  background-color: ${ss_form_element_color};
  color: ${ss_form_element_text_color};
  }

div.ss_style input[type="text"],
div.ss_style input[type="password"],
div.ss_style textarea,
div.ss_style select,
div.ss_global_toolbar_quick input {
  background-color: #FFFFFF;
  background-image: none;
  color: ${ss_form_element_text_color};
  font-size: ${ss_style_font_fineprint};
  font-family: ${ss_style_folder_view_font_family};  
  border: solid .01em #999;
}
div.ss_style textarea {
  padding: 0px 0px 0px 2px;
}
div.ss_style input[type="text"],
div.ss_style input[type="password"],
div.ss_style select,
div.ss_global_toolbar_quick input {
  padding: 2px;
}

  
.ss_form textarea {
  background-color: ${ss_form_element_color};
  background-image: none;
  color: ${ss_form_element_text_color};
  padding: 0px;
  font-size: ${ss_style_font_normalprint};
  font-family: ${ss_style_folder_view_font_family};  
  border-style: solid;
  border-width: 1px;
  border-color: ${ss_style_text_field_border_color};
}


.ss_form input.ss_text { 
  background-color: ${ss_form_element_color};
  background-image: none;
  color: ${ss_form_element_text_color};
  padding: 0px;
  font-size: ${ss_style_font_normalprint};
  font-family: ${ss_style_folder_view_font_family};  
  }
.ss_readonly {
  background-color: ${ss_form_element_readonly_color};
  }
    
.ss_text_field {
  border:1px solid ${ss_style_text_field_border_color} !important;
  background-color: ${ss_style_text_field_background_color};
  padding: 0px;
  font-size: ${ss_style_font_smallprint};
  font-family: ${ss_style_folder_view_font_family};  
  }




fieldset.ss_fieldset {
  margin-top: 6px;
  margin-bottom: 4px;
  border: 1px solid ${ss_style_border_color_light};
}
  
fieldset.ss_fieldset_square {
  border: 1px solid ${ss_style_border_color_light};
  padding: 5px;
  margin-top: 12px;
}

legend.ss_legend {
  font-family: ${ss_style_font_family};
  font-weight: bold;
  font-size: ${ss_style_font_size}; 
  color: ${ss_style_text_color};
}

legend.ss_legend_bold {
  font-family: ${ss_style_font_family};
  font-weight: bold;
  font-size: ${ss_style_font_size}; 
  color: ${ss_style_text_color};
}

.ss_formInstructions { 
  border-color: #cccccc; 
  border-style: solid; 
  border-width: 1px; 
  font-size: smaller;
  padding: 2px;
}

div.ss_editorHints {
  font-size: ${ss_style_font_smallprint} !important; 
}


/* entry styles: common to entry views and entry forms */
.ss_entryTitle {
  font-weight: bold;
  font-size: ${ss_style_font_largerprint};
  font-family: ${ss_style_title_font_family};
  margin-bottom: 4px;
  margin-top: 4px;
  }
/* container for entry content: description, signature, attachments, data gathered from entry form */
.ss_entryContent {
  margin: 0px;
  padding: 2px;
  padding-left: 22px;
  background-color:inherit;
  }
.ss_entryContent table {
  background-color:inherit;
  }
.ss_entrySignature {
  font-size: 10px;
  padding-left: 5px;
  color: ${ss_style_metadata_color};
  
  }
.ss_entryDescription {
  padding-left: 24px;
  padding-right: 14px;
  padding-top: 4px;
  padding-bottom: 4px;
  background-color: inherit;
  border: 1px transparent solid;
  font-size: 12px;
  color: ${ss_style_muted_foreground_color};
}
.ss_entryDescription p {
  margin-top: 2px;
  margin-bottom: 10px;
}


div.ss_entryDescription ul li {
    margin: 2px 0px 0px 25px;
    list-style-type: square;
    list-style-position: outside;
    list-style-image: none;
}

div.ss_entryDescription ol li {
    margin: 2px 0px 0px 25px;
    list-style-position: outside;
    list-style-type: decimal;
}


div.ss_editableTab {
  float: right;
  background-color: ${ss_style_background_color_opaque};
  margin-left: 5px;
  margin-top: -4px;
  padding-bottom: 3px;
  padding-right: 5px;
  padding-left: 5px;
}

div.ss_editableTab a, div.ss_editableTab a:hover {
  text-decoration: none;
}


div.ss_replies .ss_editableTab {
	background-color: ${ss_replies_background_color};
}

div.ss_workflow { 
  border: 1px #666666 solid;
  padding: 2px;
}



.ss_replies .ss_entryDescription {
  padding-left: 15px;
  padding-right: 15px;
  padding-top: 5px;
  padding-bottom: 5px;
}
   
.ss_entryDescriptionLead {
	display: inline;
}

.ss_replies .ss_entryDescriptionLead {
	display: inline;
	background-image: url(<html:imagesPath/>icons/comment.png);
	height: 16px;
	width: 16px;
	float: left;
	margin-left: 11px;
	margin-top: 3px;
}

.ss_blockquote_watermark {
	background-color: transparent;
	background-image: url(<html:imagesPath/>pics/watermark_blockquote_blue.gif);
    background-position: top left;
    background-repeat: no-repeat;
    padding: 7px 6px 33px 20px;
}
.ss_blockquote_watermark_content {
	margin-top: -25px;
	padding-left: 10px;
}

div.ss_blockquote_start p, div.ss_blockquote_watermark_content p {
	margin-top: 0px;
}
      
table.ss_guestbook {
	background-color: ${ss_entry_description_background_color};
	margin-bottom: 3px;
	padding: 5px 0px 0px 5px;
}

/* Text styled as buttons */

.ss_inlineButton {
  cursor: pointer;
  display: block;
 <c:if test="<%= isIE %>">
  height: 20px;
 </c:if>
 <c:if test="<%= !isIE %>">
  height: 18px;
 </c:if>
  line-height: 18px;
  text-align: center;
  padding: 0px 3px 0px 3px;
  font-size: 9px !important;
  font-family: Arial, sans-serif;
  white-space: nowrap;
  text-decoration: none !important;
  border: 1px solid #AFC8E3;
  background-image: url(<html:imagesPath/>pics/background_inline_button_blue.gif);
  background-repeat: repeat-x;
}
.ss_inlineButton:hover {
  border: 1px solid #666666;
}
.ss_inlineButton span {
  padding: 0px 5px 0px 5px;
  text-align: center;
}
.ss_inlineButton img {
  margin: 0px 0px -5px 0px;
}

.ss_tinyControl {
  font-size: 9px !important;
  font-family: sans-serif;
  padding: 3px 15px 3px 15px !important;
  margin: 1px 2px 1px 2px !important;
  line-height: 100% !important;
}



input.ss_linkButton, input.ss_submit, a.ss_linkButton:link, a.ss_linkButton:visited, a.ss_dashboard_config_control {
  font-family: ${ss_style_folder_view_font_family};
  font-size: ${ss_style_font_fineprint};
  background-color: ${ss_linkbutton_background_color};
  border: 1px solid ${ss_linkbutton_outline_color};
  padding: 0px 6px 0px 6px;
  cursor: pointer;
  white-space: nowrap;
}

a.ss_dashboard_config_control {
}


a.ss_linkButton:focus, a.ss_linkButton:hover {
  color: ${ss_linkbutton_link_hover_color};
  font-family: ${ss_style_folder_view_font_family};
  font-size: ${ss_style_font_fineprint};
  background-color: ${ss_linkbutton_background_color};
  border: 1px solid ${ss_linkbutton_outline_color};
  padding: 0px 6px 0px 6px;
  cursor: pointer;
  white-space: nowrap;
}

/* styles for labels: required for forms; optional for views */
.ss_labelAbove {
  padding-top: 2px;
  padding-bottom: 2px;
  display: block;
  font-weight: bold;
}

.ss_labelLeft {
  font-weight: bold;
  font-size: ${ss_style_font_normalprint};
  display: inline;
  padding-right: 2px;
}

.ss_labelRight {
  font-weight: bold;
  display: inline;
  padding-left: 2px;
  font-size: ${ss_style_font_normalprint};
}

div.ss_iconed_label {
  font-family: ${ss_style_title_font_family};
  background-repeat: no-repeat;
  background-position: 13px 50%;
  padding-left: 30px;
  padding-right: 4px;
  padding-top:	6px;
  padding-bottom: 6px;
  margin-left: 2px;
  font-size: 11px;
  font-weight: bold;
  line-height: 16px;
  color: ${ss_style_muted_label_color};
}

div.ss_iconed_label a {
  font-size: 11px;
  font-weight: bold;
  color: ${ss_style_muted_label_color};
}
div.ss_iconed_label hover {
  font-size: 11px;
  font-weight: bold;
  color: ${ss_style_muted_label_color};
}
.ss_muted_label_small {
  font-size: 11px;
  color: ${ss_style_muted_label_color};
}

.ss_muted_cloud_tag {
  color: ${ss_style_muted_tag_color};
   font-size: 11px;
}
.ss_muted_tag_cloud {
  width: 70%;
 
}


div.ss_add_tag {
  background-image: url(<html:imagesPath/>icons/add_tag.gif);
}

div.ss_add_comment {
  background-image: url(<html:imagesPath/>icons/add_comment.gif);
}

div.ss_view_something {
  background-image: url(<html:imagesPath/>icons/view_something.gif);
}

div.ss_subscribe {
  background-image: url(<html:imagesPath/>icons/send_friend.gif);
  padding-left: 19px;
}

div.ss_send_friend {
  background-image: url(<html:imagesPath/>icons/send_friend.gif);
  padding-left: 19px;
}


.ss_popupMenu {
  position:absolute;
  border:1px solid black;
  margin:2px;
  padding:2px;
  background-color: ${ss_style_background_color_opaque};
  }
.ss_popupMenuClose {
  width:100%;
  padding:0px 8px 0px 0px;
  text-align:right;
}

.ss_permalink {
	background-color: ${ss_toolbar4_background_color};
}

.ss_hover_over {
  position:absolute;
  border:1px solid black;
  margin:2px;
  padding:2px;
  background-color: ${ss_style_background_color_opaque};
  width: 150px;
}

.ss_popupTitleOptions {
  position:absolute;
  border:1px solid black;
  margin:4px;
  padding:2px;
  background-color:${ss_style_background_color};
  }

table.ss_popup {
	position: relative;
	background-color: transparent;
	border: 1px solid ${ss_toolbar1_background_color};
}

div.ss_popup_top {
  position: relative;
  background-image: url(<html:imagesPath/>pics/background_base_title_bar.jpg);
  background-repeat: repeat-x;
  height: 14px;
  padding: 0px;
}



div.ss_popup_title {
   font-family: ${ss_style_title_font_family};
   font-size: 11px;
   font-weight: bold;
   color: ${ss_style_header_bar_title_color};
   position: relative;
   text-align: center;
}

div.ss_popup_close {
  position: relative;
  background-image: url(<html:imagesPath/>pics/popup_close_box.gif);
  background-repeat: no-repeat;
  width: 12px;
  height: 13px;
  top: 1px;
  left: -1px;
}

div.ss_popup_body {
  position: relative;
  background-color: ${ss_toolbar4_background_color};
  padding: 10px;
}


.ss_edit_button {
	color:${ss_style_link_hover_color};
}

/* Help system */
.ss_helpSpot {
  position:absolute;
  width:30px;
  height:30px;
  visibility:hidden;
  display:none;
}
.ss_helpSpotTitle {
  background-color:${ss_help_spot_background_color} !important;
  border:1px solid black;
}
.ss_helpWelcome {
  position:absolute;
  visibility:hidden;
  display:none;
  background-color: transparent !important;
  margin:2px;
  padding:2px;
}
.ss_helpPanel {
  position:absolute;
  visibility:hidden;
  display:none;
  margin:2px;
  padding:2px;
  width:500px;
}
.ss_helpToc {
  border: 1px solid ${ss_style_border_color_light};
  padding-right: 5px;
  background-color:${ss_help_spot_background_color} !important;
  visibility:hidden;
  display:none;
}
.ss_helpToc li {
  list-style-type: square;
}

.ss_help_bullets li {
	list-style-type: disc;
}

div.ss_help_popup_body_frame {
  border: 1px solid #6B78A9;
  padding: 10px;
}

img.ss_help_cpanel_show {
	background-image: url(<html:imagesPath/>icons/accessory_hide.gif);
	background-repeat: no-repeat;
	width: 16px;
	height: 16px;
	position: relative;
	vertical-align: middle;
}

img.ss_help_cpanel_hide {
	background-image: url(<html:imagesPath/>icons/accessory_show.gif);
	background-repeat: no-repeat;
	width: 16px;
	height: 16px;
	position: relative;
	vertical-align: middle;
}
	

.ss_inlineHelp {
  visibility:hidden; 
  display:none; 
  border:1px solid black; 
  background-color: #ffffff;
}

.ss_lightBox {
  position:absolute;
  background-color:${ss_lightBox_background_color};
}

.ss_lightBox_transparent {
  position:absolute;
}

/* **************************************************************** */
/* Help content */

/* Horizontal and vertical spacing */
DIV.ss_help_style {
    text-align: left;
}

DIV.ss_help_title {
    margin-top: 0px;
    margin-bottom: 1px;
}

DIV.ss_help_style P {
    margin-top: 3px;
    margin-bottom: 10px;
}

P.ss_ihelp_para {
    margin-top: 3px;
}

div.ss_help_style ul li, ul.ss_square li {
    margin-top: 3px;
    margin-bottom: 6px;
    margin-left: 25px;
    list-style-type: square;
    list-style-position: outside;
    list-style-image: none;
}

div.ss_help_style ol li {
    margin-top: 3px;
    margin-bottom: 6px;
    margin-left: 25px;
    list-style-position: outside;
    list-style-type: decimal;
}

DIV.ss_help_style LI P {
    margin-top: 4px;
}

DIV.ss_help_style UL, OL {
    margin-top: 0px;
    margin-bottom: 0px;
}

DIV.ss_help_style UL {
  list-style-type: square;
}

DIV.ss_help_style DIV.picture {
    text-align: center;
    margin-top: 2px;
    margin-bottom: 5px;
}

DIV.ss_help_style DIV.footnote_text {
    font-size: ${ss_style_font_smallprint} !important;
    color: ${ss_style_gray_color}; 
    border-top: solid  ${ss_style_light_color}  1px;
    font-style: italic;
    margin-top: 2px;
    margin-bottom: 5px;
}

DIV.ss_help_style DIV.example, DIV.note {
    margin-left: 15px;
    margin-top: 2px;
    margin-bottom: 5px;
}

P.ss_help_moreinfo, P.ss_help_body_header {
    margin: 0px;
    font-weight: bold;
    color: ${ss_title_line_color};  
}

DIV.ss_help_moreinfo {
    margin-bottom: 3px;
}

DIV.ss_help_moreinfo P {
    margin-top: 3px;
    margin-left: 15px;
    margin-bottom: 1px;
}

DIV.ss_help_more_pages_section {
    font-size: ${ss_style_font_smallprint} !important;
    text-align: center;
    margin-bottom: 3px;
    margin-top: 10px;
}

DIV.ss_help_more_pages_section DIV {
    display: inline;
    margin-top: 0px;
    margin-bottom: 0px;
}

DIV.ss_help_more_pages_section DIV.current_page {
    padding: 2px 2px 2px 2px;
    margin-right: 8px;
    border: solid ${ss_style_light_color} 1px;
}

DIV.ss_help_more_pages_section DIV.not_last_link {
    padding: 2px 2px 2px 2px;
    margin-right: 8px;
}

DIV.ss_help_more_pages_section DIV.no_next_page {
    padding: 2px 2px 2px 2px;
}

DIV.ss_help_more_pages_section DIV.no_prev_page {
    padding: 2px 2px 2px 2px;
    margin-right: 8px;
}


/*  Word styling */
DIV.ss_help_style .clickable_item, .document_title, .light_emphasis, .keyboard_keys, .variable, .page_item {
    font-style: italic;
}

DIV.ss_help_moreinfo .document_title {
    font-style: italic;
}

DIV.ss_help_style .new_term {
    font-weight: bold;
    font-style: italic;
    color: ${ss_title_line_color};  
}

DIV.ss_help_style .help_superscript {
    font-size: ${ss_style_font_largestprint} !important; 
    vertical-align: text-top;
}

DIV.ss_help_style .strong-emphasis, .def_list_term, .subtitle, .header {
    font-weight: bold;
}

/* Getting Started Portal */


.ss_getting_started {
	font-weight: bold;
	text-align: center;
	font-size: ${ss_style_font_largeprint};
	text-decoration: underline;
	color: ${ss_logo_text};
}


/* Blogs */
.ss_blog {
 
}



div.ss_blog_content {
	padding-bottom: 30px;

}

div.ss_blog_sidebar {
    padding-left:5px;
 	padding-right: 5px;
    padding-top: 10px;
    background-color:${ss_style_background_color_side_panel_featured};
}

.ss_blog_sidebar table {
	background-color: transparent;
}

div.ss_blog_sidebar_hole {
	margin-left: 18px;
	margin-right: 0px;
}

div.ss_blog_sidebar_box {
	background-color: #FFFFFF;
	border: 1px solid ${ss_blog_sidebar_box_outline};
	margin-bottom: 10px;
	margin-top: 2px;
	padding: 2px 5px;
}

.ss_blog_sidebar_subhead {
	font-size: 14px;
	font-weight: normal;
	font-family: ${ss_style_title_font_family};
	color: ${ss_style_muted_label_color};
}

div.ss_blog_sidebar_subhead {
	padding-top: 2px;
	padding-bottom: 5px;
	
}

div.ss_clear_float {
	height: 0px;
	line-height:0px;
	clear: both;
	font-size:0px;
	border:0px;
}

.ss_blog_content, .ss_blog_content table {
  background-color:${ss_blog_content_background_color};
}


.ss_blog_summary_title, .ss_blog_summary_title table {
  background-color:${ss_blog_summary_title_background_color};
}

span.ss_blog_summary_title_text {
  font-family: ${ss_style_title_font_family};
  font-weight: bold;
  font-size: 13px;
}

table.ss_blog_title table {
  background-color:${ss_style_header_bar_background};
}

div.ss_blog_title  {
  overflow: hidden;
  padding-top: 12px;
  padding-bottom: 3px;
  padding-left: 2px;
  padding-right: 5px;
}

div.ss_header_bar_timestamp {
	font-family: ${ss_style_title_font_family};
	font-size: 11px;
	color: ${ss_style_metadata_color};
	margin-top: 3px;
	margin-right: 5px;
	float: right;
	position: relative;
}
.ss_blog_footer {
	background-color:${ss_blog_footer_color};
	padding-bottom: 5px; 
	padding-top: 4px;
	padding-left: 22px
}
	
div.ss_header_bar_timestamp a, div.ss_header_bar_timestamp a:visited {
	color: ${ss_style_metadata_color};
}

div.ss_header_bar_timestamp a:hover, div.ss_header_bar_timestamp a:visited:hover {
	color: ${ss_style_metadata_color};
	text-decoration: underline;
}


div.ss_header_bar_burst {
    display: inline;
	padding-left: 3px;
}

div.ss_replies div.ss_header_bar_burst {
    display: inline;
	padding-left: 30px;
}

div.ss_header_bar_title_text {
    display: inline;
}

span.ss_header_bar_title_text {
    font-family: ${ss_style_title_font_family};
    font-weight: bold;
	color: ${ss_style_header_bar_title_color};
	font-size: 15px;
	margin-left: 5px;
}

a.ss_header_bar_title_link {
	color: ${ss_style_header_bar_title_link_color};
	text-decoration: none;
}

a.ss_header_bar_title_link:visited {
	color: ${ss_style_header_bar_title_link_color};
	text-decoration: none;
}

a.ss_header_bar_title_link:hover, a.ss_header_bar_title_link:visited:hover {
	color: ${ss_style_header_bar_title_link_color};
	text-decoration: underline;
}



a.ss_displaytag {
	color: ${ss_style_metadata_color};
}


/* Sliding tables */
div.ss_sliding_table_column0 {
  display: block; 
  border: ${ss_sliding_table_border_color} 1px solid;
  margin: 0px;
}
.ss_sliding_table_column0 * {
  color: ${ss_sliding_table_text_color};
  white-space:nowrap;
}
div.ss_sliding_table_column1 {
  position: absolute; 
  visibility: hidden;
  display: block; 
  border-left: #ffffff solid 1px;
  margin: 0px;
}
.ss_sliding_table_column1 * {
  color: ${ss_sliding_table_text_color};
  white-space:nowrap;
}
div.ss_sliding_table_column {
  position: absolute; 
  visibility: hidden;
  display: block; 
  border-left: ${ss_sliding_table_border_color} solid 1px;
  margin: 0px;
}
.ss_sliding_table_column * {
  color: ${ss_sliding_table_text_color};
  white-space:nowrap;
}
.ss_sliding_table_info_popup {
  position: absolute; 
  visibility: hidden;
  display:block; 
  border-left: ${ss_sliding_table_border_color} solid 1px;
  margin: 0px;
}
.ss_sliding_table_row0 {
  background-color: ${ss_sliding_table_row0_background_color}; 
  line-height:16px;
  font-family: ${ss_style_folder_view_font_family} !important;
}
.ss_sliding_table_row1 {
  background-color: ${ss_sliding_table_row1_background_color}; 
  line-height:16px;
  font-family: ${ss_style_folder_view_font_family} !important;
}
.ss_highlightEntry {
  background-color: ${ss_folder_line_highlight_color} !important;
}
.ss_sliding_table_info_popup * {
  color: ${ss_sliding_table_text_color};
}
.ss_sliding_table_column a, .ss_sliding_table_column a:visited {
  color: ${ss_sliding_table_text_color};
}
.ss_sliding_table_column a:hover {
  color: ${ss_sliding_table_link_hover_color};
}
.ss_sliding_table_column0 a, .ss_sliding_table_column0 a:visited {
  color: ${ss_sliding_table_text_color};
}
.ss_sliding_table_column0 a:hover {
  color: ${ss_sliding_table_link_hover_color};
}
.ss_sliding_table_column1 a, .ss_sliding_table_column1 a:visited {
  color: ${ss_sliding_table_text_color};
}
.ss_sliding_table_column1 a:hover {
  color: ${ss_sliding_table_link_hover_color};
}
.ss_sliding_table_info_popup a, .ss_sliding_table_info_popup a:visited {
  color: ${ss_sliding_table_text_color};
}
.ss_sliding_table_info_popup a:hover {
  color: ${ss_sliding_table_link_hover_color} !important;
}
table.ss_mouseOverInfo {
  position:relative;
  left:1px;
  top:1px;
}
.ss_mouseOverInfo span {
  position:relative;
  left:-1px;
  top:0px;
  border:1px solid black;
  padding-right:10px;
}
.ss_mouseOverInfo td a.ss_title_menu span {
  position:relative;
  top:0px;
  border:1px solid black;
  padding-right:10px;
}
.ss_mouseOverInfo td a.ss_title_menu img {
  position:relative;
  top:0px;
}


/* Folder */
.ss_folder_border, .ss_folder_border table , .ss_folder_border form {
  background-color: ${ss_folder_border_color} !important;
  }


/* Entry */
.ss_entry_border, .ss_entry_border table {
  background-color: ${ss_entry_border_color} !important;
  }

/* Forum toolbars */

.ss_toolbar {
  width: 100%; 
  border-top: 1px solid ${ss_toolbar_border_color};
  border-bottom: 1px solid ${ss_toolbar_border_color};
  background-color: ${ss_toolbar_background_color};
  color: ${ss_toolbar_text_color};
  margin-top: 0px;
  margin-bottom: 8px;
  padding-top:2px;
  padding-bottom:2px;
  }
.ss_toolbar * {
  background-color: ${ss_toolbar_background_color};
  color: ${ss_toolbar_text_color};
  }
.ss_toolbar_color {
  background-color: ${ss_toolbar_background_color};
  color: ${ss_toolbar_text_color};
  }
  
.ss_toolbar_menu {
  position: absolute;
  visibility: hidden;
  color: ${ss_toolbar_text_color};
  border: 1px ${ss_toolbar_border_color} solid;
  padding: 4px;
  width: 300px;
  }
.ss_toolbar_menu * {
  color: ${ss_toolbar_text_color};
  }
.ss_toolbar_item * {
  color: ${ss_toolbar_text_color};
  }
.ss_toolbar_item a {
  color: ${ss_toolbar_text_color};
  }
.ss_toolbar a, .ss_toolbar a:visited {
  color: ${ss_toolbar_text_color};
}
.ss_toolbar a:hover {
  color: ${ss_toolbar_link_hover_color};
}
.ss_toolbar_inactive { 
  color:#999999; 
}


li.ss_menu_item_highlighted {
  font-weight: bold;
  border: 1px solid #666666;
}

.ss_entry_toolbar {
  display:inline;
  background-color: ${ss_toolbar_background_color};
  color: ${ss_toolbar_text_color};
  margin-top: 0px;
  margin-bottom: 0px;
  padding-top:0px;
  padding-bottom:0px;
  }
.ss_entry_toolbar * {
  background-color: ${ss_toolbar_background_color};
  color: ${ss_toolbar_text_color};
  }
.ss_entry_toolbar_color {
  background-color: ${ss_toolbar_background_color};
  color: ${ss_toolbar_text_color};
  }
  
.ss_entry_toolbar_menu {
  position: absolute;
  visibility: hidden;
  color: ${ss_toolbar_text_color};
  border: 1px ${ss_toolbar_border_color} solid;
  padding: 4px;
  width: 300px;
  }
.ss_entry_toolbar_menu * {
  color: ${ss_toolbar_text_color};
  }
.ss_entry_toolbar_item * {
  color: ${ss_toolbar_text_color};
  }
.ss_entry_toolbar_item a {
  color: ${ss_toolbar_text_color};
  }
.ss_entry_toolbar a, .ss_toolbar a:visited {
  color: ${ss_toolbar_text_color};
}
.ss_entry_toolbar a:hover {
  color: ${ss_toolbar_link_hover_color};
}


table.ss_attachments_list {
	padding-left: 30px;
	width: 98%;
	empty-cells: show;
}

table.ss_attachments_list td.ss_att_title {
	font-size: 10px;
	font-weight: bold;
	padding-left: 0px;
}

table.ss_attachments_list hr.ss_att_divider {
	border-top: 1px solid ${ss_generic_border_color};
	border-bottom: none;
	height: 0px;
}

table.ss_attachments_list td.ss_att_meta, .ss_att_meta {
	font-size: 10px;
	white-space: nowrap;
	padding-left: 5px;
	color: ${ss_style_metadata_color};
}
table.ss_attachments_list td.ss_att_space {
    padding-left: 10px;
}

.ss_subhead2 {
	color: ${ss_style_text_color};
}

.ss_footer_toolbar {
  width: 100%; 
  background-color: ${ss_style_background_color};
  color: ${ss_style_footer_text_color};
  font: ${ss_style_footer_font};
  text-align:center;
  padding-top:15px;
  padding-bottom:2px;
  }
.ss_footer_toolbar * {
  background-color: ${ss_style_background_color};
  color: ${ss_style_text_color};
  }

.ss_footer_toolbar_menu {
  position: absolute;
  visibility: hidden;
  color: ${ss_style_footer_text_color};
  border: 1px ${ss_style_border_color} solid;
  padding: 4px;
  width: 300px;
  }
.ss_footer_toolbar_menu * {
  color: ${ss_style_footer_text_color};
  }
.ss_footer_toolbar_item * {
  color: ${ss_style_footer_text_color};
  }
.ss_footer_toolbar_item a {
  color: ${ss_style_footer_text_color};
  }
.ss_footer_toolbar a, .ss_toolbar a:visited {
  color: ${ss_style_footer_text_color};
  text-decoration: none;
}
.ss_footer_toolbar a:hover {
  color: ${ss_style_link_hover_color};
  text-decoration: underline;
}

div.ss_folder_tags {
	width: 100%;
	margin-top: 0px;
}

.ss_tags {
  color:${ss_tag_color};
  font-weight:bold;
}
.ss_tag_pane {
  display:none; 
  visibility:hidden; 
  margin:0px;
  padding:2px;
  opacity: 0.95;
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=95);
 </c:if>
  
}
.ss_tag_pane_color {
  background-color: ${ss_tag_pane_background_color} !important; 
  color: ${ss_style_text_color} !important;
}

table.ss_tag_pane_color th {
	border: none;
}


.ss_tag_pane_ok_cover {
  position:relative; 
  top:-14px;
<c:if test="<%= !isIE %>">
  left: 35%; 
</c:if>
  height:20px; 
  width:50px; 
  background-color:${ss_tag_pane_background_color};
}

.ss_link_menu {
  position:absolute;
  visibility:hidden; 
  text-align:left; 
  background-color: ${ss_style_background_color}; 
  color: ${ss_style_text_color};
  border: 1px ${ss_style_border_color} solid;
  padding: 0px 4px 0px 10px;
}

ul.ss_dropdownmenu {
  list-style: outside;
  text-align:left;
  margin:2px 2px 2px 13px;
  padding: 2px;
}

ul.ss_dropdownmenu li {
  list-style-type: square;
  margin-left: 0px;
  margin-bottom: 2px;
  padding-left: 2px;
} 

.ss_dashboard_component {
}
.ss_dashboard_component_dragger {
}
.ss_dashboard_component_title {
}
.ss_dashboard_dragHandle {
  cursor:move;
}
.ss_dashboard_dragHandle_clone {
  background-color: ${ss_style_component_toolbar_background_color} !important;
  border:1px solid black;
  padding:2px 2px 2px 20px;
  height:18px;
}
table.ss_dashboardTable_off {
	border: 1px solid transparent;
}
td.ss_dashboardTable_off {
  border-right:1px solid #ffffff;	
  border-bottom:1px solid #ffffff;	
}
table.ss_dashboardTable_on {
  border-top:1px solid blue;	
  border-left:1px solid blue;	
}
td.ss_dashboardTable_on {
  border-right:1px solid blue;	
  border-bottom:1px solid blue;	
}
div.ss_dashboardDropTarget {
  position:absolute;
  height:15px;
}
div.ss_dashboardDropTarget_over {
  position:absolute;
  background-color:blue;
  height:15px;
}
div.ss_dashboardProtoDropTarget {
  position:relative;
  visibility:hidden;
  height:4px;
  top:-4px;
  background-color: ${ss_style_background_color}; 
}

.ss_dashboard_menu {
  position: absolute;
  visibility: hidden;
  display: none;
  background-color: ${ss_style_background_color_opaque}; 
  color: ${ss_style_text_color};
  border: 1px ${ss_style_border_color} solid;
  padding: 0px;
  width: 400px;
  }

.ss_dashboard_config {
  margin: 10px;
  border: 1px solid ${ss_form_border_color};
  background-color: ${ss_form_component_background_color};
<c:if test="<%= isIE %>">
  zoom:1; /* a workaround IE bug - font color not display correctly */
</c:if>
}

.ss_dashboard_view {
  border: 1px solid ${ss_style_border_color};
  background-color: ${ss_style_component_background_color};
}

.ss_dashboard_toolbar {
  margin:0px;
  padding:0px;
  border-bottom: 1px solid ${ss_style_border_color_light} !important;
}

.ss_dashboard_toolbar_color {
  background-color: ${ss_style_component_toolbar_background_color} !important;
}

.ss_dashboard_toolbar_color * {
  background-color: ${ss_style_component_toolbar_background_color} !important;
}

.ss_dashboard_display_simple {
  border: 1px solid ${ss_style_border_color} !important;
}
.ss_dashboard_display_simple_toolbar {
  margin:0px;
  padding:0px;
  border-bottom: 1px solid ${ss_style_border_color_light} !important;
}
.ss_dashboard_display_simple_toolbar_color {
  background-color: ${ss_style_component_toolbar_background_color} !important;
}

.ss_dashboard_display_simple_toolbar_color * {
  background-color: ${ss_style_component_toolbar_background_color} !important;
}

.ss_dashboard_display_none {
}
.ss_dashboard_display_none_toolbar {
  margin:0px;
  padding:0px;
}
.ss_dashboard_display_none_toolbar_color {
  background-color: ${ss_style_background_color} !important;
}

  
/* highlights */
a.ss_tree_highlight  {
  text-decoration: none;
  }
span.ss_tree_highlight {
  font-weight: bold;
  color: ${ss_tree_highlight_line_color};
}

div.ss_sidebar_panel div.ss_treeWidget span {
    font-size: ${ss_style_font_smallprint};
}

a.ss_tree_highlight:active, a.ss_tree_highlight:hover {
  font-weight: bold;
  color: ${ss_tree_highlight_line_color};
  text-decoration: underline !important;
  }
  
.ss_tree_bucket_text_div {
  position:absolute;
  border:1px solid black;
  margin:0px;
  padding:0px 4px;
  background-color:#ffffff;
  left:-4px;
}
  
.ss_titlebold {
  font-size: ${ss_style_font_largestprint};
  font-weight: bold;
  color: ${ss_title_line_color};  
  }

/* Box styles */

div.ss_box_top_rounded {
	background-color: ${ss_style_background_color_opaque};
}

.ss_box_rounded {
	background-color: ${ss_box_color};
}

.ss_box_bottom_rounded {
	background-color: ${ss_box_color};
	height: 1px;
	margin: 0px;
}

.ssf_box {
	background-color: ${ss_style_background_color};
	height: auto;
<c:if test="<%= !isIE %>">
	height: 100%;
</c:if>
	margin: 2px 2px 0px 2px;
}

.ss_box_minimum_height {
	height: 1px;
	margin: 0px;
}

.ss_box_small_icon_bar {
	background-color: ${ss_box_color};
	height: 1em;
	position:relative;
	top: 0px;
	margin: 0px;
}

.ss_box_small_icon {
	height: 14px;
	margin: 0px;
	width: 14px;
}

.ss_plus {
  width:16px;
  height:16px;
  background-image: url(<html:imagesPath/>pics/sym_s_plus.gif);
  border:none;
  }
.ss_minus {
  width:16px;
  height:16px;
  background-image: url(<html:imagesPath/>pics/sym_s_minus.gif);
  border:none;
  }

/* Tree widget styles */
.ss_treeWidget {
  background: url("<html:imagesPath/>pics/1pix.gif") fixed no-repeat;
    margin-top:8px;
    }
.ss_twDiv {
  }
.ss_twSpan, ss_twA {
  position:relative;
  top:-6px;
  line-height:10px;
  margin:0px;
  padding:0px;
  }
.ss_twImg {
  width:19px;
  height:20px;
  border:none;
  margin: 0px 2px 0px 0px !important;
  padding:0px;
  }
.ss_twImg8 {
  width:8px;
  height:20px;
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twIcon {
  width:19px;
  height:20px;
  border:none;
  margin: 1px 2px 0px 0px !important;
  padding-top:10px;
  }
.ss_twPlus {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/plus.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twPlusTop {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/plus_top.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twMinus {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/minus.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twMinusTop {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/minus_top.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twPlusBottom {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/plus_bottom.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twPlusTopBottom {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/plus_top_bottom.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twMinusBottom {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/minus_bottom.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twMinusTopBottom {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/minus_top_bottom.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twJoin {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/join.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twJoinBottom {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/join_bottom.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twSpacer {
  width:19px;
  height:20px;
  background-image: url(<html:imagesPath/>trees/spacer.gif);
  border:none;
  margin:0px;
  padding:0px;
  }
.ss_twNone {
  width:0px;
  height:20px;
  background-image: url(<html:imagesPath/>pics/1pix.gif);
  border:none;
  margin:0px;
  padding:0px;
  }

/* htmlarea overrides */
.htmlarea { 
    background-color: ${ss_form_element_color}; 
}


/* -------------------Skins-------------------- */
div.ss_content_outer {
    position: relative;
    background: #FFFFFF;
	margin-top:0;
	border: solid 1px #CCC;
}
.ss_content_inner {
	width:100%;
	padding:0px;
	margin:0px 0px 0px 0px;
    background-color: ${ss_style_background_color};
}
.ss_content_window {
	padding:0px 0px;
	
}
.ss_content_window_compact {
	padding:0px;
}
.ss_content_window_content {
	padding:4px 10px;
	border-left: 1px solid ${ss_style_border_color_dark_hue};
	border-right: 1px solid ${ss_style_border_color_dark_hue};
	border-bottom: 1px solid ${ss_style_border_color_dark_hue};
	border-top: none;
}

.ss_content_window_content_off {
	padding: 0px;
	border: 0px none;
}

.ss_clear {
	clear:both;
	height:1px;
	font-size:0px;
}


div.ss_content_outer table, div.ss_portlet table, table.ss_style, div.ss_popup_body table, table.ss_popup {
	border-collapse: separate;
	border-spacing: 0px;
	
}


/* round corners: */

.ss_decor-border5{
	background:url(<html:imagesPath/>roundcorners2/border.gif) repeat-y left;
}
.ss_decor-border6{
	background:url(<html:imagesPath/>roundcorners2/border.gif) repeat-y right;
}

.ss_decor-round-corners-bottom3 {
	background:url(<html:imagesPath/>roundcorners3/border2.gif) repeat-x bottom;
}
.ss_decor-round-corners-bottom3 div {
	background:url(<html:imagesPath/>roundcorners3/corner3.jpg) no-repeat left;
}
.ss_decor-round-corners-bottom3 div div {
	background:url(<html:imagesPath/>roundcorners3/corner4.jpg) no-repeat right;
	height:7px;
	font-size:1px;
}
.ss_decor-border7 {
	background:url(<html:imagesPath/>roundcorners3/border3.jpg) repeat-y left;
}
.ss_decor-border8 {
	background:url(<html:imagesPath/>roundcorners3/border4.gif) repeat-y right;
}

.ss_decor-round-corners-top2{
	background:url(<html:imagesPath/>roundcorners2/border.gif) repeat-x top;
}
.ss_decor-round-corners-top2 div{
	background:url(<html:imagesPath/>roundcorners2/corner1.gif) no-repeat left ;
}
.ss_decor-round-corners-top2 div div{
	background:url(<html:imagesPath/>roundcorners2/corner2.gif) no-repeat right;
	height:10px;
	font-size:1px;
}
.ss_decor-round-corners-top2 div {
	background:none;
}
.ss_decor-round-corners-bottom2{
	background:url(<html:imagesPath/>roundcorners2/border.gif) repeat-x bottom;
	margin-bottom:0px;
}
.ss_decor-round-corners-bottom2 div{
	background:url(<html:imagesPath/>roundcorners2/corner3.gif) no-repeat left;
}
.ss_decor-round-corners-bottom2 div div{
	background:url(<html:imagesPath/>roundcorners2/corner4.gif) no-repeat right;
	height:10px;
	font-size:1px;
}
.ss_rounden-content {
	margin:0px 19px 0px 6px;
	padding:0px 0px 0px 0px;
    background-color: ${ss_style_background_color};
}

td.ss_view_sidebar {
    width: 200px;
    background-color: #D8D8D8;
    border-right: 6px solid #FFFFFF;
}

td.ss_view_info {
	padding-left: 0;
}


img.ss_generic_close {
    background:url(<html:imagesPath/>tabs/tab_delete.gif) no-repeat left 0px;
    width:9px;
    height: 9px;
}

a:hover img.ss_generic_close {
    background-position:  left -9px;
}

img.ss_print_button {
	position:relative;
    background:url(<html:imagesPath/>icons/printer.gif) no-repeat left -16px;
    width:16px;
    height: 16px;
}

a:hover img.ss_print_button {
    background-position:  left 0px;
}

img.ss_accessory_modify {
	position:relative;
    background:url(<html:imagesPath/>icons/accessory_modify.gif) no-repeat left 0px;
    width:16px;
    height: 16px;
    top: -1px;
}

a:hover img.ss_accessory_modify {
    background-position:  left -16px;
}

img.ss_accessory_delete {
	position:relative;
    background:url(<html:imagesPath/>icons/accessory_delete.gif) no-repeat left 0px;
    width:12px;
    height: 12px;
    top: 1px;
}

a:hover img.ss_accessory_delete {
    background-position:  left -12px;
}


span.ss_tabs_title {
	font-size: ${ss_style_font_smallprint}!important;
}

/* breadcrumbs */
.ss_breadcrumb {
	margin-top:0px;
	margin-left:-10px;
	padding:0px 5px 0px 0px;
	font-size: ${ss_style_font_smallprint};
}
.ss_breadcrumb ul{
	margin-left:-15px;
}
.ss_breadcrumb li{
	float:left;
	margin:0px;
	padding:0px 5px 0 5px; 
}  
a.ss_breadcrumb {
	font-size: ${ss_style_font_smallprint};
}

div.ss_sidebar_panel {
	padding-bottom: 1px;
	width: 200px;
	overflow: hidden;
	}

div.ss_sidebar_panel_featured {
	width: 200px;
	overflow: hidden;
	background-color: ${ss_style_background_color_side_panel_featured};
	}
/* titlebar */
.ss_base_title_bar {
	background-color: ${ss_toolbar1_background_color};
	color: #333;
	font-size: 12px;
	font-family: ${ss_style_title_font_family};
	padding-bottom: 1px;
	padding-top: 2px;
	padding-left:10px;
	padding-right: 5px;
	}
.ss_title_bar {
	background-image: url(<html:imagesPath/>roundcorners3/background_dc_bar.jpg);
	color: #333333;
	height:14px;
	margin:0px;
	padding-bottom: 3px;
	}
.ss_title_bar * {
	background-color: transparent;
	}
.ss_title_bar_history_bar {
	background:inherit;
	color:${ss_style_text_color};
	}		
.ss_title_bar_inner1 {
	background:url(<html:imagesPath/>roundcorners3/corner1.gif) no-repeat left;
	height:24px;
	width:8px;
	}		
.ss_title_bar_inner2 {
	background:url(<html:imagesPath/>roundcorners3/corner2.gif) no-repeat right;
	height:24px;
	width:8px;
	}
.ss_title_bar strong {
	position:relative;
	padding-left:15px;
	top:1px;
	margin:0px;
	padding:0px;
	}
/* titlebar icons: */
.ss_title_bar_icons {
	float:right;
	margin:0px;
	padding:1px 0px 0px 10px;
	cursor:auto;
}
.ss_title_bar_icons li {
	float:left;
	margin-right:6px;
	height: 16px;
}

/* title menu: */

.ss_title_menu_dd {
	background-color: #CCCCCC;
	font-weight: normal;
	font-size: 11px;
	font-family: Arial;
    text-align:left;
	position:absolute;
	display:none;
	z-index:500;
	border: 1px solid #999999;
}

.ss_title_menu_dd li {
  list-style-type:none;
}

.ss_title_menu_dd ul {
  margin:5px 0pt 10px 20px;
  padding:0pt;  
}

.ss_title_menu_dd a:hover {
  text-decoration:underline;
}

.ss_title_menu_dd a {
  color: #333333;
  text-decoration:none;
}

/* actions: */

.ss_actions_bar1 {
	font-weight: normal;
	font-size: 11px;
	font-family: ${ss_style_title_font_family};
}

div.ss_actions_bar1_pane {
	background-color: #AFAFAF;
	border-bottom: 1px solid;
	border-color: #FFF;
	height: 22px;
	white-space: nowrap;
}

table.ss_actions_bar2_pane, table.ss_actions_bar2_pane td {
	background-color: ${ss_toolbar2_background_color};
	width: 100%;
	height: 22px;
	padding: 0px;
	margin: 0px;
	border-collapse: collapse;
	border-spacing: 0px;
}

.ss_actions_bar2, table.ss_actions_bar2_pane {
	background-image: url(<html:imagesPath/>pics/background_toolbar2.gif);
	background-repeat: repeat-x;
}

.ss_actions_bar2 {
	font-weight: normal;
	font-size: 11px;
	font-family: ${ss_style_title_font_family};
}

.ss_actions_bar3 {
	background-color: #AFC8E3;
	font-weight: normal;
	font-size: 11px;
	font-family: ${ss_style_title_font_family};
}


table.ss_actions_bar4_pane {
    background-image: url(<html:imagesPath/>pics/background_actionbar4.gif);
	background-repeat: repeat-x;
	width: 100%;
	height: 20px;
	padding: 4px 0px 0px 0px;
	margin: 0px;
	border-collapse: collapse;
	border-spacing: 0px;
}

.ss_actions_bar4 {
	font-weight: normal;
	font-size: 11px;
	font-family: ${ss_style_title_font_family};
}



ul.ss_actions_bar1  {
	height: 22px;
	
}

ul.ss_actions_bar1.ss_actions_bar_submenu {
	height: auto;
}


.ss_actions_bar {
	list-style-type:none;	
	padding: 0px 0px 0px 0px;
	height: 20px;
	margin: 0px;
}

.ss_action_bar ul, .ss_actions_bar_submenu {
	list-style-type: none;
}
ul.ss_actions_bar li {
	float:left;
	display: inline;
	margin: 0px 0px 0px 0px;
	white-space: nowrap;
}

.ss_actions_bar li.ss_toolBarItem {
	border: 0px;
	margin: 0px;
	padding: 0px;
	background-color: inherit;
}



.ss_actions_bar li.ss_toolBarItem ul {
	float: left;
	display: inline;
	margin: 0px;
	padding: 0px;
}

.ss_actions_bar li.ss_toolBarItem .ss_toolBarItemTxt {
	float: left;
	margin-top: 0px;
}

.ss_actions_bar li.ss_actions_bar_separator {
	border-right: 0px;
	border-top: 0px;
	height: 18px;
	margin: 0px 5px 0px 5px;
	width: 0px;
	padding: 0px;
}

.ss_actions_bar form {
	display: inline;
	background-color: transparent;
}

.ss_actions_bar form label, ul.ss_actions_bar form input {
	background-color: transparent;
	margin-top: 5px;
}

.ss_actions_bar li.ss_actions_bar_last-child {
	border-right:none;
}

.ss_actions_bar li a, .ss_actions_bar li a:visited {
	color:${ss_style_text_color} !important;
	display:block;
	padding:0px 7px;
}

.ss_actions_bar li a.ss_actions_bar_inline, .ss_actions_bar li a.ss_actions_bar_inline:visited  {
	color:${ss_style_text_color} !important;
	display: inline;
	padding:0px 7px;
}

.ss_actions_bar_background {
	margin:0px;
	padding:0px;
}

.ss_actions_bar_history_bar {
	margin-top: 5px;
	font-size: 11px;
	font-family: ${ss_style_title_font_family};
}
.ss_actions_bar_history_bar a {
	padding:0px !important;
}
.ss_actions_bar_history_bar * {
	color:#0000FF
}

ul.ss_actions_bar1 li {
	margin-top: 3px;
}

ul.ss_actions_bar1 li a:hover {
	text-decoration:none;
}

.ss_actions_bar2 li a:hover {
	background-image: url(<html:imagesPath/>pics/background_actionbar4.gif);
	text-decoration:none;
}

.ss_actions_bar3 li a:hover {
	background-color: ${ss_style_background_color_side_panel};
	text-decoration:none;
}

div.ss_actions_bar_submenu {
	background-color: ${ss_style_background_color_side_panel};
	margin:0px;
	padding:0px;
	text-align:left;
	position:absolute;
	display:none;
	z-index:500;
	white-space: nowrap;
}

div.ss_actions_bar_submenu ul.ss_actions_bar1 {
  background-color: ${ss_toolbar1_dropdown_menu_color};
  background-image: none;
  opacity: 0.95;
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=95);
 </c:if>
}

div.ss_actions_bar_submenu ul.ss_actions_bar2 {
  background-color: ${ss_toolbar_border_color};
  background-image: none;
  opacity: 0.95;
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=95);
 </c:if>
}

div.ss_actions_bar_submenu ul.ss_actions_bar3 {
  background-color: ${ss_toolbar1_background_color};
  background-image: none;
  opacity: 0.95;
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=95);
 </c:if>
}

div.ss_actions_bar_submenu ul.ss_actions_bar4 {
  background-color: ${ss_toolbar1_background_color};
  background-image: none;
  opacity: 0.95;
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=75);
 </c:if>
 margin: 0px;
}

.ss_actions_bar_submenu {
 <c:if test="<%= isIE %>">
	top:-12px;
	left:-26px;
 </c:if>
 <c:if test="<%= !isIE %>">
	top:-8px;
	left:-10px;
 </c:if>	
	border:1px solid ${ss_toolbar1_background_color};
	border-top:none;
	padding: 1px 1px;
}

.ss_actions_bar_submenu li  {
	float:none;
	border-right-style:none;
    line-height:18px;
}

.ss_actions_bar_submenu ul.ss_actions_bar3 li  {
	float:none;
	border-bottom:1px solid #AFC8E3;
	border-right-style:none;
    line-height:18px;
}

.ss_actions_bar_submenu li a {
  display: block;
  padding-left: 10px;
  padding-right: 10px;
  text-decoration: none;
}

.ss_actions_bar_submenu div {
	background: none;
}

.ss_actions_bar_filters {
 <c:if test="<%= isIE %>">
	top:-6px;
	left:-45px;
 </c:if>
 <c:if test="<%= !isIE %>">
	top: 5px;
	left: -32px;
 </c:if>
}

.ss_actions_bar1 li:hover, .ss_actions_bar1 a:hover {
	background-color: ${ss_style_header_bar_background};
}

.ss_actions_bar2 li:hover, .ss_actions_bar1 a:hover {
background-image: url(<html:imagesPath/>pics/background_actionbar4.gif);
}


.ss_actions_bar3 li:hover, .ss_actions_bar1 a:hover {
background-image: url(<html:imagesPath/>pics/background_actionbar4.gif);
}

.ss_actions_bar_submenu li:hover, .ss_actions_bar_submenu a:hover {
	text-decoration:underline;
	color:${ss_style_text_color};
}
.ss_actions_bar_submenu a, .ss_actions_bar_submenu a:visited {
	color:${ss_style_text_color};
}
/* utils bar (dashboard)  */
div.ss_dashboardContainer {
	border-bottom: 1px solid ${ss_generic_border_color};
	width: 100%;
	margin: 0px;
	padding: 0px 0px 0px 0px;
}
div.ss_utils_bar {
    float:right;
    text-align:right;
	margin-bottom:0px;
	background:transparent none repeat scroll 0%;
}
div.ss_line {
	border-bottom: 1px solid #666666;
	width: 100%;
	margin: 0px;
	padding: 0px 0px 0px 0px;
}
ul.ss_utils_bar {
	float: right;
	list-style-type:none;
	margin:0px;
	padding:0px;
	text-align:right;
}
div.ss_utils_bar ul.ss_utils_bar li {
	float:left;
	margin: 0px 5px 0px 0px;
}
div.ss_utils_bar ul.ss_utils_bar li a, div.ss_utils_bar ul.ss_utils_bar li a:visited {
	color:#003782;
	display:block;
	margin:0px;
	border:0px;
	
}
div.ss_utils_bar ul.ss_utils_bar li a span {
	padding: 0px;
	margin: 0px;
	font-size:${ss_style_font_smallprint};
}

.ss_utils_bar li a:hover {
	text-decoration:underline;
}

div.ss_utils_bar_submenu {
	background-color: #ECECEC;
	margin:0px;
	padding:0px;
	text-align:left;
	position:absolute;
	display: none;
	visibility: hidden;
	z-index:500;
	border: 1px solid #CCCCCC;
	white-space: nowrap;
}

ul.ss_utils_bar_submenu {
	margin: 0px 5px 0px 5px;
	background-color: #ECECEC;
} 

ul.ss_utils_bar_submenu li  {
	float:none;
	padding:0px;
	font-weight:normal;
	border-right-style:none;
    line-height:20px;
}
ul.ss_utils_bar_submenu li a:hover {
	text-decoration:underline;
	background-color:transparent;
	color:#666666;
	font-size:${ss_style_font_smallprint};	
}
ul.ss_utils_bar_submenu a, .ss_utils_bar_submenu a:visited {
	text-decoration:none;
	color:#666666;
	font-size:${ss_style_font_smallprint};
}

div.ss_search_results_pane, div.ss_search_results_pane table {
  background-color:${ss_entry_description_background_color};
}
div.ss_search_results_pane {
  position: relative;
  padding-bottom: 20px;
  padding-left: 20px;
  padding-top: 10px;
  padding-right: 20px;
}

/* Search results selection */
.ss_search_results_selection {
	list-style-type:none;
	margin:0px;
	padding:0px;
	height:17px;
	line-height:17px;
}
.ss_search_results_selection li {
	float:left;
	font-weight:bold;
	border-right:1px solid #9687A7;
	border-left:1px solid #FFF;
	background:inherit;
}
.ss_search_results_selection li a {
	color:#5A3C68;
	display:block;
	padding:0px 15px;
	background:inherit;
}
.ss_search_results_selection_active {
	background:url(<html:imagesPath/>pics/background_search_results_active.gif) repeat-x !important;
	text-decoration:none;
}
.ss_search_results_selection_inactive {
}
div.ss_typeToFindResults {
	position:absolute;
	margin: 2px;
	padding: 0px;
	border-top:    solid 1px ${ss_generic_border_color}; 
	border-left:   solid 1px ${ss_generic_border_color}; 
	border-right:  solid 1px ${ss_generic_border_shadow_color}; 
	border-bottom: solid 1px ${ss_generic_border_shadow_color}; 
	background-color: ${ss_style_background_color_opaque};
	z-index:500;
	text-align: left;
}
.ss_typeToFindResults div ul {
	list-style-type:none;
	margin: 0px 0px 0px 0px;
	padding: 0px 5px;
	text-align:left;
}
.ss_typeToFindResults li {
	padding: 0px;
	float:none;
	line-height: 13px;
}

.ss_typeToFindResults li a  {
	font-family: ${ss_style_folder_view_font_family};
	font-size: ${ss_style_font_smallprint};
	font-weight: bold;
	color: #666666;
	text-decoration: none;
}

.ss_typeToFindResults li a:hover {
	font-family: ${ss_style_folder_view_font_family};
	font-size: ${ss_style_font_smallprint};
	font-weight: bold;
	color: #333333;
	text-decoration: none;
}

table.ss_typeToFindNav {
	width: 150px;
	padding-top: 3px;
	padding-bottom: 1px;
}


a.ss_title_menu img {
	margin-right:4px;
}

ul.ss_title_menu {
  list-style: outside;
  text-align:left;
  margin:0px 2px 4px 13px;
  padding: 0px;
}

ul.ss_title_menu li {
  list-style-type: square;
  margin-left: 0px;
  margin-bottom: 0px;
  padding-left: 2px;
} 

div.ss_title_menu {
	display:inline;
}

/* Footer */
div.ss_bottomlinks {
	display:inline;
	margin:5px 0px;
	padding:0px;
	color:#8E8FA7;
}

.ss_bottomlinks *, span.ss_bottomlinks {
	color:#8E8FA7 !important;
}

.ss_innerContentBegins{
	margin-top:9px;
}

/* Profile elements */
.ss_table_spacer_right {
	padding-right:3px;
}

/* Photo gallery */
div.ss_thumbnail_gallery {
    padding-left: 10px;
    padding-right: 10px;
    padding-bottom: 0px;
    padding-top: 0px;
}

div.ss_thumbnail_standalone {
	padding: 0px;
}

div.ss_thumbnail_small div {
    width: 77px;
    height: 105px;
    margin-left:   5px;
    margin-right:  5px;
}
div.ss_thumbnail_small_no_text div {
    width: 54px;
    height: 54px;
    margin-left:   5px;
    margin-right:  5px;
}
div.ss_thumbnail_small_no_text a img {
    opacity: 0.60;
<c:if test="<%= isIE %>">
    filter:alpha(opacity=60);
</c:if>
}

div.ss_thumbnail_standalone a img {
    opacity: 1.0 !important;
<c:if test="<%= isIE %>">
    filter:alpha(opacity=100) !important;
</c:if>
}

div.ss_thumbnail_standalone_small a:hover img {
    border-color: #003782;
}


div.ss_thumbnail_small_no_text a:hover img {
    border-color: #003782;
    opacity: 1.0;
<c:if test="<%= isIE %>">
    filter:alpha(opacity=100);
</c:if>
}
div.ss_thumbnail_small img {
    width: 75px;
    height: 75px;
}
div.ss_thumbnail_small_no_text img,  div.ss_thumbnail_standalone_small img {
    width: 50px;
    height: 50px;
    margin-right: 5px;    
}

div.ss_thumbnail_small_buddies_list, div.ss_thumbnail_small_buddies_list img {
    width: 35px;
    height: 35px;
    background-color: ${ss_gallery_background_color};
    color: ${ss_style_metadata_color};    
    vertical-align: middle;
	text-align:center;
	font-weight:bold;
	font-size:${ss_style_font_fineprint};
}


div.ss_thumbnail_big div {
    width: 152px;
    height: 180px;
    margin-left:   10px;
    margin-right:  10px;
}
div.ss_thumbnail_big img {
    width: 150px;
    height: 150px;
}
div.ss_thumbnail_medium div {
    width: 102px;
    height: 132px;
    margin-left:   10px;
    margin-right:  10px;
}
div.ss_thumbnail_medium img {
    width: 100px;
    height: 100px;
}
div.ss_thumbnail_gallery div {
    float: left;
    margin-top:    20px;
    margin-bottom: 10px;
    text-align: center;
    font-size: 8pt;
    font-family: ${ss_style_font_family};
    overflow: hidden;
    background-color: ${ss_gallery_background_color};
}
    
.noImg {
	color: ${ss_style_metadata_color};   
    vertical-align: middle;
	font-weight: bold;
}
	
div.ss_thumbnail_gallery img, div.ss_thumbnail_standalone img {
    border-width: 1px;
    border-style: solid;
    border-color: ${ss_gallery_image_background_color};
}
div.ss_thumbnail_gallery a {
    padding: 0px;
    text-decoration: none;
    color: ${ss_gallery_anchor_color};
}

div.ss_faded a img {
    opacity: 0.5;
<c:if test="<%= isIE %>">
    filter:alpha(opacity=50);
</c:if>
}

div.ss_faded_alot a img {
    opacity: 0.25;
<c:if test="<%= isIE %>">
    filter:alpha(opacity=25);
</c:if>
}

div.ss_faded_abit a img {
    opacity: 0.75;
<c:if test="<%= isIE %>">
    filter:alpha(opacity=75);
</c:if>
}

div.ss_faded_pop a:hover img {
    opacity: 1.0;
<c:if test="<%= isIE %>">
    filter:alpha(opacity=100);
</c:if>
}


div.ss_thumbnail_gallery a:hover img {
    border-color: ${ss_gallery_anchor_hover_color};
}
div.ss_thumbnail_gallery a:hover {
    text-decoration: underline;
    color: ${ss_gallery_anchor_hover_color};
}
div.ss_end_thumbnail_gallery {
    clear: both;
}

/* Presence styles */
.ss_presence_green_dude {
    background:url(<html:imagesPath/>pics/sym_s_green_dude.gif) no-repeat left;
}
.ss_presence_gray_dude {
    background:url(<html:imagesPath/>pics/sym_s_gray_dude.gif) no-repeat left;
}
.ss_presence_white_dude {
    background:url(<html:imagesPath/>pics/sym_s_white_dude.gif) no-repeat left;
}
.ss_presence_yellow_dude {
    background:url(<html:imagesPath/>pics/sym_s_yellow_dude.gif) no-repeat left;
}

/* Accessible mode styles */
.ss_treeIframeDiv {
	position:absolute;
	background-color:${ss_style_background_color_opaque};
}

/* CSS document for table - author: rsmart 1.23.07 v02 */
.ss_table{
	font-family: ${ss_table_font_family};
	border-collapse: collapse;
}
/* table head - dark grey with black border top */	
.ss_table thead tr{
	background-color: ${ss_table_background_color_head};
	border-top: 1px solid black;
}
/* table head - dark grey with black border bottom */
.ss_table thead th{
	padding: 0.5em;
	white-space: nowrap;
	border-bottom: 1px solid black;
}
/* row is white */
.ss_table tr{
background-color: ${ss_table_background_color_even_row};
}
.ss_table_tr_even{
background-color: ${ss_table_background_color_even_row};
}
/* row is lite grey */
.ss_table_tr_odd{
background-color: ${ss_table_background_color_odd_row} !important;
white-space: nowrap !important;
}

/* yellow hover for table rows */	
.ss_table tbody tr:hover{
	background-color: ${ss_table_background_color_row_hover} !important;
}
/* cell and row border */
.ss_table td, .ss_table th {
	border: 1px dotted ${ss_generic_border_color};
}
.ss_table tbody td {
	padding: 0.5em;
}
/* work around to eliminate borders on nested tables */

.ss_table_tr_noborder{
	background-color: ${ss_table_background_color_odd_row} !important;
	border: 1px dotted #ECECEC !important;
	border-collapse: collapse !important;
	border-bottom: 1px solid black !important;
	padding: 10px !important;
}
.ss_table_td_noborder{
	background-color: ${ss_table_background_color_odd_row} !important;
	border: 1px dotted #ECECEC!important;
	padding-top: 10px !important;
	padding-right: 0px !important;
	padding-bottom: 10px !important;
	padding-left: 0px !important;	
}
.ss_table_noborder_td:hover{
	background-color: ${ss_table_background_color_odd_row};
	border-color:#ECECEC;
}

/* fonts for master file tables */

.ss_table_paragraph_bld{
	font-size: ${ss_style_font_normalprint};
	color: ${ss_style_text_color};
	font-weight: bold;
	margin-left: 25px;
	}
.ss_table_paragraph{
	font-size: ${ss_style_font_smallprint};
	color: ${ss_style_text_color};
	margin-left: 0px;
}
.ss_table_header{
	font-size: ${ss_style_font_normalprint};
	color: ${ss_style_text_color};
	font-weight: bold;
	margin-left: 0px;
}
table.ss_table thead th.ss_table_smheaders {
	font-family: ${ss_style_folder_view_font_family};
	font-size: ${ss_style_font_fineprint};
	font-weight: bold;
	color: ${ss_style_text_color};
	line-height: normal;
	white-space: normal;
}
.ss_table_paragraph_red {
	font-size: ${ss_style_font_smallprint};
	color: #FF0000 !important;
	margin-left: 25px;
}

/* Muster div */
.ss_muster_div {
	position:absolute;
}

/* Muster div */
.ss_calendar_popup_div {
	position:absolute;
}

.ss_miniBusinessCard { 
	vertical-align: top;
	width: 2%;
}
table.ss_guestbookContainer {
	border-spacing: 5px;
	vertical-align: top;	
}
td.ss_guestbookContainer {
	vertical-align: top;
	padding: 10px;
}

table.ss_searchviewContainer {
	border-spacing: 1px;
	vertical-align: top;	
}
td.ss_searchviewContainer {
	vertical-align: top;
	padding: 4px;
}

table.ss_searchviewDashboardContainer {
	border-spacing: 1px;
	vertical-align: top;
	margin-left: 2px;
	margin-right: 2px;
}
td.ss_searchviewDashboardContainer {
	vertical-align: top;
	padding: 2px;
}

/*
 ************************************************************************
 *                     Calendar View Styles                             *
 ************************************************************************
 */


/*
 * The following styles are used for calendar grid layouts.
 * hr = horizontal rules/dividers
 * vr = vertical rules/dividers
 */

table.ss_cal_gridTable  {
  margin-top: 2px;
  margin-bottom: 0px; 
  border-collapse: collapse;
  border-width: 0px;
  width: 100%; 
}

table.ss_cal_gridTable td {
  padding-left:   0px;
  padding-right:  1px;
  padding-top:    0px;
  padding-bottom: 0px;
}


/* Discourage rubber-banding views to useless widths */
.ss_cal_reserveWidth {
  min-width: 300px;
}


/*
 * Used to create a highlight in the grid when the current
 * is visible.
 */
div.ss_cal_todayMarker {
  background-color: #AFC8E3;
  position: absolute;
}


/*
 * Header for grids
 */

.ss_cal_gridHeader {
  position: relative;
  width: 100%;
  height: 20px;
  background-color: ${ss_entry_description_background_color};
}

div.ss_cal_gridHeaderText {
  position: absolute;
  text-align: center;
  font-size: 10px;
  font-family: sans-serif;
  padding-top: 3px;
  color: #888888;

}

div.ss_cal_gridHeaderTextToday {
  background-color: #E8EFF7;
  color: #666666;
}

div.ss_cal_gridHeaderText a {
  color: #666666;
  text-decoration: none;
}

div.ss_cal_gridHeaderText a:hover {
  color: #888888;
  text-decoration: underline;
}


/*
 * Day Grid styles
 */


/* Used to establish drawing grids for the day view */

div.ss_cal_dayGridHour {
  height: 1008px;
  position: relative;
}

/*
 * Overrides to the dayGridHour for the "all day" events area.
 * Tied to a div with the same id.  Note that the height is the
 * INITIAL height.  The element's actual height is set based
 * on content.
 */

div.ss_cal_dayGridAllDay {
  height: 25px;
  min-width: 300px;
  overflow-y: hidden;
}


/* A visual divider between the all-day grid and the hour grid */
div.ss_cal_dayGridDivider {
  height: 3px;
  width: 100%;
  background-color: ${ss_entry_description_background_color};
}


/* Left-hand column with the 24 hours of the day */
td.ss_cal_dayGridHourTicksColumn {
	width: 35px;
  	color:#666666;
	font-family:sans-serif;
	font-size:11px;
	padding-right:3px;
	padding-top:3px;
	text-align:right;  
}

div.ss_cal_timeHead {
  text-align: right;
  padding-right: 3px;
  padding-top: 3px;
  font-size: 11px;
  font-family: sans-serif;
  color: #666666;
}

/* Styles to divide up the grid visually */

div.ss_cal_hrHalfHour { border-top: 1px dotted #DDDDDD; }

div.ss_cal_hrHour { border-top: 1px solid  #DDDDDD; }

div.ss_cal_hr {
  left: 0px;
  position: absolute;
  width: 100%;
}

div.ss_cal_dayRule {
  position: absolute;
  top: 0px;
  height: 1008px;
  width: 1px;
  border-left: 2px solid #DDDDDD;
}


/*
 * The inner/outer styles are tied to divs with the same ids.  They
 * create an expansion window for the day grid to show working hours
 * or a full 24-hour day.  The heights and top properties are changed
 * with JavaScript to achieve the effect.
 *
 * If you play with the values, make coordinated changes to the 
 * JavaScript routines.
 */

div.ss_cal_dayGridWindowOuter {
  height: 500px;     /* Note: This is modified dynamically in the element */
  top: 0px;
  position: relative;
  overflow: hidden;
}

div.ss_cal_dayGridWindowInner {
  position: absolute;
  top: -255px;    /* Note: This is modified dynamically in the element */
}




/*
 * Month Grid layout styles
 */

div.ss_cal_monthGrid {
  position: relative;
  width: 99.8%;
  height: 320px;
}


div.ss_cal_monthVRule {
  position: absolute;
  top: 0px;
  height: 100%;
  width: 1px;
  border-left: 1px solid #DDDDDD;
}

div.ss_cal_monthHRule {
  position: absolute;
  left: 0px;
  height: 1px;
  width: 100%;
  border-top: 1px solid #DDDDDD;
}


/* Day of month numbers */
div.ss_cal_monthGridDayBadgeCurrent, div.ss_cal_monthGridDayBadge {
  background-color: #ECECEC;
  position: absolute;
  height: 11px;
  width: 14.2857%;
  border-top: 1px solid #DDDDDD;
  border-left: 1px solid #DDDDDD;
  text-align: right;
  font-size: 9px;
  font-family: sans-serif;
}

div.ss_cal_monthGridDayBadgeCurrent a, div.ss_cal_monthGridDayBadge a {
  position: relative;
  right: 3px;
  top: -2px;
}

/* Highlight for today */
div.ss_cal_monthGridDayBadgeToday {
  background-color: #E8EFF7;
  color: #FFFFFF;
}

div.ss_cal_monthGridDayBadge a {
  color: #BBBBBB;
  text-decoration: none;
}

div.ss_cal_monthGridDayBadgeCurrent a {
  color: #666666;
  text-decoration: none;
}

div.ss_cal_monthGridDayBadge a:hover, div.ss_cal_monthGridDayBadgeCurrent a:hover {
  color: #BBBBBB;
  text-decoration: underline;
}

a.ss_calDaySelectButton img, a.ss_calDaySelectButtonActive img, 
	a.ss_cal3DaysSelectButton img, a.ss_cal3DaysSelectButtonActive img,
	a.ss_cal5DaysSelectButton img, a.ss_cal5DaysSelectButtonActive img,
	a.ss_cal7DaysSelectButton img, a.ss_cal7DaysSelectButtonActive img,
	a.ss_cal14DaysSelectButton img, a.ss_cal14DaysSelectButtonActive img,
	a.ss_calMonthSelectButton img, a.ss_calMonthSelectButtonActive img {
	width: 25px;
	height: 24px;
	margin: 0px;
	padding: 0 5px 0 0;
	border: 0px;
	vertical-align: bottom;
	position: relative;
	top: -4px;
}

a.ss_calDaySelectButton img, a.ss_calDaySelectButton:link img , a.ss_calDaySelectButton:focus img, a.ss_calDaySelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/day_u.gif) no-repeat top left; 
}

a.ss_calDaySelectButtonActive img, a.ss_calDaySelectButtonActive:link img , a.ss_calDaySelectButtonActive:focus img, a.ss_calDaySelectButtonActive:visited img { 
	background: transparent url(<html:imagesPath/>icons/day.gif) no-repeat top left; 
}

a.ss_cal3DaysSelectButton img, a.ss_cal3DaysSelectButton:link img , a.ss_cal3DaysSelectButton:focus img, a.ss_cal3DaysSelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/3_day_u.gif) no-repeat top left; 
}

a.ss_cal3DaysSelectButtonActive img, a.ss_cal3DaysSelectButtonActive:link img , a.ss_cal3DaysSelectButtonActive:focus img, a.ss_cal3DaysSelectButtonActive:visited img { 
	background: transparent url(<html:imagesPath/>icons/3_day.gif) no-repeat top left; 
}

a.ss_cal5DaysSelectButton img, a.ss_cal5DaysSelectButton:link img , a.ss_cal5DaysSelectButton:focus img, a.ss_cal5DaysSelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/5_day_u.gif) no-repeat top left; 
}

a.ss_cal5DaysSelectButtonActive img, a.ss_cal5DaysSelectButtonActive:link img , a.ss_cal5DaysSelectButtonActive:focus img, a.ss_cal5DaysSelectButtonActive:visited img { 
	background: transparent url(<html:imagesPath/>icons/5_day.gif) no-repeat top left; 
}

a.ss_cal7DaysSelectButton img, a.ss_cal7DaysSelectButton:link img , a.ss_cal7DaysSelectButton:focus img, a.ss_cal7DaysSelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/7_day_u.gif) no-repeat top left; 
}

a.ss_cal7DaysSelectButtonActive img, a.ss_cal7DaysSelectButtonActive:link img , a.ss_cal7DaysSelectButtonActive:focus img, a.ss_cal7DaysSelectButtonActive:visited img { 
	background: transparent url(<html:imagesPath/>icons/7_day.gif) no-repeat top left; 
}

a.ss_cal14DaysSelectButton img, a.ss_cal14DaysSelectButton:link img , a.ss_cal14DaysSelectButton:focus img, a.ss_cal14DaysSelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/14_day_u.gif) no-repeat top left; 
}

a.ss_cal14DaysSelectButtonActive img, a.ss_cal14DaysSelectButtonActive:link img , a.ss_cal14DaysSelectButtonActive:focus img, a.ss_cal14DaysSelectButtonActive:visited img { 
	background: transparent url(<html:imagesPath/>icons/14_day.gif) no-repeat top left; 
}

a.ss_calMonthSelectButton img, a.ss_calMonthSelectButton:link img , a.ss_calMonthSelectButton:focus img, a.ss_calMonthSelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/month_u.gif) no-repeat top left; 
}

a.ss_calMonthSelectButtonActive img, a.ss_calMonthSelectButtonActive:link img , a.ss_calMonthSelectButtonActive:focus img, a.ss_calMonthSelectButtonActive:visited img { 
	background: transparent url(<html:imagesPath/>icons/month.gif) no-repeat top left; 
}

a.ss_calDaySelectButton:hover img, 
	a.ss_cal3DaysSelectButton:hover img,
	a.ss_cal5DaysSelectButton:hover img,
	a.ss_cal7DaysSelectButton:hover img,
	a.ss_cal14DaysSelectButton:hover img,
	a.ss_calMonthSelectButton:hover img {
    background-position:  left -24px; 
}

a.ss_calDateDownButton img {
	width: 12px;
	height: 28px;
	margin: 0px;
	padding: 0px;
	border: 0px;
	vertical-align: bottom;
}

a.ss_calDateDownButton img, a.ss_calDateDownButton:link img , a.ss_calDateDownButton:focus img, a.ss_calDateDownButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/date_down.gif) no-repeat top left; 
}

a.ss_calDateDownButton:hover img {
    background-position:  left -28px; 
}

a.ss_calDateUpButton img {
	width: 12px;
	height: 28px;
	margin: 0px;
	padding: 0px;
	border: 0px;
	vertical-align: bottom;
}

a.ss_calDateUpButton img, a.ss_calDateUpButton:link img , a.ss_calDateUpButton:focus img, a.ss_calDateUpButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/date_up.gif) no-repeat top left; 
}

a.ss_calDateUpButton:hover img {
    background-position:  left -28px; 
}


a.ss_calendarButton img {
	width: 14px;
	height: 16px;
	margin: 0px;
	padding: 0px;
	border: 0px;
	vertical-align: bottom;
}

a.ss_calendarButton img, a.ss_calendarButton:link img , a.ss_calendarButton:focus img, a.ss_calendarButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/calendar.gif) no-repeat top left; 
}

a.ss_calendarButton:hover img {
    background-position:  left -16px; 
}
 
 
.ss_calendar_defaultCalendar {
	border-color: #CCCCCC;
	background-color: #E8EFF7;
}

.ss_calendar_calendar0 {
	border-color: #66AA66;
	background-color: #88CC88;
}

.ss_calendar_calendar1 {
	border-color: #AA66AA;
	background-color: #CC88CC;
}

.ss_calendar_calendar2 {
	border-color: #66AAAA;
	background-color: #88CCCC;
}

.ss_calendar_calendar3 {
	border-color: #AAAA66;
	background-color: #CCCC88;
}

.ss_calendar_calendar4 {
	border-color: #CCCCCC;
	background-color: #E8EFF7;
}

.ss_calendar_more_box {
	position: absolute;
	z-index: 2003;
	background-color: transparent;
	border: 2px solid #AFC8E3;
	background-color: #FFFFFF;
}

.ss_calendar_more_box_header {
	height: 19px;
	width: 100%;
	background-color: #ECECEC;
}

.ss_calendar_more_box_close {
	float: right;
  	background-image: url(<html:imagesPath/>pics/popup_close_box.gif);
	background-repeat: no-repeat;
	height: 13px;
	width: 13px;
	margin-top: 3px;
	margin-right: 3px;
}
			  

/*
 * Calendar events are displayed in small boxes with rounded corners.
 * The rounding mechanism is suitable for any small rounded box, so there is no
 * calendar-specific designation in the class names.
 *
 * You construct the box by placing divs in the following order:
 *   box top 2
 *   box top 1
 *   box body
 *   box bottom 1
 *   box bottom 2
 *
 */
div.ss_cal_smallRBoxTop2 {
  line-height: 1px;
  margin: 0 2px;
  font-size: 1px;
  border-top-width: 1px;
  border-top-style: solid;
}

div.ss_cal_smallRBoxTop1 {
  line-height: 1px;
  margin: 0 1px;
  height: 1px;
  font-size: 1px;
  border-right-width: 1px;
  border-right-style: solid;
  border-left-width: 1px;
  border-left-style: solid;
  border-top: 0;
  border-bottom: 0;
}


div.ss_cal_smallRBoxBtm1 {
  line-height: 1px;
  margin: 0 1px;
  height: 1px;
  font-size: 1px;
  border-right-width: 1px;
  border-right-style: solid;
  border-left-width: 1px;
  border-left-style: solid;
  border-top: 0;
  border-bottom: 0;
}

div.ss_cal_smallRBoxBtm2 {
  line-height: 1px;
  margin: 0 2px;
  font-size: 1px;
  border-bottom-width: 1px;
  border-bottom-style: solid;
}

<c:set var="sboxMargin" value="0px" />
<c:if test="<%= isIE %>">
    <c:set var="sboxMargin" value="0px" />
</c:if>
div.ss_smallRBoxTop2 {
  line-height: 1px;
  margin: ${sboxMargin} 2px;
  height: 1px;
  font-size: 1px;
}

div.ss_smallRBoxTop1 {
  line-height: 1px;
  margin: ${sboxMargin} 1px;
  height: 1px;
  font-size: 1px;
}


div.ss_smallRBoxBtm1 {
  line-height: 0px;
  margin: ${sboxMargin} 1px;
  height: 1px;
  font-size: 0px;
}

div.ss_smallRBoxBtm2 {
  line-height: 0px;
  margin: ${sboxMargin} 2px;
  height: 1px;
  font-size: 0px;
}

div.ss_smallRBoxBody {
  position: relative;
  overflow: hidden;
}


div.ss_profileBox1 {
    font-family:${ss_style_folder_view_font_family};
}
div.ss_profileBox2 {
  background-color: ${ss_replies_background_color};
/*  width: 230px; */
}
div.ss_profile_box_title {
  font-size: 13px;
  color: ${ss_style_metadata_color};
  height: 20px;
}

div.ss_profile_matte {
  padding: 5px;
}
div.ss_profile_picture_frame {
  height: 200px;
  width: 200px;
  text-align: center;
  vertical-align: middle;
}

div.ss_profile_info_frame {
  height: 200px;
  text-align: center;
  vertical-align: top;
}

div.ss_profile_photo_box_empty {
    background-color: transparent !important;
	background-image: url(<html:brandedImagesPath/>pics/watermark_person.gif);
    background-position:  center;
	background-repeat: no-repeat;
}


table.ss_minicard_interior {
	background-color: white;
	padding-right: 5px;
}


/*
 * Calendar event boxes
 */

div.ss_calendarNaviBarAccessible {	
	font-family: Arial;
	border: 1px solid #CCCCCC;
	background-color: #AFC8E3;
	height: 30px;
	vertical-align: middle;
	padding: 0px;
	margin: 0px 0px 5px 0px;
}

div.ss_cal_eventBody {
  font-size: 11px;
  color: #666666;
  font-family: sans-serif;
  margin: 0px;
  border-right-width: 1px;
  border-right-style: solid;
  border-left-width: 1px;
  border-left-style: solid;
  border-top: 0;
  border-bottom: 0;
}


div.ss_cal_eventBox {
  overflow: hidden;
  position: absolute;
  border: 0;
}

div.ss_cal_moreBox {
	background-color: #BBBBBB;
}

div.ss_cal_moreBox a, div.ss_cal_moreBox a:hover {
	text-decoration: none;
}

div.ss_cal_monthEventBody {
  font-size: 11px;
  color: #666666;
  font-family: sans-serif;
  margin: 0px;
  border-right-width: 1px;
  border-right-style: solid;
  border-left-width: 1px;
  border-left-style: solid;
  border-top: 0;
  border-bottom: 0;
  overflow: hidden;
}

div.ss_cal_monthEventBody a, div.ss_cal_monthEventBody a:hover, 
	div.ss_cal_monthEventBody a:link, div.ss_cal_monthEventBody a:active,
	div.ss_cal_monthEventBody a:visited {
	text-decoration: none;
	color: #666666;
}

/* Used to render the short-form time of the event */
span.ss_cal_eventTime {
  padding-right: 3px;
}

.ss_calendar_today {
  background-color: ${ss_calendar_today_background_color};
}

.ss_calendar_notInView {
  background-color: ${ss_calendar_today_background_color};
}

ul.ss_calendarNaviBar {	
	display: table;
	font-family: Arial;
	border: 1px solid #CCCCCC;
	background-color: #AFC8E3;
	list-style-type: none;
	height: 30px;
	padding: 0px;
	margin: 0px 0px 5px 0px;
	width: 100%;
}

ul.ss_calendarNaviBar li {
	display: table-cell;
	list-style-image:none;
	list-style-position:outside;
	list-style-type:none;
}

ul.ss_calendarNaviBar li.ss_calendarNaviBarOption, ul.ss_calendarNaviBar .ss_calendarNaviBarSeparator {
	position:relative;
	vertical-align: middle;
	float: left;
	height: 30px;
	line-height: 30px;
}

ul.ss_calendarNaviBar li.ss_calendarNaviBarOption {	
	padding: 0px 4px 0px 4px;
	margin: 0px;	
}

ul.ss_calendarNaviBar .ss_calendarNaviBarSeparator {
	border-left: 1px dotted #FFFFFF;
	margin: 0px 5px 0px 5px;
	padding: 0px;
	width: 0px;
}

ul.ss_calendarNaviBar .ss_hoursSelectorTitle {
	background-color: #AFC8E3;
	color: #333333;
	text-decoration: none;
	border: 1px solid #FFFFFF;
	font-size: 11px;
}

ul.ss_calendarNaviBar li.ss_calHoursSelectorMenu {
 <c:if test="<%= isIE %>">
	padding-top: 5px;
 </c:if>
}

ul.ss_calendarNaviBar li.ss_calendarNaviBarOptionMiddleImg {
	padding-top: 3px;
}

ul.ss_calendarNaviBar li.ss_calendarNaviBarOption > a > img.ss_fullHeight {
	margin: 1px 0px 1px 0px;
}

ul.ss_calendarNaviBar li.ss_calendarNaviBarOption div.ss_toolbar_color {
	background-color: #AFC8E3;
}

ul.ss_calendarNaviBar li.ss_calendarNaviBarOption a.ss_calendarButton img {
	margin: 7px 0px 7px 0px;
}

ul.ss_calendarNaviBar li.ss_calViewDatesDescriptions td {	
	color: #FFFFFF;
}

ul.ss_calendarNaviBar li.ss_calSelectDate {
	padding: 0px 10px 0px 7px;
}

ul.ss_calendarNaviBar li.ss_taskViewOptions {
	border: 1px solid white; 
	height: 24px; 
	margin: 2px 0px 2px 0px;
}

.ss_hoursSelectorList li  {
	float:none;
	border-bottom:1px solid #CCCCCC;
	border-right-style:none;
    line-height:18px;
}

.ss_hoursSelectorList {
	position:absolute;
 <c:if test="<%= isIE %>">
	top:-8px;
	left:-25px;
 </c:if>
 <c:if test="<%= !isIE %>">
	top:-8px;
	left:-24px;
 </c:if>	
	border:1px solid #907FA3;
	border-top:none;
	padding: 4px 1px;
}

.ss_hoursSelectorList li a {
  display: block;
  padding-left: 10px;
  padding-right: 10px;
  text-decoration: none;
}

.ss_hoursSelectorList div {
	background:none;
}

.ss_hoursSelectorList li:hover, .ss_hoursSelectorList a:hover {
	text-decoration:underline;
	color:#333333;
}
.ss_hoursSelectorList a, .ss_actions_bar_submenu a:visited {
	color:#333333;
}

/* EVENT EDITOR */

.ss_event_editor {
  font-family: ${ss_style_folder_view_font_family};
  font-size: 12px; 
}

.ss_event_editor .ss_requrency_row_active {
	border: 1px solid #CCCCCC;
}

.ss_event_editor .ss_requrency_row_unactive {
	border: 1px solid transparent;
}

.ss_event_editor .ss_event_recurences {
	padding: 7px 0 0 0;
}

.ss_event_editor .ss_event_repeat {
	padding: 7px 0 7px 30px;
}

.ss_event_editor .ss_event_range {
	padding-left: 30px;
}

/* TEAM BUDDIES LIST */

div.ss_buddies {
	padding-bottom: 10px; 
}
div.ss_buddiesListHeader {
	padding: 0px;
	font-family: ${ss_style_title_font_family};
	font-size: ${ss_style_font_largeprint};
	font-weight: bold;
}
div.ss_buddiesListHeader img {
	vertical-align: text-bottom;
}
table.ss_buddiesList {
	width: 100%; 
	empty-cells: show;
	padding: 0px; 
	border-top: 1px solid ${ss_guestbook_rule_color};
}
table.ss_buddiesList td {
	border-bottom: 1px solid ${ss_guestbook_rule_color};
	padding: 6px 0px 7px 0px;
	height: 10px;
}
table.ss_buddiesList td.selectable {
	width: 9px;
	padding-left: 15px;
	padding-right: 15px;
	border: 0px;
}
table.ss_buddiesList td.picture {
	border-bottom: 1px solid ${ss_guestbook_rule_color};
	padding: 6px 5px 7px 8px; 
	width: 24px;
}
table.ss_buddiesList tr.options td {
	border: 0px;
}
table.ss_buddiesList tr.options td.selectall {
	height: 30px;
	text-align: center;
}
div.ss_buddiesListFooter {
	text-align: right;
}


form.inline {
	display: inline;
	border: 0px;
	padding: 0px;
	margin: 0px;	
}

div.ss_teamMembersList, div.ss_clipboardUsersList {
	padding-left: 20px; 
}

/* styles used in the version history comparisons */
.ss_diff_added {
	background-color:${ss_diff_color_added};
}
.ss_diff_deleted {
	background-color:${ss_diff_color_deleted};
	text-decoration:line-through;
}
.ss_diff_same {
	background-color:${ss_diff_color_same};
}

/* Ajax loading... */
#ss_loading, .ss_loading {
	text-align: center;
}


/*
	Search and surveys styles / start
*/
#ss_tab_content {
	padding:6px 6px 12px 6px;
}
#ss_tabs_container {
	border-left: 1px solid #cccccc;
	border-right:1px solid #cccccc;
	border-bottom:1px solid #cccccc;
	position:relative;
    margin: 0px;
    padding: 0px;
    width: 100%;
}

#ss_rankings { 
	width: 215px;
	vertical-align: top;
}
#ss_content_container {
    padding:0px;
    margin:0px;
    vertical-align:top;
}
div.ss_searchContainer, div.ss_surveyContainer {
	background: transparent url(<html:imagesPath/>pics/top_border.gif) repeat-x top left;
	padding:5px 0px 0px 0px;
	margin: 0px 0px 0px 15px;
	border-right:1px solid #afc8e3;
	width:95%;
<c:if test="<%= isIE %>">
  zoom:1; /* a workaround IE bug */
</c:if>	
}
p.ss_survey_question, span.ss_survey_answer {
	font-size: ${ss_style_font_normalprint};
	color:#333333;
}

div.ss_searchContainer #ss_content { 
	border-left: 1px solid #afc8e3; 
    border-bottom: 1px solid #afc8e3; 
	margin: 0px;
	padding: 0px;
<c:if test="<%= isIE %>">
  zoom:1; /* a workaround IE bug */
</c:if>	
}

#ss_searchForm_container, #ss_surveyForm_container {margin:0px; padding:0px; width:100%;}
#ss_searchForm, #ss_surveyForm {
	background: #e8eff7 url(<html:imagesPath/>pics/left_border.gif) repeat-y top left;
	padding:6px;
	margin:0px;
	border-bottom:1px solid #afc8e3;
}
#ss_surveyForm_main form {
	background-color: transparent;
}
#ss_searchForm table, #ss_surveyForm_main {
	background-color: #e8eff7;
}
#ss_searchForm input { width: 150px;}
#ss_searchForm th {text-align:left;padding: 0px 0px 0px 12px;}
#ss_searchForm td {text-align:left;padding: 0px 12px 0px 12px;}
#ss_searchForm h4 {margin:0px 0px 6px 0px;padding:0px;}
#ss_searchForm ul {
	margin: 0px 0px 5px 0px;
	padding: 0px;
}
#ss_searchForm li {
	margin: 0px;
	padding: 0px;
}
#ss_searchForm_additionalFilters {
	background-color:#ffffff;
	margin:0px;
	padding:0px;
	width:100%;
}
a.ss_searchButton img {
	width: 20px;
	height: 16px;
	margin: 0px;
	padding: 0px;
	border: 0px;
	vertical-align: bottom;
}

a.ss_searchButton img, a.ss_searchButton:link img , a.ss_searchButton:focus img, a.ss_searchButton:visited img { 
	background: transparent url(<html:rootPath/>images/pics/searchheadbkg.png) no-repeat top left; 
}

a.ss_searchButton:hover img {
    background-position:  left -16px; 
<ssf:ifaccessible>
  outline: dotted 1px gray;
</ssf:ifaccessible>  
}

a.ss_advanced:link, a.ss_advanced:hover, a.ss_advanced:visited, a.ss_advanced:active {
	color: #0000FF;
	text-decoration:underline;
	margin:0px 0px 0px 6px;
}
a.ss_parentPointer:hover, a.ss_parentPointer:link, a.ss_parentPointer:hover, a.ss_parentPointer:active {
	color: #333333;
	text-decoration:underline;
	margin:0px 0px 0px 0px;
<ssf:ifaccessible>
  outline: dotted 1px gray;
</ssf:ifaccessible>  
	}
a.ss_parentPointer:hover {
	color: #0000ff;
	text-decoration:underline;
<ssf:ifaccessible>
  outline: dotted 1px gray;
</ssf:ifaccessible>  
}
a.ss_parentPointer:visited, a.ss_parentPointer:hover {
	color: purple;
	text-decoration:underline;
}	
a.ss_parentPointer:visited, a.ss_parentPointer:hover {
	color: #0000ff;
	text-decoration:underline;
}

.ss_searchResult {
	margin: 0px;
	padding:0px;
<c:if test="<%= isIE %>">	
	zoom: 1; /* a workaround IE bug - parent border disappears */
	/* width: 502px;*/
</c:if>
}
.ss_searchResult li {
	border-bottom: 1px solid #cccccc;
	display:block;
	margin:12px 12px 12px 12px;	
}

.ss_searchResult li.last {
	border-bottom: none;
}

.ss_searchResult_header_top, .ss_searchResult_header_bottom {
	margin: 0px 0px 24px 0px;
	padding: 3px 24px 3px 12px;
<c:if test="<%= isIE %>">	
	zoom: 1; /* a workaround IE bug - parent border disappears */
</c:if>
}

.ss_searchResult_header_top {
	border-bottom: 1px solid #afc8e3;	
}

.ss_searchResult_header_bottom {
	border-top: 1px solid #afc8e3;	
}

.ss_searchResult_numbers {
	float:left;
	padding: 3px 24px 3px 0px;
}
.ss_paginator {float:left;}

.ss_searchResult_dashboardHeader {
	border-bottom: 1px solid #afc8e3;	
	padding: 3px 0px 3px 12px;
}
.ss_searchResult_dashboardNumbers {
	float:right;
	padding: 0px 24px 3px 0px;
}
.ss_dashboardPaginator {
	float:right; 
	padding: 0px 24px 0px 0px;
}

div.ss_thumbnail {float: left; width:24px; text-align:center;}
div.ss_thumbnail img {width:24px;height:24px;padding:0px; margin:0px;}
div.ss_entry {
	float: left;
}
div.ss_entry p {
	margin-top:    2px;
	margin-bottom: 2px;

}
div.ss_entry_folderListView {
	float: left;
	width: 600px;	
}
h3.ss_entryTitle {
   /* float:left;margin:0px; */
}
h3.ss_entryTitle a {
	color: #333333;
}

h3.ss_entryTitle a:hover,h3.ss_entryTitle a:link, h3.ss_entryTitle a:active {
	color:#0000FF !important;
}

h3.ss_entryTitle a:visited {
	color:  purple;
}
div.ss_more {text-align:right;}
div.ss_entryDetails {padding:0px 0px 6px 24px;}
div.ss_entryDetails p {
	margin:0px 3px 1px 0px;
	font-size: ${ss_style_font_smallprint};
	color: #999999;
}
img.ss_attachment_thumbnail {width:80px;height:74px;padding:0px; margin:0px;}
.ss_label {}

.ss_rating_box {
	margin:0px 0px 4px 0px;
	padding:0px;
}
.ss_rating_box_content {
	margin:0px;
	padding: 6px;
	font-size: ${ss_style_font_smallprint};
}
img.ss_toogler {float:right;}
img.ss_toggler {float:right;}
div.ss_rating_box_content table {width:200px; border-collapse: collapse; border-spacing: 0px;}
div.ss_rating_box_content th {border-bottom: 1px solid #afc8e3;text-align:left; font-size: 11px;}
div.ss_rating_box_content td {text-align:left;font-size: 11px;}
div.ss_rating_box_content td.ss_star {padding-left:5px;}
div.ss_rating_box_content h5 {margin:0px; border-bottom: 1px solid #afc8e3;font-size: 11px;  }
div.ss_rating_box_content p {margin:6px 0px 0px 0px; font-size: 11px;}

span.ss_pageNumber {
	margin: 0px 2px 0px 2px;
}

span.ss_pageNumber a {

}

a.ss_pageNumberCurrent {
	font-size: 14px;
}
div.ss_options_container {border-bottom:1px solid #afc8e3; margin:0px 0px 0px 0px;padding:0px 12px 12px 12px;}
div.ss_options {padding: 6px 0px 12px 0px;}
div.ss_options_container h4 {margin:6px 0px 6px 0px;}
a.ss_button {
	background-color: #dbe6f2;
	border: 1px solid #afc8e3;
	padding: 0px 6px 0px 12px;
}
#ss_filterSummary_content {float:left;}
#ss_filterSummary_content p {margin:6px;}
#ss_filterSummary_content h4 {margin:6px 0px 0px 6px;}
#ss_filterSummary_switch {float:right; padding: 6px 12px 6px 0px;}
#ss_searchForm_filterSummary {border-bottom:1px solid #afc8e3;}

#ss_searchForm_main h4 {float:left;}
#ss_searchForm_main a.ss_advanced {float:right;}
#ss_searchForm_main table {
	width: 100%;
}
#ss_searchForm_main th, #ss_searchForm_main td, #ss_surveyForm_main th, #ss_surveyForm_main td {
	vertical-align: top;
}
#ss_searchForm_main table div.ss_additionals {
	margin-top: 10px;
}
input.ss_saveQueryName, input.ss_saveQueryNameUnactive {
	width: 100px;
}

input.ss_saveQueryNameUnactive {
	color: #CCCCCC;	
}

input.ss_saveQueryName {
	color: #333333;
}

#ss_saveQueryErrMsg {
	font-weight: bold;
}

#ss_surveyForm_questions {
	margin:0px 0px 0px 0px;
}
div.ss_questionContainer {
	border-bottom: 1px solid #afc8e3;
	padding: 0px 0px 10px 0px;
	margin: 0px 0px 5px 0px;
	background-color:#ffffff;
}
div.ss_questionContainer h4 {
	border: 1px solid #afc8e3;
	background-color:#e8eff7;
	margin:0px;
}
div.ss_questionContainer h4 img, div.ss_questionContainer li img {
	border: 0px;
	margin:6px;
	vertical-align:text-bottom;
}
div.ss_questionContainer h4 span {
	vertical-align:top;
}
div.ss_questionContainer input {width:300px;}

div.ss_content_outer table.ss_milestones_list, div.ss_content_outer table.ss_surveys_list  {
	empty-cells: show;
	border-collapse: collapse;
}

table.ss_surveys_list, table.ss_milestones_list {
	width:100%;
	margin-top:6px;
	padding: 0px;
}
table.ss_surveys_list th, table.ss_milestones_list th {
	background-color: #dbe6f2;
	border:1px solid #afc8e3;
	text-align: left;
	padding: 3px 3px 3px 3px;
}
table.ss_surveys_list td, table.ss_milestones_list td {
	padding: 6px  3px 6px 3px;
	border-bottom: 1px solid #afc8e3;
}
div.ss_questionContainer input.ss_survay_checkbox, div.ss_questionContainer input.ss_survay_radio {
	width:20px;
	vertical-align:bottom;
}

div.ss_survey_required {
	background-color: ${ss_form_element_color};
}

ul.ss_survey_users_list {
	margin: 0;
	padding: 0;
}

table.ss_milestones_list td.ss_completed, table.ss_milestones_list td.ss_completed a {
	color: #999966;
}

table.ss_milestones_list td.ss_overdue, table.ss_milestones_list td.ss_overdue a {
	color: #CC0000;
	font-weight: bold;
}

.ss_navbarPopupPane {
	display: none;
	position: absolute;
	z-index: 500;
	background-color: ${ss_style_background_color_opaque};
	border: 1px solid ${ss_form_border_color};
	margin: 0px;
	padding: 6px;
	text-align: left;
	font-size: ${ss_style_font_smallprint};
	font-family: ${ss_style_title_font_family};
	color: ${ss_form_element_text_color}
}

.ss_navbarPopupPane ul, .ss_navbarPopupPane li {
	margin: 0px;
	padding: 0px;
	list-style: none;
	
}

.ss_navbarPopupPane h1 {
	font-size: ${ss_style_font_normalprint};
	margin-top: 0px;
	margin-bottom: 4px;
}

p.ss_legend {
	margin:6px 0px 3px 0px;
	color:#666666;
	font-size:${ss_style_font_smallprint};
}

/*
	Search & survey styles / end
*/

/* skip link */

.ss_skiplink {
	display:none !important;
}

/* skip link - end */

/* Widgets */

div.ss_selectedItemsContainer {
	border: 1px solid #333333;
	<c:if test="<%= isIE %>">
	  zoom:1; /* a workaround IE bug - font color not display correctly */
	</c:if>
}
div.ss_selectedItemsContainer ul {
	margin:6px; padding:0px;
}
div.ss_selectedItemsContainer li {
	margin:0px; padding:0px;
}
div.ss_selectedItemsContainer img {
	margin:0px 0px 0px 12px;
}
/* Widgets / end*/

/* Dojo Widgets /start */

.dojoComboBoxItem {
	text-align: left;
	padding-left: 12px;
}

/* Dojo Widgets / end */

/* Survey chart styles */
div.ss_chartContainer {
	float: left;
	text-align:left;
	margin-right: 5px;
}

div.ss_chartContainer div.ss_total, div.ss_chartContainer table.ss_total {
	border: 1px solid #afc8e3;
	background-color: #e8eff7;
	width: 300px;
	margin: 3px 0px 3px 0px;
	padding:0px;
	height: 15px;
}
div.ss_chartContainer table.ss_total {
	border-spacing: 0px;
	padding:0px;
	border-collapse: collapse;	
}

div.ss_chartContainer div.ss_total div, div.ss_chartContainer table.ss_total td.ss_bar {
	background-color: #afc8e3;
	color:#000099;
	<c:if test="<%= isIE %>">
	  zoom:1; /* a workaround IE bug - font color not display correctly */
	</c:if>	
}
div.ss_chartContainer table.ss_total td { 
	line-height:13px; 
	font-size: ${ss_style_font_finestprint};	
}
/* Charts styles - end*/

/* Statistic styles - start */
table.shortColoredBar { width: 100px;}
table.coloredBar { width: 300px;}

table.ss_statisticTable td {vertical-align:top;}

table.ss_statisticContainer {
	margin: 3px 0px 3px 6px;
	padding:0px;
	border-spacing: 0px;
	padding:0px;
	border-collapse: collapse;	
	border:0px;
}
table.ss_statisticContainer td {
	font-size: ${ss_style_font_finestprint};
	white-space: nowrap;
	padding: 0px;
	border-spacing: 0px;
	border-collapse: collapse;	
	border:0px;
}
div.statistic0, td.statistic0 {background-color:#EF5612;}
div.statistic1, td.statistic1 {background-color:#EC9112;}
div.statistic2, td.statistic2 {background-color:#617F9F;}
div.statistic3, td.statistic3 {background-color:#995DB2;}
div.statistic4, td.statistic4 {background-color:#70AE55;}
div.statistic5, td.statistic5 {background-color:#72AF58;}
div.statistic6, td.statistic6 {background-color:#617F9F;}
div.statistic7, td.statistic7 {background-color:#547AA5;}

div.ss_statusBar div.statistic0, table.ss_statusBar td.statistic0 {background-color:#EC9112;}
div.ss_statusBar div.statistic1, table.ss_statusBar td.statistic1 {background-color:#617F9F;}
div.ss_statusBar div.statistic2, table.ss_statusBar td.statistic2 {background-color:#70AE55;}
div.ss_statusBar div.statistic3, table.ss_statusBar td.statistic3 {background-color:#995DB2;}

div.ss_statisticBar span, td.ss_statisticBar span {
	margin-left: 2px;
	color: #ffffff;
}
h5.ss_statisticLabel {
	margin:6px 0px 0px 6px;
}
ul.ss_statisticLegend {
	margin:0px 0px 0px 6px;
	padding:0px;
}
ul.ss_statisticLegend li {
	line-height:12px;
	margin:0px;
	padding:0px;
}
ul.ss_statisticLegend li div.ss_statisticLegend {
	float:left; 
	width:12px;
	height:12px;
	margin:0px 3px 3px 0px;
}
/* Statistic styles - end */

/* Tasks */
div.ss_task_list_container {
	<c:if test="<%= isIE %>">
		height:1%;
	</c:if>
	margin:0px;
	padding:0px;
	text-align: center;
}

div.ss_content_outer table.ss_tasks_list {
	empty-cells: show;
	border-collapse: collapse;
}

table.ss_tasks_list {
	margin:6px 0px 4px 10px;
	padding: 0px;
	border-top: 1px solid #afc8e3;
	border-left: 1px solid #afc8e3;
	border-right: 1px solid #afc8e3;
	border-spacing: 0px;
	width: 99%;
	empty-cells: show;
	border-collapse: collapse;
}

table.ss_tasks_list th {
	background-color: #dbe6f2;
	border-bottom: 1px solid #afc8e3;
	border-right: 1px solid #afc8e3;
	font-size: 10px !important;
	text-align: left;
	padding: 3px 0px 3px 5px;
	white-space: nowrap;
	overflow: visible;
}
table.ss_tasks_list td {
	padding: 3px 0px 3px 6px;
	font-size: 10px !important;
	border-bottom: 1px solid #afc8e3;
	
	
}

table.ss_tasks_accessory td {
	font-size: 10px;
}


table.ss_tasks_list td.ss_iconsContainer {
	white-space: nowrap;
}
table.ss_tasks_list ul, table.ss_tasks_list li {
	margin:0px;
	padding:0px;
	border:0px;
}

table.ss_tasks_list .ss_due, table.ss_tasks_list .ss_assigned {
	font-size: 10px;
}

table.ss_tasks_list td.ss_overdue, table.ss_tasks_list td.ss_overdue a {
	color: #CC0000;
	font-weight: bold;
}

div.ss_completedContainer {
	border: 1px solid #afc8e3;
	background-color: #e8eff7;
	<c:choose>
		<c:when test="<%= isIE %>">
			width: 101px;
		</c:when>
		<c:otherwise>
			width: 99px;
		</c:otherwise>
	</c:choose>
		

	height:12px;
	margin: 3px;
	padding:0px;
}


div.ss_inline {
	float:left;
	margin:0px;
	padding:0px;
}

div.ss_bar_on, div.ss_bar_off, div.ss_inline {
	float:left;
}

div.ss_bar_on {
	background-color: #afc8e3; 
	width: 9px; 
	float:left; 
	padding:0px;
	margin: 0px 0px 0px 0px; 
	border:0px; 
	color:#000099; 
	font-size:10px;
	line-height:12px;
	height:12px;
	overflow: visible;
	white-space: nowrap;
}

div.ss_bar_off {
	background-color: #e8eff7; 
	width: 9px; 
	float: left; 
	padding: 0px; 
	margin: 0px; 
	border: 0px; 
	color: #000099; 
	font-size: 10px;
	line-height: 12px;
	height: 12px;
	overflow: visible;
	white-space: nowrap;
}

div.ss_bar_status {
	clear: both;
	padding: 0px; 
	margin: 0px;
	color: #000099; 
	font-size: 10px;
	line-height: 12px;
	margin-left: 3px;
}

a.ss_taskStatus img, img.ss_taskStatus {
	width: 21px;
	height: 22px;
	margin: 0px;
	padding: 0px;
	border: 0px;
	vertical-align: bottom;
}


a.ss_taskStatus_s2_u img, a.ss_taskStatus_s2_u:link img , a.ss_taskStatus_s2_u:focus img, a.ss_taskStatus_s2_u:visited img, img.ss_taskStatus_s2_u { 
	background: transparent url(<html:imagesPath/>icons/status_s2_u.gif) no-repeat top left; 
}

a.ss_taskStatus_s1_u img, a.ss_taskStatus_s1_u:link img , a.ss_taskStatus_s1_u:focus img, a.ss_taskStatus_s1_u:visited img, img.ss_taskStatus_s1_u { 
	background: transparent url(<html:imagesPath/>icons/status_s1_u.gif) no-repeat top left; 
}

a.ss_taskStatus_s4_u img, a.ss_taskStatus_s4_u:link img , a.ss_taskStatus_s4_u:focus img, a.ss_taskStatus_s4_u:visited img, img.ss_taskStatus_s4_u { 
	background: transparent url(<html:imagesPath/>icons/status_s4_u.gif) no-repeat top left; 
}

a.ss_taskStatus_s3_u img, a.ss_taskStatus_s3_u:link img , a.ss_taskStatus_s3_u:focus img, a.ss_taskStatus_s3_u:visited img, img.ss_taskStatus_s3_u { 
	background: transparent url(<html:imagesPath/>icons/status_s3_u.gif) no-repeat top left; 
}

a.ss_taskStatus_s2_u:hover img, a.ss_taskStatus_s1_u:hover img, a.ss_taskStatus_s4_u:hover img, a.ss_taskStatus_s3_u:hover img {
    background-position:  left -21px; 
}

a.ss_taskPriority img, img.ss_taskPriority {
	width: 21px;
	height: 21px;
	margin: 0px;
	padding: 0px;
	border: 0px;
	vertical-align: bottom;
}



a.ss_taskPriority_p5_u img, a.ss_taskPriority_p5_u:link img , a.ss_taskPriority_p5_u:focus img, a.ss_taskPriority_p5_u:visited img, img.ss_taskPriority_p5_u { 
	background: transparent url(<html:imagesPath/>icons/prio_p5_u.gif) no-repeat top left; 
}

a.ss_taskPriority_p4_u img, a.ss_taskPriority_p4_u:link img , a.ss_taskPriority_p4_u:focus img, a.ss_taskPriority_p4_u:visited img, img.ss_taskPriority_p4_u { 
	background: transparent url(<html:imagesPath/>icons/prio_p4_u.gif) no-repeat top left; 
}

a.ss_taskPriority_p3_u img, a.ss_taskPriority_p3_u:link img , a.ss_taskPriority_p3_u:focus img, a.ss_taskPriority_p3_u:visited img, img.ss_taskPriority_p3_u { 
	background: transparent url(<html:imagesPath/>icons/prio_p3_u.gif) no-repeat top left; 
}

a.ss_taskPriority_p2_u img, a.ss_taskPriority_p2_u:link img , a.ss_taskPriority_p2_u:focus img, a.ss_taskPriority_p2_u:visited img, img.ss_taskPriority_p2_u { 
	background: transparent url(<html:imagesPath/>icons/prio_p2_u.gif) no-repeat top left; 
}

a.ss_taskPriority_p1_u img, a.ss_taskPriority_p1_u:link img , a.ss_taskPriority_p1_u:focus img, a.ss_taskPriority_p1_u:visited img, img.ss_taskPriority_p1_u { 
	background: transparent url(<html:imagesPath/>icons/prio_p1_u.gif) no-repeat top left; 
}

a.ss_taskPriority_p5_u:hover img, a.ss_taskPriority_p4_u:hover img, a.ss_taskPriority_p3_u:hover img, a.ss_taskPriority_p2_u:hover img, a.ss_taskPriority_p1_u:hover img {
    background-position:  left -21px; 
}

.ss_task_completed {
	text-decoration: line-through;
}

/* Task - end */

div.ssPageNavi {
	margin: 0px;
	padding-left: 15px;
	padding-top: 2px;
	padding-bottom: 5px;
	background-color: ${ss_style_background_color_side_panel_featured};
}

div.ssPageNavi table td {
    white-space: nowrap;
}

.ssPageNavi, .ssPageNavi table, .ssPageNavi td, .ssPageNavi div {
	background-color: transparent;
}

.ssPageNavi .ssVisibleEntryNumbers {
	font-family: Arial;
	font-size: 10px;
	color: #666666;
}

.ssPageNavi .ss_go_to_page {
	font-size: 10px;
	color: #333333;
}

.ssPageNavi input.form-text {
	border: 1px solid #333333;
}

.ssPageNavi a.ss_linkButton:link, .ssPageNavi a.ss_linkButton:visited, .ssPageNavi a.ss_linkButton:hover {
	border: 1px solid ${ss_linkbutton_outline_color};
	background-color: ${ss_linkbutton_background_color};
	background-image: none;
}

.ssPageNavi .ssCurrentPage {
	font-size: 11px;
	color: #333333;
}

.ssPageNavi a.ssPageNumber {
	font-size: 11px;
	color: #3333FF;
	text-decoration: underline;
	margin: 0px 2px 0px 2px;
}

.ssPageNavi a.ssPageNumber:visited {
	color: #CCCCCC;
	text-decoration:underline;
}

.ssPageNavi .ss_actions_bar4 {
	background-color: #FFFFFF;
	font-weight: normal;
	font-size: 10px;
	font-family: ${ss_style_title_font_family};
}

.ssPageNavi .ss_results_pro_page, .ssPageNavi .ss_results_pro_page div, .ssPageNavi .ss_results_pro_page table {
	background-color: #FFFFFF;
}

div.ss_results_pro_page {
	position:relative;
	top: 0px; 
	margin:0px 2px; 
	padding: 1px 4px 1px 4px;
	border-top:solid #CCCCCC 1px; 
	border-bottom:solid #CCCCCC 1px;  
	border-right:solid #CCCCCC 1px;  
	border-left:solid #CCCCCC 1px;
}
#ss_operation_status {
	width: 300px;
	
	text-align: center;
}

#ss_operation_status span {
	width: 100%;
	text-align: center;
	color: ${ss_style_link_hover_color};
	font-weight: bold;
}


div.ss_themeMenu {
  position:absolute;
  border:1px solid #333333;
  margin: 0px;
  padding: 10px 30px;
  background-color:${ss_style_background_color_opaque};
  text-align: left;
}

div.ss_themeMenu a {
  font-family: ${ss_style_font_family};
  font-size: ${ss_style_font_size}; 
  font-weight: bold;
}

div.ss_themeMenu ul {
	list-style-type: square;
}


div.ss_license_warning {
  color: red;
  font-weight: bold;
  font-family: ${ss_style_font_family};
  font-size: ${ss_style_font_size}; 
}

.ss_radio_button_horizontal td {
	padding-right:10px;
}

table.ssMeetingRecords {
	text-align: left;
	empty-cells: show;
	width: 100%;
	margin: 10px 0px 10px 0px;
}

table.ssMeetingRecords th {
	padding: 5px 0px 5px 5px;
	border-top: 1px solid ${ss_style_border_color};
	border-bottom: 1px solid ${ss_style_border_color};
	background-color: #dbe6f2;
}

table.ssMeetingRecords td {
	padding: 2px 0px 2px 10px;
	border-bottom: 1px solid ${ss_style_border_color};
}

table.ssMeetingRecords .ssDocuments td {
	padding-top: 10px;
}

table.ssMeetingRecords .ssHeader {
	font-weight: bold;
}

.ss_background_iframe {
	position: absolute;
	display: none;
	border: none!important;
	margin: 0;
	padding: 0;
}

.ss_pseudoPortal {
	margin:25px;
	padding:0px;
	border:1px solid white;
	background: white;
}

<jsp:include page="/WEB-INF/jsp/common/ssf_css_dashboard.jsp" />
<jsp:include page="/WEB-INF/jsp/common/ssf_css_global_nav.jsp" />
<jsp:include page="/WEB-INF/jsp/common/ssf_css_discussion.jsp" />

<% // Place all CSS code above this line %>
</c:if> <%// test="${empty ss_skipCssStyles || ss_skipCssStyles != true} %>
