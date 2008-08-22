<%
/**
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
	String slidingTableRowOddStyle = (String) request.getAttribute("ss_slidingTableRowOddStyle");
	String slidingTableRowEvenStyle = (String) request.getAttribute("ss_slidingTableRowEvenStyle");

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
<tr <%= slidingTableRowIdText %>
<c:if test="${ss_slidingTableRowCount%2 == '0'}">class="${ss_slidingTableRowStyle} ${ss_slidingTableRowOddStyle}" </c:if>
<c:if test="${ss_slidingTableRowCount%2 == '1'}">class="${ss_slidingTableRowStyle} ${ss_slidingTableRowEvenStyle}" </c:if>
<c:set var="ss_slidingTableRowCount" value="${ss_slidingTableRowCount + 1}" scope="request"/>
>
<%
	}
	
	if (slidingTableRowColumns != null) {
		for (int i = 0; i < slidingTableRowColumns.size(); i++) {
			Map column = (Map)slidingTableRowColumns.get(i);
			String width = (String) column.get("width");
			String text = (String) column.get("text");
			String colStyle = (String) column.get("style");
			//Make sure the column text field has more than just white space
	    	String testText = text.replaceAll("[\\s]", "");
	    	if (testText.equals("") || testText.toLowerCase().equals("<span></span>")) text = "&nbsp;";
			String widthAttr = "";
			if (width != null && !width.equals("")) widthAttr = "width='" + width + "' ";
			if (colStyle == null) colStyle = "";
			if (slidingTableHeaderRow != null && slidingTableHeaderRow.booleanValue()) {
%>
  <th scope="col" class="ss_bold <%= colStyle %>" align="left" <%= widthAttr %> style="padding: 4px 2px 5px 5px; border:.4px solid #CCCCCC; border-collapse: collapse;"><%= text %></th>
<%
			} else {
%>
  <td valign="top" <%= widthAttr %> class="ss_normal <%= colStyle %>"
    style="padding: 4px 2px 5px 5px; border:.4px solid #CCCCCC; border-collapse: collapse;"><%= text %></td>
<%
			}
		}
	}
%>
</tr>
