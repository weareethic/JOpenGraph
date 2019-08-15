package com.weareethic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class JOpenGraph {

    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36";

    private static final String DEFAULT_REFERRER = "http://www.google.com";

    private static final int DEFAULT_TIMEOUT = 30 * 1000;  // 30 seconds

    private static final boolean DEFAULT_IGNORE_HTTP_ERRORS_STATUS = false;

    private static final boolean DEFAULT_FOLLOW_REDIRECTS_STATUS = true;

    private static final boolean DEFAULT_SSL_VALIDATION_STATUS = true;

    private static final boolean DEFAULT_IGNORE_CONTENT_TYPE_STATUS = true;

    private static final String[] PREFIXES = new String[]{"og:", "music:", "video:", "article:", "book:", "profile:", "twitter:"};

    private static final String[] IMG_SUFFIXES = new String[]{".png", ".jpeg", ".jpg"};

    private String userAgent;

    private String referrer;

    private int timeout;

    private boolean ignoreContentType;

    private boolean ignoreHttpErrors;

    private boolean followRedirects;

    private boolean validateTLSCertificates;

    public static void main(String[] args) throws IOException {

        JOpenGraph jOpenGraph = new JOpenGraph();
        OpenGraphData openGraphData = jOpenGraph.getGraph("https://www.imdb.com/title/tt0068646/");

        Set<String> properties = openGraphData.getAllProperties();
        System.out.println(properties);

        String title = openGraphData.getTitle();
        String description = openGraphData.getDescription();
        String type = openGraphData.getType();
        String siteName = openGraphData.getSiteName();
        String url = openGraphData.getUrl();
        List<String> images = openGraphData.getImages();

        List<String> content = openGraphData.getContent("favicon");
        String favicon = content.get(0);
        System.out.println(favicon);

        jOpenGraph.setTimeout(120 * 1000);
        jOpenGraph.setUserAgent("my custom user agent");
        jOpenGraph.setReferrer("new referrrer");
        jOpenGraph.ignoreContentType(true);
        jOpenGraph.ignoreHttpErrors(false);
        jOpenGraph.followRedirects(true);
        jOpenGraph.validateTLSCertificates(false);

    }

    public JOpenGraph() {
        userAgent = DEFAULT_USER_AGENT;
        referrer = DEFAULT_REFERRER;
        timeout = DEFAULT_TIMEOUT;
        ignoreContentType = DEFAULT_IGNORE_CONTENT_TYPE_STATUS;
        ignoreHttpErrors = DEFAULT_IGNORE_HTTP_ERRORS_STATUS;
        followRedirects = DEFAULT_FOLLOW_REDIRECTS_STATUS;
        validateTLSCertificates = DEFAULT_SSL_VALIDATION_STATUS;
    }

    public JOpenGraph(boolean ignoreContentType, boolean ignoreHttpErrors, boolean followRedirects, boolean validateTLSCertificates) {
        userAgent = DEFAULT_USER_AGENT;
        referrer = DEFAULT_REFERRER;
        timeout = DEFAULT_TIMEOUT;
        this.ignoreContentType = ignoreContentType;
        this.ignoreHttpErrors = ignoreHttpErrors;
        this.followRedirects = followRedirects;
        this.validateTLSCertificates = validateTLSCertificates;
    }

    public JOpenGraph(String userAgent, String referrer, int timeout, boolean ignoreContentType, boolean ignoreHttpErrors, boolean followRedirects, boolean validateTLSCertificates) {
        this.userAgent = userAgent;
        this.referrer = referrer;
        this.timeout = timeout;
        this.ignoreContentType = ignoreContentType;
        this.ignoreHttpErrors = ignoreHttpErrors;
        this.followRedirects = followRedirects;
        this.validateTLSCertificates = validateTLSCertificates;
    }

    public OpenGraphData getGraph(String URL) throws IOException {

        Document document = Jsoup.connect(URL)
                .userAgent(userAgent)
                .referrer(referrer)
                .timeout(timeout)
                .ignoreContentType(ignoreContentType)
                .ignoreHttpErrors(ignoreHttpErrors)
                .followRedirects(followRedirects)
                .validateTLSCertificates(validateTLSCertificates)
                .get();

        if (document == null) {
            throw new IllegalStateException("Document was null");
        }

        Map<String, List<String>> allMetaTags = fetchAllMetaData(document);

        return new OpenGraphData(allMetaTags);
    }

    private Map<String, List<String>> fetchAllMetaData(Document document) {
        Elements metaTags = document.getElementsByTag("meta");
        if (metaTags.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> metaContents = new HashMap<>();
        metaTags.forEach(metaTag -> {
            String attributeKey;
            if (metaTag.hasAttr("property")) {
                attributeKey = "property";
            } else if (metaTag.hasAttr("name")) {
                attributeKey = "name";
            } else {
                return;
            }

            if (Stream.of(PREFIXES).anyMatch(prefix -> (metaTag.attr(attributeKey).startsWith(prefix)))) {

                String key = metaTag.attr(attributeKey);
                String val;
                if (metaTag.hasAttr("content")) {
                    val = metaTag.attr("content");
                } else if (metaTag.hasAttr("value")) {
                    val = metaTag.attr("value");
                } else {
                    return;
                }

                if (metaContents.containsKey(metaTag.attr(attributeKey))) {
                    metaContents.get(key).add(val);
                } else {
                    List<String> values = new LinkedList<>();
                    values.add(val);
                    metaContents.put(key, values);
                }
            }
        });

        handleFallbacksForCertainFields(document, metaContents);
        fetchFavicon(document, metaContents);

        return metaContents;
    }

    private void handleFallbacksForCertainFields(Document document, Map<String, List<String>> metaContents) {
        if (!metaContents.containsKey("og:title") && !metaContents.containsKey("twitter:title")) {
            getMetaDataHelper(metaContents, "title", document::title);
        }
        if (!metaContents.containsKey("og:description") && !metaContents.containsKey("twitter:description")) {
            getMetaDataHelper(metaContents, "description", () -> document.select("meta[name=description]").attr("content"));
        }
        if (!metaContents.containsKey("og:url")) {
            getMetaDataHelper(metaContents, "url", () -> document.select("link[rel=canonical]").attr("href"));
        }

        if (!metaContents.containsKey("og:image") && !metaContents.containsKey("og:image:url")
                && !metaContents.containsKey("og:image:secure_url") && !metaContents.containsKey("twitter:image") && !metaContents.containsKey("twitter:image:src")) {
            Supplier<String> s = () -> document.select("img").stream()
                    .filter(img -> Stream.of(IMG_SUFFIXES).anyMatch(suffix -> img.absUrl("src").endsWith(suffix)))
                    .findFirst().map(img -> img.absUrl("src")).orElse(null);
            getMetaDataHelper(metaContents, "image", s);
        }
    }

    private void fetchFavicon(Document document, Map<String, List<String>> metaContents) {
        getMetaDataHelper(metaContents, "favicon", () -> document.select("link[href~=.*\\.(ico|png)]").attr("href"));
    }

    private void getMetaDataHelper(Map<String, List<String>> metaContents, String key, Supplier<String> supplier) {
        String value = supplier.get();
        if (value != null && value.length() != 0) {
            List<String> contents = new LinkedList<>();
            contents.add(value);
            metaContents.put(key, contents);
        }
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void ignoreContentType(boolean value) {
        this.ignoreContentType = value;
    }

    public void ignoreHttpErrors(boolean value) {
        this.ignoreHttpErrors = value;
    }

    public void followRedirects(boolean value) {
        this.followRedirects = value;
    }

    public void validateTLSCertificates(boolean value) {
        this.validateTLSCertificates = value;
    }

}
