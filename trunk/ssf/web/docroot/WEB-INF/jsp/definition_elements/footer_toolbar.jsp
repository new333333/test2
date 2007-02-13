<% // Footer toolbar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.context.request.RequestContextHolder" %>
<c:if test="${!empty ssFooterToolbar}">
<div align="center" class="ss_footer_toolbar">
<c:set var="delimiter" value=""/>
<c:forEach var="toolbarMenu" items="${ssFooterToolbar}">
    <c:if test="${!empty toolbarMenu.value.url || !empty toolbarMenu.value.urlParams}">
      <c:out value="${delimiter}" escapeXml="false"/>
      <c:set var="popup" value="false"/>
      <c:if test="${toolbarMenu.value.qualifiers.popup}">
        <c:set var="popup" value="true"/>
      </c:if>
	  <c:choose>
	    <c:when test="${!empty toolbarMenu.value.url}">
	      <div class="ss_bottomlinks"><a href="${toolbarMenu.value.url}"
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
	        value="${toolbarMenu.value.title}" /></a></div>
	    </c:when>
	    <c:when test="${!empty toolbarMenu.value.urlParams}">
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
	    </c:when>
	    <c:otherwise>
	      <div class="ss_bottomlinks"><a href=""><c:out value="${toolbarMenu.value.title}" /></a></div>
	    </c:otherwise>
	  </c:choose>
	  <c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;|&nbsp;</span>" />
    </c:if>
</c:forEach>
</div>

<div id="ss_div_folder_dropbox${ssFolder.id}<portlet:namespace/>" class="ss_border_light" style="visibility:hidden;display:none;">
	<div align="right">
	<a  onClick="ss_hideFolderAddAttachmentDropbox${ssFolder.id}<portlet:namespace />(); return false;"><img 
	  border="0" src="<html:imagesPath/>box/close_off.gif"/></a>
	</div>	
	<iframe frameborder="0" scrolling="no" id="ss_iframe_folder_dropbox${ssFolder.id}<portlet:namespace/>" name="ss_iframe_folder_dropbox${ssFolder.id}<portlet:namespace/>" height="80%" width="100%">xxx</iframe>
</div>

</c:if>

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

	divObj.style.width = "350px";
	divObj.style.height = "125px";

	if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
}

function ss_hideFolderAddAttachmentDropbox${ssFolder.id}<portlet:namespace/>() {
	var divId = 'ss_div_folder_dropbox${ssFolder.id}<portlet:namespace/>';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}
</script>
