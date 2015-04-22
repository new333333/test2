package org.kabling.teaming.install.client.widgets;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.HelpData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;

public class WarningDialog extends DlgBox
{
	private String warningMsg;

	public WarningDialog(String warningMsg,DlgButtonMode mode)
	{
		super(false, true, mode);
		this.warningMsg = warningMsg;
	}

	public boolean isValid()
	{
		return true;
	}

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel content = new FlowPanel();

		FlowPanel messagePanel = new FlowPanel();
		messagePanel.addStyleName("gw-dlgConfirm");
		content.add(messagePanel);

		Image img = new Image(AppUtil.getAppImageBundle().warnLarge());
		messagePanel.add(img);

		// description label
		HTML label = new HTML(warningMsg);
		label.addStyleName("gw-dlgConfirmDesc");
		messagePanel.add(label);
		
		return content;
	}

	@Override
	public Object getDataFromDlg()
	{
		return Boolean.TRUE;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}

	@Override
	public HelpData getHelpData()
	{
		return null;
	}
}
