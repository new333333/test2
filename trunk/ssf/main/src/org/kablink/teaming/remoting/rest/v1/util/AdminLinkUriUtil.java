package org.kablink.teaming.remoting.rest.v1.util;

/**
 * User: David
 * Date: 11/18/13
 * Time: 1:31 PM
 */
public class AdminLinkUriUtil {
    public static String getNetFolderServerLinkUri(Long id) {
        return "/net_folder_servers/" + id;
    }

    public static String getUserSourceLinkUri(String id) {
        return "/user_sources/" + id;
    }


}
