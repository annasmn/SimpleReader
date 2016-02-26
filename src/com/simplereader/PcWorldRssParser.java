package com.simplereader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class PcWorldRssParser {

	// We don't use namespaces
	private final String ns = null;

	public List<RssItem> parse(InputStream inputStream) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inputStream, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			inputStream.close();
		}
	}

	private List<RssItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "feed");
		String title = null;
		String link = null;
		List<RssItem> items = new ArrayList<RssItem>();
		try {
			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				Log.d(Constants.TAG, "name: " + name);

				if (name.equals("title")) {
					title = readTitle(parser);
					Log.d(Constants.TAG, "title: " + title);
				} else if (name.equals("link")) {
					link = readLink(parser);
					Log.d(Constants.TAG, "link: " + link);
				}
				if (title != null && link != null) {
					RssItem item = new RssItem(title, link);
					items.add(item);
					title = null;
					link = null;
				}
			}
		}
		catch(XmlPullParserException e){
			Log.d(Constants.TAG, "XmlPullParserException: ");
		}
		return items;
	}

	private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = parser.getAttributeValue(ns, "href");
		return link;
	}

	private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "title");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "title");
		return title;
	}

	// For the tags title and link, extract their text values.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
}
