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
	String clickRoutine = (String) request.getAttribute("clickRoutine");
	String clickRoutineArgs = (String) request.getAttribute("clickRoutineArgs");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="userList" value="<%= userList %>"/>
<c:set var="binderId" value="<%= binderId %>"/>
<c:set var="prefix" value="${form_name}_${form_element}_${iCount}" />

<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/user_list.js"></script>


<table class="ss_style" cellspacing="2px" cellpadding="5px">
<tbody>
<tr>
<td valign="top">

<input type="hidden" name="<%= userListElementName %>" id="ss_usersListInput${prefix}"/>		


<img src="<html:imagesPath/>pics/1pix.gif" onload="ss_findUsersConfVariableForPrefix('${prefix}', '${clickRoutine}', '<%= userListFormName %>', '<%= userListElementName %>'); ss_findUserListInitializeForm('${prefix}', '<%= userListFormName %>', '<%= userListElementName %>');  <c:forEach var="item" items="${userList}" varStatus="status"> ss_addUserIdToFormElement('${prefix}', '<c:out value="${item.id}"/>');</c:forEach>" />

  <ssf:find formName="" 
    formElement="searchText" 
    type="${list_type}"
    width="70px" 
    clickRoutine="ss_userListSelectItem"
    clickRoutineArgs="${prefix}"
    leaveResultsVisible="${leaveResultsVisible}"
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
            onClick="ss_userListRemove('${prefix}', this);return false;"><img border="0" style="padding-left: 10px;" 
            src="<html:imagesPath/>pics/sym_s_delete.gif"/></a>
        </li>
      </c:forEach>
    </ul>    
  </div>  
</div>

</td>
</tr>
</tbody>
</table>

