<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>

<c:set var="ssNamespace" value="${renderResponse.namespace}"/>

<body class="ss_style_body">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<div class="ss_style ss_portlet">
	<div style="margin:10px;">
		<form name="filterData" id="filterData" method="post" 
		    action="<ssf:url action="build_filter" actionUrl="true"><ssf:param 
		    name="binderId" value="${ssBinder.id}"/><ssf:param 
		    name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
		    onSubmit="return ss_onSubmit(this);">
<input type="hidden" id="t_searchForm_wsTreesearchFolders_idChoices" name="idChoices" value="searchFolders_${ssBinder.id}"/>

			<div class="ss_buttonBarRight">
				<input type="submit" class="ss_submit" name="okBtn" onClick="ss_buttonSelect('ok');"
				  value="<ssf:nlt tag="button.ok" text="OK"/>">&nbsp;<input 
				  type="submit" class="ss_submit" name="cancelBtn" onClick="ss_buttonSelect('cancel');"
				  value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
			</div>

			<fieldset class="ss_fieldset">
				<legend class="ss_legend"><ssf:nlt tag="filter.filterName"/></legend>
				<label for="filterName"><span class="ss_labelAbove"><ssf:nlt tag="filter.filterName" text="Filter name"/></span></label>
				<input type="text" class="ss_text" style="width:400px;" name="filterName" 
					id="filterName" value="<c:out value="${ss_selectedFilter}"/>">
				<input type="hidden" name="filterNameOriginal" value="<c:out value="${ss_selectedFilter}"/>">
				<c:if test="${ss_searchFilterShowGlobalCheckbox}">
				<br/>
				<input type="checkbox" name="global"
				<c:if test="${!empty ss_searchFilterIsGlobal}"> checked="checked" </c:if>
				>
				<input type="hidden" name="globalOriginal" value="${ss_searchFilterIsGlobal}">
				<span class="ss_labelLeft"><ssf:nlt tag="filter.global"/></span>
				</c:if>
			</fieldset>

			<div class="ss_formBreak"></div>
  
			<fieldset class="ss_fieldset">
				<legend class="ss_legend"><ssf:nlt tag="filter.terms" text="Filter terms"/> <ssf:showHelp guideName="user" pageId="mngfolder_filter" sectionId="mngfolder_filter_create" /></legend>
				<span class="ss_bold"><ssf:nlt tag="filter.selectFilterType"/></span><br/><br/>
				<div class="ss_searchContainer" style="margin-left: 0;">
					<div id="ss_content">
						<c:set var="disableSearchButton" value="1"/>	
						<c:set var="filterDefinition" value="true"/>		
						<%@ include file="/WEB-INF/jsp/search/advanced_search_form_common.jsp" %>
					</div>
				</div>

			</fieldset>
  
			<div class="ss_formBreak"></div>

			<div class="ss_buttonBarLeft">
				<input type="submit" class="ss_submit" name="okBtn" onClick="ss_buttonSelect('ok');"
				  value="<ssf:nlt tag="button.ok" text="OK"/>">&nbsp;<input 
				  type="submit" class="ss_submit" name="cancelBtn" onClick="ss_buttonSelect('cancel');"
				  value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
			</div>
			<sec:csrfInput />
		</form>
	</div>
</div>

<script type="text/javascript">
	ss_createOnSubmitObj('ss_prepareAdditionalFilterSearchOptions', 'filterData', ss_prepareAdditionalSearchOptions);
	

	<%@ include file="/WEB-INF/jsp/search/advanced_search_form_data_init.jsp" %>	
	
	function ss_checkFilterForm() {
		//Set the term numbers into the form
		var formObj = document.getElementById("filterData");
		if (ss_buttonSelected == 'ok' && formObj.filterName.value == "") {
			alert("<ssf:nlt tag="filter.enterName"/>")
			formObj.filterName.focus()
			return false;
		}
		return true;
	}

	ss_createOnSubmitObj('ss_prepareAdditionalFilterCheck', 'filterData', ss_checkFilterForm);
	
	dojo.addOnLoad(function() {
		ss_showAdditionalOptions('ss_searchForm_additionalFilters', 'ss_search_more_options_txt_${ssNamespace}', '${ssNamespace}');
	});
</script>

</body>
