package com.weareethic.community.jopengraph;

import com.weareethic.community.jopengraph.exception.NullDocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A Java Object that retrieves Open Graph and other metadata, storing the result in an OpenGraphData object
 * for retrieval.
 * Uses the Jsoup library for web-scraping/data parsing.
 * Permits various options to be set for establishing a connection, e.g. setting a user-agent or ignoring SSL certification.
 *
 * @author Aleks Itskovich
 */
public class JOpenGraph {

    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36";

    private static final String DEFAULT_REFERRER = "http://www.google.com";

    private static final int DEFAULT_TIMEOUT = 30 * 1000;  // 30 seconds

    private static final boolean DEFAULT_IGNORE_HTTP_ERRORS_STATUS = false;

    private static final boolean DEFAULT_FOLLOW_REDIRECTS_STATUS = true;

    private static final boolean DEFAULT_SSL_VALIDATION_STATUS = true;

    private static final boolean DEFAULT_IGNORE_CONTENT_TYPE_STATUS = true;

    private static final String[] PREFIXES = new String[]{"og:", "music:", "video:", "article:", "book:", "profile:", "twitter:"};

    /**
     * The user-agent header to be used in requests made by this object
     */
    private String userAgent;

    /**
     * The referrer header to be used in requests made by this object
     */
    private String referrer;

    /**
     * The timeout value (in milliseconds) to be used in requests made by this object
     */
    private int timeout;

    /**
     * Configures whether or not requests made by this object will ignore the media content type of the web page
     */
    private boolean ignoreContentType;

    /**
     * Configures whether or not requests made by this object will ignore http errors
     */
    private boolean ignoreHttpErrors;

    /**
     * Configures whether or not requests made by this object will ignore http errors
     */
    private boolean followRedirects;

    /**
     * Configures whether or not requests made by this object will validate TLS/SSL certificates
     */
    private boolean validateTLSCertificates;

    /**
     * Default constructor. Sets the following default values:
     * <p>
     * userAgent = Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36
     * referrer = http://www.google.com
     * timeout = 30000
     * ignoreContentType = true;
     * ignoreHttpErrors = false;
     * followRedirects = true;
     * validateTLSCertificates = false;
     */
    public JOpenGraph() {
        this.userAgent = DEFAULT_USER_AGENT;
        this.referrer = DEFAULT_REFERRER;
        this.timeout = DEFAULT_TIMEOUT;
        this.ignoreContentType = DEFAULT_IGNORE_CONTENT_TYPE_STATUS;
        this.ignoreHttpErrors = DEFAULT_IGNORE_HTTP_ERRORS_STATUS;
        this.followRedirects = DEFAULT_FOLLOW_REDIRECTS_STATUS;
        this.validateTLSCertificates = DEFAULT_SSL_VALIDATION_STATUS;
    }

    /**
     * @param ignoreContentType       if true sets the connection to ignore the media type of the resource (true by default)
     * @param ignoreHttpErrors        if true sets the connection to not throw exceptions when an HTTP error occurs (false by default)
     * @param followRedirects         if true sets the connection to follow server redirects (true by default)
     * @param validateTLSCertificates if false, sets the connection to ignore TLS/SSL certificates (true by default); if set to false this resolves SSLHandshake exception s
     */
    public JOpenGraph(boolean ignoreContentType, boolean ignoreHttpErrors, boolean followRedirects, boolean validateTLSCertificates) {
        this.userAgent = DEFAULT_USER_AGENT;
        this.referrer = DEFAULT_REFERRER;
        this.timeout = DEFAULT_TIMEOUT;
        this.ignoreContentType = ignoreContentType;
        this.ignoreHttpErrors = ignoreHttpErrors;
        this.followRedirects = followRedirects;
        this.validateTLSCertificates = validateTLSCertificates;
    }

    /**
     * @param userAgent               sets the http request user-agent header
     * @param referrer                sets the http request referrer header
     * @param timeout                 sets the http request connection timeout (default is 30 seconds); a timeout of 0 corresponds to an infinite timeout
     * @param ignoreContentType       if true sets the connection to ignore the media type of the resource (true by default)
     * @param ignoreHttpErrors        if true sets the connection to not throw exceptions when an HTTP error occurs (false by default)
     * @param followRedirects         if true sets the connection to follow server redirects (true by default)
     * @param validateTLSCertificates if false, sets the connection to ignore TLS/SSL certificates (true by default); if set to false this resolves SSLHandshake exception s
     */
    public JOpenGraph(String userAgent, String referrer, int timeout, boolean ignoreContentType, boolean ignoreHttpErrors, boolean followRedirects, boolean validateTLSCertificates) {
        this.userAgent = userAgent;
        this.referrer = referrer;
        this.timeout = timeout;
        this.ignoreContentType = ignoreContentType;
        this.ignoreHttpErrors = ignoreHttpErrors;
        this.followRedirects = followRedirects;
        this.validateTLSCertificates = validateTLSCertificates;
    }

    /**
     * Establishes a connection using the passed URL and if successful, retrieves available Open Graph and
     * other metadata.
     *
     * @param URL the URL address of the website from which Open Graph and other meta data is to be obtained
     * @return OpenGraphData object storing the meta information retrieved from the passed URL
     * @throws IOException           occurs if Jsoup is unable to make a connection to the requested URL due to network errors
     * @throws NullDocumentException occurs if the Document object returned after connection is null
     */
    public OpenGraphData getGraph(String URL) throws IOException, NullDocumentException {

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
            throw new NullDocumentException("Jsoup Document was null");
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

                List<String> content = metaContents.putIfAbsent(key, new LinkedList<>(Arrays.asList(val)));
                if (content != null) {
                    content.add(val);
                }
            }
        });

        // if certain Open Graph or Twitter properties missing, attempt to find alternatives
        handleFallbacksForCertainFields(document, metaContents);
        fetchFavicon(document, metaContents);

        return metaContents;
    }


    /**
     * If certain Open Graph or Twitter properties are missing, attempt to find reasonable alternatives;
     * currently supporting: title, description, url and a page image.
     */
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
            getMetaDataHelper(metaContents, "image", () -> document.select("img[src~=.*\\.(png|jpg|jpeg)]").attr("src"));
        }
    }

    private void fetchFavicon(Document document, Map<String, List<String>> metaContents) {
        getMetaDataHelper(metaContents, "favicon", () -> document.select("link[href~=.*\\.(ico|png)]").attr("href"));
    }

    private void getMetaDataHelper(Map<String, List<String>> metaContents, String key, Supplier<String> supplier) {
        String value = supplier.get();
        if (value != null && value.length() != 0) {
            metaContents.put(key, Collections.singletonList(value));
        }
    }

    /**
     * @return the user-agent header currently configured for the JOpenGraph object
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * @return the referrer header currently configured for the JOpenGraph object
     */
    public String getReferrer() {
        return referrer;
    }

    /**
     * @return the timeout length currently configured for the JOpenGraph object
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @return true if the media content type is configured to be ignored by this JOpenGraphObject, false otherwise
     */
    public boolean isIgnoreContentType() {
        return ignoreContentType;
    }

    /**
     * @return true if http errors are configured to being ignored by this JOpenGraphObject, false otherwise
     */
    public boolean isIgnoreHttpErrors() {
        return ignoreHttpErrors;
    }

    /**
     * @return true if server redirects are followed by this JOpenGraph object, false otherwise
     */
    public boolean isFollowRedirects() {
        return followRedirects;
    }

    /**
     * @return true if TLS/SSL certificates are being validated by this JOpenGraph object, false otherwise
     */
    public boolean isValidateTLSCertificates() {
        return validateTLSCertificates;
    }

    /**
     * @param userAgent sets the user-agent header to be used by this JOpenGraph object in establishing connections
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * @param referrer sets the referred header to be used by this JOpenGraph object in establishing connections
     */
    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    /**
     * @param timeout sets the request timeout length to be used by this JOpenGraph object when attempting to establish a connection; a value of 0 corresponds to an infinite timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @param ignoreContentType sets whether this JOpenGraph object will ignore media content type when establishing a connection
     */
    public void setIgnoreContentType(boolean ignoreContentType) {
        this.ignoreContentType = ignoreContentType;
    }

    /**
     * @param ignoreHttpErrors sets whether this JOpenGraph object will ignore http errors when establishing a connection
     */
    public void setIgnoreHttpErrors(boolean ignoreHttpErrors) {
        this.ignoreHttpErrors = ignoreHttpErrors;
    }

    /**
     * @param followRedirects sets whether this JOpenGraph object will follow server redirects when establishing a connection
     */
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    /**
     * @param validateTLSCertificates sets whether this JOpenGraph object will validate TLS/SSL certificates when establishing a connection
     */
    public void setValidateTLSCertificates(boolean validateTLSCertificates) {
        this.validateTLSCertificates = validateTLSCertificates;
    }
}
