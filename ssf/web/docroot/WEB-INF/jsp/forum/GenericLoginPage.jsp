<!--  This form is used for logging in. -->
<div id="genericLoginPage" style="display: none">
<p>Generic Login Page</p>
	<form name="loginFormName" id="genericLoginFormId" method="post"
		action="<c:out value="${ss_loginPostUrl}" escapeXml="true"/>"
		style="display: none;" accept-charset="UTF-8">
		<table cellspacing="4" class="dlgContent" style="margin: 10px;">
			<colgroup>
				<col>
			</colgroup>
			<tbody>
				<tr>
					<td><span id="userIdLabel"></span></td>
					<td><input type="text" size="20" id="j_usernameId"
						name="j_username" class="gwt-TextBox"></td>
				</tr>
				<tr>
					<td><span id="pwdLabel"></span></td>
					<td><input type="password" size="20" id="j_passwordId"
						name="j_password" class="gwt-PasswordTextBox"></td>
				</tr>
				<tr>
					<td align="right"></td>
					<td>
						<div class="gwt-Label margintop3 loginDlgKeyShieldPanel"
							style="display: none; width: 300px; white-space: normal !important;"
							id="loginDlgKeyShieldPanel">
							<span class="gwt-InlineLabel loginDlgKeyShieldErrorMessage"
								id="loginDlgKeyShieldErrorMessage"></span>
							<div class="gwt-Label loginDlgKeyShieldRefererPanel"
								id="loginDlgKeyShieldRefererPanel">
								<a class="gwt-Anchor loginDlgKeyShieldRefererLink" href="#"
									target="_top" id="loginDlgKeyShieldRefererLink"><ssf:nlt
										tag="loginDlg.keyShieldRefererLink" /></a>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td align="right"></td>
					<td>
						<div class="gwt-Label loginFailedMsg"
							style="display: none; width: 250px;" id="loginFailedMsgDiv"></div>
					</td>
				</tr>
				<tr>
					<td align="right"></td>
					<td>
						<div class="gwt-Label loginAuthenticatingMsg"
							style="display: none;" id="authenticatingDiv"></div>
					</td>
				</tr>
			</tbody>
		</table>

		<!-- If needed, show the Text Verification controls. -->
		<%@ include
			file="/WEB-INF/jsp/definition_elements/textVerification.jsp"%>

		<table cellspacing="4" class="dlgContent" style="margin: 10px;">
			<colgroup>
				<col>
			</colgroup>
			<tbody>
				<tr>
					<td><span
						class="gwt-InlineLabel margintop3 selfRegLink1 selfRegLink2"
						style="display: none;" id="forgottenPwdSpan"></span></td>
					<td><span
						class="gwt-InlineLabel margintop3 selfRegLink1 selfRegLink2"
						style="display: none;" id="createNewAccountSpan"></span></td>
				</tr>
			</tbody>
		</table>

		<div class="teamingDlgBoxFooter" id="loginDlgFooterId"
			style="margin: 0px !important;">
			<button type="submit" class="gwt-Button teamingButton"
				id="loginOkBtn"></button>
			<div id="termsBlock">
				<div id="termsContainer" class="gwt-CheckBox"
					style="display: none; float: left;">
					<div>
						<input id="acceptTermsCheckBox" type="checkbox"
							style="float: left;" /> <label id="acceptTermsAnchor"
							style="position: absolute; margin-top: -2px;">I accept <span
							style="cursor: pointer; color: #135c8f; text-decoration: underline;">terms
								and conditions</span></label>
					</div>
				</div>
				<button type="button" class="teamingButton"
					style="display: none; color: white;" id="loginRegisterBtn"></button>
			</div>
			<button type="button" class="gwt-Button teamingButton"
				style="display: none;" id="resetPwdBtn"></button>
			<button type="button" class="gwt-Button teamingButton" style=""
				id="loginCancelBtn"></button>
		</div>
		<sec:csrfInput />
	</form>
</div>