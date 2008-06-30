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
<c:if test="${empty ss_box_color}"><c:set var="ss_box_color" value="#CCCCCC" scope="request"/></c:if>
<c:if test="${empty ss_box_canvas_color}"><c:set var="ss_box_canvas_color" value="#FFFFAA" scope="request"/></c:if>
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
//If width is set to 0, then use "100% instead
if (iWidth == 0) {
	width = "100%";
} else {
	width = width + "px";
}

String boxClass = ParamUtil.get(request, "box_class", "ss_box_top_rounded");
String boxStyle = ParamUtil.get(request, "box_style", "");
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
<c:set var="colWidth" value="99"/>
<c:if test="<%= showMoveIcon %>">
    <c:set var="colWidth" value="${colWidth - 35}"/>
</c:if>
<c:if test="<%= showMoveIcon %>">
    <c:set var="colWidth" value="${colWidth - 35}"/>
</c:if>
<c:if test="<%= showCloseIcon %>">
    <c:set var="colWidth" value="${colWidth - 10}"/>
</c:if>

<c:set var="ss_boxColCount" value="2" scope="request"/>
<div class="<%= boxClass %>" id="<%= divId %>" 
  style="width: <%= width %>; <%= boxStyle %>">
<%@ include file="/WEB-INF/jsp/box/box_top-ext.jsp" %>
	<table cellspacing="0" cellpadding="0" width="100%">
	  <col width="8"/>
	  <c:if test="<%= showResizeIcon %>">
	    <col width="20"/>
		<c:set var="ss_boxColCount" value="${ss_boxColCount + 1}" scope="request"/>
	  </c:if>
	  <c:if test="<%= showMoveIcon %>">
	    <col width="35%"/>
		<c:set var="ss_boxColCount" value="${ss_boxColCount + 1}" scope="request"/>
	  </c:if>
	  <col width="${colWidth}%"/>
	  <c:if test="<%= showMoveIcon %>">
	    <col width="35%"/>
		<c:set var="ss_boxColCount" value="${ss_boxColCount + 1}" scope="request"/>
	  </c:if>
	  <c:if test="<%= showCloseIcon %>">
	    <col width="10%"/>
		<c:set var="ss_boxColCount" value="${ss_boxColCount + 1}" scope="request"/>
	  </c:if>
	  <col width="8"/>
	  <tr>
	  <td><img border="0" <ssf:alt/>
	    src="<html:imagesPath/>roundcorners3/corner1.jpg"></td>
	  <c:if test="<%= showResizeIcon %>">
		<td class="ss_title_bar" align="left"><div style="display:inline; width:20px; 
		    background-position:center left;
            background-image:url(<html:imagesPath/><%= showResizeGif %>);
            background-repeat:no-repeat;" onMouseDown="<%= showResizeRoutine %>"
		  ><span class="ss_box_small_icon" 
		  style="cursor:w-resize; width:20px;"><img border="0" <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif" 
		  style="width: 20px; height: 12px;"></span></div></td>
	  </c:if>

	  <c:if test="<%= showMoveIcon %>">
	    <td class="ss_title_bar"><div onMouseDown="<%= showMoveRoutine %>"
	    style="margin:0px; cursor:move; padding:0px;"><img <ssf:alt/> border="0" style="height:15px;"
	    src="<html:imagesPath/>pics/1pix.gif"/></div></td>
	  </c:if>

	  <td class="ss_title_bar" align="center"><div class="ss_title_bar" align="center"
	    style="margin:0px; padding:0px;"><%= title %></div></td>
	
	  <c:if test="<%= showMoveIcon %>">
	    <td class="ss_title_bar"><div onMouseDown="<%= showMoveRoutine %>"
	    style="margin:0px; cursor:move; padding:0px;"><img border="0" <ssf:alt/> style="height:15px;"
	    src="<html:imagesPath/>pics/1pix.gif"/></div></td>
	  </c:if>

	  <c:if test="<%= showCloseIcon %>">
		<td class="ss_title_bar" align="right"><span class="ss_box_small_icon"><a 
			 href="javascript: <%= showCloseRoutine %>;"><img border="0" <ssf:alt/>
			 border="0" height="14" hspace="0" 
			 name="p_${renderResponse.namespace}_close" 
			 src="<html:imagesPath/>icons/close_off.gif" 
			 title="<ssf:nlt tag="icon.close" text="Close" />" 
			 vspace="0" width="14" ></a></span></td>
	  </c:if>
	  <td><img <ssf:alt/> border="0" src="<html:imagesPath/>roundcorners3/corner2.jpg"></td>
	  </tr>
	  
	  <tr>
	  <td class="ss_decor-border7" colspan="${ss_boxColCount}" style="background-color:${boxBgColor};">

<c:if test="<%= brWrapContent %>">
  <br>
</c:if>
<div style="margin:0px 5px 0px 0px; padding:0px; position:relative; left:5px;">