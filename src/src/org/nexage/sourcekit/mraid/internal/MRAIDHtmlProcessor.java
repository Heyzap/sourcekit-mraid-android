package org.nexage.sourcekit.mraid.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MRAIDHtmlProcessor {

    public static String processRawHtml(String rawHtml) {
        StringBuffer processedHtml = new StringBuffer(rawHtml);

        // Remove the mraid.js script tag.
        // We expect the tag to look like this:
        // <script src='mraid.js'></script>
        // But we should also be to handle additional attributes and whitespace
        // like this:
        // <script type = 'text/javascript' src = 'mraid.js' > </script>

        // Remove the mraid.js script tag.
        String regex = "<script\\s+[^>]*\\bsrc\\s*=\\s*([\\\"\\\'])mraid\\.js\\1[^>]*>\\s*</script>\\n*";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(processedHtml);
//        if (matcher.find()) {
//            processedHtml.delete(matcher.start(), matcher.end());
//        }

        // Add html, head, and/or body tags as needed.
        boolean hasHtmlTag = rawHtml.contains("<html");
        boolean hasHeadTag = rawHtml.contains("<head");
        boolean hasBodyTag = rawHtml.contains("<body");

        // basic sanity checks
        // if the html has no <html> tag but has a head or body it's invalid
        // if the html has an <html> tag but no <body> tag it's invalid
        if ((!hasHtmlTag && (hasHeadTag || hasBodyTag)) || (hasHtmlTag && !hasBodyTag)) {
            return null;
        }

        String ls = System.getProperty("line.separator");

        if (!hasHtmlTag) {
            processedHtml.insert(0, "<html>" + ls + "<head>" + ls + "</head>" + ls + "<body><div align='center'>" + ls);
            processedHtml.append("</div></body>" + ls + "</html>");
        } else if (!hasHeadTag) {
            // html tag exists, head tag doesn't, so add it
            regex = "<html[^>]*>";
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(processedHtml);
            int idx = 0;
            while (matcher.find(idx)) {
                processedHtml.insert(matcher.end(), ls + "<head>" + ls + "</head>");
                idx = matcher.end();
            }
        }

        // Add meta and style tags to head tag.
        regex = "<head[^>]*>";
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(processedHtml);
        int idx = 0;
        if (matcher.find(idx)) {
            String metaTag = "<meta name='viewport' content='width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no' />";
            String styleTag =
                    "<style>" + ls +
                            "body { margin:0; padding:0;}" + ls +
                            "*:not(input) { -webkit-touch-callout:none; -webkit-user-select:none; -webkit-text-size-adjust:none; }" + ls +
                            "</style>";
            processedHtml.insert(matcher.end(), ls + metaTag + ls + styleTag);
        }

        return processedHtml.toString();
    }

}
