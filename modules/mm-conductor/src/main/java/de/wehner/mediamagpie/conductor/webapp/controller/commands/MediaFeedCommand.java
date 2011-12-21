package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Based for rss-output like:
 * 
 * <pre>
 * <?xml version="1.0" encoding="utf-8" standalone="yes"?>
 * <rss version="2.0" xmlns:media="http://search.yahoo.com/mrss/"
 * xmlns:atom="http://www.w3.org/2005/Atom">
 * <channel>
 *     <title>Feed title</title>
 *     <description>Feed Description</description>
 *     <link>http://www.example_url.com</link>
 *     <item>
 *          <title>Picture A</title>
 *          <media:description> This one's my favorite.</media:description>
 *          <link>pl_images/A.jpg</link>
 *          <media:thumbnail url="http://example.com/pl_thumbs/A.jpg"/>
 *          <media:content url="http://example.com/pl_images/A.jpg"/>
 *     </item>
 *     <item>
 *         <title>Video B</title>
 *         <link>http://example.com/pl_images/B.jpg</link>
 *         <media:thumbnail url="http://example.com/pl_thumbs/B.jpg"/>
 *         <media:content type="video/x-flv"
 *         url="http://example.com/pl_images/B.flv"/>
 *     </item>
 * </channel>
 * </rss>
 * </pre>
 */
public class MediaFeedCommand {

    private List<Item> _items = new ArrayList<Item>();

    public static class Item {
        String _title;
        String _description;
        String _link;
        String _urlThumbnail;
        String _urlContent;

        public Item() {
        }

        public Item(String title, String description, String link, String urlThumbnail, String urlContent) {
            super();
            _title = title;
            _description = description;
            _link = link;
            _urlThumbnail = urlThumbnail;
            _urlContent = urlContent;
        }

        public String getTitle() {
            return _title;
        }

        public void setTitle(String title) {
            _title = title;
        }

        public String getDescription() {
            return _description;
        }

        public void setDescription(String description) {
            _description = description;
        }

        public String getLink() {
            return _link;
        }

        public void setLink(String link) {
            _link = link;
        }

        public String getUrlThumbnail() {
            return _urlThumbnail;
        }

        public void setUrlThumbnail(String urlThumbnail) {
            _urlThumbnail = urlThumbnail;
        }

        public String getUrlContent() {
            return _urlContent;
        }

        public void setUrlContent(String urlContent) {
            _urlContent = urlContent;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    public void addItem(Item item) {
        _items.add(item);
    }

    public List<Item> getItems() {
        return _items;
    }
}
