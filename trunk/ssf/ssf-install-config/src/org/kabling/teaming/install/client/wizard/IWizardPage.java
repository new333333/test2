package org.kabling.teaming.install.client.wizard;

import com.google.gwt.user.client.ui.Widget;

public interface IWizardPage<T>
{
	String isValid();
	
	String getPageTitle();
	
	Widget getWizardUI();
	
	boolean canFinish();
	
	void initUIWithData(T object);
	
	void save();
}
