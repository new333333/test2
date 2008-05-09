package com.sitescape.team.liferay.events;

import java.rmi.RemoteException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleServiceUtil;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.struts.LastPath;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.RoleNames;
import com.liferay.portal.util.WebKeys;

public class ServletLandingPageAction extends Action {

	private static final String DEFAULT_LANDING_CONTEXT = "default.landing.context";
	private static final String DEFAULT_LANDING_PATH = "default.landing.path";

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ActionException {
		User user;
		try {
			user = PortalUtil.getUser(request);
		} catch (PortalException e) {
			throw new ActionException(
					"Unable to retrieve user to determine landing page.", e);
		} catch (SystemException e) {
			throw new ActionException(
					"Unable to retrieve user to determine landing page.", e);
		}
		try {
			if (!UserServiceUtil.hasRoleUser(RoleServiceUtil.getRole(
					user.getCompanyId(), RoleNames.ADMINISTRATOR).getRoleId(),
					user.getUserId())) {
				String context = PropsUtil.get(DEFAULT_LANDING_CONTEXT);
				String path = PropsUtil.get(DEFAULT_LANDING_PATH);
				if (StringUtils.isBlank(context) || StringUtils.isBlank(path)) {
					// landing page not fully specified, don't do anything
					return;
				}
				LastPath landing = new LastPath(context, path,
						new HashMap<Object, Object>());
				request.getSession().setAttribute(WebKeys.LAST_PATH, landing);
			}
		} catch (PortalException e) {
			throw new ActionException(
					"Unable to determine user role to set landing redirect", e);
		} catch (SystemException e) {
			throw new ActionException(
					"Unable to determine user role to set landing redirect", e);
		} catch (RemoteException e) {
			throw new ActionException(
					"Unable to determine user role to set landing redirect", e);
		}
	}

}
