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
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
//Routine called when "Add entry" is clicked
function ss_selectEntryId${ss_mashupItemId}_${renderResponse.namespace}(id) {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "entry,entryId="+id;
}
function ss_selectFolderId${ss_mashupItemId}_${renderResponse.namespace}(id) {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "folder,folderId="+id;
}
function ss_mashup_addTable${ss_mashupItemId}_${renderResponse.namespace}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "table";
}
function ss_mashup_addCustomJsp${ss_mashupItemId}_${renderResponse.namespace}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "customJsp";
}
function ss_mashupShowAddDiv${ss_mashupItemId}_${renderResponse.namespace}(obj) {
	var divObj = document.getElementById('ss_mashupAddDiv_${ss_mashupItemId}_${renderResponse.namespace}');
	divObj.style.top = parseInt(ss_getObjectTop(obj) + 20) + "px";
	divObj.style.left = parseInt(ss_getObjectLeft(obj) + 10) + "px";
	divObj.style.display = 'block';
	ss_mashupClearAttrs${ss_mashupItemId}();
}
function ss_mashupHideAddDiv${ss_mashupItemId}_${renderResponse.namespace}() {
	var divObj = document.getElementById('ss_mashupAddDiv_${ss_mashupItemId}_${renderResponse.namespace}');
	divObj.style.display = 'none';
	ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();
}

var ss_mashupShowAddTypeDivObj${ss_mashupItemId}_${renderResponse.namespace} = null;
function ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(obj, type) {
	ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();
	var divObj = document.getElementById('ss_mashupAdd'+type+'Div_${ss_mashupItemId}_${renderResponse.namespace}');
	ss_mashupShowAddTypeDivObj${ss_mashupItemId}_${renderResponse.namespace} = divObj;
	divObj.style.top = parseInt(ss_getObjectTop(obj) + 20) + "px";
	divObj.style.left = parseInt(ss_getObjectLeft(obj) + ss_getObjectWidth(obj) + 12) + "px";
	divObj.style.display = 'block';
	ss_mashupClearAttrs${ss_mashupItemId}();
}

function ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}() {
	if (ss_mashupShowAddTypeDivObj${ss_mashupItemId}_${renderResponse.namespace} != null) {
		ss_mashupShowAddTypeDivObj${ss_mashupItemId}_${renderResponse.namespace}.style.display = 'none';
		ss_mashupShowAddTypeDivObj${ss_mashupItemId}_${renderResponse.namespace} = null;
	}
}

//Mashup attributes
var ss_mashupAttr_noTitle${ss_mashupItemId} = "";
var ss_mashupAttr_showFolderDescription${ss_mashupItemId} = "";
var ss_mashupAttr_showEntriesOpened${ss_mashupItemId} = "";
var ss_mashupAttr_numberOfLines${ss_mashupItemId} = "";
var ss_mashupAttr_customJsp${ss_mashupItemId} = "";

function ss_mashupClearAttrs${ss_mashupItemId}() {
	ss_mashupAttr_noTitle${ss_mashupItemId} = "";
	ss_mashupAttr_showFolderDescription${ss_mashupItemId} = "";
	ss_mashupAttr_showEntriesOpened${ss_mashupItemId} = "";
	ss_mashupAttr_numberOfLines${ss_mashupItemId} = "";
	ss_mashupAttr_customJsp${ss_mashupItemId} = "";
}
function ss_mashupBuildAttrs${ss_mashupItemId}() {
	var attr = "";
	if (ss_mashupAttr_noTitle${ss_mashupItemId} != "") attr += ",noTitle=1"
	if (ss_mashupAttr_showFolderDescription${ss_mashupItemId} != "") attr += ",showFolderDescription=1"
	if (ss_mashupAttr_showEntriesOpened${ss_mashupItemId} != "") attr += ",showEntriesOpened=1"
	if (ss_mashupAttr_numberOfLines${ss_mashupItemId} != "") 
		attr += ",entriesToShow=" + ss_mashupAttr_numberOfLines${ss_mashupItemId}
	if (ss_mashupAttr_customJsp${ss_mashupItemId} != "") 
		attr += ",customJsp=" + ss_mashupAttr_customJsp${ss_mashupItemId}
	//alert(attr)
	return attr;
}
function ss_mashupSubmit${ss_mashupItemId}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value += ss_mashupBuildAttrs${ss_mashupItemId}();
	//alert(formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value)
}

</script>
<div>
  <div>
	<a class="ss_linkButton ss_fineprint" href="javascript: ;"
	  onClick="ss_mashupShowAddDiv${ss_mashupItemId}_${renderResponse.namespace}(this);return false"
	><ssf:nlt tag="button.add"/>...</a>
	
	<div id="ss_mashupAddDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:400;
	  		padding:10px;" 
	>
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'Entry');return false;"
		><ssf:nlt tag="mashup.addEntry"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'Folder');return false;"
		><ssf:nlt tag="mashup.addFolder"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'CustomJsp');return false;"
		><ssf:nlt tag="mashup.addCustomJsp"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'Table');return false;"
		><ssf:nlt tag="mashup.addTable"/></a>
		
		<div style="padding-top:10px;">
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
    </div>
  </div>
	
	
	
  <div id="ss_mashupAddEntryDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:401;
	  	padding:10px;" 
  >
  		<div><ssf:nlt tag="mashup.selectEntry"/></div>
  		<div>
  		  <ssf:find 
    		type="entries"
    		width="140px" 
    		singleItem="true"
		    clickRoutine="ss_selectEntryId${ss_mashupItemId}_${renderResponse.namespace}"
		    accessibilityText="wiki.findPage"
		    />
          <br/>
          <input type="checkbox" name="${ss_mashupPropertyName}__noTitle"
            onChange="ss_mashupAttr_noTitle${ss_mashupItemId} = this.value;"/> 
          <span><ssf:nlt tag="mashup.noTitle"/></span>
		  <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    onClick="ss_mashupSubmit${ss_mashupItemId}();return true;"
		    class="ss_linkButton ss_fineprint" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>
	
  <div id="ss_mashupAddFolderDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:401;
	  	padding:10px;" 
  >
  		<div><ssf:nlt tag="mashup.selectFolder"/></div>
  		<div>
  		  <ssf:find 
    		type="places"
    		width="140px" 
    		singleItem="true"
    		foldersOnly="true"
		    clickRoutine="ss_selectFolderId${ss_mashupItemId}_${renderResponse.namespace}"
		    accessibilityText="wiki.findFolder"
		    />
          <br/>
          <input type="checkbox" name="${ss_mashupPropertyName}__noTitle"
            onChange="ss_mashupAttr_noTitle${ss_mashupItemId} = this.value;"/> 
          <span><ssf:nlt tag="mashup.noTitle"/></span>
          <br/>
          <input type="checkbox" name="${ss_mashupPropertyName}__showFolderDescription"
            onChange="ss_mashupAttr_showFolderDescription${ss_mashupItemId} = this.value;"/> 
          <span><ssf:nlt tag="mashup.showFolderDescription"/></span>
          <br/>
          <input type="checkbox" name="${ss_mashupPropertyName}__showEntriesOpened"
            onChange="ss_mashupAttr_showEntriesOpened${ss_mashupItemId} = this.value;"/> 
          <span><ssf:nlt tag="mashup.showEntriesOpened"/></span>
          <br/>
          <input type="text" name="${ss_mashupPropertyName}__numberOfLines"
            onChange="if (ss_isInteger(this.value)) {ss_mashupAttr_numberOfLines${ss_mashupItemId} = this.value;}"/> 
          <span><ssf:nlt tag="mashup.numberOfLines"/></span>
          <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    onClick="ss_mashupSubmit${ss_mashupItemId}();return true;"
		    class="ss_linkButton ss_fineprint" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>
		
  <div id="ss_mashupAddTableDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:401;
	  	padding:10px;" 
  >
  		<div><ssf:nlt tag="mashup.addTable"/></div>
  		<div>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    class="ss_linkButton ss_fineprint"
			onClick="ss_mashup_addTable${ss_mashupItemId}_${renderResponse.namespace}();ss_mashupSubmit${ss_mashupItemId}();return true;" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>

  <div id="ss_mashupAddCustomJspDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:401;
	  	padding:10px;" 
  >
  		<div><ssf:nlt tag="mashup.addCustomJsp"/></div>
  		<div>
          <span class="ss_labelBefore"><ssf:nlt tag="mashup.customJspName"/></span>
          <input type="text" name="${ss_mashupPropertyName}__customJsp" size="20"
            onChange="ss_mashupAttr_customJsp${ss_mashupItemId} = this.value;"/> 
          <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    class="ss_linkButton ss_fineprint"
			onClick="ss_mashup_addCustomJsp${ss_mashupItemId}_${renderResponse.namespace}();ss_mashupSubmit${ss_mashupItemId}();return true;" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>

</div>
<input type="hidden" name="${ss_mashupPropertyName}__${ss_mashupItemId}"/>

