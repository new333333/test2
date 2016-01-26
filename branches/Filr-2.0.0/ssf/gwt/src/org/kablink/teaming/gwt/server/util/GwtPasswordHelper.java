/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.server.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.PasswordMismatchException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.User.ExtProvState;
import org.kablink.teaming.extuser.ExternalUserUtil;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.RequestResetPwdRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.module.admin.SendMailErrorWrapper;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.EmailHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PasswordPolicyHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.EmailHelper.UrlNotificationType;

/**
 * Helper methods for GWT password management.
 *
 * @author drfoster@novell.com
 */
public class GwtPasswordHelper {
	protected static Log m_logger = LogFactory.getLog(GwtPasswordHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtPasswordHelper() {
		// Nothing to do.
	}
	
	/**
	 * Change the current user's password.
	 * 
	 * @param bs
	 * @param request
	 * @param oldPwd
	 * @param newPwd
	 * @param userId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData changePassword(final AllModulesInjected bs, final HttpServletRequest request, final String oldPwd, final String newPwd, final Long userId) throws GwtTeamingException {
		// Allocate an ErrorListRpcResponseData we can return with
		// any errors from the password change.
		ErrorListRpcResponseData reply = new ErrorListRpcResponseData();
		
		try {
			// Who we change the password for?
			boolean isCurrentUser = (null == userId);
			User    currentUser   = GwtServerHelper.getCurrentUser();
			User pwChangeUser;
			if (isCurrentUser) {
				pwChangeUser = currentUser;
			}
			else {
				pwChangeUser  = ((User) bs.getProfileModule().getEntry(userId, false));	// false -> Don't do an access check.
				isCurrentUser = ((null != currentUser) && userId.equals(currentUser.getId()));
			}
			
			// Does the new password violate the system's password
			// policy for that user?
			List<String> ppViolations = PasswordPolicyHelper.getPasswordPolicyViolations(pwChangeUser, pwChangeUser, newPwd);
			if (MiscUtil.hasItems(ppViolations)) {
				// Yes!  Copy the violations to the response.
				for (String ppViolation:  ppViolations) {
					reply.addError(ppViolation);
				}
			}
			
			else {
				// No, the new password doesn't violate the system's
				// password policy!  Change the user's password.  Are
				// the built-in admin or are we changing the password
				// for the currently logged in user?
				if (isCurrentUser || currentUser.isAdmin()) {
					// Yes!  We can change it directly without worry of
					// an access control violation.
					changePasswordImpl(bs, oldPwd, newPwd, userId, false);	// false -> Don't validate password policy.  We took care of that above.
				}
				
				else {
					// Otherwise, we have to change it as the user
					// whose password is being changed.
					RunasTemplate.runas(
						new RunasCallback() {
							@Override
							public Object doAs() {
								changePasswordImpl(bs, oldPwd, newPwd, userId, false);	// false -> Don't validate password policy.  We took care of that above.
								return null;
							}
						},
						WebHelper.getRequiredZoneName(request),
						userId);
				}
				
				// Are we dealing with the built-in admin user?
				if (pwChangeUser.isAdmin()) {
					// Yes!  Is this the admin's first time logging in?
					if (null == pwChangeUser.getFirstLoginDate()) {
						// Yes!  Remember the login date.
						bs.getProfileModule().setFirstLoginDate(pwChangeUser.getId());
					}
				}
			}

			return reply;
		}
		
		catch (PasswordMismatchException ex) {
			GwtTeamingException gwtEx = GwtLogHelper.getGwtClientException(m_logger, ex);
			gwtEx.setAdditionalDetails(ex.getLocalizedMessage());
			throw gwtEx;
		}
	}
	
	private static void changePasswordImpl(AllModulesInjected bs, String oldPwd, String newPwd, Long userId, boolean validatePolicy) {
		bs.getProfileModule().changePassword(userId, oldPwd, newPwd, validatePolicy);
	}
	
	/**
	 * Reset send the user an e-mail with a link they need to click on
	 * to verify their password reset.
	 * 
	 * @param bs
	 * @param request
	 * @param userID
	 * @param pwd
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static RequestResetPwdRpcResponseData requestResetPwd(final AllModulesInjected bs, final HttpServletRequest request, final Long userId, final String pwd) {
		final RequestResetPwdRpcResponseData responseData = new RequestResetPwdRpcResponseData();
		if ((!(MiscUtil.hasString(pwd))) || (null == userId)) {
			responseData.addError("Invalid parameters passed to GwtPasswordHelper.requestResetPwd()");
			return responseData;
		}

		// Do the necessary work as the admin user.
		RunasTemplate.runasAdmin(new RunasCallback() {
			@Override
			public Object doAs() {
				User user = null;
				try {
					user = ((User) bs.getProfileModule().getEntry(userId));
				}
				
				catch (AccessControlException acEx) {
					String error = NLT.get("request.pwd.reset.cant.find.user", new String[]{userId.toString()});
					responseData.addError(error);
				}
				
				if ((null != user) &&
						(!(user.getIdentityInfo().isInternal())) &&
						(ExtProvState.pwdResetRequested == user.getExtProvState())) {
					// Does the new password violate the system's password
					// policy for that user?
					List<String> ppViolations = PasswordPolicyHelper.getPasswordPolicyViolations(user, user, pwd);
					if (MiscUtil.hasItems(ppViolations)) {
						// Yes!  Copy the violations to the response.
						for (String ppViolation:  ppViolations) {
							responseData.addError(ppViolation);
						}
					}

					else {
						// No, the new password doesn't violate the
						// system's password policy for that user!
						// Save the password to the user's properties.
						// We will read it from the user's properties
						// when the user clicks on the url in the
						// 'reset password verification' e-mail.
						bs.getProfileModule().setUserProperty(
							user.getId(),
							ObjectKeys.USER_PROPERTY_RESET_PWD,
							pwd);
						
						// Send the user an e-mail telling them that
						// their password has been modified and they
						// need to verify that they were the one who
						// changed the password.
						
						// Get a url to the user's workspace.
						AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true, false);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
						adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, String.valueOf(userId));
						adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityIdentifier.EntityType.user.name());
						
						// If we are running Filr, take the user to
						// 'My Files'.
						if (Utils.checkIfFilr()) {
							adapterUrl.setParameter(WebKeys.URL_SHOW_COLLECTION, "0");	// 0 -> CollectionType.MY_FILES
						}
						
						// Append the encoded user token to the URL.
						String token = ExternalUserUtil.encodeUserTokenWithNewSeed(user);
						adapterUrl.setParameter(ExternalUserUtil.QUERY_FIELD_NAME_EXTERNAL_USER_ENCODED_TOKEN, token);
						String url = adapterUrl.toString();

						Map<String,Object> errorMap = null;
						try {
							errorMap = EmailHelper.sendUrlNotification(
								bs,
								url,
								UrlNotificationType.PASSWORD_RESET_REQUESTED,
								userId);
						}
						
						catch (Exception ex) {
							String error = NLT.get("request.pwd.reset.send.email.failed", new String[]{ex.toString()});
							responseData.addError(error);
							
							GwtLogHelper.error(m_logger, "GwtPasswordHelper.requestResetPwd( EXCEPTION ):  ", ex);
						}

						if (null != errorMap) {
							List<SendMailErrorWrapper> emailErrors = ((List<SendMailErrorWrapper>) errorMap.get(ObjectKeys.SENDMAIL_ERRORS));
							if (MiscUtil.hasItems(emailErrors)) {
								responseData.addErrors(SendMailErrorWrapper.getErrorMessages(emailErrors));
							}
							
							else {
								// Sending the e-mail worked.  Change
								// the user's 'external user
								// provisioned state' to 'password
								// reset waiting for verification'.
								ExternalUserUtil.markAsPwdResetWaitingForVerification(user);
							}
						}
					}
				}
				
				else {
					String error = NLT.get("request.pwd.reset.invalid.user.state");
					responseData.addError(error);
				}
				
				return null;
			}},
			RequestContextHolder.getRequestContext().getZoneName());

		return responseData;
	}
}
