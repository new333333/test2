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
<% //list end %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	Map ss_mashupTableItemCount2 = (Map) request.getAttribute("ss_mashupTableItemCount2");
	Long mashupTableNumber = (Long) ss_mashupTableItemCount2.get(ss_mashupTableDepth);
	String mashupTableValue = "";
	mashupTableValue = (String) ss_mashupTableItemCount.get(mashupTableNumber);
	if (mashupTableValue == null) mashupTableValue = "";

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
%>
<c:if test="${ssConfigJspStyle == 'form'}">
	<script type="text/javascript">
	//Routine called when "Delete list" is clicked
	function ss_mashup_deleteList${ss_mashupItemId}_${renderResponse.namespace}() {
		if ('<%= mashupTableValue %>' != '') {
			alert('<ssf:nlt tag="mashup.listNotEmpty"/>');
			return false;
		}
		var formObj = self.document.forms['${ss_form_form_formName}'];
		formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "listEnd_delete";
		return true;
	}
	</script>
</c:if>

  </ul>
  </div>
  <div class="ss_mashup_round_bottom"><div></div></div>
<c:if test="${ssConfigJspStyle == 'form'}">
  <div style="padding-bottom:10px;">
  <input type="submit" name="applyBtn" value="<ssf:nlt tag="mashup.deleteList"/>" 
    class="ss_linkButton ss_fineprint"
    onClick="return ss_mashup_deleteList${ss_mashupItemId}_${renderResponse.namespace}();"/>
  </div>
</c:if>
</div>

<%
	ss_mashupTableDepth = ss_mashupTableDepth - 1;
	request.setAttribute("ss_mashupTableDepth", ss_mashupTableDepth);
	
	ss_mashupListDepth = ss_mashupListDepth - 1;
	request.setAttribute("ss_mashupListDepth", ss_mashupListDepth);
%>
<% if (ss_mashupListDepth > 0) { %>
</li>
<% } %>
