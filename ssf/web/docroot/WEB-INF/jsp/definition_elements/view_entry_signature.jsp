<% //Entry signature view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<table cellspacing="0" cellpadding="0">
 <tr>
  <td valign="top" style="padding-left:10px;">
    <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_creator.jsp" %>
  </td>
  <td valign="top" style="padding-left:15px;">
    <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_date.jsp" %>
  </td>
 </tr>
</table>

<c:if test="${!empty ssDefinitionEntry.modification.principal && 
  ssDefinitionEntry.modification.date > ssDefinitionEntry.creation.date}">
<table cellspacing="0" cellpadding="0">
 <tr>
  <td valign="top" style="padding-left:30px;">
	<div class="ss_entryContent ss_entrySignature">
	  <span style="padding-right:8px;"><ssf:nlt tag="entry.modifiedBy"/></span>
	  <ssf:presenceInfo user="${ssDefinitionEntry.modification.principal}" showTitle="true"/>
	</div>
  </td>
  <td valign="top" style="padding-left:15px;">
	<div class="ss_entryContent ss_entrySignature">
	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
	     value="${ssDefinitionEntry.modification.date}" type="both" 
		 timeStyle="short" dateStyle="medium" />
	</div>
  </td>
 </tr>
</table>
</c:if>
