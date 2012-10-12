package org.kabling.teaming.install.client;

import org.kabling.teaming.install.shared.ProductInfo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class InstallConfigMain implements EntryPoint
{
	private final ProductInfoCallback productInfoCallback = new ProductInfoCallback();

	public void onModuleLoad()
	{
		//Main Entry Point to the UI
		//We will load the product information and determine what login screen to display
		AppUtil.getInstallService().getProductInfo(productInfoCallback);

	}

	class ProductInfoCallback implements AsyncCallback<ProductInfo>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			// TODO: What are we doing here?
			GWT.log("Failed to get product info");
		}

		@Override
		public void onSuccess(ProductInfo result)
		{
			// Show the login screen
			// Login screen should show the images (filr, teaming) based on the product info
			// TODO: Maybe we check the validity of the product before we show the login screen

			//Save the product information as we may have to display certain configuration
			//based on the product 
			AppUtil.setProductInfo(result);

			//Temporary, for windows, we won't show the login screen
			if (Navigator.getPlatform().startsWith("Win"))
			{
				MainUILayoutPanel panel = new MainUILayoutPanel();
				RootLayoutPanel.get().add(panel);
			}
			else
			{
				LoginUIPanel loginUIPanel = new LoginUIPanel(result);
				RootPanel.get("installConfig").add(loginUIPanel);
			}
		}

	}
}
