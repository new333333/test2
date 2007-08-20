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
<%@ page import="java.util.ArrayList" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">
function ss_numbers_only(evt)
  {
     var charCode = (evt.which) ? evt.which : event.keyCode
     if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;

     return true;
  }
</script>
<c:set var="formName"><ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm</c:set>
<script type="text/javascript">
var ssReportURL="<portlet:actionURL windowState="maximized"><portlet:param 
	name="action" value="quota_report"/><portlet:param 
	name="binderId" value="${ssBinder.id}"/><portlet:param 
	name="binderType" value="${ssBinder.entityType}"/></portlet:actionURL>";
</script>

<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<ssf:url webPath="reportDownload"/>" 
	method="post" 
	name="${formName}">
<input type="hidden" name="ss_reportType" value="quota"/>
<div class="ss_buttonBarRight">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm.action=ssReportURL; document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm.submit()">
</div>
   <br/>
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.quota"/></span>
   <br/>
   <br/>
	<div>
	<input type="radio" name="ssQuotaOption" value="UsersOnly" id="usersOnlyQuota" checked="checked"/><label for="usersOnlyQuota"><span><ssf:nlt tag="administration.report.quota.option.usersOnly"/></span></label><br/>
	<input type="radio" name="ssQuotaOption" value="WorkspacesOnly" id="workspacesOnlyQuota"/><label for="workspacesOnlyQuota"><span><ssf:nlt tag="administration.report.quota.option.workspacesOnly"/></span></label><br/>
	<input type="radio" name="ssQuotaOption" value="UsersAndWorkspaces" id="bothQuota"/><label for="bothQuota"><span><ssf:nlt tag="administration.report.quota.option.both"/></span></label><br/>
	</div>
	<div>
	<label for="quotaThreshold"><span><ssf:nlt tag="administration.report.quota.threshold"/></span></label><input type="text" name="ssQuotaThreshold" id="quotaThreshold" value="0" onkeypress="return ss_numbers_only(event)"/>
	</div>
   <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onclick="document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm.action=ssReportURL; document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm.submit()">
   </div>
</form>
<br>
</td></tr></table>
