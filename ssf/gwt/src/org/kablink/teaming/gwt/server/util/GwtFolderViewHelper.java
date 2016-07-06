/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.util;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.util.*;
import org.kablink.teaming.module.definition.DefinitionConfigurationBuilder;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.DefinitionHelper;

import java.util.*;

/**
 * Created by david on 4/13/16.
 */
public class GwtFolderViewHelper {
    private static Set<String> binderViewsToSkip = new HashSet<String>() {
        {
            add("folderDescriptionView");
        }
    };

    public static BinderViewLayout buildBinderViewLayout(Binder binder) throws GwtTeamingException {
        BinderViewLayout layout = new BinderViewLayout();
        List<Node> viewItems = getBinderViewElementNodes(binder);
        layout.setChildren(toBinderViewList(viewItems));
        return layout;
    }

    private static List<BinderViewDefBase> toBinderViewList(List<Node> viewItems) throws GwtTeamingException {
        List<BinderViewDefBase> layoutItems = new ArrayList<BinderViewDefBase>(viewItems.size());
        for (Node viewItem : viewItems) {
            BinderViewDefBase layoutItem = factoryBinderViewItem((Element) viewItem);
            if (layoutItem!=null) {
                layoutItems.add(layoutItem);
            }
        }
        return layoutItems;
    }

    public static List<BinderViewDefBase> factoryBinderViewChildren(Element viewItem) throws GwtTeamingException {
        List<Node> viewItems = viewItem.selectNodes("item");
        return toBinderViewList(viewItems);
    }

    public static BinderViewDefBase factoryBinderViewItem(Element viewItem) throws GwtTeamingException {
        BinderViewDefBase binderView = null;
        String name = viewItem.attributeValue("name");
        if (!binderViewsToSkip.contains(name)) {
            DefinitionConfigurationBuilder configBuilder = DefinitionHelper.getDefinitionBuilderConfig();
            String folderViewClass = configBuilder.getItemGwtFolderViewByStyle(viewItem, name, Definition.JSP_STYLE_DEFAULT);
            if (folderViewClass != null) {
                try {
                    binderView = (BinderViewDefBase) Class.forName(folderViewClass).newInstance();
                    binderView.setName(name);
                } catch (InstantiationException e) {
                    throw new GwtTeamingException(GwtTeamingException.ExceptionType.UNKNOWN, e.getLocalizedMessage());
                } catch (IllegalAccessException e) {
                    throw new GwtTeamingException(GwtTeamingException.ExceptionType.UNKNOWN, e.getLocalizedMessage());
                } catch (ClassNotFoundException e) {
                    throw new GwtTeamingException(GwtTeamingException.ExceptionType.UNKNOWN, e.getLocalizedMessage());
                }
                Map<String, String> properties = DefinitionHelper.getDefinitionProperties(viewItem);
                if (binderView instanceof BinderViewContainer) {
                    ((BinderViewContainer) binderView).setChildren(factoryBinderViewChildren(viewItem));
                    if (binderView instanceof BinderViewTwoColumnTable) {
                        BinderViewTwoColumnTable tableView = (BinderViewTwoColumnTable) binderView;
                        tableView.setWidth(Width.parseWidth(properties.get("tableWidth")));
                        tableView.setColumn1Width(Width.parseWidth(properties.get("width1")));
                        tableView.setColumn2Width(Width.parseWidth(properties.get("width2")));
                    } else if (binderView instanceof BinderViewThreeColumnTable) {
                        BinderViewThreeColumnTable tableView = (BinderViewThreeColumnTable) binderView;
                        tableView.setWidth(Width.parseWidth(properties.get("tableWidth")));
                        tableView.setColumn1Width(Width.parseWidth(properties.get("width1")));
                        tableView.setColumn2Width(Width.parseWidth(properties.get("width2")));
                        tableView.setColumn3Width(Width.parseWidth(properties.get("width3")));
                    } else if (binderView instanceof BinderViewBox) {
                        BinderViewBox boxView = (BinderViewBox) binderView;
                        boxView.setBorder("square".equals(properties.get("style")));
                    }
                }
            } else {
                String jsp = configBuilder.getItemJspByStyle(viewItem, name, Definition.JSP_STYLE_DEFAULT);
                if (jsp != null) {
                    BinderViewJsp binderJspView = new BinderViewJsp(jsp);
                    binderJspView.setName(name);
                    binderJspView.setItemId(viewItem.attributeValue("id"));
                    binderView = binderJspView;
                }
            }
        }
        return binderView;
    }

    public static boolean hasCustomView(AllModulesInjected ami, Binder binder) {
        if (binder==null) {
            return false;
        }
        if (binder instanceof Workspace) {
            Definition def2 = binder.getDefaultViewDef();
            return !def2.getName().startsWith("_");
        }
        return true;
    }

    /*
     * Returns a List<Node> of the <item>'s from a binder's view
     * definition.
     */
    @SuppressWarnings("unchecked")
    private static List<Node> getBinderViewElementNodes(Binder binder) {
        // Do we have a Binder?
        List<Node> reply = null;
        if (null != binder) {
            // Yes!  Can we access it's view definition document?
            Definition viewDef    = binder.getDefaultViewDef();
            Document viewDefDoc = ((null == viewDef) ? null : viewDef.getDefinition());
            if (null != viewDefDoc) {
                // Yes!  Does it contain any HTML <item>'s?
                String viewName;
                if (binder instanceof Folder)
                    viewName = "forumView";
                else viewName = "workspaceView";
            reply = viewDefDoc.selectNodes("//item[@name='" + viewName + "']/item");
            }
        }

        // If we get here, reply refers to a List<Node> of the binder's
        // HTML <item>'s or is null.  Return it.
        return reply;
    }

}
