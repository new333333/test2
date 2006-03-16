<% // navigation bar to be placed on the various calendar
   // view templates, to be used to jump off to another date
   // (uses the datepicker tag)

  // Expand the nav bar to all calendar nav functions: views -- today, 
  // week, month, year (as appropriate). The datepicker widget is Day View.
%>
<form name="ssCalNavBar" action="${goto_form_url}" 
  class="ss_toolbar_color"
  method="post" style="display:inline;"><div class="ss_toolbar_color" style="display:inline;">
	<ssf:datepicker formName="ssCalNavBar" showSelectors="true" 
	 popupDivId="ss_calDivPopup" id="ss_goto" initDate="${ssCurrentDate}" 
	 immediateMode="true" altText="<%= NLT.get("calendar.view.popupAltText") %>"
	 /></div></form>
<div id="ss_calDivPopup" class="ss_calPopupDiv"></div>
