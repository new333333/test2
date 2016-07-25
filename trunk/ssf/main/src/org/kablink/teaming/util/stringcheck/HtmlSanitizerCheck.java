package org.kablink.teaming.util.stringcheck;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.util.regex.Pattern;

/**
 * Created by david on 4/28/16.
 */
public class HtmlSanitizerCheck implements StringCheck {
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
                .allowAttributes("href")
                    //.matching(Pattern.compile("(^http:\\/\\/)|(^https:\\/\\/)|(^\\{\\{.*\\}\\}$)"))
                    .onElements("a")
                .allowAttributes("src")
                    //.matching(Pattern.compile("(^http:\\/\\/)|(^https:\\/\\/)|(^\\{\\{.*\\}\\}$)"))
                    //.matching(Pattern.compile("^\\{\\{.*\\}\\}$"))
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
