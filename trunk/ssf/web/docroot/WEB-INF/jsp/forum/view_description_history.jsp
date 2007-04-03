<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="org.dom4j.Element" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>

<div class="ss_style ss_portlet">
<h3><ssf:nlt tag="entry.versionHistory"/></h3>
<form class="ss_form" method="post" action="<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		action="view_editable_history" 
		actionUrl="true">
		<ssf:param name="entityId" value="${ss_entityId}" />
		<ssf:param name="operation" value="modifyEntry" />
		</ssf:url>"
>
<table class="ss_style" cellpadding="10" width="100%">
<th><ssf:nlt tag="entry.Version"/></th><th><ssf:nlt tag="entry.data"/></th>
<c:forEach var="change" items="${ss_changeLogList}">
<tr>
<td valign="top" width="50"><input type="radio" name="item1" 
  value="${change.folderEntry.attributes.logVersion}"/>
  <input style="padding-left:10px;" type="radio" name="item2" 
  value="${change.folderEntry.attributes.logVersion}"/>
  <span style="padding-left:10px;">${change.folderEntry.attributes.logVersion}</span>
</td>
<td valign="top">
  <ssf:expandableArea title="${change.folderEntry.attributes.modifedOn}">
  <div><span class="ss_largeprint">${change.folderEntry.attribute.title}</span></div>
  <div><span class="ss_smallprint">${change.folderEntry.attributes.modifedBy}</span></div>
  <div class="ss_entryContent ss_entryDescription">
    ${change.folderEntry.attribute.description}
  </div>
  </ssf:expandableArea>
</td>
</tr>
</c:forEach>
<tr>
<td valign="top">
  <input type="submit" name="okBtn" value="<ssf:nlt tag="button.compare"/>"/>
</td>
<td></td>
</tr>
</table>
</form>
</div>
<br/>

<c:if test="${!empty titleDiff}">
<div class="ss_largeprint">${titleDiff}</div>
</c:if>
<c:if test="${!empty descriptionDiff}">
<div class="ss_entryContent ss_entryDescription">${descriptionDiff}</div>
</c:if>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
