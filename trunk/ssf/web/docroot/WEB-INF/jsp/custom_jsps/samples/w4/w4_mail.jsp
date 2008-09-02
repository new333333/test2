
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<table width="100%" class="border" cellpadding="6" 
summary="W4 Information">
<tr>
<td><font size="-2">Form</font> <b><font size="+2">W-4</font></b></td>
<td colspan="5"><font size="+1">Employee's Withholding
Allowance Certificate</font></td>
<td><font size="-2">OMB No. 1545-0010</font></td>
</tr>
<tr>
<td><font size="-3">Department of the Treasury<br />Internal
Revenue Service</font></td>
<td colspan="5">For Privacy Act and Paperwork Reduction Act Notice, see
elsewhere</td>
<td><font size="+2">2008</font></td>
</tr>
<tr><td colspan="7"><hr size="1" noshade="noshade" /></td></tr>
<tr>
<td colspan="2"><font size="-1"><b>1</b> First name and
middle initial</font></td>
<td colspan="3"><font size="-1">Last name</font></td>
<td colspan="2"><font size="-1"><b>2</b> Your social security
number</font></td>
</tr>
<tr>
<td colspan="2">${ssDefinitionEntry.customAttributes['firstName'].value}</td>
<td colspan="3">${ssDefinitionEntry.customAttributes['lastName'].value}</td>
<td colspan="2">${ssDefinitionEntry.customAttributes['ssn'].value}</td>
</tr>
<tr><td colspan="7"><hr size="1" noshade="noshade" /></td></tr>

</table>
