package com.codexperiments.colossus.utility;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;


public class HttpUtility
{
    public static String get (String pUrl) throws IOException
    {
        BufferedReader lResultReader = null;
        try {
            URI lUri = new URI(pUrl);

            // Prepares the request.
            HttpClient lHttpClient = new DefaultHttpClient();
            HttpGet lHttpGet = new HttpGet();
            lHttpGet.setURI(lUri);

            // Sends the request and read the response
            HttpResponse lHttpResponse = lHttpClient.execute(lHttpGet);
            InputStream lInputStream = lHttpResponse.getEntity().getContent();

            // Converts the response to a String.
            lResultReader = new BufferedReader(new InputStreamReader(lInputStream));
            StringBuffer lResultBuffer = new StringBuffer("");
            String lTmpResult = lResultReader.readLine();
            while (lTmpResult != null) {
                lResultBuffer.append(lTmpResult);
                lResultBuffer.append("\n");
                lTmpResult = lResultReader.readLine();
            }

            return lResultBuffer.toString();
        } catch (URISyntaxException eURISyntaxException) {
            eURISyntaxException.printStackTrace();
            throw (IOException) new IOException().initCause(eURISyntaxException);
        } catch (ClientProtocolException eClientProtocolException) {
            eClientProtocolException.printStackTrace();
            throw (IOException) new IOException().initCause(eClientProtocolException);
        } catch (IOException eIOException) {
            eIOException.printStackTrace();
            throw eIOException;
        } finally {
            // Closes the input stream anyway
            if (lResultReader != null) {
                try {
                    lResultReader.close();
                } catch (IOException e) {
                    Log.e("WEBTAG", e.getMessage());
                }
            }
        }
    }
}
