package org.kabling.teaming.install.client.widgets;

import org.kabling.teaming.install.client.AppUtil;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;

public class StatusIndicator extends PopupPanel
{
	private InlineLabel label;

	public StatusIndicator(String message)
	{
		// Set the style for this widget
		setStyleName("statusIndicator");

		// Add a image and style
		FlowPanel dialogContent = new FlowPanel();
		Image img = new Image(AppUtil.getAppImageBundle().loading16());
		dialogContent.add(img);

		if (message == null)
			message = AppUtil.getAppResource().pleaseWait();

		label = new InlineLabel(message);
		dialogContent.add(label);
		
		setModal(true);
		setWidget(dialogContent);
	}
	
	public void setText(String text)
	{
		label.setText(text);
	}
}