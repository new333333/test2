package org.kabling.teaming.install.client.wizard;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigFinishEnableEvent;
import org.kabling.teaming.install.client.ConfigFinishEnableEvent.ConfigFinishEnableEventHandler;
import org.kabling.teaming.install.client.ConfigNextButtonEnableEvent;
import org.kabling.teaming.install.client.ConfigNextButtonEnableEvent.ConfigNextEnableEventHandler;
import org.kabling.teaming.install.client.GwtClientHelper;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.StatusIndicator;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;

public class ConfigWizard extends PopupPanel implements IWizard, ClickHandler, ConfigFinishEnableEventHandler,
		ConfigNextEnableEventHandler
{
	private int currentPage;
	private List<IWizardPage<InstallerConfig>> pages = new ArrayList<IWizardPage<InstallerConfig>>();
	private FlowPanel wizardContentPanel;
	private FlowPanel wizard;
	private FlowPanel m_captionImagePanel;
	private Label m_caption;
	private FlowPanel m_footerPanel;
	private Button previousButton;
	private FocusWidget nextButton;
	private Button finishButton;
	private InstallerConfig config;
	private Label errorLabel;
	private FlowPanel errorPanel;
	private StatusIndicator loadingWidget;

	private AppResource RBUNDLE = AppUtil.getAppResource();

	public ConfigWizard(InstallerConfig config)
	{
		super(false, true);
		this.config = config;

		addStyleName("configWizardDlg");

		wizard = new FlowPanel();
		wizard.setStyleName("installConfigWizard");
		setWidget(wizard);

		// Header Panel
		wizard.add(createHeader());

		// Error Panel
		wizard.add(createErrorPanel());

		// Content
		wizardContentPanel = new FlowPanel();
		wizardContentPanel.setStyleName("installConfigWizardPage");
		wizard.add(wizardContentPanel);

		// Footer buttons
		wizard.add(createFooter());

		// Add pages (3 page wizard)
		// Initial Page
		IWizardPage<InstallerConfig> configPage = new InitialConfigPage(config);
		pages.add(configPage);

		// Database Page
		IWizardPage<InstallerConfig> dbPage = new DatabaseConfigPage(this, config);
		pages.add(dbPage);

		// Lucene Page
		IWizardPage<InstallerConfig> lucenePage = new LuceneConfigPage(this, config);
		pages.add(lucenePage);

		// Storage Page
		IWizardPage<InstallerConfig> storagePage = new StoragePage(this, config);
		pages.add(storagePage);

		// Listen for events (enable/disable finish button, enable/disable next button)
		AppUtil.getEventBus().addHandler(ConfigFinishEnableEvent.TYPE, this);
		AppUtil.getEventBus().addHandler(ConfigNextButtonEnableEvent.TYPE, this);

		// Show first page
		currentPage = 0;
		showPage(currentPage);
	}

	private void showPage(int pageToShow)
	{
		if (wizardContentPanel.getWidgetCount() > 0)
			wizardContentPanel.remove(0);

		updateButtons();

		// Clear any errors as we are moving to a new page
		setErrorMessage(null);

		IWizardPage<InstallerConfig> newPageToShow = pages.get(pageToShow);
		wizardContentPanel.add(newPageToShow.getWizardUI());

		IWizardPage<InstallerConfig> currentWizardPage = pages.get(currentPage);
		m_caption.setText(currentWizardPage.getPageTitle());
	}

	@Override
	public void previousPage()
	{
		if (currentPage > 0)
			currentPage = currentPage - 1;

		showPage(currentPage);

	}

	@Override
	public void nextPage()
	{
		IWizardPage<InstallerConfig> page = pages.get(currentPage);

		if (!page.isValid())
			return;

		if (currentPage < pages.size())
			currentPage = currentPage + 1;

		showPage(currentPage);
	}

	@Override
	public void finish()
	{
		// We only need to go through pages for advanced configuration
		// For default configuration, we will go with the defaults in installer.xml
		if (config.isAdvancedConfiguration())
		{
			// Call save
			for (int page = 0; page < pages.size(); page++)
			{
				IWizardPage<InstallerConfig> wizardPage = pages.get(page);

				if (!wizardPage.isValid())
				{
					// Show error message in error panel
					showPage(page);
					return;
				}
				else
				{
					wizardPage.save();
				}
			}
		}
		showStatusIndicator(AppUtil.getAppResource().pleaseWait());
		AppUtil.getInstallService().saveConfiguration(config, new SaveConfigCallback());
	}

	private void updateButtons()
	{
		if (currentPage == 0)
		{
			previousButton.setEnabled(false);
			nextButton.setEnabled(true);
		}
		else if (currentPage == pages.size() - 1)
		{
			previousButton.setEnabled(true);
			nextButton.setEnabled(false);
		}
		else
		{
			previousButton.setEnabled(true);
			nextButton.setEnabled(true);
		}

		finishButton.setEnabled(pages.get(currentPage).canFinish());

	}

	private Panel createHeader()
	{
		FlowPanel flowPanel;

		flowPanel = new FlowPanel();
		flowPanel.setStyleName("teamingDlgBoxHeader");
		if (GwtClientHelper.jsIsIE())
			flowPanel.addStyleName("teamingDlgBoxHeaderBG_IE");
		else
			flowPanel.addStyleName("teamingDlgBoxHeaderBG_NonIE");

		m_captionImagePanel = new FlowPanel();
		m_captionImagePanel.setStyleName("teamingDlgBoxHeader-captionImagePanel");
		flowPanel.add(m_captionImagePanel);

		m_caption = new Label();
		m_caption.setStyleName("teamingDlgBoxHeader-captionLabel");
		flowPanel.add(m_caption);
		return flowPanel;
	}

	private Panel createErrorPanel()
	{
		errorPanel = new FlowPanel();
		errorPanel.addStyleName("dlgErrorPanel");
		errorPanel.setVisible(false);

		errorLabel = new Label();
		errorLabel.setStyleName("errorLabel");

		errorPanel.add(errorLabel);
		return errorPanel;
	}

	private Panel createFooter()
	{
		m_footerPanel = new FlowPanel();

		// Associate this panel with its stylesheet.
		m_footerPanel.setStyleName("teamingDlgBoxFooter");

		previousButton = new Button(RBUNDLE.previous());

		previousButton.addClickHandler(this);
		previousButton.addStyleName("teamingButton");
		m_footerPanel.add(previousButton);

		nextButton = new Button(RBUNDLE.next());
		nextButton.addClickHandler(this);
		nextButton.addStyleName("teamingButton");
		m_footerPanel.add(nextButton);

		finishButton = new Button(RBUNDLE.finish());

		finishButton.addClickHandler(this);
		finishButton.addStyleName("teamingButton");
		finishButton.addStyleName("finishButton");
		m_footerPanel.add(finishButton);

		return m_footerPanel;
	}

	@Override
	public void onClick(ClickEvent event)
	{
		if (event.getSource() == nextButton)
			nextPage();
		else if (event.getSource() == previousButton)
			previousPage();
		else if (event.getSource() == finishButton)
			finish();
	}

	@Override
	public void onEvent(ConfigFinishEnableEvent event)
	{
		finishButton.setEnabled(event.isEnabled());
	}

	@Override
	public void onEvent(ConfigNextButtonEnableEvent event)
	{
		nextButton.setEnabled(event.isEnabled());
	}

	public void setErrorMessage(String errorMessage)
	{
		if (errorMessage != null)
		{
			errorLabel.setText(errorMessage);
			errorPanel.setVisible(true);
		}
		else
		{
			errorPanel.setVisible(false);
		}
	}

	class SaveConfigCallback implements AsyncCallback<Void>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			hideStatusIndicator();
			setErrorMessage(caught.getMessage());
		}

		@Override
		public void onSuccess(Void coid)
		{
			loadingWidget.setText(AppUtil.getAppResource().creatingDatabase());
			AppUtil.getInstallService().createDatabase(config.getDatabase(), new CreateDatabaseCallback());
		}
	}

	class CreateDatabaseCallback implements AsyncCallback<Void>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			hideStatusIndicator();
			setErrorMessage("Configuration failed on the server.");
		}

		@Override
		public void onSuccess(Void coid)
		{
			loadingWidget.setText(AppUtil.getAppResource().updatingDatabase());
			AppUtil.getInstallService().updateDatabase(config.getDatabase(), new UpdateDatabaseCallback());
		}
	}

	class UpdateDatabaseCallback implements AsyncCallback<Void>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			hideStatusIndicator();
			setErrorMessage("Configuration failed on the server.");
		}

		@Override
		public void onSuccess(Void coid)
		{
			loadingWidget.setText(AppUtil.getAppResource().reconfiguringServer());
			AppUtil.getInstallService().reconfigure(false, new ReconfigureCallback());
		}
	}

	class ReconfigureCallback implements AsyncCallback<Void>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			hideStatusIndicator();
			setErrorMessage("Configuration failed on the server.");
		}

		@Override
		public void onSuccess(Void coid)
		{
			loadingWidget.setText(AppUtil.getAppResource().startingServer());
			AppUtil.getInstallService().startFilrServer(new StartFilrCallback());
		}
	}

	class StartFilrCallback implements AsyncCallback<Void>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			hideStatusIndicator();
			setErrorMessage("Configuration failed on the server.");
		}

		@Override
		public void onSuccess(Void coid)
		{
			hideStatusIndicator();
			ConfigWizard.this.hide(true);
		}
	}

	public void showStatusIndicator(String msg)
	{
		if (loadingWidget == null)
			loadingWidget = new StatusIndicator(msg);

		loadingWidget.setText(msg);

		loadingWidget.setPopupPositionAndShow(new PopupPanel.PositionCallback()
		{
			@Override
			public void setPosition(int offsetWidth, int offsetHeight)
			{
				int left = ConfigWizard.this.getAbsoluteLeft() + ConfigWizard.this.getOffsetWidth() / 2 - offsetWidth
						/ 2;
				int top = ConfigWizard.this.getAbsoluteTop() + ConfigWizard.this.getOffsetHeight() / 2 - offsetHeight
						/ 2;
				loadingWidget.setPopupPosition(left, top);
			}
		});
	}

	public void hideStatusIndicator()
	{
		if (loadingWidget != null)
			loadingWidget.hide();
	}
}
