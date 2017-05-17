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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.select.Elements;

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
    private static final String HTML_INSERTION_POINT = "ss_insertion_point";
    private static final String HTML_FRAGMENT_BEGIN = "ss_fragment_begin";
    private static final String HTML_FRAGMENT_END = "ss_fragment_end";

    private static Set<WorkspaceType> standardBinderViews = new HashSet<WorkspaceType>() {
        {
            add(WorkspaceType.ADMINISTRATOR_MANAGEMENT);
            add(WorkspaceType.EMAIL_TEMPLATES);
            add(WorkspaceType.GLOBAL_ROOT);
            add(WorkspaceType.NET_FOLDERS_ROOT);
            add(WorkspaceType.PROFILE_ROOT);
            add(WorkspaceType.PROFILE_ROOT_MANAGEMENT);
            add(WorkspaceType.TEAM_ROOT);
            add(WorkspaceType.TEAM_ROOT_MANAGEMENT);
            add(WorkspaceType.TRASH);
        }
    };

    private static Set<String> binderViewsToSkip = new HashSet<String>() {
        {
        }
    };

    public static BinderViewLayout buildBinderViewLayout(Binder binder) throws GwtTeamingException {
        BinderViewLayout layout = new BinderViewLayout();
        List<Node> viewItems = getBinderViewElementNodes(binder);
        layout.setChildren(toBinderViewList(viewItems, layout));
        return layout;
    }

    private static List<BinderViewDefBase> toBinderViewList(List<Node> viewItems, BinderViewContainer parent) throws GwtTeamingException {
        List<BinderViewDefBase> layoutItems = new ArrayList<BinderViewDefBase>(viewItems.size());
        for (Node viewItem : viewItems) {
            List<BinderViewDefBase> layoutItem = factoryBinderViewItem((Element) viewItem, parent);
            if (layoutItem!=null) {
                layoutItems.addAll(layoutItem);
            }
        }
        return layoutItems;
    }

    public static List<BinderViewDefBase> factoryBinderViewChildren(Element viewItem, BinderViewContainer parent) throws GwtTeamingException {
        List<Node> viewItems = viewItem.selectNodes("item");
        return toBinderViewList(viewItems, parent);
    }

    public static List<BinderViewDefBase> factoryBinderViewItem(Element viewItem, BinderViewContainer parent) throws GwtTeamingException {
        BinderViewDefBase binderView = null;
        List<BinderViewDefBase> additionalEntries = null;

        String name = viewItem.attributeValue("name");
        if (!binderViewsToSkip.contains(name)) {
            boolean skip = false;
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
                    if (binderView instanceof BinderViewHtmlContainer) {
                        String htmlTop = properties.get("htmlTop");
                        String htmlBottom = properties.get("htmlBottom");
                        String outerTop = null;
                        String outerBottom = null;

                        if (parent instanceof BinderViewHtmlContainer) {
                            outerTop = ((BinderViewHtmlContainer)parent).getHtmlTop();
                            outerBottom = ((BinderViewHtmlContainer)parent).getHtmlBottom();
                        }

                        BinderViewHtmlContainer newContainer = parseHtml(htmlTop, htmlBottom, outerTop, outerBottom);
                        newContainer.setName(binderView.getName());
                        newContainer.setHtmlTop(outerTop == null ? htmlTop : outerTop + htmlTop);
                        newContainer.setHtmlBottom(outerBottom == null ? htmlBottom : outerBottom + htmlBottom);

                        binderView = newContainer;
                    }
                    ((BinderViewContainer) binderView).setChildren(factoryBinderViewChildren(viewItem, (BinderViewContainer) binderView));
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
                } else {
                    if (binderView instanceof BinderViewFolderDataItem) {
                        String formItem = viewItem.attributeValue("formItem");
                        if ("mashupCanvas".equals(formItem)) {
                            binderView = new BinderViewLandingPageLayout();
                        } else {
                            binderView = null;
                        }
                    } else if (binderView instanceof BinderViewJsp) {
                        String jspName = DefinitionHelper.getCustomJspName(viewItem);
                        if (jspName!=null) {
                            BinderViewJsp jspView = (BinderViewJsp) binderView;
                            jspView.setCustom(true);
                            jspView.setJsp(jspName);
                            binderView = jspView;
                        } else {
                            skip = true;
                        }
                    } else if (binderView instanceof BinderViewBreadCrumb) {
                        additionalEntries = factoryBinderViewChildren(viewItem, parent);
                    }
                }
            }
            if (binderView == null && !skip) {
                String jsp = configBuilder.getItemJspByStyle(viewItem, name, Definition.JSP_STYLE_DEFAULT);
                if (jsp != null) {
                    BinderViewJsp binderJspView = new BinderViewJsp(jsp);
                    binderJspView.setName(name);
                    binderJspView.setItemId(viewItem.attributeValue("id"));
                    binderView = binderJspView;
                }
            }
        }
        if (binderView!=null) {
            List<BinderViewDefBase> retList =  new ArrayList<BinderViewDefBase>();
            retList.add(binderView);
            if (additionalEntries!=null) {
                retList.addAll(additionalEntries);
            }
            return retList;
        }
        return null;
    }

    public static boolean hasCustomView(AllModulesInjected ami, Binder binder, boolean trash) {
        if (binder==null) {
            return false;
        }
        if (trash) {
            return false;
        }
        if (binder instanceof Workspace) {
            WorkspaceType workspaceType = GwtServerHelper.getWorkspaceType(binder);
            return !standardBinderViews.contains(workspaceType);
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

    public static BinderViewHtmlContainer parseHtml(String htmlTop, String htmlBottom) {
        return parseHtml(htmlTop, htmlBottom, null, null);
    }

    public static BinderViewHtmlContainer parseHtml(String htmlTop, String htmlBottom, String outerTop, String outerBottom) {
        String htmlStr = htmlTop + "<!--" + HTML_INSERTION_POINT + "-->" + htmlBottom;
        if (outerTop!=null && outerBottom!=null) {
            htmlStr = outerTop + "<!--" + HTML_FRAGMENT_BEGIN + "-->" + htmlStr + "<!--" + HTML_FRAGMENT_END + "-->" + outerBottom;
        } else if (outerTop!=null) {
            htmlStr = outerTop + "<!--" + HTML_FRAGMENT_BEGIN + "-->" + htmlStr + "<!--" + HTML_FRAGMENT_END + "-->";
        } else if (outerBottom!=null) {
            htmlStr = "<!--" + HTML_FRAGMENT_BEGIN + "-->" + htmlStr + "<!--" + HTML_FRAGMENT_END + "-->" + outerBottom;
        }
        org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(htmlStr);
        Elements elements = extractFragmentElements(doc);
        Stack<org.jsoup.nodes.Element> parents = getCommentParentHierarchy(elements, HTML_INSERTION_POINT);

        HTMLListInsertionPointPair pair = buildHtmlStructure(elements, parents, outerTop, outerBottom);
        if (pair.children!=null && pair.children.size()==1 && pair.children.get(0) instanceof BinderViewHtmlContainer) {
            return (BinderViewHtmlContainer) pair.children.get(0);
        } else {
            BinderViewHtmlContainer container = new BinderViewHtmlContainer();
            for (BinderViewHtml child : pair.children) {
                container.addChild((BinderViewDefBase) child);
            }
            container.setChildInsertionPoint(pair.insertionPoint);
            return container;
        }
    }

    private static Elements extractFragmentElements(org.jsoup.nodes.Document doc) {
        Elements elements = new Elements();
        Comment begin = findComment(doc.body(), HTML_FRAGMENT_BEGIN);
        Comment end = findComment(doc.body(), HTML_FRAGMENT_END);
        if (begin!=null) {
            org.jsoup.nodes.Node node = begin.nextSibling();
            while (node!=null && node!=end) {
                if (node instanceof org.jsoup.nodes.Element) {
                    elements.add((org.jsoup.nodes.Element) node);
                }
                node = node.nextSibling();
            }
        } else {
            elements = doc.body().children();
        }
        return elements;
    }

    private static Comment findComment(Elements elements, String commentText) {
        for (org.jsoup.nodes.Element element : elements) {
            Comment comment = findComment(element, commentText);
            if (comment!=null) {
                return comment;
            }
        }
        return null;
    }
    private static Comment findComment(org.jsoup.nodes.Element element, String commentText) {
        List<org.jsoup.nodes.Node> nodes = element.childNodes();
        for (org.jsoup.nodes.Node node : nodes) {
            if (node instanceof Comment) {
                Comment comment = (Comment) node;
                if (commentText.equals(comment.getData().trim())) {
                    return comment;
                }
            } else if (node instanceof org.jsoup.nodes.Element) {
                Comment comment = findComment((org.jsoup.nodes.Element)node, commentText);
                if (comment!=null) {
                    return comment;
                }
            }
        }
        return null;
    }

    private static BinderViewHtmlBlock buildHtmlBlock(org.jsoup.nodes.Element element) {
        BinderViewHtmlBlock block = new BinderViewHtmlBlock();
        populateTagInformation(block, element);
        block.setInnerHtml(element.html());
        return block;
    }

    private static BinderViewHtmlContainer buildHtmlElement(org.jsoup.nodes.Element element,
                                                            Stack<org.jsoup.nodes.Element> placeholderHierarchy) {
        BinderViewHtmlContainer html = new BinderViewHtmlContainer();
        populateTagInformation(html, element);
        html.setText(element.text());

        BinderViewHtmlContainer insertionPoint = null;
        if (placeholderHierarchy.empty()) {
            insertionPoint = html;
        }

        HTMLListInsertionPointPair structure = buildHtmlComponents(html, element.children(), placeholderHierarchy);
        for (BinderViewHtml child : structure.children) {
            html.addChild((BinderViewDefBase) child);
        }
        if (insertionPoint!=null) {
            html.setChildInsertionPoint(insertionPoint);
        } else {
            html.setChildInsertionPoint(structure.insertionPoint);
        }
        return html;
    }

    private static void populateTagInformation(BinderViewHtml html, org.jsoup.nodes.Element element) {
        html.setTag(element.tagName());
        for (org.jsoup.nodes.Attribute attr : element.attributes()) {
            html.setAttribute(attr.getKey(), attr.getValue());
        }
    }

    private static HTMLListInsertionPointPair buildHtmlComponents(BinderViewHtmlContainer parent, Elements elements, Stack<org.jsoup.nodes.Element> placeholderHierarchy) {
        org.jsoup.nodes.Element placeholderChild = null;
        BinderViewHtmlContainer insertionPoint = null;
        if (!placeholderHierarchy.empty()) {
            placeholderChild = placeholderHierarchy.pop();
        }
        List<BinderViewHtml> children = new ArrayList<BinderViewHtml>();
        for (org.jsoup.nodes.Element child : elements) {
            if (placeholderChild!=null && child==placeholderChild) {
                BinderViewHtmlContainer container = buildHtmlElement(child, placeholderHierarchy);
                container.setParent(parent);
                children.add(container);
                insertionPoint = container.getChildInsertionPoint();
            } else {
                children.add(buildHtmlBlock(child));
            }
        }
        HTMLListInsertionPointPair pair = new HTMLListInsertionPointPair();
        pair.children = children;
        pair.insertionPoint = insertionPoint;
        return pair;
    }

    private static HTMLListInsertionPointPair buildHtmlStructure(Elements elements, Stack<org.jsoup.nodes.Element> placeholderHierarchy, String outerTop, String outerBottom) {
        return buildHtmlComponents(null, elements, placeholderHierarchy);
    }

    private static Stack<org.jsoup.nodes.Element> getCommentParentHierarchy(Elements elements, String commentText) {
        Comment comment = findComment(elements, commentText);
        if (comment!=null) {
            Stack<org.jsoup.nodes.Element> parents = new Stack<org.jsoup.nodes.Element>();
            org.jsoup.nodes.Element parent = (org.jsoup.nodes.Element)comment.parent();
            while (parent!=null) {
                parents.push(parent);
                if (elements.contains(parent)) {
                    parent = null;
                } else {
                    parent = parent.parent();
                }
            }
            return parents;
        }
        return new Stack<org.jsoup.nodes.Element>();
    }

    static class HTMLListInsertionPointPair {
        List<BinderViewHtml> children;
        BinderViewHtmlContainer insertionPoint;
    }
}
