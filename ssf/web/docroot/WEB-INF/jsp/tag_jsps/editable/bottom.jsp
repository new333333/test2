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
%><%--
--%><%@ page session="false" %><%--
--%><%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%--
--%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%--
--%><%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %><%--
--%><%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %><%--
--%><%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %><%--
--%><%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %><%--
--%><%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %><%--
--%><%@ page import="com.sitescape.util.BrowserSniffer" %><%--
--%><%
		boolean isIE = BrowserSniffer.is_ie(request);
	%><%--
--%><portletadapter:defineObjects1/><%--
--%><ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter><%--
--%><ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter><%--
--%><c:if test="${aclMap[entity.id]['modifyEntry']}"><%--
	--%><div class="ss_editableTab"><a href="javascript:;"<%--
    --%> onMouseOver="ss_editableHighlight('over', this, '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_editableDiv${ss_editableDivIdNumber}');return false;"<%--
    --%> onMouseOut="ss_editableHighlight('out', this, '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_editableDiv${ss_editableDivIdNumber}');return false;"<%--
    --%> onClick="ss_editablePopUp('${editUrl}', '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_editableDiv${ss_editableDivIdNumber}');return false;"<%--
    --%> <ssf:title tag="title.edit.description" /><%--
    --%>><span class="ss_fineprint"><%--
    --%><ssHelpSpot helpId="workspaces_folders/entries/more_entry_tools" offsetX="-15"<%--
	--%><c:if test="<%= !isIE %>"><%--
		--%> offsetY="-15"<%--
	--%></c:if><%--
	--%> title="<ssf:nlt tag="helpSpot.moreEntryTools"/>"></ssHelpSpot><%--
	--%>[<ssf:nlt tag="Edit"/>]</span></a><%--
	--%></div><%--
--%></c:if><%--
--%><c:set var="ss_editableDivIdNumber" value="${ss_editableDivIdNumber + 1}" scope="request"/><%--
--%></div><%--
--%><div class="ss_clear"></div>