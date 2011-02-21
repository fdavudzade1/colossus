package com.codexperiments.colossus.utility;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
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
    
    public static Drawable getImage (String pUrl) throws IOException
    {
        InputStream lImageStream = (InputStream) new URL(pUrl).getContent();
        return Drawable.createFromStream(lImageStream, "");
    }


//    static Bitmap downloadBitmap(String url) {
//        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
//        final HttpGet getRequest = new HttpGet(url);
//
//        try {
//            HttpResponse response = client.execute(getRequest);
//            final int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != HttpStatus.SC_OK) { 
//                Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url); 
//                return null;
//            }
//            
//            final HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                InputStream inputStream = null;
//                try {
//                    inputStream = entity.getContent(); 
//                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                    return bitmap;
//                } finally {
//                    if (inputStream != null) {
//                        inputStream.close();  
//                    }
//                    entity.consumeContent();
//                }
//            }
//        } catch (Exception e) {
//            // Could provide a more explicit error message for IOException or IllegalStateException
//            getRequest.abort();
//            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url, e.toString());
//        } finally {
//            if (client != null) {
//                client.close();
//            }
//        }
//        return null;
//    }
//    Note: a bug in the previous versions of BitmapFactory.decodeStream may prevent this code from working over a slow connection. Decode a new FlushedInputStream(inputStream) instead to fix the problem. Here is the implementation of this helper class:
//    static class FlushedInputStream extends FilterInputStream {
//        public FlushedInputStream(InputStream inputStream) {
//            super(inputStream);
//        }
//
//        @Override
//        public long skip(long n) throws IOException {
//            long totalBytesSkipped = 0L;
//            while (totalBytesSkipped < n) {
//                long bytesSkipped = in.skip(n - totalBytesSkipped);
//                if (bytesSkipped == 0L) {
//                      int byte = read();
//                      if (byte < 0) {
//                          break;  // we reached EOF
//                      } else {
//                          bytesSkipped = 1; // we read one byte
//                      }
//               }
//                totalBytesSkipped += bytesSkipped;
//            }
//            return totalBytesSkipped;
//        }
//    }
}
