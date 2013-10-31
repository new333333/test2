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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.CellTreeResource;
import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.GwCellTreeMessages;
import org.kablink.teaming.gwt.client.GwtLdapConfig;
import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.ldapbrowser.DirectoryServer;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapSearchInfo;
import org.kablink.teaming.gwt.client.ldapbrowser.QueryOutput;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapServerDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.LdapServerDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.view.client.*;

/**
 * An LDAP browser dialog.
 *  
 * @author rvasudevan
 */
public class LdapBrowserDlg extends DlgBox implements EditCanceledHandler {
	private AsyncDataProvider<LdapObject>		m_dataProvider;		// Data provider for the CellTree.
	private CellTree							m_tree;				// The CellTree for browsing the LDAP directory with.
	private FlowPanel							m_browsePanel;		// The panel holding the tree.
	private FlowPanel							m_treesPanel;		// Panel containing the tree ListBox when m_browseList refers to multiple trees.
	private GwtTeamingMessages					m_messages;			// Access to Vibe's messages.
	private LdapBrowseSpec						m_activeTree;		// The LdapBrowseSpec specifying the LDAP tree currently being browsed.
	private LdapBrowserCallback					m_ldapCallback;		// Callback interface to let the caller know what's going on.
	private LdapObject							m_selected;			// The currently selected LDAP object.
	private LdapTreeModel						m_treeViewModel;	// The data model for the CellTree.
	private ListBox								m_treesLB;			// The list of trees.
	private List<LdapBrowseSpec>				m_browseList;		// List of LDAP trees the user can choose to browse from.
	private SingleSelectionModel<LdapObject>	m_selectionModel;	// Provides selection handling for the cell tree.
	private UIObject							m_showRelativeTo;	// Show the dialog relative to this.  null -> Center it on the screen.
	
	// Controls whether the LDAP browse button show up in the various
	// administration pages where it's used.
	public final static boolean	ENABLE_LDAP_BROWSER	= true;	//
	
	// Various strings to construct the LDAP browser. 
	private final static String	TREE_ROOT		= "ROOT";				// Used to mark the root node in the CellTree.
	private final static String SELECT_MARKER	= "xxx-SelectOne-xxx";	// Marks the <Select One> entry in the tree select widget when ther are multiple trees to choose from.

	/*
	 * Inner class that provides cells for the LDAP browser's tree.
	 */
	private class LdapObjectCell extends AbstractCell<LdapObject> {
		/**
		 * ?
		 * 
		 * @param context
		 * @param value
		 * @param sb
		 */
		@Override
		public void render(Context context, LdapObject value, SafeHtmlBuilder sb) {
			if (null != value) {
				Image img;
				boolean treeRoot = (null == value.getObjectClass());
				if (treeRoot) {
					img = GwtClientHelper.buildImage(GwtTeaming.getLdapBrowserImageBundle().tree());
				}
				
				else {
					if (value.isObjectClassFound("person")) {
						// Show User icon.
						img = GwtClientHelper.buildImage(GwtTeaming.getLdapBrowserImageBundle().user());
					}
					
					else if (value.isObjectClassFound("domain")) {
						// Show domain icon.
						img = GwtClientHelper.buildImage(GwtTeaming.getLdapBrowserImageBundle().edirDomain());
					}
					
					else if (value.isObjectClassFound("organizationalunit") || value.isObjectClassFound("container")) {
						// Show Container icon.
						img = GwtClientHelper.buildImage(GwtTeaming.getLdapBrowserImageBundle().organizationalUnit());
					}
					
					else if (value.isObjectClassFound("organization")) {
						// Show Organization icon.
						img = GwtClientHelper.buildImage(GwtTeaming.getLdapBrowserImageBundle().organization());
					}
					
					else if (value.isObjectClassFound("organizationalrole")) {
						// Show Organization icon.
						img = GwtClientHelper.buildImage(GwtTeaming.getLdapBrowserImageBundle().orgRole());
					}
					
					else if (value.isObjectClassFound("group") || value.isObjectClassFound("groupwisedistributionlist") || value.isObjectClassFound("groupOfNames")) {
						// Show Group icon.
						img = GwtClientHelper.buildImage(GwtTeaming.getLdapBrowserImageBundle().group());
					}
					
					else if (value.isObjectClassFound("country")) {
						// Show Country icon.
						img = GwtClientHelper.buildImage(GwtTeaming.getLdapBrowserImageBundle().country());
					}
					
					else if (value.isObjectClassFound("locality")) {
						// Show Country icon.
						img = GwtClientHelper.buildImage(GwtTeaming.getLdapBrowserImageBundle().locality());
					}
					
					else {
						// Default to user icon here.  We need an
						// unknown icon.
						img = GwtClientHelper.buildImage(GwtTeaming.getLdapBrowserImageBundle().user());
					}
				}
				FlowPanel flowPanel = new FlowPanel();
				flowPanel.addStyleName("gwtUI_nowrap");
				if (treeRoot)
				     flowPanel.addStyleName("gwNameCell-root");
				else flowPanel.addStyleName("gwNameCell");

				if (treeRoot)
				     img.addStyleName("gwNameCellImg-root");
				else img.addStyleName("gwNameCellImg");
				flowPanel.add(img);
				
				String name = value.getName();
				if (null == name) {
					name = value.getDn();
					if (null == name) {
						name = "";
					}
				}
				flowPanel.add(new InlineLabel(name));

				sb.appendHtmlConstant(flowPanel.toString());
			}
		}
	}

	/*
	 * Inner class that provides the data model for the LDAP browser's
	 * tree.
	 */
	private class LdapTreeModel implements TreeViewModel {
		/**
		 * ?
		 * 
		 * @param value
		 * 
		 * @return
		 */
		@Override
		public <T> NodeInfo<?> getNodeInfo(final T value) {
			final DirectoryServer ds = m_activeTree.getDirectoryServer();
			final LdapSearchInfo  si = m_activeTree.getSearchInfo();
			
			if ((value instanceof String) && value.equals(TREE_ROOT)) {
				// Root Node.
				List<LdapObject> list = new ArrayList<LdapObject>();
				String treeName = ds.getName();
				if (!(GwtClientHelper.hasString(treeName))) {
					treeName = m_messages.ldapBrowser_Label_Tree();
				}
				list.add(new LdapObject(treeName));
				ListDataProvider<LdapObject> dataProvider = new ListDataProvider<LdapObject>(list);
				return new DefaultNodeInfo<LdapObject>(dataProvider, new LdapObjectCell(), m_selectionModel, null);
			}
			
			else {
				// Non-root Nodes.
				m_dataProvider = new AsyncDataProvider<LdapObject>() {
					@Override
					protected void onRangeChanged(HasData<LdapObject> display) {
						final LdapObject ldapObject = ((LdapObject) value);
						if (null == ldapObject.getObjectClass()) {
							if (null == si.getBaseDn()) {
								if (ds.isEDirectory() || ds.isUnknown()) {
									ds.setUrl("");
								}
								else {
									String baseDn = getBaseDnFromUserName(ds.getSyncUser());
									if (!(GwtClientHelper.hasString(baseDn)))
										baseDn = getBaseDnFromUserName(ds.getBaseDn());
									ds.setUrl(baseDn);
								}
							}
							
							else {
								ds.setUrl(si.getBaseDn());
							}
						}
						
						else {
							ds.setUrl(((LdapObject) value).getDn());
						}
						
						GetLdapServerDataCmd cmd = new GetLdapServerDataCmd(ds, si);
						GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
							@Override
							public void onFailure(Throwable caught) {
								// We couldn't read the information
								// from the server!  Tell the user
								// about the problem...
								GwtClientHelper.handleGwtRPCFailure(
									caught,
									m_messages.rpcFailure_GetLdapServerData());
								
								// ...and render it as an empty list.
								getLeafObjectsAsync(ldapObject, null);
							}

							@Override
							public void onSuccess(VibeRpcResponse response) {
								// Extract the share lists from the
								// details and render them into the
								// tree.
								LdapServerDataRpcResponseData responseData = ((LdapServerDataRpcResponseData) response.getResponseData());
								if (responseData.hasError()) {
									GwtClientHelper.deferredAlert(responseData.getError());
								}
								getLeafObjectsAsync(ldapObject, responseData.getQueryOutput());
							}
						});
					}
				};
				
				return new DefaultNodeInfo<LdapObject>(m_dataProvider, new LdapObjectCell(), m_selectionModel, null);
			}
		}

		/**
		 * ?
		 * 
		 * @param value
		 * 
		 * @return
		 */
		@Override
		public boolean isLeaf(Object value) {
			if (value instanceof LdapObject) {
				LdapObject ldapObject = ((LdapObject) value);
				return ldapObject.isLeaf();
			}

			// Not a leaf.
			return false;
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private LdapBrowserDlg() {
		// Initialize the super class...
		super(
			false,					// false -> Don't auto hide.
			true,					// true  -> Modal.
			DlgButtonMode.Close);	// Only show a 'Close' button.
		
		// ...initialize everything that requires it...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.ldapBrowser_Caption(),	// The dialog's caption.
			getSimpleSuccessfulHandler(),		// The dialog's EditSuccessfulHandler.
			this,								// The dialog's EditCanceledHandler.
			null);								// Create callback data.  Unused. 
	}

	/**
	 * Creates the dialog's main content panel.
	 * 
	 * Implements the DlgBox.createContent() method.
	 *
	 * @param unused
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object unused) {
		// Create a main panel to hold everything.
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("ldapBrowser-mainPanel");

		// Create a list box and panel for the user to select which
		// tree to browse.
		m_treesPanel = new FlowPanel();
		m_treesPanel.addStyleName("ldapBrowser-treesPanel");
		InlineLabel il = new InlineLabel(m_messages.ldapBrowser_Label_SelectTree());
		il.addStyleName("ldapBrowser-treeSelectLabel");
		m_treesPanel.add(il);
		m_treesLB = new ListBox(false);	// false -> Single-select ListBox.
		m_treesLB.setVisibleItemCount(1);
		m_treesLB.addStyleName("ldapBrowser-treeSelect");
		m_treesLB.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// Is there anything selected in the ListBox?
				int i = m_treesLB.getSelectedIndex();
				if (0 <= i) {
					// Yes!  Can we find the selected tree?
					String address = m_treesLB.getValue(i);
					LdapBrowseSpec browse = LdapBrowseSpec.findBrowseSpecByAddress(m_browseList, address);
					if (null == browse) {
						// No!  That should never happen.  Tell the
						// user about the problem and otherwise ignore
						// the selection.
						GwtClientHelper.deferredAlert(m_messages.ldapBrowser_InternalError_CantFindTree());
					}
					else {
						// Yes, we found the selected tree!  Remove any
						// previous tree being browsed and activate
						// this one.
						m_browsePanel.clear();
						m_activeTree = browse;
						invokeLdapBrowserAsync();
					}

					// If the first item in the list is still the
					// <Select One> item...
					address = m_treesLB.getValue(0);
					if (GwtClientHelper.hasString(address) && address.equals(SELECT_MARKER)) {
						// ...remove it.
						m_treesLB.removeItem(0);
					}
				}
			}
		});
		m_treesPanel.add(m_treesLB);
		mainPanel.add(m_treesPanel);

		// Create a scroll panel and panel for the browser's cell tree. 
		ScrollPanel scroller = new ScrollPanel();
		scroller.addStyleName("ldapBrowser-scroller");
		m_browsePanel = new FlowPanel();
		m_browsePanel.addStyleName("ldapBrowser-browsePanel");
		scroller.add(m_browsePanel);
		mainPanel.add(scroller);

		// Return the main panel that contains everything.
		return mainPanel;
	}
	
	/**
	 * This method gets called when user user presses the Close push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean editCanceled() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_ldapCallback.closed();
			}
		});
		return true;
	}

	/*
	 */
	private void invokeLdapBrowserAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				invokeLdapBrowserNow();
			}
		});
	}
	
	/*
	 */
	private void invokeLdapBrowserNow() {
		// LDAP Tree Model.
		m_treeViewModel = new LdapTreeModel();
		CellTreeResource   treeResource = GWT.create(CellTreeResource.class  );
		GwCellTreeMessages treeMessages = GWT.create(GwCellTreeMessages.class);

		// Selection model.
		m_selectionModel = new SingleSelectionModel<LdapObject>();
		m_selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				m_selected = m_selectionModel.getSelectedObject();
				m_ldapCallback.selectionChanged(m_selected);
			}
		});

		// Built the tree and set the max node size to be 1000.  Set
		// the initial value to be ROOT string to differ from
		// LdapObject.
		m_tree = new CellTree(m_treeViewModel, TREE_ROOT, treeResource, treeMessages);

		// Active Directory has a max limit of 1000, we will do the
		// same.
		m_tree.setDefaultNodeSize(1000);

		// Set open the tree so that we can get the data.
		m_tree.getRootTreeNode().setChildOpen(0, true);
		m_browsePanel.add(m_tree);
		
		// Finally, show the dialog.
		showLdapBrowser();
	}

	/*
	 */
	private String getBaseDnFromUserName(String syncUser) {
		// If the user name is in the format username@domain,
		// we cannot find the base dn..
		if ((null == syncUser) || syncUser.contains("@")) {
			return "";
		}
		String[] tokens = syncUser.split(",");

		StringBuilder baseDn = new StringBuilder();
		if (null != tokens) {
			for (String str:  tokens) {
				if (!(str.toLowerCase().startsWith("dc="))) {
					continue;
				}

				baseDn.append(str);
				baseDn.append(",");
			}
			baseDn.deleteCharAt(baseDn.length() - 1);
			return baseDn.toString();
		}
		return syncUser;
	}

	/**
	 * 
	 * Implements the DlgBox.getDataFromDlg() method.
	 *
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
	}

	/*
	 * Issue an RPC request to get the ldap configuration data from the
	 * server.
	 */
	private static void getLdapServerListFromServerAsync(final LdapBrowseListCallback ldapBrowseListCallback) {
		GwtClientHelper.deferCommand( new ScheduledCommand() {
			@Override
			public void execute() {
				getLdapServerListFromServerNow(ldapBrowseListCallback);
			}
		});
	}
	
	private static void getLdapServerListFromServerNow(final LdapBrowseListCallback ldapBrowseListCallback) {
		// Execute a GWT RPC command to get the LDAP configurations.
		GwtClientHelper.executeCommand(new GetLdapConfigCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetLdapConfig());
				ldapBrowseListCallback.onFailure();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Allocate a List<LdapBrowseSpec> we can hand back
				// via the callback.
				final List<LdapBrowseSpec> browseList = new ArrayList<LdapBrowseSpec>();
				
				// Do we have any LDAP connections?
				GwtLdapConfig m_ldapConfig = ((GwtLdapConfig) response.getResponseData());
				ArrayList<GwtLdapConnectionConfig> ldapConnections = ((null == m_ldapConfig) ? null : m_ldapConfig.getListOfLdapConnections());
				if (GwtClientHelper.hasItems(ldapConnections)) {
					// Yes!  We'll use the same LdapSearchInfo for all
					// of them.
					LdapSearchInfo si = new LdapSearchInfo();
					si.setSearchObjectClass(LdapSearchInfo.RETURN_USERS);
					si.setSearchSubTree(false);

					// Construct the List<LdapBrowseSpec> using the
					// information from the
					// List<GwtLdapConnectionConfig>.
					for (GwtLdapConnectionConfig ldapConnection:  ldapConnections) {
						DirectoryServer ds = new DirectoryServer();
						ds.setAddress(      ldapConnection.getServerUrl()        );
						ds.setSyncUser(     ldapConnection.getProxyDn()          );
						ds.setSyncPassword( ldapConnection.getProxyPwd()         );
						ds.setGuidAttribute(ldapConnection.getLdapGuidAttribute());
						browseList.add( new LdapBrowseSpec(ds, si));
					}
				}

				// Hand the List<LdapBrowseSpec> back to the caller via
				// the callback.
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						ldapBrowseListCallback.onSuccess(browseList);
					}
				});
			}
		});
	}
	
	/*
	 */
	private void getLeafObjectsAsync(final LdapObject ldapObject, final QueryOutput<LdapObject> result) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				getLeafObjectsNow(ldapObject, result);
			}
		});
	}
	
	/*
	 */
	private void getLeafObjectsNow(final LdapObject ldapObject, final QueryOutput<LdapObject> result) {
		// Get the ldap objects list.
		List<LdapObject> resultList;
		if (null != result)
		     resultList = result.getResultList();
		else resultList = null;

		// If we did get back a list...
		if (GwtClientHelper.hasItems(resultList)) {
			// ...update the tree node.
			m_dataProvider.updateRowData(0, resultList);
			m_dataProvider.updateRowCount(resultList.size(), true);
		}
		
		else {
			// ...otherwise, no data.
//!			m_dataProvider.updateRowData(0, new ArrayList<LdapObject>());
//!			m_dataProvider.updateRowCount(0, true);
			m_dataProvider.updateRowCount((-1), true);	// -1 -> Forces the expansion to not show an 'Empty' message.
		}

		// If the search results exceeded the max limit...
		if ((null != result) && result.isSizeExceeded()) {
			// ...display an information message.
			GwtClientHelper.deferredAlert(m_messages.ldapBrowser_Warning_Partial());
//!			setInfoMessage(RBUNDLE.sizeExceededPartialResults(), null);
		}
		
		else {
			// ...otherwise, clear any existing error message.
//!			setErrorMessage(null, null);
		}
	}

	/**
	 * 
	 * Implements the DlgBox.getFocusWidget() method.
	 *
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}
	
	/**
	 * ?
	 * 
	 * @return
	 */
	public LdapObject getSelected() {
		return m_selected;
	}

	/**
	 * ?
	 * 
	 * @param objectClass
	 * @param typeToSearch
	 * 
	 * @return
	 */
	public boolean isObjectClassFound(String objectClass, String typeToSearch) {
		if (null != objectClass) {
			String[] strings = objectClass.split(",");
			for (String str:  strings) {
				if (str.equals(typeToSearch)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Asynchronously populates the content of the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the content of the dialog.
	 */
	private void populateDlgNow() {
		// Clear the current browsing panel.
		m_browsePanel.clear();
		
		// Clear and hide any existing tree selector.
		m_treesLB.clear();
		m_treesPanel.setVisible(false);

		// How many trees were we given to browse?
		int trees = ((null == m_browseList) ? 0 : m_browseList.size());
		switch (trees) {
		case 0:
			// None!  That should never happen.  Tell the user about
			// the problem and bail.
			GwtClientHelper.deferredAlert(m_messages.ldapBrowser_InternalError_NoTrees());
			break;
			
		case 1:
			// One!  Activate it and invoke the browser.
			m_activeTree = m_browseList.get(0);
			invokeLdapBrowserNow();
			break;
			
		default:
			// More than one!  The user needs to pick and choose which
			// to browse.  Scan those supplied...
			m_treesLB.addItem(m_messages.ldapBrowser_Label_SelectOne(), SELECT_MARKER);
			for (LdapBrowseSpec browse:  m_browseList) {
				// ...adding each to the selector list...
				m_treesLB.addItem(
					browse.getDirectoryServer().getAddress(),
					browse.getDirectoryServer().getAddress());
			}
			
			// ...and show the selector panel and dialog.
			m_treesPanel.setVisible(true);
			showLdapBrowser();
			break;
		}
		
	}
	
	/*
	 * Asynchronously runs the given instance of the LDAP browser
	 * dialog.
	 */
	private static void runDlgAsync(final LdapBrowserDlg pDlg, final LdapBrowserCallback ldapCallback, final List<LdapBrowseSpec> browseList, final UIObject showRelativeTo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				pDlg.runDlgNow(ldapCallback, browseList, showRelativeTo);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the LDAP browser
	 * dialog.
	 */
	private void runDlgNow(LdapBrowserCallback ldapCallback, List<LdapBrowseSpec> browseList, UIObject showRelativeTo) {
		// Store the parameters...
		m_ldapCallback   = ldapCallback;
		m_browseList     = browseList;
		m_showRelativeTo = showRelativeTo;

		// ...initialize anything else that requires it...
		m_selected = null;

		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Does what's necessary to make the LDAP browser visible.
	 */
	private void showLdapBrowser() {
		if (null == m_showRelativeTo)
		     center();
		else showRelativeTo(m_showRelativeTo);
	}

	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the LDAP browser and perform some operation on it.            */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the prompt dialog
	 * asynchronously after it loads. 
	 */
	public interface LdapBrowserDlgClient {
		void onSuccess(LdapBrowserDlg ldapDlg);
		void onUnavailable();
	}

	/**
	 * Callback interface used when requesting a list of
	 * LdapBrowseSpec's for the currently defined LDAP servers. 
	 */
	public interface LdapBrowseListCallback {
		void onFailure();
		void onSuccess(List<LdapBrowseSpec> browseList);
	}

	/*
	 * Asynchronously loads the LdapBrowserDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// createAsync parameters.
			final LdapBrowserDlgClient ldapDlgClient,
			
			// getLdapServerList parameters.
			final LdapBrowseListCallback ldapBrowseListCallback,
			
			// initAndShow parameters,
			final LdapBrowserDlg		ldapDlg,
			final LdapBrowserCallback	ldapCallback,
			final List<LdapBrowseSpec>	browseList,
			final UIObject				showRelativeTo) {
		GWT.runAsync(LdapBrowserDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_LdapBrowserDlg());
				if (null != ldapDlgClient) {
					ldapDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != ldapDlgClient) {
					// Yes!  Create it and return it via the callback.
					LdapBrowserDlg ldapDlg = new LdapBrowserDlg();
					ldapDlgClient.onSuccess(ldapDlg);
				}
				
				// No, it's not a request to create a dialog!  Is it a
				// request for the list of LDAP servers?
				else if (null != ldapBrowseListCallback) {
					// Yes!  Issue the RPC request to get the defined
					// LDAP servers.
					getLdapServerListFromServerAsync(ldapBrowseListCallback);
				}
				
				else {
					// No, it's not a request for the LDAP servers
					// either!  It must be a request to run an existing
					// dialog.  Run it.
					runDlgAsync(ldapDlg, ldapCallback, browseList, showRelativeTo);
				}
			}
		});
	}
	
	/**
	 * Loads the LdapBrowserDlg split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param ldapDlgClient
	 */
	public static void createAsync(LdapBrowserDlgClient ldapDlgClient) {
		doAsyncOperation(ldapDlgClient, null, null, null, null, null);
	}
	
	/**
	 * Loads the a List<LdapBrowseSpec> of the currently defined LDAP
	 * servers via the split point and returns it via the callback.
	 * 
	 * @param ldapDlgClient
	 */
	public static void getLdapServerList(LdapBrowseListCallback ldapBrowseListCallback) {
		doAsyncOperation(null, ldapBrowseListCallback, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the LDAP browser dialog.
	 * 
	 * @param ldapDlg
	 * @param ldapCallback
	 * @param browseList
	 * @param showRelativeTo
	 */
	public static void initAndShow(LdapBrowserDlg ldapDlg, LdapBrowserCallback ldapCallback, List<LdapBrowseSpec> browseList, UIObject showRelativeTo) {
		doAsyncOperation(null, null, ldapDlg, ldapCallback, browseList, showRelativeTo);
	}
	
	public static void initAndShow(LdapBrowserDlg ldapDlg, LdapBrowserCallback ldapCallback, LdapBrowseSpec browse, UIObject showRelativeTo) {
		// Always use the initial form of the method.
		List<LdapBrowseSpec> browseList = new ArrayList<LdapBrowseSpec>();
		browseList.add(browse);
		initAndShow(ldapDlg, ldapCallback, browseList, showRelativeTo);
	}
	
	public static void initAndShow(LdapBrowserDlg ldapDlg, LdapBrowserCallback ldapCallback, DirectoryServer ds, LdapSearchInfo si, UIObject showRelativeTo) {
		// Always use a previous form of the method.
		initAndShow(ldapDlg, ldapCallback, new LdapBrowseSpec(ds, si), showRelativeTo);
	}
	
	public static void initAndShow(LdapBrowserDlg ldapDlg, LdapBrowserCallback ldapCallback, DirectoryServer ds, LdapSearchInfo si) {
		// Always use a previous form of the method.
		initAndShow(ldapDlg, ldapCallback, ds, si, null);
	}
	
	public static void initAndShow(LdapBrowserDlg ldapDlg, LdapBrowserCallback ldapCallback, DirectoryServer ds, UIObject showRelativeTo) {
		// Always use a previous form of the method.
		initAndShow(ldapDlg, ldapCallback, ds, null, showRelativeTo);
	}
	
	public static void initAndShow(LdapBrowserDlg ldapDlg, LdapBrowserCallback ldapCallback, DirectoryServer ds) {
		// Always use a previous form of the method.
		initAndShow(ldapDlg, ldapCallback, ds, null, null);
	}
}
