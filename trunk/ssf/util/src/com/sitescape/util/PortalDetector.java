package com.sitescape.util;

public class PortalDetector {

	public static final String LIFERAY_CLASS = "/com/liferay/portal/kernel/portlet/LiferayPortlet.class";

	private static PortalDetector instance;

	private Boolean liferay;

	public static boolean isLiferay() {
		PortalDetector pd = getInstance();

		if (pd.liferay == null) {
			Class c = pd.getClass();

			if (c.getResource(LIFERAY_CLASS) != null) {
				pd.liferay = Boolean.TRUE;
			}
			else {
				pd.liferay = Boolean.FALSE;
			}
		}

		return pd.liferay.booleanValue();
	}

	private static PortalDetector getInstance() {
		if (instance == null) {
			synchronized (ServerDetector.class) {
				if (instance == null) {
					instance = new PortalDetector();
				}
			}
		}

		return instance;
	}
}