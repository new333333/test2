<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
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
--%><%@ page import="org.kablink.util.BrowserSniffer" %><%--
--%><%
		boolean isIE = BrowserSniffer.is_ie(request);
	%><%--
--%><portletadapter:defineObjects1/><%--
--%><ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter><%--
--%><ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<c:if test="${empty ss_editableSectionTOCNumber}">
<c:set var="ss_editableSectionTOCNumber" value="0" scope="request"/>
</c:if>
<c:set var="ss_editableSectionIdNumber" value="0"/>
<script type="text/javascript">
    function ss_showHideToc${ss_editableSectionTOCNumber}() {
    	var tocObj = document.getElementById('ss_wiki_toc${ss_editableSectionTOCNumber}');
    	var tocButtonObj = document.getElementById('ss_wiki_toc_toggle${ss_editableSectionTOCNumber}');
    	if (tocObj.style.display != 'block') {
     	tocObj.style.display = 'block';
     	tocObj.style.visibility = 'visible';
     	tocObj.style.opacity = '1';
     	tocButtonObj.innerHTML = "<ssf:nlt tag="button.hide"/>"
    	} else {
    		ss_hideDiv('ss_wiki_toc${ss_editableSectionTOCNumber}');
    		tocButtonObj.innerHTML = "<ssf:nlt tag="button.show"/>"
    	}
    }
</script>
<c:forEach var="part" items="${parts}"><%--
	--%>${part['prefix']}
	<c:if test="${ss_editableSectionIdNumber == 0}">
		<table class="ss_editableSectionTOC">
		<tr>
		<td>
		<div class="ss_editableSectionTOC">
		  <h2><ssf:nlt tag="wiki.editable.toc.contents"/></h2>
		  <span class="ss_smallprint" style="padding: 0px 6-px;">
		    [<a href="#" onClick="ss_showHideToc${ss_editableSectionTOCNumber}();return false;">
		      <span id="ss_wiki_toc_toggle${ss_editableSectionTOCNumber}"><ssf:nlt tag="button.hide"/></span>
		    </a>]
		  </span>
		</div>
		<div id="ss_wiki_toc${ss_editableSectionTOCNumber}" style="display:block;">
		<ul class="ss_editableSectionTOC">
		  <c:set var="ss_TOCeditableSectionIdNumber" value="0"/>
		  <c:forEach var="tocPart" items="${parts}">
		    <c:if test="${!empty tocPart['sectionTitle']}">
		      <c:set var="toc_padding" value="${tocPart['sectionDepth'] * 10}"/>
		      <li style="padding-left: ${toc_padding}px;">
		        <a href="#ss_editableSectionDiv${renderResponse.namespace}_${ss_TOCeditableSectionIdNumber}">
		        <span>${tocPart['sectionNumberText']}</span> 
		        <span>${tocPart['sectionTitle']}</span>
		        </a>
		      </li>
		    </c:if>
		    <c:set var="ss_TOCeditableSectionIdNumber" value="${ss_TOCeditableSectionIdNumber + 1}"/>
		  </c:forEach>
		</ul>
		</div>
		</td>
		</tr>
		</table>
		<c:set var="ss_editableSectionTOCNumber" value="${ss_editableSectionTOCNumber + 1}" scope="request"/>
	</c:if>
	<div <%--
    --%> id="ss_editableSectionDiv${renderResponse.namespace}_${ss_editableSectionIdNumber}"><%--
	--%><c:if test="${!empty part['sectionTitle']}"><%--
		--%><table width="100%" cellspacing="0" cellpadding="0"><tr><td width="90%" class="ss_editableSectionHeader${part['sectionDepth']}"><%--
		--%><div class="ss_editableSectionTitle"><span class="${part['sectionTitleClass']}">${part['sectionTitle']}</span></div><%--
		--%></td><td width="10%" class="ss_editableSectionHeader${part['sectionDepth']}"><%--
		--%><c:if test="${aclMap[entityId]['modifyEntry']}"><%--
			--%><div align="right" class="ss_editableSectionEditBtn"><a href="javascript:;"<%--
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
		--%></td></tr></table><%--
		--%><div class="ss_clear"></div><%--
	--%></c:if><%--
	--%>${part['sectionBody']}<%--
	--%></div><%--
	--%><c:set var="ss_editableSectionIdNumber" value="${ss_editableSectionIdNumber + 1}"/>
</c:forEach>

