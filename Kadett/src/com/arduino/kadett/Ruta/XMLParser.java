package com.arduino.kadett.Ruta;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class XMLParser {
	// names of the XML tags
    protected static final String MARKERS = "markers";
    protected static final String MARKER = "marker";

    protected URL feedUrl;

    protected XMLParser(final String feedUrl) {
    		Log.i("XMLParser","Constructor");
            try {
                    this.feedUrl = new URL(feedUrl);
            } catch (MalformedURLException e) {
                    Log.e(e.getMessage(), "XML parser - " + feedUrl);
            }
    }

    protected InputStream getInputStream() {
    		Log.i("XMLParser","getInputStream()");
            try {
            	//Esta línea solo sirve para evitar un warning, aunque no diese ningún tipo de fallo posible. Mejor curarse en salud
            	Thread.currentThread().setContextClassLoader(XMLParser.class.getClassLoader());
            		
            		InputStream url = feedUrl.openStream();
            		return url;
            } catch (IOException e) {
                    Log.e(e.getMessage(), "XML parser - " + feedUrl);
                    return null;
            }
    }
}
