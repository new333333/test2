<% //File entry title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div class="ss_entryContent">
<span class="ss_entryTitle">
<c:set var="title_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="title_entry" type="com.sitescape.ef.domain.Entry" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<%
	if (!ssSeenMap.checkIfSeen(title_entry)) {
		ssSeenMap.setSeen(title_entry);
		%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
<c:set var="fileHandle" value="${ssDefinitionEntry.customAttributes['_fileEntryTitle'].value}"/>
<c:if test="${empty fileHandle}">
<c:set var="fileHandle" value="${ssDefinitionEntry.customAttributes['title'].value}"/>
</c:if>
<a style="text-decoration: none;" href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${fileHandle.id}"/>
    </ssf:url>">
<c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_light">--<ssf:nlt tag="entry.noTitle"/>--</span>
</c:if><c:out value="${ssDefinitionEntry.title}"/></a></span>

<ssf:ifSupportsEditInPlace relativeFilePath="${fileHandle.fileItem.name}">
<a style="text-decoration: none;"
	href="<ssf:ssfsLibraryFileUrl 
		binder="${ssDefinitionEntry.parentBinder}"
		entity="${ssDefinitionEntry}"
		fileAttachment="${fileHandle}"/>">
		<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="Edit"/>]</span></a>
</ssf:ifSupportsEditInPlace>

<c:set var="versionCount" value="0"/>
<c:forEach var="fileVersion" items="${fileHandle.fileVersions}">
<c:set var="versionCount" value="${versionCount + 1}"/>
</c:forEach>
<c:if test="${!empty fileHandle.fileVersions && versionCount > 1}">
<div class="ss_indent_medium">
<span class="ss_bold"><ssf:nlt tag="entry.PreviousVersions"/></span>
<br>
<c:set var="versionCount" value="0"/>
<table class="ss_compact20">
<c:forEach var="fileVersion" items="${fileHandle.fileVersions}">
<c:if test="${versionCount > 0}">
<tr>
<td class="ss_compact20"><a style="text-decoration: none;"
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${fileHandle.id}"/>
    <ssf:param name="versionId" value="${fileVersion.id}"/>
    </ssf:url>"><ssf:nlt tag="entry.version"/> ${fileVersion.versionNumber}</a></td>
<td class="ss_compact20"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${fileVersion.modification.date}" type="both" 
	 timeStyle="short" dateStyle="short" /></td>
<td class="ss_compact20"><span class="ss_smallprint">(${fileVersion.fileItem.lengthKB}KB)</span></td>
</tr>
</c:if>
<c:set var="versionCount" value="${versionCount + 1}"/>
</c:forEach>
</table>
</div>
<br>
</c:if>

</div>
