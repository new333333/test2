<%
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<taconite-root xml:space="preserve">
<c:if test="${!empty ss_ajaxStatus[ss_ajaxNotLoggedIn]}">

	<taconite-replace contextNodeID="ss_status_message" 
	  parseInBrowser="true">
		<div id="ss_status_message" 
		  style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
</c:if>
<c:if test="${empty ss_ajaxStatus[ss_ajaxNotLoggedIn]}">
	<taconite-replace contextNodeID="ss_status_message" 
	  parseInBrowser="true">
		<div id="ss_status_message" 
		  style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>
	<taconite-replace contextNodeID="ss_definitions" 
	parseInBrowser="true"><div id="ss_definitions">
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="binder.add.folder.definition.legend" 
    text="Folder definition"/></legend>
  <br/>
  <span class="ss_bold"><ssf:nlt tag="binder.add.folder.select.definition" 
  text="Select the folder definition:"/></span><br/>
  <c:forEach var="item" items="${ssPublicBinderDefinitions}">
      <c:choose>
        <c:when test="${ssDefaultWorkspaceDefinitionId == item.value.id}">
          <input type="radio" name="binderDefinition" value="${item.value.id}" checked/>
          <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
        </c:when>
        <c:otherwise>
          <input type="radio" name="binderDefinition" value="${item.value.id}"/>
          <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
        </c:otherwise>
      </c:choose>
  </c:forEach>
</fieldset>
</div></taconite-replace>

</c:if>
</taconite-root>




