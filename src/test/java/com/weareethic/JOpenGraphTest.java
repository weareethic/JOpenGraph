package com.weareethic;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JOpenGraphTest {

    private JOpenGraph jOpenGraph;

    @Before
    public void createJOpenGraph() {
        jOpenGraph = new JOpenGraph();
    }

    @Test
    public void testGetGraph() throws IOException {
        String url = "https://www.nytimes.com/2019/08/14/business/german-economy.html?action=click&module=Top%20Stories&pgtype=Homepage";

        OpenGraphData openGraphData = jOpenGraph.getGraph(url);
        openGraphData.getAllProperties();
        System.out.println("Properties = " + openGraphData.getAllProperties());

        assertEquals(20, openGraphData.getAllProperties().size());

        assertTrue(openGraphData.hasTitle());
        assertEquals("Germany Nears Recession and Chinese Factories Slow in Trade War Fallout", openGraphData.getTitle());


        assertTrue(openGraphData.hasType());
        assertEquals("article", openGraphData.getType());

        assertTrue(openGraphData.hasUrl());
        assertEquals("https://www.nytimes.com/2019/08/14/business/german-economy.html", openGraphData.getUrl());

        assertTrue(openGraphData.hasDescription());
        assertEquals("The German economy shrank in the second quarter and may be headed toward recession. Growth of Chinese factories was the slowest in 17 years.", openGraphData.getDescription());

        assertFalse(openGraphData.hasSiteName());
        assertNull(openGraphData.getSiteName());

        assertTrue(openGraphData.hasImages());
        List<String> images = new ArrayList<>();
        images.add("https://static01.nyt.com/images/2019/08/14/business/14germanecon1-promo/14germanecon1-promo-facebookJumbo-v3.jpg");
        assertEquals(images, openGraphData.getImages());

        assertTrue(openGraphData.hasContent("article:tag"));
        List<String> articleTags = new ArrayList() {{
            add("Gross Domestic Product"); add("International Trade and World Market"); add("Merkel, Angela");
            add("Trump, Donald J"); add("Germany"); add("China"); add("United States");
        }};
        assertEquals(articleTags, openGraphData.getContent("article:tag"));
    }

    @Test
    public void testFallbackMethods() throws IOException {
        OpenGraphData openGraphData = jOpenGraph.getGraph("https://www.amazon.com/Best-Sellers-Health-Personal-Care-Sleep-Masks/zgbs/hpc/3764231");
        assertEquals("Amazon Best Sellers: Best Sleep Masks", openGraphData.getTitle());
        assertEquals("Discover the best Sleep Masks in Best Sellers.  Find the top 100 most popular items in Amazon Health & Personal Care Best Sellers.", openGraphData.getDescription());
        assertEquals("https://images-na.ssl-images-amazon.com/images/G/01/gno/sprites/nav-sprite-global_bluebeacon-1x_optimized_layout1._CB468670774_.png", openGraphData.getImages().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetGraphInvalidUrl() throws IOException {
        jOpenGraph.getGraph("");
    }

    @Test
    public void testPartialArgsConstructor() {
        new JOpenGraph(true, false, true, false );
    }

    @Test
    public void testAllArgsConstructor() {
        new JOpenGraph("testAgent", "http://www.google.com", 120 * 1000, true, false, true, true);
    }

}