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
<script type="text/javascript" src="<html:rootPath/>js/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript">
//need to shut off relative links and adding pictures for mail.
tinyMCE.init(
 {mode: "specific_textareas", editor_selector: "mceEditable",
<c:choose><%--
    --%><c:when test="${ssUser.locale.language == 'da'}">language: 'da',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'de'}">language: 'de',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'es'}">language: 'es',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'fr'}">language: 'fr',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'hu'}">language: 'hu',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'it'}">language: 'it',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'ja'}">language: 'ja',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'nl'}">language: 'nl',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'pl'}">language: 'pl',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'pt'}">language: 'pt',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'ru'}">language: 'ru',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'uk'}">language: 'uk',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'sv'}">language: 'sv',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'sv'}">language: 'sv',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'zh'}">language: 'zh',</c:when><%--
    --%><c:otherwise>language: 'en',</c:otherwise><%--
--%></c:choose>
  content_css: "<ssf:url webPath="viewCss"><ssf:param name="sheet" value="editor"/></ssf:url>",
  relative_urls: false, 
  width: "100%",
<ssf:ifnotaccessible simple_ui="true">  accessibility_focus: false,</ssf:ifnotaccessible>
<ssf:ifaccessible simple_ui="true">  accessibility_focus: true,</ssf:ifaccessible>
  remove_script_host: false,
  entities:  "39,#39,34,quot,38,amp,60,lt,62,gt",
  gecko_spellcheck : true,
  plugins: "table,preview,paste", 
  theme_advanced_toolbar_location: "top", theme_advanced_toolbar_align: "top", 
  theme_advanced_toolbar_align: "left", theme_advanced_statusbar_location: "bottom", 
  theme_advanced_resizing: true, 
  convert_fonts_to_spans: true,
  theme_advanced_styles: "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px;18px=ss_size_18px;20px=ss_size_20px;24px=ss_size_24px;28px=ss_size_28px;32px=ss_size_32px",
  theme_advanced_buttons1_add: "forecolor,backcolor",
  theme_advanced_buttons2_add: "pastetext,pasteword",
  theme_advanced_path: false,
  theme_advanced_buttons3_add: "tablecontrols", 
  theme_advanced_resizing_use_cookie : false});

</script>
<div align="left">
 <textarea class="ss_style mceEditable" name="mailBody" style="height:200px">${body}</textarea>
</div>
