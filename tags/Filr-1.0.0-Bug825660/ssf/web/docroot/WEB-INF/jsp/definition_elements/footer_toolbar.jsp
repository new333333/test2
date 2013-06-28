<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%-- Footer toolbar --%><%--
--%><%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %><%--
--%><%@ page import="org.kablink.util.BrowserSniffer" %><%--
--%><%@ page import="org.kablink.teaming.context.request.RequestContextHolder" %><%--
--%><%@ page import="org.kablink.teaming.util.NLT" %><%--
--%><%
Boolean webdavSupportedFooter = new Boolean(org.kablink.teaming.web.util.BinderHelper.isWebdavSupported(request));
%><%--
--%><c:choose><%--
	--%><c:when test="${empty ss_footerToolbarCount}"><%--
		--%><c:set var="ss_footerToolbarCount" value="0" scope="request"/><%--
	--%></c:when><%--
	--%><c:otherwise><%--
		--%><c:set var="ss_footerToolbarCount" value="${ss_footerToolbarCount + 1}" scope="request"/><%--
	--%></c:otherwise><%--
--%></c:choose><%--

--%><ssf:skipLink tag='<%= NLT.get("skip.footer.toolbar") %>' id="footerToolbar_${ss_footerToolbarCount}_${renderResponse.namespace}"><%--

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
		--%><ssHelpSpot helpId="workspaces_folders/misc_tools/footer_toolbar" offsetX="-12" offsetY="-4" <%--
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
	
									--%><a href="javascript: //" onclick="<c:if test="${!empty toolbarMenu.value.qualifiers.popup}"
									  >ss_toolbarPopupUrl('', 'footerToolbarOptionWnd')</c:if>; ss_submitParentForm(this);return false; "><%--
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
					
						--%><c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;&nbsp;|&nbsp;&nbsp;</span>" /><%--
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
						--%><c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;&nbsp;|&nbsp;&nbsp;</span>" /><%--
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
					--%><c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;&nbsp;|&nbsp;&nbsp;</span>" /><%--
				--%></c:otherwise><%--
				
			--%></c:choose><%--
			
		--%></c:forEach><%--
		
	--%></div><%--

--%></c:if><%--
--%><div class="ss_clear"></div><%--

--%></ssf:skipLink><%--

--%><script type="text/javascript">
<c:if test="${!empty ssFooterToolbar.RSS.url}">
	//Add the rss feed info
	if (self.document.getElementById("ss_rssLink") == null) {
		var ss_linkEle = document.createElement("link");
		ss_linkEle.setAttribute("id", "ss_rssLink");
		ss_linkEle.setAttribute("rel", "alternate");
		ss_linkEle.setAttribute("type", "application/rss+xml");
		ss_linkEle.setAttribute("title", "RSS<c:if test="${!empty ssBinder.title}"> - ${ssBinder.title}</c:if>");
		ss_linkEle.setAttribute("href", "${ssFooterToolbar.RSS.url}");
		document.getElementsByTagName("head")[0].appendChild(ss_linkEle);
	}
</c:if>
	
var iFrameFolderAttachmentInvokedOnce${ssFolder.id}${renderResponse.namespace} = "false"

</script>
<div id="ss_permalink_display_div" class="ss_style" style="display:none;">
  <div style="text-align: right;">
	<a <ssf:alt tag="alt.hidePermalinks"/>
   	  onClick="ss_hideDivNone('ss_permalink_display_div'); return false;"><img 
  	   border="0" src="<html:imagesPath/>icons/close_gray16.png"/>
  	</a>
  </div>
<%@ include file="/WEB-INF/jsp/forum/permalinks.jsp" %>
</div>
