package org.kablink.teaming.util.stringcheck;

import junit.framework.TestCase;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.stringcheck.HtmlSanitizerCheck;
import org.kablink.teaming.util.stringcheck.XSSCheck;
import org.kablink.util.PropsUtil;

/**
 * Created by david on 4/28/16.
 */
public class HtmlSanitizerCheckTest extends TestCase {

    static {
//        PropsUtil.getProperties().setProperty("xss.check.enable", "true");
//        PropsUtil.getProperties().setProperty("xss.check.mode.default", "trusted.strip");
//        PropsUtil.getProperties().setProperty("xss.check.mode.file", "trusted.disallow");
    }

    private HtmlSanitizerCheck sanitizer;
    private XSSCheck xssCheck;

    protected void setUp() throws Exception {
        sanitizer = new HtmlSanitizerCheck();
//        xssCheck = new XSSCheck();
    }

    public void testBasic() {
        String sanitized = sanitizer.check("<a href=\"javascript:alert(1)\">clickme</a>");
        sanitized = sanitizer.check("<div class=\"fak_header\" style=\"font-size: 100%; height: 33px; font-family: verdana; background-image: url(http://vignette4.wikia.nocookie.net/despicableme/images/c/ca/Bob-from-the-minions-movie.jpg/revision/latest?cb=20151224154354); color: #fff; margin-top: 5px; padding-left: 10px; margin-left: 0px; line-height: 33px; padding-right: 0px; margin-right: 0px;\">Meddelelser fra holdlederen og l�rerne</div> <br>");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"http://localhost:8080/ssf/s/readFile/folderEntry/16/-/1461957717981/last/gedit-logo.png\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"{{attachmentUrl: somename.png}}\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"cid:{{attachmentUrl: somename.png}}\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"data:image/png;abcdef\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"data:image/jpeg;abcdef\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"data:image/gif;abcdef\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"data:image/svg;abcdef\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"data:text/html;abcdef\" alt=\" \" />");
        sanitized = sanitizer.check("<p> {{youtubeUrl: url=https://www.youtube.com/watch?v=HRqZhJcae3M width=425 height=344}}</p>");
        sanitized = sanitizer.check("<img src='>' onerror='alert(1)'>");
        sanitized = sanitizer.check("<p><a href=\"\tjavascript:alert(1)\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"http://www.novell.com\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"https://www.novell.com\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"mailto://www.novell.com\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"cid:{{fsakjfldsa}}\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"data:text/html;abcde\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"data:image/png;abcde\">clickme</a></p>");


        assertEquals("<a >clickme</a>", xssCheck.check("<a href=\"\tjavascript:alert(1)\">clickme</a>"));
    }
}
