<% // Toolbar viewer %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%
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

%>
<c:if test="${empty ss_toolbar_style}">
  <c:set var="ss_toolbar_style" value="ss_toolbar"/>
</c:if>
<c:forEach var="toolbarMenu" items="${ss_toolbar}">
    <c:if test="${empty toolbarMenu.value.url && empty toolbarMenu.value.urlParams}">
     <li id="parent_<%= menuTagDivId %><portlet:namespace/>">

     <% // BEGIN Helpspots for folder menus %>
     <c:choose>
	     <c:when test="${toolbarMenu.value.title == 'Manage this folder'}">
         <ssHelpSpot helpId="folder_menu/manage_folder_menu" offsetY="5" offsetX="-10"
		     title="<ssf:nlt tag="helpSpot.manageFolderMenu"/>">
         </c:when>
	     <c:when test="${toolbarMenu.value.title == 'Manage dashboard'}">
         <ssHelpSpot helpId="folder_menu/manage_dashboard" offsetY="5" offsetX="-10"
		     title="<ssf:nlt tag="helpSpot.manageDashboard"/>">
         </c:when>
         <c:otherwise>
	     </c:otherwise>
	 </c:choose>
 
     <c:choose>
     	<c:when test="${empty toolbarMenu.value.qualifiers.disabled}">
	      <a id="toolbar_${toolbarMenu.key}" href="javascript: ;" 
	      onClick="ss_activateMenuLayerClone('<%= menuTagDivId %><portlet:namespace/>', 'parent_<%= menuTagDivId %><portlet:namespace/>');">
	      ${toolbarMenu.value.title}</a>
		</c:when>
     	<c:when test="${!empty toolbarMenu.value.qualifiers.disabled}">
		 <span class="ss_toolbar_inactive">&nbsp;&nbsp;&nbsp;&nbsp;${toolbarMenu.value.title}&nbsp;&nbsp;&nbsp;&nbsp;</span>
		</c:when>
        <c:otherwise>
        </c:otherwise>
	 </c:choose>

      <% // END Helpspots for folder menus %>
      <c:if test="${toolbarMenu.value.title == 'Manage this folder' || toolbarMenu.value.title == 'Manage dashboard'}">
      </ssHelpSpot>
      </c:if>

      <div id="<%= menuTagDivId %><portlet:namespace/>" 
        class="${ss_toolbar_style}_submenu" style="width:<%= menuDivWidth %>;">
      <ul class="${ss_toolbar_style}_submenu">
	  <c:forEach var="toolbarMenuCategory" items="${toolbarMenu.value.categories}">
	    <c:if test="${empty toolbarMenuCategory.key}">
	      <span class="ss_bold"><c:out value="${toolbarMenuCategory.key}" /></span>
	    </c:if>
	      <c:forEach var="toolbarMenuCategoryItem" items="${toolbarMenuCategory.value}">
	        <c:set var="popup" value="false"/>
	        <c:if test="${toolbarMenuCategoryItem.value.qualifiers.popup}">
	          <c:set var="popup" value="true"/>
	        </c:if>
	        <li>
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
	          	onClick="return(ss_openUrlInPortlet(this.href, ${popup}));">
	          </c:if>
	          <c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.onClick}">
	          	onClick="${toolbarMenuCategoryItem.value.qualifiers.onClick}">
	          </c:if>
	          <span
	          <c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.textId}">
	            id="${toolbarMenuCategoryItem.value.qualifiers.textId}"
	          </c:if>
	          ><c:out value="${toolbarMenuCategoryItem.key}" /></span></a>
	        </li>
	      </c:forEach>
       </c:forEach>
      </ul>
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
      <c:if test="${toolbarMenu.value.qualifiers.popup}">
        <c:set var="popup" value="true"/>
      </c:if>
	  <c:choose>
	    <c:when test="${!empty toolbarMenu.value.url}">
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
    	      		onClick="ss_toolbarPopupUrl(this.href);return false;"
    	    	</c:if>
    	    </c:if>
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.onClick}">
    	      	onClick="${toolbarMenu.value.qualifiers.onClick}"
    	    </c:if>
	      ><span
	      <c:if test="${!empty toolbarMenu.value.qualifiers.textId}">
	        id="${toolbarMenu.value.qualifiers.textId}"
	      </c:if>
	      ><c:out 
	        value="${toolbarMenu.value.title}" /></span></a></li>
	    </c:when>
	    <c:when test="${!empty toolbarMenu.value.urlParams}">
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
    	      		onClick="ss_toolbarPopupUrl(this.href);return false;"
    	    	</c:if>
    	    </c:if>
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.onClick}">
    	      	onClick="${toolbarMenu.value.qualifiers.onClick}"
    	    </c:if>
	 	  ><span
	      <c:if test="${!empty toolbarMenu.value.qualifiers.textId}">
	        id="${toolbarMenu.value.qualifiers.textId}"
	      </c:if>
	 	  ><c:out 
	 	    value="${toolbarMenu.value.title}" /></span></a></li>
	    </c:when>
	    <c:otherwise>
	      <li><a href=""><span
	      <c:if test="${!empty toolbarMenu.value.qualifiers.textId}">
	        id="${toolbarMenu.value.qualifiers.textId}"
	      </c:if>
	      ><c:out value="${toolbarMenu.value.title}" /></span></a></li>
	    </c:otherwise>
	  </c:choose>
    </c:if>
</c:forEach>
