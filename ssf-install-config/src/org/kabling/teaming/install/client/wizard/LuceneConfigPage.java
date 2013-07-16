package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.client.widgets.VibePasswordTextBox;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.Lucene;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class LuceneConfigPage implements IWizardPage<InstallerConfig>
{

	private InstallerConfig config;
	private VibeTextBox luceneAddrTextBox;
	private GwValueSpinner rmiPortSpinner;
	private ConfigWizard wizard;
	private boolean validatedCredentials;
	private FlowPanel content;
	private AppResource RBUNDLE = AppUtil.getAppResource();
	private VibeTextBox luceneUserNameTextBox;
	private VibePasswordTextBox luceneUserPasswordTextBox;

	public LuceneConfigPage(ConfigWizard wizard, InstallerConfig config)
	{
		this.config = config;
		this.wizard = wizard;
	}

	@Override
	public Widget getWizardUI()
	{
		validatedCredentials = false;

		if (content == null)
		{
			content = new FlowPanel();
			content.addStyleName("wizardPage");

			HTML descLabel = new HTML(RBUNDLE.wizLucenePageTitleDesc());
			descLabel.addStyleName("wizardPageDesc");
			content.add(descLabel);

			FlexTable table = new FlexTable();
			content.add(table);

			int row = 0;
			FlexTable hostTable = new FlexTable();
			content.add(hostTable);
			{
				row = 0;
				// Host Name or IP Address
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostNameColon());
				hostTable.setWidget(row, 0, keyLabel);
				hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				luceneAddrTextBox = new VibeTextBox();
				hostTable.setWidget(row, 1, luceneAddrTextBox);
				hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			}

			{
				row++;
				// RMI Port
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.rmiPortColon());
				hostTable.setWidget(row, 0, keyLabel);
				hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				rmiPortSpinner = new GwValueSpinner(1199, 1024, 9999, RBUNDLE.defaultIs1199());
				rmiPortSpinner.addStyleName("luceneConfigWizRmiSpinnerLabel");
				hostTable.setWidget(row, 1, rmiPortSpinner);
				hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			}
			
			{
				row++;
				// Server Name
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.luceneUserNameColon());
				hostTable.setWidget(row, 0, keyLabel);
				hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				luceneUserNameTextBox = new VibeTextBox();
				hostTable.setWidget(row, 1, luceneUserNameTextBox);
				hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			}
			
			{
				row++;
				// Server Password
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.luceneUserPasswordColon());
				hostTable.setWidget(row, 0, keyLabel);
				hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				luceneUserPasswordTextBox = new VibePasswordTextBox();
				hostTable.setWidget(row, 1, luceneUserPasswordTextBox);
				hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			}

			HTML footerLabel = new HTML(
					"The Lucene search server can be:<br> - The integrated search server in the Filr virtual appliance (local)<br>"
							+ "-The Lucene virtual appliance that is included with Filr, running separately.");
			footerLabel.addStyleName("configWizFooterLabel");
			content.add(footerLabel);

			initUIWithData();
		}
		return content;
	}

	@Override
	public String getPageTitle()
	{
		return RBUNDLE.lucene();
	}

	@Override
	public boolean canFinish()
	{
		return false;
	}

	@Override
	public boolean isValid()
	{
		String host = luceneAddrTextBox.getText();
		long port = rmiPortSpinner.getValueAsInt();
		String serverLogin = luceneUserNameTextBox.getText();
		String serverPassword = luceneUserPasswordTextBox.getText();

		if (host.isEmpty() || port < 1024 || serverLogin.isEmpty() || serverPassword.isEmpty())
		{
			wizard.setErrorMessage(RBUNDLE.allFieldsRequired());
			return false;
		}

//		if (Navigator.getPlatform().startsWith("Win") || Navigator.getPlatform().startsWith("Mac"))
//		{
//			return true;
//		}
		
//		if (!validatedCredentials)
//		{
//			wizard.showStatusIndicator(RBUNDLE.validatingLuceneServer());
//			AppUtil.getInstallService().isLuceneServerValid(host, port, new LuceneAuthCallback());
//			return false;
//		}
		return true;
	}

	@Override
	public void save()
	{
		Lucene lucene = config.getLucene();

		if (lucene != null)
		{
			lucene.setLocation("server");
			lucene.setIndexHostName(luceneAddrTextBox.getText());
			lucene.setRmiPort((int) rmiPortSpinner.getValue());
			lucene.setServerLogin(luceneUserNameTextBox.getText());
			lucene.setServerPassword(luceneUserPasswordTextBox.getText());
		}
	}

	public void initUIWithData()
	{
		Lucene lucene = config.getLucene();

		if (lucene != null)
		{
			rmiPortSpinner.setValue(lucene.getRmiPort());
			luceneUserNameTextBox.setText(lucene.getServerLogin());
		}
	}

	class LuceneAuthCallback implements AsyncCallback<Boolean>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			wizard.hideStatusIndicator();
			wizard.setErrorMessage(RBUNDLE.unableToConnectLuceneServer());
		}

		@Override
		public void onSuccess(Boolean result)
		{
			if (!result)
				return;

			validatedCredentials = true;
			wizard.hideStatusIndicator();
			wizard.finish();
		}
	}

	@Override
	public IWizardPage<InstallerConfig> getPreviousPage() {
		return wizard.dbPage;
	}

	@Override
	public IWizardPage<InstallerConfig> getNextPage() {
		return wizard.localePage;
	}

	@Override
	public FocusWidget getWidgetToFocus()
	{
		return luceneAddrTextBox;
	}

}
