package org.kabling.teaming.install.client.wizard;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigFinishEnableEvent;
import org.kabling.teaming.install.client.ConfigFinishEnableEvent.ConfigFinishEnableEventHandler;
import org.kabling.teaming.install.client.ConfigNextButtonEnableEvent;
import org.kabling.teaming.install.client.ConfigNextButtonEnableEvent.ConfigNextEnableEventHandler;
import org.kabling.teaming.install.client.GwtClientHelper;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class ConfigWizard extends Composite implements IWizard, ClickHandler, ConfigFinishEnableEventHandler,ConfigNextEnableEventHandler
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

	public ConfigWizard(InstallerConfig config)
	{
		this.config = config;
		
		wizard = new FlowPanel();
		wizard.setStyleName("installConfigWizard");
		initWidget(wizard);

		// Header Panel
		wizard.add(createHeader());

		// Content
		wizardContentPanel = new FlowPanel();
		wizardContentPanel.setStyleName("installConfigWizardPage");
		wizard.add(wizardContentPanel);

		// Footer buttons
		wizard.add(createFooter());

		config = new InstallerConfig();
		
		// Add pages
		//Initial Page
		IWizardPage<InstallerConfig> page = new InitialConfigPage();
		page.initUIWithData(config);
		pages.add(page);
		
		//Database Page
		page = new DatabaseConfigPage();
		page.initUIWithData(config);
		pages.add(page);
		
		//Lucene Page
		page = new DatabaseConfigPage();
		page.initUIWithData(config);
		pages.add(page);

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
		if (currentPage < pages.size())
			currentPage = currentPage + 1;

		showPage(currentPage);
	}

	@Override
	public void finish()
	{
		// Call save
		for (int page = 0; page < pages.size(); page++)
		{
			IWizardPage<InstallerConfig> wizardPage = pages.get(page);

			if (wizardPage.isValid() != null)
			{
				// Show error message in error panel
				showPage(page);
				break;
			}
			else
			{
				// TODO save the page
			}
		}
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

	private Panel createFooter()
	{
		m_footerPanel = new FlowPanel();

		// Associate this panel with its stylesheet.
		m_footerPanel.setStyleName("teamingDlgBoxFooter");

		previousButton = new Button("Previous");

		previousButton.addClickHandler(this);
		previousButton.addStyleName("teamingButton");
		m_footerPanel.add(previousButton);

		nextButton = new Button("Next");
		nextButton.addClickHandler(this);
		nextButton.addStyleName("teamingButton");
		m_footerPanel.add(nextButton);

		finishButton = new Button("Finish");

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
}
