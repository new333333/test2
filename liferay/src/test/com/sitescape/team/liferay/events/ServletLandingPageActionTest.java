package com.sitescape.team.liferay.events;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleService;
import com.liferay.portal.service.UserService;
import com.liferay.portal.struts.LastPath;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.RoleNames;
import com.liferay.portal.util.WebKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.util.Pair;

//@ContextConfiguration(locations = { "/com/sitescape/team/liferay/mock-liferay-context.xml" })
public class ServletLandingPageActionTest extends AbstractTestBase {
	
	@Autowired(required = true)
	protected UserService userService;
	@Autowired(required = true)
	protected RoleService roleService;
	private ServletLandingPageAction action = new ServletLandingPageAction();

	@Test
	public void runNonAdminRedirectFullScreen() throws Exception {
		String context = "/ssf";
		String path = "/home";
		LastPath landing = new LastPath(context, path, new HashMap<Object, Object>());
		System.setProperty("default.landing.context", context);
		System.setProperty("default.landing.path", path);
		User user = createLiferayUser();
		Pair<MockHttpServletRequest, MockHttpServletResponse> p = setupParams(user);
		try {
		action.run(p.getFirst(), p.getSecond());
		} finally {
			System.clearProperty("default.landing.context");
			System.clearProperty("default.landing.path");
		}
		LastPath landing0 = (LastPath) p.getFirst().getSession().getAttribute(WebKeys.LAST_PATH);
		assertEquals(landing.getContextPath(), landing0.getContextPath());
		assertEquals(landing.getPath(), landing0.getPath());
	}
	
	@Test
	public void runContextNull() throws Exception {
		String path = "path!";
		System.setProperty("default.landing.path", path);
		User user = createLiferayUser();
		Pair<MockHttpServletRequest, MockHttpServletResponse> p = setupParams(user);
		try {
		action.run(p.getFirst(), p.getSecond());
		} finally {
			System.clearProperty("default.landing.context");
			System.clearProperty("default.landing.path");
		}
		assertNull(p.getFirst().getSession().getAttribute(WebKeys.LAST_PATH));
	}
	
	@Test
	public void runPathNull() throws Exception {
		String context = "/ssf";
		System.setProperty("default.landing.context", context);
		User user = createLiferayUser();
		Pair<MockHttpServletRequest, MockHttpServletResponse> p = setupParams(user);
		try {
		action.run(p.getFirst(), p.getSecond());
		} finally {
			System.clearProperty("default.landing.context");
			System.clearProperty("default.landing.path");
		}
		assertNull(p.getFirst().getSession().getAttribute(WebKeys.LAST_PATH));
	}

	@Test
	public void runAdminNoRedirect() throws Exception {
		String context = "defaultPage";
		String path = "/home";
		System.setProperty("default.landing.page.path", context);
		System.setProperty("default.landing.path", path);
		User user = createLiferayUser();
		userService.addRoleUsers(roleService.getRole(user.getCompanyId(),
				RoleNames.ADMINISTRATOR).getRoleId(), new long[] { user
				.getUserId() });
		Pair<MockHttpServletRequest, MockHttpServletResponse> p = setupParams(user);
		try {
			action.run(p.getFirst(), p.getSecond());
		} finally {
			System.clearProperty("default.landing.context");
			System.clearProperty("default.landing.path");
		}
		assertNull(p.getFirst().getSession().getAttribute(WebKeys.LAST_PATH));
		assertNull(p.getSecond().getRedirectedUrl());
		assertNull(p.getSecond().getForwardedUrl());
	}
	
	private Pair<MockHttpServletRequest, MockHttpServletResponse> setupParams(User user) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setSession(session);
		request.setAttribute(WebKeys.USER_ID, user.getUserId());
		request.setAttribute(WebKeys.USER, user);
		return new Pair<MockHttpServletRequest, MockHttpServletResponse>(request, response);
	}
	
	protected com.liferay.portal.model.User createLiferayUser() throws Exception {
		return userService.addUser(com.liferay.portal.util.PortalUtil
				.getCompanyIdByWebId(PropsUtil.get(PropsUtil.COMPANY_DEFAULT_WEB_ID)), false,
				"password", "password", false, RandomStringUtils.randomAlphabetic(10), "test@example.com",
				new Locale("en"), "admin", "", "admin", 0, 0, true, 1, 1, 1,
				"Administrator", 0, 0, false);
	}

}
