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
<%-- Toolbar viewer --%><%-- 
--%><%@ page import="com.sitescape.util.BrowserSniffer" %><%--
--%><%@ page import="com.sitescape.team.util.NLT" %><%--
--%><%
boolean isIE = BrowserSniffer.is_ie(request);
String ss_portletNamespace = renderResponse.getNamespace();

// General variables
Integer nameCount = (Integer) renderRequest.getAttribute("ss_menu_tag_name_count");
if (nameCount == null) {
	nameCount = new Integer(new Long(Math.round(Math.random()*999999)).toString());
}

nameCount = new Integer(nameCount.intValue() + 1);
renderRequest.setAttribute("ss_menu_tag_name_count", new Integer(nameCount.intValue()));

String menuTagDivId = "ss_menuTagDiv" + nameCount.toString();
String menuDivWidth = "300px";

Boolean webdavSupported = new Boolean(com.sitescape.team.web.util.BinderHelper.isWebdavSupported(request));
%><%--
--%><c:choose><%--
    --%><c:when test="${empty ss_toolbarCount}"><%--
        --%><c:set var="ss_toolbarCount" value="0" scope="request"/><%--
    --%></c:when><%--
    --%><c:otherwise><%--
        --%><c:set var="ss_toolbarCount" value="${ss_toolbarCount + 1}" scope="request"/><%--
    --%></c:otherwise><%--
--%></c:choose><%--
--%><c:set var="isWebdavSupported" value="<%= webdavSupported %>"/><%--

--%><c:if test="${empty ss_toolbar_style}"><%--
    --%><c:set var="ss_toolbar_style" value="ss_toolbar"/><%--
--%></c:if><%--

--%><ssf:skipLink tag="<%= NLT.get("skip.toolbar") %>" id="toolbar_${ss_toolbarCount}_${renderResponse.namespace}"><%--

--%><c:forEach var="toolbarMenu" items="${ss_toolbar}"><%--
    --%><c:if test="${empty toolbarMenu.value.url && empty toolbarMenu.value.urlParams}"><%--
        --%><c:if test="${!empty toolbarMenu.value.qualifiers.highlight}"><%--
			--%><li class="ss_navbar_new" <%--
        --%></c:if><%--
        --%><c:if test="${empty toolbarMenu.value.qualifiers.highlight}"><%--
			--%><li <%--
        --%></c:if><%--
        --%>id="parent_<%= menuTagDivId %>${renderResponse.namespace}"><%--
        
        --%><%-- BEGIN Helpspots for folder menus --%><%--
        
        --%><c:choose><%--
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageFolderMenu'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_folder" offsetY="-5" offsetX="-5"<%--
                --%> title="<ssf:nlt tag="helpSpot.manageFolderMenu"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--
        
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageWorkspaceMenu'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_workspace" offsetY="-5" offsetX="-20" <%--
                --%> title="<ssf:nlt tag="helpSpot.manageWorkspaceMenu"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.whatsNew'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/whats_new" offsetY="-5" offsetX="-20" <%--
                --%> title="<ssf:nlt tag="helpSpot.whatsNew"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.whatsUnread'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/whats_unread" offsetY="-5" offsetX="-20" <%--
                --%> title="<ssf:nlt tag="helpSpot.whatsUnread"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.modifyProfileButton'}"><%--
                --%><ssHelpSpot helpId="people/modify_profile" offsetY="-3" offsetX="-8" <%--
                --%> title="<ssf:nlt tag="helpSpot.modifyProfileButton"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageSubscriptionsMenu'}"><%--
                --%><ssHelpSpot helpId="/workspaces_folders/menus_toolbars/subscriptions" offsetY="-5" offsetX="-10" <%--
                --%> title="<ssf:nlt tag="helpSpot.subscriptionsMenu"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageDashboard'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/misc_tools/manage_accessories"<%--
                --%><c:if test="<%= !isIE %>"><%--
                    --%> offsetX="-36"<%--
                --%></c:if><%--
                --%><c:if test="<%= isIE %>"><%--
                    --%> offsetX="-15"<%--
                --%></c:if><%--
                --%> offsetY="-2" xAlignment="left" title="<ssf:nlt tag="helpSpot.manageDashboard"/>"></ssHelpSpot><%--
            --%></c:when><%--

        --%></c:choose><%--
 
        --%><c:choose><%--

            --%><c:when test="${empty toolbarMenu.value.qualifiers.disabled}"><%--
                --%><a id="toolbar_${toolbarMenu.key}" href="javascript: ;" <%--
                --%><c:if test="${!empty toolbarMenu.value.qualifiers.linkclass}"><%--
                	--%> class="${toolbarMenu.value.qualifiers.linkclass}"<%--
                --%></c:if><%--                
                --%> onClick="${spin} ss_activateMenuLayerClone('<%= menuTagDivId %>${renderResponse.namespace}', 'parent_<%= menuTagDivId %>${renderResponse.namespace}');"><%--

                --%><c:if test="${!empty toolbarMenu.value.qualifiers.icon}"><%--
                    --%><img border="0" <%--
                    --%> src="<html:imagesPath/>icons/${toolbarMenu.value.qualifiers.icon}" <%--
                    --%> title="${toolbarMenu.value.title}" ><%--
                --%></c:if><%--

                --%><c:if test="${!empty toolbarMenu.value.qualifiers.iconclass}"><%--
                    --%><img border="0" <%--
                    --%> src="<html:imagesPath/>pics/1pix.gif" class="${toolbarMenu.value.qualifiers.iconclass}" <%--
                    --%> title="${toolbarMenu.value.title}" ><%--
                --%></c:if><%--

                --%><c:if test="${empty toolbarMenu.value.qualifiers.icon && empty toolbarMenu.value.qualifiers.iconclass}"><%--
                    --%><span>${toolbarMenu.value.title}<%--
                --%></c:if><%--

                --%><c:if test="${!empty toolbarMenu.value.categories}"><%--
                    --%><img border="0" style="padding-left: 2px;" title="<ssf:nlt tag="alt.showMenu"/>"<%--
                    --%> src="<html:imagesPath/>pics/menudown.gif"/><%--
                --%></c:if><%--
                --%></span></a><%--
            --%></c:when><%--
            
            --%><c:when test="${!empty toolbarMenu.value.qualifiers.disabled}"><%--
                --%><span class="ss_toolbar_inactive">&nbsp;&nbsp;&nbsp;&nbsp;${toolbarMenu.value.title}&nbsp;&nbsp;&nbsp;&nbsp;</span><%--
            --%></c:when><%--

        --%></c:choose><%--

        --%><div id="<%= menuTagDivId %>${renderResponse.namespace}" <%--
        --%> class="${ss_toolbar_style}_submenu" style=""><%--
        --%><ul class="${ss_toolbar_style}_submenu"><%--
        --%><c:forEach var="toolbarMenuCategory" items="${toolbarMenu.value.categories}"><%--
        
            --%><c:forEach var="toolbarMenuCategoryItem" items="${toolbarMenuCategory.value}"><%--

                --%><c:set var="popup" value="false"/><%--
                --%><c:set var="popupWidth" value=""/><%--
                --%><c:set var="popupHeight" value=""/><%--

                --%><c:if test="${toolbarMenuCategoryItem.value.qualifiers.popup}"><%--
                    --%><c:set var="popup" value="true"/><%--
                    --%><c:set var="popupWidth" value="${toolbarMenuCategoryItem.value.qualifiers.popupWidth}"/><%--
                    --%><c:set var="popupHeight" value="${toolbarMenuCategoryItem.value.qualifiers.popupHeight}"/><%--
                --%></c:if><%--

                --%><c:set var="spin" value=""/><%--

                --%><c:if test="${toolbarMenuCategoryItem.value.qualifiers.showSpinner}"><%--
                    --%><c:set var="spin" value="ss_startSpinner();"/><%--
                --%></c:if><%--

                --%><c:if test="${empty toolbarMenuCategoryItem.value.qualifiers.folder || (!empty toolbarMenuCategoryItem.value.qualifiers.folder && isWebdavSupported)}"><%--
                    --%><li><%--
                    --%><c:choose><%--
                        --%><c:when test="${!empty toolbarMenuCategoryItem.value.url}"><%--
                            --%><a href="<c:out value="${toolbarMenuCategoryItem.value.url}"/>"<%--
                        --%></c:when><%--
                        --%><c:when test="${!empty toolbarMenuCategoryItem.value.urlParams}"><%--
                            --%><a href="<%--
                            --%><ssf:url><%--
                            --%><c:forEach var="p" items="${toolbarMenuCategoryItem.value.urlParams}"><%--
                                --%><c:set var="key" value="${p.key}"/><%--
                                --%><c:set var="value" value="${p.value}"/><%--
                                --%><ssf:param name="${key}" value="${value}" /><%--
                            --%></c:forEach><%--
                            --%></ssf:url>"<%--
                        --%></c:when><%--
                    --%></c:choose><%--
                    --%><c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.folder}"><%--
                        --%><%
	                      	if (BrowserSniffer.is_ie(request)) {
    	                    %><%--
        	                --%> style="behavior: url(#default#AnchorClick);"<%--
	                    --%><%
    	               		}
	                        %><%--
                        --%> folder="${toolbarMenuCategoryItem.value.qualifiers.folder}"<%--
                        --%> target="_blank"<%--
                    --%></c:if><%--

                    --%><c:if test="${empty toolbarMenuCategoryItem.value.qualifiers.onClick}"><%--
                        --%> onClick="${spin} return(ss_openUrlInPortlet(this.href, ${popup}, '${popupWidth}', '${popupHeight}'));"><%--
                   --%></c:if><%--

                    --%><c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.onClick}"><%--
                       --%> onClick="${spin} ${toolbarMenuCategoryItem.value.qualifiers.onClick}"><%--
                    --%></c:if><%--
                
                    --%><span<%--
                    
                    --%><c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.textId}"><%--
                        --%> id="${toolbarMenuCategoryItem.value.qualifiers.textId}"<%--
                    --%></c:if><%--
                    --%><c:if test="${toolbarMenuCategoryItem.value.qualifiers.selected}"><%--
                        --%> class="ss_bold ss_selected"<%--
                    --%></c:if><%--
                    --%>><c:out value="${toolbarMenuCategoryItem.key}" /><%--
                    --%></span><%--
                    --%></a><%--
                    --%></li><%--
                --%></c:if><%--
            --%></c:forEach><%--
        --%></c:forEach><%--
        --%></ul><%--
        --%></div><%--

        --%><%
		 	nameCount = new Integer(nameCount.intValue() + 1);
			renderRequest.setAttribute("ss_menu_tag_name_count", new Integer(nameCount.intValue()));
			menuTagDivId = "ss_menuTagDiv" + nameCount.toString();
	  		%><%--
        --%></li><%--

    --%></c:if><%--

    --%><c:if test="${!empty toolbarMenu.value.url || !empty toolbarMenu.value.urlParams}"><%--

        --%><c:set var="popup" value="false"/><%--
        --%><c:set var="popupWidth" value=""/><%--
        --%><c:set var="popupHeight" value=""/><%--

        --%><c:if test="${toolbarMenu.value.qualifiers.popup}"><%--
            --%><c:set var="popup" value="true"/><%--
            --%><c:set var="popupWidth" value="${toolbarMenu.value.qualifiers.popupWidth}"/><%--
            --%><c:set var="popupHeight" value="${toolbarMenu.value.qualifiers.popupHeight}"/><%--
        --%></c:if><%--

        --%><c:set var="spin" value=""/><%--

        --%><c:if test="${toolbarMenu.value.qualifiers.showSpinner}"><%--
            --%><c:set var="spin" value="ss_startSpinner();"/><%--
        --%></c:if><%--

        --%><%-- BEGIN Helpspots for folder menus --%><%--
        
        --%><c:choose><%--
        
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageFolderMenu'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_folder" offsetY="-5" offsetX="-5" <%--
                --%> title="<ssf:nlt tag="helpSpot.manageFolderMenu"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--
            
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageWorkspaceMenu'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_workspace" offsetY="-5" offsetX="-20" <%--
                --%> title="<ssf:nlt tag="helpSpot.manageWorkspaceMenu"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--
            
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.whatsNew'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/whats_new" offsetY="0" offsetX="0" <%--
                --%> title="<ssf:nlt tag="helpSpot.whatsNew"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.whatsUnread'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/whats_unread" offsetY="0" offsetX="0" <%--
                --%> title="<ssf:nlt tag="helpSpot.whatsUnread"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.modifyProfileButton'}"><%--
                --%><ssHelpSpot helpId="people/modify_profile" <%--
                --%><c:if test="<%= !isIE %>"><%--
                    --%> offsetY="0" offsetX="-160" <%--
                --%></c:if><%--
                --%><c:if test="<%= isIE %>"><%--
                    --%> offsetY="-4" offsetX="79" <%--
                --%></c:if><%--
                --%> title="<ssf:nlt tag="helpSpot.modifyProfileButton"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--
            
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageDashboard'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/misc_tools/manage_accessories"<%--
                --%><c:if test="<%= !isIE %>"><%--
                    --%> offsetX="-36" <%--
                --%></c:if><%--
                --%><c:if test="<%= isIE %>"><%--
                    --%> offsetX="-15"  <%--
                --%></c:if><%--
                --%> offsetY="-2" xAlignment="left" title="<ssf:nlt tag="helpSpot.manageDashboard"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--

        --%></c:choose><%--
 
        --%><c:choose><%--
        
            --%><c:when test="${!empty toolbarMenu.value.url}"><%--
                --%><c:if test="${empty toolbarMenu.value.qualifiers.folder || (!empty toolbarMenu.value.qualifiers.folder && isWebdavSupported)}"><%--
	                --%><c:if test="${!empty toolbarMenu.value.qualifiers.highlight}"><%--
						--%><li class="ss_navbar_inline"><%--
	                --%></c:if><%--
	                --%><c:if test="${empty toolbarMenu.value.qualifiers.highlight}"><%--
						--%><li><%--
	                --%></c:if><%--
                    --%><a href="${toolbarMenu.value.url}"<%--
                    --%><c:if test="${!empty toolbarMenu.value.qualifiers.folder}"><%--
                        --%><%
							if (BrowserSniffer.is_ie(request)) {
							%><%--
					        --%> style="behavior: url(#default#AnchorClick);"<%--
                        --%><%
							}
							%><%--
                        --%> folder="<c:out value="${toolbarMenu.value.qualifiers.folder}" />"<%--
                        --%> target="_blank"<%--
                    --%></c:if><%--
                    --%><c:if test="${!empty spin and empty toolbarMenu.value.qualifiers.onClick}"><%--
                        --%> onClick="${spin}"<%--
                    --%></c:if><%--
                    --%><c:if test="${!empty toolbarMenu.value.qualifiers.folder}"><%--
                        --%><%
							if (BrowserSniffer.is_ie(request)) {
							%><%--
                                --%> style="behavior: url(#default#AnchorClick);"<%--
                        --%><%
							}
							%><%--
                        --%> folder="<c:out value="${toolbarMenu.value.qualifiers.folder}" />"<%--
                        --%> target="_blank"<%--
                    --%></c:if><%--
                    --%><c:if test="${empty toolbarMenu.value.qualifiers.onClick}"><%--
                        --%><c:if test="${!empty toolbarMenu.value.qualifiers.popup}"><%--
                            --%><c:set var="popupWidth" value="${toolbarMenu.value.qualifiers.popupWidth}"/><%--
                            --%><c:set var="popupHeight" value="${toolbarMenu.value.qualifiers.popupHeight}"/><%--
                            --%> onClick="ss_toolbarPopupUrl(this.href, '_blank', '${popupWidth}', '${popupHeight}');return false;"<%--
                         --%></c:if><%--
                         --%><c:if test="${!empty spin and empty toolbarMenu.value.qualifiers.popup}"><%--
                             --%> onClick="${spin}"<%--
                         --%></c:if><%--
                    --%></c:if><%--
                    --%><c:if test="${!empty toolbarMenu.value.qualifiers.onClick}"><%--
                        --%> onClick="${spin} ${toolbarMenu.value.qualifiers.onClick}"<%--
                    --%></c:if><%--
                    --%>><%--
                    --%><span<%--
                    --%><c:if test="${!empty toolbarMenu.value.qualifiers.textId}"><%--
                        --%> id="${toolbarMenu.value.qualifiers.textId}"<%--
                    --%></c:if><%--
                    --%><c:if test="${toolbarMenu.value.qualifiers.selected}"><%--
                        --%> class="ss_bold ss_selected"<%--
                    --%></c:if><%--
                    --%>><%--
                    --%><c:if test="${!empty toolbarMenu.value.qualifiers.icon}"><%--
                        --%><img border="0" <%--
                        --%> src="<html:imagesPath/>icons/<c:out value="${toolbarMenu.value.qualifiers.icon}" />" <%--
                        --%> title="<c:out value="${toolbarMenu.value.title}" />" ><%--
                    --%></c:if><%--
                    --%><c:if test="${empty toolbarMenu.value.qualifiers.icon}"><%--
                        --%><c:out value="${toolbarMenu.value.title}" /><%--
                    --%></c:if><%--
                    --%></span></a><%--
                    --%></li><%--
                --%></c:if><%--
            --%></c:when><%--
            
            --%><c:when test="${!empty toolbarMenu.value.urlParams}"><%--
                --%><c:if test="${empty toolbarMenu.value.qualifiers.folder || (!empty toolbarMenu.value.qualifiers.folder && isWebdavSupported)}"><%--
                   --%><li><%--
                   --%><a href="<ssf:url><%--
                   --%><c:forEach var="p2" items="${toolbarMenu.value.urlParams}"><%--
                       --%><c:set var="key2" value="${p2.key}"/><%--
                       --%><c:set var="value2" value="${p2.value}"/><%--
                       --%><ssf:param name="${key2}" value="${value2}" /><%--
                   --%></c:forEach><%--
                   --%></ssf:url>"<%--
                   --%><c:if test="${!empty toolbarMenu.value.qualifiers.folder}"><%--
                       --%><%
					  		if (BrowserSniffer.is_ie(request)) {
							%><%--
                       --%> style="behavior: url(#default#AnchorClick);"<%--
   	                  --%><%
						}
						%><%--
                       --%> folder="<c:out value="${toolbarMenu.value.qualifiers.folder}" />"<%--
                       --%> target="_blank"<%--
                   --%></c:if><%--
                   --%><c:if test="${empty toolbarMenu.value.qualifiers.onClick}"><%--
                       --%><c:if test="${!empty toolbarMenu.value.qualifiers.popup}"><%--
                           --%><c:set var="popupWidth" value="${toolbarMenu.value.qualifiers.popupWidth}"/><%--
                           --%><c:set var="popupHeight" value="${toolbarMenu.value.qualifiers.popupHeight}"/><%--
                           --%> onClick="ss_toolbarPopupUrl(this.href, '_blank', '${popupWidth}', '${popupHeight}');return false;"<%--
                       --%></c:if><%--
                   --%></c:if><%--
                   --%><c:if test="${!empty toolbarMenu.value.qualifiers.onClick}"><%--
                       --%> onClick="${spin} ${toolbarMenu.value.qualifiers.onClick}"<%--
                   --%></c:if><%--
                   --%>><span<%--
                   --%><c:if test="${!empty toolbarMenu.value.qualifiers.textId}"><%--
                   --%> id="${toolbarMenu.value.qualifiers.textId}"<%--
                   --%></c:if><%--
                   --%><c:if test="${toolbarMenu.value.qualifiers.selected}"><%--
                       --%> class="ss_bold ss_selected"<%--
                    --%></c:if><%--
                    --%>><c:out value="${toolbarMenu.value.title}" /><%--
                    --%></span><%--
                    --%></a><%--
                    --%></li><%--
                --%></c:if><%--
            --%></c:when><%--

            --%><c:otherwise><%--
                --%><li><%--
                --%><a href=""><%--
                --%><span<%--
                --%><c:if test="${!empty toolbarMenu.value.qualifiers.textId}"><%--
                    --%> id="${toolbarMenu.value.qualifiers.textId}"<%--
                --%></c:if><%--
                --%><c:if test="${toolbarMenu.value.qualifiers.selected}"><%--
                --%> class="ss_bold ss_selected"<%--
                --%></c:if><%--
                --%>><c:out value="${toolbarMenu.value.title}" /><%--
                --%></span><%--
                --%></a><%--
                --%></li><%--
            --%></c:otherwise><%--
            
        --%></c:choose><%--
    --%></c:if><%--
--%></c:forEach><%--
--%></ssf:skipLink>