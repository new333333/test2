package org.kablink.teaming.util.stringcheck;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.util.regex.Pattern;

/**
 * Created by david on 4/28/16.
 */
public class HtmlSanitizerCheck implements StringCheck {
    private static String PROTOCOL_HTTP = "^http[:].*$";
    private static String PROTOCOL_HTTPS = "^https[:].*$";
    private static String PROTOCOL_MAILTO = "^mailto[:].*$";
    private static String PROTOCOL_CID= "^cid[:].*$";
    private static String PROTOCOL_DATA_IMAGE= "^data[:]image/(jpeg|png|gif).*$";

    private static Pattern A_PATTERN = Pattern.compile(
            "(" + PROTOCOL_HTTP +
            ")|(" + PROTOCOL_HTTPS +
            ")|(" + PROTOCOL_MAILTO +
            ")|(" + PROTOCOL_CID + ")", Pattern.CASE_INSENSITIVE
    );

    private static Pattern IMG_PATTERN = Pattern.compile(
            "(" + PROTOCOL_HTTP +
            ")|(" + PROTOCOL_HTTPS +
            ")|(" + PROTOCOL_MAILTO +
            ")|(" + PROTOCOL_CID +
            ")|(" + PROTOCOL_DATA_IMAGE + ")", Pattern.CASE_INSENSITIVE
    );

    private PolicyFactory factory;

    public HtmlSanitizerCheck() {
        factory = new HtmlPolicyBuilder()
                .allowCommonBlockElements()
                .allowCommonInlineFormattingElements()
                .allowAttributes("class", "alt", "name").globally() // name attribute is added by lokesh.  This is need by few customers since they have references in long html pages.
                .allowElements("table", "tbody", "td", "tr", "hr")
                .allowElements("a", "img", "input", "span")
                .allowStandardUrlProtocols()
                .allowUrlProtocols("cid")
                // Pasted images in Firefox
                .allowUrlProtocols("data")
                .allowAttributes("href")
                    .matching(A_PATTERN)
                    .onElements("a")
                .allowAttributes("src")
                    .matching(IMG_PATTERN)
                .onElements("img")
                .allowStyling()
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
        return safeHtml;
    }

    @Override
    public String checkForQuotes(String input, boolean checkOnly) throws StringCheckException {
        return null;
    }
}
