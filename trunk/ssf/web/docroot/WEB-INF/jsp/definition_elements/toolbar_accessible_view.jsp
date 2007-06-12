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
<% // Toolbar viewer %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
String ss_portletNamespace = renderResponse.getNamespace();

// General variables
Integer nameCount = (Integer) renderRequest.getAttribute("ss_menu_tag_name_count");
if (nameCount == null) {
	nameCount = new Integer(0);
}

nameCount = new Integer(nameCount.intValue() + 1);
renderRequest.setAttribute("ss_menu_tag_name_count", new Integer(nameCount.intValue()));

String menuTagDivId = "ss_menuTagDiv" + nameCount.toString();
String menuDivWidth = "300px";

Boolean webdavSupported = new Boolean(com.sitescape.team.web.util.BinderHelper.isWebdavSupported(request));
%>

<c:choose>
<c:when test="${empty ss_toolbarCount}">
	<c:set var="ss_toolbarCount" value="0" scope="request"/>
</c:when>
<c:otherwise>
	<c:set var="ss_toolbarCount" value="${ss_toolbarCount + 1}" scope="request"/>
</c:otherwise>
</c:choose>

<c:set var="isWebdavSupported" value="<%= webdavSupported %>"/>

<c:if test="${empty ss_toolbar_style}">
  <c:set var="ss_toolbar_style" value="ss_toolbar"/>
</c:if>

<ssf:skipLink tag="<%= NLT.get("skip.toolbar") %>" id="toolbar_${ss_toolbarCount}_${renderResponse.namespace}">

<c:forEach var="toolbarMenu" items="${ss_toolbar}">
    <c:if test="${empty toolbarMenu.value.url && empty toolbarMenu.value.urlParams}">
     <li id="parent_<%= menuTagDivId %><portlet:namespace/>">

     <% // BEGIN Helpspots for folder menus %>
     <c:choose>
	     <c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageFolderMenu'}">
         <ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_folder" offsetY="-3" offsetX="-5" 
		     title="<ssf:nlt tag="helpSpot.manageFolderMenu"/>"/>
         </c:when>
	     <c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageWorkspaceMenu'}">
         <ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_workspace" offsetY="-5" offsetX="-13" 
		     title="<ssf:nlt tag="helpSpot.manageWorkspaceMenu"/>"/>
         </c:when>
	     <c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.modifyProfileButton'}">
         <ssHelpSpot helpId="people/modify_profile" offsetY="-16" offsetX="-20" 
		     title="<ssf:nlt tag="helpSpot.modifyProfileButton"/>"/>
         </c:when>
	     <c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageDashboard'}">
         <ssHelpSpot helpId="workspaces_folders/misc_tools/manage_accessories"
          <c:if test="<%= !isIE %>">
              offsetX="-150" offsetY="-2" 
          </c:if>
          <c:if test="<%= isIE %>">
              offsetX="-15"  offsetY="2" 
          </c:if>
		  title="<ssf:nlt tag="helpSpot.manageDashboard"/>"></ssHelpSpot>
         </c:when>
	     <c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.setWikiHomepage'}">
         <ssHelpSpot helpId="workspaces_folders/entries/set_wiki_homepage" offsetY="-16" offsetX="-20" 
		     title="<ssf:nlt tag="helpSpot.setWikiHomepage"/>"/>
         </c:when>
         <c:otherwise>
	     </c:otherwise>
	 </c:choose>
 
     <c:choose>
     	<c:when test="${empty toolbarMenu.value.qualifiers.disabled}">
	      <a id="toolbar_${toolbarMenu.key}" href="javascript: ;" <ssf:title tag="title.showMenu"/> 
	      onClick="ss_showAccessibleMenu('<%= menuTagDivId %><portlet:namespace/>');">
	      <span>${toolbarMenu.value.title}<c:if test="${!empty toolbarMenu.value.categories && ss_toolbar_style != 'ss_utils_bar'}"
	      > <img border="0" <ssf:alt tag="alt.showMenu"/>
	        src="<html:imagesPath/>pics/menudown.gif"/></c:if></span></a>
		</c:when>
     	<c:when test="${!empty toolbarMenu.value.qualifiers.disabled}">
		 <span class="ss_toolbar_inactive">&nbsp;&nbsp;&nbsp;&nbsp;${toolbarMenu.value.title}&nbsp;&nbsp;&nbsp;&nbsp;</span>
		</c:when>
        <c:otherwise>
        </c:otherwise>
	 </c:choose>

      <div id="<%= menuTagDivId %><portlet:namespace/>" style="visibility:hidden;display:none;white-space:nowrap;">
      
	  <c:forEach var="toolbarMenuCategory" items="${toolbarMenu.value.categories}">
	    <c:if test="${empty toolbarMenuCategory.key}">
	      <span class="ss_bold"><c:out value="${toolbarMenuCategory.key}" /></span>
	    </c:if>
	      <c:forEach var="toolbarMenuCategoryItem" items="${toolbarMenuCategory.value}">
	        <c:set var="popup" value="false"/>
	        <c:set var="popupWidth" value=""/>
	        <c:set var="popupHeight" value=""/>
	        <c:if test="${toolbarMenuCategoryItem.value.qualifiers.popup}">
	          <c:set var="popup" value="true"/>
	          <c:set var="popupWidth" value="${toolbarMenuCategoryItem.value.qualifiers.popupWidth}"/>
	          <c:set var="popupHeight" value="${toolbarMenuCategoryItem.value.qualifiers.popupHeight}"/>
	        </c:if>
	        <c:if test="${empty toolbarMenuCategoryItem.value.qualifiers.folder || (!empty toolbarMenuCategoryItem.value.qualifiers.folder && isWebdavSupported)}">
	        
	          <c:choose>
	            <c:when test="${!empty toolbarMenuCategoryItem.value.url}">
	        	  <a 
	        	  href="<c:out value="${toolbarMenuCategoryItem.value.url}"/>"
	            </c:when>
	            <c:when test="${!empty toolbarMenuCategoryItem.value.urlParams}">
	            <a 
	              href="<ssf:url>
	              <c:forEach var="p" items="${toolbarMenuCategoryItem.value.urlParams}">
				    <c:set var="key" value="${p.key}"/>
				    <c:set var="value" value="${p.value}"/>
	                <ssf:param name="${key}" value="${value}" />
	              </c:forEach>
	 	          </ssf:url>"
	 	        </c:when>
	            <c:otherwise>
	            </c:otherwise>
	   		  </c:choose>
	    	  <c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.folder}">
<%
		if (BrowserSniffer.is_ie(request)) {
%>
    	      	  style="behavior: url(#default#AnchorClick);"
<%
		}
%>
    	      	  folder="${toolbarMenuCategoryItem.value.qualifiers.folder}"
    	      	  target="_blank"
    	      </c:if>
	          <c:if test="${empty toolbarMenuCategoryItem.value.qualifiers.onClick}">
	          	onClick="return(ss_openUrlInPortlet(this.href, ${popup}, '${popupWidth}', '${popupHeight}'));">
	          </c:if>
	          <c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.onClick}">
	          	onClick="${toolbarMenuCategoryItem.value.qualifiers.onClick}">
	          </c:if>
	          <span
	          <c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.textId}">
	            id="${toolbarMenuCategoryItem.value.qualifiers.textId}"
	          </c:if>
	      	  <c:if test="${toolbarMenuCategoryItem.value.qualifiers.selected}">
	            class="ss_bold"
	          </c:if>
	          ><c:out value="${toolbarMenuCategoryItem.key}" /></span></a>
	        
	        </c:if>
	      </c:forEach>
       </c:forEach>
       
       <div align="center" style="margin:10px 0px 0px 0px;">
	      <a id="toolbar_${toolbarMenu.key}" href="javascript: ;" <ssf:title tag="title.closeMenu"/> 
	      onClick="ss_hideAccessibleMenu('<%= menuTagDivId %><portlet:namespace/>');">
	      <span><ssf:nlt tag="button.close"/></span></a>
       </div>

      </div>

<%
	nameCount = new Integer(nameCount.intValue() + 1);
	renderRequest.setAttribute("ss_menu_tag_name_count", new Integer(nameCount.intValue()));
	menuTagDivId = "ss_menuTagDiv" + nameCount.toString();
%>
     </li>

    </c:if>
    <c:if test="${!empty toolbarMenu.value.url || !empty toolbarMenu.value.urlParams}">
      <c:set var="popup" value="false"/>
	  <c:set var="popupWidth" value=""/>
	  <c:set var="popupHeight" value=""/>
      <c:if test="${toolbarMenu.value.qualifiers.popup}">
        <c:set var="popup" value="true"/>
	    <c:set var="popupWidth" value="${toolbarMenu.value.qualifiers.popupWidth}"/>
	    <c:set var="popupHeight" value="${toolbarMenu.value.qualifiers.popupHeight}"/>
      </c:if>

     <% // BEGIN Helpspots for folder menus %> 
     <c:choose>
	     <c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageFolderMenu'}">
         <ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_folder" offsetY="-3" offsetX="-5" 
		     title="<ssf:nlt tag="helpSpot.manageFolderMenu"/>"/>
         </c:when>
	     <c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageWorkspaceMenu'}">
         <ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_workspace" offsetY="-5" offsetX="-13" 
		     title="<ssf:nlt tag="helpSpot.manageWorkspaceMenu"/>"/>
         </c:when>
	     <c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.modifyProfileButton'}">
         <ssHelpSpot helpId="people/modify_profile" 
          <c:if test="<%= !isIE %>">
              offsetY="0" offsetX="-160" 
          </c:if>
          <c:if test="<%= isIE %>">
              offsetY="-4" offsetX="79" 
          </c:if>
		  title="<ssf:nlt tag="helpSpot.modifyProfileButton"/>"></ssHelpSpot>
         </c:when>
	     <c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageDashboard'}">
         <ssHelpSpot helpId="workspaces_folders/misc_tools/manage_accessories" offsetY="-4" offsetX="-20" 
		     title="<ssf:nlt tag="helpSpot.manageDashboard"/>"/>
         </c:when>
	     <c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.setWikiHomepage'}">
         <ssHelpSpot helpId="workspaces_folders/entries/set_wiki_homepage" offsetY="-16" offsetX="-20" 
		     title="<ssf:nlt tag="helpSpot.setWikiHomepage"/>"/>
         </c:when>
         <c:otherwise>
	     </c:otherwise>
	 </c:choose>
 
	  <c:choose>
	  
	  
	  
	    <c:when test="${!empty toolbarMenu.value.url}">
	      <c:if test="${empty toolbarMenu.value.qualifiers.folder || (!empty toolbarMenu.value.qualifiers.folder && isWebdavSupported)}">
	      
	      
	      
	      <li><a href="${toolbarMenu.value.url}"
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
    	    <c:if test="${empty toolbarMenu.value.qualifiers.onClick}">
    	    	<c:if test="${!empty toolbarMenu.value.qualifiers.popup}">
	                <c:set var="popupWidth" value="${toolbarMenu.value.qualifiers.popupWidth}"/>
	                <c:set var="popupHeight" value="${toolbarMenu.value.qualifiers.popupHeight}"/>
    	      		onClick="ss_toolbarPopupUrl(this.href, '_blank', '${popupWidth}', '${popupHeight}');return false;"
    	    	</c:if>
    	    </c:if>
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.onClick}">
    	      	onClick="${toolbarMenu.value.qualifiers.onClick}"
    	    </c:if>
	      ><span
	      <c:if test="${!empty toolbarMenu.value.qualifiers.textId}">
	        id="${toolbarMenu.value.qualifiers.textId}"
	      </c:if>
	      <c:if test="${toolbarMenu.value.qualifiers.selected}">
	        class="ss_bold"
	      </c:if>
	      >
	      <c:if test="${!empty toolbarMenu.value.qualifiers.icon}">
	      	<img border="0" 
	      	src="<html:imagesPath/>icons/<c:out value="${toolbarMenu.value.qualifiers.icon}" />" 
	      	alt="<c:out value="${toolbarMenu.value.title}" />" >
	      </c:if>
	      <c:if test="${empty toolbarMenu.value.qualifiers.icon}">
		      	<c:out value="${toolbarMenu.value.title}" />
		  </c:if>
	      </span></a></li>
	      
	      
	      
	      
	      </c:if>
	    </c:when>
	    
	    
	    
	    
	    <c:when test="${!empty toolbarMenu.value.urlParams}">
	      <c:if test="${empty toolbarMenu.value.qualifiers.folder || (!empty toolbarMenu.value.qualifiers.folder && isWebdavSupported)}">
	      <li><a href="<ssf:url>
	        <c:forEach var="p2" items="${toolbarMenu.value.urlParams}">
			  <c:set var="key2" value="${p2.key}"/>
		      <c:set var="value2" value="${p2.value}"/>
	          <ssf:param name="${key2}" value="${value2}" />
	        </c:forEach>
	 	    </ssf:url>"
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
    	    <c:if test="${empty toolbarMenu.value.qualifiers.onClick}">
    	    	<c:if test="${!empty toolbarMenu.value.qualifiers.popup}">
	                <c:set var="popupWidth" value="${toolbarMenu.value.qualifiers.popupWidth}"/>
	                <c:set var="popupHeight" value="${toolbarMenu.value.qualifiers.popupHeight}"/>
    	      		onClick="ss_toolbarPopupUrl(this.href, '_blank', '${popupWidth}', '${popupHeight}');return false;"
    	    	</c:if>
    	    </c:if>
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.onClick}">
    	      	onClick="${toolbarMenu.value.qualifiers.onClick}"
    	    </c:if>
	 	  ><span
	      <c:if test="${!empty toolbarMenu.value.qualifiers.textId}">
	        id="${toolbarMenu.value.qualifiers.textId}"
	      </c:if>
	      <c:if test="${toolbarMenu.value.qualifiers.selected}">
	        class="ss_bold"
	      </c:if>
	 	  ><c:out 
	 	    value="${toolbarMenu.value.title}" /></span></a></li>
	 	  </c:if>
	    </c:when>
	    <c:otherwise>
	      <li><a href=""><span
	      <c:if test="${!empty toolbarMenu.value.qualifiers.textId}">
	        id="${toolbarMenu.value.qualifiers.textId}"
	      </c:if>
	      <c:if test="${toolbarMenu.value.qualifiers.selected}">
	        class="ss_bold"
	      </c:if>
	      ><c:out value="${toolbarMenu.value.title}" /></span></a></li>
	    </c:otherwise>
	  </c:choose>
    </c:if>
</c:forEach>

</ssf:skipLink>