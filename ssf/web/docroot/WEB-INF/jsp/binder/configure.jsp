<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("binder.configure.definitions") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body tundra">
</ssf:ifadapter>

<script type="text/javascript">
function ss_treeShowIdConfig${renderResponse.namespace}(id, obj, action) {
	var binderId = id;
	//See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	//Build a url to go to
	var url = "<ssf:url actionUrl="false" action="configure_definitions"><ssf:param 
		name="binderId" value="ssBinderIdPlaceHolder"/></ssf:url>";
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	self.location.href = url;
	return false;
}
</script>

<div class="ss_portlet">
<div class="ss_style ss_form" style="margin:0px; padding:10px 16px 10px 10px;">
<div style="margin:6px; width:100%;">

<ssf:form title='<%= NLT.get("binder.configure.definitions") %>'>

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
<div align="right">
<form class="ss_form" method="post" style="display:inline;" 
	action="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="binderType" value="${ssBinder.entityType}"/></ssf:url>">
  <input type="submit" class="ss_submit" name="closeBtn" 
    value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</div>

<c:set var="ss_breadcrumbsShowIdRoutine" 
  value="ss_treeShowIdConfig${renderResponse.namespace}" 
  scope="request" />
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />

<br/>
<c:if test="${ssSimpleUrlChangeAccess}">
<form method="post" action="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param 
		name="binderType" value="${ssBinder.entityIdentifier.entityType}"/><ssf:param 
		name="binderId" value="${ssBinder.id}"/></ssf:url>" >
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.defineSimpleUrl"/>  
      <ssf:inlineHelp jsp="workspaces_folders/misc_tools/defineSimpleUrl"/> </legend>

    <span class="ss_labelAbove"><ssf:nlt tag="simpleUrl.currentlyDefinedUrls"/></span>
    <c:if test="${!empty ssSimpleUrlNames}">
      <c:forEach var="name" items="${ssSimpleUrlNames}">
        <input type="checkbox" name="delete_${name.name}"/><span style="padding-left:6px;">${ssSimpleUrlPrefix}${name.name}</span><br/>
      </c:forEach>
    </c:if>
    <input type="submit" class="ss_submit" name="deleteUrlBtn" 
      value="<ssf:nlt tag="simpleUrl.deleteSelectedUrls"/>"
      onClick="if(confirm('<ssf:escapeJavaScript><ssf:nlt tag="simpleUrl.confirmDeleteUrl"/></ssf:escapeJavaScript>')){return true}else{return false};"
    />
    <br/>
    <br/>
    <c:if test="${ss_simpleUrlNameExistsError}">
    	<span class="ss_bold ss_errorLabel"><ssf:nlt tag="simpleUrl.nameAlreadyExists"/></span><br/><br/>
    </c:if>
    <c:if test="${ss_simpleUrlNameNotAllowedError}">
    	<span class="ss_bold ss_errorLabel"><ssf:nlt tag="simpleUrl.nameNotAllowed"/></span><br/><br/>
    </c:if>
    <c:if test="${ss_simpleUrlInvalidCharactersError}">
    	<span class="ss_bold ss_errorLabel"><ssf:nlt tag="simpleUrl.invalidCharacters"/></span><br/><br/>
    </c:if>
    
    <span class="ss_labelAbove"><ssf:nlt tag="simpleUrl.addUrl"/></span> 
    <table cellspacing="0" cellpadding="0">
    <tr>
    <td valign="top">
      <span class="ss_bold">${ssSimpleUrlPrefix}&nbsp;</span>
    </td>
    <td valign="top">
      <label for="prefix">&nbsp;</label>
      <select name="prefix" id="prefix">
        <c:if test="${ss_isSiteAdmin}">
          <option value="" selected>--<ssf:nlt tag="simpleUrl.leaveBlank"/>--</option>
        </c:if>
        <option value="${ssUser.name}" selected>${ssUser.name}</option>
        <c:if test="${ssBinder.owner.name != ssUser.name}">
          <option value="${ssBinder.owner.name}">${ssBinder.owner.name}</option>
        </c:if>
        <c:forEach var="item" items="${ssSimpleUrlGlobalKeywords}">
          <option value="${item}">${item}</option>
        </c:forEach>
      </select>
    </td>
    <td valign="top">
      <span class="ss_bold">&nbsp;/&nbsp;</span>
    </td>
    <td valign="top">
      <input type="text" name="name" id="name" size="60"/>
      <label for="name">&nbsp;</label>
    </td>
    </tr>
    </table>
    <br>
    <input type="submit" class="ss_submit" name="addUrlBtn" value="<ssf:nlt tag="button.add"/>"> 
	<c:if test="${ssSimpleEmailEnabled}">
		<br/><br/>
	    <table cellspacing="0" cellpadding="0">
	      <tr>
	      	<td>
		  <c:choose>
			<c:when test="${ssBinder.postingEnabled}">
	    	  <input type="checkbox" id="enableCB" name="allow_simple_email" checked/>
	    	</c:when>
	    	<c:otherwise>
	    	  <input type="checkbox" id="enableCB" name="allow_simple_email"/>
	    	</c:otherwise>
	      </c:choose>
	      	</td>
	      	<td><label for="enableCB"><span style="padding-left:6px;"><ssf:nlt tag="simpleEmail.title"/></span><label></td>
	      </tr>
		  <c:forEach var="name" items="${ssSimpleUrlNames}">
			<tr>
		  	  <td>&nbsp;</td>
			  <td><span style="padding-left:6px;">${name.emailAddress}@${ssSimpleEmailHostname}</span></td>
			</tr>
		  </c:forEach>
		</table>
		<br>
		<input type="submit" class="ss_submit" name="updateEmailButton" value="<ssf:nlt tag="button.apply"/>"> 
	</c:if>
  </fieldset>
  <br>
</form>
</c:if>
<c:if test="${!ssSimpleUrlChangeAccess}">
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.defineSimpleUrl"/></legend>
  </fieldset>
<br/>
</c:if>

<c:set var="allDefinitionsMap" value="${ssBinder.definitionMap}"/>
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
  action="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param 
  		name="binderId" value="${ssBinder.id}"/><ssf:param 
  		name="binderType" value="${ssBinder.entityIdentifier.entityType}"/></ssf:url>">
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
<table cellspacing="0" cellpadding="0" width="99%">
<tr>
<td width="50%" valign="top">
<form method="post" action="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param 
		name="binderType" value="${ssBinder.entityIdentifier.entityType}"/><ssf:param 
		name="binderId" value="${ssBinder.id}"/></ssf:url>" >


<c:if test="${ssBinder.entityType == 'workspace'}">
    <fieldset class="ss_fieldset">
      <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultView" text="Default folder view"/> <ssf:inlineHelp jsp="workspaces_folders/misc_tools/views_workspaces" /> </legend>

      <c:forEach var="item" items="${ssAllBinderDefinitions}" >
      <c:if test="${item.value.binderId == -1}">
          <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" id="<c:out value="${item.value.id}"/>" <c:if test="${ssBinder.entryDef.id== item.value.id}"> checked </c:if> <c:out value="${disabled}"/>>
          <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
          <label for="<c:out value="${item.value.id}"/>">&nbsp;</label>
         </c:if>
     </c:forEach>
      <br>
    <c:set var="headerOut" value=""/>
     <c:forEach var="item" items="${ssAllBinderDefinitions}">
   	   <c:if test="${item.value.binderId != -1}">
  	    <c:if test="${empty headerOut}"><c:set var="headerOut" value="1"/><hr/><span class="ss_bold"><ssf:nlt tag="definition.local"/></span><br/></c:if>
	    <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" <c:if test="${ssBinder.entryDef.id== item.value.id}"> checked </c:if><c:out value="${disabled}"/>>
	     <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
        </c:if>
    </c:forEach>
    <br>
      
<c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
    </fieldset>
    <br>
 </c:if>

<c:if test="${ssBinder.entityType == 'folder'}">
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.allowedViews" text="Allowed views"/> <ssf:inlineHelp jsp="workspaces_folders/misc_tools/allowed_views" /> </legend>

    <c:set var="folderViewCount" value=""/>
    <c:forEach var="item" items="${ssAllBinderDefinitions}">
       <c:if test="${item.value.binderId == -1}">
 	      <input type="checkbox" name="binderDefinitions" value="<c:out value="${item.value.id}"/>" 
	      <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked <c:set var="folderViewCount" value="1"/></c:if>
	      <c:out value="${disabled}"/>><c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
		</c:if>
    </c:forEach>
    <br>
    <c:set var="headerOut" value="0"/>
      <c:forEach var="item" items="${ssAllBinderDefinitions}">
   	   <c:if test="${item.value.binderId != -1}">
  	    <c:if test="${headerOut == '0'}"><c:set var="headerOut" value="1"/><hr/><span class="ss_bold"><ssf:nlt tag="definition.local"/></span><br/></c:if>
	      <input type="checkbox" name="binderDefinition" value="<c:out value="${item.value.id}"/>" 
	      <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked <c:set var="folderViewCount" value="1"/></c:if>
		      <c:out value="${disabled}"/>> <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
		</c:if>
     </c:forEach>
    <br>
    
<c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  <br>

  <c:if test="${!empty folderViewCount}">
    <fieldset class="ss_fieldset">
      <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultView" text="Default view"/> <ssf:inlineHelp jsp="workspaces_folders/misc_tools/views_folders" /> </legend>

      <c:forEach var="item" items="${ssAllBinderDefinitions}">
        <c:if test="${!empty allDefinitionsMap[item.value.id]}">
           <input type="radio" name="binderDefinition" value="<c:out value="${item.value.id}"/>" <c:if test="${ssBinder.entryDef.id == item.value.id}"> checked </c:if> <c:out value="${disabled}"/>>
          <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
        </c:if>
      </c:forEach>
      <br>
<c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
    </fieldset>
    <br>
  </c:if>

  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultEntryTypes" text="Default entry types"/> <ssf:inlineHelp jsp="workspaces_folders/misc_tools/defaultEntryTypes" /> </legend>

    <c:forEach var="item" items="${ssAllEntryDefinitions}">
   	   <c:if test="${item.value.binderId == -1}">
	      <input type="checkbox" name="entryDefinition" value="<c:out value="${item.value.id}"/>" 
	      <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
		      <c:out value="${disabled}"/>> <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
	  </c:if>
   </c:forEach>
    <br>
    <c:set var="headerOut" value=""/>
     <c:forEach var="item" items="${ssAllEntryDefinitions}">
   	   <c:if test="${item.value.binderId != -1}">
  	    <c:if test="${empty headerOut}"><c:set var="headerOut" value="1"/><hr/><span class="ss_bold"><ssf:nlt tag="definition.local"/></span><br/></c:if>
	      <input type="checkbox" name="entryDefinition" value="<c:out value="${item.value.id}"/>" 
	      <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
		      <c:out value="${disabled}"/>> <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
	 </c:if>
    </c:forEach>
    <br>

<c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  <br>
  
  <% //only display if have workflows - which covers the case where workflow is not supported %>
<c:if test="${!empty ssAllWorkflowDefinitions}">
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.workflowAssociations" text="Workflow associations"/> <ssf:inlineHelp jsp="workspaces_folders/misc_tools/workflowAssociations"/> </legend>

	<table>	
    <tr><th><span class="ss_bold"><ssf:nlt tag="workflow.type.entry"/></span></th></tr>
	<c:forEach var="item" items="${ssAllEntryDefinitions}">
	  <c:if test="${!empty allDefinitionsMap[item.value.id]}">
	  <tr>
	    <td><c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if></td>
		<td>
		  <select name="workflow_<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
		    <option value=""><ssf:nlt tag="common.select.none" text="--none--"/></option>
	          <c:forEach var="wfp" items="${ssAllWorkflowDefinitions}">
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

	<table>
    <tr><th><span class="ss_bold"><ssf:nlt tag="workflow.type.reply"/></span></th></tr>
	<c:forEach var="item" items="${ssAllEntryDefinitions}">
	  <c:if test="${!empty ssReplyDefinitionMap[item.value.id]}">
	  <tr>
	    <td><c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if></td>
		<td>
		  <select name="workflow_<c:out value="${item.value.id}"/>" <c:out value="${disabled}"/>>
		    <option value=""><ssf:nlt tag="common.select.none" text="--none--"/></option>
	          <c:forEach var="wfp" items="${ssAllWorkflowDefinitions}">
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
<c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.allowedWorkflows" text="Allowed workflows"/>  <ssf:inlineHelp jsp="workspaces_folders/misc_tools/allowedWorkflows"/> </legend>

    <c:forEach var="item" items="${ssAllWorkflowDefinitions}">
   	   <c:if test="${item.value.binderId == -1}">
	      <input type="checkbox" name="workflowDefinition" value="<c:out value="${item.value.id}"/>" 
	      <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
	      <c:out value="${disabled}"/>>
	          <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
	   </c:if>
   </c:forEach>
    <br>
   <c:set var="headerOut" value=""/>
    <c:forEach var="item" items="${ssAllWorkflowDefinitions}">
   	   <c:if test="${item.value.binderId != -1}">
		<c:if test="${empty headerOut}"><c:set var="headerOut" value="1"/><hr/><span class="ss_bold"><ssf:nlt tag="definition.local"/></span><br/></c:if>		
	      <input type="checkbox" name="workflowDefinition" value="<c:out value="${item.value.id}"/>" 
	      <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
	      <c:out value="${disabled}"/>>
	          <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
	   </c:if>
   </c:forEach>
     <br>

   
<c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  <br>
</c:if>
  
</c:if>

<c:if test="${ssBinder.entityType == 'profiles'}">
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.profileView" text="Profile listing"/> <ssf:inlineHelp tag="ihelp.other.profile_view"/> </legend>

    <c:forEach var="item" items="${ssAllBinderDefinitions}">
 	      <input type="radio" name="binderDefinitions" value="<c:out value="${item.value.id}"/>" <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if><c:out value="${disabled}"/>>
	      <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
	</c:forEach>
    <br>
<c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  <br>
  
  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="binder.configure.profileEntryType" text="Profile type"/> <ssf:inlineHelp tag="ihelp.other.profile_type"/> </legend>

    <c:forEach var="item" items="${ssAllEntryDefinitions}">
	      <input type="checkbox" name="entryDefinition" value="<c:out value="${item.value.id}"/>" 
	      <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
		      <c:out value="${disabled}"/>> <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if test="${item.value.visibility == 3}"></del></c:if><br/>
   </c:forEach>
    <br>
<c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
      <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
</c:if>
  </fieldset>
  
</c:if>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

</form>

</td>
</tr>
</table>

</ssf:form>

</div>
</div>
</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

