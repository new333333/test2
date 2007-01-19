<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<script language="JavaScript">
function reloadUrlFromApplet()
{
	alert("Called from Applet");
}
</script>

<%
 boolean isIE = com.sitescape.util.BrowserSniffer.is_ie(request);
%>

<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<div class="ss_entryContent">
<br/>
<span class="ss_labelLeft"><c:out value="${property_caption}"/></span>

<c:if test="<%= isIE %>">
<object id="dropboxobj" classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" CLASS="dropbox" 
  WIDTH = "20" HEIGHT = "20" NAME = "launcher" ALIGN = "middle" VSPACE = "0" HSPACE = "0" 
  codebase="http://java.sun.com/update/1.5.0/jinstall-1_5-windows-i586.cab#Version=5,0,0,3">
</c:if>
<c:if test="<%= !isIE %>">
<applet CODE = "com.sitescape.ef.applets.droptarget.TopFrame" 
  JAVA_CODEBASE = "<html:rootPath/>applets" 
  ARCHIVE = "droptarget/ssf-droptarget-applet.jar" 
  WIDTH = "20" HEIGHT = "20">
</c:if>
    <PARAM NAME = CODE VALUE = "com.sitescape.ef.applets.droptarget.TopFrame" >
    <PARAM NAME = CODEBASE VALUE = "<html:rootPath/>applets" >
    <PARAM NAME = ARCHIVE VALUE = "droptarget/ssf-droptarget-applet.jar" >
    <PARAM NAME = "type" value="application/x-java-applet;version=1.5">
    <param name = "scriptable" value="true">
    <PARAM NAME = "NAME" VALUE = "droptarget" >
    <PARAM NAME = "startingDir" VALUE="">
    <PARAM NAME = "reloadFunctionName" VALUE="reloadUrlFromApplet">
    <PARAM NAME = "bgcolorFunctionName" VALUE="getWindowBgColor">
    <PARAM NAME = "savePreviousVersions" VALUE="yes">
    <PARAM NAME = "fileReceiverURL" VALUE="<ssf:url adapter="true" actionUrl="true" portletName="ss_forum" action="add_attachment_entry"><ssf:param name="binderId" value="${ssDefinitionEntry.parentBinder.id}"/><ssf:param name="entryId" value="${ssDefinitionEntry.id}"/><ssf:param name="operation" value="add_files_from_applet" /></ssf:url>">
    <PARAM NAME = "deactivationUrl" VALUE="">
    <PARAM NAME = "displayUrl" VALUE="0">
    <PARAM NAME = "loadDirectory" VALUE="no" />
    <PARAM NAME = "menuLabelPaste" VALUE="<ssf:nlt tag="binder.add.files.applet.menu.paste" />" />
    <PARAM NAME = "menuLabelCancel" VALUE="<ssf:nlt tag="binder.add.files.applet.menu.cancel" />" />
    <PARAM NAME = "menuLabelDeactivate" VALUE="<ssf:nlt tag="binder.add.files.applet.menu.deactivate" />" />
    <PARAM NAME = "directoryLoadErrorMessage" value="<ssf:nlt tag="binder.add.files.applet.no.directory" />" />
<c:if test="<%= !isIE %>">
</applet>
</c:if>
<c:if test="<%= isIE %>">
</object>
</c:if>

<br>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
<div style="margin:0px; padding:0px;">
<a style="text-decoration: none;" href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>" 
<c:if test="${ssConfigJspStyle != 'mail'}">    
    onClick="return ss_launchUrlInNewWindow(this, '${selection.fileItem.name}');"
</c:if>
     ><c:out value="${selection.fileItem.name} "/></a>
<c:if test="${ssConfigJspStyle != 'mail'}">        
<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}">
<a style="text-decoration: none;"
	href="<ssf:ssfsInternalAttachmentUrl 
		binder="${ssDefinitionEntry.parentBinder}"
		entity="${ssDefinitionEntry}"
		fileAttachment="${selection}"/>">
		<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="Edit"/>]</span></a>
</ssf:ifSupportsEditInPlace>
</c:if>
<div class="ss_indent_medium">
<table class="ss_compact20">
<tr>
<td class="ss_compact20"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${selection.modification.date}" type="both" 
	 timeStyle="short" dateStyle="short" /></td>
<td class="ss_compact20"><span class="ss_smallprint">(${selection.fileItem.lengthKB}KB)

<a target="_blank" style="text-decoration: none;" href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    <ssf:param name="viewType" value="html"/>
    </ssf:url>" >[HTML]</a>
</span>
</td>
</tr>
</table>
</div>
</div>
<c:set var="versionCount" value="0"/>
<c:forEach var="fileVersion" items="${selection.fileVersions}">
<c:set var="versionCount" value="${versionCount + 1}"/>
</c:forEach>
<c:if test="${!empty selection.fileVersions && versionCount > 1}">
<div class="ss_indent_medium">
<span class="ss_bold"><ssf:nlt tag="entry.PreviousVersions"/></span>
<br>
<c:set var="versionCount" value="0"/>
<table class="ss_compact20">
<c:forEach var="fileVersion" items="${selection.fileVersions}">
<c:if test="${versionCount > 0}">
<tr>
<td class="ss_compact20"><a style="text-decoration: none;"
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    <ssf:param name="versionId" value="${fileVersion.id}"/>
    </ssf:url>"><ssf:nlt tag="entry.version"/> ${fileVersion.versionNumber}</a></td>
<td class="ss_compact20"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${fileVersion.modification.date}" type="both" 
	 timeStyle="short" dateStyle="short" /></td>
<td class="ss_compact20"><span class="ss_smallprint">(${fileVersion.fileItem.lengthKB}KB)

<a style="text-decoration: none;" href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    <ssf:param name="viewType" value="html"/>
    </ssf:url>" >[HTML]</a>

</span></td>
</tr>
</c:if>
<c:set var="versionCount" value="${versionCount + 1}"/>
</c:forEach>
</table>
</div>
<br>
</c:if>
</c:forEach>
</div>
</c:if>
