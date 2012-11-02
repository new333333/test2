package org.kabling.teaming.install.client.wizard;

import com.google.gwt.user.client.ui.Widget;

public interface IWizardPage<T>
{
	boolean isValid();

	String getPageTitle();

	Widget getWizardUI();

	boolean canFinish();

	void save();
	
	IWizardPage<T> getPreviousPage();
	
	IWizardPage<T> getNextPage();
}
