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
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ page import="com.sitescape.util.ParamUtil" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<%@ page import="java.lang.String" %>
<%@ page import="java.lang.Boolean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
	String slidingTableRowId = (String) request.getAttribute("ss_slidingTableRowId");
	List slidingTableRowColumns = (List) request.getAttribute("ss_slidingTableRowColumns");
	Boolean slidingTableHeaderRow = (Boolean) request.getAttribute("ss_slidingTableHeaderRow");

	String slidingTableRowIdText = "";
	if (slidingTableRowId != null && !slidingTableRowId.equals("")) {
		slidingTableRowIdText = "id='" + slidingTableRowId + "' ";
	}
	if (slidingTableHeaderRow != null && slidingTableHeaderRow.booleanValue()) {
%>
<tr class="ss_headerRow" <%= slidingTableRowIdText %>>
<%
	} else {
%>
<tr <%= slidingTableRowIdText %>>
<%
	}
	
	if (slidingTableRowColumns != null) {
		for (int i = 0; i < slidingTableRowColumns.size(); i++) {
			Map column = (Map)slidingTableRowColumns.get(i);
			String width = (String) column.get("width");
			String text = (String) column.get("text");
			String widthAttr = "";
			if (width != null && !width.equals("")) widthAttr = "width='" + width + "' ";
			if (slidingTableHeaderRow != null && slidingTableHeaderRow.booleanValue()) {
%>
  <th class="ss_bold" align="left" <%= widthAttr %>><%= text %></th>
<%
			} else {
%>
  <td valign="top" <%= widthAttr %>><%= text %></td>
<%
			}
		}
	}
%>
</tr>
