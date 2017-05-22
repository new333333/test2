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
<%-- Toolbar viewer --%><%-- 
--%><%@ page import="org.kablink.util.BrowserSniffer" %><%--
--%><%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%
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

Boolean webdavSupported = new Boolean(org.kablink.teaming.web.util.BinderHelper.isWebdavSupported(request));
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

--%><ssf:skipLink tag='<%= NLT.get("skip.toolbar") %>' id="toolbar_${ss_toolbarCount}_${renderResponse.namespace}"><%--

--%><c:forEach var="toolbarMenu" items="${ss_toolbar}"><%--
    --%><c:if test="${empty toolbarMenu.value.url && empty toolbarMenu.value.urlParams}"><%--
        --%><c:if test="${!empty toolbarMenu.value.qualifiers.highlight}"><%--
			--%><li class="ss_navbar_new" <%--
        --%></c:if><%--
        --%><c:if test="${empty toolbarMenu.value.qualifiers.highlight}"><%--
			--%><li <%--
        --%></c:if><%--
        --%>id="parent_<%= menuTagDivId %>${renderResponse.namespace}"><%--

--%><%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view_helpspots.jsp" %><%--

        --%><c:choose><%--

            --%><c:when test="${empty toolbarMenu.value.qualifiers.disabled}"><%--
                --%><a id="toolbar_${toolbarMenu.key}" href="javascript: ;" <%--
                --%><c:if test="${!empty toolbarMenu.value.qualifiers.linkclass}"><%--
                	--%> class="${toolbarMenu.value.qualifiers.linkclass}"<%--
                --%></c:if><%--                
                --%><c:if test="${!empty toolbarMenu.value.qualifiers.title}"><%--
                	--%> title="${toolbarMenu.value.qualifiers.title}"<%--
                --%></c:if><%--                
                --%> onclick="${spin} ss_activateMenuLayerClone('<%= menuTagDivId %>${renderResponse.namespace}', 'parent_<%= menuTagDivId %>${renderResponse.namespace}');"> <%--

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
                    --%> <ssf:alt tag="alt.showMenu"/> align="absmiddle" src="<html:imagesPath/>pics/menu_sm.png"/><%--
                --%></c:if><%--
                --%></span></a><%--
            --%></c:when><%--
            
            --%><c:when test="${!empty toolbarMenu.value.qualifiers.disabled}"><%--
                --%><span class="ss_toolbar_inactive">${toolbarMenu.value.title}</span><%--
            --%></c:when><%--

        --%></c:choose><%--

        --%><div id="<%= menuTagDivId %>${renderResponse.namespace}" <%--
        --%> class="${ss_toolbar_style}_submenu" style=""><%--
        --%><ul class="${ss_toolbar_style}_submenu"><%--
        --%><c:set var="toolbarCategoryItemSeen" value="false"/><%--
        --%><c:forEach var="toolbarMenuCategory" items="${toolbarMenu.value.categories}"><%--
            --%><c:if test="${toolbarCategoryItemSeen && !empty toolbarMenuCategory.value}"><%--
            --%><li><span class="ss_dropdownmenu_spacer">------------------------------</span></li><%--
            --%></c:if><%--
        
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
                        --%> onclick="${spin} return ss_openUrlInPortlet(this.href, ${popup}, '${popupWidth}', '${popupHeight}');"><%--
                   --%></c:if><%--

                    --%><c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.onClick}"><%--
                       --%> onclick="${spin} ${toolbarMenuCategoryItem.value.qualifiers.onClick}"><%--
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
                    --%><c:set var="toolbarCategoryItemSeen" value="true"/><%--
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
       --%><c:choose><%--
        
            --%><c:when test="${!empty toolbarMenu.value.url}"><%--
                --%><c:if test="${empty toolbarMenu.value.qualifiers.folder || (!empty toolbarMenu.value.qualifiers.folder && isWebdavSupported)}"><%--
	                --%><c:if test="${!empty toolbarMenu.value.qualifiers.highlight}"><%--
						--%><li class="ss_navbar_inline"<%--
	                --%></c:if><%--
	                --%><c:if test="${empty toolbarMenu.value.qualifiers.highlight}"><%--
						--%><li<%--
	                --%></c:if><%--
	                	--%><c:if test="${!empty toolbarMenu.value.qualifiers.icon && !empty toolbarMenu.value.qualifiers.iconFloatRight}"><%--
	                		--%> class="ss_toolbar_iconfloatright" <%--
		                --%></c:if><%--
	                	--%><c:if test="${!empty toolbarMenu.value.qualifiers.icon && !empty toolbarMenu.value.qualifiers.iconGwtUI}"><%--
	                		--%> class="ss_toolbar_gwtui" <%--
		                --%></c:if><%--
	                --%>><%--

--%><%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view_helpspots.jsp" %><%--

                    --%><a href="${toolbarMenu.value.url}"<%--
                	--%><c:if test="${!empty toolbarMenu.value.qualifiers.linkclass}"><%--
                		--%> class="${toolbarMenu.value.qualifiers.linkclass}"<%--
                	--%></c:if><%--                
	                --%><c:if test="${!empty toolbarMenu.value.qualifiers.title}"><%--
	                	--%> title="${toolbarMenu.value.qualifiers.title}"<%--
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
                    --%><c:if test="${!empty spin and empty toolbarMenu.value.qualifiers.onClick}"><%--
                        --%> onclick="${spin}"<%--
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
                            --%> onclick="ss_toolbarPopupUrl(this.href, '_blank', '${popupWidth}', '${popupHeight}');return false;"<%--
                         --%></c:if><%--
                         --%><c:if test="${!empty spin and empty toolbarMenu.value.qualifiers.popup}"><%--
                             --%> onclick="${spin}"<%--
                         --%></c:if><%--
                    --%></c:if><%--
                    --%><c:if test="${!empty toolbarMenu.value.qualifiers.onClick}"><%--
                        --%> onclick="${spin} ${toolbarMenu.value.qualifiers.onClick}"<%--
                    --%></c:if><%--
                    --%>><%--
                    --%><span<%--
                    --%><c:if test="${!empty toolbarMenu.value.qualifiers.textId}"><%--
                        --%> id="${toolbarMenu.value.qualifiers.textId}"<%--
                    --%></c:if><%--
                    --%><c:if test="${toolbarMenu.value.qualifiers.selected}"><%--
                        --%> class="ss_bold ss_selected"<%--
                    --%></c:if><%--
                	--%><c:if test="${!empty toolbarMenu.value.qualifiers.icon && !empty toolbarMenu.value.qualifiers.iconFloatRight}"><%--
                		--%> class="ss_toolbar_iconfloatright" <%--
	                --%></c:if><%--
                	--%><c:if test="${!empty toolbarMenu.value.qualifiers.icon && !empty toolbarMenu.value.qualifiers.iconGwtUI}"><%--
                		--%> class="ss_toolbar_gwtui" <%--
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
                	--%><c:if test="${!empty toolbarMenu.value.qualifiers.linkclass}"><%--
                		--%> class="${toolbarMenu.value.qualifiers.linkclass}"<%--
                	--%></c:if><%--                
	                --%><c:if test="${!empty toolbarMenu.value.qualifiers.title}"><%--
	                	--%> title="${toolbarMenu.value.qualifiers.title}"<%--
	                --%></c:if><%--                
                   --%><c:if test="${empty toolbarMenu.value.qualifiers.onClick}"><%--
                       --%><c:if test="${!empty toolbarMenu.value.qualifiers.popup}"><%--
                           --%><c:set var="popupWidth" value="${toolbarMenu.value.qualifiers.popupWidth}"/><%--
                           --%><c:set var="popupHeight" value="${toolbarMenu.value.qualifiers.popupHeight}"/><%--
                           --%> onclick="ss_toolbarPopupUrl(this.href, '_blank', '${popupWidth}', '${popupHeight}');return false;"<%--
                       --%></c:if><%--
                   --%></c:if><%--
                   --%><c:if test="${!empty toolbarMenu.value.qualifiers.onClick}"><%--
                       --%> onclick="${spin} ${toolbarMenu.value.qualifiers.onClick}"<%--
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