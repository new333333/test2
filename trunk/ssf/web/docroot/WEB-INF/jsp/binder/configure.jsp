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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<div class="ss_portlet">

<div class="forms">
<form method="post" action="<portlet:actionURL>
					<portlet:param name="action" value="configure_forum"/>
					<portlet:param name="binderId" value="${ssBinder.id}"/>
					</portlet:actionURL>" >

<div class="ss_buttonBarRight">
<input type="submit" name="closeBtn" class="ss_submit" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

<fieldset class="fieldset">
 <legend class="legend"><ssf:nlt tag="forum.configure.allowedViews" text="Allowed folder views"/></legend>

<c:set var="folderViewCount" value=""/>
<c:forEach var="item" items="${ssPublicFolderDefinitions}">
  <c:choose>
	<c:when test="${empty ssFolderDefinitionMap[item.key]}">
	  <input type="checkbox" name="binderDefinitions" value="<c:out value="${item.value.id}"/>"><c:out value="${item.value.name}"/><br/>
	</c:when>
	<c:otherwise>
	  <input type="checkbox" name="binderDefinitions" value="<c:out value="${item.value.id}"/>" checked><c:out value="${item.value.name}"/><br/>
	  <c:set var="folderViewCount" value="1"/>
	</c:otherwise>
  </c:choose>
</c:forEach>
<br>
<input type="submit" name="okBtn" class="ss_submit" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 

</fieldset>
<br>

<c:if test="${!empty folderViewCount}">
<fieldset class="fieldset">
 <legend class="legend"><ssf:nlt tag="forum.configure.defaultView" text="Default folder view"/></legend>

<c:forEach var="item" items="${ssPublicFolderDefinitions}">
  <c:if test="${!empty ssFolderDefinitionMap[item.key]}">
    <c:choose>
	  <c:when test="${ssDefaultFolderDefinitionId == item.value.id}">
	    <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" checked><c:out value="${item.value.name}"/><br/>
	  </c:when>
	  <c:otherwise>
	    <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>"><c:out value="${item.value.name}"/><br/>
	  </c:otherwise>
    </c:choose>
  </c:if>
</c:forEach>
<br>
<input type="submit" name="okBtn" class="ss_submit" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 

</fieldset>
<br>
</c:if>

<fieldset class="fieldset">
 <legend class="legend"><ssf:nlt tag="forum.configure.defaultEntryTypes" text="Default entry types"/></legend>

<c:forEach var="item" items="${ssPublicEntryDefinitions}">
	<c:choose>
	<c:when test="${empty ssEntryDefinitionMap[item.key]}">
	<input type="checkbox" name="entryDefinition" value="<c:out value="${item.value.id}"/>"><c:out value="${item.value.name}"/><br/>
	</c:when>
	<c:otherwise>
	<input type="checkbox" name="entryDefinition" value="<c:out value="${item.value.id}"/>" checked><c:out value="${item.value.name}"/><br/>
	</c:otherwise>
	</c:choose>
</c:forEach>
<br>
<input type="submit" name="okBtn" class="ss_submit" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 

</fieldset>
<br>

<fieldset class="fieldset">
 <legend class="legend"><ssf:nlt tag="forum.configure.workflowAssociations" text="Workflow associations"/></legend>

<table>
<c:forEach var="item" items="${ssPublicEntryDefinitions}">
  <c:if test="${!empty ssEntryDefinitionMap[item.key]}">
  <tr>
    <td><c:out value="${item.value.name}"/></td>
	<td>
	  <select name="workflow_<c:out value="${item.value.id}"/>">
	    <option value=""><ssf:nlt tag="common.select.none" text="--none--"/></option>
          <c:forEach var="wfp" items="${ssPublicWorkflowDefinitions}">
            <c:if test="${ssFolderWorkflowAssociations[item.value.id] eq wfp.value.id}">
              <option value="<c:out value="${wfp.value.id}"/>" selected><c:out value="${wfp.value.name}"/></option>
            </c:if>
            <c:if test="${ssFolderWorkflowAssociations[item.value.id] != wfp.value.id}">
              <option value="<c:out value="${wfp.value.id}"/>"><c:out value="${wfp.value.name}"/></option>
            </c:if>
          </c:forEach>
	  </select>
	</td>
  </tr>
  </c:if>
</c:forEach>
</table>
<br>
<input type="submit" name="okBtn" class="ss_submit" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 

</fieldset>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" name="closeBtn" class="ss_submit" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

</form>
</div>

</div>

