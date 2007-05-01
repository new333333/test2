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
<ssf:ifadapter>
<body>
<div id='ss_validation_errors_div' style="position:absolute; display:none;"><p><ssf:nlt tag="validation.errorMessage"/></p><input type="button" onclick="ss_cancelPopupDiv('ss_validation_errors_div')" name="<ssf:nlt tag="button.close"/>" value="<ssf:nlt tag="button.close"/>"/></div>
</ssf:ifadapter>

<table class="ss_style" cellpadding="10" width="100%"><tr><td>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}"
  processThisItem="true" />
</td></tr></table>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
