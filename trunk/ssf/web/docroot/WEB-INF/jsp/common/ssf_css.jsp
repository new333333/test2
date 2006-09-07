<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<% // Define the user's choice of skins (right now there is only one) %>
<c:set var="ss_user_skin" value="r1" scope="request"/>

<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<c:if test="${!empty ss_skipCssStyles && ss_skipCssStyles == true}">
<%@ page contentType="text/css" %>
</c:if>

<%
// Color values used in ss styles, highlighting, borders, and headers
// Select a color theme: "blackandwhite" or "debug"
%>
<c:set var="ss_color_theme" value="blackandwhite" scope="request"/>
<c:if test="${!empty ssCssTheme}">
  <c:set var="ss_color_theme" value="${ssCssTheme}" scope="request"/>
</c:if>
<%
//Color theme: "debug"
%>
<c:set var="ss_portlet_style_background_color" value="#FFFFAA" scope="request"/>
<c:set var="ss_portlet_style_text_color" value="#000099" scope="request"/>
<c:set var="ss_portlet_style_inherit_font_specification" value="false" scope="request"/>

<c:set var="ss_style_background_color" value="#FFFFCC" scope="request"/>
<c:set var="ss_style_component_background_color" value="#FFFFAA" scope="request"/>
<c:set var="ss_style_component_toolbar_background_color" value="#FFFFDD" scope="request"/>
<c:set var="ss_style_border_color" value="#999999" scope="request"/>
<c:set var="ss_style_border_color_light" value="#cecece" scope="request"/>
<c:set var="ss_style_text_color" value="#009900" scope="request"/>
<c:set var="ss_style_footer_text_color" value="blue" scope="request"/>
<c:set var="ss_style_link_color" value="#009900" scope="request"/>
<c:set var="ss_style_link_hover_color" value="#3333FF" scope="request"/>
<c:set var="ss_style_gray_color" value="#999999" scope="request"/>
<c:set var="ss_style_light_color" value="#999999" scope="request"/>

<c:set var="ss_folder_border_color" value="#CC6666" scope="request"/>
<c:set var="ss_folder_line_highlight_color" value="#CECECE" scope="request"/>
<c:set var="ss_entry_border_color" value="#CC0000" scope="request"/>
<c:set var="ss_replies_background_color" value="#FFEECC" scope="request"/>
<c:set var="ss_replies_text_color" value="#009900" scope="request"/>
<c:set var="edit_text_color" value="#3333FF" scope="request"/>

<c:set var="ss_form_background_color" value="#CCFFFF" scope="request"/>
<c:set var="ss_form_component_background_color" value="#66FFFF" scope="request"/>
<c:set var="ss_form_border_color" value="#CC99CC" scope="request"/>
<c:set var="ss_form_gray_color" value="#CC99CC" scope="request"/>
<c:set var="ss_form_element_header_color" value="#66CCCC" scope="request"/>
<c:set var="ss_form_text_color" value="#3333FF" scope="request"/>
<c:set var="ss_form_element_color" value="#FFCCFF" scope="request"/>
<c:set var="ss_form_element_border_color" value="#669966" scope="request"/>
<c:set var="ss_form_element_text_color" value="#0033FF" scope="request"/>
<c:set var="ss_form_element_readonly_color" value="InfoBackground" scope="request"/>

<c:set var="ss_toolbar_background_color" value="#CECECE" scope="request"/>
<c:set var="ss_toolbar_text_color" value="#000000" scope="request"/>
<c:set var="ss_toolbar_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
<c:set var="ss_toolbar_border_color" value="#3366CC" scope="request"/>
<c:set var="ss_toolbar_dropdown_menu_color" value="#666666" scope="request"/>

<c:set var="ss_help_spot_background_color" value="#ffff00" scope="request"/>
<c:set var="ss_help_panel_background_color" value="#ffffff" scope="request"/>

<c:set var="ss_lightBox_background_color" value="#e5e5e5" scope="request"/>

<c:set var="ss_dashboard_table_border_color" value="blue" scope="request"/>

<c:set var="ss_blog_title_background_color" value="#AAAAFF" scope="request"/>
<c:set var="ss_blog_content_background_color" value="#FFFFAA" scope="request"/>
<c:set var="ss_blog_sidebar_background_color" value="#E5E5E5" scope="request"/>

<c:set var="ss_linkbutton_background_color" value="#FFFFE8" scope="request"/>
<c:set var="ss_linkbutton_text_color" value="#009900" scope="request"/>
<c:set var="ss_linkbutton_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
<c:set var="ss_linkbutton_border_color_in" value="#d5d5d5" scope="request"/>
<c:set var="ss_linkbutton_border_color_out" value="#666666" scope="request"/>

<c:set var="ss_title_line_color" value="#3333FF" scope="request"/>

<c:set var="ss_tree_highlight_line_color" value="#6666FF" scope="request"/>

<c:set var="ss_box_color" value="#CCCCCC" scope="request"/>
<c:set var="ss_box_canvas_color" value="#FFFFCC" scope="request"/>
<c:set var="ss_box_title_color" value="#009999" scope="request"/>
<c:set var="ss_box_title_text_color" value="#993333" scope="request"/>

<c:set var="ss_sliding_table_background_color" value="#FFFFAA" scope="request"/>
<c:set var="ss_sliding_table_border_color" value="#999999" scope="request"/>
<c:set var="ss_sliding_table_text_color" value="#3333FF" scope="request"/>
<c:set var="ss_sliding_table_link_hover_color" value="#3333FF" scope="request"/>
<c:set var="ss_sliding_table_row0_background_color" value="#FFFFCC" scope="request"/>
<c:set var="ss_sliding_table_row1_background_color" value="#FFFFAA" scope="request"/>

<c:set var="ss_calendar_today_background_color" value="#ffffe8" scope="request"/>
<c:set var="ss_calendar_notInView_background_color" value="#f7f7f7" scope="request"/>

<%
//Color theme: "black and white"
%>
<c:if test="${ss_color_theme == 'blackandwhite'}">
	<c:set var="ss_portlet_style_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_portlet_style_text_color" value="#000000" scope="request"/>
	<c:set var="ss_portlet_style_inherit_font_specification" value="false" scope="request"/>
	
	<c:set var="ss_style_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_style_component_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_style_component_toolbar_background_color" value="#ECECEC" scope="request"/>
	<c:set var="ss_style_border_color" value="#999999" scope="request"/>
	<c:set var="ss_style_border_color_light" value="#cecece" scope="request"/>
	<c:set var="ss_style_text_color" value="#000000" scope="request"/>
    <c:set var="ss_style_footer_text_color" value="blue" scope="request"/>
	<c:set var="ss_style_link_color" value="#000000" scope="request"/>
	<c:set var="ss_style_link_hover_color" value="#3333FF" scope="request"/>
	<c:set var="ss_style_gray_color" value="#999999" scope="request"/>
	<c:set var="ss_style_light_color" value="#999999" scope="request"/>
	
	<c:set var="ss_folder_border_color" value="#CECECE" scope="request"/>
	<c:set var="ss_folder_line_highlight_color" value="#CECECE" scope="request"/>
	<c:set var="ss_entry_border_color" value="#CECECE" scope="request"/>
	<c:set var="ss_replies_background_color" value="#FFEECC" scope="request"/>
	<c:set var="ss_replies_text_color" value="#009900" scope="request"/>
	<c:set var="edit_text_color" value="#3333FF" scope="request"/>
	
	<c:set var="ss_form_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_form_component_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_form_border_color" value="#CECECE" scope="request"/>
	<c:set var="ss_form_gray_color" value="#CECECE" scope="request"/>
	<c:set var="ss_form_element_header_color" value="#66CCCC" scope="request"/>
	<c:set var="ss_form_text_color" value="#000000" scope="request"/>
	<c:set var="ss_form_element_color" value="#EEEEEE" scope="request"/>
	<c:set var="ss_form_element_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_form_element_text_color" value="#000000" scope="request"/>
	<c:set var="ss_form_element_readonly_color" value="InfoBackground" scope="request"/>
	
	<c:set var="ss_toolbar_background_color" value="#CECECE" scope="request"/>
	<c:set var="ss_toolbar_text_color" value="#000000" scope="request"/>
	<c:set var="ss_toolbar_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
	<c:set var="ss_toolbar_border_color" value="#3366CC" scope="request"/>
	<c:set var="ss_toolbar_dropdown_menu_color" value="#666666" scope="request"/>

	<c:set var="ss_help_spot_background_color" value="#ffff00" scope="request"/>
	<c:set var="ss_help_panel_background_color" value="#ffffff" scope="request"/>

	<c:set var="ss_lightBox_background_color" value="#e5e5e5" scope="request"/>

	<c:set var="ss_dashboard_table_border_color" value="blue" scope="request"/>

	<c:set var="ss_blog_title_background_color" value="#AACCFF" scope="request"/>
	<c:set var="ss_blog_content_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_blog_sidebar_background_color" value="#E5E5E5" scope="request"/>

	<c:set var="ss_linkbutton_background_color" value="#FFFFE8" scope="request"/>
	<c:set var="ss_linkbutton_text_color" value="#666666" scope="request"/>
	<c:set var="ss_linkbutton_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
	<c:set var="ss_linkbutton_border_color_in" value="#dddddd" scope="request"/>
	<c:set var="ss_linkbutton_border_color_out" value="#666666" scope="request"/>
	
	<c:set var="ss_title_line_color" value="#3333FF" scope="request"/>
	
	<c:set var="ss_tree_highlight_line_color" value="#6666FF" scope="request"/>
	
	<c:set var="ss_box_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_box_canvas_color" value="#FFFFCC" scope="request"/>
	<c:set var="ss_box_title_color" value="#009999" scope="request"/>
	<c:set var="ss_box_title_text_color" value="#993333" scope="request"/>
	
	<c:set var="ss_sliding_table_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_sliding_table_border_color" value="#CECECE" scope="request"/>
	<c:set var="ss_sliding_table_text_color" value="#3333FF" scope="request"/>
	<c:set var="ss_sliding_table_link_hover_color" value="#3333FF" scope="request"/>
	<c:set var="ss_sliding_table_row0_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_sliding_table_row1_background_color" value="#EEEEEE" scope="request"/>
	
	<c:set var="ss_calendar_today_background_color" value="#ffffe8" scope="request"/>
	<c:set var="ss_calendar_notInView_background_color" value="#f7f7f7" scope="request"/>
</c:if>

<c:if test="${empty ss_skipCssStyles || ss_skipCssStyles != true}">

.ss_portlet_style, .ss_portlet_style * {
<c:if test="${!empty ss_portlet_style_background_color}">
  background-color: ${ss_portlet_style_background_color};
</c:if>
<c:if test="${!empty ss_portlet_style_text_color}">
  color: ${ss_portlet_style_text_color};
</c:if>
<c:if test="${ss_portlet_style_inherit_font_specification}">
  font-family: arial, helvetica, sans-serif;
  font-weight: inherit;
  font-size: 12px; 
</c:if>
}

.ss_style, .ss_style table {
  font-family: arial, helvetica, sans-serif;
  background-color: ${ss_style_background_color};
  color: ${ss_style_text_color};
  font-weight: inherit;
  font-size: 12px; 
  }
.ss_style a, .ss_style a:visited {
  color: ${ss_style_link_color};
}
.ss_style a:hover {
  color: ${ss_style_link_hover_color};
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
  background: #ccc;
  position: relative;
  top: 2px;
  left: 2px;
}
.ss_shadowbox2 {
  position: relative;
  top: -2px;
  left: -2px;
}

.ss_form, .ss_form table, .ss_style form {
  color: ${ss_form_text_color};
  background-color: ${ss_form_background_color};
  }
    
.ss_replies, .ss_replies table * {
  color: ${ss_replies_text_color};
  background-color: ${ss_replies_background_color};
  }
div.ss_replies {
  margin:10px;
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
    
.ss_form input.ss_submit {}

.ss_form input.ss_submit:hover {}
  
/* Text styled as buttons */
a.ss_linkButton:link, a.ss_linkButton:visited {
  color: ${ss_linkbutton_text_color};
  border-top: 1px solid ${ss_linkbutton_border_color_in};
  border-left: 1px solid ${ss_linkbutton_border_color_in};
  border-right: 1px solid ${ss_linkbutton_border_color_out};
  border-bottom: 1px solid ${ss_linkbutton_border_color_out};
  background-color: ${ss_linkbutton_background_color};
  padding-left: 3px;
  padding-right: 3px;
  margin-left: 0px;
  margin-right: 6px;
  margin-bottom: 2px;
  margin-top: 2px;
  line-height: 200%;
  text-decoration: none;
  display: inline;
  }

a.ss_linkButton:focus, a.ss_linkButton:hover {
  color: ${ss_linkbutton_link_hover_color};
  border-top: 1px solid ${ss_linkbutton_border_color_out};
  border-left: 1px solid ${ss_linkbutton_border_color_out};
  border-right: 1px solid ${ss_linkbutton_border_color_in};
  border-bottom: 1px solid ${ss_linkbutton_border_color_in};
  background-color: ${ss_linkbutton_background_color};
  padding-left: 3px;
  padding-right: 3px;
  margin-left: 0px;
  margin-right: 6px;
  margin-bottom: 2px;
  margin-top: 2px;
  line-height: 200%;
  text-decoration: none;
  display: inline;
  }

.ss_popupMenu {
  position:absolute;
  border:1px solid black;
  margin:2px;
  padding:2px;
  background-color:${ss_style_background_color};
  }

.ss_popupTitleOptions {
  position:absolute;
  border:1px solid black;
  margin:4px;
  padding:2px;
  background-color:${ss_style_background_color};
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
  width:300px;
}
.ss_helpToc {
  border: 1px solid #cecece;
  background-color:${ss_help_spot_background_color} !important;
  visibility:hidden;
  display:none;
}
.ss_helpToc li {
  list-style-type: square;
}

.ss_lightBox {
  position:absolute;
  background-color:${ss_lightBox_background_color};
}

/* Blogs */
.ss_blog {
  border: ${ss_sliding_table_border_color} 1px solid;
}

.ss_blog_content, .ss_blog_content table {
  background-color:${ss_blog_content_background_color};
}

.ss_blog_title, .ss_blog_title table {
  background-color:${ss_blog_title_background_color};
}

.ss_blog_sidebar, .ss_blog_sidebar table {
  background-color:${ss_blog_sidebar_background_color};
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
}
.ss_sliding_table_row1 {
  background-color: ${ss_sliding_table_row1_background_color}; 
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


/* Folder */
.ss_folder_border, .ss_folder_border table , .ss_folder_border form {
  background-color: ${ss_folder_border_color} !important;
  }

/* Entry */
.ss_entry_border, .ss_entry_border table {
  background-color: ${ss_entry_border_color} !important;
  }

/* Forum toolbar */
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


.ss_footer_toolbar {
  width: 100%; 
  background-color: ${ss_style_background_color};
  color: ${ss_style_footer_text_color};
  font-size:10px;
  font-weight:normal;
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
}
.ss_footer_toolbar a:hover {
  color: ${ss_style_link_hover_color};
}

ul.ss_dropdownmenu {
  list-style: outside;
  margin:2px 2px 2px 13px;
  padding: 2px;
}

ul.ss_dropdownmenu li {
  list-style-type: square;
  margin-left: 0px;
  margin-bottom: 2px;
  padding-left: 2px;
} 

.ss_calendar_today {
  background-color: ${ss_calendar_today_background_color};
}

.ss_calendar_notInView {
  background-color: ${ss_calendar_today_background_color};
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
  height:4px;
  top:-4px;
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
.ss_highlightEntry {
  background-color: ${ss_folder_line_highlight_color};
  }

.ss_tree_highlight {
  font-weight: bold;
  color: ${ss_tree_highlight_line_color};
  }
  
.ss_titlebold {
  font-size: 16px;
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

/* htmlarea overrides */
.htmlarea { 
    background: ${ss_form_element_color}; 
}

/* Tab div styling */

div.ss_activeTabcontainer {
  margin-right: 0px;
  margin-left: 0px;
  margin-top: 4px;
  margin-bottom: 0px;
  background-color: #ceddf2;
  float: left;
  border-bottom: #ceddf2 1px solid;
  }  
  
div.ss_inactiveTabcontainer {
  margin-right: 0px;
  margin-left: 0px;
  margin-top: 4px;
  margin-bottom: 0px;
  background-color: #e5e5e5;
  float: left;
  border-bottom: #c1c1c1 1px solid;
  }  

div.ss_disabledTabcontainer {
  margin-right: 0px;
  margin-left: 0px;
  margin-top: 4px;
  margin-bottom: 0px;
  background-color: #e5e5e5;
  float: left;
  text-align: center;
  color: #666666;
  font-weight: bold; 
  font-size: 12px;
  border-bottom: #c1c1c1 1px solid;
  }  

div.ss_upperleft {
  background: url(<html:imagesPath/>pics/sym_s_upperleft.gif) no-repeat top left;
}

div.ss_upperright { 
  background: url(<html:imagesPath/>pics/sym_s_upperright.gif) no-repeat top right;
  }

div.ss_inactiveTab, div.ss_disabledTab {
  padding-left: 7px;
  padding-right: 7px;
  padding-top: 4px;
  padding-bottom: 3px;
  }

div.ss_activeTab {
  padding-left: 7px;
  padding-right: 7px;
  padding-top: 4px;
  padding-bottom: 3px;
  font-size: 12px;
  font-weight: bold;
  color: #3366cc;
  }
  
div.ss_activeTab a:link, div.ss_activeTab a:visited {
  font-size: 12px;
  font-weight: bold;
  color: #3366cc;
  text-decoration: none;
  }

div.ss_activeTab a:focus, div.ss_activeTab a:hover, div.ss_activeTab a:active {
  font-size: 12px;
  font-weight: bold;
  color: #ff0000;
  text-decoration: none;
  }

div.ss_inactiveTab a:link, div.ss_inactiveTab a:visited {
  font-size: 12px;
  font-weight: bold;
  color: #3366cc;
  text-decoration: none;
  }

div.ss_inactiveTab a:focus, div.ss_inactiveTab a:hover, div.ss_inactiveTab a:active { 
  font-size: 12px;
  font-weight: bold;
  color: #ff0000;
  text-decoration: none;
  } 

/* end of Tab div styling */


/* -------------------Skins-------------------- */
.ss_content_outer {
	padding-bottom:100px;
	margin-top:1px;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/back1.gif) repeat;
}
.ss_content_inner {
	width:100%;
	padding:0px 2px 0px 10px;
}
.ss_clear {
	clear:both;
	height:1px;
	font-size:0px;
}
/* round corners: */
.ss_decor-round-corners-top1{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/border1.gif) repeat-x;
}
.ss_decor-round-corners-top1 div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/corner1.gif) no-repeat left;
}
.ss_decor-round-corners-top1 div div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/corner2.gif) no-repeat right;
	height:19px;
}
.ss_decor-round-corners-bottom1{
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
	background:#FFF url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/border3.gif) repeat-y left;
}
.ss_decor-border4{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners/border4.gif) repeat-y right;
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
	margin-bottom:10px;
}
.ss_decor-round-corners-bottom2 div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/corner3.gif) no-repeat left;
}
.ss_decor-round-corners-bottom2 div div{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/corner4.gif) no-repeat right;
	height:10px;
	font-size:1px;
}
.ss_decor-border5{
	background:#FFF url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/border.gif) repeat-y left;
}
.ss_decor-border6{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/roundcorners2/border.gif) repeat-y right;
}
.ss_rounden-content{
	padding:0px 35px 10px 10px;
}

/* global toolbar: */
.ss_global_toolbar{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/back2.gif) repeat-x;
	height:60px;
<c:if test="<%= isIE %>">
	margin-bottom:0px;
</c:if>
<c:if test="<%= !isIE %>">
	margin-bottom:36px;
</c:if>
}
.ss_global_toolbar_links{
	float:right;
	margin-top:4px;
	margin-right:50px;
}
* html .ss_global_toolbar_links {
	margin-right:25px;
}
.ss_global_toolbar_links li {
	float:left;
	margin-left:19px;
}
.ss_global_toolbar_links li div {
	display:block;
	padding-top:32px;
	color:#484848;
	text-align:center;
}
.ss_global_toolbar_favs div {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/toolbar/favs.gif) no-repeat top;
}
.ss_global_toolbar_myworkspace div {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/toolbar/workspace.gif) no-repeat top;
}
.ss_global_toolbar_clipboard div {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/toolbar/clipboard.gif) no-repeat top;
}
.ss_global_toolbar_help div {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/toolbar/help.gif) no-repeat top;
}
.ss_global_toolbar_search {
	float:right;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/toolbar/searchback.gif);
	width:198px;
	height:48px;
	position:relative;
}
.ss_global_toolbar_search .form-text {
	position:absolute;
	top:16px;
	left:17px;
	width:127px;
}
.ss_global_toolbar_search .go {
	position:absolute;
	top:15px;
	left:152px;

}

/* tabs: */
.ss_tabs{
	margin-left:45px;
	margin-top:10px;
	position:relative;
<c:if test="<%= !isIE %>">
	top:-12px;
</c:if>
<c:if test="<%= isIE %>">
	top:10px;
</c:if>	
}
.ss_tabs li{
	float:left;
	height:35px;
	position:relative;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back1.gif) repeat-x top;
}
.ss_tabs li.ss_decor-finish-tabs{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/ulback.gif) no-repeat right;
	width:4px;
}
.ss_tabs li.ss_tabs_active{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back1_active.gif) repeat-x top;
}
.ss_tabs li.ss_tabs_longtab{
	/*background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/longtab.gif) no-repeat;*/
}
.tabs li.shorttab{
	/* background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/shorttab.gif) no-repeat;*/
}
.ss_tabs li a{
	font:bold 14px "Trebuchet MS", Arial, sans-serif;
	color:#5A3C68;
	display:block;
	padding-left:12px;
	margin-right:35px;
	padding-top:12px;
	padding-bottom:4px;
}
.ss_tabs li a:hover{
	text-decoration:none;
}
.ss_tabs li.ss_tabs_icon1 a{
	/* background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/tabicon1.gif) no-repeat 13px 2px;*/
}
.ss_tabs li.ss_tabs_icon2 a{
	/* background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/tabicon2.gif) no-repeat 13px 2px;*/
}
.ss_tabs li a.ss_tabs_tabclose{
	position:absolute;
	margin-right:8px;
	padding-left:0px;
	background:none;
	right:0px;
	z-index:2;
}
.ss_tabs li.ss_active a.ss_tabs_tabclose{
	display:none;
}
#ss_tabs_undertabs{
	position:relative;
	margin-top:2px;
<c:if test="<%= !isIE %>">
	top:20px;
</c:if>
<c:if test="<%= isIE %>">
	top:2px;
</c:if>	
}
.ss_tabs_tabwrap1{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back2.gif) no-repeat left top;
}
li.ss_tabs_active .ss_tabs_tabwrap1{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back2_active.gif) no-repeat left top;

}
.ss_tabs_tabend{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back3.gif) no-repeat right top;
	position:absolute;
	right:-1px;
	width:15px;
	height:35px;
	z-index:1;

}
li.ss_tabs_active .ss_tabs_tabend{
	background:url(<html:imagesPath/>skins/${ss_user_skin}/tabs/back3_active.gif) no-repeat right top;

}
.ss_tabs_tabwrap2{
}
.ss_tabs_tabwrap3{
}

/* breadcrumbs */
.ss_breadcrumb {
	color:#5A3C68;
	border:1px solid #EBEBEB;
	padding:5px;
	margin-top:10px;
	margin-bottom:0px;
	font:bold 12px "Trebuchet MS", Arial, sans-serif;
	float:left;
}
.ss_breadcrumb a{
	color:#5A3C68;
	font:bold 12px "Trebuchet MS", Arial, sans-serif;
}

/* actions: */
.ss_actions_bar {
	list-style-type:none;
	width:100%;
	margin:0px;
	padding:0px;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/back3.gif) repeat-x;
	height:23px;
	line-height:23px;
	border-left:1px solid #9687A7;
}
.ss_actions_bar_background {
	margin:0px;
	padding:0px;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/back3.gif) repeat-x;
}
.ss_actions_bar li {
	float:left;
	font-weight:bold;
	border-right:1px solid #9687A7;
	border-left:1px solid #FFF;
}
.ss_actions_bar li.ss_actions_bar_last-child {
	border-right:none;
}
.ss_actions_bar li a {
	color:#5A3C68;
	display:block;
	padding:0px 15px;
}
.ss_actions_bar li a:hover, .ss_actions_bar li a.ss_active {
	background:url(<html:imagesPath/>skins/${ss_user_skin}/back4.gif) repeat-x;
	text-decoration:none;
}
.ss_actions_bar.ss_actions_bar_lower {
	margin:0px 3px;
}
div.ss_actions_bar_submenu {
	margin:0px;
	padding:0px;
	text-align:left;
	position:absolute;
	display:none;
	z-index:500;
}
.ss_actions_bar_submenu {
	position:absolute;
	top:-10px;
	left:-20px;
	background:#EFEEF3;
	border:1px solid #907FA3;
	border-top:none;
	padding:4px;
}
.ss_actions_bar_submenu li  {
	float:none;
	padding:0px 15px;
	font-weight:bold;
	border-bottom:1px solid #E0DFEF;
	border-right-style:none;
	background:url(<html:imagesPath/>skins/${ss_user_skin}/bullet1.gif) no-repeat 5px 5px;
	width:250px;
}
.ss_actions_bar_submenu div {
	background:none;
}
.ss_actions_bar_submenu li:hover, .ss_actions_bar_submenu a:hover {
	text-decoration:underline;
	background-color:#FBFAFF;
}

/* Footer */
.ss_bottomlinks {
	text-align:center;
	margin:5px 0px;
	color:#8E8FA7;
}


</c:if>
