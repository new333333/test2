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
<div id="<%= divId %>" width="<%= width %>">
<%@ include file="/WEB-INF/jsp/box/box_top-ext.jsp" %>
<c:if test="<%= decorateBox %>">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td colspan="3"><img border="0" height="10" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
		<td class="<%= titleClassName %><%= BrowserSniffer.is_ie(request) ? "-gradient" : StringPool.BLANK %>" rowspan="4">
			<c:if test="<%= Validator.isNotNull(title) %>">
				<table border="0" cellpadding="2" cellspacing="0">
				<tr>
					<td nowrap><font class="<%= titleClassName %>" size="2"><%= boldTitle ? "<b>" : "" %>&nbsp;<%= title %>&nbsp;<%= boldTitle ? "</b>" : "" %></font></td>
				</tr>
				</table>
			</c:if>
		</td>
		<td></td>
		<td rowspan="4">
			<table border="0" cellpadding="0" cellspacing="0">
			  <tr>
				<c:if test="<%= showCloseIcon %>">
					<script language="JavaScript">
						loadImage("p_<portlet:namespace/>_close", "<html:imagesPath/>box/close_on.gif", "<html:imagesPath/>box/close_off.gif");
					</script>

					<td rowspan="3">
						<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><img border="0" height="6" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
						</tr>
						<tr>
							<td class="<%= bodyClassName %>"><a 
							 href="javascript: <%= showCloseRoutine %>;"><img 
							 border="0" height="14" hspace="0" 
							 name="p_<portlet:namespace/>_close" 
							 src="<html:imagesPath/>box/close_off.gif" 
							 title="<ssf:nlt tag="close" text="Close" />" 
							 vspace="0" width="14" onMouseOut="offRollOver();" 
							 onMouseOver="onRollOver('p_<portlet:namespace/>_close');"></a></td>
						</tr>
						<tr>
							<td class="<%= bodyClassName %>"><img border="0" height="6" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
						</tr>
						</table>
					</td>
				</c:if>

				<td><img border="0" height="10" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
			  </tr>
			  <tr>
				<td class="alpha" width="1"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
			  </tr>
			  <tr>
				<td class="<%= bodyClassName %>"><img border="0" height="14" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
			  </tr>
			</table>
		</td>
		<td colspan="2"><img border="0" height="10" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
	</tr>
	<tr>
		<td colspan="2" rowspan="2"><img border="0" height="5" hspace="0" src="<html:imagesPath/>box/<%= bodyClassName %>_edge_ul.gif" vspace="0" width="5"></td>

		<td class="alpha" width="5"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="5"></td>
		<td class="alpha" width="<%= wildWidth %>"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
		<td class="alpha" width="5"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="5"></td>

		<td colspan="2" rowspan="2"><img border="0" height="5" hspace="0" src="<html:imagesPath/>box/<%= bodyClassName %>_edge_ur.gif" vspace="0" width="5"></td>
	</tr>
	<tr>
		<td class="<%= bodyClassName %>"><img border="0" height="4" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
		<td class="<%= bodyClassName %>" rowspan="2"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
		<td class="<%= bodyClassName %>"><img border="0" height="4" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
		<td></td>
	</tr>
	<tr>
		<td class="alpha" width="1"><img border="0" height="10" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
		<td class="<%= bodyClassName %>"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="4"></td>
		<td class="<%= bodyClassName %>"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="5"></td>
		<td class="<%= bodyClassName %>"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="5"></td>
		<td class="<%= bodyClassName %>"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="4"></td>
		<td class="alpha" width="1"><img border="0" height="10" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
	</tr>
	</table>
</c:if>

<c:if test="<%= !decorateBox %>">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">

	<tr>
		<td rowspan="2" width="5"><img border="0" height="5" hspace="0" src="<html:imagesPath/>box/<%= bodyClassName %>_edge_ul.gif" width="5" vspace="0"></td>
		<td class="alpha"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="*"></td>
		<td rowspan="2" width="5"><img border="0" height="5" hspace="0" src="<html:imagesPath/>box/<%= bodyClassName %>_edge_ur.gif" width="5" vspace="0"></td>
	</tr>
	<tr>
		<td colspan="3" class="<%= bodyClassName %>"><img border="0" height="4" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="*"></td>
	</tr>

	</table>
</c:if>

<table border="0" cellpadding="0" cellspacing="0" <%= false ? "style=\"table-layout: fixed;\"" : "" %> width="100%">
<tr id="p_p_body_<portlet:namespace/>" >
	<td class="alpha" width="1"><img border="0" height="1" hspace="0" src="<html:imagesPath/>pics/spacer.gif" vspace="0" width="1"></td>
	<td width="<%= wildWidth %>">
		<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr class="<%= bodyClassName %>">
			<td>
				<c:if test="<%= brWrapContent %>">
					<br>
				</c:if>