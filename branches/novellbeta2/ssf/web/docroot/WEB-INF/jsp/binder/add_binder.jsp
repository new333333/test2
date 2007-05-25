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
var ss_checkTitleUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_binder_title" />
	</ssf:url>";
ss_addValidator("ss_titleCheck", ss_ajax_result_validator);
 </script>

<div class="ss_portlet">
<br/>

<form class="ss_style ss_form" 
  id="<portlet:namespace/>fm" 
  method="post" onSubmit="return ss_onSubmit(this);">
<span class="ss_bold">
  <c:if test="${ssOperation == 'add_workspace'}">
<ssf:nlt tag="binder.add.workspace.title"><ssf:param name="value" value="${ssBinder.pathName}"/>
</ssf:nlt>
</c:if>
<c:if test="${ssOperation != 'add_workspace'}">
<ssf:nlt tag="binder.add.folder.title"><ssf:param name="value" value="${ssBinder.pathName}"/>
</ssf:nlt>
</c:if>

</span></br></br>
  
	<span class="ss_labelLeft" id="title_label"><ssf:nlt tag="folder.label.title" text="Title"/></span>
    <div class="needed-because-of-ie-bug"><div id="ss_titleCheck" style="display:none; visibility:hidden;" ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
	<input type="text" class="ss_text" size="70" name="title" id="title" onchange="ss_ajaxValidate(ss_checkTitleUrl, this,'title_label', 'ss_titleCheck');"><br/><br/>
  <span class="ss_bold"><ssf:nlt tag="binder.add.binder.select.config"/></span> <ssf:inlineHelp tag="ihelp.other.select_template"/>
  <br/>
  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
      <input type="radio" name="binderConfigId" value="${config.id}" 
      <c:if test="${status.count == 1}">checked="checked"</c:if>
      ><ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/><br/>
  </c:forEach>
<br/>  

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onClick="ss_buttonSelect('okBtn');">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="ss_buttonSelect('cancelBtn');">

</form>
</div>

