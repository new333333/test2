<% // User/Group list widget %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	Set userList = (Set) request.getAttribute("user_list");
	String userGroupType = (String) request.getAttribute("list_type");
	String binderId = (String) request.getAttribute("binderId");
	String userListFormName = (String) request.getAttribute("form_name");
	String userListElementName = (String) request.getAttribute("form_element");
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="userList" value="<%= userList %>"/>
<c:set var="binderId" value="<%= binderId %>"/>
<c:set var="prefix" value="${form_name}_${form_element}_${iCount}" />
<c:set var="addUserToListRoutine" value="ss_addUserToUserList_${prefix}"/>


<input type="hidden" name="<%= userListElementName %>" value="" id="ss_usersListInput${prefix}"/>

<script type="text/javascript">
      
function ss_addUserIdToFormElement(userId) {

	var hiddenUserIdObj = $("ss_usersListInput${prefix}");

	var newValue = "";
	if (hiddenUserIdObj.value) {
		newValue = hiddenUserIdObj.value;
	}

	newValue += " ";
	newValue += userId;
	newValue += " ";

	hiddenUserIdObj.value = newValue;
}

function afterAddUser${prefix}(obj) {
	dojo.lfx.html.highlight(obj, "#FFFF33", 500).play();
}

//Routine called when item is selected
function ss_userListSelectItem${prefix}(id, obj) {
	if (ss_userListSelectItemAlreadyAdded${prefix}(id))
		return;
	var spanObj = obj.getElementsByTagName("span").item(0);
	var ulObj = document.getElementById('added_${prefix}');
	var newLiObj = document.createElement("li");
	newLiObj.setAttribute("id", id);
	newLiObj.className = "ss_nowrap";
	newLiObj.innerHTML = spanObj.innerHTML;
	var newAnchorObj = document.createElement("a");
	newAnchorObj.setAttribute("href", "javascript: ;");
//	newAnchorObj.setAttribute("onClick", "ss_userListRemove${prefix}(this);");
	dojo.event.connect(newAnchorObj, "onclick", function(evt) {
		ss_userListRemove${prefix}(evt.target.parentNode);
    });	
	var newImgObj = document.createElement("img");
	newImgObj.setAttribute("src", "<html:imagesPath/>pics/sym_s_delete.gif");
	newImgObj.setAttribute("border", "0");
	newImgObj.style.paddingLeft = "10px";
	newAnchorObj.appendChild(newImgObj);
	newLiObj.appendChild(newAnchorObj);
	ulObj.appendChild(newLiObj);
	
	ss_addUserIdToFormElement(id);
	
	afterAddUser${prefix}(newLiObj);	
}

//Routine called when item is selected, it takes as parameters user id and user name
function ${addUserToListRoutine}(userId, userName) {
	if (ss_userListSelectItemAlreadyAdded${prefix}(userId))
		return;
	var ulObj = document.getElementById('added_${prefix}');
	var newLiObj = document.createElement("li");
	newLiObj.setAttribute("id", userId);
	var newSpanObj = document.createElement("span");
	newSpanObj.className = "ss_nowrap";
	newSpanObj.appendChild(document.createTextNode(userName));
	newLiObj.appendChild(newSpanObj);
	var newAnchorObj = document.createElement("a");
	newAnchorObj.setAttribute("href", "javascript: ;");
//	newAnchorObj.setAttribute("onClick", "ss_userListRemove${prefix}(this);");
	dojo.event.connect(newAnchorObj, "onclick", function(evt) {
		ss_userListRemove${prefix}(this);
    });
	
	
	
	var newImgObj = document.createElement("img");
	newImgObj.setAttribute("src", "<html:imagesPath/>pics/sym_s_delete.gif");
	newImgObj.setAttribute("border", "0");
	newImgObj.style.paddingLeft = "5px";
	newAnchorObj.appendChild(newImgObj);
	newLiObj.appendChild(newAnchorObj);
	ulObj.appendChild(newLiObj);
	
	ss_addUserIdToFormElement(userId);
		
	afterAddUser${prefix}(newLiObj);	
}

// Check if user allready added to list, if yes highlight it
function ss_userListSelectItemAlreadyAdded${prefix}(id) {
	var ulObj = document.getElementById('added_${prefix}');
	var lisObj = ulObj.childNodes;
	for (var i = 0; i < lisObj.length; i++) {
		if (lisObj[i].id == id) {
			afterAddUser${prefix}(lisObj[i]);
			return true;
		}
	}
	return false;
}

//Routine to remove a user
function ss_userListRemove${prefix}(obj) {
	var liObj = obj.parentNode;
	liObj.parentNode.removeChild(liObj);
	
	var userId = liObj.id;
	var userHiddenIdObj = document.getElementById("userIds_${prefix}_" + userId);
	if (userHiddenIdObj) {
		var p = userHiddenIdObj.parentNode;
		p.removeChild(userHiddenIdObj);
	}
	
	var hiddenUserIdObj = $("ss_usersListInput${prefix}");
	var value = hiddenUserIdObj.value;
	value = value.replace(" " + userId + " ", " ");
	hiddenUserIdObj.value = value;
}

</script>

<table class="ss_style" cellspacing="2px" cellpadding="5px">
<tr>
<td valign="top">
  <ssf:find formName="" 
    formElement="searchText" 
    type="${list_type}"
    width="70px" 
    clickRoutine="ss_userListSelectItem${prefix}"
    leaveResultsVisible="true"
    singleItem="true"/> 
    <c:if test="${list_type == 'user'}">
      <div><span class="ss_fineprint"><ssf:nlt tag="navigation.findUser"/></span></div>
    </c:if>
    <c:if test="${list_type == 'group'}">
      <div><span class="ss_fineprint"><ssf:nlt tag="navigation.findGroup"/></span></div>
    </c:if>

</td>
<td valign="top">
<div style="float: left;">
  <div style="border:solid black 1px;">
    <ul id="added_${prefix}" class="ss_userlist">
      <c:forEach var="item" items="${userList}">
        <li class="ss_nowrap" id="<c:out value="${item.id}"/>" ><c:out value="${item.title}"/>
          <a href="javascript: ;" 
            onClick="ss_userListRemove${prefix}(this);return false;"><img border="0" style="padding-left: 10px;" 
            src="<html:imagesPath/>pics/sym_s_delete.gif"/></a>
        </li>
      </c:forEach>
    </ul>    
  </div>  
</div>

</td>
</tr>
</table>


<script type="text/javascript">
function ss_saveUserListData_${prefix}() {
	var formObj = document.forms['<%= userListFormName %>'];
	var elementObj = formObj['<%= userListElementName %>']
	var addedObj = document.getElementById('added_${prefix}');
	var s = "";
	var items = addedObj.getElementsByTagName( "li" );
	for (var i = 0; i < items.length; i++) {
		s += items[i].id + " ";
	}
	elementObj.value = s;
	return true;
}

ss_createOnSubmitObj('${prefix}onSubmit', '<%= userListFormName %>', ss_saveUserListData_${prefix});
<c:forEach var="item" items="${userList}">
	ss_addUserIdToFormElement(<c:out value="${item.id}"/>);
</c:forEach>

</script>
