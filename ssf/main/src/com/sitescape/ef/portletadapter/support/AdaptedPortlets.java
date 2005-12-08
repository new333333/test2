package com.sitescape.ef.portletadapter.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import com.sitescape.ef.SingletonViolationException;
import com.sitescape.ef.portletadapter.portlet.PortletConfigImpl;
import com.sitescape.ef.portletadapter.portlet.PortletContextImpl;

public class AdaptedPortlets implements ServletContextAware, InitializingBean,
	DisposableBean {

	private static AdaptedPortlets instance; // singleton instance
	
	private ServletContext servletContext;
	
	private Map portlets = new HashMap();
	private PortletContext portletContext;
	
	private String[] portletNames; // names of adapted portlets 
	
	public AdaptedPortlets() {
		if(instance == null)
			instance = this;
		else
			throw new SingletonViolationException(AdaptedPortlets.class);
	}
	
	protected static AdaptedPortlets getInstance() {
		return instance;
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;		
	}
	protected ServletContext getServletContext() {
		return servletContext;
	}

	public void setPortletNames(String[] portletNames) {
		this.portletNames = portletNames;
	}
	
	private boolean isAdaptedPortlet(String portletName) {
		if(portletNames == null)
			return false;
		
		for(int i = 0; i < portletNames.length; i++) {
			if(portletNames[i].equals(portletName))
				return true; // match found
		}
		
		return false;
	}
	
	public void afterPropertiesSet() throws Exception {
		portletContext = new PortletContextImpl(getServletContext());

		// Since portlet.xml is processed by the portlet container, it will be
    	// validated any way. So we will not bother with validation here. 
        SAXReader reader = new SAXReader(false);  
        Document doc = null;
        try {
			doc = reader.read(getServletContext().getResourceAsStream("/WEB-INF/portlet.xml"));
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}

		Element root = doc.getRootElement();
		for(Iterator it = root.elementIterator("portlet"); it.hasNext();) {
			Element portletElem = (Element) it.next();
			String portletName = portletElem.elementText("portlet-name");
			
			if(isAdaptedPortlet(portletName)) {			
				String portletClassName = portletElem.elementText("portlet-class");
				Map params = new HashMap();
				for(Iterator it2 = portletElem.elementIterator("init-param"); it2.hasNext();) {
					Element initParamElem = (Element) it2.next();
					String initParamName = initParamElem.elementText("name");
					String initParamValue = initParamElem.elementText("value");
					params.put(initParamName, initParamValue);
				}
				
				Vector mimeTypes = new Vector();
				for(Iterator it3 = portletElem.element("supports").elementIterator("mime-type"); it3.hasNext();) {
					mimeTypes.add(((Element) it3.next()).getText());
				}
				
				String resourceBundle = portletElem.elementText("resource-bundle");
				PortletConfigImpl portletConfig = new PortletConfigImpl(portletName, portletContext, params, resourceBundle);
				try {
					portlets.put(portletName, new PortletInfo(portletName, portletClassName, portletConfig, mimeTypes));
				} catch (Exception e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			}
		}		
	}

	public static PortletInfo getPortletInfo(String portletName) {
		return (PortletInfo) getInstance().portlets.get(portletName); 
	}
	
	public static PortletContext getPortletContext() {
		return getInstance().portletContext;
	}

	public void destroy() throws Exception {
		for(Iterator i = portlets.values().iterator(); i.hasNext();) {
			PortletInfo portletInfo = (PortletInfo) i.next();
			portletInfo.getPortlet().destroy();
		}
	}
}
