/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.client;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.ldapbrowser.DirectoryServer;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapSearchInfo;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapServer;
import org.kablink.teaming.gwt.client.ldapbrowser.QueryOutput;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapServerDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.LdapServerDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.view.client.*;

/**
 * ?
 *  
 * @author rvasudevan
 */
public class LDAPBrowser extends DialogBox {
	private AsyncDataProvider<LdapObject>		m_dataProvider;		//
	private CellTree							m_tree;				//
	private DirectoryServer						m_directoryServer;	//
	private final FlowPanel						m_contentPanel;		//
	private GwtTeamingMessages					m_messages;			// Access to Vibe's messages.
	private LdapObject							m_selected;			//
	private LdapSearchInfo						m_searchInfo;		//
	private LDAPTreeModel						m_treeViewModel;	//
	private SingleSelectionModel<LdapObject>	m_selectionModel;	//

	/**
	 * Constructor method.
	 * 
	 * @param m_directoryServer
	 * @param info
	 */
	public LDAPBrowser(DirectoryServer directoryServer, LdapSearchInfo info) {
		m_directoryServer = directoryServer;
		m_searchInfo      = info;
		
		m_messages = GwtTeaming.getMessages();

		if (m_searchInfo == null) {
			m_searchInfo = new LdapSearchInfo();
			if (directoryServer.getDirectoryType().equals(LdapServer.DirectoryType.ACTIVE_DIRECTORY))
			     m_searchInfo.setSearchObjectClass(LdapSearchInfo.RETURN_EVERYTHING_AD  );
			else m_searchInfo.setSearchObjectClass(LdapSearchInfo.RETURN_CONTAINERS_ONLY);
		}

		m_contentPanel = new FlowPanel();
		m_contentPanel.addStyleName("ldapBrowserContent");
		setWidget(m_contentPanel);

		setText("LDAP Browser");

		invokeLdapBrowser();
	}

	/**
	 * Constructor method.
	 * 
	 * @param m_directoryServer
	 */
	public LDAPBrowser(DirectoryServer directoryServer) {
		// Always use the initial form of the constructor.
		this(directoryServer, null);
	}

	private void invokeLdapBrowser() {
		// LDAP Tree Model
		m_treeViewModel = new LDAPTreeModel();
		CellTreeResource treeResource = GWT.create(CellTreeResource.class);
		GwCellTreeMessages treeMessages = GWT.create(GwCellTreeMessages.class);

		// Selection model
		m_selectionModel = new SingleSelectionModel<LdapObject>();
		m_selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				m_selected = m_selectionModel.getSelectedObject();
			}
		});

		// Build the m_tree and set the max node size to be 1000
		// Set the initial value to be ROOT string to differ from LdapObject
		m_tree = new CellTree(m_treeViewModel, "ROOT", treeResource, treeMessages);

		// Active Directory has a max limit of 1000, we will do the same
		m_tree.setDefaultNodeSize(1000);

		// Set open the m_tree so that we can get the data
		m_tree.getRootTreeNode().setChildOpen(0, true);
		m_contentPanel.add(m_tree);

	}

	class LDAPTreeModel implements TreeViewModel {
		@Override
		public <T> NodeInfo<?> getNodeInfo(final T value) {
			// ROOT Node
			if (value instanceof String && value.equals("ROOT")) {
				ArrayList<LdapObject> list = new ArrayList<LdapObject>();
				if (m_directoryServer.getName() != null)
					list.add(new LdapObject(m_directoryServer.getName()));
				else
					list.add(new LdapObject("TREE"));
				ListDataProvider<LdapObject> dataProvider = new ListDataProvider<LdapObject>(list);
				return new DefaultNodeInfo<LdapObject>(dataProvider, new LDAPObjectCell(), m_selectionModel, null);
			}
			else {
				m_dataProvider = new AsyncDataProvider<LdapObject>() {
					@Override
					protected void onRangeChanged(HasData<LdapObject> display) {
						LdapObject ldapObject = (LdapObject) value;
						if (ldapObject.getObjectClass() == null) {
							if (m_searchInfo.getBaseDn() == null) {
								if (m_directoryServer.getDirectoryType().equals(LdapServer.DirectoryType.EDIRECTORY)
										|| m_directoryServer.getDirectoryType().equals(LdapServer.DirectoryType.UNKNOWN))
									m_directoryServer.setUrl("");
								else
								{
									String baseDn = getBaseDnFromUserName(m_directoryServer.getSyncUser());
									if (baseDn == null || baseDn.equals(""))
										baseDn = getBaseDnFromUserName(m_directoryServer.getBaseDn());
									m_directoryServer.setUrl(baseDn);
								}
							}
							else {
								m_directoryServer.setUrl(m_searchInfo.getBaseDn());
							}
						}
						else {
							m_directoryServer.setUrl(((LdapObject) value).getDn());
						}
						
						GetLdapServerDataCmd cmd = new GetLdapServerDataCmd(m_directoryServer, m_searchInfo);
						GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
							@Override
							public void onFailure(Throwable caught) {
								GwtClientHelper.handleGwtRPCFailure(
									caught,
									m_messages.rpcFailure_GetLdapServerData());
							}

							@Override
							public void onSuccess(VibeRpcResponse response) {
								// Extract the share lists from the details...
								LdapServerDataRpcResponseData responseData = ((LdapServerDataRpcResponseData) response.getResponseData());
								getLeafObjectsAsync(responseData.getQueryOutput());
							}
						});
						
						
						
						
					}
				};
				return new DefaultNodeInfo<LdapObject>(m_dataProvider, new LDAPObjectCell(), m_selectionModel, null);
			}
		}

		@Override
		public boolean isLeaf(Object value) {
			if (value instanceof LdapObject) {
				LdapObject ldapObject = (LdapObject) value;
				return ldapObject.isLeaf();
			}

			// Not a leaf
			return false;
		}
	}

	private String getBaseDnFromUserName(String syncUser) {
		// If the user name is in the format username@domain,
		// we cannot find the base dn..
		if (syncUser == null || syncUser.contains("@")) {
			return "";
		}

		String[] tokens = syncUser.split(",");

		StringBuilder baseDn = new StringBuilder();
		if (tokens != null) {
			for (String str : tokens) {
				if (!str.toLowerCase().startsWith("dc="))
					continue;

				baseDn.append(str);
				baseDn.append(",");
			}
			baseDn.deleteCharAt(baseDn.length() - 1);
			return baseDn.toString();
		}
		return syncUser;
	}

	private void getLeafObjectsAsync(final QueryOutput<LdapObject> result) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				getLeafObjectsNow(result);
			}
		});
	}
	
	private void getLeafObjectsNow(final QueryOutput<LdapObject> result) {
		ArrayList<LdapObject> resultList = null;

		// Get the ldap objects list
		if (result != null)
			resultList = result.getResultList();

		// If we did get back a list, update the m_tree node
		if (resultList != null && resultList.size() > 0) {
			m_dataProvider.updateRowData(0, resultList);
			m_dataProvider.updateRowCount(resultList.size(), true);
		}
		// No data
		else {
			m_dataProvider.updateRowData(0, new ArrayList<LdapObject>());
			m_dataProvider.updateRowCount(0, true);
		}

		// If the search results exceeded the max limit, throw an information message
		if (result != null && result.isSizeExceeded()) {
			Window.alert("partial results ");
			// setInfoMessage(RBUNDLE.sizeExceededPartialResults(), null);
		}
		// Clear any existing error message
		else {
			// setErrorMessage(null, null);
		}
	}
	
	class LDAPObjectCell extends AbstractCell<LdapObject> {
		@Override
		public void render(Context context, LdapObject value, SafeHtmlBuilder sb) {
			if (value != null) {
				Image img;

				if (value.getObjectClass() == null)
					img = new Image(GwtTeaming.getLdapBrowserImageBundle().tree());
				else {
					value.getObjectClass();
					// Show User Icon
					if (value.isObjectClassFound("person")) {
						img = new Image(GwtTeaming.getLdapBrowserImageBundle().user());
					}
					// Show domain icon
					else if (value.isObjectClassFound("domain")) {
						img = new Image(GwtTeaming.getLdapBrowserImageBundle().edirDomain());
					}
					// Show Container Icon
					else if (value.isObjectClassFound("organizationalunit") || value.isObjectClassFound("container")) {
						img = new Image(GwtTeaming.getLdapBrowserImageBundle().organizationalUnit());
					}
					// Show Organization Icon
					else if (value.isObjectClassFound("organization")) {
						img = new Image(GwtTeaming.getLdapBrowserImageBundle().organization());
					}
					// Show Organization Icon
					else if (value.isObjectClassFound("organizationalrole")) {
						img = new Image(GwtTeaming.getLdapBrowserImageBundle().orgRole());
					}
					// Show Group Icon
					else if (value.isObjectClassFound("group") || value.isObjectClassFound("groupwisedistributionlist") || value.isObjectClassFound("groupOfNames")) {
						img = new Image(GwtTeaming.getLdapBrowserImageBundle().group());
					}
					// Show Country Icon
					else if (value.isObjectClassFound("country")) {
						img = new Image(GwtTeaming.getLdapBrowserImageBundle().country());
					}
					// Show Country Icon
					else if (value.isObjectClassFound("locality")) {
						img = new Image(GwtTeaming.getLdapBrowserImageBundle().locality());
					}
					// Default to user icon here..We need an unknown icon
					else {
						img = new Image(GwtTeaming.getLdapBrowserImageBundle().user());
					}
				}
				img.addStyleName("gwNameCellImg");

				FlowPanel flowPanel = new FlowPanel();
				flowPanel.addStyleName("gwNameCell");

				flowPanel.add(img);
				String name = value.getName();

				if (name == null)
					name = value.getDn();
				flowPanel.add(new InlineLabel(name));

				sb.appendHtmlConstant(flowPanel.toString());
			}
		}
	}

	public boolean isObjectClassFound(String objectClass, String typeToSearch) {
		if (objectClass != null) {
			String[] strings = objectClass.split(",");

			for (String str : strings) {
				if (str.equals(typeToSearch))
					return true;
			}
		}
		return false;
	}

	public LdapObject getSelected() {
		return m_selected;
	}
}
