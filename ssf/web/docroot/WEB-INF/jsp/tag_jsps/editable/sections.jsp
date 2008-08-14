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
--%><ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<c:set var="ss_editableSectionIdNumber" value="0"/>
<c:forEach var="part" items="${parts}"><%--
	--%>${part['prefix']}
	<div class="ss_entryContent ss_entryDescription" <%--
    --%> id="ss_editableSectionDiv${renderResponse.namespace}_${ss_editableSectionIdNumber}"><%--
	--%><c:if test="${!empty part['sectionTitle']}"><%--
		--%><div class="ss_editableTab"><span class="${part['sectionTitleClass']}">${part['sectionTitle']}</span></div><%--
		--%><c:if test="${aclMap[entity.id]['modifyEntry']}"><%--
			--%><div align="right" class="ss_editableTab"><a href="javascript:;"<%--
	    	--%> onMouseOver="ss_editableHighlight('over', this, 'ss_editableSectionDiv${renderResponse.namespace}_${ss_editableSectionIdNumber}');return false;"<%--
	    	--%> onMouseOut="ss_editableHighlight('out', this, 'ss_editableSectionDiv${renderResponse.namespace}_${ss_editableSectionIdNumber}');return false;"<%--
	    	--%> onClick="ss_editablePopUp('${editUrl}', 'ss_editableSectionDiv${renderResponse.namespace}_${ss_editableSectionIdNumber}', '${part['sectionNumber']}');return false;"<%--
	    	--%> <ssf:title tag="title.edit.description" /><%--
	    	--%>><span class="ss_fineprint"><%--
	    	--%><ssHelpSpot helpId="workspaces_folders/entries/more_entry_tools" offsetX="-15"<%--
			--%> title="<ssf:nlt tag="helpSpot.moreEntryTools"/>"></ssHelpSpot><%--
			--%>[<ssf:nlt tag="Edit"/>]</span></a><%--
			--%></div><%--
		--%></c:if><%--
		--%><div class="ss_clear"></div><%--
	--%></c:if><%--
	--%>${part['sectionBody']}<%--
	--%></div><%--
	--%><c:set var="ss_editableSectionIdNumber" value="${ss_editableSectionIdNumber + 1}"/>
</c:forEach>

