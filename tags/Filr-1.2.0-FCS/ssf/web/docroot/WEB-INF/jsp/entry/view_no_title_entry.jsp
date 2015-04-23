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
%>
<% // No entry by title %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<ssf:ifadapter>
<body class="ss_style_body tundra">
</ssf:ifadapter>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>

<div class="ss_style ss_portlet">
<span class="ss_entryTitle">${ssEntryTitle}</span>
<br/>
<br/>
<span class="ss_smallprint ss_italic">[<ssf:nlt tag="entry.noEntryByThisTitle"/>]</span>
<c:if test="${!empty ssAddEntryTitles}">
<br/>
<br/>
<c:set var="count" value="0"/>
<c:forEach var="title" items="${ssAddEntryTitles}">
  <c:set var="count" value="${count + 1}"/>
</c:forEach>

<script type="text/javascript">
function ss_setPageFormActionUrl${renderResponse.namespace}(url) {
	document.forms['ss_createPageForm'].action = url;
}
function ss_setPageFormActionUrl2${renderResponse.namespace}() {
	var formObj = document.forms['ss_createPageForm']
	for (var i = 0; i < formObj.url.length; i++) {
		if (formObj.url[i].checked) url = formObj.url[i].value;
	}
	formObj.action = url;
}
</script>

<form method="post" name="ss_createPageForm">
<input type="hidden" name="title" value="<ssf:escapeQuotes>${ssEntryTitle}</ssf:escapeQuotes>"/>
<c:if test="${count == 1}">
  <c:forEach var="title" items="${ssAddEntryTitles}">
    <input type="submit" class="ss_linkButton" name="addBtn" value="<ssf:nlt tag="entry.createPage"/>"
      onClick="ss_setPageFormActionUrl${renderResponse.namespace}('${ssAddEntryUrls[title.value]}');"/>
  </c:forEach>
  <a href="javascript:;" style="padding-left: 10px;" class="ss_linkButton"
    onClick="self.history.back();return false;"><ssf:nlt tag="button.cancel"/></a>
</c:if>
<c:if test="${count > 1}">
  <table><tr><td valign="top">
  <c:set var="firstTime" value="1"/>
  <c:forEach var="title" items="${ssAddEntryTitles}">
    <input type="radio" name="url" value="${ssAddEntryUrls[title.value]}"
    <c:if test="${firstTime == '1'}">
      checked="checked"
    </c:if>
    />${title.key}<br/>
  <c:set var="firstTime" value="0"/>
  </c:forEach>
  </td>
  <td valign="top">
    <input type="submit" class="ss_linkButton" name="addBtn" value="<ssf:nlt tag="entry.createPage"/>"
      onClick="ss_setPageFormActionUrl2${renderResponse.namespace}();"/>
  </td></tr></table>
  <br/>
  <br/>
  <a href="javascript:;" style="padding-left: 10px;" class="ss_linkButton"
    onClick="self.history.back();return false;"><ssf:nlt tag="button.cancel"/> </a>
</c:if>
</c:if>
</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
