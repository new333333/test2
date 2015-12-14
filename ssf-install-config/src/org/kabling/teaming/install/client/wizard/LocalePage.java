package org.kabling.teaming.install.client.wizard;

import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.shared.Environment;
import org.kabling.teaming.install.shared.FilrLocale;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class LocalePage implements IWizardPage<InstallerConfig>
{
	private InstallerConfig config;
	private FlowPanel content;

	private ConfigWizard wizard;

	private AppResource RBUNDLE = AppUtil.getAppResource();
	private ListBox defaultLocaleListBox;
	private List<FilrLocale> filrLocales;

	public LocalePage(ConfigWizard wizard, InstallerConfig config)
	{
		this.wizard = wizard;
		this.config = config;
	}

	@Override
	public String getPageTitle()
	{
		return RBUNDLE.defaultLocale();
	}

	@Override
	public Widget getWizardUI()
	{
		if (content == null)
		{
			content = new FlowPanel();
			content.addStyleName("wizardPage");

			Label descLabel = new Label(RBUNDLE.defaultLocaleDesc());
			descLabel.addStyleName("wizardPageDesc");
			content.add(descLabel);

			InlineLabel label = new InlineLabel(RBUNDLE.defaultLocaleColon());
			label.addStyleName("defaultLocaleLabel");
			content.add(label);

			defaultLocaleListBox = new ListBox(false);
			content.add(defaultLocaleListBox);
			
			initUIWithData();
		}
		return content;
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	@Override
	public boolean canFinish()
	{
		return true;
	}

	public void initUIWithData()
	{
		final Environment environment = config.getEnvironment();
		final String language = environment.getDefaultLanguage();
		final String country = environment.getDefaultCountry();
		AppUtil.getInstallService().getFilrLocales(new AsyncCallback<List<FilrLocale>>()
		{

			@Override
			public void onSuccess(List<FilrLocale> result)
			{
				filrLocales = result;
				if (result != null)
				{
					int i = 0;
					for (FilrLocale locale : result)
					{
						defaultLocaleListBox.addItem(locale.getDisplayName());
						
						if (locale.getCountry().equals(country) && locale.getLanguage().equals(language))
							defaultLocaleListBox.setSelectedIndex(i);
						
						i++;
					}
				}
			}

			@Override
			public void onFailure(Throwable caught)
			{
			}
		});

	}

	@Override
	public void save()
	{
		Environment environment = config.getEnvironment();

		if (environment != null)
		{
			int selectedIndex = defaultLocaleListBox.getSelectedIndex();
			if (selectedIndex < 0)
				selectedIndex = 0;

			FilrLocale filrLocale = filrLocales.get(selectedIndex);
			environment.setDefaultCountry(filrLocale.getCountry());
			environment.setDefaultLanguage(filrLocale.getLanguage());
		}
	}

	@Override
	public IWizardPage<InstallerConfig> getPreviousPage()
	{
		if (wizard.configPage.getDeploymentType().equals("local"))
			return wizard.dbLocalPage;
		
		return wizard.lucenePage;
	}

	@Override
	public IWizardPage<InstallerConfig> getNextPage()				
	{
		return null;
	}

	@Override
	public FocusWidget getWidgetToFocus()
	{
		return wizard.getFinishButton();
	}
}
