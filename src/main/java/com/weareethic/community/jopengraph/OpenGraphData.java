package com.weareethic.community.jopengraph;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Simple class storing the retrieved meta data, indexed by the property name (e.g. og:image or twitter:url).
 * Also provides several convenience methods to access common meta tag attributes such as title or description.
 * <p>
 * Because meta tags can include multiple items under the same property attribute (e.g. multiple meta tags with the
 * name 'og:image'), all meta tag contents are stored in a list even when there is only a single item per property
 * attribute.
 *
 * @author Aleks Itskovich
 *
 * This file is copyrighted under the MIT license.
 */
public class OpenGraphData {

    /**
     * A map storing meta tag property names (e.g. og:title or twitter:url) their associated contents stored as a list.
     */
    private final Map<String, List<String>> metaData;

    /**
     * @param metaData a map of meta tag names (e.g. og:title, twitter:url, etc.) with their respective content
     */
    OpenGraphData(Map<String, List<String>> metaData) {
        this.metaData = metaData;
    }

    /**
     * @return the set of all meta tag names represented in this object (e.g. og:title, twitter:url, favicon, etc.)
     */
    public Set<String> getAllProperties() {
        return metaData.keySet();
    }

    /**
     * @param contentKey the name of the meta tag whose content should be retrieved (e.g. og:image)
     * @return a list containing any meta tag contents that correspond to the passed content key
     */
    public List<String> getContent(String contentKey) {
        List<String> content = metaData.get(contentKey);
        return content == null ? Collections.emptyList() : content;
    }

    /**
     * Convenience method used to return the title meta content for this page, if any is available
     *
     * @return Optional object containing the meta tag content for the first tag found in the following order: og:description
     * twitter:description, description
     */
    public Optional<String> getTitle() {
        String title = null;
        if (metaData.containsKey("og:title")) {
            title = metaData.get("og:title").get(0);
        } else if (metaData.containsKey("twitter:title")) {
            title = metaData.get("twitter:title").get(0);
        } else if (metaData.containsKey("title")) {
            title = metaData.get("title").get(0);
        }
        return Optional.ofNullable(title);
    }

    /**
     * Convenience method used to return the description meta content for this page, if any is available
     *
     * @return Optional object containing the meta tag content for the first tag found in the following order: og:description,
     * twitter:description, description
     */
    public Optional<String> getDescription() {
        String description = null;
        if (metaData.containsKey("og:description")) {
            description = metaData.get("og:description").get(0);
        } else if (metaData.containsKey("twitter:description")) {
            description = metaData.get("twitter:description").get(0);
        } else if (metaData.containsKey("description")) {
            description = metaData.get("description").get(0);
        }
        return Optional.ofNullable(description);
    }

    /**
     * Convenience method used to return the url meta content for this page, if any is available
     *
     * @return Optional object containing the the og:url or canonical url meta tag content (in that order) if present on the site
     */
    public Optional<String> getUrl() {
        String url = null;
        if (metaData.containsKey("og:url")) {
            url = metaData.get("og:url").get(0);
        } else if (metaData.containsKey("url")) {
            url = metaData.get("url").get(0);
        }
        return Optional.ofNullable(url);
    }

    /**
     * Convenience method used to return images meta content for this page, if any is available
     *
     * @return list of images corresponding to the tags found first in the following order: og:image,
     * og:image:url, og:image:secure_url, twitter:image, twitter:image:src, image
     */
    public List<String> getImages() {
        List<String> images = Collections.emptyList();
        if (metaData.containsKey("og:image")) {
            images = metaData.get("og:image");
        } else if (metaData.containsKey("og:image:url")) {
            images = metaData.get("og:image:url");
        } else if (metaData.containsKey("og:image:secure_url")) {
            images = metaData.get("og:image:secure_url");
        } else if (metaData.containsKey("twitter:image")) {
            images = metaData.get("twitter:image");
        } else if (metaData.containsKey("twitter:image:src")) {
            images = metaData.get("twitter:image:src");
        } else if (metaData.containsKey("image")) {
            images = metaData.get("image");
        }
        return images;
    }

    /**
     * Convenience method used to return the Open Graph content type meta content for this page, if any is available
     *
     * @return Optional object containing the og:type meta tag content if present on the site
     */
    public Optional<String> getType() {
        return metaData.containsKey("og:type") ? Optional.of(metaData.get("og:type").get(0)) : Optional.empty();
    }

    /**
     * Convenience method used to return the site name meta content for this page, if any is available
     *
     * @return Optional object containing the og:site_name or twitter:site meta tag content (in that order) if present on the site
     */
    public Optional<String> getSiteName() {
        String siteName = null;
        if (metaData.containsKey("og:site_name")) {
            siteName = metaData.get("og:site_name").get(0);
        } else if (metaData.containsKey("twitter:site")) {
            siteName = metaData.get("twitter:site").get(0);
        }
        return Optional.ofNullable(siteName);
    }

}
