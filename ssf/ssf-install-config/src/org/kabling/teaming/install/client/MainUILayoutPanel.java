package org.kabling.teaming.install.client;

import org.kabling.teaming.install.client.config.EnvironmentPage;
import org.kabling.teaming.install.client.leftnav.LeftNavContentPanel;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.client.leftnav.LeftNavSelectionEvent;
import org.kabling.teaming.install.client.leftnav.LeftNavSelectionEvent.LeftNavSelectEventHandler;
import org.kabling.teaming.install.client.widgets.DlgBox;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class MainUILayoutPanel extends Composite  implements LeftNavSelectEventHandler
{
	public MainUILayoutPanel()
	{
		DockLayoutPanel p = new DockLayoutPanel(Unit.PX);
		p.addNorth(getHeaderPanel(), 60);
		p.addWest(getLeftPanel(), 225);
		p.add(getMainContent());
		
		initWidget(p);
		
		AppUtil.getEventBus().addHandler(LeftNavSelectionEvent.TYPE, this);
	}
	
	private Widget getHeaderPanel()
	{
		FlowPanel div = new FlowPanel();
		div.addStyleName("mainheader");
		
		Image mainHeaderImg = new Image(AppUtil.getAppImageBundle().loginFilrProductInfo());
		mainHeaderImg.addStyleName("mainHeaderProductImg");
		div.add(mainHeaderImg);
		
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
		MainContentPanel mainPanel = new MainContentPanel();
		return mainPanel;
	}
	
	@Override
	public void onEvent(LeftNavSelectionEvent event)
	{
		//Based on the left nav type, we can launch the right dialog
		if (event.getType().equals(LeftNavItemType.ENVIRONMENT))
		{
			EnvironmentPage dlg = new EnvironmentPage(null);
			dlg.createAllDlgContent(AppUtil.getAppResource().environment(), null, null, null);
			dlg.show(true);
		}
		
	}
}
