/*
 * $Header$
 * $Revision: 208330 $
 * $Date: 2004-12-21 11:17:04 -0500 (Tue, 21 Dec 2004) $
 *
 * ====================================================================
 *
 * Copyright 1999-2002 The Apache Software Foundation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.slide.webdav.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.SlideException;
import org.apache.slide.common.SlideToken;
import org.apache.slide.content.Content;
import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.lock.Lock;
import org.apache.slide.lock.NodeLock;
import org.apache.slide.security.NodePermission;
import org.apache.slide.security.Security;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.Structure;
import org.apache.slide.util.Messages;
import org.apache.slide.webdav.WebdavServletConfig;

/**
 * Utility class that encapsulates the generation of HTML directory index
 * pages.
 *
 * @version $Revision: 208330 $
 */
public class DirectoryIndexGenerator {
    
    
    // -------------------------------------------------------------- Constants
    
    
    /**
     * HTTP Date format pattern (RFC 2068, 822, 1123).
     */
    public static final String DATE_FORMAT =
        "EEE, d MMM yyyy kk:mm:ss z";
    
    
    /**
     * Date formatter.
     */
    private static final DateFormat formatter =
        new SimpleDateFormat(DATE_FORMAT);
    
    
    // ----------------------------------------------------- Instance Variables
    
    
    /**
     * Access token to the namespace.
     */
    protected NamespaceAccessToken nat;
    
    
    /**
     * Configuration of the WebDAV servlet.
     */
    protected WebdavServletConfig config;
    
    
    // ----------------------------------------------------------- Constructors
    
    
    /**
     * Constructor.
     *
     * @param nat       the namespace access token
     */
    public DirectoryIndexGenerator(NamespaceAccessToken nat,
                                   WebdavServletConfig config) {
        
        if (nat == null) {
            throw new IllegalArgumentException(
                "NamespaceAccessToken must not be null");
        }
        this.nat = nat;
        if (config == null) {
            throw new IllegalArgumentException(
                "WebdavServletConfig must not be null");
        }
        this.config = config;
    }
    // Little helpers to create character refrences
    private String stringToCharacterRef(String val) {
        StringBuffer result = new StringBuffer(val.length() * 8);
        for (int i = 0; i < val.length(); i++) {
            result.append(charToCharacterRef(val.charAt(i)));
        }
        return result.toString();
    }
    
    private String charToCharacterRef(char val) {
        StringBuffer result = new StringBuffer(8);
        result.append("&#x").append(Integer.toHexString((int) val).toUpperCase()).append(";");
        return result.toString();
    }
    
    
    // --------------------------------------------------------- Public Methods
    
    
    /**
     * Display a directory browsing page.
     *
     * @param req       the HTTP request
     * @param res       the HTTP response
     * @throw IOException       if an IO exception occurrs while writing the
     *                          response
     * @throw SlideException    if an exception occurrs accessing Slide
     */
    public void generate(HttpServletRequest req, HttpServletResponse res, SlideToken slideToken)
        throws IOException, SlideException {
        
        res.setContentType("text/html; charset=\"UTF-8\"");
        
        // get the helpers
        Content content = nat.getContentHelper();
        Lock lock = nat.getLockHelper();
        Security security = nat.getSecurityHelper();
        Structure structure = nat.getStructureHelper();
        
//        SlideToken slideToken = WebdavUtils.getSlideToken(req);
        String resourcePath = WebdavUtils.getRelativePath(req, config);
        ObjectNode object = structure.retrieve(slideToken, resourcePath);
        String name = object.getUri();
        
        // Number of characters to trim from the beginnings of filenames
        int trim = name.length();
        if (!name.endsWith("/"))
            trim += 1;
        if (name.equals("/"))
            trim = 1;
        
        PrintWriter writer = new PrintWriter(res.getWriter());
        
        // Render the page header
        writer.print("<html>\r\n");
        writer.print("<head>\r\n");
        writer.print("<meta http-equiv=\"Content-type\" content=\"text/html; charset=UTF-8\" >\r\n");
        writer.print("</meta>\r\n");
        writer.print("<title>");
        writer.print
            (Messages.format
                 ("org.apache.slide.webdav.GetMethod.directorylistingfor",
                  name));
        writer.print("</title>\r\n</head>\r\n");
        writer.print("<body bgcolor=\"white\">\r\n");
        writer.print("<table width=\"90%\" cellspacing=\"0\"" +
                         " cellpadding=\"5\" align=\"center\">\r\n");
        
        // Render the in-page title
        writer.print("<tr><td colspan=\"3\"><font size=\"+2\">\r\n<strong>");
        writer.print
            (Messages.format
                 ("org.apache.slide.webdav.GetMethod.directorylistingfor",
                  name));
        writer.print("</strong>\r\n</font></td></tr>\r\n");
        
        // Render the link to our parent (if required)
        String parentDirectory = name;
        if (parentDirectory.endsWith("/")) {
            parentDirectory =
                parentDirectory.substring(0, parentDirectory.length() - 1);
        }
        String scope = config.getScope();
        parentDirectory = parentDirectory.substring(scope.length());
        if (parentDirectory.lastIndexOf("/") >= 0) {
            parentDirectory = parentDirectory.substring(0, parentDirectory.lastIndexOf("/"));
            writer.print("<tr><td colspan=\"5\" bgcolor=\"#ffffff\">\r\n");
            writer.print("<a href=\"");
            writer.print(WebdavUtils.getAbsolutePath(scope, req, config));
            if (parentDirectory.equals(""))
                parentDirectory = "/";
            writer.print(parentDirectory);   // I18N chars
            writer.print("\">");
            writer.print(Messages.format
                             ("org.apache.slide.webdav.GetMethod.parent",
                              parentDirectory));
            writer.print("</a>\r\n");
            writer.print("</td></tr>\r\n");
        }
        
        Enumeration permissionsList = null;
        Enumeration locksList = null;
        try {
            permissionsList =
                security.enumeratePermissions(slideToken, object.getUri());
            locksList = lock.enumerateLocks(slideToken, object.getUri(), false);
        } catch (SlideException e) {
            // Any security based exception will be trapped here
            // Any locking based exception will be trapped here
        }
        
        // Displaying ACL info
        if (org.apache.slide.util.Configuration.useIntegratedSecurity()) {
            displayPermissions(permissionsList, writer, false);
        }
        
        // Displaying lock info
        displayLocks(locksList, writer, false);
        
        writer.print("<tr><td colspan=\"5\" bgcolor=\"#ffffff\">");
        writer.print("&nbsp;");
        writer.print("</td></tr>\r\n");
        
        // Render the column headings
        writer.print("<tr bgcolor=\"#cccccc\">\r\n");
        writer.print("<td align=\"left\" colspan=\"3\">");
        writer.print("<font size=\"+1\"><strong>");
        writer.print(Messages.message
                         ("org.apache.slide.webdav.GetMethod.filename"));
        writer.print("</strong></font></td>\r\n");
        writer.print("<td align=\"center\"><font size=\"+1\"><strong>");
        writer.print(Messages.message
                         ("org.apache.slide.webdav.GetMethod.size"));
        writer.print("</strong></font></td>\r\n");
        writer.print("<td align=\"right\"><font size=\"+1\"><strong>");
        writer.print(Messages.message
                         ("org.apache.slide.webdav.GetMethod.lastModified"));
        writer.print("</strong></font></td>\r\n");
        writer.print("</tr>\r\n");
        
        Enumeration resources = structure.getChildren(slideToken,object);
        boolean shade = false;
        
        while (resources.hasMoreElements()) {
            String currentResource = ((ObjectNode)resources.nextElement()).getUri();
            NodeRevisionDescriptor currentDescriptor = null;
            permissionsList = null;
            locksList = null;
            try {
                NodeRevisionDescriptors revisionDescriptors =
                    content.retrieve(slideToken, currentResource);
                // Retrieve latest revision descriptor
                currentDescriptor =
                    content.retrieve(slideToken, revisionDescriptors);
            } catch (SlideException e) {
                // Silent exception : Objects without any revision are
                // considered collections, and do not have any attributes
                // Any security based exception will be trapped here
                // Any locking based exception will be trapped here
            }
            
            try {
                permissionsList =
                    security.enumeratePermissions(slideToken, currentResource);
                locksList = lock.enumerateLocks(slideToken, currentResource, false);
            } catch (SlideException e) {
                // Any security based exception will be trapped here
                // Any locking based exception will be trapped here
            }
            
            String trimmed = currentResource.substring(trim);
            if (trimmed.equalsIgnoreCase("WEB-INF") ||
                trimmed.equalsIgnoreCase("META-INF")) {
                continue;
            }
            
            writer.print("<tr");
            if (shade) {
                writer.print(" bgcolor=\"dddddd\"");
            } else {
                writer.print(" bgcolor=\"eeeeee\"");
            }
            writer.print(">\r\n");
            shade = !shade;
            
            writer.print("<td align=\"left\" colspan=\"3\">&nbsp;&nbsp;\r\n");
            writer.print("<a href=\"");
            writer.print(WebdavUtils.getAbsolutePath(currentResource, req, config));
            writer.print("\"><tt>");
            NodeProperty displayname = null;
            if (currentDescriptor != null) {
                displayname = currentDescriptor.getProperty(WebdavConstants.PN_DISPLAYNAME);
            }
            if (displayname != null)
               writer.print(stringToCharacterRef((String)displayname.getValue()));
            else
               writer.print(stringToCharacterRef(trimmed));   // I18N chars
               
            if (currentDescriptor != null) {
                if (WebdavUtils.isCollection(currentDescriptor)) {
                    writer.print("/");
                }
                else if (WebdavUtils.isRedirectref(currentDescriptor)) {
                    writer.print("*");
                }
            }
            writer.print("</tt></a></td>\r\n");
            
            writer.print("<td align=\"right\"><tt>");
            if (currentDescriptor == null
                    || WebdavUtils.isCollection(currentDescriptor)
                    || WebdavUtils.isRedirectref(currentDescriptor)) {
                writer.print("&nbsp;");
            }
            else {
                writer.print(renderSize(currentDescriptor.getContentLength()));
            }
            writer.print("</tt></td>\r\n");
            
            writer.print("<td align=\"right\"><tt>");
            if (currentDescriptor != null) {
                writer.print(currentDescriptor.getLastModified());
            } else {
                writer.print("&nbsp;");
            }
            writer.print("</tt></td>\r\n");
            
            writer.print("</tr>\r\n");
            
            // Displaying ACL info
            if (org.apache.slide.util.Configuration.useIntegratedSecurity()) {
                displayPermissions(permissionsList, writer, shade);
            }
            
            // Displaying lock info
            displayLocks(locksList, writer, shade);
        }
        
        // Render the page footer
        writer.print("<tr><td colspan=\"5\">&nbsp;</td></tr>\r\n");
        writer.print("<tr><td colspan=\"3\" bgcolor=\"#cccccc\">");
        writer.print("<font size=\"-1\">");
        writer.print(Messages.message
                         ("org.apache.slide.webdav.GetMethod.version"));
        writer.print("</font></td>\r\n");
        writer.print("<td colspan=\"2\" align=\"right\" bgcolor=\"#cccccc\">");
        writer.print("<font size=\"-1\">");
        writer.print(formatter.format(new Date()));
        writer.print("</font></td></tr>\r\n");
        writer.print("</table>\r\n");
        writer.print("</body>\r\n");
        writer.print("</html>\r\n");
        
        // Return an input stream to the underlying bytes
        writer.flush();
    }
    
    
    // ------------------------------------------------------ Protected Methods
    
    
    /**
     * Display an ACL list.
     *
     * @param permissionsList   the list of NodePermission objects
     * @param writer            the output will be appended to this writer
     * @param shade             whether the row should be displayed darker
     */
    protected void displayPermissions(Enumeration permissionsList,
                                      PrintWriter writer,
                                      boolean shade)
        throws IOException {
        
        boolean hideAcl = false;
        String hideAclStr = config.getInitParameter( "directory-browsing-hide-acl" );
        if( "true".equalsIgnoreCase(hideAclStr) )
            hideAcl = true;
        
        if( !hideAcl ) {
            if ((permissionsList != null) && (permissionsList.hasMoreElements())) {
                writer.print("<tr" + (shade ? " bgcolor=\"eeeeee\""
                                          : " bgcolor=\"dddddd\"") +
                                 ">\r\n");
                writer.print("<td align=\"left\" colspan=\"5\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.aclinfo"));
                writer.print("</b></tt></td>\r\n");
                writer.print("</tr>\r\n");
                writer.print("<tr");
                if (!shade) {
                    writer.print(" bgcolor=\"dddddd\"");
                } else {
                    writer.print(" bgcolor=\"eeeeee\"");
                }
                writer.print(">\r\n");
                writer.print("<td align=\"left\" colspan=\"2\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.subject"));
                writer.print("</b></tt></td>\r\n");
                writer.print("<td align=\"left\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.action"));
                writer.print("</b></tt></td>\r\n");
                writer.print("<td align=\"right\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.inheritable"));
                writer.print("</b></tt></td>\r\n");
                writer.print("<td align=\"right\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.deny"));
                writer.print("</b></tt></td>\r\n");
                writer.print("</tr>\r\n");
                
                while (permissionsList.hasMoreElements()) {
                    writer.print("<tr" + (shade ? " bgcolor=\"eeeeee\""
                                              : " bgcolor=\"dddddd\"") +
                                     ">\r\n");
                    NodePermission currentPermission =
                        (NodePermission) permissionsList.nextElement();
                    writer.print("<td align=\"left\" colspan=\"2\"><tt>");
                    writer.print(currentPermission.getSubjectUri());
                    writer.print("</tt></td>\r\n");
                    writer.print("<td align=\"left\"><tt>");
                    writer.print(currentPermission.getActionUri());
                    writer.print("</tt></td>\r\n");
                    writer.print("<td align=\"right\"><tt>");
                    writer.print(currentPermission.isInheritable());
                    writer.print("</tt></td>\r\n");
                    writer.print("<td align=\"right\"><tt>");
                    writer.print(currentPermission.isNegative());
                    writer.print("</tt></td>\r\n");
                    writer.print("</tr>\r\n");
                }
            }
        }
    }
    
    
    /**
     * Display a lock list.
     *
     * @param permissionsList   the list of NodePermission objects
     * @param writer            the output will be appended to this writer
     * @param shade             whether the row should be displayed darker
     */
    protected void displayLocks(Enumeration locksList, PrintWriter writer,
                                boolean shade)
        throws IOException {
        
        boolean hideLocks = false;
        String hideLocksStr = config.getInitParameter( "directory-browsing-hide-locks" );
        if( "true".equalsIgnoreCase(hideLocksStr) )
            hideLocks = true;
        
        if( !hideLocks ) {
            if ((locksList != null) && (locksList.hasMoreElements())) {
                writer.print("<tr" + (shade ? " bgcolor=\"eeeeee\""
                                          : " bgcolor=\"dddddd\"") +
                                 ">\r\n");
                writer.print("<td align=\"left\" colspan=\"5\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.locksinfo"));
                writer.print("</b></tt></td>\r\n");
                writer.print("</tr>\r\n");
                writer.print("<tr");
                if (!shade) {
                    writer.print(" bgcolor=\"dddddd\"");
                } else {
                    writer.print(" bgcolor=\"eeeeee\"");
                }
                writer.print(">\r\n");
                writer.print("<td align=\"left\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.subject"));
                writer.print("</b></tt></td>\r\n");
                writer.print("<td align=\"left\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.type"));
                writer.print("</b></tt></td>\r\n");
                writer.print("<td align=\"right\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.expiration"));
                writer.print("</b></tt></td>\r\n");
                writer.print("<td align=\"right\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.inheritable"));
                writer.print("</b></tt></td>\r\n");
                writer.print("<td align=\"right\"><tt><b>");
                writer.print(Messages.message
                                 ("org.apache.slide.webdav.GetMethod.exclusive"));
                writer.print("</b></tt></td>\r\n");
                writer.print("</tr>\r\n");
                
                while (locksList.hasMoreElements()) {
                    writer.print("<tr" + (shade ? " bgcolor=\"eeeeee\""
                                              : " bgcolor=\"dddddd\"") +
                                     ">\r\n");
                    NodeLock currentLock = (NodeLock) locksList.nextElement();
                    writer.print("<td align=\"left\"><tt>");
                    writer.print(currentLock.getSubjectUri());
                    writer.print("</tt></td>\r\n");
                    writer.print("<td align=\"left\"><tt>");
                    writer.print(currentLock.getTypeUri());
                    writer.print("</tt></td>\r\n");
                    writer.print("<td align=\"right\"><tt>");
                    writer.print
                        (formatter.format(currentLock.getExpirationDate()));
                    writer.print("</tt></td>\r\n");
                    writer.print("<td align=\"right\"><tt>");
                    writer.print(currentLock.isInheritable());
                    writer.print("</tt></td>\r\n");
                    writer.print("<td align=\"right\"><tt>");
                    writer.print(currentLock.isExclusive());
                    writer.print("</tt></td>\r\n");
                }
            }
        }
    }
    
    
    /**
     * Render the specified file size (in bytes).
     *
     * @param size File size (in bytes)
     */
    protected String renderSize(long size) {
        
        long leftSide = size / 1024;
        long rightSide = (size % 1024) / 103;   // Makes 1 digit
        if ((leftSide == 0) && (rightSide == 0) && (size > 0))
            rightSide = 1;
        
        return ("" + leftSide + "." + rightSide + " kb");
    }
    
    
}

