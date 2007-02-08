<% // User/Group list widget %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	Set userList = (Set) request.getAttribute("user_list");
	String userGroupType = (String) request.getAttribute("list_type");
	String userListFormName = (String) request.getAttribute("form_name");
	String userListElementName = (String) request.getAttribute("form_element");
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="userList" value="<%= userList %>"/>
<c:set var="prefix" value="${form_name}_${form_element}_${iCount}" />

<script type="text/javascript">

//Routine called when item is selected
function ss_userListSelectItem${prefix}(id, obj) {
	var spanObj = obj.getElementsByTagName("span").item(0);
	var ulObj = document.getElementById('added_${prefix}');
	var newLiObj = document.createElement("li");
	newLiObj.setAttribute("id", id);
	newLiObj.innerHTML = spanObj.innerHTML;
	var newAnchorObj = document.createElement("a");
	newAnchorObj.setAttribute("href", "javascript: ;");
	newAnchorObj.setAttribute("onClick", "ss_userListRemove${prefix}(this);");
	var newImgObj = document.createElement("img");
	newImgObj.setAttribute("src", "<html:imagesPath/>pics/sym_s_delete.gif");
	newImgObj.setAttribute("border", "0");
	newImgObj.style.paddingLeft = "10px";
	newAnchorObj.appendChild(newImgObj);
	newLiObj.appendChild(newAnchorObj);
	ulObj.appendChild(newLiObj);
}

//Routine to remove a user
function ss_userListRemove${prefix}(obj) {
	var liObj = obj.parentNode;
	liObj.parentNode.removeChild(liObj);
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
<div>
  <div style="border:solid black 1px;">
    <ul id="added_${prefix}" class="ss_userlist">
      <c:forEach var="item" items="${userList}">
        <li id="<c:out value="${item.id}"/>" ><c:out value="${item.title}"/>
          <a href="javascript: ;" 
            onClick="ss_userListRemove${prefix}(this);return false;"><img border="0" 
            src="<html:imagesPath/>pics/sym_s_delete.gif"/></a>
        </li>
      </c:forEach>
    </ul>
  </div>
</div>
</td>
</tr>
</table>
<input type="hidden" name="<%= userListElementName %>"/>

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
</script>
