package org.kabling.teaming.install.client.leftnav;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.widgets.ClickableFlowPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class LeftNavItemPanel extends Composite implements ClickHandler
{
	private LeftNavItemType type;

	public LeftNavItemPanel(String name,LeftNavItemType type)
	{
		this.type = type;
		
		ClickableFlowPanel fPanel = new ClickableFlowPanel();
		fPanel.addClickHandler(this);
		
		Label leftNavItem = new Label(name);
		leftNavItem.addStyleName("leftnav-item");
		//DON'T ADD CLICK HANDLER FOR LABEL 
		
		fPanel.add(leftNavItem);
		
		initWidget(fPanel);
	}

	@Override
	public void onClick(ClickEvent event)
	{
		AppUtil.getEventBus().fireEvent(new LeftNavSelectionEvent(type));
	}
	
}
