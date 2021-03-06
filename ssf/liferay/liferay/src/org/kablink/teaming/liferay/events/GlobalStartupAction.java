/**
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

package org.kablink.teaming.liferay.events;

import com.liferay.portal.comm.CommLink;
import com.liferay.portal.events.FixCounterAction;
import com.liferay.portal.events.FixOracleAction;
import com.liferay.portal.jcr.JCRFactoryUtil;
import com.liferay.portal.kernel.deploy.auto.AutoDeployDir;
import com.liferay.portal.kernel.deploy.auto.AutoDeployListener;
import com.liferay.portal.kernel.deploy.auto.AutoDeployUtil;
import com.liferay.portal.kernel.deploy.hot.HotDeployListener;
import com.liferay.portal.kernel.deploy.hot.HotDeployUtil;
import com.liferay.portal.smtp.SMTPServerUtil;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.struts.SimpleAction;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.util.GetterUtil;
import com.liferay.util.InstancePool;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="GlobalStartupAction.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class GlobalStartupAction extends SimpleAction {
/*
 * This class is a temporary substitute/patch for Liferay's 
 * com.liferay.portal.events.GlobalStartupAction class with a fix for the 
 * problem reported in LEP-2855. Also see ICEcore issue #1204 for additional
 * details. This class will be no longer necessary once/when we upgrade to
 * Liferay 4.3.1 or later since the later Liferay versions already contain
 * fix for this problem.
 */
	public static List getAutoDeployListeners() {
		List list = new ArrayList();

		String[] autoDeployListeners =
			PropsUtil.getArray(PropsUtil.AUTO_DEPLOY_LISTENERS);

		for (int i = 0; i < autoDeployListeners.length; i++) {
			try {
				if (_log.isDebugEnabled()) {
					_log.debug("Instantiating " + autoDeployListeners[i]);
				}

				AutoDeployListener autoDeployListener =
					(AutoDeployListener)Class.forName(
						autoDeployListeners[i]).newInstance();

				list.add(autoDeployListener);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}

		return list;
	}

	public static List getHotDeployListeners() {
		List list = new ArrayList();

		String[] hotDeployListeners =
			PropsUtil.getArray(PropsUtil.HOT_DEPLOY_LISTENERS);

		for (int i = 0; i < hotDeployListeners.length; i++) {
			try {
				if (_log.isDebugEnabled()) {
					_log.debug("Instantiating " + hotDeployListeners[i]);
				}

				HotDeployListener hotDeployListener =
					(HotDeployListener)Class.forName(
						hotDeployListeners[i]).newInstance();

				list.add(hotDeployListener);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}

		return list;
	}

	public void run(String[] ids) throws ActionException {

		// JCR

		try {
			if (GetterUtil.getBoolean(PropsUtil.get(
					PropsUtil.JCR_INITIALIZE_ON_STARTUP))) {

				JCRFactoryUtil.initialize();
			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		// Hot deploy

		_log.debug("Registering hot deploy listeners");

		Iterator itr = getHotDeployListeners().iterator();

		while (itr.hasNext()) {
			HotDeployListener hotDeployListener = (HotDeployListener)itr.next();

			HotDeployUtil.registerListener(hotDeployListener);
		}

		// Auto deploy

		try {
			if (PrefsPropsUtil.getBoolean(PropsUtil.AUTO_DEPLOY_ENABLED)) {
				if (_log.isInfoEnabled()) {
					_log.info("Registering auto deploy directories");
				}

				File deployDir = new File(
					PrefsPropsUtil.getString(PropsUtil.AUTO_DEPLOY_DEPLOY_DIR));
				File destDir = new File(
					PrefsPropsUtil.getString(PropsUtil.AUTO_DEPLOY_DEST_DIR));
				long interval = PrefsPropsUtil.getLong(
					PropsUtil.AUTO_DEPLOY_INTERVAL);
				int blacklistThreshold = PrefsPropsUtil.getInteger(
					PropsUtil.AUTO_DEPLOY_BLACKLIST_THRESHOLD);

				List autoDeployListeners = getAutoDeployListeners();

				AutoDeployDir autoDeployDir = new AutoDeployDir(
					"defaultAutoDeployDir", deployDir, destDir, interval,
					blacklistThreshold, autoDeployListeners);

				AutoDeployUtil.registerDir(autoDeployDir);
			}
			else {
				if (_log.isInfoEnabled()) {
					_log.info("Not registering auto deploy directories");
				}
			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		// SMTP server

		if (GetterUtil.getBoolean(PropsUtil.get(
				PropsUtil.SMTP_SERVER_ENABLED))) {

			int port = GetterUtil.getInteger(PropsUtil.get(
				PropsUtil.SMTP_SERVER_PORT));

			SMTPServerUtil.setPort(port);

			SMTPServerUtil.start();
		}

		// JGroups

		CommLink.getInstance();

		// Other required events

		runEvent(FixOracleAction.class.getName(), ids);
		runEvent(FixCounterAction.class.getName(), ids);
	}

	protected void runEvent(String className, String[] ids)
		throws ActionException {

		SimpleAction action = (SimpleAction)InstancePool.get(className);

		action.run(ids);
	}

	private static Log _log = LogFactory.getLog(GlobalStartupAction.class);

}