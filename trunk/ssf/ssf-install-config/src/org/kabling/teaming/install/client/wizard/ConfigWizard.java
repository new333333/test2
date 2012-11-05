package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigWizardSucessEvent;
import org.kabling.teaming.install.client.GwtClientHelper;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.StatusIndicator;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;

public class ConfigWizard extends PopupPanel implements IWizard, ClickHandler
		
{
	private IWizardPage<InstallerConfig> currentPage;
	private FlowPanel wizardContentPanel;
	private FlowPanel wizard;
	private FlowPanel m_captionImagePanel;
	private Label m_caption;
	private HorizontalPanel m_footerPanel;
	private Button previousButton;
	private Button nextButton;
	private Button finishButton;
	private InstallerConfig config;
	private Label errorLabel;
	private FlowPanel errorPanel;
	private StatusIndicator loadingWidget;

	private AppResource RBUNDLE = AppUtil.getAppResource();
	InitialConfigPage configPage;
	DatabaseConfigPage dbPage;
	LuceneConfigPage lucenePage;
	//StoragePage storagePage;
	ImportConfigPage importPage;
	
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
		configPage = new InitialConfigPage(this,config);

		// Database Page
		dbPage = new DatabaseConfigPage(this, config);

		// Lucene Page
		lucenePage = new LuceneConfigPage(this, config);

		//Import Page
		importPage = new ImportConfigPage(this);
		
		// Show first page
		currentPage = configPage;
		showPage(currentPage);
	}

	private void showPage(IWizardPage<InstallerConfig> pageToShow)
	{
		currentPage = pageToShow;
		
		updateButtons();

		// Clear any errors as we are moving to a new page
		setErrorMessage(null);

		wizardContentPanel.clear();
		wizardContentPanel.add(currentPage.getWizardUI());

		m_caption.setText(currentPage.getPageTitle());
	}

	@Override
	public void previousPage()
	{
		IWizardPage<InstallerConfig> previousPage = currentPage.getPreviousPage();
		
		if (previousPage != null)
		{
			currentPage = previousPage;
			showPage(currentPage);
		}
	}

	@Override
	public void nextPage()
	{
		if (!currentPage.isValid())
			return;
		
		currentPage.save();
		
		IWizardPage<InstallerConfig> nextPage = currentPage.getNextPage();

		if (nextPage != null)
		{
			currentPage = nextPage;
			showPage(currentPage);
		}
	}

	@Override
	public void finish()
	{
		if (!currentPage.isValid())
			return;
		
		showStatusIndicator(AppUtil.getAppResource().pleaseWait());
		AppUtil.getInstallService().saveConfiguration(config, new SaveConfigCallback());
	}

	private void updateButtons()
	{
		if (currentPage.getPreviousPage() == null)
		{
			previousButton.setEnabled(false);
			nextButton.setEnabled(true);
		}
		else if (currentPage.getNextPage() == null)
		{
			previousButton.setEnabled(true);
			nextButton.setEnabled(false);
		}
		else
		{
			previousButton.setEnabled(true);
			nextButton.setEnabled(true);
		}

		finishButton.setEnabled(currentPage.canFinish());

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
		m_footerPanel = new HorizontalPanel();
		m_footerPanel.setWidth("100%");
		
		// Associate this panel with its stylesheet.
		m_footerPanel.setStyleName("teamingDlgBoxFooter");

		previousButton = new Button(RBUNDLE.previous());
		//We want the next/previous button to go all the way right
		previousButton.addClickHandler(this);
		previousButton.addStyleName("teamingButton");
		m_footerPanel.add(previousButton);
		m_footerPanel.setCellWidth(previousButton, "100%");

		nextButton = new Button(RBUNDLE.next());
		nextButton.addClickHandler(this);
		nextButton.addStyleName("teamingButton");
		m_footerPanel.add(nextButton);
		m_footerPanel.setCellHorizontalAlignment(nextButton, HasAlignment.ALIGN_RIGHT);

		finishButton = new Button(RBUNDLE.finish());

		finishButton.addClickHandler(this);
		finishButton.addStyleName("teamingButton");
		m_footerPanel.setCellHorizontalAlignment(finishButton, HasAlignment.ALIGN_RIGHT);
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
			setErrorMessage("Creating the database failed.");
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
			setErrorMessage("Updating the database failed.");
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
			setErrorMessage("Reconfiguring the Filr Server failed.");
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
			setErrorMessage("Starting the Filr Server failed.");
		}

		@Override
		public void onSuccess(Void coid)
		{
			hideStatusIndicator();
			//Set the flag that we have configured
			AppUtil.getProductInfo().setConfigured(true);
			
			AppUtil.getEventBus().fireEvent(new ConfigWizardSucessEvent(true));
			
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
	
	public Button getFinishButton()
	{
		return finishButton;
	}
	
	public Button getNextButton()
	{
		return nextButton;
	}
}
