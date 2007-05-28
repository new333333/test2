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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<% // Define the user's choice of skins (right now there is only one) %>
<c:set var="ss_user_skin" value="r1" scope="request"/>


<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<c:if test="${!empty ss_loadCssStylesInline && ss_loadCssStylesInline == true}">
<%@ page contentType="text/css" %>
</c:if>

<%
// Color values used in ss styles, highlighting, borders, and headers
// Select a color theme: "blackandwhite" or "debug"
%>
<c:set var="ss_color_theme" value="defaultblue" scope="request"/>
<c:if test="${!empty ssCssTheme}">
  <c:set var="ss_color_theme" value="${ssCssTheme}" scope="request"/>
</c:if>

<%
//Color theme: "defaultblue"
%>
<c:if test="${ss_color_theme == 'defaultblue'}">

<% //FONT STYLES %>
	<c:set var="ss_style_font_family" value="Lucida Sans Unicode, Arial, sans-serif" scope="request"/>
	<c:set var="ss_style_folder_view_font_family" value="Arial, sans-serif" scope="request"/>
	<c:set var="ss_style_title_font_family" value="Arial, Helvetica, sans-serif" scope="request"/>

<% //FONT SIZES %>
	<c:set var="ss_style_font_size" value="12px" scope="request"/>
	<c:set var="ss_style_font_finestprint" value="9px" scope="request"/>
	<c:set var="ss_style_font_fineprint" value="10px" scope="request"/>
	<c:set var="ss_style_font_smallprint" value="11px" scope="request"/>
	<c:set var="ss_style_font_normalprint" value="12px" scope="request"/>
	<c:set var="ss_style_font_largeprint" value="13px" scope="request"/>
	<c:set var="ss_style_font_largerprint" value="14px" scope="request"/>
	<c:set var="ss_style_font_largestprint" value="15px" scope="request"/>
	<c:set var="ss_style_font_input_size" value="11px" scope="request"/>

	<c:set var="ss_style_brightest" value="1.0" scope="request"/>
	<c:set var="ss_style_brighter" value="0.8" scope="request"/>
	<c:set var="ss_style_bright" value="0.7" scope="request"/>
	<c:set var="ss_style_dim" value="0.6" scope="request"/>
	<c:set var="ss_style_very_dim" value="0.4" scope="request"/>
	
<% //ACCESS CONTROL TABLE %>
	<c:set var="ss_table_font_family" value="Lucida Sans Unicode, Arial, Helvetica, sans-serif" scope="request"/>
	<c:set var="ss_table_background_color_background" value="#FFFFFF" scope="request"/>
	<c:set var="ss_table_background_color_head" value="#CCCCCC" scope="request"/>
	<c:set var="ss_table_background_color_odd_row" value="#ECECEC" scope="request"/>
	<c:set var="ss_table_background_color_even_row" value="#FFFFFF" scope="request"/>
	<c:set var="ss_table_background_color_row_hover" value="#FFFFAA" scope="request"/>
	
<% //BACKGROUND COLORS %>
	<c:set var="ss_style_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_style_component_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_style_component_toolbar_background_color" value="#ECECEC" scope="request"/>
	
<% //BLOG %>
	<c:set var="ss_blog_summary_title_background_color" value="#E8EFF7" scope="request"/>
	<c:set var="ss_blog_content_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_blog_sidebar_background_color" value="#CECECE" scope="request"/>
	
<% //BORDER COLORS %>
	<c:set var="ss_style_border_color" value="#999999" scope="request"/>
	<c:set var="ss_style_border_color_light" value="#CCCCCC" scope="request"/>
	<c:set var="ss_style_text_color" value="#333333" scope="request"/>
    <c:set var="ss_style_gray_color" value="#333333" scope="request"/>
    <c:set var="ss_style_light_color" value="#999999" scope="request"/>
	
<% //BOX	 %>
	<c:set var="ss_box_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_box_canvas_color" value="#FFFFCC" scope="request"/>
	<c:set var="ss_box_title_color" value="#009999" scope="request"/>
	<c:set var="ss_box_title_text_color" value="#993333" scope="request"/>
	
<% //BUTTON %>
	<c:set var="ss_linkbutton_background_color" value="#CFDF8F" scope="request"/>
	<c:set var="ss_linkbutton_text_color" value="#333333" scope="request"/>
	<c:set var="ss_linkbutton_link_hover_color" value="#666666" scope="request"/>
	<c:set var="ss_linkbutton_border_color_in" value="#BFCA8A" scope="request"/>
	<c:set var="ss_linkbutton_border_color_out" value="#666666" scope="request"/>
	
<% //CALENDAR %>
	<c:set var="ss_calendar_today_background_color" value="#ffffe8" scope="request"/>
	<c:set var="ss_calendar_notInView_background_color" value="#f7f7f7" scope="request"/>
	
<% //DASHBOARD COLORS %>
    <c:set var="ss_dashcomp_header_bar_background" value="#CCCCCC" scope="request"/>
    <c:set var="ss_dashcomp_header_bar_title_color" value="#333333" scope="request"/>
    <c:set var="ss_dashcomp_header_bar_title_link_color" value="#666666" scope="request"/>
    <c:set var="ss_dashboard_table_border_color" value="blue" scope="request"/>
    
<% //ENTRIES	 %>
	<c:set var="ss_entry_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_entry_description_background_color" value="#E8EFF7" scope="request"/>
	<c:set var="ss_entry_description_border_color" value="#FFFFFF" scope="request"/>
	
<% //FORMS	 %>
	<c:set var="ss_form_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_form_component_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_form_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_form_element_header_color" value="#66CCCC" scope="request"/>
	<c:set var="ss_form_text_color" value="#333333" scope="request"/>
    <c:set var="ss_form_gray_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_form_element_color" value="#ECECEC" scope="request"/>
	<c:set var="ss_form_element_text_color" value="#333333" scope="request"/>
	<c:set var="ss_form_element_readonly_color" value="InfoBackground" scope="request"/>
	<c:set var="ss_style_text_field_background_color" value="#FFEECC" scope="request"/> 
	<c:set var="ss_style_text_field_border_color" value="#F0E0C0" scope="request"/>
	
<% //FOOTER COLORS %>
    <c:set var="ss_style_footer_text_color" value="blue" scope="request"/>
    <c:set var="ss_style_footer_font" value=" normal 11px Arial, Helvetica" scope="request"/>
    
<% //GALLERY %>
	<c:set var="ss_gallery_background_color" value="#F0F0F0" scope="request"/>
	<c:set var="ss_gallery_image_background_color" value="#666666" scope="request"/>
	<c:set var="ss_gallery_anchor_color" value="#333333" scope="request"/>
	<c:set var="ss_gallery_anchor_hover_color" value="blue" scope="request"/>
	<c:set var="ss_gallery_rule_color" value="#CCCCCC" scope="request"/>
	
<% //GUESTBOOK %>
	<c:set var="ss_guestbook_rule_color" value="#AFC8E3" scope="request"/>
	  
<% //LINK COLORS %>
	<c:set var="ss_style_link_color" value="#333333" scope="request"/>
	<c:set var="ss_style_link_visited_color" value="#663366" scope="request"/>
	<c:set var="ss_style_link_hover_color" value="#3333FF" scope="request"/> 
	
<% //TOOLBAR %>

    <c:set var="ss_toolbar_background_color" value="#DBDBDB" scope="request"/>
    <c:set var="ss_toolbar_text_color" value="#000000" scope="request"/>
    <c:set var="ss_toolbar_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
    <c:set var="ss_toolbar_border_color" value="#3366CC" scope="request"/>
    <c:set var="ss_toolbar_dropdown_menu_color" value="#666666" scope="request"/>
    <c:set var="ss_toolbar_inactive" value="#999999" scope="request"/>
    
	<c:set var="ss_toolbar1_background_color" value="#BFCA8A" scope="request"/>
	<c:set var="ss_toolbar1_text_color" value="#333333" scope="request"/>
	<c:set var="ss_toolbar1_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
	<c:set var="ss_toolbar1_border_color" value="#3366CC" scope="request"/>
	<c:set var="ss_toolbar1_dropdown_menu_color" value="#BFCA8A" scope="request"/>
	<c:set var="ss_toolbar1_inactive" value="#BFCA8A" scope="request"/>

	<c:set var="ss_toolbar2_background_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_toolbar2_text_color" value="#333333" scope="request"/>
	<c:set var="ss_toolbar2_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
	<c:set var="ss_toolbar2_border_color" value="#3366CC" scope="request"/>
	<c:set var="ss_toolbar2_dropdown_menu_color" value="#666666" scope="request"/>
	<c:set var="ss_toolbar2_inactive" value="#999999" scope="request"/>

	<c:set var="ss_toolbar4_background_color" value="#DBE6F2" scope="request"/>
	<c:set var="ss_toolbar4_text_color" value="#666666" scope="request"/>
	<c:set var="ss_toolbar4_link_hover_color" value="#E8EFF7" scope="request"/>
	<c:set var="ss_toolbar4_border_color" value="#DBE6F2" scope="request"/>
	<c:set var="ss_toolbar4_dropdown_menu_color" value="#DBE6F2" scope="request"/>
	<c:set var="ss_toolbar4_inactive" value="#333333" scope="request"/>
	
	<c:set var="ss_folder_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_folder_line_highlight_color" value="#CCCCCC" scope="request"/>	
	
<% //HELP COLORS %>
	<c:set var="ss_help_spot_background_color" value="#E8EFF7" scope="request"/>
	<c:set var="ss_help_panel_background_color" value="#ffffff" scope="request"/>
	<c:set var="ss_lightBox_background_color" value="#ECECEC" scope="request"/>	
				
<% //HEADER COLORS	 %>
    <c:set var="ss_style_header_bar_background" value="#DEE7C6" scope="request"/>
    <c:set var="ss_style_header_bar_title_color" value="#333333" scope="request"/>
    <c:set var="ss_style_header_bar_title_link_color" value="#333333" scope="request"/>	
    
<%  //LINK COLORS   %>
	<c:set var="ss_style_link_color" value="#333333" scope="request"/>
	<c:set var="ss_style_link_visited_color" value="#663366" scope="request"/>	<c:set var="ss_style_link_hover_color" value="#3333FF" scope="request"/> 
	
<%  //search    %>
	
	<c:set var="ss_style_link_color" value="#333333" scope="request"/>
	<c:set var="ss_style_link_visited_color" value="#663366" scope="request"/>
	<c:set var="ss_style_link_hover_color" value="#3333FF" scope="request"/> 
	
<%  //breadcrumbs   %>
	
<% //METADATA COLORS %>

    <c:set var="ss_style_metadata_color" value="#666666" scope="request"/>   
    
<% //MUTED %>
	<c:set var="ss_style_muted_foreground_color" value="#333333" scope="request"/>
	<c:set var="ss_style_muted_label_color" value="#2956B2" scope="request"/>
			
<% //PORTLET COLORS	 %>
	<c:set var="ss_portlet_style_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_portlet_style_text_color" value="#333333" scope="request"/>
	<c:set var="ss_portlet_style_inherit_font_specification" value="false" scope="request"/> 
	
<% //SLIDING TABLE %>
	<c:set var="ss_sliding_table_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_sliding_table_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_sliding_table_text_color" value="#3333FF" scope="request"/>
	<c:set var="ss_sliding_table_link_hover_color" value="#3333FF" scope="request"/>
	<c:set var="ss_sliding_table_row0_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_sliding_table_row1_background_color" value="#ECECEC" scope="request"/>

<% //TAG %>
	<c:set var="ss_tag_color" value="#999966" scope="request"/>
	<c:set var="ss_tag_pane_background_color" value="transparent" scope="request"/>

	<c:set var="ss_diff_color_added" value="FFFFAA" scope="request"/>
	<c:set var="ss_diff_color_deleted" value="red" scope="request"/>
	<c:set var="ss_diff_color_same" value="lightblue" scope="request"/>	

<% //TOOLBARs %>
	<c:set var="ss_toolbar2_background_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_toolbar2_text_color" value="#333333" scope="request"/>
	<c:set var="ss_toolbar2_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
	<c:set var="ss_toolbar2_border_color" value="#3366CC" scope="request"/>
	<c:set var="ss_toolbar2_dropdown_menu_color" value="#666666" scope="request"/>
	<c:set var="ss_toolbar2_inactive" value="#999999" scope="request"/>
	
	<c:set var="ss_toolbar1_background_color" value="#BFCA8A" scope="request"/>
	<c:set var="ss_toolbar1_text_color" value="#333333" scope="request"/>
	<c:set var="ss_toolbar1_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
	<c:set var="ss_toolbar1_border_color" value="#3366CC" scope="request"/>
	<c:set var="ss_toolbar1_dropdown_menu_color" value="#BFCA8A" scope="request"/>
	<c:set var="ss_toolbar1_inactive" value="#BFCA8A" scope="request"/>	
	
	<c:set var="ss_folder_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_folder_line_highlight_color" value="#CCCCCC" scope="request"/>	 
	 
<% //REPLYS %>
	<c:set var="ss_replies_background_color" value="#DBE6F2" scope="request"/>
	<c:set var="edit_text_color" value="#3333FF" scope="request"/>
	
<% //TITLE	 %>
	<c:set var="ss_title_line_color" value="#3333FF" scope="request"/>

<% //TREE	 %>
	<c:set var="ss_tree_highlight_line_color" value="#999966" scope="request"/>

<% //??	 %>
	<c:set var="ss_generic_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_generic_border_shadow_color" value="#666666" scope="request"/>

<% //?? %>
	<c:set var="ss_style_drop_highlight" value="#FFFFFF" scope="request"/>
	<c:set var="ss_style_drop_shadow" value="#666666" scope="request"/>

</c:if>

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
	margin: 0px 2px 0px 2px;
}

.ss_style {
  font-family: ${ss_style_font_family};
  font-weight: inherit;
  font-size: ${ss_style_font_size}; 
  background-color: ${ss_style_background_color};
  color: ${ss_style_text_color};
}

.ss_style td, .ss_style th {
  font-family: ${ss_style_font_family};
  font-size: ${ss_style_font_size}; 
  color: ${ss_style_text_color};
}

.ss_style_trans , .ss_style_trans td, .ss_style_trans th{
  font-family: ${ss_style_font_family};
  font-weight: inherit;
  font-size: ${ss_style_font_size}; 
  background-color: transparent;
  color: ${ss_style_text_color};
}
  
/* LINKS */
.ss_style a {
  color: ${ss_style_link_color};
  text-decoration: none;
}
.ss_style a:visited {
  color: ${ss_style_link_visited_color};
}
.ss_style a:hover {
  color: ${ss_style_link_hover_color};
  text-decoration:underline;
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
  background: #CCCCCC;
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


.ss_style_color, .ss_style_color table, .ss_style form {
  color: ${ss_style_text_color};
  background-color: ${ss_style_background_color};
  }
    
.ss_form_color {
  color: ${ss_form_text_color};
  background-color: ${ss_form_background_color};
  }
    
.ss_form.ss_gray {
  color: ${ss_form_gray_color};
  }

.ss_form select, .ss_form option {
  background-color: ${ss_form_element_color};
  color: ${ss_form_element_text_color};
  }
  
.ss_form textarea {
  background-color: ${ss_form_element_color};
  color: ${ss_form_element_text_color};
  }

.ss_form input.ss_text { 
  background-color: ${ss_form_element_color};
  color: ${ss_form_element_text_color};
  }
.ss_readonly {
  background-color: ${ss_form_element_readonly_color};
  }
    
.ss_text_field {
  border:1px solid ${ss_style_text_field_border_color} !important;
  background-color: ${ss_style_text_field_background_color};
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
  background:inherit;
  }
.ss_entryContent table {
  background:inherit;
  }
.ss_entrySignature {
  font-size: ${ss_style_font_smallprint};
  }
.ss_entryDescription {
  padding-left: 15px;
  padding-right: 15px;
  padding-top: 5px;
  padding-bottom: 5px;
  background-color: ${ss_entry_description_background_color};
  font-size: 13px;
  color: ${ss_style_muted_foreground_color};
}
.ss_entryDescription p {
  margin-top: 2px;
  margin-bottom: 10px;
}

.ss_editableTab {
  background-color: ${ss_entry_description_background_color};
  float: right;
  margin-left: 5px;
  padding-bottom: 3px;
  padding-right: 5px;
  padding-left: 5px;
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
	background: ${ss_entry_description_background_color};
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



input.ss_linkButton, input.ss_submit, a.ss_linkButton:link, a.ss_linkButton:visited {
  color: ${ss_linkbutton_text_color};
  font: normal 9px Lucida Sans Unicode;
  letter-spacing: 0.25px;
  border-top: 1px solid ${ss_linkbutton_border_color_in};
  border-left: 1px solid ${ss_linkbutton_border_color_in};
  border-right: 1px solid ${ss_linkbutton_border_color_out};
  border-bottom: 1px solid ${ss_linkbutton_border_color_out};
  background-color: ${ss_linkbutton_background_color};
  background-image: url(<html:imagesPath/>pics/background_linkbutton.jpg);
  background-repeat: repeat-x;
  padding-left: 15px;
  padding-right: 15px;
  padding-bottom: 1px;
  vertical-align: middle;
  text-decoration: none;
  cursor: pointer;
  }


a.ss_linkButton:focus, a.ss_linkButton:hover {
  color: ${ss_linkbutton_link_hover_color};
  font: normal 9px Lucida Sans Unicode;
  letter-spacing: 0.25px;
  border-top: 1px solid ${ss_linkbutton_border_color_in};
  border-left: 1px solid ${ss_linkbutton_border_color_in};
  border-right: 1px solid ${ss_linkbutton_border_color_out};
  border-bottom: 1px solid ${ss_linkbutton_border_color_out};
  background-color: ${ss_linkbutton_background_color};
  background-image: url(<html:imagesPath/>pics/background_linkbutton.jpg);
  background-repeat: repeat-x;
  padding-left: 15px;
  padding-right: 15px;
  padding-bottom: 1px;
  vertical-align: bottom;
  text-decoration: none;
  cursor: pointer;
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
  display: inline;
  font-family: ${ss_style_title_font_family};
  background-repeat: no-repeat;
  background-position: left center;
  padding-left: 16px;
  padding-right: 16px;
  margin-left: 2px;
  font-size: 11px;
  line-height: 20px;
  color: ${ss_style_muted_label_color};
}

div.ss_iconed_label a {
  font-size: 11px;
  color: ${ss_style_muted_label_color};
}

.ss_muted_label_small {
  font-size: 11px;
  color: ${ss_style_muted_label_color};
}

.ss_muted_cloud_tag {
  color: ${ss_style_muted_label_color};
}
.ss_muted_tag_cloud {
  width: 70%;
}

a:hover div.ss_iconed_label , a:visited:hover div.ss_iconed_label  {
  font-size: 11px;
  color: ${ss_style_muted_label_color};
  text-decoration: underline;
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
  background-image: url(<html:imagesPath/>icons/subscribe.gif);
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
  background-color:${ss_style_background_color};
  }
.ss_popupMenuClose {
  width:100%;
  padding:0px 8px 0px 0px;
  text-align:right;
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
	background: transparent;
}

div.ss_popup_topleft {
  position: relative;
  background-image: url(<html:imagesPath/>pics/popup_top_blue.gif);
  background-repeat: no-repeat;
  background-position: top left;
  height: 24px;
  width: 40px;
  padding: 0px;
}

div.ss_popup_topcenter {
  position: relative;
  background-image: url(<html:imagesPath/>pics/popup_top_blue.gif);
  background-repeat: no-repeat;
  background-position: -50px top;
  height: 24px;
  padding: 0px;
}


div.ss_popup_topright {
  position: relative;
  background-image: url(<html:imagesPath/>pics/popup_top_blue.gif);
  background-repeat: no-repeat;
  background-position: top right;
  height: 24px;
  width: 40px;
  padding: 0px;
}

div.ss_popup_bottomleft {
  position: relative;
  background-image: url(<html:imagesPath/>pics/popup_btm_blue.gif);
  background-repeat: no-repeat;
  background-position: top left;
  height: 16px;
  width: 40px;
  padding: 0px;
}

div.ss_popup_bottomcenter {
  position: relative;
  background-image: url(<html:imagesPath/>pics/popup_btm_blue.gif);
  background-repeat: no-repeat;
  background-position: -50px top;
  height: 16px;
  padding: 0px;
}

div.ss_popup_bottomright {
  position: relative;
  background-image: url(<html:imagesPath/>pics/popup_btm_blue.gif);
  background-repeat: no-repeat;
  background-position: top right;
  height: 16px;
  width: 40px;
  padding: 0px;
}

div.ss_popup_title {
   font-family: ${ss_style_title_font_family};
   font-weight: bold;
   color: ${ss_style_header_bar_title_color};
   position: relative;
   padding-top: 3px;
   text-align: center;
}

div.ss_popup_close {
  position: relative;
  background-image: url(<html:imagesPath/>pics/popup_close_box.gif);
  background-repeat: no-repeat;
  width: 10px;
  height: 10px;
  top: 7px;
  left: 18px;
}

div.ss_popup_body {
  position: relative;
  background-image: url(<html:imagesPath/>pics/popup_bg_blue.gif);
  background-repeat: repeat-y;
  background-position: top right;
  padding: 1px 5px 1px 5px;
}


.ss_edit_button {
	color:${edit_text_color};
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
  background-color:${ss_help_panel_background_color} !important;
  border:2px solid black;
  margin:2px;
  padding:2px;
}
.ss_helpPanel {
  position:absolute;
  visibility:hidden;
  display:none;
  background-color:${ss_help_panel_background_color} !important;
  border:2px solid black;
  margin:2px;
  padding:2px;
  width:400px;
}
.ss_helpToc {
  border: 1px solid ${ss_style_border_color_light};
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

.ss_inlineHelp {
  visibility:hidden; 
  display:none; 
  border:1px solid black; 
  background:#ffffff;
}

.ss_lightBox {
  position:absolute;
  background-color:${ss_lightBox_background_color};
}

/* Blogs */
.ss_blog {
 
}

<c:set var="ss_sidebar_side" value="left" scope="request"/>

div.ss_blog_content_container1 {
	width: 100%;
	background-repeat: repeat-y;
}

div.ss_blog_sidebar_container {
	width: 180px;
	background-repeat: repeat-y;
}
<c:if test="${ss_sidebar_side == 'left'}">

div.ss_blog_content_container1 {
	margin-left: -190px;
	float: right;
	background-image: url(<html:imagesPath/>pics/background_sidebar_lhs.jpg);
	background-position: left;
}

div.ss_blog_content_container2 {
	margin-left: 188px;
}

div.ss_blog_sidebar_container {
	float: left;
	padding-right: 3px;
	background-image: url(<html:imagesPath/>pics/background_sidebar_lhs.jpg);
	background-position: left;
}

div.ss_blog_content {
	padding-bottom: 30px;
	border-left: 1px solid ${ss_generic_border_color};
}

</c:if>

<c:if test="${ss_sidebar_side == 'right'}">

div.ss_blog_content_container1 {
	margin-right: -190px;
	float: left;
	background-image: url(<html:imagesPath/>pics/background_sidebar_rhs.jpg);
	background-position: right;
}

div.ss_blog_content_container2 {
	margin-right: 188px;
}

div.ss_blog_sidebar_container {
	float: right;
	padding-left: 5px;
	background-image: url(<html:imagesPath/>pics/background_sidebar_rhs.jpg);
	background-position: right;
}

div.ss_blog_content {
	padding-bottom: 30px;
	border-right: 1px solid ${ss_generic_border_color};
}


</c:if>

div.ss_blog_sidebar {
    margin-left: 5px;
    margin-right: 5px;
    margin-top: 10px;
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
  background-color:${ss_style_header_bar_background};
  overflow: hidden;
  padding-top: 6px;
  padding-bottom: 6px;
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

.ss_blog_sidebar table {
	background-color: transparent;
}

div.ss_blog_sidebar_hole {
	background-color: ${ss_entry_description_background_color};
	margin-left: 8px;
	margin-right: 9px;
}


a.ss_displaytag {
	color: ${ss_style_metadata_color};
}

div.ss_blog_sidebar_subhead {
	padding-top: 20px;
	padding-bottom: 5px;
	font-size: 12px;
	font-weight: bold;
}

/* Sliding tables */
div.ss_sliding_table_column0 {
  display: block; 
  border: ${ss_sliding_table_border_color} 1px solid;
  margin: 0px;
}
.ss_sliding_table_column0 * {
  color: ${ss_sliding_table_text_color};
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
  line-height:15px;
  font-family: ${ss_style_folder_view_font_family} !important;
}
.ss_sliding_table_row1 {
  background-color: ${ss_sliding_table_row1_background_color}; 
  line-height:15px;
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
  margin-top: 0px;
  margin-bottom: 8px;
  padding-top:2px;
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

.ss_tags {
  color:${ss_tag_color};
  font-weight:bold;
}
.ss_tag_pane {
  position:absolute; 
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
  border-top:1px solid #ffffff;	
  border-left:1px solid #ffffff;	
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
  background-color: ${ss_style_background_color}; 
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
.ss_tree_highlight {
  font-weight: bold;
  color: ${ss_tree_highlight_line_color};
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

.ss_box_title {
	background: ${ss_box_title_color} url(<html:imagesPath/>box/box_title_bg_gradient.gif) repeat-x;
	color: ${ss_box_title_text_color};
	height: 20px;
	margin:0px;
	padding: 0px 3px 0px 3px;
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
  margin:0px;
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
  width:16px;
  height:16px;
  border:none;
  margin:0px;
  padding:0px;
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
    background: ${ss_form_element_color}; 
}


/* -------------------Skins-------------------- */
.ss_content_outer {
    position: relative;
	padding-bottom:100px;
	margin-top:1px;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/back1.gif) repeat;
}
.ss_content_inner {
	width:100%;
	padding:0px;
	margin:0px 0px 0px 0px;
    background-color: ${ss_style_background_color};
}
.ss_content_window {
	padding:2px 5px;
}
.ss_content_window_compact {
	padding:0px;
}
.ss_content_window_content {
	padding:4px 10px;
}
.ss_clear {
	clear:both;
	height:1px;
	font-size:0px;
}
/* round corners: */
.ss_decor-round-corners-top1{
	margin:0px 12px 0px 12px;
	background:transparent url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/border1.gif) repeat-x;
}
.ss_decor-round-corners-top1 div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/corner1s.gif) no-repeat left;
}
.ss_decor-round-corners-top1 div div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/corner2.gif) no-repeat right;
	height:19px;
}
.ss_decor-round-corners-bottom1{
	margin:0px 12px 0px 12px;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/border2.gif) repeat-x;
}
.ss_decor-round-corners-bottom1 div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/corner3.gif) no-repeat left;
}
.ss_decor-round-corners-bottom1 div div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/corner4.gif) no-repeat right;
	height:19px;
}
.ss_decor-border3{
	background:transparent url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/border3.gif) repeat-y left;
}
.ss_decor-border4{
	margin:0px 0px 0px 10px;
	background:transparent url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/border4.gif) repeat-y right;
}
.ss_decor-border5{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/border.gif) repeat-y left;
}
.ss_decor-border6{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/border.gif) repeat-y right;
}

.ss_decor-round-corners-bottom3 {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/border2.gif) repeat-x bottom;
}
.ss_decor-round-corners-bottom3 div {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/corner3.jpg) no-repeat left;
}
.ss_decor-round-corners-bottom3 div div {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/corner4.jpg) no-repeat right;
	height:7px;
	font-size:1px;
}
.ss_decor-border7 {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/border3.jpg) repeat-y left;
}
.ss_decor-border8 {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/border4.gif) repeat-y right;
}

.ss_decor-round-corners-top2{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/border.gif) repeat-x top;
}
.ss_decor-round-corners-top2 div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/corner1.gif) no-repeat left ;
}
.ss_decor-round-corners-top2 div div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/corner2.gif) no-repeat right;
	height:10px;
	font-size:1px;
}
.ss_decor-round-corners-top2 div div.ss_utils{
	background:none;
}
.ss_decor-round-corners-bottom2{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/border.gif) repeat-x bottom;
	margin-bottom:0px;
}
.ss_decor-round-corners-bottom2 div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/corner3.gif) no-repeat left;
}
.ss_decor-round-corners-bottom2 div div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/corner4.gif) no-repeat right;
	height:10px;
	font-size:1px;
}
.ss_rounden-content {
	margin:0px 19px 0px 6px;
	padding:0px 0px 0px 0px;
    background-color: ${ss_style_background_color};
}

/* global toolbar: */
div.ss_global_toolbar_in_portlet {
	background: #FFFFFF;
	height: auto;
}
div.ss_global_toolbar_maximized {
    position: relative;
	background:${ss_style_background_color} url(<html:imagesPath/>pics/background_global_toolbar.jpg) repeat-x;
	height:45px;
	padding-top:2px;
	<c:if test="<%= isIE %>">
		margin-bottom:2px;
	</c:if>
	<c:if test="<%= !isIE %>">
		margin-bottom:4px;
	</c:if>
}

div.ss_global_toolbar_maximized div.ss_global_toolbar_myworkspace, div.ss_global_toolbar_maximized div.ss_global_toolbar_favs,
 div.ss_global_toolbar_maximized div.ss_global_toolbar_divider, div.ss_global_toolbar_maximized div.ss_global_toolbar_quick,
 div.ss_global_toolbar_maximized .ss_global_toolbar_findUser, div.ss_global_toolbar_maximized .ss_global_toolbar_help,
 div.ss_global_toolbar_maximized .ss_global_toolbar_accessible {
	float: left;
	white-space: nowrap;
	position: relative;
}

div.ss_global_toolbar_help {
	text-align: center;
    <c:if test="<%= !isIE %>">
    padding-top: 15px;
    </c:if>
}

div#ss_navbarHelpButton {
    margin-left: 5px;
}

div.ss_global_toolbar_maximized .ss_global_toolbar_divider {
	width: 4px;
	height: 43px;
	background: transparent url(<html:imagesPath/>pics/divider_global_toolbar.jpg) no-repeat;	
	margin: 0 5px 0 5px;	
}

div.ss_global_toolbar_maximized .ss_global_toolbar_accessible {
}

div.ss_global_toolbar_maximized .ss_global_toolbar_findUser {
	width: 72px;
	padding-top: 7px;
}

div.ss_global_toolbar_maximized .ss_global_toolbar_quick {
	padding-top: 7px;
}

div.ss_global_toolbar_maximized .ss_global_toolbar_quick .ss_global_toolbar_label_text {
	text-align: left;	
	vertical-align: bottom;
}

div.ss_global_toolbar_maximized .ss_global_toolbar_quick .ss_global_toolbar_quick_advanced {
	text-align: right;
	font-size: 10px;
	padding-left: 10px;
	vertical-align: top;
}

div.ss_global_toolbar table {
	background: transparent;
}
div.ss_global_toolbar table tr.ss_row_txt td {
	padding-top: 10px;
}
div.ss_global_toolbar table tr.ss_row_last td {
	height: 29px;
}
div.ss_global_toolbar table td {
	white-space: nowrap;
	padding-left: 10px;
	padding-right: 10px;
}

.ss_global_toolbar_portlet_box {
	background: #DBE6F2 !important;
}

.ss_global_toolbar a span, .ss_global_toolbar div span {
	background:transparent;
}



.ss_global_toolbar_myworkspace div, .ss_global_toolbar_favs div {
	padding: 25px 5px 0px 5px;
	vertical-align: middle;
}

.ss_global_toolbar_favs div {
	background:url(<html:imagesPath/>icons/toolbar_favorites.gif) no-repeat top;
	width: 40px;
	position: relative;
}
.ss_global_toolbar_myworkspace div {
	background:url(<html:imagesPath/>icons/toolbar_myworkspace.gif) no-repeat top;
	width: 62px;
	position: relative;
}

.ss_global_toolbar_favs_big div {
	background:url(<html:imagesPath/>icons/toolbar_favorites_big.jpg) no-repeat top;
	width: 75px;
	padding-top: 40px;
	text-align: center;
}
.ss_global_toolbar_myworkspace_big div {
	background:url(<html:imagesPath/>icons/toolbar_myworkspace_big.jpg) no-repeat top;
	width: 75px;
	padding-top: 40px;
	text-align: center;
}

div.ss_global_toolbar_show_portal {
	background:url(<html:imagesPath/>icons/toolbar_show_portal.jpg) no-repeat top;
}
div.ss_global_toolbar_hide_portal {
	background:url(<html:imagesPath/>icons/toolbar_hide_portal.jpg) no-repeat top;
}
div.ss_global_toolbar_help {
	background:url(<html:imagesPath/>icons/help.png) no-repeat top center;
	width: 30px;
	padding-right: 4px;
	margin-top: 5px;
	text-align: center;
}
.ss_global_toolbar_accessible {
	margin-top: 0px;
	padding-left: 6px;
	background: transparent;
}
.ss_global_toolbar_accessible form {
	background:transparent;
}
.ss_global_toolbar_accessible a {
    background:transparent;
}
.ss_global_toolbar_accessible img {
	background: transparent;
}
.ss_global_toolbar_findUser {
	margin-top: 0px;
	background: transparent;
}
.ss_global_toolbar_findUser form {
	background:transparent;
}
.ss_global_toolbar_findUser .form-text {
	width:40px;
}
span.ss_global_toolbar_label_text, span.ss_global_toolbar_label_text_quickSearch {
	color:#333333;
	background: transparent;
	font-weight: bold;	
	white-space: nowrap;
}

span.ss_global_toolbar_label_text_quickSearch {
	font-size: 9px;
}
span.ss_global_toolbar_label_text {
	font-size: 10px;
}
div.ss_global_toolbar_findUser_text span {
}
.ss_global_toolbar_findUser a {
}
.ss_global_toolbar_search {
	margin-top: 0px;
	background: transparent;
	vertical-align: bottom;
}
.ss_global_toolbar_search form {
	background:transparent;
}

.ss_global_toolbar_quick input {
	width: 115px;
	background: #FFFFFF;
	border: 1px solid #7F9DB9;
}
div.ss_global_toolbar_search_text {
    width:100px;
	color:#484848;
	text-align:center;
	padding:0px !important;
}
div.ss_global_toolbar_search_text span {
	background:inherit !important;
}




/* tabs: */
div.ss_tab_canvas {
    position:relative;
<c:if test="<%= isIE %>">
    top:-3px;
</c:if>
<c:if test="<%= !isIE %>">
    top:-2px;
</c:if>
    margin:0px;
    padding:0px;
    width:100%;
    border-right: 1px ${ss_generic_border_color} solid;
    border-left: 1px ${ss_generic_border_color} solid;
    border-bottom: 1px ${ss_generic_border_color} solid;
}
div.ss_tabs {
    position:relative;
    left:14px; 
    z-index:2;
    width:100%;
    margin-right:40px;
}
.ss_tabs_td, td.ss_tabs_td {
	font-family: ${ss_style_title_font_family};
	font-weight: bold;
	font-size: 11px;
	color:#5A3C68;
	height:24px;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back1.jpg) repeat-x left top;
	white-space: nowrap;
	margin:0px;
	padding:3px 0px 0px 0px;
}
.ss_tabs_td_active, td.ss_tabs_td_active {
	font-family: ${ss_style_title_font_family};
	font-weight: bold;
	font-size:11px;
	color:#666666;
	height:25px;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back1_active.jpg) repeat-x left top;
	margin:0px;
	padding:3px 0px 0px 0px;
}
.ss_tabs_td_left_active {
    background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back2_active.jpg) no-repeat left;
    width:4px;
    min-width:4px;
}
.ss_tabs_td_right_active {
    background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back3_active.jpg) no-repeat right;
    width:4px;
    min-width:4px;
}
.ss_tabs_td_left {
    background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back2.jpg) no-repeat left;
    width:4px;
    min-width:4px;
}
.ss_tabs_td_right {
    background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back3.jpg) no-repeat right;
    width:4px;
    min-width:4px;
}
.ss_tabs_corner_l {
  min-width:4px;
  width:4px;
}
.ss_tabs_corner_r {
  width:4px;
  min-width:4px;
}

.ss_tabs_td_active a:hover, .ss_tabs_td a:hover {
	text-decoration:underline;
}

/* breadcrumbs */
.ss_breadcrumb {
	margin:0px 5px;
	padding:0px 5px 0px 9px;
	font-size: ${ss_style_font_smallprint};
	float:left;
}
a.ss_breadcrumb {
	font-size: ${ss_style_font_smallprint};
}



/* titlebar */
.ss_title_bar {
	background-image: url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/background_dc_bar.jpg);
	background-repeat: repeat-x;
	color: ${ss_dashcomp_header_bar_title_color};
	height:16px;
	margin:0px;
	padding-bottom: 3px;
}
.ss_title_bar * {
	background:transparent;
}
.ss_title_bar_history_bar {
	background:inherit;
	color:${ss_style_text_color};
}
.ss_title_bar_inner1 {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/corner1.gif) no-repeat left;
	height:24px;
	width:8px;
}
.ss_title_bar_inner2 {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/corner2.gif) no-repeat right;
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
	padding:2px 0px 2px 10px;
	cursor:auto;
}
.ss_title_bar_icons li {
	float:left;
	margin-right:6px;
}
.ss_title_bar_icons li a {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/iconset/iconback.gif) no-repeat top;
	display:block;
}
.ss_title_bar_icons li a:hover {
	background-position:bottom;
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
	background-color: #DCE2BE;
	font-weight: normal;
	font-size: 11px;
	font-family: ${ss_style_title_font_family};
}


table.ss_actions_bar2_pane, table.ss_actions_bar2_pane td {
	background-color: ${ss_toolbar2_background_color};
	width: 100%;
	height: 26px;
	padding: 0;
	margin: 0;
	border-collapse: collapse;
	border-spacing: 0;
}

.ss_actions_bar2, table.ss_actions_bar2_pane {
	background-color: ${ss_toolbar2_background_color};
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

table.ss_actions_bar4_pane, table.ss_actions_bar4_pane td {
	background-color: ${ss_toolbar4_background_color};
	width: 100%;
	height: 26px;
	padding: 0;
	margin: 0;
	border-collapse: collapse;
	border-spacing: 0;
}

.ss_actions_bar4, table.ss_actions_bar4_pane {
	background-color: ${ss_toolbar4_background_color};
}

.ss_actions_bar4 {
	font-weight: normal;
	font-size: 11px;
	font-family: ${ss_style_title_font_family};
}



ul.ss_actions_bar1  {
	background-image: url(<html:imagesPath/>pics/background_actions_bar1.jpg);
	background-repeat: repeat-x;
}

.ss_actions_bar {
	<c:if test="<%= isIE %>">
		width: 100%;
	</c:if>
	<c:if test="<%= !isIE %>">
		width: 99.3%;
	</c:if> 
	list-style-type:none;	
	margin: 0;
	padding: 0 0 0 5px;
	height: 26px;
}

.ss_actions_bar li {
	float:left;
	display: inline;
	border-top:    1px solid ${ss_style_drop_highlight};
	border-left:   1px solid ${ss_style_drop_highlight};
	border-bottom: 1px solid ${ss_style_drop_shadow};
	border-right:  1px solid ${ss_style_drop_shadow};	
	background-color: inherit;
	margin: 3px 3px 0 0;
	padding: 2px 6px 3px 6px;
}

.ss_actions_bar li.ss_toolBarItem {
	border: 0;
	margin: 0;
	padding: 0;
	background-color: inherit;
}



.ss_actions_bar li.ss_toolBarItem ul {
	float: left;
	display: inline;
	margin: 0;
	padding: 0;
}

.ss_actions_bar li.ss_toolBarItem .ss_toolBarItemTxt {
	float: left;
	margin-top: 7px;
}

.ss_actions_bar li.ss_actions_bar_separator {
	border-left: 1px dotted #FFFFFF;
	border-right: 0;
	border-top: 0;
	border-bottom: 0;
	height: 26px;
	margin: 0 5px 0 5px;
	width: 0;
	padding: 0;
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
	padding:0px 15px;
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

.ss_actions_bar1 li a:hover {
	background-color: ${ss_style_header_bar_background};
	text-decoration:none;
}

.ss_actions_bar2 li a:hover {
	background-color: #DFDFDF;
	text-decoration:none;
}

.ss_actions_bar3 li a:hover {
	background-color: #AFC8E3;
	text-decoration:none;
}

div.ss_actions_bar_submenu {
	background-color: inherit;
	margin:0px;
	padding:0px;
	text-align:left;
	position:absolute;
	display:none;
	z-index:500;
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
  background-color: ${ss_toolbar2_background_color};
  background-image: none;
  opacity: 0.95;
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=95);
 </c:if>
}

div.ss_actions_bar_submenu ul.ss_actions_bar3 {
  background-color: #AFC8E3;
  background-image: none;
  opacity: 0.95;
 <c:if test="<%= isIE %>">
  filter: alpha(opacity=95);
 </c:if>
}

.ss_actions_bar_submenu {
	position:absolute;
 <c:if test="<%= isIE %>">
	top:-12px;
	left:-26px;
 </c:if>
 <c:if test="<%= !isIE %>">
	top:-14px;
	left:-28px;
 </c:if>	
	border:1px solid #907FA3;
	border-top:none;
	padding: 4px 1px;
}

.ss_actions_bar_submenu li  {
	float:none;
	border-bottom:1px solid ${ss_generic_border_color};
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
	background:none;
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
	background-color:#DFDFDF;
}

.ss_actions_bar3 li:hover, .ss_actions_bar1 a:hover {
	background-color:#AFC8E3;
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
	border-top: 1px solid ${ss_generic_border_color};
	border-bottom: 1px solid ${ss_generic_border_color};
	width: 100%;
	margin: 0px;
	padding: 0px 0px 0px 0px;
}
div.ss_utils_bar {
    float:right;
    text-align:right;
	margin-bottom:0px;
	color:#999999;
	font-weight:bold;
	background:transparent none repeat scroll 0%;
	font-size:${ss_style_font_size};
}
div.ss_line {
	border-bottom: 1px solid #999999;
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
	font-weight:bold;
	margin: 0px 10px 0px 0px;
}
div.ss_utils_bar ul.ss_utils_bar li a, div.ss_utils_bar ul.ss_utils_bar li a:visited {
	color:#999999;
	display:block;
	margin:0px;
	border:0px;
	text-decoration:underline;
}
div.ss_utils_bar ul.ss_utils_bar li a span {
	padding: 5px 0px 6px 0px;
	margin: 0px;
	font-size:${ss_style_font_smallprint};
	color:#999999;
}

.ss_utils_bar li a:hover {
	text-decoration:underline;
}

div.ss_utils_bar_submenu {
	background:#ffffff;
	margin:0px;
	padding:0px;
	text-align:left;
	position:absolute;
	display:none;
	z-index:500;
	border: 1px solid #999999;
}

ul.ss_utils_bar_submenu {
	margin: 0px 5px 0px 5px;
	background:#ffffff;
} 

.ss_utils_bar_submenu li  {
	float:none;
	padding:0px;
	font-weight:bold;
	border-right-style:none;
    line-height:20px;
}
.ss_utils_bar_submenu li:hover .ss_utils_bar_submenu a:hover {
	text-decoration:underline;
	background-color:transparent;
	color:#999999;
	font-size:${ss_style_font_smallprint};	
}
.ss_utils_bar_submenu a, .ss_utils_bar_submenu a:visited {
	color:#999999;
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
	background:url(<html:imagesPath/>skins/${ss_user_skin}/back4.gif) repeat-x !important;
	text-decoration:none;
}
.ss_search_results_selection_inactive {
}
div.ss_findUserList {
	position:absolute;
	margin:2px;
	padding:0px 15px;
	border-top:    solid 1px ${ss_generic_border_color}; 
	border-left:   solid 1px ${ss_generic_border_color}; 
	border-right:  solid 1px ${ss_generic_border_shadow_color}; 
	border-bottom: solid 1px ${ss_generic_border_shadow_color}; 
	background:${ss_style_background_color};
	z-index:500;
}
.ss_findUserList div ul {
	list-style-type:none;
	margin:-6px 0px 0px -6px;
	padding:6px;
	text-align:left;
}
.ss_findUserList li {
	padding:1px;
	float:none;
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

.ss_utils{
	position:absolute;
	top:0px;
	right:10px;
}
.ss_util{
    float:right;
	margin-right:10px;
	line-height:24px;
	text-align:center;
	color:#9C9C9C !important;
	font-weight:bold;
	font-size:${ss_style_font_fineprint};
}
.ss_util_cartouche {
    position:relative; 
    background-color:${ss_style_background_color}; 
    z-index:3;
}
.ss_util.ss_long{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners4/3.gif) repeat-x;
	display:block;
}
.ss_utilwrap1{
	float:right;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners4/1.gif) no-repeat left top;
	padding-left:8px;
	height:24px;
}
.ss_utilwrap1 a.ss_util{
	float:none;
	margin:0px;
}
.ss_utilwrap2{
	display:block;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners4/2.gif) no-repeat right;
	padding-right:8px;
	margin-right:10px;
}
.ss_util.ss_short{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/util_short.gif) no-repeat;
	width:25px;
}
.ss_innerContentBegins{
	margin-top:9px;
}

/* Profile elements */
.ss_table_spacer_right {
	padding-right:10px;
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
    border-color: blue;
}


div.ss_thumbnail_small_no_text a:hover img {
    border-color: blue;
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
    margin-top:    0px;
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
	background-color:${ss_style_background_color};
}

/* CSS document for table - author: rsmart 1.23.07 v02*/
.ss_table{
	font-family: ${ss_table_font_family};
	border-collapse: collapse;
}
/*table head - dark grey with black border top*/	
.ss_table thead tr{
	background-color: ${ss_table_background_color_head};
	border-top: 1px solid black;
}
/*table head - dark grey with black border bottom*/
.ss_table thead th{
	padding: 0.5em;
	white-space: nowrap;
	border-bottom: 1px solid black;
}
/*row is white*/
.ss_table tr{
background-color: ${ss_table_background_color_even_row};
}
.ss_table_tr_even{
background-color: ${ss_table_background_color_even_row};
}
/*row is lite grey*/
.ss_table_tr_odd{
background-color: ${ss_table_background_color_odd_row} !important;
white-space: nowrap !important;
}

/*yellow hover for table rows*/	
.ss_table tbody tr:hover{
	background-color: ${ss_table_background_color_row_hover} !important;
}
/*cell and row border*/
.ss_table td, .ss_table th {
	border: 1px dotted ${ss_generic_border_color};
}
.ss_table tbody td {
	padding: 0.5em;
}
/*work around to eliminate borders on nested tables*/

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

/*fonts for master file tables*/

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
.ss_table_smheaders {
	font-size: ${ss_style_font_fineprint};
	font-weight: bold;
	color: ${ss_style_text_color};
	line-height: normal;
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
.ss_calendar_import_div {
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
  min-height: 300px;
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
div.ss_cal_monthGridDayBadge {
  background-color: #ECECEC;
  position: absolute;
  height: 11px;
  width: 14.0%;
  border-top: 1px solid #DDDDDD;
  text-align: right;
  padding-right: 3px;
  font-size: 9px;
  font-family: sans-serif;
  color: #666666;
}

/* Highlight for today */
div.ss_cal_monthGridDayBadgeToday {
  background-color: #E8EFF7;
  color: #FFFFFF;
}

div.ss_cal_monthGridDayBadge a {
  color: #666666;
  text-decoration: none;
}

div.ss_cal_monthGridDayBadge a:hover {
  color: #BBBBBB;
  text-decoration: underline;
}



a.ss_calDaySelectButton img {
	width: 25px;
	height: 24px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}

a.ss_calDaySelectButton img, a.ss_calDaySelectButton:link img , a.ss_calDaySelectButton:focus img, a.ss_calDaySelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/day.gif) no-repeat top left; 
}

a.ss_calDaySelectButton:hover img {
	background-position: left -24px; 
}


a.ss_cal3DaysSelectButton img {
	width: 25px;
	height: 24px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}

a.ss_cal3DaysSelectButton img, a.ss_cal3DaysSelectButton:link img , a.ss_cal3DaysSelectButton:focus img, a.ss_cal3DaysSelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/3_day.gif) no-repeat top left; 
}

a.ss_cal3DaysSelectButton:hover img {
	background-position: left -24px; 
}

a.ss_cal5DaysSelectButton img {
	width: 25px;
	height: 24px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}

a.ss_cal5DaysSelectButton img, a.ss_cal5DaysSelectButton:link img , a.ss_cal5DaysSelectButton:focus img, a.ss_cal5DaysSelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/5_day.gif) no-repeat top left; 
}

a.ss_cal5DaysSelectButton:hover img {
	background-position: left -24px; 
}

a.ss_cal7DaysSelectButton img {
	width: 25px;
	height: 24px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}

a.ss_cal7DaysSelectButton img, a.ss_cal7DaysSelectButton:link img , a.ss_cal7DaysSelectButton:focus img, a.ss_cal7DaysSelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/5_day.gif) no-repeat top left; 
}

a.ss_cal7DaysSelectButton:hover img {
	background-position: left -24px; 
}

a.ss_cal14DaysSelectButton img {
	width: 25px;
	height: 24px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}

a.ss_cal14DaysSelectButton img, a.ss_cal14DaysSelectButton:link img , a.ss_cal14DaysSelectButton:focus img, a.ss_cal14DaysSelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/5_day.gif) no-repeat top left; 
}

a.ss_cal14DaysSelectButton:hover img {
	background-position: left -24px; 
}

a.ss_calMonthSelectButton img {
	width: 25px;
	height: 24px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}

a.ss_calMonthSelectButton img, a.ss_calMonthSelectButton:link img , a.ss_calMonthSelectButton:focus img, a.ss_calMonthSelectButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/5_day.gif) no-repeat top left; 
}

a.ss_calMonthSelectButton:hover img {
	background-position: left -24px; 
}

a.ss_calDateDownButton img {
	width: 12px;
	height: 28px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}

a.ss_calDateDownButton img, a.ss_calDateDownButton:link img , a.ss_calDateDownButton:focus img, a.ss_calDateDownButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/date_down.gif) no-repeat top left; 
}

a.ss_calDateDownButton:hover img {
	background-position: left -28px; 
}

a.ss_calDateUpButton img {
	width: 12px;
	height: 28px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}

a.ss_calDateUpButton img, a.ss_calDateUpButton:link img , a.ss_calDateUpButton:focus img, a.ss_calDateUpButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/date_up.gif) no-repeat top left; 
}

a.ss_calDateUpButton:hover img {
	background-position: left -28px; 
}


a.ss_calendarButton img {
	width: 14px;
	height: 16px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}

a.ss_calendarButton img, a.ss_calendarButton:link img , a.ss_calendarButton:focus img, a.ss_calendarButton:visited img { 
	background: transparent url(<html:imagesPath/>icons/calendar.gif) no-repeat top left; 
}

a.ss_calendarButton:hover img {
	background-position: left -16px; 
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
<c:set var="sboxMargin" value="0px" />
<c:if test="<%= isIE %>">
    <c:set var="sboxMargin" value="-1px" />
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
  background: ${ss_style_header_bar_background};
}

div.ss_profileBox2 {
  background: ${ss_replies_background_color};
}

div.ss_profile_box_title {
  font-size: 15px;
  color: ${ss_style_metadata_color};
  height: 20px;
}

div.ss_profile_matte {
  background: #FFFFFF;
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
	background-image: url(<html:imagesPath/>pics/watermark_person.gif);
	background-position: center;
	background-repeat: no-repeat;
}

div.ss_profile_info_box {
    background-color: transparent !important;
	background-image: url(<html:imagesPath/>pics/watermark_info.gif);
	background-position: center;
	background-repeat: no-repeat;
}

div.ss_profile_contact_box {
    background-color: transparent !important;
	background-image: url(<html:imagesPath/>pics/watermark_talkbubbles.gif);
	background-position: center;
	background-repeat: no-repeat;
}

table.ss_minicard_interior {
	background-color: white;
	padding-right: 5px;
}


/*
 * Calendar event boxes
 */

div.ss_cal_eventBody {
  font-size: 11px;
  color: #666666;
  font-family: sans-serif;
  margin: 0px;
  padding-left: 4px;
  border-left: 1px solid;
  border-right: 1px solid;
}


div.ss_cal_eventBox {
  overflow: hidden;
  position: absolute;
}


div.ss_cal_monthEventBody {
  font-size: 11px;
  color: #666666;
  font-family: sans-serif;
  margin: 0px;
  padding-left: 4px;
}

div.ss_cal_monthEventBody a {
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

ul#ss_calendarNaviBar {	
	font-family: Arial;
	border: 1px solid #CCCCCC;
	background-color: #AFC8E3;
	list-style-type: none;
	height: 30px;
	padding: 0;
	margin: 0 0 5px 0;
}


ul#ss_calendarNaviBar li.ss_calendarNaviBarOption, ul#ss_calendarNaviBar .ss_calendarNaviBarSeparator {
	position:relative;
	vertical-align: middle;
	float: left;
	height: 30px;
	line-height: 30px;
	display: inline;
}

ul#ss_calendarNaviBar li.ss_calendarNaviBarOption {	
	padding: 0 4px 0 4px;
	margin: 0;	
}

ul#ss_calendarNaviBar .ss_calendarNaviBarSeparator {
	border-left: 1px dotted #FFFFFF;
	margin: 0 5px 0 5px;
	padding: 0;
	width: 0;
}

ul#ss_calendarNaviBar .ss_hoursSelectorTitle {
	background-color: #AFC8E3;
	color: #333333;
	text-decoration: none;
	border: 1px solid #FFFFFF;
	font-size: 11px;
}

ul#ss_calendarNaviBar .ss_calHoursSelectorMenu {
	height: 22px;
	<c:if test="<%= isIE %>">
		margin-top: 6px;
	</c:if>
	<c:if test="<%= !isIE %>">
		margin-top: 4px;
	</c:if>	
}

ul#ss_calendarNaviBar li.ss_calendarNaviBarOptionMiddleImg {
	padding-top: 3px;
}

ul#ss_calendarNaviBar li.ss_calendarNaviBarOptionBigImg {
	padding-top: 1px;
	font-size: 0;
}

ul#ss_calendarNaviBar li.ss_calendarNaviBarOption > a > img.ss_fullHeight {
	margin: 1px 0 1px 0;
}

ul#ss_calendarNaviBar li.ss_calendarNaviBarOption div.ss_toolbar_color {
	background-color: #AFC8E3;
}

ul#ss_calendarNaviBar li.ss_calendarNaviBarOption a#anchor_ss_goto img {
	margin: 7px 0 7px 0;
}

ul#ss_calendarNaviBar span#ss_calViewDatesDescriptions {	
	color: #FFFFFF;
}

ul#ss_calendarNaviBar li.ss_calViewDatesDescriptionLi {
	width: 208px;
	text-align: center;
	padding: 0;
}

ul#ss_calendarNaviBar li.ss_calSelectDate {
	padding: 0 10px 0 7px;
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


/* TEAM BUDDIES LIST */

div.ss_buddies {
	padding-bottom: 35px; 
}
div.ss_buddiesListHeader {
	padding: 5px;
	font-family: ${ss_style_title_font_family};
	font-size: ${ss_style_font_largerprint};
	font-weight: bold;
}
div.ss_buddiesListHeader img {
	vertical-align: text-bottom;
}
table.ss_buddiesList {
	width: 100%; 
	empty-cells: show;
	padding: 0px; 
	border-top: 1px solid ${ss_style_text_color};
}
table.ss_buddiesList td {
	border-bottom: 1px solid ${ss_guestbook_rule_color};
	padding: 6px 0 7px 0px;
	height: 50px;
}
table.ss_buddiesList td.selectable {
	width: 9px;
	padding-left: 15px;
	padding-right: 15px;
	border: 0;
}
table.ss_buddiesList td.picture {
	border-bottom: 1px solid ${ss_guestbook_rule_color};
	padding: 6px 15px 7px 8px; 
	width: 35px;
}
table.ss_buddiesList tr.options td {
	border: 0;
}
table.ss_buddiesList tr.options td.selectall {
	height: 50px;
	text-align: center;
}
div.ss_buddiesListFooter {
	text-align: right;
}


form.inline {
	display: inline;
	border: 0;
	padding: 0;
	margin: 0;	
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
#ss_loading {
	text-align: center;
}


/*
	Search styles / start
*/
#ss_tab_content {
	padding:6px 6px 12px 6px;
}
#ss_tabs_container {
	border-left: 1px solid #cccccc;
	border-right:1px solid #cccccc;
	border-bottom:1px solid #cccccc;
	position:relative;
	<c:if test="<%= isIE %>">
	    top:-3px;
	</c:if>
	<c:if test="<%= !isIE %>">
	    top:-2px;
	</c:if>
    margin: 0px;
    padding: 0px;
    width: 100%;
}

#ss_rankings { 
	width: 215px;
	vertical-align: top;
}
#ss_content_container {
    padding:0;
    margin:0;
    vertical-align:top;
}
div.ss_searchContainer {
	background: transparent url(<html:imagesPath/>pics/top_left.gif) no-repeat top left;
	margin-left: 15px;
}
#ss_content { 
	border-left:1px solid #afc8e3; 
	border-right:1px solid #afc8e3;
    border-bottom:1px solid #afc8e3; 
	margin:0px; padding:0px;
}
#ss_searchForm_container {margin:0px; padding:0px;	}
#ss_searchForm_spacer {
	background: #e8eff7 url(<html:imagesPath/>pics/top_border.gif) repeat-x top left;
	margin:0px 0px 0px 5px;
	padding:0px;
	height:5px;
	line-height:1px; 
	font-size:0px;
	border-right:1px solid #afc8e3;
}
#ss_searchForm {
	background: #e8eff7 url(<html:imagesPath/>pics/left_border.gif) repeat-y top left;
	padding:6px;
	border-bottom:1px solid #afc8e3;
}
#ss_searchForm table {
	background-color: #e8eff7;
}
#ss_searchForm input { width: 150px;}
#ss_searchForm th {text-align:left;padding: 0px 0px 0px 12px;}
#ss_searchForm td {text-align:left;padding: 0px 12px 0px 12px;}
#ss_searchForm h4 {margin:0px 0px 6px 0px;padding:0px;}

a.ss_searchButton img {
	width: 22px;
	height: 17px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}


a.ss_searchButton img, a.ss_searchButton:link img , a.ss_searchButton:focus img, a.ss_searchButton:visited img { 
	background: transparent url(<html:imagesPath/>pics/search_icons.gif) no-repeat top left; 
}

a.ss_searchButton:hover img {
	background-position: left -17px; 
}

a.ss_advanced:link, a.ss_advanced:hover, a.ss_advanced:visited, a.ss_advanced:active {
	color: #0000FF;
	text-decoration:underline;
	margin:0px 0px 0px 6px;
}
a.ss_parentPointer:link, a.ss_parentPointer:hover, a.ss_parentPointer:visited, a.ss_parentPointer:active {
	color: #0000ff;
	text-decoration:underline;
	margin:0px 0px 0px 0px;
}

#ss_searchResult {
	margin:12px 0px 0px 0px;
	padding:0px;
<c:if test="<%= isIE %>">	
	zoom: 1; /* a workaround IE bug - parent border disappears */
</c:if>
}
#ss_searchResult li {
	border-bottom: 1px solid #cccccc;
	display:block;
	margin:12px 12px 12px 12px;	
}

#ss_searchResult_header {
	border-bottom: 1px solid #afc8e3;	
	margin-bottom:24px;
	padding:6px 24px 6px 12px;
<c:if test="<%= isIE %>">	
	zoom: 1; /* a workaround IE bug - parent border disappears */
</c:if>
}
#ss_searchResult_numbers {float:left;}
#ss_paginator {float:right;}

div.ss_thumbnail {float: left; width:62px; text-align:center;}
div.ss_thumbnail img {width:50px;height:50px;padding:0px; margin:0px;}
div.ss_entry {
	float: left;
	width: 400px;
}
div.ss_entry_folderListView {
	float: left;
	width: 600px;	
}
h3.ss_entryTitle {
   /* float:left;margin:0px; */
}
h3.ss_entryTitle a:link, h3.ss_entryTitle a:hover, h3.ss_entryTitle a:visited, h3.ss_entryTitle a:active, h3.ss_entryTitle a {
	color: #0000FF;
	text-decoration:underline;
}
div.ss_more {text-align:right;}
div.ss_entryDetails {  padding:6px;}
div.ss_entryDetails p {margin:0px 0px 6px 0px;}
img.ss_attachment_thumbnail {width:80px;height:74px;padding:0px; margin:0px;}
.ss_label {}

.ss_rating_box {
	margin:0px 0px 24px 0px;
	padding:0px;
	width: 215px;
}
.ss_rating_box_title {
	background: transparent url(<html:imagesPath/>pics/sidemenuhead.gif) no-repeat top left;
	margin:0px;
	padding:0px 5px 0px 5px;
	height: 16px;
}
.ss_rating_box_content {
	margin:0px;
	padding: 6px;
	border-left: 1px solid #cccccc;
	border-right: 1px solid #cccccc;
	border-bottom: 1px solid #cccccc;
	background-color: #dbe6f2;
}
img.ss_toogler {float:right;}
div.ss_rating_box_title h4 {float:left;margin:0px;}
div.ss_rating_box_content table {width:200px; border-collapse: collapse; border-spacing: 0;}
div.ss_rating_box_content th {border-bottom: 1px solid #afc8e3;text-align:left;}
div.ss_rating_box_content td {text-align:left;}
div.ss_rating_box_content td.ss_star {padding-left:10px;}
div.ss_rating_box_content h5 {margin:0px; border-bottom: 1px solid #afc8e3; }
div.ss_rating_box_content p {margin:6px 0px 12px 0px; }
span.ss_pageNumber{margin:0px 24px 0px 24px;}
#ss_searchForm_additionalFilters {background-color:#ffffff;}

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
#ss_searchForm_main th, #ss_searchForm_main td {
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


/*
	Search styles / end
*/

/* skip link */

.ss_skiplink {
	display:none;
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

/* Charts styles */
div.ss_chartContainer {
	border: 1px solid #afc8e3;
	background-color: #e8eff7;
	width: 300px;
	float:left;
	margin: 3px 0px 3px 0px;
	padding:0px;
}
div.ss_chartContainer div {
	background-color: #afc8e3;
	color:#000099;

}
/* Charts styles - end*/

/* Tasks */
table.ss_tasks_list {
	width:100%;
	margin-top:6px;
	padding: 0px;
	border-collapse: collapse;
}
table.ss_tasks_list th {
	background-color: #dbe6f2;
	border:1px solid #afc8e3;
	text-align: left;
	padding: 3px 3px 3px 3px;
}
table.ss_tasks_list td {
	padding: 6px  3px 6px 3px;
	border-bottom: 1px solid #afc8e3;
}
table.ss_tasks_list ul, table.ss_tasks_list li {
	margin:0px;
	padding:0px;
	border:0px;
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
	float:left;
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
	width: 9%; 
	float: left; 
	padding: 0; 
	margin: 0; 
	border: 0; 
	color: #000099; 
	font-size: 10px;
	line-height: 12px;
	height: 12px;
	overflow: visible;
	white-space: nowrap;
}

div.ss_bar_status {
	clear: both;
	padding: 0; 
	margin: 0;
	color: #000099; 
	font-size: 10px;
	line-height: 12px;
	margin-left: 3px;
}

a.ss_taskStatus img {
	width: 21px;
	height: 22px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}


a.ss_taskStatus_inProcess_u img, a.ss_taskStatus_inProcess_u:link img , a.ss_taskStatus_inProcess_u:focus img, a.ss_taskStatus_inProcess_u:visited img { 
	background: transparent url(<html:imagesPath/>icons/status_inProcess_u.gif) no-repeat top left; 
}

a.ss_taskStatus_needsAction_u img, a.ss_taskStatus_needsAction_u:link img , a.ss_taskStatus_needsAction_u:focus img, a.ss_taskStatus_needsAction_u:visited img { 
	background: transparent url(<html:imagesPath/>icons/status_needsAction_u.gif) no-repeat top left; 
}

a.ss_taskStatus_cancelled_u img, a.ss_taskStatus_cancelled_u:link img , a.ss_taskStatus_cancelled_u:focus img, a.ss_taskStatus_cancelled_u:visited img { 
	background: transparent url(<html:imagesPath/>icons/status_cancelled_u.gif) no-repeat top left; 
}

a.ss_taskStatus_completed_u img, a.ss_taskStatus_completed_u:link img , a.ss_taskStatus_completed_u:focus img, a.ss_taskStatus_completed_u:visited img { 
	background: transparent url(<html:imagesPath/>icons/status_completed_u.gif) no-repeat top left; 
}

a.ss_taskStatus_inProcess_u:hover img, a.ss_taskStatus_needsAction_u:hover img, a.ss_taskStatus_cancelled_u:hover img, a.ss_taskStatus_completed_u:hover img {
	background-position: left -22px; 
}

a.ss_taskPriority img {
	width: 21px;
	height: 22px;
	margin: 0;
	padding: 0;
	border: 0;
	vertical-align: bottom;
}



a.ss_taskPriority_trivial_u img, a.ss_taskPriority_trivial_u:link img , a.ss_taskPriority_trivial_u:focus img, a.ss_taskPriority_trivial_u:visited img { 
	background: transparent url(<html:imagesPath/>icons/prio_trivial_u.gif) no-repeat top left; 
}

a.ss_taskPriority_low_u img, a.ss_taskPriority_low_u:link img , a.ss_taskPriority_low_u:focus img, a.ss_taskPriority_low_u:visited img { 
	background: transparent url(<html:imagesPath/>icons/prio_low_u.gif) no-repeat top left; 
}

a.ss_taskPriority_medium_u img, a.ss_taskPriority_medium_u:link img , a.ss_taskPriority_medium_u:focus img, a.ss_taskPriority_medium_u:visited img { 
	background: transparent url(<html:imagesPath/>icons/prio_medium_u.gif) no-repeat top left; 
}

a.ss_taskPriority_high_u img, a.ss_taskPriority_high_u:link img , a.ss_taskPriority_high_u:focus img, a.ss_taskPriority_high_u:visited img { 
	background: transparent url(<html:imagesPath/>icons/prio_high_u.gif) no-repeat top left; 
}

a.ss_taskPriority_critical_u img, a.ss_taskPriority_critical_u:link img , a.ss_taskPriority_critical_u:focus img, a.ss_taskPriority_critical_u:visited img { 
	background: transparent url(<html:imagesPath/>icons/prio_critical_u.gif) no-repeat top left; 
}

a.ss_taskPriority_trivial_u:hover img, a.ss_taskPriority_low_u:hover img, a.ss_taskPriority_medium_u:hover img, a.ss_taskPriority_high_u:hover img, a.ss_taskPriority_critical_u:hover img {
	background-position: left -22px; 
}

/* Task - end*/

/* Administration->Reporting styles */
.ss_tab_container {
}

.ss_tabbed {
      float:left;
      width:100%;
      font-size:93%;
      line-height:normal;
      border-bottom:1px solid #6B78A9;
}
.ss_tabbed ul {
      margin:0;
      padding:10px 10px 0 50px;
      list-style:none;
}
.ss_tabbed li {
      display:inline;
      margin:0;
      padding:0;
}

.ss_tabbed a {
      float:left;
      background:url(<html:imagesPath/>pics/tableft5.gif) no-repeat left top;
      margin:0;
      padding:0 0 0 7px;
      text-decoration:none;
}

.ss_tabbed a span {
      float:left;
      display:block;
      background:url(<html:imagesPath/>pics/tabright5.gif) no-repeat right top;
      padding:5px 15px 4px 6px;
      color:#6B78A9;
}

    /* Commented Backslash Hack hides rule from IE5-Mac \*/

.ss_tabbed a span {float:none;}

    /* End IE5-Mac hack */

.ss_tabbed a:hover span {
      color:#6B78A9;
}

.ss_tabbed a:hover {
      background-position:0% -42px;
      text-decoration:none;
}

.ss_tabbed a:hover span {
      background-position:100% -42px;
}

 a.ss_current_tab {
      background-position:0% -42px;
}

a.ss_current_tab span {
      background-position:100% -42px;
}

.ss_tab_pane {
}


<% // Place all CSS code above this line %>
</c:if> <%// test="${empty ss_skipCssStyles || ss_skipCssStyles != true} %>
