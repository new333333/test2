package com.sitescape.ef.portletadapter.portlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.util.Constants;
import com.sitescape.util.GetterUtil;

public class PortletContextImpl implements PortletContext {

	private ServletContext ctx;
	private String ctxName;
	
	private static final Log log = LogFactory.getLog(PortletContextImpl.class);
	
	public PortletContextImpl(ServletContext ctx) {
		this.ctx = ctx;
		this.ctxName = GetterUtil.getString(ctx.getServletContextName());
	}
	
	public String getServerInfo() {
		throw new UnsupportedOperationException();
	}

	public PortletRequestDispatcher getRequestDispatcher(String path) {
		RequestDispatcher rd = null;

		try {
			rd = ctx.getRequestDispatcher(path);
		}
		catch (IllegalArgumentException iae) {
			return null;
		}

		// Workaround for bug in Jetty that returns the default request
		// dispatcher instead of null for an invalid path

		if ((rd != null) &&
			(rd.getClass().getName().equals(
				"org.mortbay.jetty.servlet.Dispatcher"))) {

			// Dispatcher[/,default[org.mortbay.jetty.servlet.Default]]

			String rdToString = rd.toString();

			String rdPath = rdToString.substring(11, rdToString.indexOf(","));

			if (rdPath.equals(Constants.SLASH) &&
				!path.equals(Constants.SLASH)) {

				rd = null;
			}
		}

		if (rd != null) {
			return new PortletRequestDispatcherImpl(rd, this, path);
		}
		else {
			return null;
		}
	}

	public PortletRequestDispatcher getNamedDispatcher(String name) {
		throw new UnsupportedOperationException();
	}

	public InputStream getResourceAsStream(String path) {
		return ctx.getResourceAsStream(path);
	}

	public int getMajorVersion() {
		throw new UnsupportedOperationException();
	}

	public int getMinorVersion() {
		throw new UnsupportedOperationException();
	}

	public String getMimeType(String file) {
		return ctx.getMimeType(file);
	}

	public String getRealPath(String path) {
		return ctx.getRealPath(path);
	}

	public Set getResourcePaths(String path) {
		return ctx.getResourcePaths(path);
	}

	public URL getResource(String path) throws MalformedURLException {
		if ((path == null) || (!path.startsWith(Constants.SLASH))) {
			throw new MalformedURLException();
		}

		return ctx.getResource(path);
	}

	public Object getAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return ctx.getAttribute(name);

	}

	public Enumeration getAttributeNames() {
		return ctx.getAttributeNames();
	}

	public String getInitParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return ctx.getInitParameter(name);

	}

	public Enumeration getInitParameterNames() {
		return ctx.getInitParameterNames();
	}

	public void log(String msg) {
		log.info(msg);
	}

	public void log(String message, Throwable throwable) {
		log.info(message, throwable);
	}

	public void removeAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		ctx.removeAttribute(name);

	}

	public void setAttribute(String name, Object obj) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		ctx.setAttribute(name, obj);
	}

	public String getPortletContextName() {
		return ctxName;
	}

}
