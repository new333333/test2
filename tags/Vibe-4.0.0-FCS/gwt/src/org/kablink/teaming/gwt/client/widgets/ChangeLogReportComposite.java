/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtPublic;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.ChangeLogReportRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CreateChangeLogReportCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Composite that runs the change log report.
 * 
 * @author drfoster@novell.com
 */
public class ChangeLogReportComposite extends ReportCompositeBase
	implements KeyPressHandler,
		// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private FindCtrl		m_binderFinder;			// The FindCtrl to select the binder to report on.
	private FindCtrl		m_entityFinder;			// The FindCtrl to select the entity to report on.
	private InlineLabel		m_binderFinderLabel;	// The label on the binder FindCtrl.
	private InlineLabel		m_entityFinderLabel;	// The label on the entity FindCtrl.
	private List<String>	m_changes;				// List<String> of the changes read from the server.
	private ListBox			m_entityTypeLB;			// The <SELECT> for the entity type.
	private ListBox			m_operationLB;			// The <SELECT> for the operation.
	private TextArea		m_reportTable;			// The <TextArea> containing the generated report.
	private TextBox			m_binderIdTB;			// The <INPUT>  for the binder ID.
	private TextBox			m_entityIdTB;			// The <INPUT>  for the entity ID.
	private VibeFlowPanel	m_reportPanel;			// The <DIV> containing m_reportTable.

	private static final int MAX_ID_LENGTH			=  8;	// Maximum number of characters that can be typed into an ID entry field.
	private static final int VISIBLE_ID_LENGTH		=  5;	// Number of characters visible in an ID entry field.
	private static final int VISIBLE_REPORT_LINES	= 25;	// Number of lines that are visible in the report table.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	
	/**
	 * Constructor method.
	 */
	public ChangeLogReportComposite() {
		// Simply initialize the super class.
		super();
	}
	
	/**
	 * Creates the content for the report.
	 * 
	 * Overrides the ReportCompositeBase.createContent() method.
	 */
	@Override
	public void createContent() {
		// Let the super class create the initial base content...
		super.createContent();
		
		// ...add a panel for the report widgets...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-reportCompositeBase-widgetsPanel");
		m_rootContent.add(fp);

		// ...add the description as to how to run the report...
		InlineLabel il = buildInlineLabel(m_messages.changeLogDescription(), "vibe-userAccessReportComposite-label");
		fp.add(il);
		
		// ...create a FlexTable to hold the various selectors in the
		// ...dialog...
		final VibeFlexTable ft = new VibeFlexTable();
		ft.addStyleName("vibe-changeLogReportComposite-widgetsPanel");
		ft.setCellPadding(0);
		ft.setCellSpacing(2);
		fp.add(ft);

		// ...add the selector for the folder or workspace...
		m_binderFinderLabel = buildInlineLabel(m_messages.changeLogFindBinder(), "vibe-changeLogReportComposite-label");
		ft.setWidget(0, 0, m_binderFinderLabel);
		
		FindCtrl.createAsync(m_binderFinderLabel, SearchType.PLACES, new FindCtrlClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(FindCtrl findCtrl) {
				// Store the find control...
				m_binderFinder = findCtrl;

				m_binderFinder.setSearchForExternalPrincipals( true );
				m_binderFinder.setSearchForInternalPrincipals( true );
				
				// ...style it...
				m_binderFinder.addStyleName("vibe-changeLogReportComposite-binderFind");
				FocusWidget fw = m_binderFinder.getFocusWidget();
				if ((null != fw) && (fw instanceof TextBox)) {
					fw.addStyleName("vibe-changeLogReportComposite-binderFind");
				}
				
				// ...and add it to the layout table.
				ft.setWidget(1, 0, m_binderFinder);
			}
		});

		// ...add the entry field for the folder or workspace ID...
		il = buildInlineLabel(m_messages.changeLogBinderId(), "vibe-changeLogReportComposite-label");
		ft.setWidget(0, 1, il);
		
		m_binderIdTB = new TextBox();
		m_binderIdTB.addStyleName("vibe-reportCompositeBase-textEntry");
		m_binderIdTB.addKeyPressHandler(this);
		m_binderIdTB.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String idS = m_binderIdTB.getText();
				if (null != idS) {
					idS = idS.trim();
					if (0 == idS.length()) {
						idS = null;
					}
				}
				m_entityFinder.enableScope(idS, false);	// false -> Even if scope is enabled, don't show the scope select widgets.
			}
		});
		m_binderIdTB.setVisibleLength(VISIBLE_ID_LENGTH);
		m_binderIdTB.setMaxLength(MAX_ID_LENGTH);
		ft.setWidget(1, 1, m_binderIdTB);
		
		// ...add the selector for the entity...
		m_entityFinderLabel = buildInlineLabel(m_messages.changeLogFindEntity(), "vibe-changeLogReportComposite-label");
		ft.setWidget(2, 0, m_entityFinderLabel);
		
		FindCtrl.createAsync(m_entityFinderLabel, SearchType.ENTRIES, new FindCtrlClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(FindCtrl findCtrl) {
				// Store the find control...
				m_entityFinder = findCtrl;

				m_entityFinder.setSearchForExternalPrincipals( true );
				m_entityFinder.setSearchForInternalPrincipals( true );
				
				// ...style it...
				m_entityFinder.addStyleName("vibe-changeLogReportComposite-entityFind");
				FocusWidget fw = m_entityFinder.getFocusWidget();
				if ((null != fw) && (fw instanceof TextBox)) {
					fw.addStyleName("vibe-changeLogReportComposite-entityFind");
				}
				
				// ...and add it to the layout table.
				ft.setWidget(3, 0, m_entityFinder);
			}
		});

		// ...add the entry field for the entity ID...
		il = buildInlineLabel(m_messages.changeLogEntityId(), "vibe-changeLogReportComposite-label");
		ft.setWidget(2, 1, il);
		
		m_entityIdTB = new TextBox();
		m_entityIdTB.addStyleName("vibe-reportCompositeBase-textEntry");
		m_entityIdTB.addKeyPressHandler(this);
		m_entityIdTB.setVisibleLength(VISIBLE_ID_LENGTH);
		m_entityIdTB.setMaxLength(MAX_ID_LENGTH);
		ft.setWidget(3, 1, m_entityIdTB);
		
		// ...add the selector field for the entity type...
		il = buildInlineLabel(m_messages.changeLogEntityType(), "vibe-changeLogReportComposite-label");
		ft.setWidget(4, 1, il);
		
		m_entityTypeLB = new ListBox();
		m_entityTypeLB.addStyleName("vibe-changeLogReportComposite-entityTypeList");
		ft.setWidget(5, 1, m_entityTypeLB);
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_folderEntry(), "folderEntry");
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_user(),        "user"       );
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_group(),       "group"      );
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_folder(),      "folder"     );
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_workspace(),   "workspace"  );
//		m_entityTypeLB.addItem(m_messages.changeLogEntityType_profiles(),    "profiles"   );	// Not sure what this was in the JSP.  I don't see the need for it.
		m_entityTypeLB.setSelectedIndex(0);
		m_entityTypeLB.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// Set the FindCtrl to search for the selected type of
				// entity.
				int		si   = m_entityTypeLB.getSelectedIndex();
				String	type = m_entityTypeLB.getValue(si);
				
				SearchType st;
				if      (type.equals("folderEntry")) st = SearchType.ENTRIES;
				else if (type.equals("user"))        st = SearchType.USER;
				else if (type.equals("group"))       st = SearchType.GROUP;
				else if (type.equals("folder"))      st = SearchType.FOLDERS;
				else if (type.equals("workspace"))   st = SearchType.FOLDERS;
				else if (type.equals("profiles"))    st = SearchType.PERSON;
				else                                 st = SearchType.ENTRIES;
				m_entityFinder.setSearchType(st);
			}
		});
		
		
		// ...add the selector field for the operation...
		il = buildInlineLabel(m_messages.changeLogOperation(), "vibe-changeLogReportComposite-label");
		ft.setWidget(6, 0, il);
		
		m_operationLB = new ListBox();
		m_operationLB.addStyleName("vibe-changeLogReportComposite-operationList");
		ft.setWidget(7, 0, m_operationLB);
		m_operationLB.addItem(m_messages.changeLogOperation_showAll(),             ""                   );
		m_operationLB.addItem(m_messages.changeLogOperation_addEntry(),            "addEntry"           );
		m_operationLB.addItem(m_messages.changeLogOperation_modifyEntry(),         "modifyEntry"        );
		m_operationLB.addItem(m_messages.changeLogOperation_deleteEntry(),         "deletetEntry"       );
		m_operationLB.addItem(m_messages.changeLogOperation_startWorkflow(),       "startWorkflow"      );
		m_operationLB.addItem(m_messages.changeLogOperation_modifyWorkflowState(), "modifyWorkflowState");
		m_operationLB.addItem(m_messages.changeLogOperation_addWorkflowResponse(), "addWorkflowResponse");
		m_operationLB.addItem(m_messages.changeLogOperation_moveEntry(),           "moveEntry"          );
		m_operationLB.addItem(m_messages.changeLogOperation_addFile(),             "addFile"            );
		m_operationLB.addItem(m_messages.changeLogOperation_modifyFile(),          "modifyFile"         );
		m_operationLB.addItem(m_messages.changeLogOperation_deleteFile(),          "deleteFile"         );
		m_operationLB.addItem(m_messages.changeLogOperation_deleteVersion(),       "deleteVersion"      );
		m_operationLB.addItem(m_messages.changeLogOperation_renameFile(),          "renameFile"         );
		m_operationLB.addItem(m_messages.changeLogOperation_addBinder(),           "addBinder"          );
		m_operationLB.addItem(m_messages.changeLogOperation_modifyBinder(),        "modifyBinder"       );
		m_operationLB.addItem(m_messages.changeLogOperation_deleteBinder(),        "deleteBinder"       );
		m_operationLB.addItem(m_messages.changeLogOperation_moveBinder(),          "moveBinder"         );
		m_operationLB.addItem(m_messages.changeLogOperation_modifyAccess(),        "modifyAccess"       );
		m_operationLB.addItem(m_messages.changeLogOperation_deleteAccess(),        "deleteAccess"       );
		
		// ...add the 'Run Report' push button...
		Button runReportBtn = new Button(m_messages.changeLogRunReport());
		runReportBtn.addStyleName("vibe-reportCompositeBase-buttonBase vibe-reportCompositeBase-runButton");
		runReportBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createReport();
			}
		});
		fp.add(runReportBtn);
		
		// ...add the panel containing the output of the report...
		m_reportPanel = new VibeFlowPanel();
		m_reportPanel.addStyleName("vibe-changeLogReportComposite-reportPanel");
		m_reportPanel.setVisible(false);	// Initially hidden, shown once a report is created.
		m_rootContent.add(m_reportPanel);
		
		// ...and create the TextArea to hold the report.
		m_reportTable = new TextArea();
		m_reportTable.addStyleName("vibe-changeLogReportComposite-reportTable");
		m_reportTable.setVisibleLines(VISIBLE_REPORT_LINES);
		m_reportPanel.add(m_reportTable);
	}
	
	/*
	 * Creates a report and downloads it.
	 */
	private void createReport() {
		getReportDataAsync();
	}

	/*
	 * Returns the Long ID of an item returned from a FindCtrl.
	 */
	private Long getEntityIdFromSearchResults(GwtTeamingItem selection) {
		Long reply;
		if      (null == selection)                   reply = null;
		else if (selection instanceof GwtFolder)      reply = Long.parseLong(((GwtFolder)      selection).getFolderId());
		else if (selection instanceof GwtFolderEntry) reply = Long.parseLong(((GwtFolderEntry) selection).getEntryId());
		else if (selection instanceof GwtGroup)       reply =                ((GwtGroup)       selection).getIdLong();
		else if (selection instanceof GwtPublic)      reply =                ((GwtPublic)      selection).getIdLong();
		else if (selection instanceof GwtUser)        reply =                ((GwtUser)        selection).getIdLong();
		else                                          reply = null;
		return reply;
	}

	/*
	 * Returns a Long from a numeric value contained in a TextBox.
	 */
	private static Long getLFromTB(TextBox tb) {
		String value = tb.getValue();
		if (null == value)
		     value = "";
		else value = value.trim();
		Long reply;
		if (0 == value.length())
			 reply = null;
		else reply = Long.parseLong(value);
		return reply;
	}
	
	/**
	 * Returns a TeamingEvents[] of the events to be registered for the
	 * composite.
	 *
	 * Implements the ReportCompositeBase.getRegisteredEvents() method.
	 * 
	 * @return
	 */
	@Override
	public TeamingEvents[] getRegisteredEvents() {
		return REGISTERED_EVENTS;
	}
	
	/*
	 * Asynchronously retrieves the change log report for the
	 * selections.
	 */
	private void getReportDataAsync() {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					getReportDataNow();
				}
			});
	}
	
	/*
	 * Synchronously retrieves the change log report for the
	 * selections.
	 */
	private void getReportDataNow() {
		// Get and validate the values from the report widgets...
		Long binderId = getLFromTB(m_binderIdTB);
		Long entityId = getLFromTB(m_entityIdTB);
		if ((null == binderId) && (null == entityId)) {
			GwtClientHelper.deferredAlert(m_messages.changeLogError_NoIds());
			return;
		}
		String entityType = getSFromLB(m_entityTypeLB);
		if ((null != entityId) && (!(GwtClientHelper.hasString(entityType)))) {
			entityType = "folderEntry";
		}
		
		// ...create the report...
		m_busySpinner.center();
		GwtClientHelper.executeCommand(
				new CreateChangeLogReportCmd(binderId, entityId, entityType, getSFromLB(m_operationLB)),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_CreateChangeLogReport());
				m_busySpinner.hide();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...and display the results.
				ChangeLogReportRpcResponseData responseData = ((ChangeLogReportRpcResponseData) response.getResponseData());
				m_changes = responseData.getChangeLogs();
				populateReportResultsAsync();
			}
		});
	}
	
	/*
	 * Returns a String value from a ListBox.
	 */
	private static String getSFromLB(ListBox lb) {
		int i = lb.getSelectedIndex();
		String reply = null;
		if (0 <= i) {
			reply = lb.getValue(i);
		}
		return reply;
	}
	
	/**
	 * This method gets called when the user types in the ID text
	 * boxes.  We only allow the user to enter numbers.
	 *
	 * Implements the KeyPressHandler.onKeyPress() method.
	 * 
	 * @param event
	 */
	@Override
	public void onKeyPress(KeyPressEvent event) {
        // Get the key the user pressed
        int keyCode = event.getNativeEvent().getKeyCode();
        if (!(GwtClientHelper.isKeyValidForNumericField( event.getCharCode(), keyCode))) {
        	// Make sure we are dealing with a text box...
        	Object source = event.getSource();
        	if (source instanceof TextBox) {
        		// ...and suppress the current keyboard event.
            	TextBox txtBox = ((TextBox) source);
        		txtBox.cancelKey();
        	}
        }
	}
	
	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults(final SearchFindResultsEvent event) {
		// If the find results aren't for this this composite...
		Widget findLabel = ((Widget) event.getSource());
		GwtTeamingItem	selection = event.getSearchResults();
		final Long		id        = getEntityIdFromSearchResults(selection);
		final String	idS       = ((null == id) ? null : String.valueOf(id));
		if (findLabel.equals(m_binderFinderLabel)) {
			// Process the find results.
			GwtClientHelper.deferCommand(
				new ScheduledCommand() {
					@Override
					public void execute() {
						// Hide the search results...
						m_binderFinder.hideSearchResults();
						if (null != idS) {
							// ...and put the ID of the selection into
							// ...the corresponding ID entry widget.
							m_binderIdTB.setText(idS);
						}
						m_entityFinder.enableScope(idS, false);	// false -> Although scope is enabled, don't show the scope select widgets.
					}
				});
		}
		
		else if (findLabel.equals(m_entityFinderLabel)) {
			// Process the find results.
			GwtClientHelper.deferCommand(
				new ScheduledCommand() {
					@Override
					public void execute() {
						// Hide the search results...
						m_entityFinder.hideSearchResults();
						if (null != idS) {
							// ...and put the ID of the selection into
							// ...the corresponding ID entry widget.
							m_entityIdTB.setText(idS);
						}
					}
				});
		}
	}
	
	/*
	 * Asynchronously populates the results of a report.
	 */
	private void populateReportResultsAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateReportResultsNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the results of a report.
	 */
	private void populateReportResultsNow() {
		// Reset the report table...
		resetReportTable(true);	// true -> Show the report panel.

		// ...and display the report.
		StringBuffer sb = new StringBuffer();
		if (GwtClientHelper.hasItems(m_changes)) {
			for (String change:  m_changes) {
				sb.append(change);
			}
		}
		else {
			sb.append(m_messages.changeLogWarning_NoChanges());
		}
		m_reportTable.setValue(sb.toString());
		
		// Finally, hide any busy spinner that may be showing.
		m_busySpinner.hide();
	}

	/**
	 * Resets the reports content.
	 * 
	 * Implements the ReportCompositeBase.resetReport() method.
	 */
	@Override
	public void resetReport() {
		resetReportTable(false);
	}
	
	/*
	 * Resets the table holding the output of a report.
	 */
	private void resetReportTable(boolean visible) {
		// Hide/show the report panel...
		m_reportPanel.setVisible(visible);

		// ...and empty the report table.
		m_reportTable.setValue("");
	}
}
