package com.softkey.mazno2.serviceHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Ahmed Saad on 1/20/2018.
 */

public class DownloadUrl {

    public String readUrl(String url) throws IOException {
        String data = "";
        InputStream stream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL myUrl = new URL(url);
            urlConnection = (HttpURLConnection) myUrl.openConnection();
            urlConnection.connect();
            stream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
