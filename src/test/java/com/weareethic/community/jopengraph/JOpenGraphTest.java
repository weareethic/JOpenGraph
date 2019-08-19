package com.weareethic.community.jopengraph;

import com.weareethic.community.jopengraph.exception.NullDocumentException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JOpenGraphTest {

    private JOpenGraph jOpenGraph;

    private OpenGraphData openGraphArticleData;

    private ArticleData expectedArticleData;

    private ProductData expectedProductData;

    private ImageDataZoo imageDataZoo;

    @Before
    public void createJOpenGraph() throws IOException, NullDocumentException {
        this.jOpenGraph = new JOpenGraph();
        this.openGraphArticleData = jOpenGraph.getGraph("https://www.nytimes.com/2019/08/14/business/german-economy.html?action=click&module=Top%20Stories&pgtype=Homepage");
        this.expectedArticleData = new ArticleData();
        this.expectedProductData = new ProductData();
        this.imageDataZoo = new ImageDataZoo();
    }

    @Test
    public void testGetAllProperties() {
        openGraphArticleData.getAllProperties();
        assertEquals(20, openGraphArticleData.getAllProperties().size());
    }

    @Test
    public void testGetTitle() {
        assertEquals(expectedArticleData.getTitle(), openGraphArticleData.getTitle().orElse(""));

        Map<String, List<String>> mockMetaContents = new HashMap<>();
        List<String> titleTest = Collections.singletonList("test_twitter_title");
        mockMetaContents.put("twitter:title", titleTest);
        OpenGraphData testOpenGraphData = new OpenGraphData(mockMetaContents);
        assertEquals(titleTest.get(0), testOpenGraphData.getTitle().orElse(""));
    }

    @Test
    public void testGetDescription() {
        assertEquals(expectedArticleData.getDescription(), openGraphArticleData.getDescription().orElse(""));

        Map<String, List<String>> mockMetaContents = new HashMap<>();
        List<String> descriptionTest = Collections.singletonList("test_twitter_description");
        mockMetaContents.put("twitter:description", descriptionTest);
        OpenGraphData testOpenGraphData = new OpenGraphData(mockMetaContents);
        assertEquals(descriptionTest.get(0), testOpenGraphData.getDescription().orElse(""));
    }

    @Test
    public void testGetType() {
        assertEquals(expectedArticleData.getType(), openGraphArticleData.getType().orElse(""));
    }

    @Test
    public void testGetUrl() {
        assertEquals(expectedArticleData.getUrl(), openGraphArticleData.getUrl().orElse(""));

        Map<String, List<String>> mockMetaContents = new HashMap<>();
        List<String> descriptionTest = Collections.singletonList("test_twitter_description");
        mockMetaContents.put("twitter:description", descriptionTest);
        OpenGraphData testOpenGraphData = new OpenGraphData(mockMetaContents);
        assertEquals(descriptionTest.get(0), testOpenGraphData.getDescription().orElse(""));
    }

    @Test
    public void testGetSiteName() {
        assertEquals(expectedArticleData.getSiteName(), openGraphArticleData.getSiteName().orElse(""));
    }

    @Test
    public void testGetImages() {
        assertEquals(expectedArticleData.getImages(), openGraphArticleData.getImages());

        OpenGraphData imageTestOpenGraphData;
        Map<String, List<String>> mockMetaTags = new HashMap<>();
        mockMetaTags.put("og:image:url", imageDataZoo.getImageUrl());
        mockMetaTags.putIfAbsent("og:image:secure_url", imageDataZoo.getSecureImageUrl());
        mockMetaTags.put("twitter:image", imageDataZoo.getTwitterImage());
        mockMetaTags.put("twitter:image:src", imageDataZoo.getTwitterImageSrc());

        imageTestOpenGraphData = new OpenGraphData(mockMetaTags);
        assertEquals(imageDataZoo.getImageUrl(), imageTestOpenGraphData.getImages());
        mockMetaTags.remove("og:image:url");

        imageTestOpenGraphData = new OpenGraphData(mockMetaTags);
        assertEquals(imageDataZoo.getSecureImageUrl(), imageTestOpenGraphData.getImages());
        mockMetaTags.remove("og:image:secure_url");

        imageTestOpenGraphData = new OpenGraphData(mockMetaTags);
        assertEquals(imageDataZoo.getTwitterImage(), imageTestOpenGraphData.getImages());
        mockMetaTags.remove("twitter:image");

        imageTestOpenGraphData = new OpenGraphData(mockMetaTags);
        assertEquals(imageDataZoo.getTwitterImageSrc(), imageTestOpenGraphData.getImages());
    }

    @Test
    public void testGetContent() {
        assertEquals(expectedArticleData.getArticleTags(), openGraphArticleData.getContent("article:tag"));
    }

    @Test
    public void testFallbackMethods() throws IOException, NullDocumentException {
        OpenGraphData openGraphData = jOpenGraph.getGraph("https://www.amazon.com/Best-Sellers-Health-Personal-Care-Sleep-Masks/zgbs/hpc/3764231");
        assertEquals(expectedProductData.getTitle(), openGraphData.getTitle().orElse(""));
        assertEquals(expectedProductData.getDescription(), openGraphData.getDescription().orElse(""));
        assertEquals(expectedProductData.getImages(), openGraphData.getImages());
        assertTrue(openGraphData.getUrl().orElse("").startsWith(expectedProductData.getUrl()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetGraphInvalidUrl() throws IOException, NullDocumentException {
        jOpenGraph.getGraph("");
    }

    @Test
    public void testPartialArgsConstructor() {
        JOpenGraph testOpenGraph = new JOpenGraph(true, false, true, false);
        assertTrue(testOpenGraph.isIgnoreContentType());
        assertFalse(testOpenGraph.isIgnoreHttpErrors());
        assertTrue(testOpenGraph.isFollowRedirects());
        assertFalse(testOpenGraph.isValidateTLSCertificates());
    }

    @Test
    public void testAllArgsConstructor() {
        JOpenGraph testOpenGraph = new JOpenGraph("testAgent", "http://www.google.com", 120 * 1000, true, false, true, true);
        assertEquals("testAgent", testOpenGraph.getUserAgent());
        assertEquals("http://www.google.com", testOpenGraph.getReferrer());
        assertEquals(120000, testOpenGraph.getTimeout());
        assertTrue(testOpenGraph.isIgnoreContentType());
        assertFalse(testOpenGraph.isIgnoreHttpErrors());
        assertTrue(testOpenGraph.isFollowRedirects());
        assertTrue(testOpenGraph.isValidateTLSCertificates());
    }

    public class ArticleData {
        private final int numProperties = 20;

        private final String title = "Germany Nears Recession and Chinese Factories Slow in Trade War Fallout";

        private final String url = "https://www.nytimes.com/2019/08/14/business/german-economy.html";

        private final String type = "article";

        private final String description = "The German economy shrank in the second quarter and may be headed toward recession. Growth of Chinese factories was the slowest in 17 years.";

        private final List<String> images = Collections.singletonList("https://static01.nyt.com/images/2019/08/14/business/14germanecon1-promo/14germanecon1-promo-facebookJumbo-v3.jpg");

        private final List<String> articleTags = new ArrayList() {{
            add("Gross Domestic Product");
            add("International Trade and World Market");
            add("Merkel, Angela");
            add("Trump, Donald J");
            add("Germany");
            add("China");
            add("United States");
        }};

        private final String siteName = "@nytimes";

        public int getNumProperties() {
            return numProperties;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getImages() {
            return images;
        }

        public List<String> getArticleTags() {
            return articleTags;
        }

        public String getSiteName() {
            return siteName;
        }
    }

    public class ProductData {

        private final String title = "Amazon Best Sellers: Best Sleep Masks";

        private final String description = "Discover the best Sleep Masks in Best Sellers.  Find the top 100 most popular items in Amazon Health & Personal Care Best Sellers.";

        private final List<String> images = Collections.singletonList("https://images-na.ssl-images-amazon.com/images/G/01/gno/sprites/nav-sprite-global_bluebeacon-1x_optimized_layout1._CB468670774_.png");

        private String url = "https://www.amazon.com/Best-Sellers-Health-Personal-Care-Sleep-Masks/zgbs/hpc";

        private final String type = "";

        private final String siteName = "";

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getImages() {
            return images;
        }

        public String getType() {
            return type;
        }

        public String getSiteName() {
            return siteName;
        }

        public String getUrl() {
            return url;
        }
    }

    public class ImageDataZoo {

        private final List<String> imageUrl = Collections.singletonList("https:://og_image_url.jpg");

        private final List<String> secureImageUrl = Collections.singletonList("https:://og_image_secure_url.png");

        private final List<String> twitterImage = Collections.singletonList("https:://twitter_image.jpeg");

        private final List<String> twitterImageSrc = Collections.singletonList("https:://twitter_image_src.jpg");

        public List<String> getImageUrl() {
            return imageUrl;
        }

        public List<String> getSecureImageUrl() {
            return secureImageUrl;
        }

        public List<String> getTwitterImage() {
            return twitterImage;
        }

        public List<String> getTwitterImageSrc() {
            return twitterImageSrc;
        }
    }

}