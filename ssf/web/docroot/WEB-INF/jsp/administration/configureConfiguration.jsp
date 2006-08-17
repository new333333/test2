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
<%@ page import="com.sitescape.ef.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px;">
<c:if test="${empty ssBinderConfig}">
<script type="text/javascript">

function <portlet:namespace/>_onsub(obj) {
	if (obj.cfgTitle.value == '') {
		alert('<ssf:nlt tag="general.required.title"/>');
		return false;
	}
	return true;
}
</script>

<h3><ssf:nlt tag="administration.configure_cfg" text="Configurations"/></h3>
<ssf:expandableArea title="<%= NLT.get("administration.configure_cfg.add") %>">
<form class="ss_style ss_form" name="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_configuration"/>
		</portlet:actionURL>" onSubmit="return(<portlet:namespace/>_onsub(this))">
		
	<span><b><ssf:nlt tag="administration.configure_cfg.title" text="Title"/></b></span>
	<input type="text" class="ss_text" size="70" name="cfgTitle"><br>
	
	<br/>
	 <input type="radio" name="cfgType" value="8"><ssf:nlt tag="general.type.workspace" 
		  text="Workspace" /><br/>
	 <input type="radio" name="cfgType" value="5" checked><ssf:nlt tag="general.type.folder" 
		  text="Folder" /><br/>
     <input type="radio" name="cfgType" value="9"><ssf:nlt tag="general.type.file" 
		  text="File" />
	<br/><br/>
	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</ssf:expandableArea>

<br>
<hr>
<br>
<h3><ssf:nlt tag="administration.configure_cfg.existing" text="Currently defined configurations"/></h3>
<c:if test="${!empty ssBinderConfigs}">
<ssf:expandableArea title='<%= NLT.get("administration.configure_cfg.modify") %>'>
<c:forEach var="cfg" items="${ssBinderConfigs}">
<a href="<portlet:renderURL>
			<portlet:param name="action" value="configure_configuration"/>
			<portlet:param name="objectId" value="${cfg.id}"/>
			</portlet:renderURL>"><ssf:nlt tag="${cfg.title}" checkIfTag="true"/></a>
<br/>
</c:forEach>
</ssf:expandableArea>
</c:if>
<br/>

<form class="ss_style ss_form" name="<portlet:namespace/>rolesForm" method="post" 
	action="<portlet:renderURL windowState="normal" portletMode="view">
		</portlet:renderURL>">

	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</c:if>
<c:if test="${!empty ssBinderConfig}">
<c:set var="disabled" value=""/>

<form method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_configuration"/>
			<portlet:param name="objectId" value="${ssBinderConfig.id}"/>
			</portlet:actionURL>" >

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
<ssf:nlt tag="${ssBinderConfig.title}" checkIfTag="true"/><br/>
<c:if test="${ssBinderConfig.definitionType == 8}">
    <fieldset class="ss_fieldset">
      <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultView" text="Default folder view"/></legend>

      <c:forEach var="item" items="${ssPublicBinderDefinitions}">
          <c:choose>
	        <c:when test="${ssDefaultFolderDefinition.id == item.value.id}">
	          <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	          <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
	        </c:when>
	        <c:otherwise>
	          <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
	          <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
	        </c:otherwise>
          </c:choose>
      </c:forEach>
      <br>
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
    </fieldset>
    <br>
 </c:if>

<c:if test="${ssBinderConfig.definitionType != 8}">
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.allowedViews" text="Allowed folder views"/></legend>

    <c:set var="folderViewCount" value=""/>
    <c:forEach var="item" items="${ssPublicBinderDefinitions}">
      <c:choose>
        <c:when test="${empty ssFolderDefinitionMap[item.key]}">
  	      <input type="checkbox" name="binderDefinitions" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
  	      <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
  	    </c:when>
	    <c:otherwise>
	      <input type="checkbox" name="binderDefinitions" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	      <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
	      <c:set var="folderViewCount" value="1"/>
	    </c:otherwise>
      </c:choose>
    </c:forEach>
    <br>
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
  </fieldset>
  <br>

  <c:if test="${!empty folderViewCount}">
    <fieldset class="ss_fieldset">
      <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultView" text="Default folder view"/></legend>

      <c:forEach var="item" items="${ssPublicBinderDefinitions}">
        <c:if test="${!empty ssFolderDefinitionMap[item.key]}">
          <c:choose>
	        <c:when test="${ssDefaultFolderDefinition.id == item.value.id}">
	          <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	          <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
	        </c:when>
	        <c:otherwise>
	          <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
	          <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
	        </c:otherwise>
          </c:choose>
        </c:if>
      </c:forEach>
      <br>
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
    </fieldset>
    <br>
  </c:if>

  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultEntryTypes" text="Default entry types"/></legend>

    <c:forEach var="item" items="${ssPublicBinderEntryDefinitions}">
	  <c:choose>
	    <c:when test="${empty ssEntryDefinitionMap[item.key]}">
	      <input type="checkbox" name="entryDefinition" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
	      <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
	    </c:when>
	    <c:otherwise>
	      <input type="checkbox" name="entryDefinition" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	      <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
	    </c:otherwise>
	  </c:choose>
    </c:forEach>
    <br>
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
  </fieldset>
  <br>

  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.workflowAssociations" text="Workflow associations"/></legend>

	<table>
	<c:forEach var="item" items="${ssPublicBinderEntryDefinitions}">
	  <c:if test="${!empty ssEntryDefinitionMap[item.key]}">
	  <tr>
	    <td><c:out value="${item.value.title}"/></td>
		<td>
		  <select name="workflow_<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
		    <option value=""><ssf:nlt tag="common.select.none" text="--none--"/></option>
	          <c:forEach var="wfp" items="${ssPublicWorkflowDefinitions}">
	            <c:if test="${ssBinderConfig.workflowIds[item.value.id] == wfp.key}">
	              <option value="<c:out value="${wfp.value.id}"/>" selected>
	              <c:out value="${wfp.value.title}"/> (<c:out value="${wfp.value.name}"/>)</option>
	            </c:if>
	            <c:if test="${ssBinderConfig.workflowIds[item.value.id] != wfp.key}">
	              <option value="<c:out value="${wfp.value.id}"/>">
	              <c:out value="${wfp.value.title}"/> (<c:out value="${wfp.value.name}"/>)</option>
	            </c:if>
	            
	          </c:forEach>
		  </select>
		</td>
	  </tr>
	  </c:if>
	</c:forEach>
	</table>
	<br>
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
  </fieldset>
</c:if>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete" text="Delete"/>">
</div>

</form>
</c:if>
</div>
</div>
</div>
</div>

