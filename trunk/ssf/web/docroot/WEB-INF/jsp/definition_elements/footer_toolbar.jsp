<% // Footer toolbar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.ef.context.request.RequestContextHolder" %>
<c:if test="${!empty ssFooterToolbar}">
<div align="center">
<c:set var="delimiter" value=""/>
<c:forEach var="toolbarMenu" items="${ssFooterToolbar}">
    <c:if test="${!empty toolbarMenu.value.url || !empty toolbarMenu.value.urlParams}">
      <c:out value="${delimiter}" escapeXml="false"/>
      <c:set var="popup" value="false"/>
      <c:if test="${toolbarMenu.value.qualifiers.popup}">
        <c:set var="popup" value="true"/>
      </c:if>
	  <c:choose>
	    <c:when test="${!empty toolbarMenu.value.url}">
	      <div class="ss_bottomlinks"><a href="${toolbarMenu.value.url}"
    	    <c:if test="${empty toolbarMenu.value.qualifiers.onClick}">
    	    	<c:if test="${!empty toolbarMenu.value.qualifiers.popup}">
    	      		onClick="ss_toolbarPopupUrl(this.href);return false;"
    	    	</c:if>
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
    	      	folder="${toolbarMenu.value.qualifiers.folder}"
    	      	target="_blank"
    	    </c:if>
	      ><c:out 
	        value="${toolbarMenu.value.title}" /></a></div>
	    </c:when>
	    <c:when test="${!empty toolbarMenu.value.urlParams}">
	      <div class="ss_bottomlinks"><a href="<ssf:url>
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
	 	  ><c:out 
	 	    value="${toolbarMenu.value.title}" /></a></div>
	    </c:when>
	    <c:otherwise>
	      <div class="ss_bottomlinks"><a href=""><c:out value="${toolbarMenu.value.title}" /></a></div>
	    </c:otherwise>
	  </c:choose>
	  <c:set var="delimiter" value="<span class=\"ss_bottomlinks\">&nbsp;|&nbsp;</span>" />
    </c:if>
</c:forEach>
</div>
</c:if>
