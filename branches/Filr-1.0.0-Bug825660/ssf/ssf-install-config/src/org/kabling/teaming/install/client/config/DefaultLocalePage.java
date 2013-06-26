package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.shared.Environment;
import org.kabling.teaming.install.shared.FilrLocale;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

public class DefaultLocalePage extends ConfigPageDlgBox
{
	ListBox defaultLocaleListBox;
	List<FilrLocale> filrLocales;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		HTML titleDescLabel = new HTML(RBUNDLE.defaultLocaleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);

		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		contentPanel.addStyleName("webServicePageContent");

		InlineLabel label = new InlineLabel(RBUNDLE.defaultLocaleColon());
		label.addStyleName("defaultLocaleLabel");
		contentPanel.add(label);

		defaultLocaleListBox = new ListBox(false);
		contentPanel.add(defaultLocaleListBox);
		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
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

		return config;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}

	@Override
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
	public HelpData getHelpData()
	{
		HelpData helpData = new HelpData();
		helpData.setPageId("language");

		return helpData;
	}

	@Override
	public boolean editSuccessful(Object obj)
	{
		List<LeftNavItemType> sectionsToUpdate = new ArrayList<LeftNavItemType>();
		sectionsToUpdate.add(LeftNavItemType.ENVIRONMENT);
		// Save the configuration
		AppUtil.getInstallService().saveConfiguration((InstallerConfig) obj, sectionsToUpdate, saveConfigCallback);

		// Return false, we will close if the save is successful
		return false;
	}

}
