<% // Toolbar viewer %>
<%
String ss_portletNamespace = renderResponse.getNamespace();
%>
<c:if test="${empty ss_toolbar_style}">
  <c:set var="ss_toolbar_style" value="ss_toolbar"/>
</c:if>
<script type="text/javascript">
function ss_toolbarPopupUrl(url) {
	var width = ss_getWindowWidth();
	if (width < 600) width=600;
	var height = ss_getWindowHeight();
	if (height < 600) height=600;
	self.window.open(url, "_blank", "resizable=yes,scrollbars=yes,width="+width+",height="+height);
}
</script>
<div class="${ss_toolbar_style}">
<c:set var="delimiter" value=""/>
<c:forEach var="toolbarMenu" items="${ss_toolbar}">
    <span class="${ss_toolbar_style}_item"><c:out value="${delimiter}" /></span>
    <c:if test="${empty toolbarMenu.value.url && empty toolbarMenu.value.urlParams}">

     <ssf:menu title="${toolbarMenu.value.title}" 
       titleId="toolbar_${toolbarMenu.key}" menuClass="${ss_toolbar_style}_menu" >
	  <c:forEach var="toolbarMenuCategory" items="${toolbarMenu.value.categories}">
	    <c:if test="${empty toolbarMenuCategory.key}">
	      <span class="ss_bold"><c:out value="${toolbarMenuCategory.key}" /></span>
	    </c:if>
	    <ul class="ss_dropdownmenu">
	      <c:forEach var="toolbarMenuCategoryItem" items="${toolbarMenuCategory.value}">
	        <c:set var="popup" value="false"/>
	        <c:if test="${toolbarMenuCategoryItem.value.qualifiers.popup}">
	          <c:set var="popup" value="true"/>
	        </c:if>
	        <li class="ss_dropdownmenu">
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
	          <c:if test="${empty toolbarMenuCategoryItem.value.qualifiers.onClick}">
	          	onClick="return(ss_openUrlInPortlet(this.href, ${popup}));">
	          </c:if>
	          <c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.onClick}">
	          	onClick="${toolbarMenuCategoryItem.value.qualifiers.onClick}">
	          </c:if>
	          <span>
	          <c:out value="${toolbarMenuCategoryItem.key}" /></span></a>
	        </li>
	      </c:forEach>
	    </ul>
      </c:forEach>
     </ssf:menu>

    </c:if>
    <c:if test="${!empty toolbarMenu.value.url || !empty toolbarMenu.value.urlParams}">
      <c:set var="popup" value="false"/>
      <c:if test="${toolbarMenu.value.qualifiers.popup}">
        <c:set var="popup" value="true"/>
      </c:if>
	  <c:choose>
	    <c:when test="${!empty toolbarMenu.value.url}">
	      <a 
	        class="${ss_toolbar_style}_item" 
	        href="${toolbarMenu.value.url}"
    	    <c:if test="${empty toolbarMenu.value.qualifiers.onClick}">
    	    	<c:if test="${!empty toolbarMenu.value.qualifiers.popup}">
    	      		onClick="ss_toolbarPopupUrl(this.href);return false;"
    	    	</c:if>
    	    </c:if>
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.onClick}">
    	      	onClick="${toolbarMenu.value.qualifiers.onClick}"
    	    </c:if>
	      ><span class="${ss_toolbar_style}_item"><c:out 
	        value="${toolbarMenu.value.title}" /></span></a>
	    </c:when>
	    <c:when test="${!empty toolbarMenu.value.urlParams}">
	      <a 
	        class="${ss_toolbar_style}_item"
	        href="<ssf:url>
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
	 	  ><span class="${ss_toolbar_style}_item"><c:out 
	 	    value="${toolbarMenu.value.title}" /></span></a>
	    </c:when>
	    <c:otherwise>
	      <a class="${ss_toolbar_style}_item" href=""><span class="${ss_toolbar_style}_item"
	        ><c:out value="${toolbarMenu.value.title}" /></span></a>
	    </c:otherwise>
	  </c:choose>
    </c:if>
  <c:set var="delimiter" value=" | "/>
</c:forEach>
</div>