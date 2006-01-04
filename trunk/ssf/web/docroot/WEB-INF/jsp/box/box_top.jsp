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

<%

// General variables

String divId = ParamUtil.get(request, "box_id", "");
String titleClassName = ParamUtil.get(request, "box_title_class", "beta");
String bodyClassName = ParamUtil.get(request, "box_body_class", "bg");

String title = ParamUtil.get(request, "box_title", "");

int iWidth = (int)ParamUtil.get(request, "box_width", 600);
String width = Integer.toString(iWidth);

String wildWidth = "*";
try {
	wildWidth = Integer.toString(iWidth - 2);
}
catch (Exception e) {
}

boolean boldTitle = ParamUtil.get(request, "box_bold_title", true);
boolean brWrapContent = ParamUtil.get(request, "box_br_wrap_content", true);

boolean showCloseIcon = ParamUtil.get(request, "box_show_close_icon", false);
String showCloseRoutine = ParamUtil.get(request, "box_show_close_routine", "");

boolean decorateBox = false;
if (Validator.isNotNull(title) || (showCloseIcon == true)) {
	decorateBox = true;
}
%>
<div class="ss_box_rounded" id="<%= divId %>" style="width: <%= width %>;">
<%@ include file="/WEB-INF/jsp/box/box_top-ext.jsp" %>
	<div class="ss_box_small_icon_bar" id="<%= divId %>_icon_bar">
	  <c:if test="<%= showCloseIcon %>">
		<span class="ss_box_small_icon"><a 
			 href="javascript: <%= showCloseRoutine %>;"><img 
			 border="0" height="14" hspace="0" 
			 name="p_<portlet:namespace/>_close" 
			 src="<html:imagesPath/>box/close_off.gif" 
			 title="<ssf:nlt tag="close" text="Close" />" 
			 vspace="0" width="14" ></a></span>
	  </c:if>
	</div><!-- end ss_box_small_icon_bar -->

	<c:if test="<%= Validator.isNotNull(title) %>">
	  <div class="ss_box_title">
	    <div style="position: relative; font-size: smaller; padding-top: 5px;"><b>&nbsp;<%= title %>&nbsp;</b></div>
	  </div>
	</c:if>
	
  <div class="ssf_box">
     <div class="ss_box_minimum_height">
	    <div style="margin-top: 0; margin-bottom: 0;">


<c:if test="<%= brWrapContent %>">
  <br>
</c:if>
