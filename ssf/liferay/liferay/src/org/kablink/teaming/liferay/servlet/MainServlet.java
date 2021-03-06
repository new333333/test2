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
 *
 * - - - - -
 * 
 * Copyright (c) 2000-2007 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.kablink.teaming.liferay.servlet;

import com.liferay.portal.deploy.hot.PluginPackageHotDeployListener;
import com.liferay.portal.events.EventsProcessor;
import com.liferay.portal.events.StartupAction;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.servlet.PortletSessionTracker;
import com.liferay.portal.lastmodified.LastModifiedAction;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.impl.LayoutTemplateLocalUtil;
import com.liferay.portal.service.impl.ThemeLocalUtil;
import com.liferay.portal.struts.PortletRequestProcessor;
import com.liferay.portal.struts.StrutsUtil;
import com.liferay.portal.util.Constants;
import com.liferay.portal.util.ContentUtil;
import com.liferay.portal.util.InitUtil;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.ReleaseInfo;
import com.liferay.portal.util.SAXReaderFactory;
import com.liferay.portal.util.ShutdownUtil;
import com.liferay.portal.util.WebKeys;
import com.liferay.portal.velocity.VelocityContextPool;
import com.liferay.portlet.PortletInstanceFactory;
import com.liferay.util.CollectionFactory;
import com.liferay.util.GetterUtil;
import com.liferay.util.Http;
import com.liferay.util.HttpHeaders;
import com.liferay.util.InstancePool;
import com.liferay.util.ParamUtil;
import com.liferay.util.Validator;
import com.liferay.util.servlet.EncryptedServletRequest;
import com.liferay.util.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.util.PortalInitableUtil;
import com.liferay.portal.kernel.deploy.hot.HotDeployUtil;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.tiles.TilesUtilImpl;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.xml.sax.SAXException;

/**
 * <a href="MainServlet.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Brian Myunghun Kim
 */
@SuppressWarnings("unchecked")
public class MainServlet extends ActionServlet {
/*
 * This class is a temporary substitute/patch for Liferay's 
 * com.liferay.portal.servlet.MainServlet class with a fix for the problem 
 * reported in LEP-2855. Also see ICEcore issue #1204 for additional details.
 * This class will be no longer necessary once/when we upgrade to Liferay 
 * 4.3.1 or later since the later Liferay versions already contain fix for
 * this problem.
 */
	static {
		InitUtil.init();
	}

	public static ServletContext servletContext; // hack to get hold of servlet context
	
	@Override
	public void init() throws ServletException {

		// Initialize

		if (_log.isDebugEnabled()) {
			_log.debug("Initialize");
		}

		super.init();

		// Process startup events

		if (_log.isDebugEnabled()) {
			_log.debug("Process startup events");
		}

		try {
			EventsProcessor.process(
				new String[] {
					StartupAction.class.getName()
				},
				true);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Velocity

		String contextPath = PortalUtil.getPathContext();

		ServletContext ctx = getServletContext();

		VelocityContextPool.put(contextPath, ctx);

		// Initialize plugin package

		if (_log.isDebugEnabled()) {
			_log.debug("Initialize plugin package");
		}

		PluginPackage pluginPackage = null;

		try {
			pluginPackage =
				PluginPackageHotDeployListener.readPluginPackage(ctx);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Initialize portlets

		if (_log.isDebugEnabled()) {
			_log.debug("Initialize portlets");
		}

		try {
			String[] xmls = new String[] {
				Http.URLtoString(ctx.getResource(
					"/WEB-INF/" + PortalUtil.PORTLET_XML_FILE_NAME_CUSTOM)),
				Http.URLtoString(ctx.getResource(
					"/WEB-INF/portlet-ext.xml")),
				Http.URLtoString(ctx.getResource(
					"/WEB-INF/liferay-portlet.xml")),
				Http.URLtoString(ctx.getResource(
					"/WEB-INF/liferay-portlet-ext.xml")),
				Http.URLtoString(ctx.getResource("/WEB-INF/web.xml"))
			};

			PortletLocalServiceUtil.initEAR(xmls, pluginPackage);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Initialize layout templates

		if (_log.isDebugEnabled()) {
			_log.debug("Initialize layout templates");
		}

		try {
			String[] xmls = new String[] {
				Http.URLtoString(ctx.getResource(
					"/WEB-INF/liferay-layout-templates.xml")),
				Http.URLtoString(ctx.getResource(
					"/WEB-INF/liferay-layout-templates-ext.xml"))
			};

			LayoutTemplateLocalUtil.init(ctx, xmls, pluginPackage);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Initialize look and feel

		if (_log.isDebugEnabled()) {
			_log.debug("Initialize look and feel");
		}

		try {
			String[] xmls = new String[] {
				Http.URLtoString(ctx.getResource(
					"/WEB-INF/liferay-look-and-feel.xml")),
				Http.URLtoString(ctx.getResource(
					"/WEB-INF/liferay-look-and-feel-ext.xml"))
			};

			ThemeLocalUtil.init(ctx, null, true, xmls, pluginPackage);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Check web settings

		if (_log.isDebugEnabled()) {
			_log.debug("Check web settings");
		}

		try {
			String xml = Http.URLtoString(ctx.getResource("/WEB-INF/web.xml"));

			checkWebSettings(xml);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Last modified paths

		if (_log.isDebugEnabled()) {
			_log.debug("Last modified paths");
		}

		if (_lastModifiedPaths == null) {
			_lastModifiedPaths = CollectionFactory.getHashSet();

			String[] pathsArray = PropsUtil.getArray(
				PropsUtil.LAST_MODIFIED_PATHS);

			for (int i = 0; i < pathsArray.length; i++) {
				_lastModifiedPaths.add(pathsArray[i]);
			}
		}

		// Process global startup events

		if (_log.isDebugEnabled()) {
			_log.debug("Process global startup events");
		}

		try {
			EventsProcessor.process(PropsUtil.getArray(
				PropsUtil.GLOBAL_STARTUP_EVENTS), true);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Initialize companies

		String[] webIds = PortalInstances.getWebIds();

		for (int i = 0; i < webIds.length; i++) {
			PortalInstances.initCompany(ctx, webIds[i]);
		}
		
		// See LEP-2885. Don't flush hot deploy events until after the portal
		// has initialized.

		PortalInitableUtil.flushInitables();
		HotDeployUtil.flushEvents();
		
		servletContext = getServletContext(); // cache this for backdoor use
	}

	public void callParentService(
			HttpServletRequest req, HttpServletResponse res)
		throws IOException, ServletException {

		super.service(req, res);
	}

	@Override
	public void service(HttpServletRequest req, HttpServletResponse res)
		throws IOException, ServletException {

		if (ShutdownUtil.isShutdown()) {
			res.setContentType(Constants.TEXT_HTML);

			String html = ContentUtil.get(
				"com/liferay/portal/dependencies/shutdown.html");

			res.getOutputStream().print(html);

			return;
		}

		HttpSession ses = req.getSession();

		// Company id

		long companyId = PortalInstances.getCompanyId(req);

		CompanyThreadLocal.setCompanyId(companyId);

		// CTX

		ServletContext ctx = getServletContext();

		req.setAttribute(WebKeys.CTX, ctx);

		// Struts module config

		ModuleConfig moduleConfig = getModuleConfig(req);

		// Last modified check

		if (GetterUtil.getBoolean(
				PropsUtil.get(PropsUtil.LAST_MODIFIED_CHECK))) {

			String path = req.getPathInfo();

			if ((path != null) && _lastModifiedPaths.contains(path)) {
				ActionMapping mapping =
					(ActionMapping)moduleConfig.findActionConfig(path);

				LastModifiedAction lastModifiedAction =
					(LastModifiedAction)InstancePool.get(mapping.getType());

				String lmKey = lastModifiedAction.getLastModifiedKey(req);

				if (lmKey != null) {
					long ifModifiedSince =
						req.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);

					if (ifModifiedSince <= 0) {
						lastModifiedAction.setLastModifiedValue(lmKey, lmKey);
					}
					else {
						String lmValue =
							lastModifiedAction.getLastModifiedValue(lmKey);

						if (lmValue != null) {
							res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

							return;
						}
						else {
							lastModifiedAction.setLastModifiedValue(
								lmKey, lmKey);
						}
					}
				}
			}
		}

		// Portlet session tracker

		if (ses.getAttribute(WebKeys.PORTLET_SESSION_TRACKER) == null ) {
			ses.setAttribute(
				WebKeys.PORTLET_SESSION_TRACKER,
				PortletSessionTracker.getInstance());
		}

		// Portlet Request Processor

		PortletRequestProcessor portletReqProcessor =
			(PortletRequestProcessor)ctx.getAttribute(
				WebKeys.PORTLET_STRUTS_PROCESSOR);

		if (portletReqProcessor == null) {
			portletReqProcessor =
				PortletRequestProcessor.getInstance(this, moduleConfig);

			ctx.setAttribute(
				WebKeys.PORTLET_STRUTS_PROCESSOR, portletReqProcessor);
		}

		// Tiles definitions factory

		if (ctx.getAttribute(TilesUtilImpl.DEFINITIONS_FACTORY) == null) {
			ctx.setAttribute(
				TilesUtilImpl.DEFINITIONS_FACTORY,
				ctx.getAttribute(TilesUtilImpl.DEFINITIONS_FACTORY));
		}

		Object applicationAssociate = ctx.getAttribute(WebKeys.ASSOCIATE_KEY);

		if (ctx.getAttribute(WebKeys.ASSOCIATE_KEY) == null) {
			ctx.setAttribute(WebKeys.ASSOCIATE_KEY, applicationAssociate);
		}

		// Set character encoding

		String strutsCharEncoding =
			PropsUtil.get(PropsUtil.STRUTS_CHAR_ENCODING);

		req.setCharacterEncoding(strutsCharEncoding);

		// Encrypt request

		if (ParamUtil.get(req, WebKeys.ENCRYPT, false)) {
			try {
				Company company = CompanyLocalServiceUtil.getCompanyById(
					companyId);

				req = new EncryptedServletRequest(req, company.getKeyObj());
			}
			catch (Exception e) {
			}
		}

		// Current URL

		String completeURL = Http.getCompleteURL(req);

		if ((Validator.isNotNull(completeURL)) &&
			(completeURL.indexOf("j_security_check") == -1)) {

			completeURL = completeURL.substring(
				completeURL.indexOf("://") + 3, completeURL.length());

			completeURL = completeURL.substring(
				completeURL.indexOf("/"), completeURL.length());
		}

		if (Validator.isNull(completeURL)) {
			completeURL = PortalUtil.getPathMain();
		}

		req.setAttribute(WebKeys.CURRENT_URL, completeURL);

		// Login

		long userId = PortalUtil.getUserId(req);
		String remoteUser = req.getRemoteUser();

		// Is JAAS enabled?

		if (!GetterUtil.getBoolean(
				PropsUtil.get(PropsUtil.PORTAL_JAAS_ENABLE))) {

			String jRemoteUser = (String)ses.getAttribute("j_remoteuser");

			if (jRemoteUser != null) {
				remoteUser = jRemoteUser;

				ses.removeAttribute("j_remoteuser");
			}
		}

		if ((userId > 0) && (remoteUser == null)) {
			remoteUser = String.valueOf(userId);
		}

		// WebSphere will not return the remote user unless you are
		// authenticated AND accessing a protected path. Other servers will
		// return the remote user for all threads associated with an
		// authenticated user. We use ProtectedServletRequest to ensure we get
		// similar behavior across all servers.

		req = new ProtectedServletRequest(req, remoteUser);

		if ((userId > 0) || (remoteUser != null)) {

			// Set the principal associated with this thread

			String name = String.valueOf(userId);

			if (remoteUser != null) {
				name = remoteUser;
			}

			PrincipalThreadLocal.setName(name);
		}

		if ((userId <= 0) && (remoteUser != null)) {
			try {

				// User id

				userId = GetterUtil.getLong(remoteUser);

				// Pre login events

				EventsProcessor.process(PropsUtil.getArray(
					PropsUtil.LOGIN_EVENTS_PRE), req, res);

				// User

				User user = UserLocalServiceUtil.getUserById(userId);

				UserLocalServiceUtil.updateLastLogin(
					userId, req.getRemoteAddr());

				// User id

				ses.setAttribute(WebKeys.USER_ID, new Long(userId));

				// User locale

				ses.setAttribute(Globals.LOCALE_KEY, user.getLocale());

				// Post login events

				EventsProcessor.process(PropsUtil.getArray(
					PropsUtil.LOGIN_EVENTS_POST), req, res);
			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}

		// Process pre service events

		try {
			EventsProcessor.process(PropsUtil.getArray(
				PropsUtil.SERVLET_SERVICE_EVENTS_PRE), req, res);
		}
		catch (Exception e) {
			_log.error(e, e);

			req.setAttribute(PageContext.EXCEPTION, e);

			StrutsUtil.forward(
				PropsUtil.get(
					PropsUtil.SERVLET_SERVICE_EVENTS_PRE_ERROR_PAGE),
				ctx, req, res);
		}

		try {

			// Struts service

			callParentService(req, res);
		}
		finally {

			// Process post service events

			try {
				EventsProcessor.process(PropsUtil.getArray(
					PropsUtil.SERVLET_SERVICE_EVENTS_POST), req, res);
			}
			catch (Exception e) {
				_log.error(e, e);
			}

			res.addHeader(
				_LIFERAY_PORTAL_REQUEST_HEADER, ReleaseInfo.getReleaseInfo());

			// Clear the company id associated with this thread

			CompanyThreadLocal.setCompanyId(0);

			// Clear the principal associated with this thread

			PrincipalThreadLocal.setName(null);
		}
	}

	@Override
	public void destroy() {
		long[] companyIds = PortalInstances.getCompanyIds();

		for (int i = 0; i < companyIds.length; i++) {
			destroyCompany(companyIds[i]);
		}

		try {
			EventsProcessor.process(PropsUtil.getArray(
				PropsUtil.GLOBAL_SHUTDOWN_EVENTS), true);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Parent

		super.destroy();
	}

	protected void checkWebSettings(String xml) throws DocumentException {
		SAXReader reader = fixSAXReaderSecurity(SAXReaderFactory.getInstance(false));

		Document doc = reader.read(new StringReader(xml));

		Element root = doc.getRootElement();

		int timeout = GetterUtil.getInteger(
			PropsUtil.get(PropsUtil.SESSION_TIMEOUT));

		Element sessionConfig = root.element("session-config");

		if (sessionConfig != null) {
			String sessionTimeout =
				sessionConfig.elementText("session-timeout");

			timeout = GetterUtil.getInteger(sessionTimeout, timeout);
		}

		PropsUtil.set(PropsUtil.SESSION_TIMEOUT, Integer.toString(timeout));
	}

	protected void destroyCompany(long companyId) {

		// Destroy portlets

		try {
			Iterator itr = PortletLocalServiceUtil.getPortlets(
				companyId).iterator();

			while (itr.hasNext()) {
				Portlet portlet = (Portlet)itr.next();

				PortletInstanceFactory.destroy(portlet);
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Process shutdown events

		if (_log.isDebugEnabled()) {
			_log.debug("Process shutdown events");
		}

		try {
			EventsProcessor.process(PropsUtil.getArray(
				PropsUtil.APPLICATION_SHUTDOWN_EVENTS),
				new String[] {String.valueOf(companyId)});
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	private static final String _LIFERAY_PORTAL_REQUEST_HEADER =
		"Liferay-Portal";

	private static Log _log = LogFactory.getLog(MainServlet.class);

	private Set _lastModifiedPaths;
	
	/*
	 * Implements a fix for bug#901787 on a newly constructed
	 * SAXReader.
	 */
	private static SAXReader fixSAXReaderSecurity(SAXReader saxReader) {
		try {
			saxReader.setFeature("http://xml.org/sax/features/external-general-entities",   false);
			saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		}
		catch (SAXException e) {
//			m_logger.error("fixSAXReaderSecurity( SAXException ):  ", e);
			saxReader = null;
		}
		return saxReader;
	}
}
