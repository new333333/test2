
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>


<table width="100%" cellpadding="6" cellspacing="0" summary="W4 form">
<tr>
<td><font size="-2">Form</font>
<b><font size="+2">W-4</font></b></td>
<td colspan="5"><font 
size="+1">Employee's Withholding
Allowance Certificate</font></td>
<td><font size="-2">OMB No. 1545-0010</font></td>
</tr>
<tr>
<td><font size="-3">Department of the Treasury
<br />Internal Revenue Service</font></td>
<td colspan="5">For Privacy Act and Paperwork
Reduction Act Notice, see elsewhere</td>
<td><font size="+2">2008</font></td>
</tr>
<tr><td colspan="7"><hr size="1" noshade="noshade" /></td></tr>
<tr>
<td colspan="2"><font size="-1"><b>1</b>
First name and middle initial</font></td>
<td colspan="3"><font size="-1">Last name</font></td>
<td colspan="2"><font size="-1"><b>2</b>
Your social security number</font></td></tr>
<tr>
<td colspan="2"><input type="text" name="firstName" id="firstName" 
size="25" value="${ssDefinitionEntry.customAttributes['firstName'].value}" /></td>
<td colspan="3"><input type="text" name="lastName" id="lastName"  
size="25" value="${ssDefinitionEntry.customAttributes['lastName'].value}" /></td>
<td colspan="2"><input type="text" name="ssn" 
size="11" value="${ssDefinitionEntry.customAttributes['ssn'].value}" /></td></tr>
<tr>
<td colspan="7"><hr size="1" noshade="noshade" /></td></tr>
<tr>
<td colspan="3"><font size="-1">Home address
(number and street or rural route)</font></td>
<td colspan="4"><font size="-1"><b>3</b>&nbsp;<input type="radio"  name="status" value="Single" <c:if test="${ssDefinitionEntry.customAttributes['status'].value == 'Single' || ssDefinitionEntry.customAttributes['status'].value != 'Married' && ssDefinitionEntry.customAttributes['status'].value != 'MarriedBut'}">checked="checked"</c:if> />Single
<input type="radio"  name="status" value="Married" <c:if test="${ssDefinitionEntry.customAttributes['status'].value == 'Married'}">checked="checked"</c:if> />Married
<input type="radio"  name="status" value="MarriedBut" <c:if test="${ssDefinitionEntry.customAttributes['status'].value == 'MarriedBut'}">checked="checked"</c:if> />Married, but withhold at higher
Single rate.</font></td></tr>
<tr>
<td colspan="3"><input type="text" name="address" id="address"
size="40" value="${ssDefinitionEntry.customAttributes['address'].value}" /></td>
<td colspan="4"><font
size="-3"><b>Note:</b><i>If married, but legally separated,
or spouse is a nonresident alien, check the Single box.</i>
</font></td></tr>
<tr>
<td colspan="7"><hr size="1" noshade="noshade" /></td></tr>
<tr>
<td colspan="3"><font
size="-1">City or town, state, and ZIP code</font></td>
<td colspan="4"><font
size="-1"><b>4 If your last name differs from that on your
social security card,</b></font></td></tr>
<tr>
<td colspan="3"><input type="text" name="city"
size="40" value="${ssDefinitionEntry.customAttributes['city'].value}" /></td>
<td colspan="4"><font size="-1"><b>&nbsp;&nbsp;check
here. You must call 1-800-772-1213 for a new card&nbsp;</b>
</font>
<input type="checkbox" name="boxFour" <c:if test="${ssDefinitionEntry.customAttributes['boxFour'].value == 'true'}">checked="checked"</c:if>/></td></tr>
<tr>
<td colspan="7"><hr size="1" noshade="noshade" /></td></tr>
<tr>
<td colspan="6"><font size="-1"><b>5</b>&nbsp;&nbsp;Total
number of allowances you are claiming (from line H above
or from the applicable worksheet on page 2)</font></td>
<td><table><tr><td><font size="-1"><b>5</b></font></td>
<td><input type="text" name="allowances" size="10"
value="${ssDefinitionEntry.customAttributes['allowances'].value}" /></td></tr></table></td></tr>
<tr>
<td colspan="6"><font size="-1"><b>6</b>&nbsp;&nbsp;Additional
amount, if any, you want withheld from
each paycheck . . . . . . . . . . . . . . . . . . . </font></td>
<td><table><tr><td><font size="-1"><b>6</b></font></td>
<td>$<input type="text" name="amount" size="10"
value="${ssDefinitionEntry.customAttributes['amount'].value}" /></td></tr></table></td></tr>
<tr>
<td colspan="7"><font size="-1"><b>7</b>&nbsp;&nbsp;I
claim exemption from withholding for 2001, and I certify
that I meet <b>both</b> of the following conditions
for exemption:</font></td></tr>
<tr>
<td colspan="7"><font size="-1">&nbsp;&nbsp;&nbsp;-
Last year I had a right to a refund of <b>all</b>
Federal income tax withheld because I had <b>no</b>
tax liability <b>and</b></font></td></tr>
<tr>
<td colspan="7"><font size="-1">&nbsp;&nbsp;&nbsp;-
This year I expect a refund of <b>all</b> Federal
tax withheld because I expect to have <b>no</b>
tax liability.</font></td></tr>
<tr>
<td colspan="5"><font size="-1">&nbsp;&nbsp;&nbsp;If
you meet both conditions, write "Exempt"
here . . . . . . . . . . . . . . . . . . . . . . . . . .</font></td>
<td colspan="2"><table><tr><td><font
size="-1"><b>7</b></font></td>
<td width="180"><input type="text" name="exempt" size="26"
value="${ssDefinitionEntry.customAttributes['exempt'].value}" /></td></tr>
</table></td></tr>
<tr><td colspan="7"><hr size="1" noshade="noshade" /></td></tr>
</table>

<br/>
<br />

<b>Test select box:</b><br/>

<c:set var="matchOne" value="0"/>
<c:set var="matchTwo" value="0"/>
<c:set var="matchThree" value="0"/>
<c:set var="matchFour" value="0"/>
<c:forEach var="selection" items="${ssDefinitionEntry.customAttributes['testSelection'].valueSet}" >
<c:if test="${selection == 'one'}"><c:set var="matchOne" value="1"/></c:if>
<c:if test="${selection == 'two'}"><c:set var="matchTwo" value="1"/></c:if>
<c:if test="${selection == 'three'}"><c:set var="matchThree" value="1"/></c:if>
<c:if test="${selection == 'four'}"><c:set var="matchFour" value="1"/></c:if>
</c:forEach>

<select name="testSelection" id="testSelection" multiple="multiple">
<option value="one" name="one" id="one" <c:if test="${matchOne == 1}">selected="selected" </c:if>>One</option>
<option value="two" name="two" id="two" <c:if test="${matchTwo == 1}">selected="selected" </c:if>>Two</option>
<option value="three" name="three" id="three" <c:if test="${matchThree == 1}">selected="selected" </c:if>>Three</option>
<option value="four" name="four" id="four" <c:if test="${matchFour == 1}">selected="selected" </c:if>>Four</option>
</select>					
