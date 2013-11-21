package org.kabling.teaming.install.client;

import org.kabling.teaming.install.client.config.ClusteringPage;
import org.kabling.teaming.install.client.config.DatabasePage;
import org.kabling.teaming.install.client.config.DefaultLocalePage;
import org.kabling.teaming.install.client.config.ExportConfigurationPage;
import org.kabling.teaming.install.client.config.InboundEmailPage;
import org.kabling.teaming.install.client.config.JavaJDKPage;
import org.kabling.teaming.install.client.config.LicenseInformationPage;
import org.kabling.teaming.install.client.config.LucenePage;
import org.kabling.teaming.install.client.config.NetworkInformationPage;
import org.kabling.teaming.install.client.config.OutboundEmailPage;
import org.kabling.teaming.install.client.config.RequestsAndConnectionsPage;
import org.kabling.teaming.install.client.config.ReverseProxyPage;
import org.kabling.teaming.install.client.config.WebDavPage;
import org.kabling.teaming.install.client.config.WebServicesPage;
import org.kabling.teaming.install.client.leftnav.LeftNavContentPanel;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.client.leftnav.LeftNavSelectionEvent;
import org.kabling.teaming.install.client.leftnav.LeftNavSelectionEvent.LeftNavSelectEventHandler;
import org.kabling.teaming.install.client.widgets.DlgBox;
import org.kabling.teaming.install.client.wizard.ConfigWizard;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainUILayoutPanel extends Composite implements LeftNavSelectEventHandler, ResizeHandler
{
	private MainContentPanel mainPanel;
	private ConfigPageDlgBox dlg;
	private LicenseInformationPage licensePage;

	public MainUILayoutPanel()
	{
		// Using DockLayout panel for laying out the UI
		DockLayoutPanel p = new DockLayoutPanel(Unit.PX);
		// Show the header
		p.addNorth(getHeaderPanel(), 60);

		// Left navigation
		p.addWest(getLeftPanel(), 225);
		p.add(getMainContent());

		initWidget(p);

		// If the product is not configured, we will show the initial configuration wizard
		if (!AppUtil.getProductInfo().isConfigured())
		{
			// Show Config Wizard
			AppUtil.getInstallService().getConfiguration(new GetConfigCallback());
		}

		// Look for left nav item selections
		AppUtil.getEventBus().addHandler(LeftNavSelectionEvent.TYPE, this);

		// Adding window resize listener
		Window.addResizeHandler(this);
	}

	private Widget getHeaderPanel()
	{
		FlowPanel div = new FlowPanel();
		div.addStyleName("mainheader");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setWidth("100%");
		div.add(hPanel);

		Image mainHeaderImg = new Image(AppUtil.getAppImageBundle().loginFilrProductInfo());
		mainHeaderImg.addStyleName("mainHeaderProductImg");
		hPanel.add(mainHeaderImg);

		FlowPanel headerActionsPanel = new FlowPanel();
		headerActionsPanel.addStyleName("masterhead-actions");
		hPanel.add(headerActionsPanel);
		hPanel.setCellHorizontalAlignment(headerActionsPanel, HasAlignment.ALIGN_RIGHT);
		
		InlineLabel label = new InlineLabel("root");
		label.addStyleName("username");
		headerActionsPanel.add(label);

		Anchor homeAnchor = new Anchor("Home");
		homeAnchor.addStyleName("logout");
		headerActionsPanel.add(homeAnchor);
		if (AppUtil.getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			homeAnchor.setHref("/");
		}
		
		Anchor logoutAction = new Anchor("Logout");
		logoutAction.addStyleName("logout");
		//For filr, redirect to /logout
		if (AppUtil.getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			logoutAction.setHref("/logout");
		}
		else
		{
			logoutAction.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					AppUtil.getInstallService().logout(new AsyncCallback<Void>()
					{

						@Override
						public void onFailure(Throwable caught)
						{

						}

						@Override
						public void onSuccess(Void result)
						{
							// Goto login Page
							LoginUIPanel loginUIPanel = new LoginUIPanel();
							if (RootLayoutPanel.get().getWidgetCount() > 0)
								RootLayoutPanel.get().remove(0);
							RootLayoutPanel.get().add(loginUIPanel);
						}
					});
				}
			});
		}

		headerActionsPanel.add(logoutAction);

		return div;
	}

	private Widget getLeftPanel()
	{
		FlowPanel div = new FlowPanel();
		div.addStyleName("leftnav");

		Label label = new Label(AppUtil.getAppResource().configuration());
		label.addStyleName("leftnav-header");
		div.add(label);

		div.add(new LeftNavContentPanel());

		return div;
	}

	private Widget getMainContent()
	{
		mainPanel = new MainContentPanel();
		return mainPanel;
	}

	@Override
	public void onEvent(LeftNavSelectionEvent event)
	{
		
		dlg = null;
		licensePage = null;
		
		// Left navigation item has been clicked, we will show the item related
		// information to be configured in a custom dialog
		LeftNavItemType itemType = event.getType();
		
		switch (itemType)
		{
		case WEBDAV_AUTHENTICATION:
			dlg = new WebDavPage();
			dlg.createAllDlgContent(AppUtil.getAppResource().webDavAuthentication(), dlg, null, null);
			break;

		case CLUSTERING:
			dlg = new ClusteringPage();
			dlg.createAllDlgContent(AppUtil.getAppResource().clustering(), dlg, null, null);
			break;

		case DATABASE:
			dlg = new DatabasePage();
			dlg.createAllDlgContent(AppUtil.getAppResource().dbConnection(), dlg, null, null);
			break;
			
		case NETWORK:
			dlg = new NetworkInformationPage();
			dlg.createAllDlgContent(AppUtil.getAppResource().network(), dlg, null, null);
			break;
			
		case ENVIRONMENT:
			dlg = new DefaultLocalePage();
			dlg.createAllDlgContent(AppUtil.getAppResource().defaultLocale(), dlg, null, null);
			break;

		case WEB_SERVICES:
			dlg = new WebServicesPage();
			dlg.createAllDlgContent(AppUtil.getAppResource().webServices(), dlg, null, null);
			break;

		case JAVA_JDK:
			dlg = new JavaJDKPage();
			dlg.createAllDlgContent(AppUtil.getAppResource().jvmSettings(), dlg, null, null);
			break;

		case REQUESTS_AND_CONNECTIONS:
			dlg = new RequestsAndConnectionsPage();
			dlg.createAllDlgContent(AppUtil.getAppResource().requestsAndConnections(), dlg, null, null);
			break;

		case INBOUND_EMAIL:
			dlg = new InboundEmailPage();
			dlg.createAllDlgContent(AppUtil.getAppResource().inboundEmail(), dlg, null, null);
			break;

		case OUTBOUND_EMAIL:
			dlg = new OutboundEmailPage();
			dlg.createAllDlgContent(AppUtil.getAppResource().outboundEmail(), dlg, null, null);
			break;

		case NOVELL_ACCESS_MANAGER:
			dlg = new ReverseProxyPage();
			dlg.createAllDlgContent(AppUtil.getAppResource().reverseProxy(), dlg, null, null);
			break;
			
		case LUCENE:
			dlg = new LucenePage();
			dlg.createAllDlgContent(AppUtil.getAppResource().lucene(), dlg, null, null);
			break;

		case LICENSE_INFORMATION:
			licensePage = new LicenseInformationPage();
			licensePage.createAllDlgContent(AppUtil.getAppResource().license(), licensePage, null, null);
			break;
			
		default:
			return;
		}

		if (dlg != null)
			showDialog(dlg);
		else
			showDialog(licensePage);
	}

	private void showDialog(DlgBox dlg)
	{
			// Set up the initial pop up position
			dlg.setPopupPosition(mainPanel.getAbsoluteLeft(), mainPanel.getAbsoluteTop());
	
			// Show the footer buttons on the left side
			Panel footerPanel = dlg.getFooterPanel();
			if (footerPanel != null)
				footerPanel.addStyleName("leftAlignFooter");
	
			// Default size, fill up the whole main panel
			int width = mainPanel.getOffsetWidth() - 15;
			int height = mainPanel.getOffsetHeight() - 15;
			dlg.setSize(width + "px", height + "px");
	
			// Show the dialog
			dlg.show();
	}
	/**
	 * Get the configuration - installer.xml data
	 *
	 */
	class GetConfigCallback implements AsyncCallback<InstallerConfig>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			// TODO: What are we doing here?
			GWT.log("Failed to get configuration");
		}

		@Override
		public void onSuccess(InstallerConfig result)
		{
			// Show the configuration wizard
			final ConfigWizard wizard = new ConfigWizard(result);
			wizard.setGlassEnabled(true);
			wizard.center();
		}

	}

	@Override
	public void onResize(ResizeEvent event)
	{
		// If we ae showing the config page, it fills up the right content
		// If user's resizes the page, we want to resize the dialog so that,
		// it stays locked up on hte right side
		if (dlg != null || licensePage != null)
		{
			int width = mainPanel.getOffsetWidth() - 15;
			int height = mainPanel.getOffsetHeight() - 15;
			if (dlg != null)
			{
				dlg.setSize(width + "px", height + "px");
				DOM.setStyleAttribute(dlg.getElement(), "overflow", "auto");
			}
			else
			{
				licensePage.setSize(width + "px", height + "px");
				DOM.setStyleAttribute(licensePage.getElement(), "overflow", "auto");
			}
		}
	}
}
