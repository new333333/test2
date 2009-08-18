<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ JBoss, a division of Red Hat                                             ~
  ~ Copyright 2006, Red Hat Middleware, LLC, and individual                  ~
  ~ contributors as indicated by the @authors tag. See the                   ~
  ~ copyright.txt in the distribution for a full listing of                  ~
  ~ individual contributors.                                                 ~
  ~                                                                          ~
  ~ This is free software; you can redistribute it and/or modify it          ~
  ~ under the terms of the GNU Lesser General Public License as              ~
  ~ published by the Free Software Foundation; either version 2.1 of         ~
  ~ the License, or (at your option) any later version.                      ~
  ~                                                                          ~
  ~ This software is distributed in the hope that it will be useful,         ~
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of           ~
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU         ~
  ~ Lesser General Public License for more details.                          ~
  ~                                                                          ~
  ~ You should have received a copy of the GNU Lesser General Public         ~
  ~ License along with this software; if not, write to the Free              ~
  ~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA       ~
  ~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.                 ~
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
   <style>
      body {
         background-color: #FFFFFF;
      }
      td {
         color: #000000;
         font-family: verdana, arial, sans-serif;
         font-size: 12px;
         line-height: 130%;
      }
   </style>
</head>

<body OnLoad="document.loginform.j_username.focus();">

<table width="100%" height="600" cellpadding="0" cellspacing="1" >
   <tr>
      <td valign="middle" align="center">
         <form method="POST" action='<%= response.encodeURL("j_spring_security_check") %>' 
           name="loginform" id="loginForm" autocomplete="off">

            <% String prefix = request.getContextPath(); %>

		  <table  cellspacing="0" cellpadding="0">
		  <tr>
		  <td ><img src="<%= prefix %>/standalone/images/login_top_left.png" /></td>
		  <td style="background-image:url('<%= prefix %>/standalone/images/login_top.png')"></td>
		  <td><img src="<%= prefix %>/standalone/images/login_top_right.png" /></td>
		  </tr>
		  <tr>
		  <td style="background-image:url('<%= prefix %>/standalone/images/login_left.png')"></td>
				<!-- REAL CONTENT CELL : begin -->
				<td style="background-color:#b4b4b4" align="center">
					<b>JBoss Portal Login</b><br/><br/>
					<table>
				       <tr>
					  <td align="right" width="50">
					     Username:&nbsp;
					  </td>
					  <td align="left">
					     <input type="text" name="j_username" value=""/>
					  </td>
				       </tr>
				       <tr>
					  <td align="right" width="50">
					     Password:&nbsp;
					  </td>
					  <td align="left">
					     <input type="password" name="j_password" value=""/>
					  </td>
				       </tr>
					<tr>
					  <td colspan="2" align="right">
					     <input type="submit" name="login" value="Login"/>
					  </td>
				       </tr>
				    </table>

				</td>
				<!-- REAL CONTENT CELL : end -->
		  <td style="background-image:url('<%= prefix %>/standalone/images/login_right.png')"></td>
		  </tr>
		  <tr>
		  <td><img src="<%= prefix %>/standalone/images/login_bottom_left.png" /></td>
		  <td style="background-image:url('<%= prefix %>/standalone/images/login_bottom.png')"></td>
		  <td><img src="<%= prefix %>/standalone/images/login_bottom_right.png" /></td>
		  </tr>
		  </table>


         </form>

      </td>
   </tr>
</table>

</body>
</html>
