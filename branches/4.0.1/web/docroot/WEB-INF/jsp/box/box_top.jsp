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
<c:set var="boxColor" value='<%= ParamUtil.get(request, "box_color", ss_box_color) %>' />
<c:set var="boxBgColor" value='<%= ParamUtil.get(request, "box_canvas_color", ss_box_canvas_color) %>' />
<%
int iWidth = (int)ParamUtil.get(request, "box_width", 600);
String width = Integer.toString(iWidth);
//If width is set to 0, then use "100% instead
if (iWidth == 0) {
	width = "100%";
} else {
	width = width + "px";
}

String boxTitleId = ParamUtil.get(request, "box_title_id", "");
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
<c:set var="ss_boxTitle" value="<%= title %>"/>
<c:set var="ss_boxTitleId" value="<%= boxTitleId %>"/>
<c:set var="colWidth" value="95"/>
<c:if test="<%= showMoveIcon %>">
    <c:set var="colWidth" value="${colWidth - 80}"/>
</c:if>
<c:if test="<%= showCloseIcon %>">
    <c:set var="colWidth" value="${colWidth - 6}"/>
</c:if>

<c:set var="ss_boxColCount" value="3" scope="request"/>
<div class="<%= boxClass %>" id="<%= divId %>" 
  style="width: <%= width %>; <%= boxStyle %>">
<%@ include file="/WEB-INF/jsp/box/box_top-ext.jsp" %>
	<table cellspacing="0" cellpadding="0" width="100%">
	  <col width="2%"/>
	  <c:if test="<%= showResizeIcon %>">
	    <col width="8%"/>
		<c:set var="ss_boxColCount" value="${ss_boxColCount + 1}" scope="request"/>
	  </c:if>
	  <c:if test="<%= showMoveIcon %>">
	    <c:if test="${!empty ss_boxTitle}"><col width="1%"/></c:if>
	    <c:if test="${empty ss_boxTitle}"><col width="0%"/></c:if>
	  </c:if>
	  <c:if test="<%= !showMoveIcon %>">
	    <col width="${colWidth}%"/>
	  </c:if>
	  <c:if test="<%= showMoveIcon %>">
	    <col width="80%"/>
		<c:set var="ss_boxColCount" value="${ss_boxColCount + 1}" scope="request"/>
	  </c:if>
	  <col width="2%"/>
	  <c:if test="<%= showCloseIcon %>">
	    <col width="6%"/>
		<c:set var="ss_boxColCount" value="${ss_boxColCount + 1}" scope="request"/>
	  </c:if>
	  <col width="2%"/>
	  <tr>
	  <td class="ss_title_bar"></td>
	  <c:if test="<%= showResizeIcon %>">
		<td class="ss_title_bar" align="left">
			<div style="display:inline; width:40px; 
		    	background-position:center left;
            	background-image:url(<html:imagesPath/><%= showResizeGif %>);
            	background-repeat:no-repeat;" onMouseDown="<%= showResizeRoutine %>"
			>
			<span class="ss_box_small_icon" style="cursor:w-resize; width:20px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></div></td>
	  </c:if>

	  <c:if test="${empty ss_boxTitle}">
	    <td class="ss_title_bar" nowrap><span id="${ss_boxTitleId}"></span></td>
	  </c:if>
	
	  <c:if test="${!empty ss_boxTitle}">
	    <td class="ss_title_bar" nowrap>
	      <div style="margin:0px; padding:0px;">
	        <span style="padding-left: 10px;" id="${ss_boxTitleId}">${ss_boxTitle}</span>
	      </div>
	    </td>
	  </c:if>
	
	  <c:if test="<%= showMoveIcon %>">
	    <td class="ss_title_bar"><div onMouseDown="<%= showMoveRoutine %>"
	    style="margin:0px; cursor:move; padding:0px;"><img border="0" <ssf:alt/> style="height:15px;"
	    src="<html:imagesPath/>pics/1pix.gif"/></div></td>
	  </c:if>

	  <td class="ss_title_bar" nowrap><span id="ss_showEntryDivNextPrev"></span></td>
	  <c:if test="<%= showCloseIcon %>">
		<td class="ss_title_bar" align="right" style="padding-left: 10px;"><span class="ss_box_small_icon ss_nowrap ss_close_text"><a 
			 href="javascript: ;" onClick="<%= showCloseRoutine %>;return false;"><ssf:nlt tag="button.close"/>&nbsp;<img align="absmiddle" border="0" <ssf:alt/>
			 height="16" hspace="0" 
			 name="p_${renderResponse.namespace}_close" 
			 src="<html:imagesPath/>icons/close_gray16.png" 
			 title="<ssf:nlt tag="icon.close" text="Close" />" 
			 vspace="0" width="16" ></a></span></td>
	  </c:if>
	  <td class="ss_title_bar"></td>
	  </tr>
	  
	  <tr>
	  <td class="ss_decor-border7" colspan="${ss_boxColCount}">

<c:if test="<%= brWrapContent %>">
  <br>
</c:if>
<div>