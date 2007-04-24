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
<% // No entry by title %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<ssf:ifadapter>
<body class="ss_style_body">
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
function ss_setPageFormActionUrl<portlet:namespace/>(url) {
	document.forms['ss_createPageForm'].action = url;
}
function ss_setPageFormActionUrl2<portlet:namespace/>() {
	var formObj = document.forms['ss_createPageForm']
	for (var i = 0; i < formObj.url.length; i++) {
		if (formObj.url[i].checked) url = formObj.url[i].value;
	}
	formObj.action = url;
}
</script>

<form method="post" name="ss_createPageForm">
<input type="hidden" name="title" value="${ssEntryTitle}"/>
<c:if test="${count == 1}">
  <c:forEach var="title" items="${ssAddEntryTitles}">
    <input type="submit" name="addBtn" value="<ssf:nlt tag="entry.createPage"/>"
      onClick="ss_setPageFormActionUrl<portlet:namespace/>('${ssAddEntryUrls[title.value]}');"/>
  </c:forEach>
  <a href="#" style="padding-left: 10px;" class="ss_linkButton"
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
    <input type="submit" name="addBtn" value="<ssf:nlt tag="entry.createPage"/>"
      onClick="ss_setPageFormActionUrl2<portlet:namespace/>();"/>
  </td></tr></table>
  <br/>
  <br/>
  <a href="#" style="padding-left: 10px;" class="ss_linkButton"
    onClick="self.history.back();return false;"><ssf:nlt tag="button.cancel"/> </a>
</c:if>
</c:if>
</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
