package org.kablink.teaming.web.util;

import junit.framework.TestCase;
import org.junit.Assert;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.util.stringcheck.HtmlSanitizerCheck;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.util.PropsUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 5/24/16.
 */
public class MarkupUtilTest extends TestCase {

    static class MockUrlBuilder implements MarkupUtil.UrlBuilder {
        @Override
        public String getFileUrlById(String fileId) {
            return "[fileId: " + fileId + "]";
        }

        @Override
        public String getFileUrlByName(String fileName) {
            return "[fileName: " + fileName + "]";
        }

        @Override
        public String getRelativeTitleUrl(String normalizedTitle, String title, Boolean isMobile) {
            return null;
        }

        @Override
        public String getTitleUrl(String binderId, String zoneUUID, String normalizedTitle, String title, Boolean isMobile) {
            return "[title: " + title + "/" + normalizedTitle + "]";
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
            return "John Doe";
        }
    }

    static {
        PropsUtil.getProperties().setProperty("temp.dir", System.getProperty("java.io.tmpdir"));
    }

    @Override
    protected void setUp() throws Exception {
        RequestContextHolder.setRequestContext(new RequestContext("zone", 1L, null));
    }

    public void testSectionMarkupReplacement() {
        String input = "Sections Test<br/>" +
                "==First==<br/>" +
                "===Sub1===<br/>" +
                "Test One<br/>" +
                "==Second==<br/>" +
                "===Sub2===<br/>" +
                "Test Two<br/>";

        HtmlSanitizerCheck sanitizer = new HtmlSanitizerCheck();
        String sanitized = sanitizer.check(input);

        List sections = MarkupUtil.markupSplitBySection(sanitized);
        Assert.assertEquals(5, sections.size());
    }

    public void testSectionMarkupReplacementWithEscapedChars() {
        String input = "&lt;Hello&gt;<br/>" +
                "==First==<br/>" +
                "===Sub1===<br/>" +
                "Test One<br/>" +
                "==Second==<br/>" +
                "===Sub2===<br/>" +
                "Test Two<br/>";

        HtmlSanitizerCheck sanitizer = new HtmlSanitizerCheck();
        String sanitized = sanitizer.check(input);

        List sections = MarkupUtil.markupSplitBySection(sanitized);
        Assert.assertEquals(5, sections.size());
        Assert.assertEquals("&lt;Hello&gt;<br />", ((Map)sections.get(0)).get("prefix"));
        Assert.assertEquals("First", ((Map)sections.get(1)).get("sectionTitle"));
        Assert.assertEquals("==First==<br />", ((Map)sections.get(1)).get("sectionText"));
        Assert.assertEquals("Sub1", ((Map)sections.get(2)).get("sectionTitle"));
        Assert.assertEquals("===Sub1===<br />Test One<br />", ((Map) sections.get(2)).get("sectionText"));
        Assert.assertEquals("Second", ((Map)sections.get(3)).get("sectionTitle"));
        Assert.assertEquals("==Second==<br />", ((Map)sections.get(3)).get("sectionText"));
        Assert.assertEquals("Sub2", ((Map)sections.get(4)).get("sectionTitle"));
        Assert.assertEquals("===Sub2===<br />Test Two<br />", ((Map) sections.get(4)).get("sectionText"));

    }

    public void testMarkupStringReplacementWithAttachmentUrl() {
        String input = "<table style=\"width:455px;height:111px\" border=\"5\"><tbody><tr><td>" +
        "<h1><span style=\"font-size:xx-large;font-family:&#39;times new roman&#39; , &#39;times&#39;;color:#ff0000\">OWASP Testing</span></h1>" +
        "</td><td><img class=\" ss_addimage \" style=\"width:155px;height:60px\" src=\"cid:%7b%7battachmentUrl:%20SmallSubaru.png%7d%7d\" alt=\" \" width=\"155\" height=\"60\" />" +
        "</td></tr></tbody></table>";

        String actual = MarkupUtil.markupStringReplacement(null, null, null, null, new MockUrlBuilder(),
                "123", "workspace", input, WebKeys.MARKUP_VIEW, false);
        String expected = "<table style=\"width:455px;height:111px\" border=\"5\"><tbody><tr><td>" +
                "<h1><span style=\"font-size:xx-large;font-family:&#39;times new roman&#39; , &#39;times&#39;;color:#ff0000\">OWASP Testing</span></h1>" +
                "</td><td><img class=\" ss_addimage \" style=\"width:155px;height:60px\" src=\"[fileName: SmallSubaru.png]\" alt=\" \" width=\"155\" height=\"60\" />" +
                "</td></tr></tbody></table>";

        Assert.assertEquals(expected, actual);
    }

    public void testMarkupStringReplacementWithAttachmentUrlAndEncodedChars() {
        String input = "&lt;Hello&gt;<br/><table style=\"width:455px;height:111px\" border=\"5\"><tbody><tr><td>" +
        "<h1><span style=\"font-size:xx-large;font-family:&#39;times new roman&#39; , &#39;times&#39;;color:#ff0000\">OWASP Testing</span></h1>" +
        "</td><td><img class=\" ss_addimage \" style=\"width:155px;height:60px\" src=\"cid:%7b%7battachmentUrl:%20SmallSubaru.png%7d%7d\" alt=\" \" width=\"155\" height=\"60\" />" +
        "</td></tr></tbody></table>";

        String actual = MarkupUtil.markupStringReplacement(null, null, null, null, new MockUrlBuilder(),
                "123", "workspace", input, WebKeys.MARKUP_VIEW, false);
        String expected = "&lt;Hello&gt;<br/><table style=\"width:455px;height:111px\" border=\"5\"><tbody><tr><td>" +
                "<h1><span style=\"font-size:xx-large;font-family:&#39;times new roman&#39; , &#39;times&#39;;color:#ff0000\">OWASP Testing</span></h1>" +
                "</td><td><img class=\" ss_addimage \" style=\"width:155px;height:60px\" src=\"[fileName: SmallSubaru.png]\" alt=\" \" width=\"155\" height=\"60\" />" +
                "</td></tr></tbody></table>";

        Assert.assertEquals(expected, actual);
    }

    public void testMarkupStringReplacementWithTitle() {
        String input =
                "<p style=\"text-align:left\">" +
                "<span style=\"color:rgb( 51 , 51 , 51 )\">" +
                        "cid:{{titleUrl: binderId&#61;150 title&#61;the_title text&#61;the_text}}" +
                "</span>" +
                "<br /></p>" +
                "<pre>Preformatted</pre>";

        String actual = MarkupUtil.markupStringReplacement(null, null, null, null, new MockUrlBuilder(),
                "123", "workspace", input, WebKeys.MARKUP_VIEW, false);
        String expected =
                "<p style=\"text-align:left\">" +
                "<span style=\"color:rgb( 51 , 51 , 51 )\">" +
                        "<a href=\"[title: the_text/the_title]\" onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this, false);\">" +
                        "<span class=\"ss_title_link\">the_text</span>" +
                        "</a>" +
                "</span>" +
                "<br /></p>" +
                "<pre>Preformatted</pre>";

        Assert.assertEquals(expected, actual);
    }

    public void testMarkupStringReplacementWithTitleAndEncodedChars() {
        String input =
                "<p style=\"text-align:left\">" +
                "&lt;Hello&gt;" +
                "<span style=\"color:rgb( 51 , 51 , 51 )\">" +
                        "cid:{{titleUrl: binderId&#61;150 title&#61;the_title text&#61;the_text}}" +
                "</span>" +
                "<br /></p>" +
                "<pre>Preformatted</pre>";

        String actual = MarkupUtil.markupStringReplacement(null, null, null, null, new MockUrlBuilder(),
                "123", "workspace", input, WebKeys.MARKUP_VIEW, false);
        String expected =
                "<p style=\"text-align:left\">" +
                "&lt;Hello&gt;" +
                "<span style=\"color:rgb( 51 , 51 , 51 )\">" +
                        "<a href=\"[title: the_text/the_title]\" onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this, false);\">" +
                        "<span class=\"ss_title_link\">the_text</span>" +
                        "</a>" +
                "</span>" +
                "<br /></p>" +
                "<pre>Preformatted</pre>";

        Assert.assertEquals(expected, actual);
    }

    public void testMarkupStringReplacementWithVibeFunction() {
        String input =
                "<p>" +
                "cid:{{vibe:user}}" +
                "</p>";

        String actual = MarkupUtil.markupStringReplacement(null, null, null, null, new MockUrlBuilder(),
                "123", "workspace", input, WebKeys.MARKUP_VIEW, false);
        String expected =
                "<p>" +
                "John Doe" +
                "</p>";

        Assert.assertEquals(expected, actual);
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

        String actual = MarkupUtil.markupStringReplacement(null, null, null, null, new MockUrlBuilder(),
                "123", "workspace", input, WebKeys.MARKUP_VIEW, false);
        String expected = "<p> <div id=\"ss_videoDiv1\" class=\"ss_videoDiv\">\n" +
                "<div id=\"ytapiplayer1\">\n" +
                "</div>\n" +
                "<div>\n" +
                "<a href=\"https://www.youtube.com/watch?v=HRqZhJcae3M\"><img width=\"60\" height=\"38\" src=\"nullpics/yt_powered_by_black.png\"/></a>\n" +
                "</div>\n" +
                "</div>\n" +
                "<script type=\"text/javascript\">\n" +
                "var params = { allowScriptAccess: \"always\", wmode: \"opaque\" };\n" +
                "var atts = { id: \"myytplayer\" };\n" +
                "swfobject.embedSWF(\"https://www.youtube.com/v/HRqZhJcae3M?enablejsapi=1&playerapiid=ytplayer\", \"ytapiplayer1\", \"425\", \"344\", \"8\", null, null, params, atts);\n" +
                "//ss_createSpannedAreaObj(\"ss_videoDiv1\");\n" +
                "</script>\n" +
                "</p>";

        Assert.assertEquals(expected, actual);
    }

    public void testReplaceSectionMarkup() {
        String input = "<p>&#61;&#61;Heading&#61;&#61;</p>\n" +
                "<p>&#61;&#61;&#61;Subheading&#61;&#61;&#61;</p>\n" +
                "<p>Hey</p>\n" +
                "<p>&#61;&#61;&#61;Subheading 2&#61;&#61;&#61;</p>\n" +
                "<p>There</p>";

        String actual = MarkupUtil.markupSectionsReplacement(input);
        String expected = "<div></div>\n" +
                "<div><div><span class=\"ss_sectionHeader1\">Heading</span></div>\n" +
                "</div>\n" +
                "<div><div><span class=\"ss_sectionHeader2\">Subheading</span></div>\n" +
                "<p>Hey</p>\n" +
                "</div>\n" +
                "<div><div><span class=\"ss_sectionHeader2\">Subheading 2</span></div>\n" +
                "<p>There</p></div>";
        Assert.assertEquals(expected, actual);
    }
}
