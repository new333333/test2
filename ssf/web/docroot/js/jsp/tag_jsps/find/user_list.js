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

if (!window.ss_findUsersClickRoutine) ss_findUsersClickRoutine = new Array();
if (!window.ss_findUsersFormName) ss_findUsersFormName = new Array();
if (!window.ss_findUsersListElementName) ss_findUsersListElementName = new Array();

function ss_findUsersConfVariableForPrefix(prefix, clickRoutine, formName, userListElementName) {
	ss_findUsersClickRoutine[prefix] = clickRoutine;
	ss_findUsersFormName[prefix] = formName;
	ss_findUsersListElementName[prefix] = userListElementName;
}

function afterAddUser(prefix, obj) {
	dojo.lfx.html.highlight(obj, "#FFFF33", 500).play();
}

function ss_addUserIdToFormElement(prefix, userId) {

	var hiddenUserIdObj = $("ss_usersListInput" + prefix);

	var newValue = "";
	if (hiddenUserIdObj.value) {
		newValue = hiddenUserIdObj.value;
	}

	newValue += " ";
	newValue += userId;
	newValue += " ";

	hiddenUserIdObj.value = newValue;
}


//Routine called when item is selected
function ss_userListSelectItem(id, obj, prefix) {
	if (ss_userListSelectItemAlreadyAdded(prefix, id))
		return;
	var spanObj = obj.getElementsByTagName("span").item(0);
	var ulObj = document.getElementById('added_' + prefix);
	var newLiObj = document.createElement("li");
	newLiObj.setAttribute("id", id);
	newLiObj.className = "ss_nowrap";
	newLiObj.innerHTML = spanObj.innerHTML;
	var newAnchorObj = document.createElement("a");
	newAnchorObj.setAttribute("href", "javascript: ;");
	// newAnchorObj.setAttribute("onClick", "ss_userListRemove('" + prefix + "', this);");
	dojo.event.connect(newAnchorObj, "onclick", function(evt) {
		ss_userListRemove(prefix, evt.target.parentNode);
    });	
	
	var newImgObj = document.createElement("img");
	newImgObj.setAttribute("src", ss_imagesPath + "pics/sym_s_delete.gif");
	newImgObj.setAttribute("border", "0");
	newImgObj.style.paddingLeft = "10px";
	newAnchorObj.appendChild(newImgObj);
	newLiObj.appendChild(newAnchorObj);
	ulObj.appendChild(newLiObj);

	ss_addUserIdToFormElement(prefix, id);
	
	if (typeof ss_findUsersClickRoutine[prefix] != "undefined" && typeof window[ss_findUsersClickRoutine[prefix]] != "undefined") {
		window[ss_findUsersClickRoutine[prefix]]();
	}

	afterAddUser(prefix, newLiObj);
}

// Check if user allready added to list, if yes highlight it
function ss_userListSelectItemAlreadyAdded(prefix, id) {
	var ulObj = document.getElementById('added_' + prefix);
	var lisObj = ulObj.childNodes;
	for (var i = 0; i < lisObj.length; i++) {
		if (lisObj[i].id == id) {
			afterAddUser(prefix, lisObj[i]);
			return true;
		}
	}
	return false;
}

//Routine to remove a user
function ss_userListRemove(prefix, obj) {
	var liObj = obj.parentNode;
	
	var userId = liObj.id;
	var userHiddenIdObj = document.getElementById("userIds_" + prefix + "_" + userId);
	liObj.parentNode.removeChild(liObj);
	if (ss_findUsersClickRoutine[prefix] && window[ss_findUsersClickRoutine[prefix]]) {
		window[ss_findUsersClickRoutine[prefix]]();
	}
		
	var hiddenUserIdObj = $("ss_usersListInput" + prefix);
	var value = hiddenUserIdObj.value;
	value = value.replace(" " + userId + " ", " ");
	hiddenUserIdObj.value = value;
}


function ss_saveUserListData(prefix, formName, elementName) {
	this.prefix = prefix;
	this.formName = formName;
	this.elementName = elementName;

	var me = this;
	
	this.invoke = function() {
		var formObj = document.forms[me.formName];
		var elementObj = formObj[me.elementName]
		var addedObj = document.getElementById('added_' + prefix);
		var s = "";
		var items = addedObj.getElementsByTagName( "li" );
		for (var i = 0; i < items.length; i++) {
			s += items[i].id + " ";
		}
		elementObj.value = s;
		return true;
	}
}


function ss_findUserListInitializeForm(prefix, formName, elementName) {
	var saveUserListData = new ss_saveUserListData(prefix, formName, elementName);
	ss_createOnSubmitObj(prefix + 'onSubmit_user_list', formName, saveUserListData.invoke);
}
