<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>

<div class="ss_style ss_portlet">
	<div style="margin:10px;">
		<form name="filterData" id="filterData" method="post" 
		    action="<portlet:actionURL><portlet:param 
		    name="action" value="build_filter"/><portlet:param 
		    name="binderId" value="${ssBinder.id}"/><portlet:param 
		    name="binderType" value="${ssBinder.entityType}"/></portlet:actionURL>"
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
				<legend class="ss_legend"><ssf:nlt tag="filter.terms" text="Filter terms"/></legend>
				<span class="ss_bold"><ssf:nlt tag="filter.selectFilterType"/></span><br/><br/>
				<div class="ss_searchContainer" style="margin-left: 0;">
					<div id="ss_searchForm_spacer"></div>
			
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
	
	var ss_buttonSelected = "";
	
	function ss_buttonSelect(btn) {
		ss_buttonSelected = btn
	}
	
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
		ss_showAdditionalOptions('ss_searchForm_additionalFilters');
	});
</script>
