package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;

public class JavaJDKPage extends ConfigPageDlgBox
{
	private VibeTextBox jvmHeapSizeTextBox;
	private InlineLabel jdkHomeValueLabel;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		//Title
		HTML titleDescLabel = new HTML(RBUNDLE.jdkPageTitleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);

		//Page Description
		HTML descLabel = new HTML(RBUNDLE.jdkPageDesc());
		descLabel.addStyleName("configPageDescLabel");
		fPanel.add(descLabel);

		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		contentPanel.addStyleName("jdkPageContent");

		// JAVA JDK HOME
		FlowPanel jdkHomePanel = new FlowPanel();
		fPanel.add(jdkHomePanel);

		InlineLabel jdkHomeLabel = new InlineLabel(RBUNDLE.javaHomeColon());
		jdkHomePanel.add(jdkHomeLabel);

		jdkHomeValueLabel = new InlineLabel();
		jdkHomePanel.add(jdkHomeValueLabel);

		// JVM Size Panel
		FlowPanel jvmSizePanel = new FlowPanel();
		fPanel.add(jvmSizePanel);
		InlineLabel authLabel = new InlineLabel(RBUNDLE.jvmHeapSizeColon());
		jvmSizePanel.add(authLabel);

		jvmHeapSizeTextBox = new VibeTextBox();
		jvmSizePanel.add(jvmHeapSizeTextBox);

		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		String jvmHeapSize = jvmHeapSizeTextBox.getText();

		// We got to have a value for the heap size
		if (jvmHeapSize.isEmpty())
		{
			setErrorMessage(RBUNDLE.requiredField());
			return null;
		}

		// The value has to end with "g" or "m" and it cannot contain fractional values
		if (jvmHeapSize.contains(".")
				|| !(jvmHeapSize.endsWith("M") || jvmHeapSize.endsWith("m") || jvmHeapSize.endsWith("G") || jvmHeapSize
						.endsWith("g")))
		{
			setErrorMessage(RBUNDLE.jvmHeapSizeInvalid());
			return null;
		}

		// Let's make sure they we have all digits except the last character.
		try
		{
			Integer.valueOf(jvmHeapSize.substring(0, jvmHeapSize.length() - 1));
		}
		catch (NumberFormatException e)
		{
			setErrorMessage(RBUNDLE.jvmHeapSizeInvalid());
			jvmHeapSizeTextBox.showError();
			return null;
		}
		clearErrorMessage();
		
		config.setJvmMemory(jvmHeapSize);
		return config;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return jvmHeapSizeTextBox;
	}

	@Override
	public void initUIWithData()
	{
		// Set the JDK HOME
		jdkHomeValueLabel.setText(config.getEnvironment().getJdkHome());

		// Heap Size
		if (config.getJvmMemory() != null)
			jvmHeapSizeTextBox.setText(config.getJvmMemory());
	}

	@Override
	public HelpData getHelpData()
	{
		HelpData helpData =  new HelpData();
		helpData.setPageId("jdk");
		
		return helpData;
	}

	@Override
	public boolean editSuccessful(Object obj)
	{
		List<LeftNavItemType> sectionsToUpdate = new ArrayList<LeftNavItemType>();
		sectionsToUpdate.add(LeftNavItemType.JAVA_JDK);
		// Save the configuration
		AppUtil.getInstallService().saveConfiguration((InstallerConfig) obj, sectionsToUpdate,saveConfigCallback);

		// Return false, we will close if the save is successful
		return false;
	}
}
