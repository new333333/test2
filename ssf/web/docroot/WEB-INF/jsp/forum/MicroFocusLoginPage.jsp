<!--  This form is used for logging in. -->
<div id="microFocusLoginPage" style="display: visible">
	<form name="loginFormName" id="microFocusLoginFormId" method="post" autocomplete="off" action="<c:out value="${ss_loginPostUrl}" escapeXml="true"/>"
		style="display: none;" accept-charset="UTF-8">
		<div class="dlgContent" id="dlgContent" style="margin: 0 auto; width: 100%; display:none;">
			<div class="loginContainer" style="margin-top:20px;">
				<div>
					<input type="text" id="j_usernameId" name="j_username" autocomplete="off" class="loginFormFields loginUserField">
				</div>
				<div>
					<input type="password" id="j_passwordId" name="j_password" autocomplete="off" class="loginFormFields loginPasswordField">
				</div>
			</div>
			<div class="gwt-Label margintop3 loginDlgKeyShieldPanel" style="display: none; width: 300px; white-space: normal !important;" id="loginDlgKeyShieldPanel">
				<span class="gwt-InlineLabel loginDlgKeyShieldErrorMessage" id="loginDlgKeyShieldErrorMessage"></span>
				<div class="gwt-Label loginDlgKeyShieldRefererPanel" id="loginDlgKeyShieldRefererPanel">
					<a class="gwt-Anchor loginDlgKeyShieldRefererLink" href="#" target="_top" id="loginDlgKeyShieldRefererLink">
						<ssf:nlt tag="loginDlg.keyShieldRefererLink" />
					</a>
				</div>
			</div>
			<div class="gwt-Label loginFailedMsg" id="loginFailedMsgDiv"></div>
			<div class="gwt-Label loginAuthenticatingMsg" id="authenticatingDiv"></div>
		</div>

		<!-- If needed, show the Text Verification controls. -->
		<%@ include file="/WEB-INF/jsp/definition_elements/textVerification.jsp"%>

		<div class="mfteamingDlgBoxFooter" id="loginDlgFooterId" style="margin: 0px !important;">
			<div class="signInBtnContainer">
				<button type="submit" class="signInBtn" id="loginOkBtn"></button>
				<button type="button" class="signInBtn" style="display: none;" id="resetPwdBtn"></button>
				<button type="button" class="signInBtn" style="display: none;" id="loginCancelBtn"></button>
			</div>
			<div id="termsBlock">
				<div>
					<button type="button" class="registerBtn" style="display: none; color: white;" id="loginRegisterBtn"></button>
				</div>
				<div id="termsContainer" class="gwt-CheckBox" style="display: none; float: left;">
					<div>
						<input id="acceptTermsCheckBox" type="checkbox" style="float: left;" /> 
						<label id="acceptTermsAnchor" style="position: absolute; margin-top: -2px;">I accept <span style="cursor: pointer; color: #fff; text-decoration: underline;">terms and conditions</span></label>
					</div>
				</div>
			</div>
			<div class="dlgContent" style="margin: 10px;">
				<div>
					<span class="gwt-InlineLabel margintop3 forgotPasswordStyle" style="display: none;" id="forgottenPwdSpan"></span>
				</div>
				<div>
					<span class="gwt-InlineLabel margintop3 selfRegLink1 selfRegLink2 createNewAccountStyle" style="display: none;" id="createNewAccountSpan">
				</div>
			</div>
			<div class="copyright">
				<span class="copyright">&copy; <ssf:nlt tag="loginDlg.copyRightText" /></span>
			</div>
		</div>		
	</form>
</div>