package com.rasmitap.tailwebs_assigment2.Map;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class XMLParser {
    protected static final String MARKER = "marker";
    protected static final String MARKERS = "markers";
    protected URL feedUrl;

    protected XMLParser(String feedUrl) {
        try {
            this.feedUrl = new URL(feedUrl);
        } catch (MalformedURLException e) {
            Log.e("Routing Error", e.getMessage());
        }
    }

    protected InputStream getInputStream() {
        try {
            return this.feedUrl.openConnection().getInputStream();
        } catch (IOException e) {
            Log.e("Routing Error", e.getMessage());
            return null;
        }
    }
}
