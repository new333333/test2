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
<body onLoad="ss_positionWindow();">
<script type="text/javascript">
var ss_scrollHeightFudge = 60
function ss_positionWindow() {
    var entryHeight = parseInt(self.document.body.scrollHeight) + ss_scrollHeightFudge
    window.innerHeight = entryHeight;
}
</script>
</ssf:ifadapter>

<form method="post">
<table class="ss_style" cellpadding="10" width="100%"><tr><td>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}"
  entry="${ssEntry}"
  processThisItem="true" />
</td></tr></table>
<br/>
<input type="submit" name="editElementBtn" value="<ssf:nlt tag="button.ok"/>"/>&nbsp;&nbsp;&nbsp;<input
  type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="window.close();return false;"/>
</form>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

