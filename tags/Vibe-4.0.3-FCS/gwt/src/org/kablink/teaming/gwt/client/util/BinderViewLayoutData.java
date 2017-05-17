/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
 * <p/>
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * <p/>
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * <p/>
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * <p/>
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * <p/>
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.binderviews.RenderEngine;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.event.ShowBinderEvent;
import org.kablink.teaming.gwt.client.event.ShowCustomBinderViewEvent;
import org.kablink.teaming.gwt.client.event.ShowStandardBinderViewEvent;
import org.kablink.teaming.gwt.client.widgets.VibeEntityViewPanel;

/**
 * Created by david on 4/13/16.
 */
public class BinderViewLayoutData {
    private ViewInfo viewInfo;
    private ShowBinderEvent typeBasedEvent;

    public BinderViewLayoutData(ViewInfo viewInfo, ShowBinderEvent typeBasedEvent) {
        this.viewInfo = viewInfo;
        this.typeBasedEvent = typeBasedEvent;
    }

    public ShowBinderEvent getShowBinderEvent(VibeEntityViewPanel parent, ViewReady viewReady) {
        ShowBinderEvent viewEvent = null;

        String typeStr = "?";
        BinderInfo bi = viewInfo.getBinderInfo();
        if (bi!=null) {
            BinderType type = bi.getBinderType();
            typeStr = type.name();
            if (type == BinderType.FOLDER && bi.getFolderType()!=null) {
                typeStr += "/" + bi.getFolderType().name();
            } else if (type == BinderType.WORKSPACE && bi.getWorkspaceType()!=null) {
                typeStr += "/" + bi.getWorkspaceType().name();
            } else if (type == BinderType.COLLECTION && bi.getCollectionType()!=null) {
                typeStr += "/" + bi.getCollectionType().name();
            }
        } else if (viewInfo.getViewType()!=null) {
            typeStr = viewInfo.getViewType().name();
        }

//        GwtClientHelper.consoleLog("BinderViewLayoutData: Determining how to render the binder view: " + typeStr);
//        GwtClientHelper.consoleLog("BinderViewLayoutData: Render engine: " + viewInfo.getRenderEngine().name() +
//                "; view layout: " + viewInfo.getViewLayout() + "; force JSP: " + viewInfo.getBinderInfo().isForceJspRendering() +
//                "; GWT show event: " + typeBasedEvent);
        if (showCustomGwtBinderView()) {
//            GwtClientHelper.consoleLog("BinderViewLayoutData: Show custom GWT binder view?");
            viewEvent = new ShowCustomBinderViewEvent(this, parent, viewReady);
        } else if (showStandardGwtBinderView()) {
//            GwtClientHelper.consoleLog("BinderViewLayoutData: Show standard GWT binder view?");
            viewEvent = new ShowStandardBinderViewEvent(this, parent, viewReady);
        }
        return viewEvent;
    }

    public ShowBinderEvent getUnderlyingShowBinderEvent(VibeEntityViewPanel parent, ViewReady viewReady) {
        typeBasedEvent.initialize(parent, viewReady);
        return typeBasedEvent;
    }

    public BinderInfo getBinderInfo() {
        return viewInfo.getBinderInfo();
    }

    public ViewType getViewType() {
        return viewInfo.getViewType();
    }

    public BinderViewLayout getViewLayout() {
        return viewInfo.getViewLayout();
    }

    private boolean showStandardGwtBinderView() {
        BinderInfo bi = viewInfo.getBinderInfo();
        return (viewInfo.getRenderEngine() == RenderEngine.GWT_STANDARD ||
                (viewInfo.getRenderEngine() == RenderEngine.GWT_CUSTOMIZED && viewInfo.getViewLayout() == null)) &&
                !bi.isForceJspRendering() &&
                typeBasedEvent != null;
    }

    private boolean showCustomGwtBinderView() {
        BinderInfo bi = viewInfo.getBinderInfo();
        return viewInfo.getRenderEngine() == RenderEngine.GWT_CUSTOMIZED && viewInfo.getViewLayout() != null &&
                !bi.isForceJspRendering() &&
                typeBasedEvent != null;
    }

    public BinderType getBinderType() {
        return getBinderInfo().getBinderType();
    }

    public boolean isWorkspace() {
        return getBinderInfo().isBinderWorkspace();
    }

    public WorkspaceType getWorkspaceType() {
        BinderInfo info = getBinderInfo();
        if (info.isBinderWorkspace()){
            return info.getWorkspaceType();
        }
        return null;
    }
}
