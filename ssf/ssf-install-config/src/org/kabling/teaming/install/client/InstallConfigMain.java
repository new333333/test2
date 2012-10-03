package org.kabling.teaming.install.client;

import org.kabling.teaming.install.shared.ProductInfo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class InstallConfigMain implements EntryPoint
{
	private final ProductInfoCallback productInfoCallback = new ProductInfoCallback();

	public void onModuleLoad()
	{
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

			AppUtil.setProductInfo(result);

			LoginUIPanel loginUIPanel = new LoginUIPanel(result);
			RootPanel.get("installConfig").add(loginUIPanel);
		}

	}
}
