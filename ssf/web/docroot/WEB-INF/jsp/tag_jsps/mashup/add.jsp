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
<% //Add entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<script type="text/javascript">
//Routine called when "Add entry" is clicked
function ss_selectEntryId${ss_mashupItemId}_${renderResponse.namespace}(id) {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "entry,"+id;
}
function ss_mashup_addTable${ss_mashupItemId}_${renderResponse.namespace}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "table";
}
function ss_mashupShowAddDiv${ss_mashupItemId}_${renderResponse.namespace}(obj) {
	var divObj = document.getElementById('ss_mashupAddDiv_${ss_mashupItemId}_${renderResponse.namespace}');
	divObj.style.top = parseInt(ss_getObjectTop(obj) + 20) + "px";
	divObj.style.left = parseInt(ss_getObjectLeft(obj) + 10) + "px";
	divObj.style.display = 'block';
}
function ss_mashupHideAddDiv${ss_mashupItemId}_${renderResponse.namespace}() {
	var divObj = document.getElementById('ss_mashupAddDiv_${ss_mashupItemId}_${renderResponse.namespace}');
	divObj.style.display = 'none';
}
</script>
<div>
	<div>
	<a class="ss_linkButton ss_fineprint" href="javascript: ;"
	  onClick="ss_mashupShowAddDiv${ss_mashupItemId}_${renderResponse.namespace}(this);return false"
	><ssf:nlt tag="button.add"/>...</a>
	
	<div id="ss_mashupAddDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:500;" 
	>
  		<table><tr><td valign="top"><ssf:nlt tag="mashup.addEntry"/></td>
  		  <td><ssf:find 
    		type="entries"
    		width="140px" 
    		singleItem="true"
		    clickRoutine="ss_selectEntryId${ss_mashupItemId}_${renderResponse.namespace}"
		    accessibilityText="wiki.findPage"
		    /> <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" />
		  </td></tr></table>
		
  		<table><tr><td valign="top"><ssf:nlt tag="mashup.addTable"/></td>
		  <td><input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn"
			onClick="ss_mashup_addTable${ss_mashupItemId}_${renderResponse.namespace}();return true;" />
			<input type="hidden" name="${ss_mashupPropertyName}__${ss_mashupItemId}"/>
		  </td></tr></table>

	  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_link" 
	    onClick="ss_mashupHideAddDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
	</div>
	</div>
</div>

