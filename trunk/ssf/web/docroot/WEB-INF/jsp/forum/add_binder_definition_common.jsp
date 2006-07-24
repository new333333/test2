<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
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
<fieldset class="ss_fieldset">
<c:choose>
<c:when test="${ssBinderDefinitionType == 8}">
  <legend class="ss_legend"><ssf:nlt tag="binder.add.workspace.definition.legend" 
    text="Workspace definition"/></legend>
  <br/>
  <span class="ss_bold"><ssf:nlt tag="binder.add.workspace.select.definition" 
  text="Select the workspace definition:"/></span>
</c:when>
<c:otherwise>
  <legend class="ss_legend"><ssf:nlt tag="binder.add.folder.definition.legend" 
    text="Folder definition"/></legend>
  <br/>
  <span class="ss_bold"><ssf:nlt tag="binder.add.folder.select.definition" 
  text="Select the folder definition:"/></span>
</c:otherwise>
</c:choose>
  <br/>
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

