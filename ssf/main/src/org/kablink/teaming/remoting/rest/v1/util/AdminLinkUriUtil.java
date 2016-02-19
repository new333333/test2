package org.kablink.teaming.remoting.rest.v1.util;

/**
 * User: David
 * Date: 11/18/13
 * Time: 1:31 PM
 */
public class AdminLinkUriUtil {
    public static String getNetFolderLinkUri(Long id) {
        return "/admin/net_folders/" + id;
    }

    public static String getNetFolderServerLinkUri(Long id) {
        return "/admin/net_folder_servers/" + id;
    }

    public static String getUserSourceLinkUri(String id) {
        return "/admin/user_sources/" + id;
    }

    public static String getShareLinkUri(Long id) {
        return "/admin/shares/" + id;
    }


}
