<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<?xml version="1.0" encoding="utf-8" standalone="yes"?>
<rss version="2.0" xmlns:media="http://search.yahoo.com/mrss/"
xmlns:atom="http://www.w3.org/2005/Atom">
<channel>
    <title>Feed title</title>
    <description>Feed Description</description>
    <link>http://www.example_url.com</link>
    <c:forEach items="${command.items}" var="item">
    <item>
         <title>${item.title}</title>
         <media:description>${item.description}</media:description>
         <link>${item.link}</link>
         <media:thumbnail url="${item.urlThumbnail}"/>
         <media:content url="${item.urlContent}"/>
    </item>
    </c:forEach>
    <%/*<item>
        <title>Video B</title>
        <link>http://example.com/pl_images/B.jpg</link>
        <media:thumbnail url="http://example.com/pl_thumbs/B.jpg"/>
        <media:content type="video/x-flv"
        url="http://example.com/pl_images/B.flv"/>
    </item>*/%>
</channel>
</rss>
