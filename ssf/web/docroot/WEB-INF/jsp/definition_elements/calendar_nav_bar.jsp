<% // navigation bar to be placed on the various calendar
   // view templates, to be used to jump off to another date
   // (uses the datepicker tag)

  // Expand the nav bar to all calendar nav functions: views -- today, 
  // week, month, year (as appropriate). The datepicker widget is Day View.
%>
<form name="ssCalNavBar" action="${goto_form_url}" method="post" style="display:inline;">
<input type="submit" class="ss_submit" name="go" value="Go to:">&nbsp;<span class="ss_content"><ssf:datepicker formName="ssCalNavBar" id="goto" initDate="${ssCurrentDate}" />
</form>
