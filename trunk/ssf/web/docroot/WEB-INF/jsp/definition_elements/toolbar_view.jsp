<% // Toolbar viewer %>
<script language="javascript">
function ss_toolbarPopupUrl(url) {
	var width = getWindowWidth();
	if (width < 600) width=600;
	var height = getWindowHeight();
	if (height < 600) height=600;
	self.window.open(url, "_blank", "resizable=yes,scrollbars=yes,width="+width+",height="+height);
}
</script>
<jsp:useBean id="toolbar" type="java.util.SortedMap" scope="request" />
<c:forEach var="toolbarMenu" items="${toolbar}">
  <c:if test="${empty toolbarMenu.value.url}">
	<div class="ss_toolbar_menu" width="100%" id="toolbar_<c:out value="${toolbarMenu.key}" />">
	  <c:forEach var="toolbarMenuCategory" items="${toolbarMenu.value.categories}">
	    <c:if test="${empty toolbarMenuCategory.key}">
	      <span class="portlet-font"><b><c:out value="${toolbarMenuCategory.key}" /></b></span>
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
			        <jsp:useBean id="key" type="java.lang.String" />
			        <jsp:useBean id="value" type="java.lang.String" />
	                <ssf:param name="<%= key %>" value="<%= value %>" />
	              </c:forEach>
	 	          </ssf:url>"
	 	        </c:when>
	            <c:otherwise>
	            </c:otherwise>
	   		  </c:choose>
	          onClick="return(ss_openUrlInPortlet(this.href, ${popup}));">
	          <span class="portlet-font" 
	          style="font-size: smaller; text-decoration: none;">
	          <c:out value="${toolbarMenuCategoryItem.key}" /></span></a>
	        </li>
	      </c:forEach>
	    </ul>
      </c:forEach>
	</div>
   </c:if>
 </c:forEach>

<div class="portlet-section-header" style="margin-top: 8px; margin-bottom: 8px; 
  margin-left: 0px; margin-right: 0px; width:100%; display:block;">
<c:set var="delimiter" value=""/>
<c:forEach var="toolbarMenu" items="${toolbar}">
  <jsp:useBean id="toolbarMenu" type="java.util.Map.Entry"/>
  <span class="portlet-font"><c:out value="${delimiter}" /></span>
  <div class="ss_toolbar_item">
    <c:if test="${empty toolbarMenu.value.url && empty toolbarMenu.value.urlParams}">
	  <a class="ss_toolbar_item" href="javascript: ;" 
	  onClick="activateMenuLayer('toolbar_${toolbarMenu.key}');">
    </c:if>
    <c:if test="${!empty toolbarMenu.value.url || !empty toolbarMenu.value.urlParams}">
      <c:set var="popup" value="false"/>
      <c:if test="${toolbarMenu.value.qualifiers.popup}">
        <c:set var="popup" value="true"/>
      </c:if>
	  <c:choose>
	    <c:when test="${!empty toolbarMenu.value.url}">
	      <a 
	        class="ss_toolbar_item" 
	        href="${toolbarMenu.value.url}"
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.popup}">
    	      onClick="ss_toolbarPopupUrl(this.href);return false;"
    	    </c:if>
	      >
	    </c:when>
	    <c:when test="${!empty toolbarMenu.value.urlParams}">
	      <a 
	        href="<ssf:url>
	        <c:forEach var="p2" items="${toolbarMenu.value.urlParams}">
			  <c:set var="key2" value="${p2.key}"/>
		      <c:set var="value2" value="${p2.value}"/>
			  <jsp:useBean id="key2" type="java.lang.String" />
			  <jsp:useBean id="value2" type="java.lang.String" />
	          <ssf:param name="<%= key2 %>" value="<%= value2 %>" />
	        </c:forEach>
	 	    </ssf:url>"
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.popup}">
    	      onClick="ss_toolbarPopupUrl(this.href);return false;"
    	    </c:if>
	 	  >
	    </c:when>
	    <c:otherwise>
	      <a href="">
	    </c:otherwise>
	  </c:choose>
    </c:if>
    <span class="portlet-font"><c:out value="${toolbarMenu.value.title}" /></span></a>
  </div>
  <c:set var="delimiter" value=" | "/>
</c:forEach>
</div>
