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

<%@ page import="org.kablink.teaming.util.NLT,org.kablink.util.PropsUtil" %>
<%@ page import="org.kablink.teaming.domain.Binder" %>
<%@ page import="org.kablink.teaming.web.util.BinderHelper" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("binder.configure.definitions.workspace") %>' scope="request"/>
<c:if test="${ssBinder.entityType == 'folder' }">
  <c:set var="ss_windowTitle" value='<%= NLT.get("binder.configure.definitions.folder") %>' scope="request"/>
</c:if>
<c:set var="ss_tabTitle" value="${ss_windowTitle}" scope="request"/>
<c:if test="${ssOperation == 'simpleUrls'}">
  <c:set var="ss_windowTitle" value='<%= NLT.get("binder.configure.definitions.simpleUrls") %>' scope="request"/>
</c:if>
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

<div class="ss_style ss_portlet">
  <div style="padding:10px;">
    <c:if test="${!empty ssException}">
      <br>
      <font color="red">
        <span class="ss_largerprint"><c:out value="${ssException}"/></span>
      </font>
      <br/>
    </c:if>

    <c:if test="${ssOperation != 'simpleUrls'}"><c:set var="ss_tab_definitions" value="on"/></c:if>
    <c:if test="${ssOperation == 'simpleUrls'}"><c:set var="ss_tab_simpleUrls" value="on"/></c:if>
    <%@ include file="/WEB-INF/jsp/binder/configure_tabs.jsp" %>

    <%
      Binder binder = (Binder) request.getAttribute("ssBinder");
      boolean forceRenderJsp = (binder == null) ? false : BinderHelper.useJspRenderer(binder);
    %>
    <div style="display:block;" class="wg-tab-content marginbottom3">
      <div class="ss_style ss_form" style="margin:0px;">
        <div style="width:100%;">
          <c:if test="${ssBinder.entityType == 'folder'}">
            <span><ssf:nlt tag="access.currentFolder"/></span>
          </c:if>
          <c:if test="${ssBinder.entityType != 'folder'}">
            <span><ssf:nlt tag="access.currentWorkspace"/></span>
          </c:if>
          <% //need to check tags for templates %>
          <span class="ss_bold ss_largestprint"><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>

          <c:set var="ss_breadcrumbsShowIdRoutine"
                 value="ss_treeShowIdConfig${renderResponse.namespace}"
                 scope="request"/>
          <jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp"/>

          <br/>
          <c:if test="${ssOperation == 'simpleUrls'}">
            <c:if test="${ssSimpleUrlChangeAccess}">
              <form method="post" name="configure_form" action="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param
		name="binderType" value="${ssBinder.entityIdentifier.entityType}"/><ssf:param 
		name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="operation" value="simpleUrls"/></ssf:url>">
                <fieldset class="ss_fieldset">
                  <legend class="ss_legend"><span class="ss_bold"><ssf:nlt
                      tag="binder.configure.defineSimpleUrl"/></span>
                    <ssf:showHelp guideName="user" pageId="workspace_mnggeneral"
                                  sectionId="workspace_mnggeneral_simpleurl"/>
                  </legend>

                  <% //define a URL control %>
                  <div class="margintop2">
                    <table cellspacing="0" cellpadding="0">
                      <tr>
                        <td valign="middle">
                          <span>${ssSimpleUrlPrefix}&nbsp;</span>
                        </td>
                        <td valign="middle">
                          <label for="prefix">&nbsp;</label>
                          <select name="prefix" id="prefix">
                            <c:if test="${ss_isSiteAdmin}">
                              <option value="" selected>--<ssf:nlt tag="simpleUrl.leaveBlank"/>--</option>
                            </c:if>
                            <option value="${ssUser.urlSafeName}" selected>${ssUser.urlSafeName}</option>
                            <c:if test="${ssBinder.owner.name != ssUser.name}">
                              <option value="${ssBinder.owner.urlSafeName}">${ssBinder.owner.urlSafeName}</option>
                            </c:if>
                            <c:forEach var="item" items="${ssSimpleUrlGlobalKeywords}">
                              <option value="${item}">${item}</option>
                            </c:forEach>
                          </select>
                        </td>
                        <td valign="middle">
                          <span class="ss_bold">&nbsp;/&nbsp;</span>
                        </td>
                        <td valign="middle">
                          <input type="text" name="name" id="name" size="20"/>
                          <label for="name">&nbsp;</label>
                        </td>
                        <td valign="middle">
                          <input type="submit" class="ss_submit" name="addUrlBtn" value="<ssf:nlt tag="button.add"/>">
                        </td>
                      </tr>
                    </table>
                  </div>
                  <div class="margintop2">
                    <c:if test="${ss_simpleUrlNameExistsError}">
                      <span class="ss_bold ss_errorLabel"><ssf:nlt tag="simpleUrl.nameAlreadyExists"/></span><br/><br/>
                    </c:if>
                    <c:if test="${ss_simpleUrlEmailNameExistsError}">
                      <span class="ss_bold ss_errorLabel"><ssf:nlt
                          tag="simpleUrl.emailNameAlreadyExists"/></span><br/><br/>
                    </c:if>
                    <c:if test="${ss_simpleUrlNameNotAllowedError}">
                      <span class="ss_bold ss_errorLabel"><ssf:nlt tag="simpleUrl.nameNotAllowed"/></span><br/><br/>
                    </c:if>
                    <c:if test="${ss_simpleUrlInvalidCharactersError}">
                      <span class="ss_bold ss_errorLabel"><ssf:nlt tag="simpleUrl.invalidCharacters"/></span><br/><br/>
                    </c:if>
                  </div>
                  <div>
                    <span class="ss_labelAbove"><ssf:nlt tag="simpleUrl.currentlyDefinedUrls"/></span>
                    <c:if test="${empty ssSimpleUrlNames}">
                      <div style="padding: 10px;"><ssf:nlt tag="simpleUrl.none"/></div>
                    </c:if>
                    <c:if test="${!empty ssSimpleUrlNames}">
                      <c:forEach var="name" items="${ssSimpleUrlNames}">
                        <input type="checkbox" name="delete_${name.name}"/><span
                          style="padding-left:6px; letter-spacing: 1px;">${ssSimpleUrlPrefix}${name.name} (${ssSimpleWebdavPrefix}${name.emailAddress})</span><br/>
                      </c:forEach>
                      <div class="margintop2 marginbottom3">
                        <input type="submit" class="ss_submit" name="deleteUrlBtn"
                               value="<ssf:nlt tag="simpleUrl.deleteSelectedUrls"/>"
                               onClick="if(confirm('
                                 <ssf:escapeJavaScript>
                                   <ssf:nlt tag="simpleUrl.confirmDeleteUrl"/>
                                 </ssf:escapeJavaScript>')){return true}else{return false};"
                            />
                      </div>
                    </c:if>

                    <c:if test="${ssSimpleEmailEnabled}">

                      <table cellspacing="0" cellpadding="0" class="margintop2">
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
                          <td><label for="enableCB"><span style="padding-left:6px;"><ssf:nlt
                              tag="simpleEmail.title"/></span></label></td>
                        </tr>
                        <c:forEach var="name" items="${ssSimpleUrlNames}">
                          <tr>
                            <td>&nbsp;</td>
                            <td><span
                                style="padding-left:20px; letter-spacing: 1px;">${name.emailAddress}@${ssSimpleEmailHostname}</span>
                            </td>
                          </tr>
                        </c:forEach>
                      </table>
                      <br>
                      <input type="submit" class="ss_submit" name="updateEmailButton"
                             value="<ssf:nlt tag="button.apply"/>">
                    </c:if>
                  </div>
                </fieldset>
                <br>

                <div class="ss_formBreak"></div>

                <div class="ss_buttonBarRight margintop3">
                  <input type="submit" class="ss_submit" name="closeBtn"
                         value="<ssf:nlt tag="button.close" text="Close"/>">
                </div>
              </form>
            </c:if>
            <c:if test="${!ssSimpleUrlChangeAccess}">
              <fieldset class="ss_fieldset">
                <legend class="ss_legend"><ssf:nlt tag="binder.configure.defineSimpleUrl"/></legend>
              </fieldset>
              <br/>
            </c:if>
          </c:if>

          <c:if test="${ssOperation != 'simpleUrls'}">
            <c:set var="allDefinitionsMap" value="${ssBinder.definitionMap}"/>
            <c:if test="${ssBinder.definitionInheritanceSupported}">
              <fieldset class="ss_fieldset">
                <legend class="ss_legend"><ssf:nlt tag="binder.configure.definitions.inheritance"
                                                   text="Definition inheritance"/> <ssf:inlineHelp
                    jsp="workspaces_folders/misc_tools/inherit_defs"/></legend>
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


            <% if (PropsUtil.getBoolean("ssf.allowFolderDefinitionFixups", false)) { %>
            <c:if test="${ssBinder.entityType == 'folder'}">
              <script type="text/javascript">
                // Manage changes to the state of the foler fixups checkbox.
                function folderFixupsChanged(folderFixupsCB) {
                  // If the folder fixups checkbox is checked...
                  var entryFixupsCB = document.getElementById("entryFixups");
                  if (folderFixupsCB.checked) {
                    // ...enable and uncheck the entry fixups checkbox...
                    entryFixupsCB.disabled =
                        entryFixupsCB.checked = false;

                  } else {
                    // ...otherwise, disable and uncheck it.
                    entryFixupsCB.checked = false;
                    entryFixupsCB.disabled = true;
                  }
                }


                // Start the folder fixup process.
                function startFolderFixups() {
                  var url;
                  var obj;


                  // Set up the object that will be used in the AJAX
                  // request.
                  obj = new Object();
                  obj.operation = "startFixupFolderDefs";

                  // Build the url used in the AJAX request.
                  url = ss_buildAdapterUrl(ss_AjaxBaseUrl, obj);

                  // Issue the AJAX request.  The function
                  // handleResponseToStartFixup() will be called we get
                  // the response to the request.
                  ss_get_url(url, handleResponseToStartFixup);
                }
                function handleResponseToStartFixup(responseData) {
                  // Nothing to do!
                }


                // Validates that something is asked to be fixed up.
                function validateFixupsForm() {
                  // If nothing is checkced...
                  if ((!(document.getElementById("folderFixups").checked)) &&
                      (!(document.getElementById("entryFixups").checked))) {
                    // ...tell the user and bail.
                    alert("<ssf:nlt tag="binder.configure.folderDefinitionFixups.warning.noChecks" text="Nothing checked."/>");
                    return false;
                  }

                  // ...otherwise, allow the form to be submitted.
                  return true;
                }


                // If we have a thread ready to start fixing folder
                // definitions...
                <c:if test="${ss_fixupThreadStatus == 'ready'}">
                // ...start it.
                window.setTimer(startFolderFixups(), 100);
                </c:if>
              </script>

              <fieldset class="ss_fieldset">
                <legend class="ss_legend">
                  <ssf:nlt tag="binder.configure.folderDefinitionFixups.banner" text="Recursively apply"/>
                  <ssf:showHelp guideName="adv_user" pageId="mngfolder_entrytype"
                                sectionId="mngfolder_entrytype_recursivedefinition"/>
                </legend>

                <c:if test="${!empty ss_fixupThreadStatus}">
                  <br/><span class="ss_bold ss_errorLabel">
					<c:choose>
            <c:when test="${ss_fixupThreadStatus == 'busy'}">
              <ssf:nlt tag="binder.configure.folderDefinitionFixups.error.fixupThreadBusy"/>
            </c:when>
            <c:when test="${ss_fixupThreadStatus == 'cantStart'}">
              <ssf:nlt tag="binder.configure.folderDefinitionFixups.error.fixupThreadCantStart"/>
            </c:when>
            <c:when test="${ss_fixupThreadStatus == 'running' || ss_fixupThreadStatus == 'ready'}">
              <ssf:nlt tag="binder.configure.folderDefinitionFixups.status.fixupThreadRunning"/>
            </c:when>
            <c:otherwise>
              <ssf:nlt tag="binder.configure.folderDefinitionFixups.error.fixupThreadOther"/>
            </c:otherwise>
          </c:choose>
				</span><br/><br/>
                </c:if>

                <form
                    name="folderDefinitionFixupsForm"
                    method="post"
                    onSubmit="return validateFixupsForm() && ss_onSubmit(this);"
                    action="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param
						name="binderId"   value="${ssBinder.id}"/><ssf:param 
						name="binderType" value="${ssBinder.entityIdentifier.entityType}"/></ssf:url>">
                  <table>
                    <tr>
                      <td><input type="checkbox" id="folderFixups" name="folderFixups"
                                 onChange="javascript:folderFixupsChanged(this)" value="yes"/></td>
                      <td colspan="2"><ssf:nlt tag="binder.configure.folderDefinitionFixups.folder"
                                               text="Fixup Folders"/></td>
                    </tr>

                    <tr>
                      <td/>
                      <td><input type="checkbox" id="entryFixups" name="entryFixups" value="yes" disabled/></td>
                      <td><ssf:nlt tag="binder.configure.folderDefinitionFixups.entry" text="Fixup Entries"/></td>
                    </tr>

                    <tr>
                      <td colspan="2"/>
                      <td><select name="entryFixupDefinitions">
                        <% /* Are there any default entry types defined on this folder? */ %>
                        <c:set var="folderHasDefaultEntryTypes" value=""/>
                        <c:forEach var="item" items="${ssAllEntryDefinitions}">
                          <c:if test="${!empty allDefinitionsMap[item.value.id]}">
                            <% /* Yes! */ %>
                            <c:set var="folderHasDefaultEntryTypes" value="true"/>
                          </c:if>
                        </c:forEach>


                        <% /* Create <options> in the <select> for the default entry    */ %>
                        <% /* types from the folder if there are any or all entry types */ %>
                        <% /* if there aren't.                                          */ %>
                        <c:forEach var="item" items="${ssAllEntryDefinitions}">
                          <c:if test="${item.value.binderId == -1}">
                            <c:if test="${empty folderHasDefaultEntryTypes || !empty allDefinitionsMap[item.value.id]}">
                              <option value="${item.value.id}" <c:if
                                  test="${item.value.visibility == 3}">style="text-decoration: line-through;"</c:if>>${item.key}</option>
                            </c:if>
                          </c:if>
                        </c:forEach>

                        <c:forEach var="item" items="${ssAllEntryDefinitions}">
                          <c:if test="${item.value.binderId != -1}">
                            <c:if test="${empty folderHasDefaultEntryTypes || !empty allDefinitionsMap[item.value.id]}">
                              <option value="${item.value.id}" <c:if
                                  test="${item.value.visibility == 3}">style="text-decoration: line-through;"</c:if>>${item.key}&#134;</option>
                            </c:if>
                          </c:if>
                        </c:forEach>
                      </select></td>
                    </tr>

                    <tr>
                      <td>&nbsp;</td>
                    </tr>
                    <tr>
                      <td colspan="3"><input type="submit" class="ss_submit" name="folderDefinitionFixupsBtn"
                                             value="<ssf:nlt tag="button.apply" text="Apply"/>"></td>
                    </tr>
                  </table>
                </form>
              </fieldset>
              <br>
            </c:if>
            <% } %>


            <table cellspacing="0" cellpadding="0" width="99%">
              <tr>
                <td width="50%" valign="top">
                  <form method="post" name="configure_jsp_form" action="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param
		name="binderType" value="${ssBinder.entityIdentifier.entityType}"/><ssf:param 
		name="binderId" value="${ssBinder.id}"/></ssf:url>">


                    <c:if test="${ssBinder.entityType == 'workspace'}">
                      <fieldset class="ss_fieldset">
                        <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultView"
                                                           text="Default folder view"/> <ssf:showHelp
                            guideName="adv_user" pageId="workspaceviews"/></legend>

                        <c:forEach var="item" items="${ssAllBinderDefinitions}">
                          <c:if test="${item.value.binderId == -1}">
                            <input type="radio" name="binderDefinition" value="${item.value.id}" id="${item.value.id}"
                            <c:if test="${ssBinder.entryDef.id== item.value.id}"> checked </c:if> <c:out
                                value="${disabled}"/>>
                            <c:if test="${item.value.visibility == 3}"><del></c:if>
                            <label for="<c:out value="${item.value.id}"/>">${item.key}</label>
                            <c:if test="${item.value.visibility == 3}"></del></c:if><br/>
                          </c:if>
                        </c:forEach>
                        <br>
                        <c:set var="headerOut" value=""/>
                        <c:forEach var="item" items="${ssAllBinderDefinitions}">
                          <c:if test="${item.value.binderId != -1}">
                            <c:if test="${empty headerOut}"><c:set var="headerOut" value="1"/>
                              <div class="ss_bold" style="border-top: 1px solid #b8b8b8;"><ssf:nlt
                                  tag="definition.local"/></div>
                            </c:if>
                            <input type="radio" name="binderDefinition" value="${item.value.id}"
                                   id="local${item.value.id}"
                            <c:if test="${ssBinder.entryDef.id== item.value.id}"> checked </c:if>
                              <c:out value="${disabled}"/>>
                            <c:if test="${item.value.visibility == 3}"><del></c:if>
                            <label for="local${item.value.id}">${item.key}</label>
                            <c:if test="${item.value.visibility == 3}"></del></c:if><br/>
                          </c:if>
                        </c:forEach>

                        <c:set var="cb_checked" value=""/>
                        <c:if test='<%= forceRenderJsp %>'>
                          <c:set var="cb_checked" value=" checked "/>
                        </c:if>
                        <div style="display:block">
                          <input type="checkbox" name="ss_renderJspView"
                            <c:out value="${cb_checked}"/>
                                 onClick="if (document.configure_jsp_form.ss_renderJspView.checked) document.configure_jsp_form.renderJspView.value='true'; else document.configure_jsp_form.renderJspView.value='false';">

                          &nbsp;
			<span class="ss_labelRight">
				<ssf:nlt tag="__useJspRenderer">
          <ssf:param name="value" value="${productName}"/>
        </ssf:nlt>
			</span>
                        </div>
                        <input type="hidden" name="renderJspView" value='<%= forceRenderJsp %>'/>
                        <br>

                        <c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
                          <input type="submit" class="ss_submit" name="okBtn"
                                 value="<ssf:nlt tag="button.apply" text="Apply"/>">
                        </c:if>
                      </fieldset>
                      <br>
                    </c:if>

                    <c:if test="${ssBinder.entityType == 'folder'}">
                      <fieldset class="ss_fieldset">
                        <legend class="ss_legend"><ssf:nlt tag="binder.configure.allowedViews" text="Allowed views"/>
                          <ssf:showHelp guideName="adv_user" pageId="mngfolder_view"
                                        sectionId="mngfolder_view_alternate"/></legend>

                        <c:set var="folderViewCount" value=""/>
                        <c:forEach var="item" items="${ssAllBinderDefinitions}">
                          <c:if test="${item.value.binderId == -1}">
                            <input type="checkbox" name="binderDefinitions" value="${item.value.id}"
                                   id="all_${item.value.id}"
                            <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked
                              <c:set var="folderViewCount" value="1"/>
                            </c:if>
                              <c:out value="${disabled}"/>><c:if test="${item.value.visibility == 3}"><del></c:if>
                            <label for="all_${item.value.id}">${item.key}</label>
                            <c:if test="${item.value.visibility == 3}"></del></c:if><br/>
                          </c:if>
                        </c:forEach>
                        <br>
                        <c:set var="headerOut" value="0"/>
                        <c:forEach var="item" items="${ssAllBinderDefinitions}">
                          <c:if test="${item.value.binderId != -1}">
                            <c:if test="${headerOut == '0'}"><c:set var="headerOut" value="1"/>
                              <div class="ss_bold" style="border-top: 1px solid #b8b8b8;"><ssf:nlt
                                  tag="definition.local"/></div>
                            </c:if>
                            <input type="checkbox" name="binderDefinitions" value="${item.value.id}"
                                   id="all2_${item.value.id}"
                            <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked
                              <c:set var="folderViewCount" value="1"/>
                            </c:if>
                              <c:out value="${disabled}"/>> <c:if test="${item.value.visibility == 3}"><del></c:if>
                            <label for="all2_${item.value.id}">${item.key}</label>
                            <c:if test="${item.value.visibility == 3}"></del></c:if><br/>
                          </c:if>
                        </c:forEach>
                        <c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
                          <input type="submit" class="ss_submit" name="okBtn"
                                 value="<ssf:nlt tag="button.apply" text="Apply"/>">
                        </c:if>
                      </fieldset>
                      <br>

                      <fieldset class="ss_fieldset">
                        <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultView" text="Default view"/>
                          <ssf:showHelp guideName="adv_user" pageId="mngfolder_view"
                                        sectionId="mngfolder_view_default"/></legend>

                        <c:if test="${!empty folderViewCount}">
                          <c:forEach var="item" items="${ssAllBinderDefinitions}">
                            <c:if test="${!empty allDefinitionsMap[item.value.id]}">
                              <input type="radio" name="binderDefinition" value="${item.value.id}"
                                     id="all3_${item.value.id}"
                              <c:if test="${ssBinder.entryDef.id == item.value.id}"> checked </c:if> <c:out
                                  value="${disabled}"/>>
                              <c:if test="${item.value.visibility == 3}"><del></c:if>
                              <label for="all3_${item.value.id}">${item.key}</label>
                              <c:if test="${item.value.visibility == 3}"></del></c:if><br/>
                            </c:if>
                          </c:forEach>
                          <br>
                        </c:if>

                        <c:set var="cb_checked" value=""/>
                        <c:if test='<%= forceRenderJsp %>'>
                          <c:set var="cb_checked" value=" checked "/>
                        </c:if>
                        <div style="display:block">
                          <input type="checkbox" name="ss_renderJspView"
                            <c:out value="${cb_checked}"/>
                                 onClick="if (document.configure_jsp_form.ss_renderJspView.checked) document.configure_jsp_form.renderJspView.value='true'; else document.configure_jsp_form.renderJspView.value='false';">

                          &nbsp;
			<span class="ss_labelRight">
				<ssf:nlt tag="__useJspRenderer">
          <ssf:param name="value" value="${productName}"/>
        </ssf:nlt>
			</span>
                        </div>
                        <input type="hidden" name="renderJspView" value='<%= forceRenderJsp %>'/>
                        <br>

                        <c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
                          <input type="submit" class="ss_submit" name="okBtn"
                                 value="<ssf:nlt tag="button.apply" text="Apply"/>">
                        </c:if>
                      </fieldset>
                      <br>

                      <fieldset class="ss_fieldset">
                        <legend class="ss_legend"><ssf:nlt tag="binder.configure.defaultEntryTypes"
                                                           text="Default entry types"/> <ssf:showHelp
                            guideName="adv_user" pageId="mngfolder_entrytype"
                            sectionId="mngfolder_entrytype_enablealternate"/></legend>

                        <c:forEach var="item" items="${ssAllEntryDefinitions}">
                          <c:if test="${item.value.binderId == -1}">
                            <input type="checkbox" name="entryDefinition" value="${item.value.id}"
                                   id="all4_${item.value.id}"
                            <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
                              <c:out value="${disabled}"/>> <c:if test="${item.value.visibility == 3}"><del></c:if>
                            <label for="all4_${item.value.id}">${item.key}</label>
                            <c:if test="${item.value.visibility == 3}"></del></c:if><br/>
                          </c:if>
                        </c:forEach>
                        <br>
                        <c:set var="headerOut" value=""/>
                        <c:forEach var="item" items="${ssAllEntryDefinitions}">
                          <c:if test="${item.value.binderId != -1}">
                            <c:if test="${empty headerOut}"><c:set var="headerOut" value="1"/>
                              <div class="ss_bold" style="border-top: 1px solid #b8b8b8;"><ssf:nlt
                                  tag="definition.local"/></div>
                              <br/>
                            </c:if>
                            <input type="checkbox" name="entryDefinition" value="${item.value.id}"
                                   id="all5_${item.value.id}"
                            <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
                              <c:out value="${disabled}"/>> <c:if test="${item.value.visibility == 3}"><del></c:if>
                            <label for="all5_${item.value.id}">${item.key}</label>
                            <c:if test="${item.value.visibility == 3}"></del></c:if><br/>
                          </c:if>
                        </c:forEach>
                        <br>

                        <c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
                          <input type="submit" class="ss_submit" name="okBtn"
                                 value="<ssf:nlt tag="button.apply" text="Apply"/>">
                        </c:if>
                      </fieldset>
                      <br>

                      <% //only display if have workflows - which covers the case where workflow is not supported %>
                      <c:if test="${!empty ssAllWorkflowDefinitions}">
                        <fieldset class="ss_fieldset">
                          <legend class="ss_legend"><ssf:nlt tag="binder.configure.workflowAssociations"
                                                             text="Workflow associations"/> <ssf:showHelp
                              guideName="adv_user" pageId="workflow_enable"
                              sectionId="workflow_enable_created"/></legend>

                          <table>
                            <tr>
                              <th><span class="ss_bold"><ssf:nlt tag="workflow.type.entry"/></span></th>
                            </tr>
                            <c:forEach var="item" items="${ssAllEntryDefinitions}">
                              <c:if test="${!empty allDefinitionsMap[item.value.id]}">
                                <tr>
                                  <td>
                                    <c:if test="${item.value.visibility == 3}">
                                    <del></c:if>
                                      ${item.key}<c:if test="${item.value.visibility == 3}"></del>
                                    </c:if></td>
                                  <td>
                                    <select name="workflow_<c:out value="${item.value.id}"/>" <c:out
                                        value="${disabled}"/>>
                                      <option value=""><ssf:nlt tag="common.select.none" text="--none--"/></option>
                                      <c:forEach var="wfp" items="${ssAllWorkflowDefinitions}">
                                        <c:set var="ss_sup" value=""/>
                                        <c:if test="${wfp.value.binderId != -1}"><c:set var="ss_sup"
                                                                                        value="&#134;"/></c:if>
                                        <c:if test="${ssBinder.workflowAssociations[item.value.id] eq wfp.value}">
                                          <option value="<c:out value="${wfp.value.id}"/>" selected>
                                            <ssf:nlt tag="${wfp.value.title}"
                                                     checkIfTag="true"/>(${wfp.value.name})${ss_sup}</option>
                                        </c:if>
                                        <c:if test="${ssBinder.workflowAssociations[item.value.id] != wfp.value}">
                                          <option value="<c:out value="${wfp.value.id}"/>">
                                            <ssf:nlt tag="${wfp.value.title}"
                                                     checkIfTag="true"/>(${wfp.value.name})${ss_sup}</option>
                                        </c:if>
                                      </c:forEach>
                                    </select>
                                  </td>
                                </tr>
                              </c:if>
                            </c:forEach>
                          </table>

                          <table>
                            <tr>
                              <th><span class="ss_bold"><ssf:nlt tag="workflow.type.reply"/></span></th>
                            </tr>
                            <c:forEach var="item" items="${ssAllEntryDefinitions}">
                              <c:if test="${!empty ssReplyDefinitionMap[item.value.id]}">
                                <tr>
                                  <td>
                                    <c:if test="${item.value.visibility == 3}">
                                    <del></c:if>
                                      ${item.key}<c:if test="${item.value.visibility == 3}"></del>
                                    </c:if></td>
                                  <td>
                                    <select name="workflow_<c:out value="${item.value.id}"/>" <c:out
                                        value="${disabled}"/>>
                                      <option value=""><ssf:nlt tag="common.select.none" text="--none--"/></option>
                                      <c:forEach var="wfp" items="${ssAllWorkflowDefinitions}">
                                        <c:set var="ss_sup" value=""/>
                                        <c:if test="${wfp.value.binderId != -1}"><c:set var="ss_sup"
                                                                                        value="&#134;"/></c:if>
                                        <c:if test="${ssBinder.workflowAssociations[item.value.id] eq wfp.value}">
                                          <option value="<c:out value="${wfp.value.id}"/>" selected>
                                            <ssf:nlt tag="${wfp.value.title}"
                                                     checkIfTag="true"/>(${wfp.value.name})${ss_sup}</option>
                                        </c:if>
                                        <c:if test="${ssBinder.workflowAssociations[item.value.id] != wfp.value}">
                                          <option value="<c:out value="${wfp.value.id}"/>">
                                            <ssf:nlt tag="${wfp.value.title}"
                                                     checkIfTag="true"/>(${wfp.value.name})${ss_sup}</option>
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
                            <input type="submit" class="ss_submit" name="okBtn"
                                   value="<ssf:nlt tag="button.apply" text="Apply"/>">
                          </c:if>
                        </fieldset>

                        <fieldset class="ss_fieldset">
                          <legend class="ss_legend"><ssf:nlt tag="binder.configure.allowedWorkflows"
                                                             text="Allowed workflows"/> <ssf:showHelp
                              guideName="adv_user" pageId="workflow_enable"
                              sectionId="workflow_enable_manual"/></legend>

                          <c:forEach var="item" items="${ssAllWorkflowDefinitions}">
                            <c:if test="${item.value.binderId == -1}">
                              <input type="checkbox" name="workflowDefinition" value="<c:out value="${item.value.id}"/>"
                              <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
                                <c:out value="${disabled}"/>>
                              <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if
                                test="${item.value.visibility == 3}"></del></c:if><br/>
                            </c:if>
                          </c:forEach>
                          <br>
                          <c:set var="headerOut" value=""/>
                          <c:forEach var="item" items="${ssAllWorkflowDefinitions}">
                            <c:if test="${item.value.binderId != -1}">
                              <c:if test="${empty headerOut}"><c:set var="headerOut" value="1"/>
                                <div class="ss_bold" style="border-top: 1px solid #b8b8b8;"><ssf:nlt
                                    tag="definition.local"/></div>
                              </c:if>
                              <input type="checkbox" name="workflowDefinition" value="<c:out value="${item.value.id}"/>"
                              <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
                                <c:out value="${disabled}"/>>
                              <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if
                                test="${item.value.visibility == 3}"></del></c:if><br/>
                            </c:if>
                          </c:forEach>
                          <br>


                          <c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
                            <input type="submit" class="ss_submit" name="okBtn"
                                   value="<ssf:nlt tag="button.apply" text="Apply"/>">
                          </c:if>
                        </fieldset>
                      </c:if>

                    </c:if>

                    <c:if test="${ssBinder.entityType == 'profiles'}">
                      <fieldset class="ss_fieldset">
                        <legend class="ss_legend"><ssf:nlt tag="binder.configure.profileView"
                                                           text="Profile listing"/></legend>

                        <c:forEach var="item" items="${ssAllBinderDefinitions}">
                          <input type="radio" name="binderDefinitions" value="<c:out value="${item.value.id}"/>"
                          <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
                            <c:out value="${disabled}"/>>
                          <c:if test="${item.value.visibility == 3}"><del></c:if>${item.key}<c:if
                            test="${item.value.visibility == 3}"></del></c:if><br/>
                        </c:forEach>
                        <br>
                        <c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
                          <input type="submit" class="ss_submit" name="okBtn"
                                 value="<ssf:nlt tag="button.apply" text="Apply"/>">
                        </c:if>
                      </fieldset>
                      <br>

                      <fieldset class="ss_fieldset">
                        <legend class="ss_legend"><ssf:nlt tag="binder.configure.profileEntryType"
                                                           text="Profile type"/></legend>

                        <c:forEach var="item" items="${ssAllEntryDefinitions}">
                          <input type="checkbox" name="entryDefinition" value="${item.value.id}"
                                 id="all6_${item.value.id}"
                          <c:if test="${!empty allDefinitionsMap[item.value.id]}"> checked </c:if>
                            <c:out value="${disabled}"/>> <c:if test="${item.value.visibility == 3}"><del></c:if>
                          <label for="all6_${item.value.id}">${item.key}</label>
                          <c:if test="${item.value.visibility == 3}"></del></c:if><br/>
                        </c:forEach>
                        <br/>
                        <c:if test="${!ssBinder.definitionInheritanceSupported || !ssBinder.definitionsInherited}">
                          <input type="submit" class="ss_submit" name="okBtn"
                                 value="<ssf:nlt tag="button.apply" text="Apply"/>">
                        </c:if>
                      </fieldset>

                    </c:if>

                    <div class="ss_formBreak"></div>

                    <div class="ss_buttonBarRight margintop3">
                      <input type="submit" class="ss_submit" name="closeBtn"
                             value="<ssf:nlt tag="button.close" text="Close"/>">
                    </div>

                  </form>

                </td>
              </tr>
            </table>
          </c:if>
        </div>
      </div>
    </div>

  </div>
</div>

<ssf:ifadapter>
  </body>
  </html>
</ssf:ifadapter>

