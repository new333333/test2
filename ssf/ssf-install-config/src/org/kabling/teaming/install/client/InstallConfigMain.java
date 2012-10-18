package org.kabling.teaming.install.client;

import org.kabling.teaming.install.shared.ProductInfo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;

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

			//Temporary, when running the browser from windows, we won't show the login screen
//			if (Navigator.getPlatform().startsWith("Win") || Navigator.getPlatform().startsWith("Mac"))
//			{
//				if (RootLayoutPanel.get().getWidgetCount() > 0)
//					RootLayoutPanel.get().remove(0);
//				
//				MainUILayoutPanel panel = new MainUILayoutPanel();
//				RootLayoutPanel.get().add(panel);
//			}
//			else
			{
				if (RootLayoutPanel.get().getWidgetCount() > 0)
					RootLayoutPanel.get().remove(0);
				
				LoginUIPanel loginUIPanel = new LoginUIPanel();
				RootLayoutPanel.get().add(loginUIPanel);
			}
		}

	}
}
