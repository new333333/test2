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

import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtPublic;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Composite that runs a change log report.
 * 
 * @author drfoster@novell.com
 */
public class ChangeLogReportComposite extends ReportCompositeBase
	implements KeyPressHandler,
		// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private FindCtrl	m_binderFinder;			// The FindCtrl to select the binder to report on.
	private FindCtrl	m_entityFinder;			// The FindCtrl to select the entity to report on.
	private InlineLabel	m_binderFinderLabel;	// The label on the binder FindCtrl.
	private InlineLabel	m_entityFinderLabel;	// The label on the entity FindCtrl.
	private ListBox		m_entityTypeLB;			// The <SELECT> for the entity type.
	private ListBox		m_filterLB;				// The <SELECT> for the filter.
	private TextBox		m_binderIdTB;			// The <INPUT>  for the binder ID.
	private TextBox		m_entityIdTB;			// The <INPUT>  for the entity ID.
	
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
		m_binderFinderLabel = buildInlineLabel(m_messages.changeLogFindFolder(), "vibe-changeLogReportComposite-label");
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
				String id = m_binderIdTB.getText();
				if (null != id) {
					id = id.trim();
					if (0 == id.length()) {
						id = null;
					}
				}
				m_entityFinder.enableScope(id);
			}
		});
		m_binderIdTB.setVisibleLength(5);
		m_binderIdTB.setMaxLength(8);
		ft.setWidget(1, 1, m_binderIdTB);
		
		// ...add the selector for the entry...
		m_entityFinderLabel = buildInlineLabel(m_messages.changeLogFindEntry(), "vibe-changeLogReportComposite-label");
		ft.setWidget(2, 0, m_entityFinderLabel);
		
		FindCtrl.createAsync(m_entityFinderLabel, SearchType.PLACES, new FindCtrlClient() {			
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
					fw.setEnabled(false);	// Disabled until we have a type to search.
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
		m_entityIdTB.setVisibleLength(5);
		m_entityIdTB.setMaxLength(8);
		ft.setWidget(3, 1, m_entityIdTB);
		
		// ...add the selector field for the entity type...
		il = buildInlineLabel(m_messages.changeLogEntityType(), "vibe-changeLogReportComposite-label");
		ft.setWidget(4, 1, il);
		
		m_entityTypeLB = new ListBox();
		m_entityTypeLB.addStyleName("vibe-changeLogReportComposite-entityTypeList");
		ft.setWidget(5, 1, m_entityTypeLB);
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_select(),      ""           );
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_folderEntry(), "folderEntry");
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_user(),        "user"       );
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_group(),       "group"      );
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_folder(),      "folder"     );
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_workspace(),   "workspace"  );
		m_entityTypeLB.addItem(m_messages.changeLogEntityType_profiles(),    "profiles"   );
		m_entityTypeLB.setSelectedIndex(0);
		m_entityTypeLB.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// If the user has somehow selected the select an
				// entity type option...
				int		si   = m_entityTypeLB.getSelectedIndex();
				String	type = m_entityTypeLB.getValue(si);
				if (0 == type.length()) {
					// ...ignore it.
					return;
				}

				// ...and if the first item in the <SELECT> is the
				// ...select an entity type option... 
				if ((0 < si) && (0 == m_entityTypeLB.getValue(0).length())) {
					// ...remove it...
					m_entityTypeLB.removeItem(0);
				}

				// ...and set the FindCtrl to search for the selected
				// ...type.
				SearchType st;
				if      (type.equals("folderEntry")) st = SearchType.ENTRIES;
				else if (type.equals("user"))        st = SearchType.USER;
				else if (type.equals("group"))       st = SearchType.GROUP;
				else if (type.equals("folder"))      st = SearchType.FOLDERS;
				else if (type.equals("workspace"))   st = SearchType.FOLDERS;
				else if (type.equals("profiles"))    st = SearchType.PERSON;
				else                                 st = null;
				boolean enable = (null != st);
				if (enable) {
					m_entityFinder.setSearchType(st);
				}
				FocusWidget fw = m_entityFinder.getFocusWidget();
				if ((null != fw) && (fw instanceof TextBox)) {
					fw.setEnabled(enable);
				}
			}
		});
		
		
		// ...add the selector field for the filter...
		il = buildInlineLabel(m_messages.changeLogFilterByOperation(), "vibe-changeLogReportComposite-label");
		ft.setWidget(6, 0, il);
		
		m_filterLB = new ListBox();
		m_filterLB.addStyleName("vibe-changeLogReportComposite-filterList");
		ft.setWidget(7, 0, m_filterLB);
		m_filterLB.addItem(m_messages.changeLogFilter_showAll(),             ""                   );
		m_filterLB.addItem(m_messages.changeLogFilter_addEntry(),            "addEntry"           );
		m_filterLB.addItem(m_messages.changeLogFilter_modifyEntry(),         "modifyEntry"        );
		m_filterLB.addItem(m_messages.changeLogFilter_deleteEntry(),         "deletetEntry"       );
		m_filterLB.addItem(m_messages.changeLogFilter_startWorkflow(),       "startWorkflow"      );
		m_filterLB.addItem(m_messages.changeLogFilter_modifyWorkflowState(), "modifyWorkflowState");
		m_filterLB.addItem(m_messages.changeLogFilter_addWorkflowResponse(), "addWorkflowResponse");
		m_filterLB.addItem(m_messages.changeLogFilter_moveEntry(),           "moveEntry"          );
		m_filterLB.addItem(m_messages.changeLogFilter_addFile(),             "addFile"            );
		m_filterLB.addItem(m_messages.changeLogFilter_modifyFile(),          "modifyFile"         );
		m_filterLB.addItem(m_messages.changeLogFilter_deleteFile(),          "deleteFile"         );
		m_filterLB.addItem(m_messages.changeLogFilter_deleteVersion(),       "deleteVersion"      );
		m_filterLB.addItem(m_messages.changeLogFilter_renameFile(),          "renameFile"         );
		m_filterLB.addItem(m_messages.changeLogFilter_addBinder(),           "addBinder"          );
		m_filterLB.addItem(m_messages.changeLogFilter_modifyBinder(),        "modifyBinder"       );
		m_filterLB.addItem(m_messages.changeLogFilter_deleteBinder(),        "deleteBinder"       );
		m_filterLB.addItem(m_messages.changeLogFilter_moveBinder(),          "moveBinder"         );
		m_filterLB.addItem(m_messages.changeLogFilter_modifyAccess(),        "modifyAccess"       );
		m_filterLB.addItem(m_messages.changeLogFilter_deleteAccess(),        "deleteAccess"       );
		m_filterLB.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// If the user has somehow selected the show all
				// changes option...
				int		si     = m_filterLB.getSelectedIndex();
				String	filter = m_filterLB.getValue(si);
				if (0 == filter.length()) {
					// ...ignore it.
					return;
				}

				// ...and if the first item in the <SELECT> is the
				// ...show all changes option... 
				if ((0 < si) && (0 == m_filterLB.getValue(0).length())) {
					// ...remove it...
					m_filterLB.removeItem(0);
				}
			}
		});
		
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
	}
	
	/*
	 * Creates a report and downloads it.
	 */
	private void createReport() {
//!		...this needs to be implemented...
		GwtClientHelper.deferredAlert("ChangeLogReportComposite.createReport():  ...this needs to be implemented...");
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
				new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						// Hide the search results...
						m_binderFinder.hideSearchResults();
						if (null != idS) {
							// ...and put the ID of the selection into
							// ...the corresponding ID entry widget.
							m_binderIdTB.setText(idS);
						}
						m_entityFinder.enableScope(idS);
					}
				});
		}
		
		else if (findLabel.equals(m_entityFinderLabel)) {
			// Process the find results.
			GwtClientHelper.deferCommand(
				new Scheduler.ScheduledCommand() {
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
	
	/**
	 * Resets the reports content.
	 * 
	 * Implements the ReportCompositeBase.resetReport() method.
	 */
	@Override
	public void resetReport() {
//!		...this needs to be implemented...
	}
}
