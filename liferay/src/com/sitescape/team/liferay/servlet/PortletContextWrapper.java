package com.sitescape.team.liferay.servlet;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.Portlet;
import javax.portlet.PreferencesValidator;
import javax.servlet.ServletContext;

import com.liferay.portal.job.Scheduler;
import com.liferay.portal.kernel.lar.PortletDataHandler;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.servlet.URLEncoder;
import com.liferay.portal.kernel.smtp.MessageListener;

public class PortletContextWrapper extends com.liferay.portal.servlet.PortletContextWrapper {

	private Map rscBundles;
	
	public PortletContextWrapper(String portletName, ServletContext servletContext, Portlet portletInstance, Indexer indexerInstance, Scheduler schedulerInstance, URLEncoder urlEncoder, PortletDataHandler portletDataHandler, MessageListener smtpMessageListener, PreferencesValidator prefsValidator, Map resourceBundles, Map customUserAttributes) {
		super(portletName, servletContext, portletInstance, indexerInstance,
				schedulerInstance, urlEncoder, portletDataHandler, smtpMessageListener,
				prefsValidator, resourceBundles, customUserAttributes);
		rscBundles = resourceBundles;
	}

	public ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle resourceBundle = (ResourceBundle) rscBundles.get(
			locale.toString());

		if(resourceBundle == null) {
			resourceBundle = (ResourceBundle) rscBundles.get(
					locale.getLanguage());
		}
		
		if (resourceBundle == null) {
			resourceBundle = (ResourceBundle) rscBundles.get(
				Locale.getDefault().toString());
		}

		if (resourceBundle == null) {
			resourceBundle = (ResourceBundle) rscBundles.get(
				Locale.getDefault().getLanguage());
		}

		return resourceBundle;
	}

}
