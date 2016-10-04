package org.kablink.teaming.web.util;

import junit.framework.TestCase;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.util.PropsUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 5/24/16.
 */
public class MarkupUtilTest extends TestCase {

    static {
        PropsUtil.getProperties().setProperty("temp.dir", System.getProperty("java.io.tmpdir"));
    }

    @Override
    protected void setUp() throws Exception {
        RequestContextHolder.setRequestContext(new RequestContext("zone", 1L, null));
    }

    public void testReplaceUrlsForUpload() throws IOException {
        String input = "<p>Hi!</p>\n" +
                "<p><img class=\" ss_addimage \" style=\"width: 300px; height: 154px;\" src=\"http://localhost:8080/ssf/s/viewFile?viewType=ss_viewUploadFile&amp;fileId=14-gedit-logo.png_7285406363672126489.tmp\" alt=\" \" width=\"300\" height=\"154\">\n" +
                "</p>\n" +
                "<p> </p>\n" +
                "<p><img class=\" ss_addimage \" style=\"width: 128px; height: 128px;\" src=\"http://localhost:8080/ssf/s/viewFile?viewType=ss_viewUploadFile&amp;fileId=23-blending_knife_prev.png_1245878562889399901.tmp\" alt=\" \" width=\"128\" height=\"128\">\n" +
                "</p>";
        String expected = "<p>Hi!</p>\n" +
                "<p><img class=\" ss_addimage \" style=\"width: 300px; height: 154px;\" " +
                "src=\"cid:{{attachmentUrl: gedit-logo.png}}\" alt=\" \" width=\"300\" height=\"154\">\n" +
                "</p>\n" +
                "<p> </p>\n" +
                "<p><img class=\" ss_addimage \" style=\"width: 128px; height: 128px;\" " +
                "src=\"cid:{{attachmentUrl: blending_knife_prev.png}}\" alt=\" \" width=\"128\" height=\"128\">\n" +
                "</p>";
        List fileData = new ArrayList();
        String output = MarkupUtil.replaceAttachmentUrlMacroForUploadFiles(input, "description", fileData);
        assertEquals(expected, output);
        assertEquals(2, fileData.size());
    }

    public void testReplaceYoutubeUrl() {
        String input = "<p> {{youtubeUrl: url&#61;https://www.youtube.com/watch?v&#61;HRqZhJcae3M width&#61;425 height&#61;344}}</p>";

        StringBuffer result = MarkupUtil.markupReplaceYouTubeUrlReference(new StringBuffer(input), MarkupUtil.youtubeUrlPattern, new MarkupUtil.UrlBuilder() {
            @Override
            public String getFileUrlByName(String fileName) {
                return null;
            }

            @Override
            public String getFileUrlById(String fileId) {
                return null;
            }

            @Override
            public String getRelativeTitleUrl(String normalizedTitle, String title, Boolean isMobile) {
                return null;
            }

            @Override
            public String getTitleUrl(String binderId, String zoneUUID, String normalizedTitle, String title, Boolean isMobile) {
                return null;
            }

            @Override
            public String getRootUrl() {
                return null;
            }

            @Override
            public String getImagesRootUrl() {
                return null;
            }

            @Override
            public String getRootServletUrl() {
                return null;
            }

            @Override
            public String getVibeFunctionResult(String functionText) {
                return null;
            }
        }, "", false);
    }
}
