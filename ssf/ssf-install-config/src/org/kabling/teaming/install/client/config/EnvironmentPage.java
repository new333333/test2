package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.widgets.DlgBox;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class EnvironmentPage extends DlgBox
{

	public EnvironmentPage(InstallerConfig config)
	{
		super(false,true,DlgButtonMode.OkCancel,true);
	}

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		
		Label label = new Label("TODO");
		fPanel.add(label);
		
		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		return null;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
