<% // View entry attachments %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.ssfs.util.SsfsUtil" %>

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%
boolean isIE = BrowserSniffer.is_ie(request);
String operatingSystem = BrowserSniffer.getOSInfo(request);
boolean isAppletSupported = SsfsUtil.supportApplets();
%>

<script language="JavaScript">
var iFrameInvokedOnce${ssDefinitionEntry.id}<portlet:namespace/> = "false";
var iFrameFileOpenInvokedOnce${ssDefinitionEntry.id}<portlet:namespace/> = "false";

function reloadUrlFromApplet()
{
	alert("Called from Applet");
}

function ss_showAddAttachmentBrowse${ssDefinitionEntry.id}<portlet:namespace/>() {
	ss_hideAddAttachmentDropbox${ssDefinitionEntry.id}<portlet:namespace/>();
	var divId = 'ss_div_browse${ssDefinitionEntry.id}<portlet:namespace/>';
	var divObj = document.getElementById(divId);

	var frameId = 'ss_iframe_browse${ssDefinitionEntry.id}<portlet:namespace/>';	
	var frameObj = document.getElementById(frameId);
	
	frameObj.src = "<html:rootPath/>js/attachments/entry_attachment_browse.html";
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";
	
	divObj.style.width = "360px";
	divObj.style.height = "120px";

    //ss_setObjectTop(divObj, (ss_getDivTop('ss_browse_div_position<portlet:namespace/>')+20) + "px");
	//ss_setObjectLeft(divObj, (ss_getDivLeft('ss_browse_div_position<portlet:namespace/>')) + "px");
}

function ss_hideAddAttachmentBrowse${ssDefinitionEntry.id}<portlet:namespace/>() {
	var divId = 'ss_div_browse${ssDefinitionEntry.id}<portlet:namespace/>';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

function ss_hideAddAttachmentBrowseAndAJAXCall${ssDefinitionEntry.id}<portlet:namespace/>() {
	ss_hideAddAttachmentBrowse${ssDefinitionEntry.id}<portlet:namespace/>();
	ss_selectEntryAttachmentAjax${ssDefinitionEntry.id}<portlet:namespace/>();
}

function ss_showAddAttachmentDropbox${ssDefinitionEntry.id}<portlet:namespace/>() {
	ss_hideAddAttachmentBrowse${ssDefinitionEntry.id}<portlet:namespace/>();
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

	var divId = 'ss_div_dropbox${ssDefinitionEntry.id}<portlet:namespace/>';
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_dropbox${ssDefinitionEntry.id}<portlet:namespace/>';	
	var frameObj = document.getElementById(frameId);
	
	if (iFrameInvokedOnce${ssDefinitionEntry.id}<portlet:namespace/> == "false") {
		frameObj.src = url;
		iFrameInvokedOnce${ssDefinitionEntry.id}<portlet:namespace/> = "true";
	}

	ss_showDiv(divId);
	frameObj.style.visibility = "visible";

	divObj.style.width = "300px";
	divObj.style.height = "55px";

	if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
}

function ss_hideAddAttachmentDropbox${ssDefinitionEntry.id}<portlet:namespace/>() {
	var divId = 'ss_div_dropbox${ssDefinitionEntry.id}<portlet:namespace/>';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

function ss_hideAddAttachmentDropboxAndAJAXCall${ssDefinitionEntry.id}<portlet:namespace/>() {
	ss_hideAddAttachmentDropbox${ssDefinitionEntry.id}<portlet:namespace/>();
	ss_selectEntryAttachmentAjax${ssDefinitionEntry.id}<portlet:namespace/>();
}

function ss_postAttachment${ssDefinitionEntry.id}<portlet:namespace/>(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_showAddAttachments<portlet:namespace/>();
}

function setURLInIFrame${ssDefinitionEntry.id}<portlet:namespace />() {
 	var url = "<ssf:url 
    	adapter="true"
    	portletName="ss_forum" 
    	action="add_entry_attachment" 
    	actionUrl="true" >
		<ssf:param name="binderId" value="${ssDefinitionEntry.parentBinder.id}" />
		<ssf:param name="entryId" value="${ssDefinitionEntry.id}" />
		<ssf:param name="operation" value="add_files_by_browse_for_entry" />
    	</ssf:url>";
	this.frames['ss_iframe_browse${ssDefinitionEntry.id}<portlet:namespace/>'].setURL(url, "<ssf:nlt tag="button.ok"/>", 
						"<ssf:nlt tag="button.cancel"/>", "<ssf:nlt tag="entry.chooseFileWarningMessage"/>", 
						"ss_hideAddAttachmentBrowse${ssDefinitionEntry.id}<portlet:namespace/>()",
						"ss_hideAddAttachmentBrowseAndAJAXCall${ssDefinitionEntry.id}<portlet:namespace/>()", 
						"<ssf:nlt tag="entry.browseAddAttachmentHelpText"/>");
}

function ss_selectEntryAttachmentAjax${ssDefinitionEntry.id}<portlet:namespace/>() {
	ss_setupStatusMessageDiv()
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="binderId" value="${ssDefinitionEntry.parentBinder.id}" />
		<ssf:param name="entryId" value="${ssDefinitionEntry.id}" />
		<ssf:param name="operation" value="reload_entry_attachments" />
		<ssf:param name="namespace" value="${renderResponse.namespace}" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postSelectEntryAttachment${ssDefinitionEntry.id}<portlet:namespace/>);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_postSelectEntryAttachment${ssDefinitionEntry.id}<portlet:namespace/>(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	var divObj = document.getElementById('ss_divAttachmentList${ssDefinitionEntry.id}<portlet:namespace/>');
	var s = divObj.innerHTML;
}

var editClicked = "false";

function ss_openWebDAVFile${ssDefinitionEntry.id}<portlet:namespace/>(strURLValue) {
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="binderId" value="${ssDefinitionEntry.parentBinder.id}" />
		<ssf:param name="entryId" value="${ssDefinitionEntry.id}" />
		<ssf:param name="operation" value="open_webdav_file" />
		<ssf:param name="namespace" value="${renderResponse.namespace}" />
		<ssf:param name="ssOSInfo" value="<%= operatingSystem %>" />
    	</ssf:url>"
    	
    url = url + "&ssEntryAttachmentURL="+strURLValue;

	var divId = "ss_div_fileopen${ssDefinitionEntry.id}<portlet:namespace/>";
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_fileopen${ssDefinitionEntry.id}<portlet:namespace/>';	
	var frameObj = document.getElementById(frameId);
	
	editClicked = "true";
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";

	frameObj.src = url;
	
	divObj.style.width = "1px";
	divObj.style.height = "1px";
}
function ss_checkEditClicked${ssDefinitionEntry.id}<portlet:namespace/>() 
{
	return editClicked;
}
function ss_resetEditClicked${ssDefinitionEntry.id}<portlet:namespace/>()
{
	editClicked = "false";
}
</script>

<div class="ss_entryContent">

<table width="100%" border="0" valign="top" cellpadding="1" cellspacing="0">
<tr>
	<td width="40%" valign="top">
		<span id="ss_browse_div_position${ssDefinitionEntry.id}<portlet:namespace/>" 
		class="ss_labelLeft"><c:out value="${property_caption}"/>&nbsp;&nbsp;</span>
	</td>

	<td valign="top">
		<% if (isAppletSupported) { %>
			<a class="ss_linkButton" id="ss_dropbox_div_position${ssDefinitionEntry.id}<portlet:namespace/>" href="javascript: ;" onClick="ss_showAddAttachmentDropbox${ssDefinitionEntry.id}<portlet:namespace/>(); return false;">
				<ssf:nlt tag="entry.AttachFilesByApplet"/>
			</a>
		<% } %>
	</td>

	<% if (isIE) { %>
	<td valign="top">
		<c:if test="${ss_folderViewStyle == 'blog'}">
			<c:set var="ss_entryIDForWebDAV" value="${ssDefinitionEntry.id}" />
			<a class="ss_linkButton" style="behavior: url(#default#AnchorClick);" folder="${ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}" href="${ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}" target="_blank">
				<ssf:nlt tag="entry.AttachFilesByWebDav"/>
			</a>		
		</c:if>
		<c:if test="${ss_folderViewStyle != 'blog'}">
			<a class="ss_linkButton" style="behavior: url(#default#AnchorClick);" folder="${ssWebDavURL}" href="${ssWebDavURL}" target="_blank">
				<ssf:nlt tag="entry.AttachFilesByWebDav"/>
			</a>		
		</c:if>
	</td>
	<% } %>
	
	<td valign="top">
		<a class="ss_linkButton" href="javascript: ;" onClick="ss_showAddAttachmentBrowse${ssDefinitionEntry.id}<portlet:namespace/>(); return false;">
			<ssf:nlt tag="entry.AttachFilesByWebBrowse"/>
		</a>
	</td>
	
	<% if (isIE) { %>
	<td width="30%">
	<% } else { %>
	<td width="40%">
	<% } %>
		<div id="ss_div_fileopen${ssDefinitionEntry.id}<portlet:namespace/>" width="1px" height="1px" name="ss_div_fileopen${ssDefinitionEntry.id}<portlet:namespace/>" style="visibility:hidden;display:none;">
			<div align="right">
				<iframe frameborder="0" scrolling="no" id="ss_iframe_fileopen${ssDefinitionEntry.id}<portlet:namespace/>" name="ss_iframe_fileopen${ssDefinitionEntry.id}<portlet:namespace/>" height="100%" width="100%">xxx</iframe>
			</div>
		</div>
	</td>	
</tr>
<tr>
	<td colspan="5" width="100%">
		<table width="100%" cellpadding="0" cellspacing="0">
			<tr>
				<td width="40%"></td>
				<td width="60%">
					<div id="ss_div_dropbox${ssDefinitionEntry.id}<portlet:namespace/>" class="ss_border_light" style="visibility:hidden;display:none;">
						<div align="right">
						<a onClick="ss_hideAddAttachmentDropbox${ssDefinitionEntry.id}<portlet:namespace />(); return false;"><img 
						  border="0" src="<html:imagesPath/>box/close_off.gif"/></a>
						</div>	
						<iframe frameborder="0" scrolling="no" id="ss_iframe_dropbox${ssDefinitionEntry.id}<portlet:namespace/>" name="ss_iframe_dropbox${ssDefinitionEntry.id}<portlet:namespace/>" height="70%" width="100%" onClick="ss_hideAddAttachmentDropbox${ssDefinitionEntry.id}<portlet:namespace />(); return false;">xxx</iframe>
					</div>

					<div id="ss_div_browse${ssDefinitionEntry.id}<portlet:namespace/>" class="ss_border_light" style="visibility:hidden;display:none;">
						<div align="right">
						<a onClick="ss_hideAddAttachmentBrowse${ssDefinitionEntry.id}<portlet:namespace/>(); return false;"><img 
						  border="0" src="<html:imagesPath/>box/close_off.gif"/></a>
						</div>	
						<iframe frameborder="0" scrolling="no" id="ss_iframe_browse${ssDefinitionEntry.id}<portlet:namespace/>" name="ss_iframe_browse${ssDefinitionEntry.id}<portlet:namespace/>" onLoad="javascript:setURLInIFrame${ssDefinitionEntry.id}<portlet:namespace/>();" src="<html:rootPath/>js/attachments/entry_attachment_browse.html" height="75%" width="100%">xxx</iframe>
					</div>
				</td>
			</tr>
		</table>
	</td>	
</tr>
</table>

<c:set var="ss_viewEntryAttachmentDivId" value="ss_divAttachmentList${ssDefinitionEntry.id}${renderResponse.namespace}" scope="request"/>
<c:set var="ss_namespace_attach" value="${renderResponse.namespace}" scope="request"/>

<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_attachments_list.jsp" %>

</div>

<c:if test="${ssConfigJspStyle == 'mail'}">
<% // The mail support is in "definition_elements/mail" %>
</c:if>
