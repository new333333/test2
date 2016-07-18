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
package org.kablink.teaming.gwt.client.binderviews;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.*;
import org.kablink.teaming.gwt.client.lpe.ConfigData;
import org.kablink.teaming.gwt.client.rpc.shared.*;
import org.kablink.teaming.gwt.client.util.*;
import org.kablink.teaming.gwt.client.widgets.VibeEntityViewPanel;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeGrid;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom binder view.
 * 
 * @author david
 */
public class CustomBinderView extends BinderViewBase implements ViewReady, ToolPanelReady, ProvidesResize, RequiresResize
{
	protected VibeFlowPanel m_layoutPanel;
	private BinderViewLayout m_viewLayout;
	private ConfigData m_landingPageConfigData;

	/**
	 * Constructor method.
	 *
	 * @param viewReady
	 */
    private CustomBinderView(BinderViewLayoutData layoutData, UIObject parent, ViewReady viewReady) {
		// Simply initialize the super class.
        super(layoutData, parent, viewReady);
        m_viewLayout = layoutData.getViewLayout();
		initialize();
    }

	private void initializeWithConfigData(ConfigData landingPageConfigData) {
		m_landingPageConfigData = landingPageConfigData;
		configurationComplete();
	}


	public void setViewSize() {
		boolean allowToScroll = scrollEntireView();

		UIObject parent = m_parent;
		if (parent==null) {
			parent = GwtTeaming.getMainPage().getMainContentLayoutPanel();
		}
//		GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  Heights: parent: " + parent.getOffsetHeight() +
//				"; main: " + m_mainPanel.getOffsetHeight() + "; footer: " + getFooterHeight());

		int height = parent.getOffsetHeight() + GwtConstants.BINDER_VIEW_ADJUST * 4;
		int width = parent.getOffsetWidth() + GwtConstants.BINDER_VIEW_ADJUST * 2;

//		GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  New size: (" + width + "," + height + ")");
		this.setPixelSize(width, height);
		if (!allowToScroll) {
			height = height - getFooterHeight();
//			GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  New layout panel size: (" + width + "," + height + ")");
			m_layoutPanel.setPixelSize(width, height);
//			GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  Layout panel resized.");
			m_layoutPanel.onResize();
		}
	}

	@Override
	protected void layoutContent(VibeFlowPanel parentPanel, boolean scrollEntireView) {
		// Add a place for the layout based on the binder definition
		{
			m_layoutPanel = new VibeFlowPanel();
			//m_layoutPanel.setWidth("100%");
			//m_layoutPanel.setHeight("100%");
			m_layoutPanel.addStyleName( "vibe-binderView_LayoutPanel" );
			if (!scrollEntireView) {
				m_layoutPanel.addStyleName("vibe-binderView_OverflowAuto");
			}
			parentPanel.add( m_layoutPanel );
		}

		this.addChildControls(m_viewLayout, m_layoutPanel);
	}

	public void addChildControls(BinderViewContainer parentDef, VibeEntityViewPanel parentWidget) {
		for (BinderViewDefBase child : parentDef.getChildren()) {
			addControl(child, parentWidget);
		}
	}

	public void addControl(BinderViewDefBase viewDef, VibeEntityViewPanel parentWidget) {
		if (viewDef instanceof BinderViewContainer) {
			VibeEntityViewPanel viewPanel;
			if (viewDef instanceof BinderViewTwoColumnTable) {
				BinderViewTwoColumnTable tableDef = (BinderViewTwoColumnTable) viewDef;
				VibeGrid viewGrid = new VibeGrid(1,2);
				viewGrid.addStyleName("vibe-grid");
				if (tableDef.getWidth() != null) {
					viewGrid.setWidth(tableDef.getWidth().toString());
//				} else {
//					viewGrid.setWidth("100%");
				}
				if (tableDef.getColumn1Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(0, tableDef.getColumn1Width().toString());
				}
				if (tableDef.getColumn2Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(1, tableDef.getColumn2Width().toString());
				}
				viewPanel = viewGrid;
			} else if (viewDef instanceof BinderViewThreeColumnTable) {
                BinderViewThreeColumnTable tableDef = (BinderViewThreeColumnTable) viewDef;
                VibeGrid viewGrid = new VibeGrid(1, 3);
                viewGrid.addStyleName("vibe-grid");
                //viewGrid.setHeight("100%");
                if (tableDef.getWidth() != null) {
                    viewGrid.setWidth(tableDef.getWidth().toString());
//				} else {
//					viewGrid.setWidth("100%");
                }
                if (tableDef.getColumn1Width() != null) {
                    viewGrid.getColumnFormatter().setWidth(0, tableDef.getColumn1Width().toString());
                }
                if (tableDef.getColumn2Width() != null) {
                    viewGrid.getColumnFormatter().setWidth(1, tableDef.getColumn2Width().toString());
                }
                if (tableDef.getColumn3Width() != null) {
                    viewGrid.getColumnFormatter().setWidth(2, tableDef.getColumn3Width().toString());
                }
                viewPanel = viewGrid;
            } else if (viewDef instanceof BinderViewBox) {
                BinderViewBox boxDef = (BinderViewBox) viewDef;
                VibeFlowPanel flowPanel = new VibeFlowPanel();
                flowPanel.addStyleName("vibe-flow");
                if (boxDef.isBorder()) {
                    flowPanel.addStyleName("vibe-binderView_Box");
                }
                viewPanel = flowPanel;
			} else {
				BinderViewContainer containerDef = (BinderViewContainer) viewDef;
				VibeFlowPanel flowPanel = new VibeFlowPanel();
				flowPanel.addStyleName("vibe-flow");
				//flowPanel.setHeight("100%");
				if (containerDef.getWidth()!=null) {
					flowPanel.setWidth(containerDef.getWidth().toString());
//				} else {
//					flowPanel.setWidth("100%");
				}
				viewPanel = flowPanel;
			}
			parentWidget.showWidget((Widget) viewPanel);
			addChildControls((BinderViewContainer) viewDef, viewPanel);
		} else {
			if (viewDef instanceof BinderViewFolderListing) {
                //GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".addControl().  Adding folder listing...");
				VibeFlowPanel flowPanel = new VibeFlowPanel(true);
				flowPanel.addStyleName("vibe-flow");
                parentWidget.showWidget(flowPanel);
				BinderInfo bi = getBinderInfo();
				m_delegatingViewReady.incrementComponent();
                ShowBinderEvent viewEvent = m_layoutData.getUnderlyingShowBinderEvent(flowPanel, this);
                if (viewEvent != null) {
					//GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".addControl().  Firing event: "+ viewEvent.getClass().getSimpleName());
					GwtTeaming.fireEvent(viewEvent);
				}
			} else if (viewDef instanceof BinderViewBreadCrumb) {
				buildBreadCrumbPanel((HasWidgets) parentWidget);
			} else if (viewDef instanceof BinderViewDescription) {
				buildDescriptionPanel((HasWidgets) parentWidget);
			} else if (viewDef instanceof BinderViewAccessories) {
				buildAccessoriesPanel((HasWidgets) parentWidget);
			} else if (viewDef instanceof BinderViewBinderList) {
				buildChildBindersPanel((HasWidgets) parentWidget, m_delegatingViewReady);
			} else if (viewDef instanceof BinderViewHtmlEntry) {
				buildHTMLPanel((HasWidgets) parentWidget, (BinderViewHtmlEntry) viewDef, m_delegatingViewReady);
			} else if (viewDef instanceof BinderViewLandingPageLayout) {
				buildLandingPageLayout((HasWidgets) parentWidget, m_delegatingViewReady);
            } else if (viewDef instanceof BinderViewJsp) {
                VibeFlowPanel flowPanel = new VibeFlowPanel();
				flowPanel.addStyleName("vibe-flow");
				parentWidget.showWidget(flowPanel);
				m_delegatingViewReady.incrementComponent();
				executeJspAsync((BinderViewJsp) viewDef, flowPanel, this);
			}
		}
	}

	/**
	 * Loads the CustomBinderView split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param viewReady
	 * @param vClient
	 */
    public static void createAsync(final BinderViewLayoutData layoutData, final UIObject parent, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(CustomBinderView.class, new RunAsyncCallback() {
            @Override
            public void onSuccess() {
                CustomBinderView customBinderView;

                customBinderView = new CustomBinderView(layoutData, parent, viewReady);
                vClient.onSuccess(customBinderView);
            }

            @Override
            public void onFailure(Throwable reason) {
                Window.alert(m_messages.codeSplitFailure_CustomBinderView());
                vClient.onUnavailable();
            }
        });
	}

	/*
    * Asynchronously loads the next part of the accessories panel.
    */
	private void executeJspAsync(final BinderViewJsp jspView, final VibeFlowPanel parent, final ViewReady viewReady) {
		GwtClientHelper.deferCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                executeJspNow(jspView, parent, viewReady);
            }
        });
	}

	/*
     * Synchronously loads the next part of the accessories panel.
     */
	private void executeJspNow(BinderViewJsp jspView, final VibeFlowPanel parent, final ViewReady viewReady) {
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("binderId", getBinderInfo().getBinderId());
		model.put("itemId", jspView.getItemId());
        final VibeJspHtmlType jspType;
        if (jspView.isCustom()) {
            jspType = VibeJspHtmlType.CUSTOM_JSP;
            model.put("customJsp", jspView.getJsp());
        } else {
            jspType = VibeJspHtmlType.BUILT_IN_JSP;
            model.put("jsp", jspView.getJsp());
        }
		GwtClientHelper.executeCommand(
				new GetJspHtmlCmd(jspType, model),
				new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
								t,
								m_messages.rpcFailure_GetJspHtml(),
								jspType.toString());
					}

					@Override
					public void onSuccess(VibeRpcResponse response) {
						// Store the accessory panel HTML and continue loading.
						JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
						String html = responseData.getHtml();
						HTMLPanel htmlPanel = new HTMLPanel(html);
						parent.add(htmlPanel);
						executeJavaScriptAsync(htmlPanel, viewReady);
					}
				});
	}

	/*
	 * Asynchronously executes the JavaScript in the HTML panel.
	 */
	private void executeJavaScriptAsync(final HTMLPanel htmlPanel, final ViewReady viewReady) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
            @Override
            public void execute() {
                executeJavaScriptNow(htmlPanel, viewReady);
            }
        });
	}

	/*
	 * Asynchronously executes the JavaScript in the HTML panel.
	 */
	private void executeJavaScriptNow(HTMLPanel htmlPanel, final ViewReady viewReady) {
		GwtClientHelper.jsExecuteJavaScript(htmlPanel.getElement(), true);
		GwtClientHelper.jsOnLoadInit();
		viewReady.viewReady();
	}

	@Override
	protected boolean includeFooterPanel() {
		return m_landingPageConfigData==null || !m_landingPageConfigData.getHideFooter();
	}

	@Override
	protected boolean requiresAdditionalConfiguration() {
		//GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".requiresAdditionalConfiguration().  Workspace type: " + this.m_layoutData.getWorkspaceType());
		return this.m_layoutData.getWorkspaceType()==WorkspaceType.LANDING_PAGE;
	}

	@Override
	protected void fetchAdditionalConfiguration() {
		//GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".fetchAdditionalConfiguration().  Getting landing page config data...");
		final String binderId = getBinderIdAsString();
		GetLandingPageDataCmd cmd = new GetLandingPageDataCmd(binderId);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
            /**
             *
             */
            @Override
            public void onFailure(Throwable t) {
                GwtClientHelper.handleGwtRPCFailure(
                        t,
                        GwtTeaming.getMessages().rpcFailure_GetLandingPageData(),
                        binderId);
            }

            @Override
            public void onSuccess(VibeRpcResponse response) {
                final ConfigData configData;

                configData = (ConfigData) response.getResponseData();

                GwtClientHelper.deferCommand(new ScheduledCommand() {
                    /**
                     *
                     */
                    @Override
                    public void execute() {
                        // FInish initialization
                        initializeWithConfigData(configData);
                    }
                });
            }
        });

	}
}

