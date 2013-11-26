<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<tr>
<td colspan="3"><font size="-1">Home address (number and street or
rural route)</font></td>
<td colspan="4">3 <input type="radio"  name="status" value="Single" <c:if test="${ssDefinitionEntry.customAttributes['status'].value == 'Single' || ssDefinitionEntry.customAttributes['status'].value != 'Married' && ssDefinitionEntry.customAttributes['status'].value != 'MarriedBut'}">checked="checked"</c:if> DISABLED />Single
<input type="radio" name="status" value="Married" <c:if test="${ssDefinitionEntry.customAttributes['status'].value == 'Married'}">checked="checked"</c:if> DISABLED />Married
<input type="radio" name="status" value="MarriedBut" <c:if test="${ssDefinitionEntry.customAttributes['status'].value == 'MarriedBut'}">checked="checked"</c:if> DISABLED />Married, but withhold at higher
Single rate.</td>
</tr>
<tr>
<td colspan="3">${ssDefinitionEntry.customAttributes['address'].value}</td>
<td colspan="4"><font size="-3"><b>Note:</b><i>If married, but
legally separated, or spouse is a nonresident alien, check the
Single box.</i></font></td>
</tr>
<tr><td colspan="7"><hr size="1" noshade="noshade" /></td></tr>
<tr>
<td colspan="3"><font size="-1">City or town, state, and ZIP code</font></td>
<td colspan="4"><font size="-1"><b>4 If your last name differs from that
on your social security card,</b></font></td>
</tr>
<tr>
<td colspan="3">${ssDefinitionEntry.customAttributes['city'].value}</td>
<td colspan="4"><font size="-1"><b>&nbsp;&nbsp;check here. You must call
1-800-772-1213 for a new card&nbsp;</b></font><input type="checkbox" name="boxFour" <c:if test="${ssDefinitionEntry.customAttributes['boxFour'].value == 'true'}">checked="checked"</c:if> DISABLED /></td>
</tr>
<tr><td colspan="7"><hr size="1" noshade="noshade" /></td></tr>
<tr>
<td colspan="6"><font size="-1"><b>5</b>&nbsp;&nbsp;Total number of allowances
you are claiming (from line H above or from the applicable worksheet on
page 2)</font></td>
<td><table class="border"><tr><td><font size="-1"><b>5</b></font></td>
<td width="55">${ssDefinitionEntry.customAttributes['allowances'].value}</td></tr></table></td>
</tr>
<tr>
<td colspan="6"><font size="-1"><b>6</b>&nbsp;&nbsp;Additional amount, if any,
you want withheld from each paycheck . . . . . . . . . . . . . .
. . . . . . </font></td>
<td><table class="border"><tr>
<td><font size="-1"><b>6</b></font></td>
<td width="55">$${ssDefinitionEntry.customAttributes['amount'].value}</td></tr></table></td>
</tr>
<tr>
<td colspan="7"><font size="-1"><b>7</b>&nbsp;&nbsp;I claim exemption from
withholding for 2001, and I certify that I meet <b>both</b> of the following
conditions for exemption:</font></td>
</tr>
<tr>
<td colspan="7"><font size="-1">&nbsp;&nbsp;&nbsp;- Last year I had a
right to a refund of <b>all</b> Federal income tax withheld because I had
<b>no</b> tax liability <b>and</b></font></td>
</tr>
<tr>
<td colspan="7"><font size="-1">&nbsp;&nbsp;&nbsp;- This year I expect a
refund of <b>all</b> Federal tax withheld because I expect to have
<b>no</b> tax liability.</font></td>
</tr>
<tr>
<td colspan="5"><font size="-1">&nbsp;&nbsp;&nbsp;If you meet both
conditions, write "Exempt" here . . . . . . . . . . . . . . .
. . . .</font></td>
<td colspan="2"><table class="border">
<tr><td><font size="-1"><b>7</b></font></td>
<td width="180">${ssDefinitionEntry.customAttributes['exempt'].value}</td></tr></table></td>
</tr>

<tr><td colspan="7"><hr size="1" noshade="noshade" /></td></tr>

</table>

<br/><br/>

<b>Test selection:</b>

<c:set var="numSelections" value="0"/>
<c:forEach var="selection" items="${ssDefinitionEntry.customAttributes['testSelection'].valueSet}" >
<c:set var="numSelections" value="${numSelections + 1}"/>
</c:forEach>

<c:set var="count" value="0"/>
<c:forEach var="selection" items="${ssDefinitionEntry.customAttributes['testSelection'].valueSet}" >
<c:set var="count" value="${count + 1}"/>
<c:if test="${selection == 'one'}">One</c:if>
<c:if test="${selection == 'two'}">Two</c:if>
<c:if test="${selection == 'three'}">Three</c:if>
<c:if test="${selection == 'four'}">Four</c:if>
<c:if test="${count != numSelections}">, </c:if>
</c:forEach>



