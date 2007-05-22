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
<% // Footer toolbar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.context.request.RequestContextHolder" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%
Boolean webdavSupportedFooter = new Boolean(com.sitescape.team.web.util.BinderHelper.isWebdavSupported(request));
%>

<c:choose>
<c:when test="${empty ss_footerToolbarCount}">
	<c:set var="ss_footerToolbarCount" value="0" scope="request"/>
</c:when>
<c:otherwise>
	<c:set var="ss_footerToolbarCount" value="${ss_footerToolbarCount + 1}" scope="request"/>
</c:otherwise>
</c:choose>

<ssf:skipLink tag="<%= NLT.get("skip.footer.toolbar") %>" id="footerToolbar_${ss_footerToolbarCount}_${renderResponse.namespace}">

<c:set var="isWebdavSupported" value="<%= webdavSupportedFooter %>"/>
<c:if test="${!empty ssFooterToolbar}">
<div align="center" class="ss_footer_toolbar">
  <ssHelpSpot helpId="tools/bottom_links" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.bottomLinks"/>"></ssHelpSpot>
<c:set var="delimiter" value=""/>
<c:forEach var="toolbarMenu" items="${ssFooterToolbar}">
      <c:set var="popup" value="false"/>
      <c:if test="${toolbarMenu.value.qualifiers.popup}">
        <c:set var="popup" value="true"/>
      </c:if>
	  <c:choose>
	    <c:when test="${!empty toolbarMenu.value.url}">
	      <c:if test="${empty toolbarMenu.value.qualifiers.folder || (!empty toolbarMenu.value.qualifiers.folder && isWebdavSupported)}">
      	  <c:out value="${delimiter}" escapeXml="false"/>
	      <div class="ss_bottomlinks">
	      
			  	<% // qualifier 'post' allows to open new page with post method - it sends a form with parameter given as qualifiers.postParams %>
			    <c:if test="${!empty toolbarMenu.value.qualifiers.post}">
				 	<form class="inline" action="${toolbarMenu.value.url}" method="post" <c:if test="${!empty toolbarMenu.value.qualifiers.popup}"> target="footerToolbarOptionWnd"</c:if>>
				 		<c:forEach var="p2" items="${toolbarMenu.value.qualifiers.postParams}">
						  <c:set var="key2" value="${p2.key}"/>
						  
						  <c:forEach var="value2" items="${p2.value}">
					      	<input type="hidden" name="${key2}" value="${value2}"/>
					      </c:forEach>
					      
				        </c:forEach>
				        
				        <a href="javascript: //" onclick="<c:if test="${!empty toolbarMenu.value.qualifiers.popup}">ss_toolbarPopupUrl('', 'footerToolbarOptionWnd')</c:if>; ss_submitParentForm(this); ">${toolbarMenu.value.title}</a>
				 	</form>
			    </c:if>
			    
				<c:if test="${empty toolbarMenu.value.qualifiers.post}">
				 
	      
				      <a href="${toolbarMenu.value.url}"
			    	    <c:if test="${empty toolbarMenu.value.qualifiers.onClick}">
			    	    	<c:if test="${!empty toolbarMenu.value.qualifiers.popup}">
			    	      		onClick="ss_toolbarPopupUrl(this.href);return false;"
			    	    	</c:if>
			    	    </c:if>
			    	    <c:if test="${!empty toolbarMenu.value.qualifiers.onClick}">
			    	      	onClick="${toolbarMenu.value.qualifiers.onClick}"
			    	    </c:if>
			    	    <c:if test="${!empty toolbarMenu.value.qualifiers.folder}">
			<%
					if (BrowserSniffer.is_ie(request)) {
			%>
			    	      	style="behavior: url(#default#AnchorClick);"
			<%
					}
			%>
			    	      	folder="${toolbarMenu.value.qualifiers.folder}"
			    	      	target="_blank"
			    	    </c:if>
				      ><c:out 
				        value="${toolbarMenu.value.title}" /></a>


				</c:if>
       
	        
	        </div>
	  		<c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;|&nbsp;</span>" />
	      </c:if>
	    </c:when>
	    <c:when test="${!empty toolbarMenu.value.urlParams}">
	      <c:if test="${empty toolbarMenu.value.qualifiers.folder || (!empty toolbarMenu.value.qualifiers.folder && isWebdavSupported)}">
          <c:out value="${delimiter}" escapeXml="false"/>
	      <div class="ss_bottomlinks"><a href="<ssf:url>
	        <c:forEach var="p2" items="${toolbarMenu.value.urlParams}">
			  <c:set var="key2" value="${p2.key}"/>
		      <c:set var="value2" value="${p2.value}"/>
	          <ssf:param name="${key2}" value="${value2}" />
	        </c:forEach>
	 	    </ssf:url>"
    	    <c:if test="${empty toolbarMenu.value.qualifiers.onClick}">
    	    	<c:if test="${!empty toolbarMenu.value.qualifiers.popup}">
    	      		onClick="ss_toolbarPopupUrl(this.href);return false;"
    	    	</c:if>
    	    </c:if>
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.onClick}">
    	      	onClick="${toolbarMenu.value.qualifiers.onClick}"
    	    </c:if>
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.folder}">
<%
		if (BrowserSniffer.is_ie(request)) {
%>
    	      	style="behavior: url(#default#AnchorClick);"
<%
		}
%>
    	      	folder="<c:out value="${toolbarMenu.value.qualifiers.folder}" />"
    	      	target="_blank"
    	    </c:if>
	 	  ><c:out 
	 	    value="${toolbarMenu.value.title}" /></a></div>
	  		<c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;|&nbsp;</span>" />
	 	  </c:if>
	    </c:when>
	    <c:otherwise>
          <c:out value="${delimiter}" escapeXml="false"/>
	      <div class="ss_bottomlinks">
	      	<a href="javascript: //"
		        <c:if test="${!empty toolbarMenu.value.qualifiers.onClick}">
	    	      	onClick="${toolbarMenu.value.qualifiers.onClick}"
	    	    </c:if>
	    	    ><c:out value="${toolbarMenu.value.title}" /></a></div>
	      <c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;|&nbsp;</span>" />
	    </c:otherwise>
	  </c:choose>    
</c:forEach>
</div>

<div id="ss_div_folder_dropbox${ssFolder.id}<portlet:namespace/>" class="ss_border_light" style="visibility:hidden;display:none;">
	<div align="right">
	<a  onClick="ss_hideFolderAddAttachmentDropbox${ssFolder.id}<portlet:namespace />(); return false;"><img 
	  <ssf:alt tag="alt.hideThisMenu"/> border="0" src="<html:imagesPath/>box/close_off.gif"/></a>
	</div>	
	<iframe frameborder="0" scrolling="no" id="ss_iframe_folder_dropbox${ssFolder.id}<portlet:namespace/>" name="ss_iframe_folder_dropbox${ssFolder.id}<portlet:namespace/>" height="80%" width="100%">xxx</iframe>
</div>

</c:if>

</ssf:skipLink>
<script language="JavaScript">
var iFrameFolderAttachmentInvokedOnce${ssFolder.id}<portlet:namespace/> = "false"
function getWindowBgColor() {
	return "#ffffff";
}

function ss_showFolderAddAttachmentDropbox${ssFolder.id}<portlet:namespace/>() {
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="binderId" value="${ssFolder.id}" />
		<ssf:param name="operation" value="add_folder_attachment_options" />
		<ssf:param name="namespace" value="${renderResponse.namespace}" />
		<ssf:param name="library" value="${ssFolder.library}" />
    	</ssf:url>"

	var divId = 'ss_div_folder_dropbox${ssFolder.id}<portlet:namespace/>';
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_folder_dropbox${ssFolder.id}<portlet:namespace/>';	
	var frameObj = document.getElementById(frameId);
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";

	if (iFrameFolderAttachmentInvokedOnce${ssFolder.id}<portlet:namespace/> == "false") {
		frameObj.src = url;
		iFrameFolderAttachmentInvokedOnce${ssFolder.id}<portlet:namespace/> = "true";
	}

	divObj.style.width = "400px";
	divObj.style.height = "180px";

	if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
}

function ss_hideFolderAddAttachmentDropbox${ssFolder.id}<portlet:namespace/>() {
	var divId = 'ss_div_folder_dropbox${ssFolder.id}<portlet:namespace/>';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}
</script>
