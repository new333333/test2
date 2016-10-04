package org.kablink.teaming.util.stringcheck;

import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.CssSchema;
import java.util.Arrays;

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

    private static final Pattern A_PATTERN = Pattern.compile(
            "(" + PROTOCOL_HTTP +
            ")|(" + PROTOCOL_HTTPS +
            ")|(" + PROTOCOL_MAILTO +
            ")|(" + PROTOCOL_CID + ")", Pattern.CASE_INSENSITIVE
    );

    private static final Pattern IMG_PATTERN = Pattern.compile(
            "(" + PROTOCOL_HTTP +
            ")|(" + PROTOCOL_HTTPS +
            ")|(" + PROTOCOL_MAILTO +
            ")|(" + PROTOCOL_CID +
            ")|(" + PROTOCOL_DATA_IMAGE + ")", Pattern.CASE_INSENSITIVE
    );

    private static final String [] ALLOWED_ATTRIBUTES = new String[] {
            "align",
            "alt",
            "border",
            "cellpadding",
            "cellspacing",
            "class",
            "dir",
            "frame",
            "id",
            "lang",
            "name",
            "rules",
            "style",
            "summary"
    };
    
    /**
     * The following CSS properties do not appear in the default whitelist from OWASP, but they
     * improve the fidelity of the HTML display without unacceptable risk.
     */
    private static final CssSchema ADDITIONAL_CSS = CssSchema.withProperties(Arrays.asList(
            "float",
            "display"
    ));

    private PolicyFactory factory;

    public HtmlSanitizerCheck() {
        factory = new HtmlPolicyBuilder()
                .allowCommonBlockElements()
                .allowCommonInlineFormattingElements()
                .allowAttributes(ALLOWED_ATTRIBUTES).globally() // name and float attributes is added by lokesh.  This is need by few customers since they have references in long html pages.
                .allowElements("table", "tbody", "td", "tr", "hr")
                .allowElements("a", "img", "input", "span")
                .allowStandardUrlProtocols()
                .allowUrlProtocols("cid")
                // Pasted images in Firefox
                .allowUrlProtocols("data")
                .allowUrlsInStyles(AttributePolicy.IDENTITY_ATTRIBUTE_POLICY)
                .allowAttributes("href")
                    .matching(A_PATTERN)
                    .onElements("a")
                .allowAttributes("target")
                    .onElements("a")
                .allowAttributes("src")
                    .matching(IMG_PATTERN)
                .onElements("img")
                .allowStyling(CssSchema.union(CssSchema.DEFAULT, ADDITIONAL_CSS))
                //.requireRelNofollowOnLinks() // Disabled by Lokesh.  Follow links is needed by few customers since they have references in long html pages.
                .toFactory();
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
