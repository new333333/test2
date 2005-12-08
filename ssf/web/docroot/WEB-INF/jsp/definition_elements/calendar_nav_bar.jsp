<% // navigation bar to be placed on the various calendar
   // view templates, to be used to jump off to another date
   // (uses the datepicker tag)
%>

<%
	Boolean tt = new Boolean(true);
%>

<form name="ssCalNavBar" action="${goto_form_url}" method="POST">
<table border="0">
<tr>
<td align="left">
<span class="ss_content">Go to: <ssf:datepicker formName="ssCalNavBar" id="goto" 
                                     initDate="${ssCurrentDate}" immediateMode="<%= tt %>" />
</span>
</td>
</tr>
</table>
</form>
