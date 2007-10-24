<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%-- Footer toolbar --%><%--
--%><%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %><%--
--%><%@ page import="com.sitescape.util.BrowserSniffer" %><%--
--%><%@ page import="com.sitescape.team.context.request.RequestContextHolder" %><%--
--%><%@ page import="com.sitescape.team.util.NLT" %><%--
--%><%
Boolean webdavSupportedFooter = new Boolean(com.sitescape.team.web.util.BinderHelper.isWebdavSupported(request));
%><%--
--%><c:choose><%--
	--%><c:when test="${empty ss_footerToolbarCount}"><%--
		--%><c:set var="ss_footerToolbarCount" value="0" scope="request"/><%--
	--%></c:when><%--
	--%><c:otherwise><%--
		--%><c:set var="ss_footerToolbarCount" value="${ss_footerToolbarCount + 1}" scope="request"/><%--
	--%></c:otherwise><%--
--%></c:choose><%--

--%><ssf:skipLink tag="<%= NLT.get("skip.footer.toolbar") %>" id="footerToolbar_${ss_footerToolbarCount}_${renderResponse.namespace}"><%--

--%><c:set var="isWebdavSupported" value="<%= webdavSupportedFooter %>"/><%--

--%><c:if test="${!empty ssFooterToolbar}"><%--

	--%><ssf:ifLicenseExpired><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.expired.warning"/></span></div></ssf:ifLicenseExpired><%--
	
	--%><ssf:ifLicenseExpired invert="true"><%--
	
		--%><ssf:ifLicenseExpired inThisManyDays="30"><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.expiring.soon.warning"/></span></div></ssf:ifLicenseExpired><%--
		--%><ssf:ifLicenseExpired inThisManyDays="30" invert="true"><%--
			--%><ssf:ifLicenseOutOfCompliance><div class="ss_warning ss_license_warning"><span class="ss_warning ss_license_warning"><ssf:nlt tag="license.out.of.compliance"/></span></div></ssf:ifLicenseOutOfCompliance><%--
		--%></ssf:ifLicenseExpired><%--
		
	--%></ssf:ifLicenseExpired><%--

	--%><div align="center" class="ss_footer_toolbar"><%--
		--%><ssHelpSpot helpId="workspaces_folders/misc_tools/footer_toolbar" offsetX="-13" offsetY="-15" <%--
			--%> title="<ssf:nlt tag="helpSpot.bottomLinks"/>"></ssHelpSpot><%--

		--%><c:set var="delimiter" value=""/><%--
		
		--%><c:forEach var="toolbarMenu" items="${ssFooterToolbar}"><%--
		
			--%><c:set var="popup" value="false"/><%--
			--%><c:if test="${toolbarMenu.value.qualifiers.popup}"><%--
				--%><c:set var="popup" value="true"/><%--
			--%></c:if><%--

			--%><c:choose><%--
			
				--%><c:when test="${!empty toolbarMenu.value.url}"><%--
				
					--%><c:if test="${empty toolbarMenu.value.qualifiers.folder || (!empty toolbarMenu.value.qualifiers.folder && isWebdavSupported)}"><%--
						--%><c:out value="${delimiter}" escapeXml="false"/><%--
						--%><div class="ss_bottomlinks"><%--
						
							--%><%-- qualifier 'post' allows to open new page with post method - it sends a form with parameter given as qualifiers.postParams --%><%--
							
							--%><c:if test="${!empty toolbarMenu.value.qualifiers.post}"><%--
	
								--%><form class="inline" action="${toolbarMenu.value.url}" method="post" <c:if test="${!empty toolbarMenu.value.qualifiers.popup}"> target="footerToolbarOptionWnd"</c:if>><%--
	
									--%><c:forEach var="p2" items="${toolbarMenu.value.qualifiers.postParams}"><%--
										--%><c:set var="key2" value="${p2.key}"/><%--
	
										--%><c:forEach var="value2" items="${p2.value}"><%--
											--%><input type="hidden" name="${key2}" value="${value2}"/><%--
										--%></c:forEach><%--
	
									--%></c:forEach><%--
	
									--%><a href="javascript: //" onclick="<c:if test="${!empty toolbarMenu.value.qualifiers.popup}">ss_toolbarPopupUrl('', 'footerToolbarOptionWnd')</c:if>; ss_submitParentForm(this); "><%--
									--%>${toolbarMenu.value.title}</a><%--
								--%></form><%--
	
							--%></c:if><%--
	
							--%><c:if test="${empty toolbarMenu.value.qualifiers.post}"><%--
		
								--%><a href="${toolbarMenu.value.url}" <%--
									--%><c:if test="${empty toolbarMenu.value.qualifiers.onClick}"><%--
										--%><c:if test="${!empty toolbarMenu.value.qualifiers.popup}"><%--
											--%> onClick="ss_toolbarPopupUrl(this.href);return false;" <%--
										--%></c:if><%--
									--%></c:if><%--
									
									--%><c:if test="${!empty toolbarMenu.value.qualifiers.onClick}"><%--
										--%>onClick="${toolbarMenu.value.qualifiers.onClick}" <%--
									--%></c:if><%--
									
									--%><c:if test="${!empty toolbarMenu.value.qualifiers.folder}"><%--
										--%><% if (BrowserSniffer.is_ie(request)) {%><%--
											--%> style="behavior: url(#default#AnchorClick);" <%--
											--%><%--
										--%><% } %><%--
										--%>folder="${toolbarMenu.value.qualifiers.folder}" target="_blank"<%--
									--%></c:if><%--
									--%>><c:out value="${toolbarMenu.value.title}" /></a><%--
							--%></c:if><%--
	
						--%></div><%--
					
						--%><c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;|&nbsp;</span>" /><%--
					--%></c:if><%--
					
				--%></c:when><%--
				
				--%><c:when test="${!empty toolbarMenu.value.urlParams}"><%--
				
					--%><c:if test="${empty toolbarMenu.value.qualifiers.folder || (!empty toolbarMenu.value.qualifiers.folder && isWebdavSupported)}"><%--
						--%><c:out value="${delimiter}" escapeXml="false"/><%--
						--%><div class="ss_bottomlinks"><%--
							--%><a href="<ssf:url><%--
								--%><c:forEach var="p2" items="${toolbarMenu.value.urlParams}"><%--
									--%><c:set var="key2" value="${p2.key}"/><%--
									--%><c:set var="value2" value="${p2.value}"/><%--
									--%><ssf:param name="${key2}" value="${value2}" /><%--
								--%></c:forEach><%--
								--%></ssf:url>" <%--
								--%><c:if test="${empty toolbarMenu.value.qualifiers.onClick}"><%--
									--%><c:if test="${!empty toolbarMenu.value.qualifiers.popup}"><%--
										--%> onClick="ss_toolbarPopupUrl(this.href);return false;" <%--
									--%></c:if><%--
								--%></c:if><%--
								
								--%><c:if test="${!empty toolbarMenu.value.qualifiers.onClick}"><%--
									--%> onClick="${toolbarMenu.value.qualifiers.onClick}" <%--
								--%></c:if><%--
								
								--%><c:if test="${!empty toolbarMenu.value.qualifiers.folder}"><%--
								
									--%><% if (BrowserSniffer.is_ie(request)) { %><%--
										--%> style="behavior: url(#default#AnchorClick);" <%--
									--%><% } %><%--
								
									--%> folder="<c:out value="${toolbarMenu.value.qualifiers.folder}" />" target="_blank" <%--
								--%></c:if><%--
								--%>><c:out value="${toolbarMenu.value.title}" /><%--
							--%></a><%--
						--%></div><%--
						--%><%--
						--%><c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;|&nbsp;</span>" /><%--
					--%></c:if><%--
				--%></c:when><%--
				
				--%><c:otherwise><%--
					--%><c:out value="${delimiter}" escapeXml="false"/><%--
					--%><div class="ss_bottomlinks"><%--
						--%><a href="javascript: //" <%--
							--%><c:if test="${!empty toolbarMenu.value.qualifiers.onClick}"> <%--
								--%> onClick="${toolbarMenu.value.qualifiers.onClick}" <%--
							--%></c:if><%--
							--%>><c:out value="${toolbarMenu.value.title}" /><%--
						--%></a><%--
					--%></div><%--
					--%><c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;|&nbsp;</span>" /><%--
				--%></c:otherwise><%--
				
			--%></c:choose><%--
			
		--%></c:forEach><%--
		
	--%></div><%--

	--%><div id="ss_div_folder_dropbox${ssFolder.id}${renderResponse.namespace}" class="ss_border_light" style="visibility:hidden;display:none;"><%--
		
		--%><div align="right"><%--
			--%><a onClick="ss_hideFolderAddAttachmentDropbox('${renderResponse.namespace}','${ssFolder.id}'); return false;"><img <%--
				--%> <ssf:alt tag="alt.hideThisMenu"/> border="0" src="<html:imagesPath/>icons/close_off.gif"/><%--
			--%></a><%--
		--%></div><%--
	
		--%><iframe frameborder="0" scrolling="no" id="ss_iframe_folder_dropbox${ssFolder.id}${renderResponse.namespace}" name="ss_iframe_folder_dropbox${ssFolder.id}${renderResponse.namespace}" height="80%" width="100%">xxx</iframe><%--
	
	--%></div><%--

--%></c:if><%--

--%></ssf:skipLink><%--

--%><script type="text/javascript">
<c:if test="${!empty ssFooterToolbar.RSS.url}">
	//Add the rss feed info
	var linkEle = document.createElement("link");
	linkEle.setAttribute("rel", "alternate");
	linkEle.setAttribute("type", "application/rss+xml");
	linkEle.setAttribute("title", "RSS feed");
	linkEle.setAttribute("href", "${ssFooterToolbar.RSS.url}");
	document.getElementsByTagName("head")[0].appendChild(linkEle);
</c:if>
	
var iFrameFolderAttachmentInvokedOnce${ssFolder.id}${renderResponse.namespace} = "false"

</script>
<ssf:ifnotadapter><div align="right" style="padding-top:20px;"><a target="_blank" 
  href="http://www.icecore.org"><span class="ss_smallprint ss_light">Powered by ICEcore</span></a></div></ssf:ifnotadapter>
