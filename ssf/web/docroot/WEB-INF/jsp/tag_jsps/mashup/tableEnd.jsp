<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% //table2_col2 top %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	if (!ss_mashupTableItemCount.containsKey(ss_mashupTableNumber)) 
		ss_mashupTableItemCount.put(ss_mashupTableNumber, "");
%>
<c:if test="${ssConfigJspStyle == 'form'}">
	<script type="text/javascript">
	//Routine called when "Delete table" is clicked
	function ss_mashup_deleteTable${ss_mashupItemId}_${renderResponse.namespace}() {
		if ('${ss_mashupTableItemCount[ss_mashupTableNumber]}' != '') {
			alert('<ssf:nlt tag="mashup.tableNotEmpty"/>');
			return false;
		}
		var formObj = self.document.forms['${ss_form_form_formName}'];
		formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "tableEnd_delete";
		return true;
	}
	</script>
</c:if>

</td>
</tr>
</table>
<c:if test="${ssConfigJspStyle == 'form'}">
  <div style="padding-bottom:10px;">
  <input type="submit" name="applyBtn" value="<ssf:nlt tag="mashup.deleteTable"/>" 
    class="ss_linkButton ss_fineprint"
    onClick="return ss_mashup_deleteTable${ss_mashupItemId}_${renderResponse.namespace}();"/>
  </div>
</c:if>
<%
	ss_mashupTableNumber = ss_mashupTableNumber - 1;
	request.setAttribute("ss_mashupTableNumber", ss_mashupTableNumber);
%>
