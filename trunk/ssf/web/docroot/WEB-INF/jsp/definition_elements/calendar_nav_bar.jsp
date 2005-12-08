<% // navigation bar to be placed on the various calendar
   // view templates, to be used to jump off to another date
   // (uses the datepicker tag)
%>

<form name="ssCalNavBar" action="${goto_form_url}" method="POST">
<table border="0">
<tr>
<td align="left">
<span class="ss_content">Go to: <ssf:datepicker formName="ssCalNavBar" id="goto" 
                                     initDate="${ssCurrentDate}" />
</span>
<input type="submit" name="GO" value="GO">
</td>
</tr>
</table>
</form>
