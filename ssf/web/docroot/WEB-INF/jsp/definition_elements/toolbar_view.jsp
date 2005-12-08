<% // Toolbar viewer %>
<jsp:useBean id="toolbar" type="java.util.SortedMap" scope="request" />
<c:forEach var="toolbarMenu" items="${toolbar}">
  <c:if test="${empty toolbarMenu.value.url}">
	<div class="ss_toolbar_menu" width="100%" id="toolbar_<c:out value="${toolbarMenu.key}" />">
	  <c:forEach var="toolbarMenuCategory" items="${toolbarMenu.value.categories}">
	    <c:if test="${empty toolbarMenuCategory.key}">
	      <span class="contentbold"><c:out value="${toolbarMenuCategory.key}" /></span>
	    </c:if>
	    <ul class="dropdownmenu">
	    <c:forEach var="toolbarMenuCategoryItem" items="${toolbarMenuCategory.value}">
	      <li class="dropdownmenu">
	        <a href="<ssf:url>
	          <c:forEach var="p" items="${toolbarMenuCategoryItem.value}">
				<c:set var="key" value="${p.key}"/>
				<c:set var="value" value="${p.value}"/>
			    <jsp:useBean id="key" type="java.lang.String" />
			    <jsp:useBean id="value" type="java.lang.String" />
	            <ssf:param name="<%= key %>" value="<%= value %>" />
	          </c:forEach>
	        </sitescape:url>" 
	        onClick="return(ss_openUrlInPortlet(this.href));"><c:out 
	        value="${toolbarMenuCategoryItem.key}" /></a></li>
	    </c:forEach>
	    </ul>
	  </c:forEach>
	</div>
  </c:if>
</c:forEach>

<div class="ss_toolbar" style="width:<c:if 
  test="${empty ss_toolbarWidth}"><c:out value="100%"/></c:if><c:if 
  test="${!empty ss_toolbarWidth}"><c:out value="${ss_toolbarWidth}"/></c:if>;">
<c:set var="delimiter" value=""/>
<c:forEach var="toolbarMenu" items="${toolbar}">
<span class="content"><c:out value="${delimiter}" /></span>
<div class="ss_toolbar_item">
  <c:if test="${empty toolbarMenu.value.url}">
	<a class="ss_toolbar_item" href="javascript: ;" 
	onClick="activateMenuLayer('toolbar_<c:out value="${toolbarMenu.key}" />');">
  </c:if>
  <c:if test="${!empty toolbarMenu.value.url}">
	<a class="ss_toolbar_item" href="<ssf:url>
      <c:forEach var="p" items="${toolbarMenu.value.url}">
		<c:set var="key2" value="${p.key}"/>
		<c:set var="value2" value="${p.value}"/>
	    <jsp:useBean id="key2" type="java.lang.String" />
	    <jsp:useBean id="value2" type="java.lang.String" />
        <ssf:param name="<%= key2 %>" value="<%= value2 %>"/>
      </c:forEach>
    </sitescape:url>"  onClick="return(ss_openUrlInPortlet(this.href));">
  </c:if>
<c:out value="${toolbarMenu.value.title}" /></a>
</div>
<c:set var="delimiter" value=" | "/>
</c:forEach>
</div>
