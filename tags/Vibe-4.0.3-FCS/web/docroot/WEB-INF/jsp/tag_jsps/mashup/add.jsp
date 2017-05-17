<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
function ss_selectBinderId${ss_mashupItemId}_${renderResponse.namespace}(id) {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "binderUrl,binderId="+id;
}
function ss_selectEntryUrlId${ss_mashupItemId}_${renderResponse.namespace}(id) {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "entryUrl,entryId="+id;
}
function ss_mashup_addTable${ss_mashupItemId}_${renderResponse.namespace}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "table";
}
function ss_mashup_addList${ss_mashupItemId}_${renderResponse.namespace}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "list";
}
function ss_mashup_addGraphic${ss_mashupItemId}_${renderResponse.namespace}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "graphic";
}
function ss_mashup_addUtility${ss_mashupItemId}_${renderResponse.namespace}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "utility";
}
function ss_mashup_addCustomJsp${ss_mashupItemId}_${renderResponse.namespace}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "customJsp";
}
function ss_mashup_addUrl${ss_mashupItemId}_${renderResponse.namespace}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "url";
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
var ss_mashupAttr_showTitle${ss_mashupItemId} = false;
var ss_mashupAttr_showBorder${ss_mashupItemId} = false;
var ss_mashupAttr_showFolderDescription${ss_mashupItemId} = false;
var ss_mashupAttr_showEntriesOpened${ss_mashupItemId} = false;
var ss_mashupAttr_numberOfLines${ss_mashupItemId} = "";
var ss_mashupAttr_numberOfCols${ss_mashupItemId} = "";
var ss_mashupAttr_columnWidths${ss_mashupItemId} = "";
var ss_mashupAttr_graphic${ss_mashupItemId} = "";
var ss_mashupAttr_graphicTitle${ss_mashupItemId} = "";
var ss_mashupAttr_listTitle${ss_mashupItemId} = "";
var ss_mashupAttr_customJsp${ss_mashupItemId} = "";
var ss_mashupAttr_binderUrlTitle${ss_mashupItemId} = "";
var ss_mashupAttr_binderUrlPopup${ss_mashupItemId} = false;
var ss_mashupAttr_entryUrlTitle${ss_mashupItemId} = "";
var ss_mashupAttr_entryUrlPopup${ss_mashupItemId} = false;
var ss_mashupAttr_urlTitle${ss_mashupItemId} = "";
var ss_mashupAttr_urlHref${ss_mashupItemId} = "";
var ss_mashupAttr_urlPopup${ss_mashupItemId} = false;
var ss_mashupAttr_utilityElement${ss_mashupItemId} = "";

function ss_mashupClearAttrs${ss_mashupItemId}() {
	ss_mashupAttr_showTitle${ss_mashupItemId} = false;
	ss_mashupAttr_showBorder${ss_mashupItemId} = false;
	ss_mashupAttr_showFolderDescription${ss_mashupItemId} = false;
	ss_mashupAttr_showEntriesOpened${ss_mashupItemId} = false;
	ss_mashupAttr_numberOfLines${ss_mashupItemId} = "";
	ss_mashupAttr_numberOfCols${ss_mashupItemId} = "";
	ss_mashupAttr_columnWidths${ss_mashupItemId} = "";
	ss_mashupAttr_graphic${ss_mashupItemId} = "";
	ss_mashupAttr_graphicTitle${ss_mashupItemId} = "";
	ss_mashupAttr_listTitle${ss_mashupItemId} = "";
	ss_mashupAttr_customJsp${ss_mashupItemId} = "";
	ss_mashupAttr_binderUrlTitle${ss_mashupItemId} = "";
	ss_mashupAttr_binderUrlPopup${ss_mashupItemId} = false;
	ss_mashupAttr_entryUrlTitle${ss_mashupItemId} = "";
	ss_mashupAttr_entryUrlPopup${ss_mashupItemId} = false;
	ss_mashupAttr_urlTitle${ss_mashupItemId} = "";
	ss_mashupAttr_urlHref${ss_mashupItemId} = "";
	ss_mashupAttr_urlPopup${ss_mashupItemId} = false;
	ss_mashupAttr_utilityElement${ss_mashupItemId} = "";
}
function ss_mashupBuildAttrs${ss_mashupItemId}() {
	var attr = "";
	if (ss_mashupAttr_showTitle${ss_mashupItemId}) attr += ",showTitle=1";
	if (ss_mashupAttr_showBorder${ss_mashupItemId}) attr += ",showBorder=1";
	if (ss_mashupAttr_showFolderDescription${ss_mashupItemId}) attr += ",showFolderDescription=1";
	if (ss_mashupAttr_showEntriesOpened${ss_mashupItemId}) attr += ",showEntriesOpened=1";
	if (ss_mashupAttr_numberOfLines${ss_mashupItemId} != "") {
		attr += ",entriesToShow=" + ss_encodeMashupString(ss_mashupAttr_numberOfLines${ss_mashupItemId});
	}
	if (ss_mashupAttr_numberOfCols${ss_mashupItemId} != "") {
		attr += ",cols=" + ss_encodeMashupString(ss_mashupAttr_numberOfCols${ss_mashupItemId})
	}
	if (ss_mashupAttr_columnWidths${ss_mashupItemId} != "") {
		var colWidths = ss_replaceSubStrAll(ss_mashupAttr_columnWidths${ss_mashupItemId}, ",", "|");
		attr += ",colWidths=" + ss_encodeMashupString(colWidths);
	}
	if (ss_mashupAttr_graphic${ss_mashupItemId} != "") {
		attr += ",graphic=" + ss_encodeMashupString(ss_mashupAttr_graphic${ss_mashupItemId});
	}
	if (ss_mashupAttr_graphicTitle${ss_mashupItemId} != "") {
		attr += ",title=" + ss_encodeMashupString(ss_replaceSubStrAll(ss_mashupAttr_graphicTitle${ss_mashupItemId}, ",", " "));
	}
	if (ss_mashupAttr_utilityElement${ss_mashupItemId} != "") {
		attr += ",element=" + ss_encodeMashupString(ss_mashupAttr_utilityElement${ss_mashupItemId})
	}
	if (ss_mashupAttr_listTitle${ss_mashupItemId} != "") {
		attr += ",title=" + ss_encodeMashupString(ss_mashupAttr_listTitle${ss_mashupItemId})
	}
	if (ss_mashupAttr_customJsp${ss_mashupItemId} != "") {
		attr += ",customJsp=" + ss_encodeMashupString(ss_mashupAttr_customJsp${ss_mashupItemId})
	}
	if (ss_mashupAttr_binderUrlTitle${ss_mashupItemId} != "") {
		attr += ",title=" + ss_encodeMashupString(ss_mashupAttr_binderUrlTitle${ss_mashupItemId})
	}
	if (ss_mashupAttr_binderUrlPopup${ss_mashupItemId}) {
		attr += ",popup=1"
	}
	if (ss_mashupAttr_entryUrlTitle${ss_mashupItemId} != "") {
		attr += ",title=" + ss_encodeMashupString(ss_mashupAttr_entryUrlTitle${ss_mashupItemId})
	}
	if (ss_mashupAttr_entryUrlPopup${ss_mashupItemId}) {
		attr += ",popup=1"
	}
	if (ss_mashupAttr_urlTitle${ss_mashupItemId} != "") {
		attr += ",title=" + ss_encodeMashupString(ss_mashupAttr_urlTitle${ss_mashupItemId})
	}
	if (ss_mashupAttr_urlHref${ss_mashupItemId} != "") {
		attr += ",href=" + ss_encodeMashupString(ss_mashupAttr_urlHref${ss_mashupItemId})
	}
	if (ss_mashupAttr_urlPopup${ss_mashupItemId}) {
		attr += ",popup=1"
	}
	
	//alert(attr)
	return attr;
}
function ss_mashupSubmit${ss_mashupItemId}() {
	var formObj = self.document.forms['${ss_form_form_formName}'];
	formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value += ss_mashupBuildAttrs${ss_mashupItemId}();
	//alert(formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value)
}
function ss_encodeMashupString(text) {
	var newText = encodeURI(text);
	newText = ss_replaceSubStrAll(newText, ",", "%2c");
	newText = ss_replaceSubStrAll(newText, ";", "%3b");
	return newText;
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
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'Table');return false;"
		><ssf:nlt tag="mashup.addTable"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'List');return false;"
		><ssf:nlt tag="mashup.addList"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'Entry');return false;"
		><ssf:nlt tag="mashup.addEntry"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'Folder');return false;"
		><ssf:nlt tag="mashup.addFolder"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'Url');return false;"
		><ssf:nlt tag="mashup.addUrl"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'BinderUrl');return false;"
		><ssf:nlt tag="mashup.addBinderUrl"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'EntryUrl');return false;"
		><ssf:nlt tag="mashup.addEntryUrl"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'Graphic');return false;"
		><ssf:nlt tag="mashup.addGraphic"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'Utility');return false;"
		><ssf:nlt tag="mashup.addUtility"/></a>
		<br/>
		
		<a href="javascript: ;" 
		  onClick="ss_mashupShowAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}(this, 'CustomJsp');return false;"
		><ssf:nlt tag="mashup.addCustomJsp"/></a>
		
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
          <input type="checkbox" name="${ss_mashupPropertyName}__showTitle"
          	id="${ss_mashupPropertyName}__showTitle_1"
            onChange="ss_mashupAttr_showTitle${ss_mashupItemId} = this.checked;"/> 
          <label for="${ss_mashupPropertyName}__showTitle_1">
          	<span><ssf:nlt tag="mashup.showTitle"/></span>
          </label>
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
          <input type="checkbox" name="${ss_mashupPropertyName}__showTitle"
          	id="${ss_mashupPropertyName}__showTitle_2"
            onChange="ss_mashupAttr_showTitle${ss_mashupItemId} = this.checked;"/> 
          <label for="${ss_mashupPropertyName}__showTitle_2">
          	<span><ssf:nlt tag="mashup.showTitle"/></span>
          </label>
          <br/>
          <input type="checkbox" name="${ss_mashupPropertyName}__showFolderDescription"
          	id="${ss_mashupPropertyName}__showFolderDescription"
            onChange="ss_mashupAttr_showFolderDescription${ss_mashupItemId} = this.checked;"/> 
          <label for="${ss_mashupPropertyName}__showFolderDescription">
          	<span><ssf:nlt tag="mashup.showFolderDescription"/></span>
          </label>
          <br/>
          <input type="checkbox" name="${ss_mashupPropertyName}__showEntriesOpened"
          	id="${ss_mashupPropertyName}__showEntriesOpened"
            onChange="ss_mashupAttr_showEntriesOpened${ss_mashupItemId} = this.checked;"/> 
          <label for="${ss_mashupPropertyName}__showEntriesOpened">
          	<span><ssf:nlt tag="mashup.showEntriesOpened"/></span>
          </label>
          <br/>
          <input type="text" name="${ss_mashupPropertyName}__numberOfLines"
          	id="${ss_mashupPropertyName}__numberOfLines"
            onChange="if (ss_isInteger(this.value)) {ss_mashupAttr_numberOfLines${ss_mashupItemId} = this.value;}"/> 
          <label for="${ss_mashupPropertyName}__numberOfLines">
          	<span><ssf:nlt tag="mashup.numberOfLines"/></span>
          </label>
          <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    onClick="ss_mashupSubmit${ss_mashupItemId}();return true;"
		    class="ss_linkButton ss_fineprint" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>
		
  <div id="ss_mashupAddBinderUrlDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:401;
	  	padding:10px;" 
  >
  		<div><ssf:nlt tag="mashup.selectBinder"/></div>
  		<div>
  		  <ssf:find 
    		type="places"
    		width="140px" 
    		singleItem="true"
    		foldersOnly="false"
		    clickRoutine="ss_selectBinderId${ss_mashupItemId}_${renderResponse.namespace}"
		    accessibilityText="mashup.selectBinder"
		    />
          <br/>
          <label for="${ss_mashupPropertyName}__binderUrlTitle">
          	<span class="ss_labelAbove" style="font-weight:normal !important;"
          	><ssf:nlt tag="mashup.urlTitle"/></span>
          </label>
          <input type="text" name="${ss_mashupPropertyName}__binderUrlTitle" size="40"
          	id="${ss_mashupPropertyName}__binderUrlTitle"
            onChange="ss_mashupAttr_binderUrlTitle${ss_mashupItemId} = this.value;"/> 
          <br/>
          <input type="checkbox" name="${ss_mashupPropertyName}__binderUrlPopup"
          	id="${ss_mashupPropertyName}__binderUrlPopup"
            onChange="ss_mashupAttr_binderUrlPopup${ss_mashupItemId} = this.checked;"/> 
          <label for="${ss_mashupPropertyName}__binderUrlPopup">
          	<span class="ss_labelAfter"><ssf:nlt tag="mashup.urlPopup"/></span>
          </label>
          <br/>
          <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    onClick="ss_mashupSubmit${ss_mashupItemId}();return true;"
		    class="ss_linkButton ss_fineprint" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>
		
  <div id="ss_mashupAddEntryUrlDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:401;
	  	padding:10px;" 
  >
  		<div><ssf:nlt tag="mashup.selectEntryUrl"/></div>
  		<div>
  		  <ssf:find 
    		type="entries"
    		singleItem="true"
			width="140px" 
			showFolderTitles="true"
		    clickRoutine="ss_selectEntryUrlId${ss_mashupItemId}_${renderResponse.namespace}"
		    accessibilityText="mashup.selectEntryUrl"
		    />
          <br/>
          <label for="${ss_mashupPropertyName}__entryUrlTitle">
          	<span class="ss_labelAbove" style="font-weight:normal !important;"
          	><ssf:nlt tag="mashup.urlTitle"/></span>
          </label>
          <input type="text" name="${ss_mashupPropertyName}__entryUrlTitle" size="40"
          	id="${ss_mashupPropertyName}__entryUrlTitle"
            onChange="ss_mashupAttr_entryUrlTitle${ss_mashupItemId} = this.value;"/> 
          <br/>
          <input type="checkbox" name="${ss_mashupPropertyName}__entryUrlPopup"
          	id="${ss_mashupPropertyName}__entryUrlPopup"
            onChange="ss_mashupAttr_entryUrlPopup${ss_mashupItemId} = this.checked;"/> 
          <label for="${ss_mashupPropertyName}__entryUrlPopup">
          	<span class="ss_labelAfter"><ssf:nlt tag="mashup.urlPopup"/></span>
          </label>
          <br/>
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
  		<div style="padding-bottom:16px;"><ssf:nlt tag="mashup.addTable"/></div>
  		<div>
          <input type="checkbox" name="${ss_mashupPropertyName}__showBorder"
          	id="${ss_mashupPropertyName}__showBorder_1"
            onChange="ss_mashupAttr_showBorder${ss_mashupItemId} = this.checked;"/> 
          <label for="${ss_mashupPropertyName}__showBorder_1">
          	<span class="ss_labelRight"><ssf:nlt tag="mashup.showBorder"/></span>
          </label>
          <br/>
          <br/>
          <label for="${ss_mashupPropertyName}__numberOfCols">
          	<span class="ss_labelAbove"><ssf:nlt tag="mashup.numberOfCols"/></span>
          </label>
          <input type="text" name="${ss_mashupPropertyName}__numberOfCols"
          	id="${ss_mashupPropertyName}__numberOfCols"
            onChange="if (ss_isInteger(this.value)) {ss_mashupAttr_numberOfCols${ss_mashupItemId} = this.value;}"/> 
          <br/>
          <br/>
          <label for="${ss_mashupPropertyName}__columnWidths">
          	<span class="ss_labelAbove"><ssf:nlt tag="mashup.columnWidths"/></span>
          </label>
          <input type="text" name="${ss_mashupPropertyName}__columnWidths"
          	id="${ss_mashupPropertyName}__columnWidths"
            onChange="ss_mashupAttr_columnWidths${ss_mashupItemId} = this.value;"/> 
          <br/>
          <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    class="ss_linkButton ss_fineprint"
			onClick="ss_mashup_addTable${ss_mashupItemId}_${renderResponse.namespace}();ss_mashupSubmit${ss_mashupItemId}();return true;" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>

  <div id="ss_mashupAddListDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:401;
	  	padding:10px;" 
  >
  		<div style="padding-bottom:16px;"><ssf:nlt tag="mashup.addList"/></div>
  		<div>
          <label for="${ss_mashupPropertyName}__listTitle">
          	<span class="ss_labelAbove"><ssf:nlt tag="mashup.listTitle"/></span>
          </label>
          <input type="text" name="${ss_mashupPropertyName}__listTitle" size="40"
          	id="${ss_mashupPropertyName}__listTitle"
            onChange="ss_mashupAttr_listTitle${ss_mashupItemId} = this.value;"/> 
          <br/>
          <input type="checkbox" name="${ss_mashupPropertyName}__showBorder"
          	id="${ss_mashupPropertyName}__showBorder_2"
            onChange="ss_mashupAttr_showBorder${ss_mashupItemId} = this.checked;"/> 
          <label for="${ss_mashupPropertyName}__showBorder_2">
          	<span class="ss_labelRight"><ssf:nlt tag="mashup.showBorder"/></span>
          </label>
          <br/>
          <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    class="ss_linkButton ss_fineprint"
			onClick="ss_mashup_addList${ss_mashupItemId}_${renderResponse.namespace}();ss_mashupSubmit${ss_mashupItemId}();return true;" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>

  <div id="ss_mashupAddGraphicDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:401;
	  	padding:10px;" 
  >
  		<div style="padding-bottom:16px;"><ssf:nlt tag="mashup.addGraphic"/></div>
  		<div>
          <input type="checkbox" name="${ss_mashupPropertyName}__showBorder"
          	id="${ss_mashupPropertyName}__showBorder_3"
            onChange="ss_mashupAttr_showBorder${ss_mashupItemId} = this.checked;"/> 
          <label for="${ss_mashupPropertyName}__showBorder_3">
          	<span class="ss_labelRight"><ssf:nlt tag="mashup.showBorder"/></span>
          </label>
          <br/>
          <br/>
          <label for="${ss_mashupPropertyName}__graphic">
          	<span class="ss_labelAbove"><ssf:nlt tag="mashup.graphic"/></span>
          </label>
          <select name="${ss_mashupPropertyName}__graphic"
          	id="${ss_mashupPropertyName}__graphic"
            onChange="ss_mashupAttr_graphic${ss_mashupItemId} = this.value;"> 
            <option value="" 
              onclick="ss_mashupAttr_graphicTitle${ss_mashupItemId} = '';"
            ><ssf:nlt tag="general.please_select"/></option>
            <c:forEach var="selection" items="${ssBinder.fileAttachments}" >
              <option value="${selection.id}"
                onclick="ss_mashupAttr_graphicTitle${ss_mashupItemId} = '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>';"
			  >${selection.fileItem.name}</option>
            </c:forEach>
          </select>
          <br/>
          <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    class="ss_linkButton ss_fineprint"
			onClick="ss_mashup_addGraphic${ss_mashupItemId}_${renderResponse.namespace}();ss_mashupSubmit${ss_mashupItemId}();return true;" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>

  <div id="ss_mashupAddUtilityDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:401;
	  	padding:10px;" 
  >
  		<div><ssf:nlt tag="mashup.addUtility"/></div>
  		<div>
          <label for="${ss_mashupPropertyName}__utility">
          	<span class="ss_labelAbove"><ssf:nlt tag="mashup.addUtilityElement"/></span>
          </label>
          <select name="${ss_mashupPropertyName}__utility"
          	id="${ss_mashupPropertyName}__utility"
            onChange="ss_mashupAttr_utilityElement${ss_mashupItemId} = this.value;"> 
            <option value=""><ssf:nlt tag="general.please_select"/></option>
            <option value="gettingStarted"><ssf:nlt tag="mashup.gettingStarted"/></option>
            <option value="signInForm"><ssf:nlt tag="mashup.signInForm"/></option>
            <option value="siteAdmin"><ssf:nlt tag="mashup.siteAdmin.form"/></option>
            <option value="myWorkspace"><ssf:nlt tag="mashup.myWorkspace.form"/></option>
            <option value="trackThis"><ssf:nlt tag="mashup.trackThis.form"/></option>
            <option value="shareThis"><ssf:nlt tag="mashup.shareThis.form"/></option>
          </select>
          <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    class="ss_linkButton ss_fineprint"
			onClick="ss_mashup_addUtility${ss_mashupItemId}_${renderResponse.namespace}();ss_mashupSubmit${ss_mashupItemId}();return true;" />
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
          <label for="${ss_mashupPropertyName}__customJsp">
          	<span class="ss_labelBefore"><ssf:nlt tag="mashup.customJspName"/></span>
          </label>
          <input type="text" name="${ss_mashupPropertyName}__customJsp" size="20"
          	id="${ss_mashupPropertyName}__customJsp"
            onChange="ss_mashupAttr_customJsp${ss_mashupItemId} = this.value;"/> 
          <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    class="ss_linkButton ss_fineprint"
			onClick="ss_mashup_addCustomJsp${ss_mashupItemId}_${renderResponse.namespace}();ss_mashupSubmit${ss_mashupItemId}();return true;" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>

  <div id="ss_mashupAddUrlDiv_${ss_mashupItemId}_${renderResponse.namespace}"
	  style="display:none; position:absolute; border:1px solid black; background-color:#fff; z-index:401;
	  	padding:10px;" 
  >
  		<div><ssf:nlt tag="mashup.addUrl"/></div>
  		<div>
          <label for="${ss_mashupPropertyName}__urlTitle">
          	<span class="ss_labelAbove"><ssf:nlt tag="mashup.urlTitle"/></span>
          </label>
          <input type="text" name="${ss_mashupPropertyName}__urlTitle" size="40"
          	id="${ss_mashupPropertyName}__urlTitle"
            onChange="ss_mashupAttr_urlTitle${ss_mashupItemId} = this.value;"/> 
          <br/>
          <label for="${ss_mashupPropertyName}__urlHref">
          	<span class="ss_labelAbove"><ssf:nlt tag="mashup.urlHref"/></span>
          </label>
          <input type="text" name="${ss_mashupPropertyName}__urlHref" size="40"
          	id="${ss_mashupPropertyName}__urlHref"
            onChange="ss_mashupAttr_urlHref${ss_mashupItemId} = this.value;"/> 
          <br/>
          <input type="checkbox" name="${ss_mashupPropertyName}__urlPopup"
          	id="${ss_mashupPropertyName}__urlPopup"
            onChange="ss_mashupAttr_urlPopup${ss_mashupItemId} = this.checked;"/> 
          <label for="${ss_mashupPropertyName}__urlPopup">
          	<span class="ss_labelAfter"><ssf:nlt tag="mashup.urlPopup"/></span>
          </label>
          <br/>
          <br/>
		  <input type="submit" value="<ssf:nlt tag="button.ok"/>" name="applyBtn" 
		    class="ss_linkButton ss_fineprint"
			onClick="ss_mashup_addUrl${ss_mashupItemId}_${renderResponse.namespace}();ss_mashupSubmit${ss_mashupItemId}();return true;" />
		  <input type="button" value="<ssf:nlt tag="button.cancel"/>" class="ss_linkButton ss_fineprint" 
		    onClick="ss_mashupHideAddTypeDiv${ss_mashupItemId}_${renderResponse.namespace}();return false"/>
		</div>
  </div>

</div>
<input type="hidden" name="${ss_mashupPropertyName}__${ss_mashupItemId}"/>

