<%@ page import="com.sitescape.util.BrowserSniffer" %>

<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>

<%
// Color values used in ss styles, highlighting, borders, and headers
// Select a color theme: "blackandwhite" or "debug"
%>
<c:set var="ss_color_theme" value="blackandwhite" scope="request"/>
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


