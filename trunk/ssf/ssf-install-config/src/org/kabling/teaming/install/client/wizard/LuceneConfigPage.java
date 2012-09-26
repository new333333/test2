package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LuceneConfigPage implements IWizardPage<InstallerConfig>
{


	public LuceneConfigPage()
	{
	}

	@Override
	public String isValid()
	{
		return null;
	}

	@Override
	public Widget getWizardUI()
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.add(new Label("Lucene Config Page"));

		return fPanel;
	}

	@Override
	public String getPageTitle()
	{
		return "Lucene";
	}

	@Override
	public boolean canFinish()
	{
		return false;
	}

	@Override
	public void save()
	{
	}

	@Override
	public void initUIWithData(InstallerConfig object)
	{
	}

}
