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

<c:if test="${ss_toolbar_item}">
<!-- Start of toolbar item -->
  <li class="ss_toolBarItem">
</c:if>
<c:if test="${!ss_toolbar_item}">
<!-- Start of toolbar -->
<ul class="${ss_toolbar_style}">
</c:if>

<c:if test="${!empty ss_toolbar}">
	<c:choose>
		<c:when test="${ss_toolbar_is_accessible == 'true'}">
			<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_accessible_view.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
		</c:otherwise>
	</c:choose>
</c:if>