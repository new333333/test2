<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<script language="JavaScript">
var iFrameInvokedOnce<portlet:namespace/> = "false";
function reloadUrlFromApplet()
{
	alert("Called from Applet");
}

function ss_showAddAttachmentBrowse<portlet:namespace/>() {
	var divId = 'ss_div_browse<portlet:namespace/>';
	var divObj = document.getElementById(divId);

	var frameId = 'ss_iframe_browse<portlet:namespace/>';	
	var frameObj = document.getElementById(frameId);
	
	frameObj.src = "<html:rootPath/>js/attachments/entry_attachment_browse.html";
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";
	
	divObj.style.width = "360px";
	divObj.style.height = "80px";

    //ss_setObjectTop(divObj, (ss_getDivTop('ss_browse_div_position<portlet:namespace/>')+20) + "px");
	//ss_setObjectLeft(divObj, (ss_getDivLeft('ss_browse_div_position<portlet:namespace/>')) + "px");
}

function ss_hideAddAttachmentBrowse<portlet:namespace/>() {
	var divId = 'ss_div_browse<portlet:namespace/>';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

function ss_showAddAttachmentDropbox<portlet:namespace/>() {
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="binderId" value="${ssDefinitionEntry.parentBinder.id}" />
		<ssf:param name="entryId" value="${ssDefinitionEntry.id}" />
		<ssf:param name="operation" value="add_attachment_options" />
		<ssf:param name="namespace" value="${renderResponse.namespace}" />
    	</ssf:url>"

	var divId = 'ss_div_dropbox<portlet:namespace/>';
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_dropbox<portlet:namespace/>';	
	var frameObj = document.getElementById(frameId);
	
	if (iFrameInvokedOnce<portlet:namespace/> == "false") {
		frameObj.src = url;
		iFrameInvokedOnce<portlet:namespace/> = "true";
	}

	ss_showDiv(divId);
	frameObj.style.visibility = "visible";

	divObj.style.width = "50px";
	divObj.style.height = "50px";

    //ss_setObjectTop(divObj, (ss_getDivTop('ss_dropbox_div_position<portlet:namespace/>')+20) + "px");
    //ss_setObjectLeft(divObj, (ss_getDivLeft('ss_dropbox_div_position<portlet:namespace/>')) + "px");
}

function ss_hideAddAttachmentDropbox<portlet:namespace/>() {
	var divId = 'ss_div_dropbox<portlet:namespace/>';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

function ss_postAttachment<portlet:namespace/>(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_showAddAttachments<portlet:namespace/>();
}

function resizeIFrame<portlet:namespace />() {
/*
	var divObj = document.getElementById("ss_add_attachment_display<portlet:namespace/>");
	var iframeObj = document.getElementById("ss_iframe_add_attachment<portlet:namespace/>");
	
	var entryHeight = divObj.scrollHeight;
	
	entryHeight = entryHeight;
	
	ss_setObjectHeight(divObj, entryHeight);
	ss_setObjectHeight(iframeObj, entryHeight);
*/
}

function setURLInIFrame() {
 	var url = "<ssf:url 
    	portletName="ss_forum" 
    	action="add_entry_attachment" 
    	actionUrl="true" >
		<ssf:param name="binderId" value="${ssDefinitionEntry.parentBinder.id}" />
		<ssf:param name="entryId" value="${ssDefinitionEntry.id}" />
		<ssf:param name="operation" value="add_files_by_browse_for_entry" />
    	</ssf:url>";
	this.frames['ss_iframe_browse<portlet:namespace/>'].setURL(url, "<ssf:nlt tag="button.ok"/>", "<ssf:nlt tag="button.cancel"/>", "<ssf:nlt tag="entry.chooseFileWarningMessage"/>", "ss_hideAddAttachmentBrowse<portlet:namespace/>()");
}

</script>

<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<div class="ss_entryContent">

<table width="100%" border="0" valign="top" cellpadding="0" cellspacing="0">
<tr>
	<td width="40%" valign="top">
		<span id="ss_browse_div_position<portlet:namespace/>" class="ss_labelLeft"><c:out value="${property_caption}"/>&nbsp;&nbsp;<ssf:nlt tag="entry.addattachments"/></span>
	</td>

	<td width="10%" valign="top">
		<a class="ss_linkButton ss_smallprint" id="ss_dropbox_div_position<portlet:namespace/>" href="javascript: ;" onClick="ss_showAddAttachmentDropbox<portlet:namespace/>(); return false;">
			<ssf:nlt tag="entry.AttachFilesByApplet"/>
		</a>
		<div id="ss_div_dropbox<portlet:namespace/>" class="ss_border_light" style="visibility:hidden;display:none;">
			<div align="right">
			<a  onClick="ss_hideAddAttachmentDropbox<portlet:namespace />(); return false;"><img 
			  border="0" src="<html:imagesPath/>box/close_off.gif"/></a>
			</div>	
			<iframe frameborder="0" scrolling="no" id="ss_iframe_dropbox<portlet:namespace/>" name="ss_iframe_dropbox<portlet:namespace/>" height="70%" width="80%" onClick="ss_hideAddAttachmentDropbox<portlet:namespace />(); return false;">xxx</iframe>
		</div>
	</td>

	<td width="10%" valign="top">
		<a class="ss_linkButton ss_smallprint" style="behavior: url(#default#AnchorClick);" folder="${ssWebDavURL}" href="${ssWebDavURL}" folder="${ssWebDavURL}" target="_blank">
			<ssf:nlt tag="entry.AttachFilesByWebDav"/>
		</a>
	</td>

	<td width="10%" valign="top">
		<a class="ss_linkButton ss_smallprint" href="javascript: ;" onClick="ss_showAddAttachmentBrowse<portlet:namespace/>(); return false;">
			<ssf:nlt tag="entry.AttachFilesByWebBrowse"/>
		</a>
	</td>
	
	<td width="30%">&nbsp;</td>	
</tr>
<tr>
	<td colspan="5" width="100%">
		<table width="100%" cellpadding="0" cellspacing="0">
			<tr>
				<td width="40%"></td>
				<td width="60%">
					<div id="ss_div_browse<portlet:namespace/>" class="ss_border_light" style="visibility:hidden;display:none;">
						<div align="right">
						<a onClick="ss_hideAddAttachmentBrowse<portlet:namespace/>(); return false;"><img 
						  border="0" src="<html:imagesPath/>box/close_off.gif"/></a>
						</div>	
						<iframe frameborder="0" scrolling="no" id="ss_iframe_browse<portlet:namespace/>" name="ss_iframe_browse<portlet:namespace/>" onLoad="javascript:setURLInIFrame();" src="<html:rootPath/>js/attachments/entry_attachment_browse.html" height="75%" width="100%">xxx</iframe>
					</div>
				</td>
			</tr>
		</table>
	</td>	
</tr>
</table>

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
<br />
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
<br />
</c:if>
</c:forEach>
</div>
</c:if>

