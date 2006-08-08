<%
/**
 * Copyright (c) 2000-2005 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
%>

<%@ include file="/WEB-INF/jsp/box/init.jsp" %>
<jsp:useBean id="ss_box_color" type="String" scope="request" />
<jsp:useBean id="ss_box_canvas_color" type="String" scope="request" />

<%

// General variables

String divId = ParamUtil.get(request, "box_id", "");
String titleClassName = ParamUtil.get(request, "box_title_class", "ss_largestprint");

String title = ParamUtil.get(request, "box_title", "");
%>
<c:set var="boxColor" value="<%= ParamUtil.get(request, "box_color", ss_box_color) %>" />
<c:set var="boxBgColor" value="<%= ParamUtil.get(request, "box_canvas_color", ss_box_canvas_color) %>" />
<%
int iWidth = (int)ParamUtil.get(request, "box_width", 600);
String width = Integer.toString(iWidth);

String wildWidth = "*";
try {
	wildWidth = Integer.toString(iWidth - 2);
}
catch (Exception e) {
}

boolean boldTitle = ParamUtil.get(request, "box_bold_title", true);
boolean brWrapContent = ParamUtil.get(request, "box_br_wrap_content", false);

boolean showCloseIcon = ParamUtil.get(request, "box_show_close_icon", false);
String showCloseRoutine = ParamUtil.get(request, "box_show_close_routine", "");

boolean showResizeIcon = ParamUtil.get(request, "box_show_resize_icon", false);
String showResizeRoutine = ParamUtil.get(request, "box_show_resize_routine", "");
String showResizeGif = ParamUtil.get(request, "box_show_resize_gif", "box/resize.gif");

boolean showMoveIcon = ParamUtil.get(request, "box_show_move_icon", false);
String showMoveRoutine = ParamUtil.get(request, "box_show_move_routine", "");

boolean decorateBox = false;
if (Validator.isNotNull(title) || (showCloseIcon == true)) {
	decorateBox = true;
}
%>
<div class="ss_box_top_rounded" id="<%= divId %>" 
  style="width: <%= width %>; background-color:${boxColor};">
<%@ include file="/WEB-INF/jsp/box/box_top-ext.jsp" %>
	<div class="ss_box_small_icon_bar" 
	  style="margin:0px; background-color:${boxColor};" 
	  id="<%= divId %>_icon_bar">
	  <table cellspacing="0" cellpaddng="0" width="100%" 
	    style="margin:0px; padding:0px; background-color:${boxColor};">
	  <tr>
	  <c:if test="<%= showResizeIcon %>">
		<td align="left" width="10%"><div style="display:inline; width:20px; 
		    background-position:center left;
            background-image:url(<html:imagesPath/><%= showResizeGif %>);
            background-repeat:no-repeat;" onMouseDown="<%= showResizeRoutine %>"
		  ><span class="ss_box_small_icon" align="left" 
		  style="cursor:w-resize; width:20px;"><img src="<html:imagesPath/>pics/1pix.gif" 
		  style="margin:4px 20px;"></span></div></td>
	  </c:if>

	  <td align="center" width="80%"><div align="center"
	  <c:if test="<%= showMoveIcon %>">
	    onMouseDown="<%= showMoveRoutine %>"
	  </c:if>
	    style="margin:0px; 
	  <c:if test="<%= showMoveIcon %>">
	    cursor:move; 
	  </c:if>
	      padding:0px;"><%= title %></div></td>
	
	  <c:if test="<%= showCloseIcon %>">
		<td align="right" width="10%"><span class="ss_box_small_icon"><a 
			 href="javascript: <%= showCloseRoutine %>;"><img 
			 border="0" height="14" hspace="0" 
			 name="p_<portlet:namespace/>_close" 
			 src="<html:imagesPath/>box/close_off.gif" 
			 title="<ssf:nlt tag="icon.close" text="Close" />" 
			 vspace="0" width="14" ></a></span></td>
	  </c:if>
	  </tr>
	  </table>
	</div><!-- end ss_box_small_icon_bar -->

  <div class="ssf_box">
     <div class="ss_box_minimum_height" style="background-color:${boxColor};">
	    <div style="margin-top: 0; margin-bottom: 0;">
<c:if test="<%= brWrapContent %>">
  <br>
</c:if>
