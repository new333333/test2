package org.kabling.teaming.install.client.widgets;

import com.google.gwt.user.client.ui.RadioButton;

public class VibeRadioButton extends RadioButton
{
	private Object data;
	public VibeRadioButton(String name,String label,Object data)
	{
		super(name,label);
		this.setData(data);
	}
	
	public Object getData()
	{
		return data;
	}
	
	public void setData(Object data)
	{
		this.data = data;
	}

}
