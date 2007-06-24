package com.sitescape.team.liferay.action;

import com.liferay.portal.CookieNotSupportedException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SendPasswordException;
import com.liferay.portal.SystemException;
import com.liferay.portal.UserEmailAddressException;
import com.liferay.portal.UserIdException;
import com.liferay.portal.UserPasswordException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AuthException;
import com.liferay.portal.security.auth.Authenticator;
import com.liferay.portal.security.auth.PrincipalFinder;
import com.liferay.portal.service.spring.UserLocalServiceUtil;
import com.liferay.portal.struts.LastPath;
import com.liferay.portal.util.Constants;
import com.liferay.portal.util.CookieKeys;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.WebKeys;
import com.liferay.util.CookieUtil;
import com.liferay.util.Encryptor;
import com.liferay.util.EncryptorException;
import com.liferay.util.GetterUtil;
import com.liferay.util.HttpHeaders;
import com.liferay.util.InstancePool;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringPool;
import com.liferay.util.Validator;
import com.liferay.util.XSSUtil;
import com.liferay.util.servlet.SessionErrors;
import com.liferay.util.servlet.SessionMessages;
import com.liferay.util.servlet.SessionParameters;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class LenientLoginAction extends Action {

		public static String getLogin(
				HttpServletRequest req, String paramName, Company company)
			throws PortalException, SystemException {

			String login = req.getParameter(paramName);

			if ((login == null) || (login.equals(StringPool.NULL))) {
				login = GetterUtil.getString(
					CookieUtil.get(req.getCookies(), CookieKeys.LOGIN));

				if (Validator.isNull(login) &&
					company.getAuthType().equals(Company.AUTH_TYPE_EA)) {

					login = "@" + company.getMx();
				}
			}

			login = XSSUtil.strip(login);

			return login;
		}

		public static void login(
				HttpServletRequest req, HttpServletResponse res, String login,
				String password, boolean rememberMe)
			throws Exception {

			// SiteScape: Skip this validation 
			//CookieKeys.validateSupportCookie(req);

			HttpSession ses = req.getSession();

			String userId = login;

			int authResult = Authenticator.FAILURE;

			Company company = PortalUtil.getCompany(req);

			if (company.getAuthType().equals(Company.AUTH_TYPE_EA)) {
				authResult = UserLocalServiceUtil.authenticateByEmailAddress(
					company.getCompanyId(), login, password);

				userId = UserLocalServiceUtil.getUserId(
					company.getCompanyId(), login);
			}
			else {
				authResult = UserLocalServiceUtil.authenticateByUserId(
					company.getCompanyId(), login, password);
			}

			try {
				PrincipalFinder principalFinder =
					(PrincipalFinder)InstancePool.get(
						PropsUtil.get(PropsUtil.PRINCIPAL_FINDER));

				userId = principalFinder.fromLiferay(userId);
			}
			catch (Exception e) {
			}

			if (authResult == Authenticator.SUCCESS) {

				// Invalidate the previous session to prevent phishing

				LastPath lastPath = (LastPath)ses.getAttribute(WebKeys.LAST_PATH);

				ses.invalidate();
				ses = req.getSession(true);

				if (lastPath != null) {
					ses.setAttribute(WebKeys.LAST_PATH, lastPath);
				}

				// Set cookies

				User user = UserLocalServiceUtil.getUserById(userId);

				ses.setAttribute("j_username", userId);
				ses.setAttribute("j_password", user.getPassword());
				ses.setAttribute("j_remoteuser", userId);

				ses.setAttribute(WebKeys.USER_PASSWORD, password);

				Cookie idCookie = new Cookie(
					CookieKeys.ID,
					UserLocalServiceUtil.encryptUserId(userId));

				idCookie.setPath("/");

				Cookie passwordCookie = new Cookie(
					CookieKeys.PASSWORD,
					Encryptor.encrypt(company.getKeyObj(), password));

				passwordCookie.setPath("/");

				if (rememberMe) {
					idCookie.setMaxAge(CookieKeys.MAX_AGE);
					passwordCookie.setMaxAge(CookieKeys.MAX_AGE);
				}
				else {
					idCookie.setMaxAge(0);
					passwordCookie.setMaxAge(0);
				}

				Cookie loginCookie = new Cookie(CookieKeys.LOGIN, login);

				loginCookie.setPath("/");
				loginCookie.setMaxAge(CookieKeys.MAX_AGE);

				CookieKeys.addCookie(res, idCookie);
				CookieKeys.addCookie(res, passwordCookie);
				CookieKeys.addCookie(res, loginCookie);
			}
			else {
				throw new AuthException();
			}
		}

		public static void processAuthenticatedUser(
				HttpServletRequest req, HttpServletResponse res, Company company,
				String login, String userId, String password, boolean rememberMe)
			throws EncryptorException, PortalException, SystemException {

			HttpSession ses = req.getSession();

			User user = UserLocalServiceUtil.getUserById(userId);

			ses.setAttribute("j_username", userId);
			ses.setAttribute("j_password", user.getPassword());
			ses.setAttribute("j_remoteuser", userId);

			ses.setAttribute(WebKeys.USER_PASSWORD, password);

			Cookie idCookie = new Cookie(
				CookieKeys.ID,
				UserLocalServiceUtil.encryptUserId(userId));

			idCookie.setPath("/");

			Cookie passwordCookie = new Cookie(
				CookieKeys.PASSWORD,
				Encryptor.encrypt(company.getKeyObj(), password));

			passwordCookie.setPath("/");

			if (rememberMe) {
				idCookie.setMaxAge(CookieKeys.MAX_AGE);
				passwordCookie.setMaxAge(CookieKeys.MAX_AGE);
			}
			else {
				idCookie.setMaxAge(0);
				passwordCookie.setMaxAge(0);
			}

			Cookie loginCookie = new Cookie(CookieKeys.LOGIN, login);

			loginCookie.setPath("/");
			loginCookie.setMaxAge(CookieKeys.MAX_AGE);

			CookieKeys.addCookie(res, idCookie);
			CookieKeys.addCookie(res, passwordCookie);
			CookieKeys.addCookie(res, loginCookie);
		}

		public ActionForward execute(
				ActionMapping mapping, ActionForm form, HttpServletRequest req,
				HttpServletResponse res)
			throws Exception {

			HttpSession ses = req.getSession();

			if (ses.getAttribute("j_username") != null &&
				ses.getAttribute("j_password") != null) {

				return mapping.findForward("/portal/touch_protected.jsp");
			}

			String cmd = ParamUtil.getString(req, Constants.CMD);

			if (cmd.equals("already-registered")) {
				try {
					_login(req, res);

					return mapping.findForward("/portal/touch_protected.jsp");
				}
				catch (Exception e) {
					if (e instanceof AuthException ||
						e instanceof CookieNotSupportedException ||
						e instanceof NoSuchUserException ||
						e instanceof UserEmailAddressException ||
						e instanceof UserIdException ||
						e instanceof UserPasswordException) {

						SessionErrors.add(req, e.getClass().getName());

						return mapping.findForward("portal.login");
					}
					else {
						req.setAttribute(PageContext.EXCEPTION, e);

						return mapping.findForward(Constants.COMMON_ERROR);
					}
				}
			}
			else if (cmd.equals("forgot-password")) {
				try {
					_sendPassword(req);

					return mapping.findForward("portal.login");
				}
				catch (Exception e) {
					if (e instanceof NoSuchUserException ||
						e instanceof SendPasswordException ||
						e instanceof UserEmailAddressException) {

						SessionErrors.add(req, e.getClass().getName());

						return mapping.findForward("portal.login");
					}
					else {
						req.setAttribute(PageContext.EXCEPTION, e);

						return mapping.findForward(Constants.COMMON_ERROR);
					}
				}
			}
			else {
				return mapping.findForward("portal.login");
			}
		}

		private void _login(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

			String login = ParamUtil.getString(req, "login").toLowerCase();
			String password = ParamUtil.getString(
					req, SessionParameters.get(req, "password"));
			// SiteScape: If no password value in the scrambled form element,
			// try getting it from non-scrambled form element. 
			if(password == null || password.length() == 0)
				password = ParamUtil.getString(req, "password");
			boolean rememberMe = ParamUtil.getBoolean(req, "rememberMe");

			login(req, res, login, password, rememberMe);
		}

		private void _sendPassword(HttpServletRequest req) throws Exception {
			String emailAddress = ParamUtil.getString(req, "emailAddress");

			String remoteAddr = req.getRemoteAddr();
			String remoteHost = req.getRemoteHost();
			String userAgent = req.getHeader(HttpHeaders.USER_AGENT);

			UserLocalServiceUtil.sendPassword(
				PortalUtil.getCompanyId(req), emailAddress, remoteAddr, remoteHost,
				userAgent);

			SessionMessages.add(req, "request_processed", emailAddress);
		}

}
