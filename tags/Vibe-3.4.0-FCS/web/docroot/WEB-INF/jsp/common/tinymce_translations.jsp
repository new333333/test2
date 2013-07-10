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
<c:choose>
	<c:when test="${ssUser.locale.language == 'cs'}"><c:set var="ss_user_lang" value="cs" /></c:when>
	<c:when test="${ssUser.locale.language == 'da'}"><c:set var="ss_user_lang" value="da" /></c:when>
	<c:when test="${ssUser.locale.language == 'de'}"><c:set var="ss_user_lang" value="de" /></c:when>
	<c:when test="${ssUser.locale.language == 'es'}"><c:set var="ss_user_lang" value="es" /></c:when>
	<c:when test="${ssUser.locale.language == 'fr'}"><c:set var="ss_user_lang" value="fr" /></c:when>
	<c:when test="${ssUser.locale.language == 'hu'}"><c:set var="ss_user_lang" value="hu" /></c:when>
	<c:when test="${ssUser.locale.language == 'it'}"><c:set var="ss_user_lang" value="it" /></c:when>
	<c:when test="${ssUser.locale.language == 'ja'}"><c:set var="ss_user_lang" value="ja" /></c:when>
	<c:when test="${ssUser.locale.language == 'nl'}"><c:set var="ss_user_lang" value="nl" /></c:when>
	<c:when test="${ssUser.locale.language == 'pl'}"><c:set var="ss_user_lang" value="pl" /></c:when>
	<c:when test="${ssUser.locale.language == 'pt'}"><c:set var="ss_user_lang" value="pt" /></c:when>
	<c:when test="${ssUser.locale.language == 'tr'}"><c:set var="ss_user_lang" value="tr" /></c:when>
	<c:when test="${ssUser.locale.language == 'ru'}"><c:set var="ss_user_lang" value="ru" /></c:when>
	<c:when test="${ssUser.locale.language == 'uk'}"><c:set var="ss_user_lang" value="uk" /></c:when>
	<c:when test="${ssUser.locale.language == 'sv'}"><c:set var="ss_user_lang" value="sv" /></c:when>
	<c:when test="${ssUser.locale.country == 'TW'}"><c:set var="ss_user_lang" value="tw" /></c:when>
	<c:when test="${ssUser.locale.country == 'CN'}"><c:set var="ss_user_lang" value="zh" /></c:when>
	<c:when test="${ssUser.locale.language == 'zh'}"><c:set var="ss_user_lang" value="zh" /></c:when>
	<c:otherwise><c:set var="ss_user_lang" value="en" /></c:otherwise>
</c:choose>

<script type="text/javascript" language="javascript">
//If you need to change a translated string in the advanced theme langs, do it as follows
function ss_addLanguageChanges() {
	tinyMCE.addI18n('${ss_user_lang}.advanced',{
		font_size : "<ssf:nlt tag="tinyMce.font_size" text="Font size"/>",
		fontdefault : "<ssf:nlt tag="tinyMce.font_family" text="Font family"/>",
		paragraph : "<ssf:nlt tag="tinyMce.paragraph" text="paragraph"/>",
		block : "<ssf:nlt tag="tinyMce.block" text="Format"/>",
		address : "<ssf:nlt tag="tinyMce.address" text="Address"/>",
		pre : "<ssf:nlt tag="tinyMce.pre" text="Preformatted"/>",
		h1 : "<ssf:nlt tag="tinyMce.h1" text="Heading 1"/>",
		h2 : "<ssf:nlt tag="tinyMce.h2" text="Heading 2"/>",
		h3 : "<ssf:nlt tag="tinyMce.h3" text="Heading 3"/>",
		h4 : "<ssf:nlt tag="tinyMce.h4" text="Heading 4"/>",
		h5 : "<ssf:nlt tag="tinyMce.h5" text="Heading 5"/>",
		h6 : "<ssf:nlt tag="tinyMce.h6" text="Heading 6"/>",
		bold_desc : "<ssf:nlt tag="tinyMce.bold_desc" text="Bold (Ctrl+B)"/>",
		italic_desc : "<ssf:nlt tag="tinyMce.italic_desc" text="Italic (Ctrl+I)"/>",
		underline_desc : "<ssf:nlt tag="tinyMce.underline_desc" text="Underline (Ctrl+U)"/>",
		forecolor_desc : "<ssf:nlt tag="tinyMce.forecolor_desc" text="Select text color"/>",
		bullist_desc : "<ssf:nlt tag="tinyMce.bullist_desc" text="Unordered list"/>",
		numlist_desc : "<ssf:nlt tag="tinyMce.numlist_desc" text="Ordered list"/>",
		outdent_desc : "<ssf:nlt tag="tinyMce.outdent_desc" text="Outdent"/>",
		indent_desc : "<ssf:nlt tag="tinyMce.indent_desc" text="Indent"/>",
		justifyleft_desc : "<ssf:nlt tag="tinyMce.justifyleft_desc" text="Align left"/>",
		justifycenter_desc : "<ssf:nlt tag="tinyMce.justifycenter_desc" text="Align center"/>",
		justifyright_desc : "<ssf:nlt tag="tinyMce.justifyright_desc" text="Align right"/>",
		justifyfull_desc : "<ssf:nlt tag="tinyMce.justifyfull_desc" text="Align full"/>",
		code_desc : "<ssf:nlt tag="tinyMce.code_desc" text="Edit HTML Source"/>"
		});
}
</script>
