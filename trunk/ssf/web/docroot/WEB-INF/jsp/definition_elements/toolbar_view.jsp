<% // Toolbar viewer %>
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
	        <li class="ss_dropdownmenu">	        
	          <c:choose>
	            <c:when test="${!empty toolbarMenuCategoryItem.value.url}">
	        	  <a 
	        	  <c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.popup}">
	        	    target="_blank"
	        	  </c:if>
	        	  href="<c:out value="${toolbarMenuCategoryItem.value.url}"/>"
	            </c:when>
	            <c:when test="${!empty toolbarMenuCategoryItem.value.urlParams}">
	            <a 
	        	  <c:if test="${!empty toolbarMenuCategoryItem.value.qualifiers.popup}">
	        	    target="_blank"
	        	  </c:if>
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
	          onClick="return(ss_openUrlInPortlet(this.href));">
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
	  <c:choose>
	    <c:when test="${!empty toolbarMenu.value.url}">
	      <a 
	        class="ss_toolbar_item" 
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.popup}">
    	      target="_blank"
    	    </c:if>
	        href="${toolbarMenu.value.url}">
	    </c:when>
	    <c:when test="${!empty toolbarMenu.value.urlParams}">
	      <a 
    	    <c:if test="${!empty toolbarMenu.value.qualifiers.popup}">
    	      target="_blank"
    	    </c:if>
	        href="<ssf:url>
	        <c:forEach var="p2" items="${toolbarMenu.value.urlParams}">
			  <c:set var="key2" value="${p2.key}"/>
		      <c:set var="value2" value="${p2.value}"/>
			  <jsp:useBean id="key2" type="java.lang.String" />
			  <jsp:useBean id="value2" type="java.lang.String" />
	          <ssf:param name="<%= key2 %>" value="<%= value2 %>" />
	        </c:forEach>
	 	  </ssf:url>"
	    </c:when>
	    <c:otherwise>
	    </c:otherwise>
	  </c:choose>
    </c:if>
    <span class="portlet-font"><c:out value="${toolbarMenu.value.title}" /></span></a>
  </div>
  <c:set var="delimiter" value=" | "/>
</c:forEach>
</div>
