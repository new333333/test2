<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>

<c:set var="ssNamespace" value="${renderResponse.namespace}"/>

<div class="ss_style ss_portlet">
	<div style="margin:10px;">
		<form name="filterData" id="filterData" method="post" 
		    action="<ssf:url action="build_filter" actionUrl="true"><ssf:param 
		    name="binderId" value="${ssBinder.id}"/><ssf:param 
		    name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
		    onSubmit="return ss_onSubmit(this);">

			<div class="ss_buttonBarRight">
				<input type="submit" class="ss_submit" name="okBtn" onClick="ss_buttonSelect('ok');"
				  value="<ssf:nlt tag="button.ok" text="OK"/>">&nbsp;<input 
				  type="submit" class="ss_submit" name="cancelBtn" onClick="ss_buttonSelect('cancel');"
				  value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
			</div>

			<fieldset class="ss_fieldset">
				<legend class="ss_legend"><ssf:nlt tag="filter.filterName"/></legend>
				<span class="ss_labelAbove"><ssf:nlt tag="filter.filterName" text="Filter name"/></span>
				<input type="text" class="ss_text" style="width:400px;" name="filterName" 
					value="<c:out value="${ss_selectedFilter}"/>">
			</fieldset>

			<div class="ss_formBreak"/>
  
			<fieldset class="ss_fieldset">
				<legend class="ss_legend"><ssf:nlt tag="filter.terms" text="Filter terms"/> <ssf:inlineHelp tag="ihelp.other.filters"/></legend>
				<span class="ss_bold"><ssf:nlt tag="filter.selectFilterType"/></span><br/><br/>
				<div class="ss_searchContainer" style="margin-left: 0;">
					<div id="ss_content">
						<c:set var="disableSearchButton" value="1"/>	
						<c:set var="filterDefinition" value="true"/>				
						<%@ include file="/WEB-INF/jsp/search/advanced_search_form_common.jsp" %>
					</div>
				</div>

			</fieldset>
  
			<div class="ss_formBreak"/>

			<div class="ss_buttonBarLeft">
				<input type="submit" class="ss_submit" name="okBtn" onClick="ss_buttonSelect('ok');"
				  value="<ssf:nlt tag="button.ok" text="OK"/>">&nbsp;<input 
				  type="submit" class="ss_submit" name="cancelBtn" onClick="ss_buttonSelect('cancel');"
				  value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
			</div>
			
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
