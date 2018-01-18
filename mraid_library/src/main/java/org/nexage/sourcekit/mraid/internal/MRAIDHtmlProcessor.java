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
        int bodyTagIdx = rawHtml.indexOf("<body");

        String ls = System.getProperty("line.separator");

        if(!hasHtmlTag){
            if(bodyTagIdx == -1) {
                processedHtml.insert(0, "<body><div align='center'>" + ls);
                processedHtml.append("</div></body>");
            }
            if(!hasHeadTag) {
                processedHtml.insert(0,"<head>" + ls + "</head>" + ls);
            }
            // In the !hasBody && hasHead case, we end up with <body><head></head></body>
            // but that was a terrible case to begin with

            processedHtml.insert(0, "<html>" + ls);
            processedHtml.append(ls + "</html>");
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

        bodyTagIdx = processedHtml.indexOf("<body");
        if (bodyTagIdx != -1) {
            regex = "<body[^>]*>";
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(processedHtml);
            if (matcher.find(0)) {
                processedHtml.insert(matcher.end(), "<script>" + JS_WINDOW_LOAD_CODE + "</script>");
            }

        }

        return processedHtml.toString();
    }

    final static String JS_WINDOW_LOAD_CODE =
      "(function() {\n"
    + "  var wasIaLoadFinishedNotified = false;\n"
    + "  var IA_AD_FINISHED_LOADING_EVENT = 'iaadfinishedloading';\n"
    + "  var NOTIFY_LOADING_FINISHED_TIMEOUT_IN_MS = 5000;\n"
    + "  var SUCCESS_STATE = 'success';\n"
    + "  var FAILURE_STATE = 'failure';\n"
    + "  var iaNotifyLoadFinished = function(state) {\n"
    + "    if (!wasIaLoadFinishedNotified) {\n"
    + "      wasIaLoadFinishedNotified = true;\n"
    + "      window.location.href = IA_AD_FINISHED_LOADING_EVENT + '://' + state;\n"
    + "    }\n"
    + "  }\n"
    + "  var prevOnload = window.onload;\n"
    + "  window.onload = function() {\n"
    + "    if (typeof prevOnload === 'function') {\n"
    + "      prevOnload.apply();\n"
    + "    }\n"
    + "    iaNotifyLoadFinished.apply(null, [SUCCESS_STATE]);\n"
    + "  };\n"
    + "  setTimeout(function() {\n"
    + "    iaNotifyLoadFinished.apply(null, [FAILURE_STATE]);\n"
    + "  }, NOTIFY_LOADING_FINISHED_TIMEOUT_IN_MS);\n"
    + "})();";


}
