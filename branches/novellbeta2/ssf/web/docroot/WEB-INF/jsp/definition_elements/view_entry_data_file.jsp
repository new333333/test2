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
<% //File view %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "nonie";
if (isIECheck) strBrowserType = "ie";
%>

<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
<div class="ss_entryContent">
<span class="ss_bold"><c:out value="${property_caption}" /></span>

<span>
<c:set var="selections" value="${ssDefinitionEntry.customAttributes[property_name].valueSet}" />
<c:forEach var="selection" items="${selections}">
<a target="_blank" 
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}"
    entityType="${ssDefinitionEntry.entityType}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name}"/>
</a>
<c:if test="${ssConfigJspStyle != 'mail'}">
<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
<a 
	href="<ssf:ssfsInternalFileUrl 
		binder="${ssDefinitionEntry.parentBinder}"
		entity="${ssDefinitionEntry}"
		elemName="${property_name}"
		fileAttachment="${selection}"/>"><b><font color="#FF0000">Edit</font></b>
</a>
</ssf:ifSupportsEditInPlace>
</c:if>
<br>
</c:forEach>
</span>
</div>
</c:if>
