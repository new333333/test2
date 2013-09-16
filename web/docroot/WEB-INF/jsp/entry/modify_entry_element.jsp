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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="tundra">
<script type="text/javascript">
var ss_scrollHeightFudge = 60
function ss_positionWindow() {
    var entryHeight = parseInt(self.document.body.scrollHeight) + ss_scrollHeightFudge
    window.innerHeight = entryHeight;
}
ss_createOnLoadObj("ss_positionWindow", ss_positionWindow);
ss_createOnResizeObj("ss_setEditableSize", ss_setEditableSize);
</script>
</ssf:ifadapter>

<div class="ss_popup_wrapper" style="font-family: Arial, Helvetica, sans-serif;">
<form method="post">
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}"
  entry="${ssEntry}"
  processThisItem="true" >
  <ssf:param name="ss_sectionNumber" value="${ss_sectionNumber}"/>
  <ssf:param name="ss_sectionText" value="${ss_sectionText}"/>
</ssf:displayConfiguration>
	<div class="margintop3" style="text-align: right;">
		<input type="submit" name="editElementBtn" value="<ssf:nlt tag="button.ok"/>"/>
		<input
		  type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" 
		  onClick="ss_cancelButtonCloseWindow();return false;"/>
		</form>
	</div>
</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

