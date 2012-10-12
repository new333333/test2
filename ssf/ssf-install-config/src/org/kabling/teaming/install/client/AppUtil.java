package org.kabling.teaming.install.client;

import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.images.InstallConfigImageBundle;
import org.kabling.teaming.install.shared.ProductInfo;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public final class AppUtil
{

	private final static InstallConfigImageBundle IMG_BUNDLE = GWT.create(InstallConfigImageBundle.class);
	private final static AppResource RBUNDLE = GWT.create(AppResource.class);
	private final static InstallServiceAsync INSTALL_SERVICE = GWT.create(InstallService.class);
	private final static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);
	private static ProductInfo productInfo = null;

	private AppUtil()
	{
	}

	/**
	 * Get the Application resource.
	 * 
	 * @return the app resource
	 */
	public static AppResource getAppResource()
	{
		return RBUNDLE;
	}

	/**
	 * Get the Application image bundle.
	 * 
	 * @return the app image bundle
	 */
	public static InstallConfigImageBundle getAppImageBundle()
	{
		return IMG_BUNDLE;
	}

	/**
	 * Get the RPC Service 
	 * 
	 * @return
	 */
	public static InstallServiceAsync getInstallService()
	{
		return INSTALL_SERVICE;
	}

	/**
	 * Get the event bus for the application
	 * 
	 * @return
	 */
	public static EventBus getEventBus()
	{
		return EVENT_BUS;
	}

	/**
	 * Get the product information 
	 * @return
	 */
	public static ProductInfo getProductInfo()
	{
		return productInfo;
	}

	public static void setProductInfo(ProductInfo info)
	{
		productInfo = info;
	}
}
