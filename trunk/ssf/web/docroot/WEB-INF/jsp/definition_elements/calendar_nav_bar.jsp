<% // navigation bar to be placed on the various calendar
   // view templates, to be used to jump off to another date
   // (uses the datepicker tag)

  // Expand the nav bar to all calendar nav functions: views -- today, 
  // week, month, year (as appropriate). The datepicker widget is Day View.
%>
<form class="ss_style" name="ssCalNavBar" action="${goto_form_url}" 
  method="post" style="display:inline;">
<input type="submit" class="ss_submit" name="go" value="Go to:">
&nbsp;
<span>
	<ssf:datepicker formName="ssCalNavBar" showSelectors="true" 
	 popupDivId="ss_calDivPopup" id="goto" initDate="${ssCurrentDate}" />
</span>
</form>
<div id="ss_calDivPopup" STYLE="position:absolute; 
  visibility:hidden; background-color:#f7f7f7; 
  layer-background-color:white;"></div>
