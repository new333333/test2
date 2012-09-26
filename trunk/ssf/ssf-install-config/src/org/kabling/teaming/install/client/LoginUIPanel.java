package org.kabling.teaming.install.client;

import org.kabling.teaming.install.shared.LoginInfo;
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class LoginUIPanel extends Composite implements ClickHandler
{
	private Button loginButton;
	private TextBox userNameTextBox;
	private PasswordTextBox passwordTextBox;

	public LoginUIPanel(ProductInfo productInfo)
	{
		FlowPanel content = new FlowPanel();
		content.setStyleName("loginUIPanel");
		DOM.setElementProperty(content.getElement(), "align", "center");

		initWidget(content);

		FlowPanel loginContainer = new FlowPanel();
		loginContainer.setStyleName("loginContainer");
		content.add(loginContainer);

		// Show Product Image
		Image productImg = null;
		if (productInfo.getType().equals(ProductType.NOVELL_FILR))
		{
			productImg = new Image(AppUtil.getAppImageBundle().loginFilrProductInfo());
		}
		productImg.addStyleName("loginProductImage");
		
		loginContainer.add(productImg);

		// Show Product Version
		{
			FlowPanel productVersionPanel = new FlowPanel();
			productVersionPanel.setStyleName("loginProductVersion");
			loginContainer.add(productVersionPanel);

			Label productInfoLabel = new Label(productInfo.getProductVersion());
			productInfoLabel.addStyleName("loginProductInfoLabel");
			productVersionPanel.add(productInfoLabel);

			Label copyRightLabel = new Label(productInfo.getCopyRight());
			copyRightLabel.addStyleName("loginCopyrightLabel");
			productVersionPanel.add(copyRightLabel);
		}

		loginContainer.add(getBevelLabel());

		// User Name Label/TextBox
		{
			FlowPanel userNamePanel = new FlowPanel();
			userNamePanel.addStyleName("userNamePanel");
			loginContainer.add(userNamePanel);

			InlineLabel userNameLabel = new InlineLabel(AppUtil.getAppResource().userNameColon());
			userNamePanel.add(userNameLabel);

			userNameTextBox = new TextBox();
			userNameTextBox.addStyleName("loginUserNameTextBox");
			userNamePanel.add(userNameTextBox);
		}

		// Password Label/Password TextBox
		{
			FlowPanel passowordPanel = new FlowPanel();
			passowordPanel.addStyleName("passwordPanel");
			loginContainer.add(passowordPanel);

			InlineLabel passwordLabel = new InlineLabel(AppUtil.getAppResource().passwordColon());
			passowordPanel.add(passwordLabel);

			passwordTextBox = new PasswordTextBox();
			passwordTextBox.addStyleName("loginPasswordTextBox");
			passowordPanel.add(passwordTextBox);
		}

		loginContainer.add(getBevelLabel());

		// Login Button
		{
			FlowPanel loginButtonPanel = new FlowPanel();
			loginButtonPanel.addStyleName("loginButtonPanel");
			loginContainer.add(loginButtonPanel);

			loginButton = new Button(AppUtil.getAppResource().login());
			loginButton.addClickHandler(this);
			loginButton.addStyleName("loginButton");
			loginButtonPanel.add(loginButton);
		}

		// Errors Label
		{
			Label errorsLabel = new Label();
			errorsLabel.addStyleName("loginFailed");
			
			errorsLabel.setVisible(false);
			
			content.add(errorsLabel);
		}

	}

	private Label getBevelLabel()
	{
		Label label = new Label();
		label.addStyleName("loginBevel");

		return label;
	}

	@Override
	public void onClick(ClickEvent event)
	{
		if (event.getSource() == loginButton)
		{
			login();
		}
	}
	
	private void login()
	{
		String userName = userNameTextBox.getText();
		String password = passwordTextBox.getText();
		
		//We need to have both the user name and password to login
		//They cannot be empty
		if (userName == null || userName.isEmpty() || password == null || password.isEmpty())
			return;
		
		AppUtil.getInstallService().login(userName, password, new ProductInfoCallback());
	}
	
	class ProductInfoCallback implements AsyncCallback<LoginInfo>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			//TODO: What are we doing here?
			GWT.log("Failed to get product info");
		}

		@Override
		public void onSuccess(LoginInfo result)
		{
			RootPanel.get("installConfig").removeFromParent();
			
			MainUILayoutPanel panel = new MainUILayoutPanel();
			RootLayoutPanel.get().add(panel);
		}
		
	}
	
}
