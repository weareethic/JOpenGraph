# JOpenGraph

JOpenGraph is a simple library used to extract Open Graph meta tags from websites according to the Open Graph protocol
(http://opengraphprotocol.org). It uses Jsoup for DOM retrieval and parsing.
The library also extracts Twitter tags, and attempts to look for fallback values for fields like "title" or
"image" if no Open Graph or Twitter tags are available. Although it is not part of the Open Graph protocol, 
it also searches for the website favicon. 

### Adding JOpenGraph to your build

For Apache Maven:
``` 
<dependency>
   <groupId>com.weareethic.community</groupId>
   <artifactId>jopengraph</artifactId>
   <version>1.0.0</version>
</dependency>
```

For Gradle:
```
dependencies {
    compile group: 'com.weareethic.community', name: 'jopengraph', version: '1.0.0'
}
```

### Usage

Creating the initial object, which can be reused, and fetching Open Graph and other meta data:
```
    import com.ethic.JOpenGraph
    
    JOpenGraph jOpenGraph = new JOpenGraph();	
    OpenGraphData openGraphData = jOpenGraph.getGraph("https://www.imdb.com/title/tt0068646/");

```

All the properties that were retrieved can be stored in a set. Additionally, OpenGraphData includes a few conveniance methods to retrieve the most common meta tags:

```

    Set<String > properties = openGraphData.getAllProperties();
    System.out.println(properties);  // => [og:image, og:type, og:site_name, favicon, og:title, og:url, og:description]
			
    Optional<String> title = openGraphData.getTitle();
    Optional<String> description = openGraphData.getDescription();
    Optional<String> type = openGraphData.getType();
    Optional<String> siteName = openGraphData.getSiteName();
    Optional<String> url = openGraphData.getUrl();
    List<String> images = openGraphData.getImages();

```

Any property can be retrieved by using the getContent() method, which always returns a List<String> because it is sometimes possible to have multiple meta tags with the same property. You will need to extract the field that you need from the list.

```
    List<String> content = openGraphData.getContent("favicon");
    String favicon = content.get(0);
    System.out.println(favicon) // => https://m.media-amazon.com/images/G/01/imdb/images/favicon-2165806970._CB470047330_.ico

```

   There are also various options you can set on JOpenGraph object before extracting data:

```
    jOpenGraph.setUserAgent("my custom user agent");	    // set the request user-agent 
    jOpenGraph.setReferrer("my custom referrrer");          // set the page referrer (usually optional)
    jOpenGraph.setTimeout(120 * 1000);                      // set the request timeout in milliseconds (so here the timeout is 120 seconds); a value of 0 will mean an infinite timeout 
    jOpenGraph.ignoreContentType(true);                     // set whether to ignore the HTTP content type
    jOpenGraph.ignoreHttpErrors(false);                     // set whether to ignore HTTP errors
    jOpenGraph.followRedirects(true);                       // set whether to follow page redirects 
    jOpenGraph.validateTLSCertificates(false);              // set whether to validate the SSL/TLS certificate; setting this to false makes a request less secure but will resolve any SSL Handshake error 

```

All of some of these values can also be set using two provided constructors:
```
    JOpenGraph jOpenGraph = new JOpenGraph("userAgent", "referrer", 30000, true, false, true, true)  // user-agent, referrer, timeout, ignoreContentType, ignoreHttpErrors, followRedirects, validateTLSCertificates
    JOpenGraph jOpenGraph = new JOpenGraph(true, false, false, true) // ignoreContentType, ignoreHttpErrors, followRedirects, validateTLSCertificates

```

All of these fields have default values, which are:
```
    userAgent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36"
    referrer: "http://www.google.com"
    timeout: 30 * 1000
    ignoreContentType: true
    ignoreHttpErrors: false
    followRedirectts: true
    tlsValidation: true
```
	
