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

</table>
