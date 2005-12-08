package com.sitescape.ef.portletadapter.portlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

import com.sitescape.ef.util.Constants;

public class PortletSessionImpl implements PortletSession {

 	protected static final String PORTLET_SCOPE_NAMESPACE = "javax.portlet.p.";
	
	private HttpSession ses;
	private String portletName;
	private PortletContext ctx;
	private boolean invalid;
	private long creationTime;
	private long lastAccessedTime;
	private int interval;
	private boolean _new;
	
	public PortletSessionImpl(HttpSession ses, String portletName, PortletContext portletContext) {
		this.ses = ses;
		this.portletName = portletName;
		this.ctx = portletContext;
		this.invalid = false;
		this.creationTime = new Date().getTime();
		this.lastAccessedTime = creationTime;
		this.interval = ses.getMaxInactiveInterval();
		this._new = true;
	}

	public Object getAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		return getAttribute(name, PortletSession.PORTLET_SCOPE);

	}

	public Object getAttribute(String name, int scope) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		if (scope == PortletSession.PORTLET_SCOPE) {
			return ses.getAttribute(getPortletScopeName(name));
		}
		else {
			return ses.getAttribute(name);
		}
	}

	public Enumeration getAttributeNames() {
		if (invalid) {
			throw new IllegalStateException();
		}

		return getAttributeNames(PortletSession.PORTLET_SCOPE);

	}

	public Enumeration getAttributeNames(int scope) {
		if (invalid) {
			throw new IllegalStateException();
		}

		if (scope == PortletSession.PORTLET_SCOPE) {
			List attributeNames = new ArrayList();

			Enumeration enu = ses.getAttributeNames();

			while (enu.hasMoreElements()) {
				String name = (String)enu.nextElement();

				StringTokenizer st = new StringTokenizer(name, "?");

				if (st.countTokens() == 2) {
					if (st.nextToken().equals(
							PORTLET_SCOPE_NAMESPACE + portletName)) {

						attributeNames.add(st.nextToken());
					}
				}
			}

			return Collections.enumeration(attributeNames);
		}
		else {
			return ses.getAttributeNames();
		}

	}

	public long getCreationTime() {
		if (invalid) {
			throw new IllegalStateException();
		}

		return creationTime;
	}

	public String getId() {
		return ses.getId();
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;

	}

	public int getMaxInactiveInterval() {
		return interval;
	}

	public void invalidate() {
		if (invalid) {
			throw new IllegalStateException();
		}

		ses.invalidate();

		invalid = true;

	}

	public boolean isNew() {
		if (invalid) {
			throw new IllegalStateException();
		}

		return _new;
	}

	public void removeAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		removeAttribute(name, PortletSession.PORTLET_SCOPE);

	}

	public void removeAttribute(String name, int scope) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		if (scope == PortletSession.PORTLET_SCOPE) {
			ses.removeAttribute(getPortletScopeName(name));
		}
		else {
			ses.removeAttribute(name);
		}
	}

	public void setAttribute(String name, Object value) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		setAttribute(name, value, PortletSession.PORTLET_SCOPE);
	}

	public void setAttribute(String name, Object value, int scope) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		if (scope == PortletSession.PORTLET_SCOPE) {
			ses.setAttribute(getPortletScopeName(name), value);
		}
		else {
			ses.setAttribute(name, value);
		}
	}

	public void setMaxInactiveInterval(int interval) {
		this.interval = interval;
	}

	public PortletContext getPortletContext() {
		return ctx;
	}
	
	boolean isValid() {
		return false;
	}
	
	private String getPortletScopeName(String name) {
		return PORTLET_SCOPE_NAMESPACE + portletName + Constants.QUESTION +
			name;
	}

}
