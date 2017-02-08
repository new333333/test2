package org.kablink.teaming.util.stringcheck;

import org.kablink.teaming.util.SPropsUtil;
import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.CssSchema;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by david on 4/28/16.
 */
public class HtmlSanitizerCheck implements StringCheck {
    private static final String PROTOCOL_HTTP = "^http[:].*$";
    private static final String PROTOCOL_HTTPS = "^https[:].*$";
    private static final String PROTOCOL_MAILTO = "^mailto[:].*$";
    private static final String PROTOCOL_CID= "^cid[:].*$";
    private static final String PROTOCOL_DATA_IMAGE= "^data[:]image/(jpeg|png|gif).*$";
    private static final String PROTOCOL_FRAGMENT = "^#.*$";

    private static final Pattern A_PATTERN = Pattern.compile(
            "(" + PROTOCOL_HTTP +
            ")|(" + PROTOCOL_HTTPS +
            ")|(" + PROTOCOL_MAILTO +
            ")|(" + PROTOCOL_CID +
            ")|(" + PROTOCOL_FRAGMENT +
            ")", Pattern.CASE_INSENSITIVE
    );

    private static final Pattern IMG_PATTERN = Pattern.compile(
            "(" + PROTOCOL_HTTP +
            ")|(" + PROTOCOL_HTTPS +
            ")|(" + PROTOCOL_MAILTO +
            ")|(" + PROTOCOL_CID +
            ")|(" + PROTOCOL_DATA_IMAGE + ")", Pattern.CASE_INSENSITIVE
    );

    private static final String [] DEFAULT_ALLOWED_ELEMENTS = new String[] {
            "abbr",
            "area",
            "b",
            "big",
            "blockquote",
            "br",
            "caption",
            "center",
            "cite",
            "code",
            "dd",
            "del",
            "dfn",
            "dir",
            "div",
            "dl",
            "dt",
            "em",
            "figcaption",
            "figure",
            "font",
            "h1",
            "h2",
            "h3",
            "h4",
            "h5",
            "h6",
            "hr",
            "i",
            "input",
            "ins",
            "kbd",
            "li",
            "map",
            "o",
            "ol",
            "p",
            "pre",
            "q",
            "s",
            "samp",
            "small",
            "span",
            "strike",
            "strong",
            "sub",
            "sup",
            "table",
            "tbody",
            "td",
            "textarea",
            "tfoot",
            "th",
            "thead",
            "tr",
            "tt",
            "u",
            "ul",
            "var",
    };

    private static final String [] DEFAULT_ALLOWED_ATTRIBUTES = new String[] {
            "abbr",
            "acronym",
            "align",
            "alt",
            "axis",
            "bgcolor",
            "border",
            "cellpadding",
            "cellspacing",
            "char",
            "charoff",
            "class",
            "color",
            "colspan",
            "compact",
            "coords",
            "dir",
            "face",
            "frame",
            "halign",
            "headers",
            "height",
            "hspace",
            "id",
            "lang",
            "longdesc",
            "name",
            "nowrap",
            "rel",
            "rowspan",
            "rules",
            "scope",
            "size",
            "sortable",
            "sorted",
            "style",
            "summary",
            "target",
            "title",
            "type",
            "usemap",
            "valign",
            "vspace",
            "width",

            "data-mce-href",
            "data-mce-src",
            "data-mce-style",
    };
    
    /**
     * The following CSS properties do not appear in the default whitelist from OWASP, but they
     * improve the fidelity of the HTML display without unacceptable risk.
     */
    private static final CssSchema ADDITIONAL_CSS = CssSchema.withProperties(Arrays.asList(
            "display",
            "float",
            "z-index"
    ));

    private PolicyFactory factory;

    public HtmlSanitizerCheck() {
        factory = new HtmlPolicyBuilder()
                .allowCommonBlockElements()
                .allowCommonInlineFormattingElements()
                .allowAttributes(getAllowedAttributes()).globally() // name and float attributes is added by lokesh.  This is need by few customers since they have references in long html pages.
                .allowElements(getAllowedElements())
                .allowElements("a", "img")
                .allowStandardUrlProtocols()
                .allowUrlProtocols("cid")
                // Pasted images in Firefox
                .allowUrlProtocols("data")
                .allowUrlsInStyles(AttributePolicy.IDENTITY_ATTRIBUTE_POLICY)
                .allowAttributes("href")
                    .matching(A_PATTERN)
                    .onElements("a", "area")
                .allowAttributes("src")
                    .matching(IMG_PATTERN)
                    .onElements("img")
                .allowStyling(getAllowedCSSSchema())
                //.requireRelNofollowOnLinks() // Disabled by Lokesh.  Follow links is needed by few customers since they have references in long html pages.
                .toFactory();
    }

    private String[] getAllowedElements() {
        Set<String> allElements = new HashSet<>();
        allElements.addAll(Arrays.asList(DEFAULT_ALLOWED_ELEMENTS));
        allElements.addAll(Arrays.asList(SPropsUtil.getStringArray("html.safe.elements", ",")));
        return allElements.toArray(new String[allElements.size()]);
    }

    private String[] getAllowedAttributes() {
        Set<String> allElements = new HashSet<>();
        allElements.addAll(Arrays.asList(DEFAULT_ALLOWED_ATTRIBUTES));
        allElements.addAll(Arrays.asList(SPropsUtil.getStringArray("html.safe.attributes", ",")));
        return allElements.toArray(new String[allElements.size()]);
    }

    private CssSchema getAllowedCSSSchema() {
        CssSchema customSchema = CssSchema.withProperties(Arrays.asList(SPropsUtil.getStringArray("css.safe.properties", ",")));
        return CssSchema.union(CssSchema.DEFAULT, ADDITIONAL_CSS, customSchema);
    }

    @Override
    public String check(String input) throws StringCheckException {
        return check(input, false);
    }

    @Override
    public String check(String input, boolean checkOnly) throws StringCheckException {
            String safeHtml = factory.sanitize(input);
        if (checkOnly && !safeHtml.equals(input)) {
            throw new XSSCheckException();
        }
        return safeHtml.replace("{<!-- -->{", "{{");
    }

    @Override
    public String checkForQuotes(String input, boolean checkOnly) throws StringCheckException {
        return null;
    }
}
