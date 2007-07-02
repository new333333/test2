<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">
function ss_treeShowIdConfig<portlet:namespace/>(id, obj, action) {
	var binderId = id;
	//See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="configure_definitions"/><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/></portlet:renderURL>";
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	self.location.href = url;
	return false;
}
</script>

<div class="ss_portlet">
<div class="ss_style ss_form" style="margin:0px; padding:10px 16px 10px 10px;">
<div style="margin:6px; width:100%;">
<table cellpadding="0" cellspacing="0" width="100%">
<tr>
<td valign="top">
<span class="ss_bold ss_largerprint"><ssf:nlt tag="binder.configure.definitions"/></span>
<br/>
<br/>
<c:if test="${ssBinder.entityType == 'folder'}">
  <span><ssf:nlt tag="access.currentFolder"/></span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span><ssf:nlt tag="access.currentWorkspace"/></span>
</c:if>
<% //need to check tags for templates %>
<span class="ss_bold"><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
</td>
<td align="right" valign="top">
<form class="ss_form" method="post" style="display:inline;" 
	action="<portlet:actionURL><portlet:param 
	name="action" value="configure_definitions"/><portlet:param 
	name="binderId" value="${ssBinder.id}"/><portlet:param 
	name="binderType" value="${ssBinder.entityType}"/></portlet:actionURL>">
  <input type="submit" class="ss_submit" name="closeBtn" 
    value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</td>
</tr>
</table>
<c:set var="ss_breadcrumbsShowIdRoutine" 
  value="ss_treeShowIdConfig${renderResponse.namespace}" 
  scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>

<c:if test="${ssBinder.definitionInheritanceSupported}">
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="binder.configure.definitions.inheritance" 
    text="Definition inheritance"/> <ssf:inlineHelp tag="ihelp.other.inherit_defs"/> </legend>
<br>
<c:set var="yes_checked" value=""/>
<c:set var="no_checked" value=""/>
<c:if test="${ssBinder.definitionsInherited}">
<span class="ss_bold"><ssf:nlt tag="binder.configure.definitions.inheriting" 
 text="Inheriting definition settings."/></span>
<c:set var="yes_checked" value="checked"/>
<c:set var="disabled" value="disabled"/>

</c:if>
<c:if test="${!ssBinder.definitionsInherited}">
<span class="ss_bold"><ssf:nlt tag="binder.configure.definitions.notInheriting" 
 text="Not inheriting definition settings."/></span>
<c:set var="no_checked" value="checked"/>
<c:set var="disabled" value=""/>

</c:if>
<br><br>

<form name="inheritanceForm" method="post" 
  onSubmit="return ss_onSubmit(this);"
  action="<portlet:actionURL><portlet:param 
  		name="action" value="configure_definitions"/><portlet:param 
  		name="binderId" value="${ssBinder.id}"/><portlet:param 
  		name="binderType" value="${ssBinder.entityIdentifier.entityType}"/></portlet:actionURL>">
<ssf:nlt tag="binder.configure.definitions.inherit"
 text="Inherit definitions :"/>
<br>
&nbsp;&nbsp;&nbsp;<input type="radio" name="inherit" value="yes" ${yes_checked}>
<ssf:nlt tag="general.yes" text="yes"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="radio" name="inherit" value="no" ${no_checked}>
<ssf:nlt tag="general.no" text="no"/>&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="inheritanceBtn"
 value="<ssf:nlt tag="button.apply" text="Apply"/>">
</form>
</fieldset>
<br>
</c:if>
<form method="post" action="<portlet:actionURL><portlet:param 
		name="action" value="configure_definitions"/><portlet:param 
		name="binderType" value="${ssBinder.entityIdentifier.entityType}"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/></portlet:actionURL>" >


<c:if test="${ssBinder.entityType == 'workspace'}">
    <fieldset class="ss_fieldset">
      <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultView" text="Default folder view"/> <ssf:inlineHelp tag="ihelp.other.def_view_type"/> </legend>

      <c:forEach var="item" items="${ssPublicBinderDefinitions}">
          <c:choose>
	        <c:when test="${ssDefaultFolderDefinition.id == item.value.id}">
	          <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	        </c:when>
	        <c:otherwise>
	          <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	        </c:otherwise>
          </c:choose>
      </c:forEach>
      <br>
<c:if test="${!ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
    </fieldset>
    <br>
 </c:if>

<c:if test="${ssBinder.entityType == 'folder'}">
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.allowedViews" text="Allowed views"/> <ssf:inlineHelp tag="ihelp.other.view_type"/> </legend>

    <c:set var="folderViewCount" value=""/>
    <c:forEach var="item" items="${ssPublicBinderDefinitions}">
      <c:choose>
        <c:when test="${empty ssFolderDefinitionMap[item.key]}">
  	      <input type="checkbox" name="binderDefinitions" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
 	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
 	    </c:when>
	    <c:otherwise>
	      <input type="checkbox" name="binderDefinitions" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	      <c:set var="folderViewCount" value="1"/>
	    </c:otherwise>
      </c:choose>
    </c:forEach>
    <br>
<c:if test="${!ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  <br>

  <c:if test="${!empty folderViewCount}">
    <fieldset class="ss_fieldset">
      <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultView" text="Default view"/> <ssf:inlineHelp tag="ihelp.other.def_view_type"/> </legend>

      <c:forEach var="item" items="${ssPublicBinderDefinitions}">
        <c:if test="${!empty ssFolderDefinitionMap[item.key]}">
          <c:choose>
	        <c:when test="${ssDefaultFolderDefinition.id == item.value.id}">
	          <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	        </c:when>
	        <c:otherwise>
	          <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	        </c:otherwise>
          </c:choose>
        </c:if>
      </c:forEach>
      <br>
<c:if test="${!ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
    </fieldset>
    <br>
  </c:if>

  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultEntryTypes" text="Default entry types"/> <ssf:inlineHelp tag="ihelp.other.def_entry_type"/> </legend>

    <c:forEach var="item" items="${ssPublicBinderEntryDefinitions}">
	  <c:choose>
	    <c:when test="${empty ssEntryDefinitionMap[item.key]}">
	      <input type="checkbox" name="entryDefinition" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	    </c:when>
	    <c:otherwise>
	      <input type="checkbox" name="entryDefinition" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	    </c:otherwise>
	  </c:choose>
    </c:forEach>
    <br>
<c:if test="${!ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  <br>
  <% //only display if have workflows - which covers the case where workflow is not supported %>
<c:if test="${!empty ssPublicWorkflowDefinitions}">
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.workflowAssociations" text="Workflow associations"/> <ssf:inlineHelp tag="ihelp.other.workflow_association"/> </legend>

	<table>
	<c:forEach var="item" items="${ssPublicBinderEntryDefinitions}">
	  <c:if test="${!empty ssEntryDefinitionMap[item.key]}">
	  <tr>
	    <td><ssf:nlt tag="${item.value.title}" checkIfTag="true"/></td>
		<td>
		  <select name="workflow_<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
		    <option value=""><ssf:nlt tag="common.select.none" text="--none--"/></option>
	          <c:forEach var="wfp" items="${ssPublicWorkflowDefinitions}">
	            <c:if test="${ssBinder.workflowAssociations[item.value.id] eq wfp.value}">
	              <option value="<c:out value="${wfp.value.id}"/>" selected>
		          <ssf:nlt tag="${wfp.value.title}" checkIfTag="true"/>(${wfp.value.name})</option>
	            </c:if>
	            <c:if test="${ssBinder.workflowAssociations[item.value.id] != wfp.value}">
	              <option value="<c:out value="${wfp.value.id}"/>">
		          <ssf:nlt tag="${wfp.value.title}" checkIfTag="true"/>(${wfp.value.name})</option>
	            </c:if>
	          </c:forEach>
		  </select>
		</td>
	  </tr>
	  </c:if>
	</c:forEach>
	</table>
	<br>
<c:if test="${!ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.allowedWorkflows" text="Allowed workflows"/>  <ssf:inlineHelp tag="ihelp.other.allowed_workflows"/> </legend>

    <c:forEach var="item" items="${ssPublicWorkflowDefinitions}">
	  <c:choose>
	    <c:when test="${empty ssWorkflowDefinitionMap[item.key]}">
	      <input type="checkbox" name="workflowDefinition" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	    </c:when>
	    <c:otherwise>
	      <input type="checkbox" name="workflowDefinition" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	    </c:otherwise>
	  </c:choose>
    </c:forEach>
    <br>
<c:if test="${!ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
</c:if>
  <br>
  
</c:if>

<c:if test="${ssBinder.entityType == 'profiles'}">
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.profileView" text="Profile listing"/> <ssf:inlineHelp tag="ihelp.other.profile_view"/> </legend>

    <c:forEach var="item" items="${ssPublicBinderDefinitions}">
      <c:choose>
        <c:when test="${empty ssFolderDefinitionMap[item.key]}">
  	      <input type="radio" name="binderDefinitions" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
 	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
 	    </c:when>
	    <c:otherwise>
	      <input type="radio" name="binderDefinitions" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	    </c:otherwise>
      </c:choose>
    </c:forEach>
    <br>
<c:if test="${!ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  <br>
  
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.profileEntryType" text="Profile type"/> <ssf:inlineHelp tag="ihelp.other.profile_type"/> </legend>

    <c:forEach var="item" items="${ssPublicBinderEntryDefinitions}">
	  <c:choose>
	    <c:when test="${empty ssEntryDefinitionMap[item.key]}">
	      <input type="radio" name="entryDefinition" value="<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	    </c:when>
	    <c:otherwise>
	      <input type="radio" name="entryDefinition" value="<c:out value="${item.value.id}"/>" checked <c:out value="${disabled}"/>>
	          <ssf:nlt tag="${item.value.title}" checkIfTag="true"/><br/>
	    </c:otherwise>
	  </c:choose>
    </c:forEach>
    <br>
<c:if test="${!ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  
</c:if>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

</form>
</div>
</div>
</div>


