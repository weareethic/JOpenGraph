package com.weareethic;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OpenGraphData {

    private final Map<String, List<String>> metaData;

    OpenGraphData(Map<String, List<String>> metaData) {
        this.metaData = metaData;
    }

    public Set<String> getAllProperties() {
        return metaData.keySet();
    }

    public boolean hasContent(String contentKey) {
        return metaData.containsKey(contentKey);
    }

    public List<String> getContent(String contentKey) {
        return metaData.get(contentKey);
    }

    public boolean hasTitle() {
        return metaData.containsKey("og:title") ||
                metaData.containsKey("twitter:title") ||
                metaData.containsKey("title");
    }

    public String getTitle() {
        String title = null;
        if (metaData.containsKey("og:title")) {
            title = metaData.get("og:title").get(0);
        } else if (metaData.containsKey("twitter:title")) {
            title = metaData.get("twitter:title").get(0);
        } else if (metaData.containsKey("title")) {
            title = metaData.get("title").get(0);
        }
        return title;
    }

    public boolean hasDescription() {
        return metaData.containsKey("og:description") ||
                metaData.containsKey("twitter:description") ||
                metaData.containsKey("description");
    }

    public String getDescription() {
        String description = null;
        if (metaData.containsKey("og:description")) {
            description = metaData.get("og:description").get(0);
        } else if (metaData.containsKey("twitter:description")) {
            description = metaData.get("twitter:description").get(0);
        } else if (metaData.containsKey("description")) {
            description = metaData.get("description").get(0);
        }
        return description;
    }

    public boolean hasUrl() {
        return metaData.containsKey("og:url") ||
                metaData.containsKey("url");
    }

    public String getUrl() {
        String url = null;
        if (metaData.containsKey("og:url")) {
            url = metaData.get("og:url").get(0);
        } else if (metaData.containsKey("url")) {
            url = metaData.get("url").get(0);
        }
        return url;
    }

    public boolean hasImages() {
        return metaData.containsKey("og:image") ||
                metaData.containsKey("og:image:url") ||
                metaData.containsKey("og:image:secure_url") ||
                metaData.containsKey("twitter:image") ||
                metaData.containsKey("image");
    }

    public List<String> getImages() {
        List<String> images = Collections.emptyList();
        if (metaData.containsKey("og:image")) {
            images = metaData.get("og:image");
        } else if (metaData.containsKey("og:image:url")) {
            images = metaData.get("og:image:url");
        }  else if (metaData.containsKey("og:image:secure_url")) {
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

    public boolean hasType() {
        return metaData.containsKey("og:type");
    }

    public String getType() {
        return metaData.containsKey("og:type") ? metaData.get("og:type").get(0) : null;
    }

    public boolean hasSiteName() {
        return metaData.containsKey("og:site_name");
    }

    public String getSiteName() {
        return metaData.containsKey("og:site_name") ? metaData.get("og:site_name").get(0) : null;
    }

}
