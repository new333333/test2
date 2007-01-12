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
<c:if test="${ssConfigJspStyle != 'mail'}">
	  <ssf:presenceInfo user="${ssDefinitionEntry.modification.principal}" showTitle="true"/>
</c:if>
<c:if test="${ssConfigJspStyle == 'mail'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    action="view_permalink"
	    binderId="${ssDefinitionEntry.creation.principal.parentBinder.id}"
	    entryId="${ssDefinitionEntry.creation.principal.id}">
	    <ssf:param name="entityType" value="workspace" />
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"><c:out value="${ssDefinitionEntry.creation.principal.title}"/></a>
</c:if>
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

<c:if test="${!empty ssDefinitionEntry.reservation.principal}">
	<table cellspacing="0" cellpadding="0">
	 <tr>
	  <td valign="top" style="padding-left:30px;">
		<div class="ss_entryContent ss_entrySignature">
		  <span style="padding-right:8px;">
		  <ssf:nlt tag="entry.reservedBy"/>&nbsp;<img src="<html:imagesPath/>pics/sym_s_caution.gif"/>
		  </span>
		  
		  <ssf:presenceInfo user="${ssDefinitionEntry.reservation.principal}" showTitle="true"/>
		</div>
	  </td>
	 </tr>
	</table>
</c:if>